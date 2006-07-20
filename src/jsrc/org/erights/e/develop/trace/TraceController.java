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

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

/**
 * The single trace controller manages all the trace classes. Most
 * notably, it has one TraceSubsystemMediator for each subsystem
 * being traced. A TraceSubsystemMediator may mediate several Trace
 * objects. Messages sent to those trace objects are forwarded to the
 * mediator. The mediator, under the control of the TraceController,
 * sends the message on to TraceMessageAcceptors that are ready to
 * accept messages. The two current TraceMessageAcceptors are the
 * TraceLog and the TraceBuffer. Once upon a time, there was a third,
 * the TraceDisplay, which would echo to the screen the contents of
 * the TraceBuffer. However, that proved impractical in the current
 * build environment.
 */
public class TraceController implements TraceConstants {

    /**
     * The list of TraceSubsystemMediators for the subsystems being traced.
     * Indexed by subsystem name.
     */
    static private final Hashtable OurTraceMediators = new Hashtable();

    /**
     * Trace thresholds that apply to subsystems that haven't
     * been given specific values. NUM_ACCEPTORS elements.
     */
    static private final int[] OurDefaultThresholds;

    /**
     * The on-disk log.
     */
    static private final TraceLog OurLog;

    /**
     * Set if stack parsing is to be debugged.
     */
    static boolean OurDebugTraceCaller = false;

    /**
     * A temporary window into the in-core trace buffer. Null if no
     * window.
     */
    // Commented out because the display can't be compiled this early.
    // It would be better to have a "registration" interface that would
    // let an independent GUI announce that it would like to see all
    // new trace messages sent to the buffer.
    // static private TraceDisplay myDisplay;

    /**
     * The in-core trace buffer.
     */
    static private final TraceBuffer OurBuffer;

    /**
     * Have we already been initialized?
     */
    static private boolean OurStarted = false;

    /**
     * The complete list of acceptors, for use by mediators. The fact
     * that there's a fixed set of acceptors that can't be changed
     * without recompiling is probably the major rigidity of the code.
     * <p/>
     * There are currently two acceptors:  on-disk logs and in-core
     * buffers. The in-core buffer has two "variants": the buffer
     * proper and a GUI window into the buffer.
     */
    static private final TraceMessageAcceptor[][] OurAcceptors;

    /**
     * This object, if non-null, is informed when notifyFatal or
     * notifyOptional is called. There is at most one such object,
     * because it may exit.
     */
    static private TraceErrorWatcher OurTraceErrorWatcher;

    /**
     * Synchronized static methods are prone to deadlocks in Sun's
     * JVM. This avoids the problem. Initialized in the class
     * initializer to make sure it's available before the first trace
     * object is used.
     */
    static private final Object OurLock;

    static {
        // Initialize component objects. Note that these constructors
        // must not call any trace functions, since the accepters and
        // thresholds haven't been set up. The component objects
        // can't be initialized after Trace, because trace makes use
        // of them.
        OurLock = new Object();
        OurLog = new TraceLog();
        // Commented out because the display can't be compiled this early.
        // myDisplay = new TraceDisplay();
        OurBuffer = new TraceBuffer();

        TraceMessageAcceptor[] logArray = new TraceMessageAcceptor[1];
        logArray[0] = OurLog;
        // Size is set to 1 because the display can't be compiled this
        // early.
        // TraceMessageAcceptor[] bufferArray = new TraceMessageAcceptor[2];
        TraceMessageAcceptor[] bufferArray = new TraceMessageAcceptor[1];
        bufferArray[0] = OurBuffer;
        // bufferArray[1] = myDisplay;

        OurAcceptors = new TraceMessageAcceptor[NUM_ACCEPTORS][];
        OurAcceptors[LOG] = logArray;
        OurAcceptors[TRACE] = bufferArray;

        OurDefaultThresholds = new int[NUM_ACCEPTORS];
        OurDefaultThresholds[TRACE] = STARTING_TRACE_THRESHOLD;
        OurDefaultThresholds[LOG] = STARTING_LOG_THRESHOLD;

        // Load Trace.class to define trace.trace(). Otherwise, it
        // only gets loaded when the first client refers to it. It's
        // convenient to load it as early as possible so that the
        // tracing subsystem's startup can itself be traced.
        // It cannot be loaded earlier than this point.
        Trace.touch();
    }

    /**
     * Request that the trace buffer be visible in a window.
     * XXX This cannot be made to work given the current layering
     * of the system / build environment. It should probably be
     * thrown out.
     */
    static public void changeDisplay(boolean showIt) {
        /*
        if (showIt) {
            Trace.trace.eventm("Request to display the trace buffer.");
            if (myDisplay.isAcceptingMessages()) {
                Trace.trace.usagem(
                    "Request to display the trace " +
                    "buffer, but it's already being displayed.");
            } else {
                myDisplay.pleaseDisplay(myBuffer.getFirstMessage());
            }
        } else {
            // Currently, this is a no-op. We could kill the window, but
            // it's much more convenient for the user to do that. (They'll
            // click rather than type.)
            Trace.trace.eventm("Request to turn off the trace buffer.");
        }
        */
    }

    /**
     * Change the default threshold for some TraceMessageAcceptor. The
     * change must propagate to all subsystems that track that
     * acceptor's default.
     */
    static private void changeOneDefault(int acceptorIndex, int newThreshold) {
        OurDefaultThresholds[acceptorIndex] = newThreshold;

        Trace.trace.eventm("The new default threshold for " +
                           acceptorNames[acceptorIndex] + " is " +
                           TraceLevelTranslator.terse(newThreshold));

        Enumeration e = OurTraceMediators.elements();
        while (e.hasMoreElements()) {
            TraceSubsystemMediator mediator =
              (TraceSubsystemMediator)e.nextElement();
            if (mediator.myDeferToDefaultThreshold[acceptorIndex]) {
                mediator.setOneThreshold(acceptorIndex, newThreshold,
                                         FROM_DEFAULT);
            }
        }
    }

    /**
     * Change a specific TraceMessageMediator to have its own
     * trace priority threshold OR change it to resume tracking
     * the default.
     */
    static private void changeOneSubsystem(int acceptorIndex,
                                           String subsystem,
                                           String value) {
        if (value.equalsIgnoreCase(DEFAULT_NAME)) {
            // set back to default threshold, whatever that is.
            findOrCreateMediator(subsystem).setOneThreshold(acceptorIndex,
                                                            OurDefaultThresholds[acceptorIndex],
                                                            FROM_DEFAULT);
        } else {
            // set to specific threshold.
            findOrCreateMediator(subsystem).setOneThreshold(acceptorIndex,
                                                            TraceLevelTranslator.toInt(
                                                              value),
                                                            FOR_SUBSYSTEM);
        }
    }

    /**
     * Take the contents of the internal trace buffer and
     * add them to the on-disk log.
     */
    static public void dumpBufferToLog() {
        OurBuffer.dump(OurLog);
    }

    /**
     * For debugging purposes, dump each java runtime stack as it's parsed.
     */
    static public void debugTraceCaller() {
        OurDebugTraceCaller = true;
    }

    /**
     * Register or unregister as a TraceErrorWatcher.
     * <p/>
     * This is kind of a stupid interface. I didn't think it through
     * before "publishing" it.
     *
     * @param aTraceErrorWatcher the object to be informed of an error.
     * @param add                true if the object is to be added, false if it's to
     *                           be removed. It's not an error to add without first removing, or
     *                           to remove without first adding, but it does provoke a warning.
     */
    static public void errorWatcher(TraceErrorWatcher aTraceErrorWatcher,
                                    boolean add) {
        if (add) {
            Trace.trace.usagem("Adding an object that watches for errors.");
            if (OurTraceErrorWatcher != null) {
                Trace.trace.warningm("Overriding previous TraceErrorWatcher",
                                     OurTraceErrorWatcher);
            }
            OurTraceErrorWatcher = aTraceErrorWatcher;
        } else {
            Trace.trace.usagem("No object will watch for errors.");
            if (OurTraceErrorWatcher == null) {
                Trace.trace.warningm(
                  "TraceErrorWatcher has already been removed");
            } else if (OurTraceErrorWatcher != aTraceErrorWatcher) {
                Trace.trace.warningm("A TraceErrorWatcher was removed by " +
                                     "someone other than itself.");
            }
            OurTraceErrorWatcher = null;
        }
    }

    /**
     * Find a TraceSubsystemMediator matching the given name. Create one if it
     * does not exist. The new mediator initially contains copies
     * of myDefaultThresholds and a pointer to myAcceptors.
     */
    static private TraceSubsystemMediator findOrCreateMediator(String name) {
        String key = name.toLowerCase();

        TraceSubsystemMediator mediator =
          (TraceSubsystemMediator)OurTraceMediators.get(key);
        if (mediator == null) {
            if (Trace.trace != null) {
                Trace.trace.debugm("Creating mediator for " + name);
            }
            mediator = new TraceSubsystemMediator(name,
                                                  OurDefaultThresholds,
                                                  OurAcceptors);
            OurTraceMediators.put(key, mediator);
        }
        return mediator;
    }

    /**
     * Invoke this to tell the TraceController that a display is
     * ready to use.
     */
    static public void mayUseUI() {
        // Commented out because the display can't be compiled this early.
        // myDisplay.setupIsComplete();
    }

    /**
     * This is called by a Trace constructor to inform the Trace
     * Controller that it exists. The end result of this call is that
     * a TraceSubsystemMediator exists for the Trace object's
     * subsystem, and the Trace object has been initialized with the
     * values it caches.
     */
    // May be unnecessary for this to be synchronized, but it's a rare
    // operation.
    static void newTrace(Trace requester, String subsystem) {
        synchronized (OurLock) {
            if (Trace.trace != null) {
                // guarded because this is called during class
                // initialization of Trace.
                Trace.trace.debugm("New trace for " + subsystem + " is " +
                                   requester);
            }
            TraceSubsystemMediator mediator = findOrCreateMediator(subsystem);
            mediator.newCache(requester);
            if (Trace.trace != null) {
                Trace.trace.debugm("New trace added to " + mediator);
            }
        }
    }
    // Not all errors are ones that should be thrust into a user's face.
    // The following two routines are used by calling code when it does
    // - or may - want to do that.

    /**
     * Notify a user that a fatal error has happened. Tell her how to
     * report the bug. Normally does not return.
     * <p/>
     * If this method is called before a TraceErrorWatcher has
     * registered, it's not clear what the best thing to do is. For
     * the moment, we log that as an error and return, in the hopes
     * that the system will hobble along a bit further to the point
     * where a watcher registers and a later notifyFatal causes the
     * user to be notified.
     */
    static public void notifyFatal() {
        if (OurTraceErrorWatcher != null) {
            OurTraceErrorWatcher.notifyFatal();
        } else {
            Trace.trace.errorm("A fatal error has been reported, " +
                               "but there is no one to report it to.", -1);
        }
    }

    /**
     * If the user wants to hear about nonfatal bugs, notify her.
     * Does return.
     * <p/>
     * It is a (non-fatal) error if no object has registered as a
     * TraceErrorWatcher.
     */
    static public void notifyOptional() {
        if (OurTraceErrorWatcher != null) {
            OurTraceErrorWatcher.notifyOptional();
        } else {
            Trace.trace.errorm("An optional error has been reported, " +
                               "but there is no one to report it to.", -1);
        }
    }

    /**
     * This method updates a trace controller from a given
     * set of properties.
     * <p/>
     * IMPORTANT:  The properties are processed in an unpredictable
     * order.
     */
    static public void setProperties(Properties p) {
        Enumeration keys = p.propertyNames();
        while (keys.hasMoreElements()) {
            String key = (String)keys.nextElement();
            String value = p.getProperty(key);
            // Note that the value cannot be null, but let's check.
            T.test(value != null);

            setProperty(key, value);
        }
    }

    /**
     * If the given Key names a tracing property, process its value.
     * It is not an error for the key to have nothing to do with
     * tracing; in that case, it's ignored.
     * <p/>
     * It is an error for the value to be null.
     */
    static public void setProperty(String key, String value) {
        // Note: synchronization is the responsibility of the objects
        // whose properties are being changed.
        T.test(value != null, "Trace property value cannot be null.");
        key = key.trim();
        value = value.trim();
        Trace.trace.debugm("Setting property " + key + " to value " + value);
        try {
            boolean bufferProp = false;  // Setting a TraceBuffer property.
            boolean logProp = false;     // Setting a TraceLog property.
            String originalKey = key;
            String lowerKey = key.toLowerCase();
            String afterUnderbar = null;  // e.g., What comes after TraceLog_

            if (lowerKey.startsWith("tracebuffer_")) {
                bufferProp = true;
                afterUnderbar = originalKey.substring(12);
            } else if (lowerKey.startsWith("trace_")) {
                // This is for backwards compatibility.
                // XXX eventually will set bufferProp or be removed entirely.
                logProp = true;
                afterUnderbar = originalKey.substring(6);
            } else if (lowerKey.startsWith("tracelog_")) {
                logProp = true;
                afterUnderbar = originalKey.substring(9);
            }

            if (bufferProp) {
                if (afterUnderbar.equalsIgnoreCase(DEFAULT_NAME)) {
                    changeOneDefault(TRACE, TraceLevelTranslator.toInt(value));
                } else if (timingProperty(afterUnderbar)) {
                    setTiming(TRACE, afterUnderbar, value);
                } else if (afterUnderbar.equalsIgnoreCase("size")) {
                    OurBuffer.changeSize(value);
                } else if (afterUnderbar.equalsIgnoreCase("display")) {
                    changeDisplay(value.equalsIgnoreCase("true"));
                } else if (afterUnderbar.equalsIgnoreCase("dump")) {
                    OurBuffer.dump(value);
                } else {
                    changeOneSubsystem(TRACE, afterUnderbar, value);
                }
            } else if (logProp) {
                if (afterUnderbar.equalsIgnoreCase(DEFAULT_NAME)) {
                    changeOneDefault(LOG, TraceLevelTranslator.toInt(value));
                } else if (timingProperty(afterUnderbar)) {
                    setTiming(LOG, afterUnderbar, value);
                } else if (afterUnderbar.equalsIgnoreCase("write")) {
                    OurLog.changeWrite(value);
                } else if (afterUnderbar.equalsIgnoreCase("dir")) {
                    OurLog.changeDir(value);
                } else if (afterUnderbar.equalsIgnoreCase("tag")) {
                    OurLog.changeTag(value);
                } else if (afterUnderbar.equalsIgnoreCase("name")) {
                    OurLog.changeName(value);
                } else if (afterUnderbar.equalsIgnoreCase("size")) {
                    OurLog.changeSize(value);
                } else if (afterUnderbar.equalsIgnoreCase("backups")) {
                    OurLog.changeBackupFileHandling(value);
                } else if (afterUnderbar.equalsIgnoreCase("reopen")) {
                    OurLog.reopen(value);
                } else {
                    changeOneSubsystem(LOG, afterUnderbar, value);
                }
            }
        } catch (IllegalArgumentException e) {
            Trace.trace.shred(e, "The exception has already been logged.");
        }

        // Other properties are ignored, because this method may be
        // called from setProperties, which is given a whole mess of
        // properties, some irrelevant to Trace.
    }

    /**
     * Set the timing value of a given subsystem's acceptor.
     *
     * @param acceptorIndex      the acceptor.
     * @param afterFirstUnderbar is of the form <subsystem>_timing,
     *                           else an assertion fails.
     * @param value              is "on", "off", "true", or "false".
     */
    static private void setTiming(int acceptorIndex,
                                  String afterFirstUnderbar,
                                  String value) {
        int underbar = afterFirstUnderbar.lastIndexOf('_');
        T.test(underbar != -1);
        String subsystem = afterFirstUnderbar.substring(0, underbar);
        if (value.equalsIgnoreCase("on") ||
          value.equalsIgnoreCase("true")) {
            findOrCreateMediator(subsystem).setTiming(acceptorIndex, true);
        } else if (value.equalsIgnoreCase("off") ||
          value.equalsIgnoreCase("false")) {
            findOrCreateMediator(subsystem).setTiming(acceptorIndex, false);
        } else {
            Trace.trace.warningm("Unknown timing value given: " + value);
        }
    }

    /**
     * This routine is used to start the Trace Controller. Prior to
     * this call, Trace objects may be obtained and messages may be
     * sent to them. However, the messages will be queued up until
     * this routine is called. (Note that the messages will be
     * governed by the default thresholds.)
     * <p/>
     * Note: This routine may be called before the user interface is
     * available. This allows on-disk logging to be useful in the
     * case the system fails before the UI is initialized.
     *
     * @param p the initial set of properties provided by the user.
     *          They override the defaults. They may be changed later.
     */
    static public void start(Properties p) {
        if (OurStarted) {
            //Used to be errorm. I (MarkM) changed it to usagem.
            Trace.trace.usagem("The tracing system is being started for the second time.\n" +
                               "Ignoring the second start.");
            return;
        }

        OurStarted = true;
        Trace.trace.usagem("Tracing system being started.");

        // XXX - temporary backwards compatibility.
        if (!p.containsKey("TraceLog_write")) {
            p.put("TraceLog_write", "true");
        }

        if (!p.containsKey("TraceLog_name") &&
          !p.containsKey("TraceLog_dir") &&
          !p.containsKey("TraceLog_tag")) {
            
            //Trace.trace.usagem("TEMPORARY: TraceLog set to standard " +
            //                   "error for backwards compatibility.");
            p.put("TraceLog_name", "-");
        }

        setProperties(p);
        OurLog.setupIsComplete();
        OurBuffer.setupIsComplete();
        // Setup is not complete yet for the TraceDisplay.
        new TraceExceptionNoticer();
    }

    /**
     * Use this constructor when you have no launch-time properties.
     */
    static public void start() {
        start(new Properties());
    }

    /**
     * Is this a timing control statement like "TraceBuffer_ui_timing=true"?
     *
     * @param afterFirstUnderbar text after first underbar.
     */
    static private boolean timingProperty(String afterFirstUnderbar) {
        int underbar = afterFirstUnderbar.lastIndexOf('_');
        if (underbar == -1) {
            return false;
        }
        String tail = afterFirstUnderbar.substring(underbar + 1);
        return tail.equalsIgnoreCase("timing");
    }
}
