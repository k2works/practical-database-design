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
 * SchemaSpy関連のGulpタスクを登録する
 * @param {import('gulp').Gulp} gulp
 */
export default function (gulp) {
    const PROJECT_DIR = path.join(process.cwd(), 'apps', 'sms');
    const OUTPUT_DIR = path.join(process.cwd(), 'docs', 'assets', 'schemaspy-output', 'sms');
    const INDEX_FILE = path.join(OUTPUT_DIR, 'index.html');

    /**
     * SchemaSpy ER図を生成する
     */
    gulp.task('schemaspy:sms:generate', (done) => {
        try {
            console.log('Generating SchemaSpy ER diagram for SMS...');

            // 出力ディレクトリを作成（存在しない場合）
            if (!fs.existsSync(OUTPUT_DIR)) {
                fs.mkdirSync(OUTPUT_DIR, { recursive: true });
                console.log(`Created output directory: ${OUTPUT_DIR}`);
            }

            // PostgreSQL が起動していることを確認
            console.log('Checking PostgreSQL status...');
            try {
                execSync('docker compose ps postgres --format json', {
                    cwd: PROJECT_DIR,
                    stdio: 'pipe'
                });
            } catch (e) {
                console.log('PostgreSQL is not running. Starting it...');
                execSync('docker compose up -d postgres', {
                    cwd: PROJECT_DIR,
                    stdio: 'inherit'
                });
                console.log('Waiting for PostgreSQL to be ready...');
                // ヘルスチェックを待つ
                execSync('docker compose exec postgres pg_isready -U postgres', {
                    cwd: PROJECT_DIR,
                    stdio: 'inherit',
                    timeout: 30000
                });
            }

            // SchemaSpy を実行
            console.log('\nRunning SchemaSpy...');
            execSync('docker compose --profile schemaspy up schemaspy', {
                cwd: PROJECT_DIR,
                stdio: 'inherit'
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
  schemaspy:sms:generate    - Generate SchemaSpy ER diagram
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
