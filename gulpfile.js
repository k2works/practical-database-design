'use strict';

/**
 * Gulpfile that loads tasks from the script directory
 */

import gulp from 'gulp';
import mkdocsTasks from './ops/scripts/mkdocs.js';
import journalTasks from './ops/scripts/journal.js';
import schemaspySmsTasks from './ops/scripts/schemaspy-sms.js';
import schemaspyFasTasks from './ops/scripts/schemaspy-fas.js';
import schemaspyPmsTasks from './ops/scripts/schemaspy-pms.js';
import herokuSmsTasks from './ops/scripts/heroku-sms.js';
import herokuFasTasks from './ops/scripts/heroku-fas.js';
import herokuPmsTasks from './ops/scripts/heroku-pms.js';
import dockerSmsTasks from './ops/scripts/docker-sms.js';
import dockerFasTasks from './ops/scripts/docker-fas.js';
import dockerPmsTasks from './ops/scripts/docker-pms.js';
import testTasks from './ops/scripts/test.js';

// Load gulp tasks from script modules
mkdocsTasks(gulp);
journalTasks(gulp);
schemaspySmsTasks(gulp);
schemaspyFasTasks(gulp);
schemaspyPmsTasks(gulp);
herokuSmsTasks(gulp);
herokuFasTasks(gulp);
herokuPmsTasks(gulp);
dockerSmsTasks(gulp);
dockerFasTasks(gulp);
dockerPmsTasks(gulp);
testTasks(gulp);

export const dev = gulp.series(
    gulp.parallel('mkdocs:serve', 'mkdocs:open'),
    gulp.parallel('docker:sms:up', 'docker:fas:up', 'docker:pms:up'),
    gulp.parallel('schemaspy:sms:generate', 'schemaspy:fas:generate', 'schemaspy:pms:generate')
);

// Export gulp to make it available to the gulp CLI
export default gulp;
