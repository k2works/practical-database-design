'use strict';

import { execSync } from 'child_process';
import fs from 'fs';
import path from 'path';

/**
 * @type {boolean} Windows環境かどうかをチェック
 */
const isWindows = process.platform === 'win32';

/**
 * @type {boolean} macOS環境かどうかをチェック
 */
const isMac = process.platform === 'darwin';

/**
 * DOCKER_HOST をクリアした環境変数を取得（Docker Desktop との互換性のため）
 * @returns {NodeJS.ProcessEnv}
 */
function getDockerEnv() {
    const env = { ...process.env };
    delete env.DOCKER_HOST;
    return env;
}

/**
 * 指定秒数待機する（クロスプラットフォーム対応）
 * @param {number} seconds
 * @returns {Promise<void>}
 */
function sleep(seconds) {
    return new Promise(resolve => setTimeout(resolve, seconds * 1000));
}

/**
 * SchemaSpy関連のGulpタスクを登録する
 * @param {import('gulp').Gulp} gulp
 */
export default function (gulp) {
    const PROJECT_DIR = path.join(process.cwd(), 'apps', 'sms');
    const OUTPUT_DIR = path.join(process.cwd(), 'docs', 'assets', 'schemaspy-output', 'sms');
    const INDEX_FILE = path.join(OUTPUT_DIR, 'index.html');

    /**
     * バックエンドを起動してマイグレーションを実行
     */
    gulp.task('schemaspy:sms:migrate', async () => {
        console.log('Starting backend for database migration...');

        // 出力ディレクトリを作成（存在しない場合）
        if (!fs.existsSync(OUTPUT_DIR)) {
            fs.mkdirSync(OUTPUT_DIR, { recursive: true });
            console.log(`Created output directory: ${OUTPUT_DIR}`);
        }

        // PostgreSQL を起動
        console.log('Starting PostgreSQL...');
        execSync('docker compose up -d postgres', {
            cwd: PROJECT_DIR,
            stdio: 'inherit',
            env: getDockerEnv()
        });

        // PostgreSQL の準備完了を待つ
        console.log('Waiting for PostgreSQL to be ready...');
        let retries = 30;
        while (retries > 0) {
            try {
                execSync('docker compose exec -T postgres pg_isready -U postgres', {
                    cwd: PROJECT_DIR,
                    stdio: 'pipe',
                    env: getDockerEnv()
                });
                break;
            } catch (e) {
                retries--;
                if (retries === 0) {
                    throw new Error('PostgreSQL failed to become ready');
                }
                await sleep(1);
            }
        }

        // バックエンドを起動（マイグレーション実行）
        console.log('\nStarting backend (running Flyway migrations)...');
        execSync('docker compose --profile backend up -d backend', {
            cwd: PROJECT_DIR,
            stdio: 'inherit',
            env: getDockerEnv()
        });

        // バックエンドのヘルスチェックを待つ（最大120秒）
        console.log('Waiting for backend to be healthy (migrations to complete)...');
        retries = 24; // 24 * 5秒 = 120秒
        while (retries > 0) {
            try {
                const result = execSync('docker compose --profile backend ps backend --format json', {
                    cwd: PROJECT_DIR,
                    stdio: 'pipe',
                    env: getDockerEnv()
                }).toString();

                if (result.includes('"Health":"healthy"') || result.includes('"Health": "healthy"')) {
                    console.log('Backend is healthy!');
                    break;
                }
            } catch (e) {
                // ignore
            }
            retries--;
            if (retries === 0) {
                throw new Error('Backend failed to become healthy within timeout');
            }
            console.log(`  Waiting... (${retries * 5}s remaining)`);
            await sleep(5);
        }

        console.log('\nDatabase migration completed!');
    });

    /**
     * SchemaSpy ER図を生成する（マイグレーション後）
     */
    gulp.task('schemaspy:sms:run', (done) => {
        try {
            console.log('Running SchemaSpy...');
            execSync('docker compose --profile schemaspy run --rm schemaspy', {
                cwd: PROJECT_DIR,
                stdio: 'inherit',
                env: getDockerEnv()
            });

            console.log('\nSchemaSpy ER diagram for SMS generated successfully!');
            console.log(`Output directory: ${OUTPUT_DIR}`);

            done();
        } catch (error) {
            console.error('Error generating SchemaSpy ER diagram:', error.message);
            done(error);
        }
    });

    /**
     * バックエンドを停止する
     */
    gulp.task('schemaspy:sms:stop-backend', (done) => {
        try {
            console.log('Stopping backend...');
            execSync('docker compose --profile backend stop backend', {
                cwd: PROJECT_DIR,
                stdio: 'inherit',
                env: getDockerEnv()
            });
            done();
        } catch (error) {
            // バックエンドが起動していない場合は無視
            done();
        }
    });

    /**
     * SchemaSpy ER図を生成する（マイグレーション含む）
     */
    gulp.task('schemaspy:sms:generate', gulp.series(
        'schemaspy:sms:migrate',
        'schemaspy:sms:run',
        'schemaspy:sms:stop-backend'
    ));

    /**
     * 生成されたSchemaSpy ER図をブラウザで開く
     */
    gulp.task('schemaspy:sms:open', (done) => {
        try {
            console.log('Opening SchemaSpy ER diagram for SMS...');

            // index.html が存在するか確認
            if (!fs.existsSync(INDEX_FILE)) {
                console.error(`Error: SchemaSpy output not found at ${INDEX_FILE}`);
                console.log('Please run "gulp schemaspy:sms:generate" first.');
                done(new Error('SchemaSpy output not found'));
                return;
            }

            const openCmd = isWindows ? 'start' : isMac ? 'open' : 'xdg-open';
            const command = `${openCmd} ${INDEX_FILE}`;
            execSync(command, { stdio: 'inherit' });

            console.log('SchemaSpy ER diagram opened successfully!');

            done();
        } catch (error) {
            console.error('Error opening SchemaSpy ER diagram:', error.message);
            done(error);
        }
    });

    /**
     * SchemaSpy 出力をクリーンアップする
     */
    gulp.task('schemaspy:sms:clean', (done) => {
        try {
            console.log('Cleaning SchemaSpy SMS output directory...');

            if (fs.existsSync(OUTPUT_DIR)) {
                // ディレクトリ内のファイルを削除（.gitkeep は残す）
                const files = fs.readdirSync(OUTPUT_DIR);
                files.forEach(file => {
                    if (file !== '.gitkeep') {
                        const filePath = path.join(OUTPUT_DIR, file);
                        if (fs.lstatSync(filePath).isDirectory()) {
                            fs.rmSync(filePath, { recursive: true, force: true });
                        } else {
                            fs.unlinkSync(filePath);
                        }
                    }
                });
                console.log('SchemaSpy SMS output directory cleaned successfully!');
            } else {
                console.log('SchemaSpy SMS output directory does not exist.');
            }

            done();
        } catch (error) {
            console.error('Error cleaning SchemaSpy output directory:', error.message);
            done(error);
        }
    });

    /**
     * SchemaSpy を再生成する（クリーン後に生成）
     */
    gulp.task('schemaspy:sms:regenerate', gulp.series('schemaspy:sms:clean', 'schemaspy:sms:generate'));

    /**
     * SchemaSpy を生成してブラウザで開く
     */
    gulp.task('schemaspy:sms', gulp.series('schemaspy:sms:generate', 'schemaspy:sms:open'));

    /**
     * ヘルプを表示
     */
    gulp.task('schemaspy:sms:help', (done) => {
        console.log(`
SchemaSpy SMS Tasks
===================

Available tasks:
  schemaspy:sms:generate    - Generate SchemaSpy ER diagram (with migrations)
  schemaspy:sms:migrate     - Run database migrations only
  schemaspy:sms:run         - Run SchemaSpy only (no migrations)
  schemaspy:sms:open        - Open generated ER diagram in browser
  schemaspy:sms:clean       - Clean output directory
  schemaspy:sms:regenerate  - Clean and regenerate ER diagram
  schemaspy:sms             - Generate and open ER diagram
  schemaspy:sms:help        - Show this help

Output directory: ${OUTPUT_DIR}
`);
        done();
    });
}
