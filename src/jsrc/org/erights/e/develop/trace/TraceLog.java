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

import java.util.Enumeration;
import java.util.Vector;

/**
 * This class manages dumping of messages to the semi-permanent
 * on-disk log.
 */
class TraceLog implements TraceMessageAcceptor, TraceConstants {

    /**
     *
     */
    private final Object myLock = new Object();

    /**
     * Determine whether to rename full log files and open new
     * ones, or to empty a full log file.
     */
    private int myBackupAction = STARTING_LOG_BACKUP_ACTION;

    /**
     * Determine whether a log file should be written at all.
     */
    private boolean myWrite = STARTING_LOG_WRITE;

    /**
     * The definition, in number of characters, of what a full log
     * file is.
     */
    private long myMaxSize = STARTING_LOG_SIZE_THRESHOLD;

    /**
     * Number of characters in the current log file.
     */
    private long myCurrentSize;

    /**
     * The log to which messages are currently flowing. Initially
     * null, but all messages will normally be queued until
     * it's pointed at a log file or stderr. Messages will be
     * redirected to stderr if a given logfile can't be opened.
     */
    private TraceLogDescriptor myCurrent;

    /**
     * The user can change the characteristics of this log descriptor,
     * then redirect the log to it.
     * <p/>
     * Characteristics are changed via properties like "TraceLog_tag".
     * Redirection is done via "TraceLog_reopen".
     */
    private TraceLogDescriptor myPending = new TraceLogDescriptor();

    /**
     * True if all the initial properties have been processed.
     */
    private boolean mySetupComplete = false;

    /**
     * The first time we actually output to a non-stderr trace file, we print
     * a notice on stderr that this has happened.
     * <p>
     * XXX Should provide a property allowing this to be initialized to true,
     * in order to be able to suppress this diagnostic.
     */
    private boolean myStderrNotified = false;

    /**
     * This converts a TraceMessage into the kind of string we like.
     */
    private final TraceMessageStringifier myStringifier =
      new TraceMessageStringifier();

    /**
     * This variable is used to count accurately the size of the log.
     * Note that I don't try to catch a SecurityException if one occurs.
     * We'll have other, bigger, problems if that happens.
     */
    static private final int OurLineSeparatorLength =
      System.getProperty("line.separator").length();

// QUEUE MANAGEMENT

    /**
     * Messages are queued here before the Log is initialized and
     * while switching to a new logfile.
     */
    private Vector queuedMessages;

    /**
     * Construct the trace log. Queue messages until setup is
     * complete.
     */
    TraceLog() {
        // DANGER:  This constructor must be called as part of static
        // initialization of TraceController. Until that
        // initialization is done, Trace should not be loaded.
        // Therefore, nothing in this constructor should directly or
        // indirectly use a tracing function.
        startQueuing();
    }

    /**
     * Accept a message for the log. It will be discarded if both writing
     * and the queue are turned off.
     */
    public void accept(TraceMessage message) {
        synchronized (myLock) {
            if (!isAcceptingMessages()) {
                return;
            }

            if (isQueuing()) {
                queuedMessages.addElement(message);
            } else {
                acceptBypassingQueue(message);
            }
        }
    }

    /**
     * that we will
     * Take a message and log it. The queue of pending messages
     * maintained before setup is complete is bypassed, because this
     * is the method used to drain that queue.
     */
    private void acceptBypassingQueue(TraceMessage message) {
        T.test(myCurrent.stream != null,
               "Trace system has null stream.");
        String output = myStringifier.toString(message);
        myCurrent.stream.println(output);
        // Note: there's little point in checking for an
        // output error. We can't put the trace in the log,
        // and there's little chance the user would see it
        // in the trace buffer. So we ignore it, with regret.

        if (!myStderrNotified &&
          myCurrent.stream != PrintStreamWriter.stderr()) {
            PrintStreamWriter.stderr().println(
              "***-----------------------***\n" +
              "*** Trace data written to ***\n" +
              myCurrent.printName() + "\n" +
              "***-----------------------***"
            );
            myStderrNotified = true;
        }

        myCurrentSize += output.length() + OurLineSeparatorLength;
        if (myCurrentSize > myMaxSize) {
            handleFullLog();
        }
    }

// THE MECHANICS OF SWITCHING LOGS

    /**
     * Call to initialize a log when logging is just beginning (or
     * resuming after having been turned off). There is no current
     * log, so nothing is written to it. If the pending log cannot be
     * opened, standard error is used as the log. In any case, the
     * queue is drained just before the method returns.
     */
    private void beginLogging() {
        T.test(myCurrent == null);
        try {
            myPending.startUsing(myBackupAction); // rename any existing file.
        } catch (Exception e) {
            // Couldn't open the log file. Bail to stderr.
            Trace.trace.shred(e, "Exception has already been logged.");

            myCurrent = TraceLogDescriptor.OurStderr;
            try {
                myCurrent.startUsing(IRRELEVANT);
            } catch (Exception ignore) {
                T.fail("Exceptions shouldn't happen opening stderr.");
            }
            drainQueue();
            return;
        }
        myCurrent = myPending;
        Trace.trace.usagem("Logging begins on " +
                           myCurrent.printName() + ".");
        myPending = (TraceLogDescriptor)myCurrent.diverge();
        myCurrentSize = 0;
        drainQueue();
    }

    /**
     * Change how a full logfile handles its backup files. "one" or
     * "1" means that there will be at most one backup file, which will
     * be overwritten if needed. "many" means a new backup file with
     * a new name should be created each time the base file fills up.
     * Has effect when the next log file fills up.
     */
    void changeBackupFileHandling(String newBehavior) {
        synchronized (myLock) {
            T.test(newBehavior != null);
            if (newBehavior.equalsIgnoreCase("one") ||
              newBehavior.equals("1")) {

                Trace.trace.eventm("Backup files will be overwritten.");
                myBackupAction = OVERWRITE;
            } else if (newBehavior.equalsIgnoreCase("many")) {
                Trace.trace.eventm("New backup files will always be created.");
                myBackupAction = ADD;
            } else {
                Trace.trace.errorm("TraceLog_backups property was given unknown value '" +
                                   newBehavior + "'.");
            }
        }
    }

    /**
     * Change the default directory in which logfiles live. Has
     * effect only when a new logfile is opened.
     */
    void changeDir(String value) {
        synchronized (myLock) {
            myPending.setDir(value);
        }
    }

    /**
     * Explicitly set the name of the next logfile to open. Overrides
     * the effect of "TraceLog_dir" only if the given name is absolute.
     * Has effect only when a new logfile is opened.
     */
    void changeName(String value) {
        synchronized (myLock) {
            myPending.setName(value);
        }
    }

    /**
     * Change the new maximum allowable size for a logfile.
     * Has effect on the current logfile. Note that the trace system
     * does not prevent the log from exceeding this size; it only opens
     * a new log file as soon as it does.
     */
    // Note:  pretty much duplicates TraceBuffer.changeSize

    void changeSize(String value) {
        synchronized (myLock) {
            long newSize;
            if (value.equalsIgnoreCase(DEFAULT_NAME)) {
                newSize = STARTING_LOG_SIZE_THRESHOLD;
            } else if (value.equalsIgnoreCase(UNLIMITED_NAME)) {
                newSize = Long.MAX_VALUE;
            } else {
                try {
                    newSize = Long.parseLong(value);
                } catch (NumberFormatException e) {
                    Trace.trace.errorm("Log size cannot be changed to illegal value '" +
                                       value + "'.");
                    newSize = myMaxSize;  // leave unchanged.
                }
            }

            if (newSize < SMALLEST_LOG_SIZE_THRESHOLD) {
                Trace.trace.errorm(value +
                                   " is too small a threshold size for the log. "
                                   + "Try " + SMALLEST_LOG_SIZE_THRESHOLD + ".");
                newSize = myMaxSize;
            }

            myMaxSize = newSize;
            if (myCurrentSize > myMaxSize) {
                handleFullLog();  // note that it's OK if log is to stderr.
            }
        }
    }

    /**
     * Change the 'tag' (base of filename) that logfiles have. Has
     * effect only when a new logfile is opened.
     */
    void changeTag(String value) {
        synchronized (myLock) {
            myPending.setTag(value);
        }
    }
// HANDLING OF PROPERTIES / LOG CONTROL

    /**
     * The meaning of changeWrite is complicated. Here are the cases
     * when it's used to turn writing ON.
     * <p/>
     * If setup is still in progress, the state
     * variable 'myWrite' is used to note that logging should
     * begin when setupIsComplete() is called.
     * <p/>
     * If setup is complete, logging should begin
     * immediately. If logging has already begun, this is a no-op.
     * <p/>
     * Here are the cases for turning writing OFF.
     * <p/>
     * If setup is not complete, the state variable
     * 'myWrite' informs setupIsComplete() that logging should not begin.
     * <p/>
     * If setup is complete, logging is stopped. However, if it was
     * already stopped, the call is a no-op.
     * <p/>
     * There would be some merit in having a state machine implement
     * all this.
     */
    void changeWrite(String value) {
        synchronized (myLock) {
            T.test(value != null);
            if (value.equalsIgnoreCase("true")) {
                if (myWrite) {
                    Trace.trace.warningm(
                      "Log writing enabled twice in a row.");
                } else {
                    myWrite = true;
                    startQueuing(); // it's ok if the queue already started.
                    Trace.trace.eventm("Logging is enabled.");
                    if (mySetupComplete) {
                        Trace.trace.debugm("Log will be opened.");
                        beginLogging();
                    }
                    // else
                    // do nothing - setupIsComplete() will handle this case.
                }
            } else if (value.equalsIgnoreCase("false")) {
                if (!myWrite) {
                    Trace.trace.warningm("Log writing disabled, " +
                                         "but it was already disabled.");
                } else {
                    Trace.trace.eventm("Logging disabled.");
                    drainQueue(); // either write messages or discard them
                    myWrite = false;
                    if (mySetupComplete) {
                        myCurrent.stopUsing();
                        myCurrent = null;
                    } else {
                        T.test(myCurrent == null);
                    }
                }
            } else {
                Trace.trace.errorm("TraceLog_write property was given value '" +
                                   value + "'.");
            }
        }
    }

    /**
     * Deal with messages on the queue. If the log is turned on
     * (myWrite is true), they are written. Otherwise, they are
     * discarded. It is safe to call this routine without knowing
     * whether queuing is in progress.
     */
    private final void drainQueue() {
        if (Trace.trace != null) {
            Trace.trace.debugm("Draining queue; write = " + myWrite +
                               ", isQueuing = " + isQueuing());
        }
        if (myWrite && isQueuing()) {
            Enumeration e = queuedMessages.elements();
            while (e.hasMoreElements()) {
                TraceMessage message = (TraceMessage)e.nextElement();
                acceptBypassingQueue(message);
            }
        }
        /*
        else if (isQueuing()) {
            if (Trace.trace != null && Trace.trace.debug && Trace.ON) {
                PrintStreamWriter.err().println(
                  "Flushing the following messages.");
                Enumeration e = queuedMessages.elements();
                while (e.hasMoreElements()) {
                    PrintStreamWriter.err().println(
                      (TraceMessage)e.nextElement());
                }
                PrintStreamWriter.err().println("===END");
            }
        }
        */
        queuedMessages = null;
    }


// MISC
    /**
     * Call when the logfile fills up. Reopens the same log file.
     * <P>
     * Standard output can never fill up, so this routine is a no-op
     * when the current size of text sent to standard out exceeds the
     * maximum, except that the current size is reset to zero.
     */
    private void handleFullLog() {
        // Preemptively set the log size back to zero. This allows log
        // messages about the fullness of the log to be placed into
        // the log, without getting into an infinite recursion.
        myCurrentSize = 0;
        if (myCurrent.stream != PrintStreamWriter.stderr()) {
            Trace.trace.worldm("This log is full.");
            shutdownAndSwap();
        }
    }

    /**
     * Call to switch to a log when another - with a different name -
     * is currently being used.
     * If the pending log cannot be opened, the current log continues
     * to be used.
     * <p/>
     * Before the old log is closed, a USAGE message is logged,
     * directing the reader to the new log. Trace messages may be
     * queued while the swap is happening, but the queue is drained
     * before the method returns.
     * <p/>
     * This routine is never called when the logfile fills - it's only
     * used when explicitly reopening a log file. (TraceLog_reopen=true).
     */
    private void hotSwap() {
        T.test(myCurrent != null);
        T.test(myCurrent.stream != null);
        T.test(!isQueuing());

        // Finish the old log with a pointer to the new.
        Trace.trace.usagem("Logging will continue on " +
                           myPending.printName() + ".");

        startQueuing(); // further messages should go to the new log.
        try {
            // rename an existing file, since it is not an earlier
            // version of the new name we're using.
            myPending.startUsing(myBackupAction);
        } catch (Exception e) {
            Trace.trace.shred(e, "Exception has already been logged.");
            // continue using current.
            drainQueue();
            return;
        }
        // Stash old log name to print in new log.
        String lastLog = myCurrent.printName();

        myCurrent.stopUsing();
        myCurrent = myPending;
        Trace.trace.usagem("Logging begins on " +
                           myCurrent.printName() + ".");
        Trace.trace.worldm("Previous log was " + lastLog + ".");

        myCurrentSize = 0;
        myPending = (TraceLogDescriptor)myCurrent.diverge();
        drainQueue();
    }

    /**
     * The log accepts messages if the "TraceLog_write" property was set.
     * Before setup is completed, it also accepts and queues up
     * messages. When setup is complete, it either posts or discards
     * those queued messages, depending on what the user wants.
     * <p/>
     * Queuing also happens transitorily while logs are being
     * switched.
     */
    private final boolean isAcceptingMessages() {
        return myWrite || isQueuing();
    }

    private final boolean isQueuing() {
        return queuedMessages != null;
    }

    /**
     * The gist of this routine is that it shuts down the current log
     * and reopens a new one (possibly with the same name, possibly
     * with a different name). There are some special cases, because
     * this routine could be called before setup is complete (though using
     * TraceLog_reopen in the initial Properties is deprecated).
     * <p/>
     * When it's called before setup is complete and writing is not enabled,
     * the behavior is the same as TraceLog_write [the preferred interface].
     * <p/>
     * When it's called before setup is complete and writing is enabled, the
     * effect is that of calling TraceLog_write twice (a warning).
     * <p/>
     * When it's called after setup is complete and writing is not enabled,
     * the behavior is the same as calling TraceLog_write [again, the
     * preferred interface, because you're not "reopening" anything].
     * <p/>
     * When it's called after setup is complete and writing is enabled; this
     * is the way it's supposed to be used. The current log is closed and the
     * pending log is opened.
     */
    void reopen(String ignored) {
        synchronized (myLock) {
            if (!myWrite || !mySetupComplete) {
                changeWrite("true");
            } else if (myPending.alike(myCurrent)) {
                shutdownAndSwap();
            } else {
                hotSwap();
            }
        }
    }
// METHODS FROM THE MESSAGE ACCEPTOR INTERFACE

    /**
     * Call this only after all properties have been processed.
     * It begins logging, but only if TraceLog_write or TraceLog_reopen have
     * been used, or if the default behavior is to write.
     */
    public void setupIsComplete() {
        synchronized (myLock) {
            T.test(isQueuing());
            Trace.trace.eventm("Logging is being started.");
            if (myWrite) {
                beginLogging();
            }
            drainQueue();
            mySetupComplete = true;
        }
    }

    /**
     * Call to initialize a log when the same file is already open.
     * The old file must be closed before it can be renamed to a
     * backup version. (This is either a Windows or java.io
     * restriction.)  If the pending log cannot be opened, standard
     * output is used.
     * <p/>
     * Before the old log is closed, a USAGE message is logged,
     * directing the reader to the new log. Trace messages may be
     * queued while the swap is happening, but the queue is drained
     * before the method returns.
     * <p/>
     * This routine can be called to backup a full logfile, or to
     * explicitly reopen the same logfile (via TraceLog_reopen=true).
     */
    private void shutdownAndSwap() {
        T.test(myCurrent != null);
        T.test(myCurrent.stream != null);
        T.test(!isQueuing());

        // In the old log, say what will happen. Can't log it
        // while it's happening, because that all goes to the
        // new log.
        myCurrent.describeFutureBackupAction(myBackupAction);

        // Stash old log name. This is used if reopening fails and
        // further logging is blurted to stderr.
        String lastLog = myCurrent.printName();

        myCurrent.stopUsing();

        startQueuing(); // further messages should go to the new log.

        try {
            myPending.startUsing(myBackupAction);
        } catch (Exception e) {
            Trace.trace.shred(e, "Exception has already been logged.");
            myCurrent = TraceLogDescriptor.OurStderr;
            myCurrentSize = 0;
            try {
                myCurrent.startUsing(IRRELEVANT);
            } catch (Exception ignore) {
                T.fail("No exceptions when opening stderr.");
            }
            drainQueue();
            Trace.trace.worldm("Previous log was " + lastLog + ".");
            return;
        }

        myCurrent = myPending;
        myCurrentSize = 0;
        Trace.trace.usagem("Logging continues on " +
                           myCurrent.printName() + ".");
        Trace.trace.worldm("This is a continuation of a previous log.");
        myPending = (TraceLogDescriptor)myCurrent.diverge();
        drainQueue();
    }

    /**
     * Redirect trace messages to a queue. Used while switching to a
     * new log file, or before setup is complete.
     * <p/>
     * It is harmless to call this routine twice.
     */
    private final void startQueuing() {
        // Note:  there can be no trace messages in this routine,
        // because it's called from the constructor.
        if (!isQueuing()) {
            queuedMessages = new Vector();
        }
    }
}
