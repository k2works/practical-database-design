'use strict';

import { execSync, spawn } from 'child_process';
import path from 'path';

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
 * PMS Docker 関連の Gulp タスクを登録する
 * @param {import('gulp').Gulp} gulp
 */
export default function (gulp) {
    const PROJECT_DIR = path.join(process.cwd(), 'apps', 'pms');

    /**
     * PostgreSQL コンテナを起動
     */
    gulp.task('docker:pms:up', (done) => {
        try {
            console.log('Starting PMS PostgreSQL container...');

            execSync('docker compose up -d postgres', {
                cwd: PROJECT_DIR,
                stdio: 'inherit',
                env: getDockerEnv()
            });

            console.log('Waiting for PostgreSQL to be ready...');

            // ヘルスチェックを待つ（最大30秒）
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
                    execSync('sleep 1 || timeout /t 1 /nobreak >nul', { stdio: 'pipe', shell: true });
                }
            }

            console.log('\nPMS PostgreSQL is ready!');
            console.log('Connection: postgresql://postgres:postgres@localhost:5434/pms');
            done();
        } catch (error) {
            console.error('Failed to start PostgreSQL:', error.message);
            done(error);
        }
    });

    /**
     * PostgreSQL コンテナを停止
     */
    gulp.task('docker:pms:down', (done) => {
        try {
            console.log('Stopping PMS Docker containers...');

            execSync('docker compose down', {
                cwd: PROJECT_DIR,
                stdio: 'inherit',
                env: getDockerEnv()
            });

            console.log('\nPMS Docker containers stopped.');
            done();
        } catch (error) {
            console.error('Failed to stop containers:', error.message);
            done(error);
        }
    });

    /**
     * PostgreSQL コンテナを停止してボリュームも削除
     */
    gulp.task('docker:pms:clean', (done) => {
        try {
            console.log('Stopping PMS Docker containers and removing volumes...');

            execSync('docker compose down -v', {
                cwd: PROJECT_DIR,
                stdio: 'inherit',
                env: getDockerEnv()
            });

            console.log('\nPMS Docker containers and volumes removed.');
            done();
        } catch (error) {
            console.error('Failed to clean containers:', error.message);
            done(error);
        }
    });

    /**
     * PostgreSQL コンテナの状態を確認
     */
    gulp.task('docker:pms:status', (done) => {
        try {
            console.log('PMS Docker container status:\n');

            execSync('docker compose ps', {
                cwd: PROJECT_DIR,
                stdio: 'inherit',
                env: getDockerEnv()
            });

            done();
        } catch (error) {
            console.error('Failed to get status:', error.message);
            done(error);
        }
    });

    /**
     * PostgreSQL のログを表示
     */
    gulp.task('docker:pms:logs', (done) => {
        try {
            console.log('PMS PostgreSQL logs:\n');

            const child = spawn('docker', ['compose', 'logs', '-f', 'postgres'], {
                cwd: PROJECT_DIR,
                stdio: 'inherit',
                env: getDockerEnv()
            });

            child.on('close', (code) => {
                if (code === 0 || code === 130) {
                    done();
                } else {
                    done(new Error(`Process exited with code ${code}`));
                }
            });

            child.on('error', (error) => {
                done(error);
            });
        } catch (error) {
            console.error('Failed to get logs:', error.message);
            done(error);
        }
    });

    /**
     * PostgreSQL コンテナを再起動
     */
    gulp.task('docker:pms:restart', gulp.series('docker:pms:down', 'docker:pms:up'));

    /**
     * 全 Docker イメージをビルド（backend, schemaspy）
     */
    gulp.task('docker:pms:build', (done) => {
        try {
            console.log('Building PMS Docker images (backend, schemaspy)...');

            execSync('docker compose build backend schemaspy', {
                cwd: PROJECT_DIR,
                stdio: 'inherit',
                env: getDockerEnv()
            });

            console.log('\nPMS Docker images built successfully.');
            done();
        } catch (error) {
            console.error('Failed to build images:', error.message);
            done(error);
        }
    });

    /**
     * ヘルプを表示
     */
    gulp.task('docker:pms:help', (done) => {
        console.log(`
PMS Docker Tasks
================

Available tasks:
  docker:pms:up            - Start PostgreSQL container
  docker:pms:down          - Stop PostgreSQL container
  docker:pms:build         - Build all images (backend, schemaspy)
  docker:pms:clean         - Stop and remove volumes (data reset)
  docker:pms:status        - Show container status
  docker:pms:logs          - Show PostgreSQL logs (follow)
  docker:pms:restart       - Restart PostgreSQL container
  docker:pms:help          - Show this help

Project directory: ${PROJECT_DIR}
Connection: postgresql://postgres:postgres@localhost:5434/pms
`);
        done();
    });
}
