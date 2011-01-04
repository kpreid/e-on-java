package org.erights.e.elib.vat;

import org.erights.e.develop.assertion.T;
import org.erights.e.develop.exception.ExceptionMgr;
import org.erights.e.develop.trace.Trace;
import org.erights.e.elib.base.SourceSpan;
import org.erights.e.elib.debug.EStackItem;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.prim.StaticMaker;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.FlexList;
import org.erights.e.elib.util.ClassCache;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.Hashtable;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

/**
 * Runs when it can, but never on empty.
 * <p/>
 * The dequeueing end of a {@link Vat} -- an event loop & thread servicing a
 * queue of {@link PendingEvent}s.
 *
 * @author Mark S. Miller
 */
public abstract class Runner {

    /**
     * Maps from runnerKinds to fqns of RunnerMgrs.
     */
    static private final String[][] RUNNER_KINDS = {{"headless",
      "org.erights.e.elib.vat.HeadlessRunnerMgr"},
      {"awt", "org.erights.e.ui.awt.AWTRunnerMgr"},
      {"swt", "org.erights.e.ui.swt.SWTRunnerMgr"},
      {"dead", "org.erights.e.elib.vat.DeadRunnerMgr"}};

    /**
     * Maps from runnerKinds to RunnerMgrs.
     * <p/>
     * XXX Security Alert: Static mutable state.
     */
    static private final Hashtable TheRunnerMgrs = new Hashtable();

    /**
     * @return
     */
    static private RunnerMgr getRunnerMgr(String runnerKind) {
        RunnerMgr optResult = (RunnerMgr)TheRunnerMgrs.get(runnerKind);
        if (null != optResult) {
            return optResult;
        }
        for (int i = 0, len = RUNNER_KINDS.length; i < len; i++) {
            if (RUNNER_KINDS[i][0].equals(runnerKind)) {
                String fqn = RUNNER_KINDS[i][1];
                Class mgrClass;
                try {
                    mgrClass = ClassCache.forName(fqn);
                } catch (ClassNotFoundException e) {
                    throw ExceptionMgr.asSafe(e);
                }
                StaticMaker mgrMaker = StaticMaker.make(mgrClass);
                optResult = (RunnerMgr)E.call(mgrMaker, "getTHE_ONE");
                TheRunnerMgrs.put(runnerKind, optResult);
                return optResult;
            }
        }
        T.fail("Unrecognized runnerKind: " + runnerKind);
        return null; // make the compiler happy
    }

    /**
     * Turning this off and recompiling results in approximately a 10%
     * speedup.
     * <p/>
     * Turning this on allows the reporting of causality trace info, as needed
     * by the upcoming Causeway tool.
     */
    static private final boolean PROFILE_ON = true;

    /**
     * To be set by {@link PendingEvent} to the Vat that enqueued the event
     * currently being served, or null if idle.
     * <p/>
     * XXX Security Alert: Mutable per-vat state. Access to this must be
     * properly tamed away.
     */
    Vat myOptServingVat;

    /**
     * To be set by {@link PendingEvent} to the ticket taken from
     * myOptServingVat by the event currently being served at the time that
     * event was enqueued, or -1 if idle.
     * <p/>
     * XXX Security Alert: Mutable per-vat state. Access to this must be
     * properly tamed away.
     */
    long myServingTicket;

    /**
     * A stack of {@link EStackItem}s used for causality tracing.
     * <p/>
     * XXX Security Alert: Mutable per-vat state. Access to this must be
     * properly tamed away.
     */
    private final FlexList myEStack = FlexList.fromType(EStackItem.class);

    /**
     * A flag used to trace causality leaves.
     * <p/>
     * It remembers whether the current event has spawned any traced children.
     * If not, then, when it's about to pop its last StackItem, it traces it
     * first.
     * <p/>
     * Only used when PROFILE_ON is true.
     * <p/>
     * XXX Security Alert: Mutable per-vat state. Access to this must be
     * properly tamed away.
     */
    private boolean myHasSpawned = false;

    /**
     *
     */
    protected Runner() {
        myOptServingVat = null;
        myServingTicket = -1;
    }

    /**
     * If called from within a thread servicing a Runner, returns that Runner;
     * otherwise it's an external thread and we return null.
     * <p/>
     * There are two kinds of threads which we take to be servicing a Runner.
     * <ul> <li>A {@link RunnerThread} created to service a Runner, currently
     * used for the "headless" and "swt" runnerKinds. <li>A pre-existing thread
     * servicing a pre-existing event-queue mechanism, managing some device or
     * system of devices that one might want to synchronously access from E, or
     * (via callbacks) have synchronous access to E. This is currently used for
     * the "awt" runnerKind. </ul> Let's call a thread not servicing a Runner
     * an <i>external thread</i>, since it is external in some sense to E.
     * (E-language code and normal ELib code should never execute in an
     * external thread.) <tt>getOptCurrentRunner() == null</tt> iff it's
     * invoked from an external thread.
     */
    static Runner getOptCurrentRunner() {
        Thread t = Thread.currentThread();
        if (t instanceof RunnerThread) {
            //Takes care of both the headless and the swt cases
            return ((RunnerThread)t).myRunner;
        }
        for (Enumeration iter = TheRunnerMgrs.elements();
             iter.hasMoreElements();) {
            RunnerMgr runnerMgr = (RunnerMgr)iter.nextElement();
            Runner optResult = runnerMgr.getOptCurrentRunner();
            if (null != optResult) {
                return optResult;
            }
        }
        return null;
    }

    /**
     * If called from within a thread servicing a Runner, returns that Runner;
     * otherwise it's an external thread and we throw an exception.
     */
    static Runner getCurrentRunner() {
        Runner result = getOptCurrentRunner();
        T.notNull(result, "Must be called from a Runner's thread");
        return result;
    }

    /**
     * optName defaults to null
     *
     * @see #obtainRunner(String,String)
     */
    static Runner obtainRunner(String runnerKind) {
        return obtainRunner(runnerKind, null);
    }

    /**
     * Gets or makes a Runner of the specified kind.
     * <p/>
     *
     * @param runnerKind says which {@link #getRunnerKind kind} of Runner to
     *                   make.
     * @param optName    If we are making a new Runner, the name is used to tag
     *                   it and its thread for debugging purposes.
     */
    static Runner obtainRunner(String runnerKind, String optName) {
        RunnerMgr runnerMgr = getRunnerMgr(runnerKind);
        return runnerMgr.obtain(optName);
//        if ("headless".equals(runnerKind)) {
//            if (null == optName) {
//                optName = "A Headless Runner";
//            }
//            return new HeadlessRunner(optName);
//
//        } else if ("awt".equals(runnerKind)) {
//            return AWTRunner.getDefault();
//
//        } else if ("swt".equals(runnerKind)) {
//            return SWTRunner.getDefault();
//
//        } else if ("dead".equals(runnerKind)) {
//            if (null == optName) {
//                optName = "A Dead Runner";
//            }
//            return new DeadRunner(new RuntimeException(optName));
//
//        } else {
//            T.fail("unrecognized runner kind: " + runnerKind);
//            return null; //make compiler happy
//        }
    }

    /**
     * XXX Should be suppressed by taming.
     */
    static public void pushEStackItem(EStackItem item) {
        if (PROFILE_ON) {
            Runner optCurrent = getOptCurrentRunner();
            if (null != optCurrent) {
                if (0 == optCurrent.myEStack.size()) {
                    // A new event
                    optCurrent.myHasSpawned = false;
                }
                optCurrent.myEStack.push(item);
            }
        }
    }

    /**
     * XXX Should be suppressed by taming.
     */
    static public void popEStackItem() {
        if (PROFILE_ON) {
            Runner optCurrent = getOptCurrentRunner();
            if (null != optCurrent) {
                EStackItem leaf = (EStackItem)optCurrent.myEStack.pop();
                if (Trace.causality.debug && Trace.ON &&
                  !optCurrent.myHasSpawned &&
                  0 == optCurrent.myEStack.size()) {

                    SourceSpan optSpan = leaf.getOptSpan();
                    if (null == optSpan) {
                        return;
                    }

                    //Hasn't been traced, so do it now.
                    StringWriter strWriter = new StringWriter();
                    TextWriter out = new TextWriter(strWriter).indent("--- ");
                    Vat vat = Vat.getCurrentVat();
                    try {
                        out.println();
                        out.print("leaf: ",
                                  vat,
                                  ":" + optCurrent.servingTicket());
                        out.lnPrint(" @ ");
                        leaf.traceOn(out);
                        out.print(": ", optSpan);

                    } catch (IOException e) {
                        throw ExceptionMgr.asSafe(e);
                    }
                    String str = strWriter.getBuffer().toString();
                    Trace.causality.debugm("", str + "\n");
                }
            }
        }
    }

    /**
     * Returns a list of {@link EStackItem}s, and sets myHasSpawned.
     */
    static ConstList getOptEStackForTracing() {
        Runner optCurrent = getOptCurrentRunner();
        if (null == optCurrent) {
            return null;
        } else {
            optCurrent.myHasSpawned = true;
            return optCurrent.myEStack.snapshot();
        }
    }

    static public void whenDead(Object deadManSwitch) {
        getCurrentRunner().addDeadManSwitch(deadManSwitch);
    }

    /**
     * If x.shorten() != x, then this Runner is no more, and should not be
     * used. This can happen only to {@link HeadlessRunner}s.
     *
     * @return The end of the merge chain. The default implementation here in
     *         Runner just returns <tt>this</tt>.
     */
    Runner shorten() {
        return this;
    }

    /**
     * Add todo to the queue my thread is servicing
     */
    protected abstract Throwable enqueue(PendingEvent todo);

    /**
     * Requests a change of priority of the thread servicing this Runner.
     */
    protected abstract void setPriority(int newPriority);

    /**
     * Performs a Thread.stop(t) on the thread executing the current event.
     * <p/>
     * Note that Thread.stop() does not stop the thread (obvious huh?), but
     * rather causes that thread to experience a "spontaneously" thrown
     * exception.
     *
     * @deprecated Since {@link Thread#stop(Throwable)} is also deprecated, but
     *             will be available as long as Thread.stop(Throwable) remains
     *             available.
     */
    protected abstract void disturbEvent(Throwable t);

    /**
     * The Vat that queued the event now being run(), or null if idle.
     * <p/>
     * Used for causality tracing.
     *
     * @see #servingTicket()
     */
    Vat getOptServingVat() {
        return myOptServingVat;
    }

    /**
     * The ticket number of the event currently being run(), or -1 if idle.
     * <p/>
     * Used for causality tracing. The serving ticket count was dispensed by
     * one of my enqueueing Vats and can only be understood relative to that
     * Vat. Use {@link #getOptServingVat()} to get that Vat.
     */
    long servingTicket() {
        return myServingTicket;
    }

    /**
     * What kind of Runner is this?
     * <p/>
     * A kind of Runner determines which kind of "devices" (eg, AWT or SWT
     * widgets) may be synchronously accessed from within this Runner
     *
     * @return one of "awt", "swt", "dead", or "headless". If "headless", this
     *         makes an HeadlessRunner, which may later be redirected, so use
     *         with caution. This list may be extended over time.
     */
    protected abstract String getRunnerKind();

    /**
     * Is the current thread this Runner's thread (the thread servicing this
     * Vat)?
     * <p/>
     * If it is, we say we are executing <i>inside</i> this Runner.
     * <p/>
     * <tt>r.isCurrent()</tt> implies <tt>{@link Runner#getCurrentRunner()} ==
     * r</tt>.
     */
    protected abstract boolean isCurrent();

    /**
     * If not {@link #isCurrent()}, throw an exception
     */
    void requireCurrent() {
        T.require(isCurrent(), "Must only be called from ", this, "'s thread");
    }

    /**
     * Remember the deadManSwitch, so that if I'm shut down, I can notify him.
     * <p/>
     * The deadManSwitch is only notified if it's be a boot-ref (a Ref handled
     * by a {@link BootRefHandler}) whose target's vat is a vat handled by a
     * different Runner. Otherwise, the notification would need to occur in
     * this Runner, which is presumably already shut down.
     */
    protected abstract void addDeadManSwitch(Object deadManSwitch);
}
