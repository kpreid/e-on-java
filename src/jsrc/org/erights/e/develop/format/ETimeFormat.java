package org.erights.e.develop.format;

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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Just converts back and forth between milliseconds since the epoch
 * (standard binary time representation) and ISO8601/UTC (standard
 * sortable textual respresentation) but enhanced to represent milliseconds.
 *
 * @author Mark S. Miller
 * @see <a href="http://www.w3.org/TR/1998/NOTE-datetime-19980827">w3c's
 *      explanation of </a>
 * @see <a href="http://www.w3.org/TR/REC-html40/types.html#type-datetime"
 *      >html4.0's explanation</a>
 */
public class ETimeFormat {

    static private final DateFormat SortableFormat =
      new SimpleDateFormat("yyyy-MM-dd!HH:mm:ss.SSS%");

    static {
        TimeZone utc = TimeZone.getTimeZone("UTC");
        SortableFormat.setTimeZone(utc);
    }

    /**
     * Shows absMillis (since epoch) as a sortable absolute date/time string
     * that can be included in a single URI field (no "#"s or "/"s).
     * <p/>
     * The format must also be independent of Locale, the default current
     * TimeZone, or the current time. In the terminology of <a
     * href="http://www.w3.org/TR/1998/NOTE-datetime-19980827">ISO601</a>,
     * the actual format is "YYYY-MM-DDThh:mm:ss.sssTZD", where the value of
     * TZD (timezone) is "Z", indicating UTC (Universal Standard Time, which
     * is just another name for GMT).
     * <p/>
     * It would be good if the format returned could also be used
     * within filenames, but unfortunately this conflicts with the
     * familiar use of colon (":") to represent clock time, as
     * mandated by ISO8601. Clients (such as the Trace system) that
     * need to embed the result in a filename should do a
     * <tt>.replace(':','_')</tt>. {@link #parseTime(String)}
     * will handle either colons or underbars in its input.
     */
    static public String formatTime(long absMillis) {
        String formatted = SortableFormat.format(new Date(absMillis));
        return formatted.replace('!', 'T').replace('%', 'Z');
    }

    /**
     * Given a sortableTime string in the format returned by millisToDate,
     * return the corresponding number of seconds since the epoch.
     */
    static public long parseTime(String sortableTime) throws ParseException {
        sortableTime = sortableTime.replace('T', '!')
          .replace('Z', '%')
          .replace('_', ':');
        return SortableFormat.parse(sortableTime).getTime();
    }
}
