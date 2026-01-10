'use strict';

import { execSync } from 'child_process';
import path from 'path';

/**
 * テスト関連の Gulp タスクを登録する
 * @param {import('gulp').Gulp} gulp
 */
export default function (gulp) {
    const SMS_BACKEND_DIR = path.join(process.cwd(), 'apps', 'sms', 'backend');
    const FAS_BACKEND_DIR = path.join(process.cwd(), 'apps', 'fas', 'backend');

    /**
     * SMS Backend のテストを実行
     */
    gulp.task('test:sms:backend', (done) => {
        try {
            console.log('Running SMS Backend tests...');
            const cmd = process.platform === 'win32' ? 'gradlew.bat test' : 'sh gradlew test';
            execSync(cmd, {
                cwd: SMS_BACKEND_DIR,
                stdio: 'inherit'
            });
            done();
        } catch (error) {
            console.error('SMS Backend tests failed');
            done(error);
        }
    });

    /**
     * FAS Backend のテストを実行
     */
    gulp.task('test:fas:backend', (done) => {
        try {
            console.log('Running FAS Backend tests...');
            const cmd = process.platform === 'win32' ? 'gradlew.bat test' : 'sh gradlew test';
            execSync(cmd, {
                cwd: FAS_BACKEND_DIR,
                stdio: 'inherit'
            });
            done();
        } catch (error) {
            console.error('FAS Backend tests failed');
            done(error);
        }
    });

    /**
     * 全ての Backend テストを実行
     */
    gulp.task('test:backend', gulp.parallel('test:sms:backend', 'test:fas:backend'));
}
