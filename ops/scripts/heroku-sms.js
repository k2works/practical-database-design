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
    gulp.task('heroku:sms:check', (done) => {
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
    gulp.task('heroku:sms:login:exec', (done) => {
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
    gulp.task('heroku:sms:login', gulp.series('heroku:sms:check', 'heroku:sms:login:exec'));

    /**
     * Docker イメージをビルド（内部実装）
     */
    gulp.task('heroku:sms:build:exec', (done) => {
        try {
            console.log(`Building Docker image: ${REGISTRY_URL}`);
            console.log(`Build context: ${BACKEND_DIR}`);

            // DOCKER_HOST 環境変数をクリアして実行（Docker Desktop との互換性のため）
            const env = { ...process.env };
            delete env.DOCKER_HOST;

            execSync(`docker build -t ${REGISTRY_URL} ${BACKEND_DIR}`, {
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
    gulp.task('heroku:sms:build', gulp.series('heroku:sms:check', 'heroku:sms:build:exec'));

    /**
     * Docker イメージを Heroku にプッシュ（内部実装）
     */
    gulp.task('heroku:sms:push:exec', (done) => {
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
    gulp.task('heroku:sms:push', gulp.series('heroku:sms:login', 'heroku:sms:push:exec'));

    /**
     * Heroku にリリース
     */
    gulp.task('heroku:sms:release', (done) => {
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
    gulp.task('heroku:sms:deploy', gulp.series(
        'heroku:sms:build',
        'heroku:sms:push',
        'heroku:sms:release'
    ));

    /**
     * Heroku ログを表示
     */
    gulp.task('heroku:sms:logs', (done) => {
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
    gulp.task('heroku:sms:open', (done) => {
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
    gulp.task('heroku:sms:restart', (done) => {
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
    gulp.task('heroku:sms:info', (done) => {
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
    gulp.task('heroku:sms:create:exec', (done) => {
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
    gulp.task('heroku:sms:create', gulp.series('heroku:sms:check', 'heroku:sms:create:exec'));

    /**
     * ヘルプを表示
     */
    gulp.task('heroku:sms:help', (done) => {
        console.log(`
Heroku SMS Deploy Tasks
=======================

Available tasks:
  heroku:sms:check    - Check prerequisites (docker, heroku CLI)
  heroku:sms:login    - Login to Heroku Container Registry
  heroku:sms:build    - Build Docker image
  heroku:sms:push     - Push image to Heroku Container Registry
  heroku:sms:release  - Release the pushed image
  heroku:sms:deploy   - Build, push, and release (all-in-one)
  heroku:sms:logs     - Show app logs (tail)
  heroku:sms:open     - Open app in browser
  heroku:sms:restart  - Restart dyno (reset data)
  heroku:sms:info     - Show app information
  heroku:sms:create   - Create new Heroku app
  heroku:sms:help     - Show this help

Quick start:
  1. gulp heroku:sms:create  # First time only
  2. gulp heroku:sms:deploy  # Build and deploy

App: ${APP_NAME}
URL: https://${APP_NAME}.herokuapp.com/
`);
        done();
    });
}
