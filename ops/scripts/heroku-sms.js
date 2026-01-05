'use strict';

import { execSync, spawnSync } from 'child_process';
import path from 'path';

/**
 * Heroku SMS デプロイ関連の Gulp タスクを登録する
 * @param {import('gulp').Gulp} gulp
 */
export default function (gulp) {
    const BACKEND_DIR = path.join(process.cwd(), 'apps', 'sms', 'backend');
    const APP_NAME = 'deploy-demo-sms';
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
    gulp.task('heroku:check', (done) => {
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
    gulp.task('heroku:login:exec', (done) => {
        try {
            console.log('Logging in to Heroku Container Registry...');
            execSync('heroku container:login', { stdio: 'inherit' });
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
    gulp.task('heroku:login', gulp.series('heroku:check', 'heroku:login:exec'));

    /**
     * Docker イメージをビルド（内部実装）
     */
    gulp.task('heroku:build:exec', (done) => {
        try {
            console.log(`Building Docker image: ${REGISTRY_URL}`);
            console.log(`Build context: ${BACKEND_DIR}`);

            execSync(`docker build -t ${REGISTRY_URL} ${BACKEND_DIR}`, {
                stdio: 'inherit',
                cwd: process.cwd()
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
    gulp.task('heroku:build', gulp.series('heroku:check', 'heroku:build:exec'));

    /**
     * Docker イメージを Heroku にプッシュ（内部実装）
     */
    gulp.task('heroku:push:exec', (done) => {
        try {
            console.log(`Pushing image to ${REGISTRY_URL}...`);

            execSync(`docker push ${REGISTRY_URL}`, {
                stdio: 'inherit'
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
    gulp.task('heroku:push', gulp.series('heroku:login', 'heroku:push:exec'));

    /**
     * Heroku にリリース
     */
    gulp.task('heroku:release', (done) => {
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
    gulp.task('heroku:deploy', gulp.series(
        'heroku:build',
        'heroku:push',
        'heroku:release'
    ));

    /**
     * Heroku ログを表示
     */
    gulp.task('heroku:logs', (done) => {
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
    gulp.task('heroku:open', (done) => {
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
    gulp.task('heroku:restart', (done) => {
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
    gulp.task('heroku:info', (done) => {
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
    gulp.task('heroku:create:exec', (done) => {
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
    gulp.task('heroku:create', gulp.series('heroku:check', 'heroku:create:exec'));

    /**
     * ヘルプを表示
     */
    gulp.task('heroku:help', (done) => {
        console.log(`
Heroku SMS Deploy Tasks
=======================

Available tasks:
  heroku:check    - Check prerequisites (docker, heroku CLI)
  heroku:login    - Login to Heroku Container Registry
  heroku:build    - Build Docker image
  heroku:push     - Push image to Heroku Container Registry
  heroku:release  - Release the pushed image
  heroku:deploy   - Build, push, and release (all-in-one)
  heroku:logs     - Show app logs (tail)
  heroku:open     - Open app in browser
  heroku:restart  - Restart dyno (reset data)
  heroku:info     - Show app information
  heroku:create   - Create new Heroku app
  heroku:help     - Show this help

Quick start:
  1. gulp heroku:create  # First time only
  2. gulp heroku:deploy  # Build and deploy

App: ${APP_NAME}
URL: https://${APP_NAME}.herokuapp.com/
`);
        done();
    });
}
