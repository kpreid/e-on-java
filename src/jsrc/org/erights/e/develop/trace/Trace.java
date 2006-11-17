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
 *  Original version by Eric Messick.
 */


/**
 * This might better be called TraceCache, but the name is retained for
 * backward compatibility. The major purpose of this class is to hold a cache
 * of a relevant trace priority threshold, so that a user of the trace code can
 * quickly decide whether to call a trace method. Methods on this object
 * communicate with the master TraceController, which does the real work of
 * tracing.
 * <p/>
 * Secondarily, this class holds some miscellaneous functions useful in
 * tracing.
 */

final public class Trace implements TraceConstants {

    /**
     * Set this to false to compile out all tracing. This also has to be set in
     * TraceDummies.java
     */
    static public final boolean ON = true;

    /**
     * Error messages report on some internal error. They don't necessarily
     * lead to the system stopping, but they might. Error messages are always
     * logged.
     */
    public boolean error;

    /**
     * Warning messages are not as serious as errors, but they're signs of
     * something odd.
     */
    public boolean warning;

    /**
     * World messages track the state of the world as a whole.  They are the
     * sort of things world operators ask for specifically, such as "can you
     * tell me when someone connects."   They should appear only occasionally,
     * much less often than once per second. This is probably the level used
     * for the on-disk log in the shipped version.
     */
    public boolean world;

    /**
     * Usage messages are used to answer the question "who did what up to the
     * point the bug appeared?" ("Spock entered Azturf. Spock started trading
     * with Kyra. Kyra gave Spock a diamond in exchange for a lump of coal.
     * Kyra spoke.")  They are also used to collect higher-level usability
     * information. This is the level probably used for the transient log in
     * the shipped version; during development, we can set the on-disk log to
     * this level.
     */
    public boolean usage;

    /**
     * Event messages describe the major actions the system takes in response
     * to user actions. The distinction between this category and debug is
     * fuzzy, especially since debug is already used for many messages of this
     * type. However, it can be used to log specific user gestures for
     * usability testing, and to log information for testers.
     */
    public boolean event;

    /**
     * Debug messages provide more detail for people who want to delve into
     * what's going on, probably to figure out a bug.
     */
    public boolean debug;

    /**
     * Verbose messages provide even more detail than debug. They're probably
     * mainly used when first getting the code to work.
     */
    public boolean verbose;

    /**
     * Timing messages are for performance tuning. Whether timing messages are
     * logged is independent of the values of all the other trace variables.
     */
    public boolean timing;

    /**
     *  Predefined subsystems. (tr)
     */
    /**
     * comm systems
     */
    static public final Trace comm = new Trace("comm");

    static public final Trace captp = new Trace("captp");

    static public final Trace wire = new Trace("wire");

    static public final Trace tunnel = new Trace("tunnel");

    /**
     * Distributed garbage collector.
     */
    static public final Trace dgc = new Trace("dgc");

    static public final Trace pumpkin = new Trace("pumpkin");

    /**
     * The E runtime
     */
    static public final Trace eruntime = new Trace("eruntime");

    static public final Trace causality = new Trace("causality");

    /**
     * Classes involved in starting up the world.
     */
    static public final Trace startup = new Trace("startup");

    static public final Trace entropy = new Trace("entropy");

    static public final Trace timers = new Trace("timers");

    static public final Trace trace = new Trace("trace");

    //misc
    static public final Trace console = new Trace("console");


    /**
     * This is the mediator that actually disposes of a message. Multiple Trace
     * objects may share the same mediator.
     */

    TraceSubsystemMediator myMediator;

    /**
     * The subsystem is the group of classes this Trace object applies to. This
     * exists for encoding/decoding; it duplicates information available in the
     * Mediator.
     */

    private final String mySubsystem;

// DEPRECATED FUNCTIONS

    public void $(String message) {
        recordTraceMessageXyzzY(message, DEBUG, null, false);
    }
// Constructors

    /**
     * Initialize a Trace object for the given subsystem. It is legal for the
     * subsystem to contain blanks. Things will become confused if it contains
     * a colon, but the code does not check for that.
     */
    public Trace(String subsystem) {
        mySubsystem = subsystem;
        TraceController.newTrace(this, subsystem);
    }
// PUBLIC UTILITIES

    public void debugm(String message) {
        if (debug) {
            recordTraceMessageXyzzY(message, DEBUG, null, false);
        }
    }

    public void debugm(String message, Object o) {
        if (debug) {
            recordTraceMessageXyzzY(message, DEBUG, o, false);
        }
    }

    public void errorm(String message) {
        if (error) {
            recordTraceMessageXyzzY(message, ERROR, null, false);
        }
    }

    /*
     * Supplying a bug number suppresses the user dialog that a
     * trace error would otherwise call. The number is not examined;
     * use the other flavor of this call to display the user dialog.
     */
    public void errorm(String message, int bugNumber) {
        if (error) {
            recordTraceMessageXyzzY(message, ERROR, null, true);
        }
    }

    public void errorm(String message, Object o) {
        if (error) {
            recordTraceMessageXyzzY(message, ERROR, o, false);
        }
    }

    /*
     * Supplying a bug number suppresses the user dialog that a
     * trace error would otherwise call. The number is not examined;
     * use the other flavor of this call to display the user dialog.
     */
    public void errorm(String message, Object o, int bugNumber) {
        if (error) {
            recordTraceMessageXyzzY(message, ERROR, o, true);
        }
    }
// TRACING EXCEPTIONS

    public void eventm(String message) {
        if (event) {
            recordTraceMessageXyzzY(message, EVENT, null, false);
        }
    }

    public void eventm(String message, Object o) {
        if (event) {
            recordTraceMessageXyzzY(message, EVENT, o, false);
        }
    }

    /**
     * Exit reporting a fatal error.
     *
     * @param message The error message to die with
     */
    public void fatalError(String message) {
        errorm(message);
        notifyFatal();
        /* above should exit, but just in case... */
        System.exit(1);
    }

    // Not all errors are ones that should be thrust into a user's face.
    // The following two routines are used by calling code when it does
    // - or may - want to do that.

    /**
     * Notify a user that a fatal error has happened. Tell her how to report
     * the bug. Does not return.
     * <p/>
     * The work is done by the TraceController static object. This method is a
     * convenience for code that doesn't import TraceController.
     */
    public void notifyFatal() {
        TraceController.notifyFatal();
    }

    /**
     * If the user wants to hear about nonfatal bugs, notify her. Does return.
     * <p/>
     * The work is done by the TraceController static object. This method is a
     * convenience for code that doesn't import TraceController.
     */
    public void notifyOptional() {
        TraceController.notifyOptional();
    }

    /**
     * The peculiar name is because the code that finds the line number a trace
     * call was made from searches for this method. So it should have a name
     * unlikely to be accidentally duplicated.
     */
    private void recordTraceMessageXyzzY(String message,
                                         int level,
                                         Object o,
                                         boolean noNotify) {
        TraceMessage traceMessage = new TraceMessage();
        traceMessage.message = message;
        traceMessage.object = o;
        traceMessage.level = level;
        traceMessage.subsystem = mySubsystem;
        myMediator.accept(traceMessage);

        // MARKM: Uncomment this if you want the error behavior back.
        /*
        if (level == ERROR && !noNotify) {
            notifyOptional();
        }
        */
    }

    /**
     * It is no longer legal for anyone other than the TraceController to set
     * the trace mode.
     */
    public void setTraceMode(String mode) {
        warningm(
          "Deprecated function setTraceMode called." + "  It is now a no-op.");
    }

    /**
     * To ensure that exceptional conditions are only being ignored for good
     * reason, we adopt the discipline that a caught exception should <p>
     * <p/>
     * 1) be rethrown <p> 2) cause another exception to be thrown instead <p>
     * 3) be ignored, in a traceable way, for some stated reason <p>
     * <p/>
     * Only by making #3 explicit can we distinguish it from accidentally
     * ignoring the exception. An exception should, therefore, only be ignored
     * by asking a Trace object to shred it. This request carries a string that
     * justifies allowing the program to continue normally following this
     * event. As shredded exceptions will likely be symptoms of bugs, one will
     * be able to have them traced.
     * <p/>
     * The reason for the shredding is logged at verbose level.
     */
    public void shred(Throwable ex, String reason) {
        verbosem(reason, ex);
    }
// PUBLIC INTERFACE

    /**
     * Public interface methods that accept classes are the names of the
     * boolean fields, with 'm' appended (to denote 'method').
     * <p/>
     * Note: In java, the 'm' isn't required. However, in the past fields and
     * methods with the same names have broken ecomp. Currently, it works, but
     * danfuzz says it would be prudent to avoid the case.
     */

    public void timingm(String message) {
        if (timing) {
            recordTraceMessageXyzzY(message, TIMING, null, false);
        }
    }

    /**
     * These methods take an Object in addition to a string. Note that logging
     * a null object is the same thing as logging no object at all. (Fix this
     * if anyone complains.)
     */

    public void timingm(String message, Object o) {
        if (timing) {
            recordTraceMessageXyzzY(message, TIMING, o, false);
        }
    }

    /**
     * Invoking this method causes this class to be loaded, which causes all
     * the static trace objects to be defined. In particular, Trace.trace
     * becomes defined. That's convenient, in that it allows more tracing of
     * the tracing startup itself.
     */
    static void touch() {
    }

    public void usagem(String message) {
        if (usage) {
            recordTraceMessageXyzzY(message, USAGE, null, false);
        }
    }

    public void usagem(String message, Object o) {
        if (usage) {
            recordTraceMessageXyzzY(message, USAGE, o, false);
        }
    }

    public void verbosem(String message) {
        if (verbose) {
            recordTraceMessageXyzzY(message, VERBOSE, null, false);
        }
    }

    public void verbosem(String message, Object o) {
        if (verbose) {
            recordTraceMessageXyzzY(message, VERBOSE, o, false);
        }
    }

    public void warningm(String message) {
        if (warning) {
            recordTraceMessageXyzzY(message, WARNING, null, false);
        }
    }

    public void warningm(String message, Object o) {
        if (warning) {
            recordTraceMessageXyzzY(message, WARNING, o, false);
        }
    }

    public void worldm(String message) {
        if (world) {
            recordTraceMessageXyzzY(message, WORLD, null, false);
        }
    }

    public void worldm(String message, Object o) {
        if (world) {
            recordTraceMessageXyzzY(message, WORLD, o, false);
        }
    }
}
