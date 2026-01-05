'use strict';

/**
 * Gulpfile that loads tasks from the script directory
 */

import gulp from 'gulp';
import mkdocsTasks from './ops/scripts/mkdocs.js';
import journalTasks from './ops/scripts/journal.js';
import schemaspyTasks from './ops/scripts/schemaspy.js';
import herokuSmsTasks from './ops/scripts/heroku-sms.js';

// Load gulp tasks from script modules
mkdocsTasks(gulp);
journalTasks(gulp);
schemaspyTasks(gulp);
herokuSmsTasks(gulp);

export const dev = gulp.series('mkdocs:serve', 'mkdocs:open');

// Export gulp to make it available to the gulp CLI
export default gulp;
