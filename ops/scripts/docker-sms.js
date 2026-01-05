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
 * SMS Docker 関連の Gulp タスクを登録する
 * @param {import('gulp').Gulp} gulp
 */
export default function (gulp) {
    const PROJECT_DIR = path.join(process.cwd(), 'apps', 'sms');

    /**
     * PostgreSQL コンテナを起動
     */
    gulp.task('docker:sms:up', (done) => {
        try {
            console.log('Starting SMS PostgreSQL container...');

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

            console.log('\nSMS PostgreSQL is ready!');
            console.log('Connection: postgresql://postgres:postgres@localhost:5432/sms');
            done();
        } catch (error) {
            console.error('Failed to start PostgreSQL:', error.message);
            done(error);
        }
    });

    /**
     * PostgreSQL コンテナを停止
     */
    gulp.task('docker:sms:down', (done) => {
        try {
            console.log('Stopping SMS Docker containers...');

            execSync('docker compose down', {
                cwd: PROJECT_DIR,
                stdio: 'inherit',
                env: getDockerEnv()
            });

            console.log('\nSMS Docker containers stopped.');
            done();
        } catch (error) {
            console.error('Failed to stop containers:', error.message);
            done(error);
        }
    });

    /**
     * PostgreSQL コンテナを停止してボリュームも削除
     */
    gulp.task('docker:sms:clean', (done) => {
        try {
            console.log('Stopping SMS Docker containers and removing volumes...');

            execSync('docker compose down -v', {
                cwd: PROJECT_DIR,
                stdio: 'inherit',
                env: getDockerEnv()
            });

            console.log('\nSMS Docker containers and volumes removed.');
            done();
        } catch (error) {
            console.error('Failed to clean containers:', error.message);
            done(error);
        }
    });

    /**
     * PostgreSQL コンテナの状態を確認
     */
    gulp.task('docker:sms:status', (done) => {
        try {
            console.log('SMS Docker container status:\n');

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
    gulp.task('docker:sms:logs', (done) => {
        try {
            console.log('SMS PostgreSQL logs:\n');

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
    gulp.task('docker:sms:restart', gulp.series('docker:sms:down', 'docker:sms:up'));

    /**
     * ヘルプを表示
     */
    gulp.task('docker:sms:help', (done) => {
        console.log(`
SMS Docker Tasks
================

Available tasks:
  docker:sms:up       - Start PostgreSQL container
  docker:sms:down     - Stop PostgreSQL container
  docker:sms:clean    - Stop and remove volumes (data reset)
  docker:sms:status   - Show container status
  docker:sms:logs     - Show PostgreSQL logs (follow)
  docker:sms:restart  - Restart PostgreSQL container
  docker:sms:help     - Show this help

Project directory: ${PROJECT_DIR}
Connection: postgresql://postgres:postgres@localhost:5432/sms
`);
        done();
    });
}
