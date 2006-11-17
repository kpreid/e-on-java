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


import org.erights.e.develop.format.ETimeFormat;

import java.util.Date;

/**
 * Convert java.util.Dates into suitable Strings for the Trace system.
 * <p/>
 * Note that times are printed in local time rather than GMT. People get
 * confused when looking at log files that are not in the local time - they
 * know something bad happened at 4 o'clock, but there's nothing in the log for
 * that time.
 * <p/>
 * For synchronizing trace logs from different time zones, this is inadequate.
 * We'll deal with that when we come to it.
 * <p/>
 * Note that the construction of strings in this class is one of the
 * bottlenecks in this code.
 * <p/>
 * Changed to use ISO8601 according to {@link ETimeFormat}.
 *
 * @author Brian Marick
 * @author Mark S. Miller (modified to use ISO8601)
 */
class TraceDateToString {

    /**
     * XXX should fix the tracing package to use 'long absMillis' instead of
     * 'Date date'
     */
    final static String dateTimeString(Date date) {
        return ETimeFormat.formatTime(date.getTime());
    }

    /**
     * This must return one that can be used within a filename, so convert ":"s
     * to "_"s.
     */
    final static String terseCompleteDateString(Date date) {
        return ETimeFormat.formatTime(date.getTime()).replace(':', '_');
    }
}
