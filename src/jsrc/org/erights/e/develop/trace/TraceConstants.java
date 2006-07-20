package org.erights.e.develop.trace;

/*
The contents of this file are subject to the Electric Communities E Open
Source Code License Version 1.0 (the "License"); you may not use this file
except in compliance with the License. You may obtain a copy of the License
at http://www.communities.com/EL/.

Software distributed under the License is distributed on an "AS IS" basis,
WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
the specific language governing rights and limitations under the License.

The Original Code is the Distributed E Language Implementation, released
July 20, 1998.

The Initial Developer of the Original Code is Electric Communities.
Copyright (C) 1998 Electric Communities. All Rights Reserved.

Contributor(s): ______________________________________.
*/
/*
 *  Trace and Logging Package. Written by Brian Marick,
 *  July-September 1997, for Electric Communities, Inc.
 */

import java.io.File;

interface TraceConstants {

    String version = "Trace version 1.2 of September 02000";

    /**
     * This identifies the TraceMessageAcceptor used for the on-disk log.
     */
    int LOG = 0;

    /**
     * This identifies the TraceMessageAcceptor used for the in-core trace and
     * its associated window.
     */
    int TRACE = 1;

    /**
     * The number of different types of TraceMessageAcceptors.
     */
    int NUM_ACCEPTORS = 2;

    String acceptorNames[] = {"log", "trace"};

    /**
     * The different trace thresholds. See the Trace class for
     * documentation. There is space between the levels for
     * expansion. If you add or delete a level, you must change
     * Trace.java to add new methods and variables.
     */

    int ERROR = 10000;  // always set.

    int WARNING = 120;

    int WORLD = 100;

    int USAGE = 80;

    int EVENT = 60;

    int DEBUG = 40;

    int VERBOSE = 20;

    int MAX_THRESHOLD = ERROR;

    /**
     * As a late addition, there's a "timing" boolean that can be
     * set orthogonally from the thresholds. The above values are
     * overloaded: thresholds, but also identifiers for the original message
     * (was it sent with errorm(), etc.). The TIMING "level" is added
     * for the latter purpose, but it has nothing to do with thresholds.
     * To avoid confusion, it's set negative, thus below the minimum
     * threshold.
     */
    int TIMING = -20;

    /**
     * When referring to thresholds, are we talking about those
     * from the default thresholds, or ones specific to a subsystem?
     * XXX These could be interned strings, but interning didn't work
     * right in 1.0.4. That is, two "default" strings weren't eq.
     */
    int FROM_DEFAULT = 0;

    int FOR_SUBSYSTEM = 1;

    String reasonNames[] = {"default", "subsystem"};

    /* Trace buffer defaults */
    int STARTING_TRACE_BUFFER_SIZE = 500;

    int STARTING_TRACE_THRESHOLD = USAGE;

    /* Trace log defaults. */
    long STARTING_LOG_SIZE_THRESHOLD = 500000;

    long SMALLEST_LOG_SIZE_THRESHOLD = 1000;

    int STARTING_LOG_THRESHOLD = WORLD;

    boolean STARTING_LOG_WRITE = false;

    // Behavior when opening files that already exist.
    // These were supposed to be strings, for debugging, but
    // interning doesn't seem to work right in 1.0.2, at least across
    // class boundaries.
    int IRRELEVANT = -1; // When opening stderr.

    int ADD = 1111;  // Add a new backup file.

    int OVERWRITE = 0;    // Overwrite any existing backup file.

    int STARTING_LOG_BACKUP_ACTION = ADD;

    // XXX At some point, this might be initialized to some default
    // directory. In Windows, the "current working directory" has
    // a bad habit of hopping around at runtime.
    File STARTING_LOG_DIR = new File(".");

    String STARTING_LOG_TAG = "etrace";

    String LOG_EXTENSION = ".txt";  // DON'T change this to upper case.
    // It causes duplicate filenames.

    // Internationalization, ho ho.
    String DEFAULT_NAME = "default";

    String UNLIMITED_NAME = "unlimited";
}
