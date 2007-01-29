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
import org.erights.e.develop.exception.PrintStreamWriter;

import java.io.File;
import java.io.PrintWriter;

/**
 * This class and its subclasses know how to construct backup version Files for
 * given Files.
 * <p/>
 * This class constructs backup names by appending sequence numbers to the
 * original name.
 */
class TraceVersionNamer implements TraceConstants {

    /**
     * The file for which a version number is being created.
     */
    private final File myFile;

    /**
     * The 'filename' part of the given file, sans directory.
     */
    final String myName;

    /**
     * A directory that contains the given file.
     */
    final String myDir;

    /**
     * The basename is that part of the file that precedes a sequence number.
     */
    final String myBasename;

    /**
     * Create a TraceVersionNamer from the given file. The file object must
     * have a directory and name component. It may be absolute or relative.
     */
    TraceVersionNamer(File aFile) {
        myFile = aFile;
        myName = myFile.getName();
        T.test(myName != null);

        myDir = myFile.getParent();
        T.test(myDir != null);

        myBasename = fetchBasename();
        Trace.trace
          .debugm("Finding next version of " + myFile + "(" + myDir + " " +
            myName + " " + myBasename + ")");
    }

    /**
     * Create a backup file name, given a sequence number.
     */
    File constructVersion(int sequence) {
        return new File(myDir, myBasename + sequence);
    }

    /**
     * Return a TraceTxtVersionNamer if the file ends with ".txt"; otherwise,
     * return a TraceVersionNamer.
     */
    static TraceVersionNamer factory(File file) {
        if (file.getName().toLowerCase().endsWith(LOG_EXTENSION)) {
            return new TraceTxtVersionNamer(file);
        } else {
            return new TraceVersionNamer(file);
        }
    }

    /**
     * The basename of a backup version, including any trailing '.' separating
     * the basename from the sequence number.
     */
    String fetchBasename() {
        return myName + ".";
    }

    /**
     * The backup file with sequence number 0. Doesn't matter if it exists.
     */
    File firstVersion() {
        File retval = constructVersion(0);
        Trace.trace.eventm("Backup version for " + myFile + " is " + retval);
        return retval;
    }

    /**
     * Return a sequence number, if the given file contains one. If it does not
     * contain one, return -1. Do not call this method unless mightHaveSeq has
     * approved the filename.
     */
    int getSeq(String filename) {
        String possibleSeqString = filename.substring(myBasename.length());

        try {
            return Integer.parseInt(possibleSeqString);
        } catch (NumberFormatException e) {
            Trace.trace.shred(e, filename + " is not a backup file.");
            return -1;
        }
    }

    static public void main(String[] args) {
        TraceController.setProperty("TraceLog_trace", "debug");
        TraceVersionNamer v;
        if (1 < args.length) {
            v = factory(new File(args[0], args[1]));
        } else {
            v = factory(new File(args[0]));
        }
        PrintWriter err = PrintStreamWriter.stderr();
        err.println("myFile " + v.myFile);
        err.println("myDir " + v.myDir);
        err.println("myName " + v.myName);
        err.println("myBasename " + v.myBasename);
        err.println(v.nextAvailableVersion());
        err.println(v.firstVersion());
    }

    /**
     * True iff the filename is of a format that <em>could</em> be a backup
     * version of the original file. It remains to be determined whether it
     * truly contains a sequence number.
     * <p/>
     * In a stunning display of write-once-run-everywhere, the check is
     * case-insensitive. This obeys Windows conventions about what "same files"
     * are, not Unix conventions.
     *
     * @param filename a filename, not including any directory part.
     */
    boolean mightHaveSeq(String filename) {
        int minlen = myBasename.length() + 1;
        return filename.length() >= minlen &&
          filename.toLowerCase().startsWith(myBasename.toLowerCase());
    }

    /**
     * Return the next file in the sequence <foo>, <foo>.0, <foo>.1, etc.
     * Subclasses will have their own name-construction rules.
     */
    File nextAvailableVersion() {
        int highestSeq = -1;
        String[] files = (new File(myDir)).list();

        if (files == null) {
            // We were asked for a file in a nonexistent directory.
            // It's safe to assume that there are no clashing names.
            return firstVersion();
        }

        for (int i = 0; i < files.length; i++) {
            if (mightHaveSeq(files[i])) {
                int possibleSeq = getSeq(files[i]);
                if (0 > possibleSeq) {
                    Trace.trace
                      .verbosem(files[i] + " has no sequence number.");
                } else if (possibleSeq <= highestSeq) {
                    Trace.trace.verbosem(files[i] + " is too low.");
                } else {
                    highestSeq = possibleSeq;
                    Trace.trace.verbosem(highestSeq + " is the best so far.");
                }
            } else {
                Trace.trace
                  .verbosem(files[i] + " is not in version file format.");
            }
        }
        File retval = constructVersion(highestSeq + 1);
        Trace.trace.eventm("Backup version for " + myFile + " is " + retval);
        return retval;
    }
}
