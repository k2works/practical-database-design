'use strict';

import { execSync, spawnSync } from 'child_process';
import path from 'path';

/**
 * Heroku PMS デプロイ関連の Gulp タスクを登録する
 * @param {import('gulp').Gulp} gulp
 */
export default function (gulp) {
    const BACKEND_DIR = path.join(process.cwd(), 'apps', 'pms', 'backend');
    const APP_NAME = 'deploy-demo-pms';
    const REGISTRY_URL = `registry.heroku.com/${APP_NAME}/web`;

    /**
     * コマンドが利用可能かチェック
     * @param {string} command
     * @returns {boolean}
     */
    function commandExists(command) {
        try {
            const result = spawnSync(
                process.platform === 'win32' ? 'where' : 'which',
                [command],
                { stdio: 'pipe' }
            );
            return result.status === 0;
        } catch (e) {
            return false;
        }
    }

    /**
     * 前提条件をチェック
     */
    gulp.task('heroku:pms:check', (done) => {
        console.log('Checking prerequisites...');

        const missing = [];

        if (!commandExists('docker')) {
            missing.push('docker');
        }

        if (!commandExists('heroku')) {
            missing.push('heroku');
        }

        if (missing.length > 0) {
            console.error(`Missing required commands: ${missing.join(', ')}`);
            console.log('\nInstall instructions:');
            if (missing.includes('docker')) {
                console.log('  docker: https://docs.docker.com/get-docker/');
            }
            if (missing.includes('heroku')) {
                console.log('  heroku: https://devcenter.heroku.com/articles/heroku-cli');
            }
            done(new Error('Prerequisites not met'));
            return;
        }

        console.log('All prerequisites are met.');
        done();
    });

    /**
     * Heroku Container Registry にログイン（内部実装）
     */
    gulp.task('heroku:pms:login:exec', (done) => {
        try {
            console.log('Logging in to Heroku Container Registry...');

            // DOCKER_HOST 環境変数をクリアして実行（Docker Desktop との互換性のため）
            const env = { ...process.env };
            delete env.DOCKER_HOST;

            execSync('heroku container:login', { stdio: 'inherit', env });
            console.log('Login successful!');
            done();
        } catch (error) {
            console.error('Login failed:', error.message);
            console.log('\nTry running: heroku login');
            done(error);
        }
    });

    /**
     * Heroku Container Registry にログイン
     */
    gulp.task('heroku:pms:login', gulp.series('heroku:pms:check', 'heroku:pms:login:exec'));

    /**
     * Docker イメージをビルド（内部実装）
     */
    gulp.task('heroku:pms:build:exec', (done) => {
        try {
            console.log(`Building Docker image: ${REGISTRY_URL}`);
            console.log(`Build context: ${BACKEND_DIR}`);

            // DOCKER_HOST 環境変数をクリアして実行（Docker Desktop との互換性のため）
            const env = { ...process.env };
            delete env.DOCKER_HOST;

            execSync(`docker build --platform linux/amd64 --provenance=false -t ${REGISTRY_URL} ${BACKEND_DIR}`, {
                stdio: 'inherit',
                cwd: process.cwd(),
                env
            });

            console.log('\nBuild successful!');
            console.log(`Image: ${REGISTRY_URL}`);
            done();
        } catch (error) {
            console.error('Build failed:', error.message);
            done(error);
        }
    });

    /**
     * Docker イメージをビルド
     */
    gulp.task('heroku:pms:build', gulp.series('heroku:pms:check', 'heroku:pms:build:exec'));

    /**
     * Docker イメージを Heroku にプッシュ（内部実装）
     */
    gulp.task('heroku:pms:push:exec', (done) => {
        try {
            console.log(`Pushing image to ${REGISTRY_URL}...`);

            // DOCKER_HOST 環境変数をクリアして実行
            const env = { ...process.env };
            delete env.DOCKER_HOST;

            execSync(`docker push ${REGISTRY_URL}`, {
                stdio: 'inherit',
                env
            });

            console.log('\nPush successful!');
            done();
        } catch (error) {
            console.error('Push failed:', error.message);
            done(error);
        }
    });

    /**
     * Docker イメージを Heroku にプッシュ
     */
    gulp.task('heroku:pms:push', gulp.series('heroku:pms:login', 'heroku:pms:push:exec'));

    /**
     * Heroku にリリース
     */
    gulp.task('heroku:pms:release', (done) => {
        try {
            console.log(`Releasing to Heroku app: ${APP_NAME}...`);

            execSync(`heroku container:release web -a ${APP_NAME}`, {
                stdio: 'inherit'
            });

            console.log('\nRelease successful!');
            console.log(`App URL: https://${APP_NAME}.herokuapp.com/`);
            done();
        } catch (error) {
            console.error('Release failed:', error.message);
            done(error);
        }
    });

    /**
     * ビルドからデプロイまで一括実行
     */
    gulp.task('heroku:pms:deploy', gulp.series(
        'heroku:pms:build',
        'heroku:pms:push',
        'heroku:pms:release'
    ));

    /**
     * Heroku ログを表示
     */
    gulp.task('heroku:pms:logs', (done) => {
        try {
            console.log(`Fetching logs for ${APP_NAME}...`);
            execSync(`heroku logs --tail -a ${APP_NAME}`, {
                stdio: 'inherit'
            });
            done();
        } catch (error) {
            // Ctrl+C で終了した場合はエラーとしない
            if (error.status === 130 || error.signal === 'SIGINT') {
                done();
            } else {
                done(error);
            }
        }
    });

    /**
     * Heroku アプリをブラウザで開く
     */
    gulp.task('heroku:pms:open', (done) => {
        try {
            console.log(`Opening ${APP_NAME} in browser...`);
            execSync(`heroku open -a ${APP_NAME}`, {
                stdio: 'inherit'
            });
            done();
        } catch (error) {
            console.error('Failed to open app:', error.message);
            done(error);
        }
    });

    /**
     * Heroku Dyno を再起動（データリセット）
     */
    gulp.task('heroku:pms:restart', (done) => {
        try {
            console.log(`Restarting ${APP_NAME}...`);
            execSync(`heroku restart -a ${APP_NAME}`, {
                stdio: 'inherit'
            });
            console.log('\nRestart successful! Data has been reset.');
            done();
        } catch (error) {
            console.error('Restart failed:', error.message);
            done(error);
        }
    });

    /**
     * Heroku アプリの情報を表示
     */
    gulp.task('heroku:pms:info', (done) => {
        try {
            console.log(`\n=== ${APP_NAME} Info ===\n`);
            execSync(`heroku info -a ${APP_NAME}`, {
                stdio: 'inherit'
            });
            done();
        } catch (error) {
            console.error('Failed to get app info:', error.message);
            done(error);
        }
    });

    /**
     * Heroku アプリを新規作成（内部実装）
     */
    gulp.task('heroku:pms:create:exec', (done) => {
        try {
            console.log(`Creating Heroku app: ${APP_NAME}...`);

            // アプリ作成
            execSync(`heroku create ${APP_NAME}`, {
                stdio: 'inherit'
            });

            // スタックを container に設定
            console.log('\nSetting stack to container...');
            execSync(`heroku stack:set container -a ${APP_NAME}`, {
                stdio: 'inherit'
            });

            // 環境変数設定
            console.log('\nSetting environment variables...');
            execSync(`heroku config:set SPRING_PROFILES_ACTIVE=demo -a ${APP_NAME}`, {
                stdio: 'inherit'
            });

            console.log(`\nApp created successfully!`);
            console.log(`URL: https://${APP_NAME}.herokuapp.com/`);
            done();
        } catch (error) {
            console.error('Failed to create app:', error.message);
            done(error);
        }
    });

    /**
     * Heroku アプリを新規作成
     */
    gulp.task('heroku:pms:create', gulp.series('heroku:pms:check', 'heroku:pms:create:exec'));

    /**
     * ヘルプを表示
     */
    gulp.task('heroku:pms:help', (done) => {
        console.log(`
Heroku PMS Deploy Tasks
=======================

Available tasks:
  heroku:pms:check    - Check prerequisites (docker, heroku CLI)
  heroku:pms:login    - Login to Heroku Container Registry
  heroku:pms:build    - Build Docker image
  heroku:pms:push     - Push image to Heroku Container Registry
  heroku:pms:release  - Release the pushed image
  heroku:pms:deploy   - Build, push, and release (all-in-one)
  heroku:pms:logs     - Show app logs (tail)
  heroku:pms:open     - Open app in browser
  heroku:pms:restart  - Restart dyno (reset data)
  heroku:pms:info     - Show app information
  heroku:pms:create   - Create new Heroku app
  heroku:pms:help     - Show this help

Quick start:
  1. gulp heroku:pms:create  # First time only
  2. gulp heroku:pms:deploy  # Build and deploy

App: ${APP_NAME}
URL: https://${APP_NAME}.herokuapp.com/
`);
        done();
    });
}
