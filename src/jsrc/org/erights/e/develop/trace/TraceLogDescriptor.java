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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

/**
 * This class describes the file system interface to a log file. The standard
 * format is <tag>.<date>.txt. The standard backup filename format is then
 * <tag>.<date>.<sequence>.txt.
 * <p/>
 * The entire format may be overridden, the tag may be changed, or the class
 * can be instructed to use PrintStreamWriter.err().
 * <p/>
 * This class is responsible for opening new files. Importantly, that includes
 * renaming old versions.
 */
class TraceLogDescriptor implements Cloneable, TraceConstants {

    /**
     * The date this trace system was initialized; used as part of the standard
     * name format.
     */
    static private final Date OurInitDate = new Date();

    /**
     * The directory in which the log lives.
     */
    private File myDir = STARTING_LOG_DIR;

    /**
     * The 'tag' is the first component of the log filename.
     */
    private String myTag = STARTING_LOG_TAG;

    /**
     * Determine whether PrintStreamWriter.err() is used instead of a file.
     */
    private boolean myUseStderr = false;

    /**
     * True if the user overrode the standard tag.date.txt format.
     */
    private boolean myUsePersonalFormat = false;

    /**
     * The file being used for the log, if format chosen by user.
     */
    private String myPersonalFile;

    /**
     * The stream open to the log. Clients print to this.
     */
    public PrintWriter stream;


    /**
     * This log descriptor always represents standard error. It should never be
     * modified. It starts out "not in use".
     */
    static final TraceLogDescriptor OurStderr;

    static {
        OurStderr = new TraceLogDescriptor();
        OurStderr.myUsePersonalFormat = false;
        OurStderr.myUseStderr = true;
    }

    /**
     * Return the file to use as a backup. Stdout is never backed up, so
     * useStderr should be false.
     *
     * @param clashAction determines which name the backup file will have. ADD
     *                    means a file with the next highest sequence number.
     *                    OVERWRITE means a file with the smallest sequence
     *                    number.
     */
    private File backupFile(File file, int clashAction) {
        T.test(!myUseStderr);

        if (ADD == clashAction) {
            return TraceVersionNamer.factory(file).nextAvailableVersion();
        } else if (OVERWRITE == clashAction) {
            return TraceVersionNamer.factory(file).firstVersion();
        } else {
            T.fail("Bad clashAction " + clashAction);
            return null;
        }
    }

    /**
     * A diverge of a TraceLogDescriptor is one that, when startUsing() is
     * called, will use the same descriptor, be it a file or
     * PrintStreamWriter.err(). The diverge is not inUse(), even if what it was
     * cloned from was.
     */
    Object diverge() {
        try {
            TraceLogDescriptor cl = (TraceLogDescriptor)clone();
            cl.stream = null;
            T.test(!cl.inUse());
            return cl;
        } catch (CloneNotSupportedException e) {
            T.fail("Clone IS SO supported.");
            // Someday I gotta figure out the right way to declare
            // clone.
            return null;
        }
    }

    /**
     * Say what renameToBackupFile will try to do when it's called by
     * startUsing. This is used when the log file will be closed before the
     * renaming is done. It's a way to get some information in the old log
     * file.
     */
    void describeFutureBackupAction(int clashAction) {
        if (myUseStderr) {
            return;  // No backup file.
        }

        File nextFile = desiredLogFile();
        if (!nextFile.exists()) {
            return;         // no backup.
        }

        File backupFile = backupFile(nextFile, clashAction);

        Trace.trace.usagem("The file will be backed up as " + backupFile);
    }

    /**
     * Given the current state of this object's fields, construct the file the
     * user wants. It is a program error to call this routine if the user wants
     * PrintStreamWriter.err(), not a file.
     */
    private File desiredLogFile() {
        T.test(!myUseStderr);
        if (myUsePersonalFormat) {
            if ((new File(myPersonalFile)).isAbsolute()) {
                return new File(myPersonalFile);
            } else {
                return new File(myDir, myPersonalFile);
            }
        } else {
            return new File(myDir,
                            myTag + "." +
                              TraceDateToString.terseCompleteDateString(
                                OurInitDate) + LOG_EXTENSION);
        }
    }

// MAIN OPERATORS

    /**
     * Two LogDescriptors are alike iff they refer to the same (canonical)
     * file.
     */
    public boolean alike(TraceLogDescriptor other) {
        return printName().equals(other.printName());
    }


    private boolean inUse() {
        return stream != null;
    }

    /**
     * Return a name of this descriptor, suitable for printing.
     * <p/>
     * PrintStreamWriter.err() is named "standard error". Real files are named
     * by their canonical pathname (surrounded by single quotes).
     * <p/>
     * Note that the printname may be the absolute pathname if the canonical
     * path could not be discovered (which could happen if the file does not
     * exist.)
     */
    String printName() {
        if (myUseStderr) {
            return "standard error";
        } else {
            String canonical;
            try {
                canonical = desiredLogFile().getCanonicalPath();
            } catch (IOException e) {
                // The canonical path was undiscoverable. Punt by
                // returning the absolute pathname.
                canonical = desiredLogFile().getAbsolutePath();
            }
            if (canonical == null) {
                // Quoth the java language spec:
                // "The canonical form of a pathname of a nonexistent
                // file may not be defined."
                // What happens in that case is ALSO not defined. Null
                // seems like a possibility.
                canonical = desiredLogFile().getAbsolutePath();
            }

            return "'" + canonical.replace('\\', '/') + "'";
        }
    }

// UTILITIES

    /**
     * Attempt to rename this file to a backup file with a version number.
     * Returns true iff the rename succeeds. The name of the backup file is
     * constructed by a TraceVersionName, using the current name of the file.
     * <p/>
     * If the backup file does not exist (and can be written), all is fine.
     * Otherwise:
     *
     * @param clashAction is ADD if a new backup file should be added,
     *                    otherwise OVERWRITE if an existing one should be
     *                    overwritten.
     * @return true if the rename was successful.
     */
    private boolean renameToBackup(File file, int clashAction) {
        T.test(ADD == clashAction || OVERWRITE == clashAction);

        File backupFile = backupFile(file, clashAction);
        if (ADD == clashAction) {
            // backupFile must return a fresh name if the clashAction
            // is ADD. It *may* return a fresh name if the
            // clashAction is OVERWRITE.
            T.test(!backupFile.exists());
        }

        Trace.trace
          .usagem("Renaming previous version of " + file + " to " +
            backupFile + ".");
        try {
            if (backupFile.exists()) {
                // clashAction == OVERWRITE
                if (!backupFile.delete()) {
                    Trace.trace
                      .warningm("The previous version of " + file +
                        " could not be put in backup " + "file " + backupFile +
                        " because the " +
                        " existing file could not be deleted.");
                    return false;
                }
                Trace.trace
                  .eventm("The previous version of " + backupFile +
                    " has been deleted.");
            }
        } catch (SecurityException e) {
            Trace.trace
              .warningm("The previous version of " + file +
                " could not be put in backup " + "file " + backupFile +
                " because the " + " existing file could not be deleted.");
            return false;
        }

        try {
            boolean renamed = file.renameTo(backupFile);
            if (!renamed) {
                Trace.trace
                  .warningm(
                    file + " could not be renamed to backup " + backupFile);
            }
            return renamed;
        } catch (SecurityException e) {
            Trace.trace
              .warningm(file + " could not be renamed to backup " +
                backupFile + " because of a security violation.");
            return false;
        }
    }
// SETTERS

    /**
     * The user wishes to use a directory component different than the default.
     * The file used is unchanged.
     */
    void setDir(String value) {
        T.test(value != null);
        if ("-".equals(value)) {
            myUseStderr = true;
            Trace.trace.eventm("Log destination is set to standard error.");
        } else {
            myUseStderr = false;
            // Don't change value of usePersonalFormat, as the directory
            // is independent of the filename format.
            myDir = new File(value);
            Trace.trace
              .eventm("Log directory will be changed to '" + value + "'.");
            if (!myDir.isDirectory()) {
                Trace.trace
                  .warningm("The log directory was set to '" + value + "', " +
                    "which is not currently a directory.");
            }
        }
    }

    /**
     * If the argument is "-", standard error is used. If the argument is
     * something else, that becomes the complete filename, overriding the tag,
     * eliminating use of the date/time field, and not using the default
     * extension. It does not affect the directory the file is placed in.
     */
    void setName(String value) {
        T.test(value != null);
        if ("-".equals(value)) {
            myUseStderr = true;
            myUsePersonalFormat = false;
            Trace.trace.eventm("Log destination set to standard error.");
        } else {
            myUseStderr = false;
            myUsePersonalFormat = true;
            myPersonalFile = value;
            Trace.trace
              .eventm("Log destination will be changed to " + "file '" +
                myPersonalFile + "'.");
        }
    }

    /**
     * The tag is the initial part of the standard filename. Setting this
     * implies that the date should be included in the filename.
     */
    void setTag(String value) {
        T.test(value != null);
        myUseStderr = false;
        myUsePersonalFormat = false;
        myTag = value;
        Trace.trace.eventm("Log tag set to '" + value + "'.");
    }

    /**
     * Enables this LogDescriptor for use. Most obvious effect is that 'stream'
     * is initialized.
     *
     * @param clashAction determines what to do if the target logfile already
     *                    exists. The two options are ADD (to add a new backup
     *                    file) or OVERWRITE (to overwrite an existing one).
     *                    IRRELEVANT should be used when the destination is
     *                    <em>known</em> to be standard error, which never
     *                    clashes.
     * @throws Exception is thrown if a logfile could not be opened. The
     *                   contents of the exception are irrelevant, as this
     *                   method logs the problem.
     */
    void startUsing(int clashAction) throws Exception {
        T.test(!inUse());

        if (myUseStderr) {
            Trace.trace.eventm("Logging has been directed to standard error.");
            stream = PrintStreamWriter.stderr();
            return;
        }

        T.test(ADD == clashAction || OVERWRITE == clashAction);

        File nextFile = desiredLogFile();
        Trace.trace.eventm("Logging has been directed to '" + nextFile + "'.");

        if (nextFile.exists()) {
            if (nextFile.isDirectory()) {
                Trace.trace
                  .errorm("Attempt to open directory " + nextFile +
                    " as a logfile failed.");
                throw new IOException("opening directory as a logfile");
            }
            // Try to back it up. If that fails, oh well. Open
            // the desiredLogFile anyway. That is less harmful than
            // the alternative, which would be to spew output to
            // stderr. I think.
            renameToBackup(nextFile, clashAction);
        }

        try {
            stream = new PrintWriter(new FileOutputStream(nextFile), true);
        } catch (SecurityException e) {
            Trace.trace
              .errorm("Security exception when opening new trace file '" +
                nextFile + "'.");
            throw e;
        } catch (FileNotFoundException e) {
            Trace.trace
              .errorm("Could not open new trace file '" + nextFile + "'.");
            throw e;
        } catch (IOException e) {
            Trace.trace
              .errorm("Unknown error when opening new trace file '" +
                nextFile + "'.");
            throw e;
        }
    }

    /**
     * Cease using this LogDescriptor. The most obvious effect is that 'stream'
     * is now null. Behind the scenes, any open file is closed. You can
     * alternate stopUsing() and startUsing() an arbitrary number of times.
     */
    void stopUsing() {
        T.test(inUse());
        if (stream != PrintStreamWriter.stderr()) {
            // I don't trust finalizers to do it, at least not in time.
            Trace.trace.eventm("Closing " + printName());
            stream.close();
        }
        stream = null;
    }
}
