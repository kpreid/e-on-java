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

/**
 * This class and its subclasses know how to construct backup version Files for
 * files that end in ".txt".
 */

class TraceTxtVersionNamer extends TraceVersionNamer {

    /**
     * The length of the ".txt" extension, for convenience.
     */
    static private final int EXTLEN = LOG_EXTENSION.length();

    TraceTxtVersionNamer(File aFile) {
        super(aFile);
    }


    File constructVersion(int sequence) {
        return new File(myDir, myBasename + sequence + LOG_EXTENSION);
    }


    String fetchBasename() {
        // Showing that the trailing "." is part of the basename, for clarity.
        return myName.substring(0, myName.length() - EXTLEN) + ".";
    }


    int getSeq(String filename) {
        String possibleSeqString =
          filename.substring(0, filename.length() - EXTLEN).
            substring(myBasename.length());

        try {
            return Integer.parseInt(possibleSeqString);
        } catch (NumberFormatException e) {
            Trace.trace.shred(e, filename + " is not a backup file.");
            return -1;
        }
    }


    boolean mightHaveSeq(String filename) {
        int minlen = myBasename.length() + 1 + EXTLEN;
        return filename.length() >= minlen &&
          filename.toLowerCase().startsWith(myBasename.toLowerCase()) &&
          filename.toLowerCase().endsWith(LOG_EXTENSION);
    }
}
