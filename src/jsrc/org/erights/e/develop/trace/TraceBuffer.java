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
 * Class that controls storing of TraceMessages in core, for later or
 * concurrent display.
 * <p/>
 * The tracebuffer is a linked list of trace messages. That linked list may be
 * a subset of a larger list. Just because an old message falls off the end of
 * this list does NOT mean it should be destroyed. Someone else (e.g., the
 * inspector) might be using it.
 */
class TraceBuffer implements TraceMessageAcceptor, TraceConstants {

    /**
     *
     */
    private final Object myLock = new Object();

    /**
     * The first message in the buffer.
     */
    private TraceMessage myStart;

    /**
     * The last message in the buffer.
     */
    private TraceMessage myEnd;

    /**
     * The max number of messages between start and end (inclusive).
     */
    private int myMaxSize = STARTING_TRACE_BUFFER_SIZE;

    /**
     * The current number.
     */
    private int myCurrentSize;

    TraceBuffer() {
        // DANGER:  This constructor must be called as part of static
        // initialization of TraceController. Until that
        // initialization is done, Trace should not be loaded.
        // Therefore, nothing in this constructor should directly or
        // indirectly use a tracing function.

        // Doing this message initialization this way is pretty icky,
        // but we can't actually *post* this message because
        // the trace system doesn't fully exist yet.
        TraceMessage message =
          unpostedTraceMessage("Transient buffer begins.", WORLD);

        myStart = message;
        myEnd = message;
        myCurrentSize = 1;
    }

    public void accept(TraceMessage message) {
        synchronized (myLock) {
            T.test(myStart != null);
            T.test(myEnd != null);
            T.test(message != null);

            myCurrentSize++;
            myEnd.next = message;
            myEnd = message;

            // In theory, this loop should only ever execute once.
            while (myCurrentSize > myMaxSize) {
                myStart = myStart.next;
                myCurrentSize--;
            }
        }
    }

    // Note:  pretty much duplicates TraceLog.changeSize.

    void changeSize(String value) {
        synchronized (myLock) {
            int newSize;
            if (value.equalsIgnoreCase(DEFAULT_NAME)) {
                newSize = STARTING_TRACE_BUFFER_SIZE;
            } else if (value.equalsIgnoreCase(UNLIMITED_NAME)) {
                newSize = Integer.MAX_VALUE;
            } else {
                try {
                    newSize = Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    Trace.trace
                      .errorm(
                        "Buffer size cannot be changed to illegal value '" +
                          value + "'.");
                    newSize = myMaxSize;  // leave unchanged.
                }
            }

            if (newSize < 1) {
                Trace.trace
                  .errorm(value +
                    " is too small a threshold size for the log. " +
                    "Ignoring.");
                newSize = myMaxSize;
            }

            myMaxSize = newSize;
            while (myCurrentSize > myMaxSize) {
                myStart = myStart.next;
                myCurrentSize--;
            }
        }
    }

    /**
     * Dump the buffer to a file. This is a quasi-temporary function. It would
     * be better (probably) if the buffer were dumped to a window, which could
     * then be dumped to a file. TraceDisplay.java is an early attempt at that,
     * but foundered on lack of time and dependencies on classes that aren't
     * built until long after trace.
     */
    void dump(String destination) {

        synchronized (myLock) {
            PrintWriter stream;

            // outputDestination is destination, except when
            // destination is "-", signifying stderr. In that
            // case, it's "standard error". Why not just pass in
            // "standard error"?  Because the user elsewhere uses
            // "-" to mean standard output, and might choose to use
            // it again here. She will be unhappy if a file named
            // "-" is created. Hopefully, she will not be terribly surprised
            // by our use of stderr rather than stdout.
            String outputDestination = destination;
            if (destination.equals("-")) {
                stream = PrintStreamWriter.stderr();
                outputDestination = "standard error";
            } else {
                try {
                    FileOutputStream fos =
                      new FileOutputStream(new File(destination));
                    stream = new PrintWriter(fos, true);
                } catch (SecurityException e) {
                    Trace.trace
                      .errorm("Security exception when opening dump file '" +
                        outputDestination + "'.");
                    return;
                } catch (FileNotFoundException e) {
                    Trace.trace
                      .errorm("Could not open dump file '" +
                        outputDestination + "'.");
                    return;
                } catch (IOException e) {
                    Trace.trace
                      .errorm("Unknown error when opening dump file '" +
                        outputDestination + "'.");
                    return;
                }
            }

            Trace.trace
              .usagem("Dumping internal trace buffer to " + outputDestination);

            TraceMessageStringifier stringifier =
              new TraceMessageStringifier();

            TraceMessage current = myStart;
            // The begin/end lines make the dump easier to see when it and the
            // log are going to the same place (typically stderr).
            stream.println("======================= BEGIN TRACE BUFFER DUMP " +
              "=======================");
            while (current != null) {
                String output = stringifier.toString(current);
                stream.println(output);
                current = current.next;
            }
            if (stream.checkError()) {
                Trace.trace
                  .errorm(
                    "Could not dump trace buffer to " + outputDestination);
            }
            stream.println("======================= END TRACE BUFFER DUMP " +
              "=======================");
            if (stream != PrintStreamWriter.stderr()) {
                stream.close();
            }
        }
    }

    /**
     * Dump the buffer to a TraceMessageAcceptor.
     */
    void dump(TraceMessageAcceptor acceptor) {
        synchronized (myLock) {
            Trace.trace.usagem("Dumping internal trace buffer.");

            // We want to demarcate the dumped trace in the new log, but
            // not have the message end up in this log, which could be
            // confusing.

            TraceMessage traceMessage = unpostedTraceMessage(
              "======================= BEGIN INTERNAL TRACE BUFFER DUMP " +
                "=======================",
              ERROR);
            acceptor.accept(traceMessage);

            TraceMessage current = myStart;
            while (current != null) {
                acceptor.accept(current);
                current = current.next;
            }
            traceMessage = unpostedTraceMessage(
              "======================= END INTERNAL TRACE BUFFER DUMP " +
                "=======================",
              ERROR);
            acceptor.accept(traceMessage);
        }
    }

    // Note:  because messages are never thrown away,
    // except by GC, it is safe for this not to be synchronized.

    TraceMessage getFirstMessage() {
        T.test(myStart != null);
        return myStart;
    }

    // This class does nothing differently before or after setup.
    public void setupIsComplete() {
    }

    /**
     * Create a trace message that isn't posted to all the message acceptors.
     * Used for messages that have one specific destination.
     */
    private TraceMessage unpostedTraceMessage(String message, int level) {
        return recordTraceMessageXyzzY(message, level);
    }

    /**
     * What's the deal with the funny name? Why does unpostedTraceMessage just
     * call this? It's because we parse the line number for the routine that
     * called a trace method out of the stack. That parsing code (TraceCaller)
     * looks for this method name to start walking the stack. The caller
     * function is expected to be 2 frames above this one's. Hence,
     * "unpostedTraceMessage" fills the same slot as "errorm" - something to be
     * skipped on the way to the true caller.
     */
    private TraceMessage recordTraceMessageXyzzY(String message, int level) {
        TraceMessage tm = new TraceMessage();
        tm.message = message;
        tm.date = new Date();
        tm.object = null;
        tm.level = level;
        tm.subsystem = "trace";
        return tm;
    }
}
