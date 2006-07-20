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

import org.erights.e.develop.assertion.T;

/**
 * Translate numerical trace levels into strings and vice versa.
 */
class TraceLevelTranslator implements TraceConstants {

    /**
     * Convert tracing thresholds into three-character synonyms.
     * Used when printing trace messages, so it does return the
     * "TIM" tag to denote a timing message.
     */
    static String terse(int level) {
        String retval;
        switch (level) {
        case ERROR:
            retval = "ERR";
            break;
        case WARNING:
            retval = "WRN";
            break;
        case WORLD:
            retval = "WLD";
            break;
        case USAGE:
            retval = "USE";
            break;
        case EVENT:
            retval = "EVN";
            break;
        case DEBUG:
            retval = "DBG";
            break;
        case VERBOSE:
            retval = "VRB";
            break;
        case TIMING:
            retval = "TIM";
            break;
        default:
            T.fail("Left level out of TraceLevelTranslator.terse.");
            retval = "";    // silence compiler.
            break;
        }
        return retval;
    }

    /**
     * Convert a string into one of the numeric trace levels. Because
     * this method is used only to identify priority thresholds, it
     * does not return the TIMING pseudo-level. (See TraceConstants.java.)
     *
     * @throws IllegalArgumentException if the string is not recognized.
     */
    static int toInt(String level) throws IllegalArgumentException {
        if (level.equalsIgnoreCase("error")) {
            return ERROR;
        } else if (level.equalsIgnoreCase("warning")) {
            return WARNING;
        } else if (level.equalsIgnoreCase("world")) {
            return WORLD;
        } else if (level.equalsIgnoreCase("usage")) {
            return USAGE;
        } else if (level.equalsIgnoreCase("event")) {
            return EVENT;
        } else if (level.equalsIgnoreCase("debug")) {
            return DEBUG;
        } else if (level.equalsIgnoreCase("verbose")) {
            return VERBOSE;
        } else {
            String problem = "Incorrect tracing level '" + level + "'";
            Trace.trace.errorm(problem);
            throw new IllegalArgumentException(problem);
        }
    }
}
