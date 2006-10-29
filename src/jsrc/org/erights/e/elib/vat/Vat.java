package org.erights.e.elib.vat;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.develop.assertion.T;
import org.erights.e.develop.exception.ExceptionMgr;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.prim.Message;
import org.erights.e.elib.ref.Ref;
import org.erights.e.elib.ref.Resolver;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * A Vat is a disjoint partitioning of objects.
 * <p/>
 * Each object should ideally be associated with exactly one Vat, and should
 * only be invoked inside that Vat. However, since it would be too costly to
 * add a Vat field to all objects, rather, the object's Vat context is
 * restored prior to invoking the object. This context is captured on
 * enqueueing a message for the object (or enqueueing any other
 * {@link PendingEvent}). These events are enqueued onto the event loop of
 * a {@link Runner}, which manages the dequeueing. There can be multiple Vats
 * per Runner.
 * <p/>
 * When Vat <tt>x</tt> is {@link #mergeInto merged into} Vat <tt>y</tt>, this
 * means that both <tt>x</tt> and <tt>y</tt> will then be served by the Runner
 * that was serving <tt>y</tt>.
 *
 * @author Mark S. Miller
 * @author Many improvements due to suggestions by E-Dean Tribble
 */
public class Vat {

    /**
     * Should only be used by getRunner(), so that everyone will get the
     * <i>current</i> Runner.
     */
    private Runner myRunner;

    /**
     * Can I still merge?
     */
    private boolean myIsMergeable;

    /**
     * The ticket number to be "dispensed" to the next enqueued Runnable.
     * <p/>
     * Ie, the ticket number that will be being served when this next
     * Runnable will be run(). May be used for causality tracing.
     */
    private long myNextTicket;

    /**
     * For debugging.
     */
    private final String myOptName;

    /**
     *
     */
    private Vat(Runner runner, boolean mergeable, String optName) {
        myRunner = runner;
        myIsMergeable =
        mergeable && (myRunner instanceof HeadlessRunner);
        myNextTicket = 0;
        myOptName = optName;
    }

    /**
     * optName defaults to null
     * <p/>
     * May be called from any thead.
     *
     * @see #make(String, String)
     */
    static public Vat make(String runnerKind) {
        return make(runnerKind, null);
    }

    /**
     * Makes a Vat onto an
     * {@link Runner#obtainRunner(String, String) obtained} Runner of the
     * specified kind.
     * <p/>
     * May be called from any thead.
     *
     * @param runnerKind says which {@link Runner#getRunnerKind kind} of
     *                   Runner to make.
     * @param optName    If we are making a new Runner, the name is also used to
     *                   tag it and its thread for debugging purposes.
     */
    static public Vat make(String runnerKind, String optName) {
        return new Vat(Runner.obtainRunner(runnerKind, optName),
                       true,
                       optName);
    }

    /**
     * If called from within a thread servicing a Vat, returns that
     * Vat; otherwise null.
     * <p/>
     * May be called from any thead.
     */
    static public Vat getOptCurrentVat() {
        Runner optRunner = Runner.getOptCurrentRunner();
        if (null == optRunner) {
            return null;
        } else {
            return optRunner.getOptServingVat();
        }
    }

    /**
     * If called from within a thread servicing a Vat, returns that
     * Vat; otherwise, throws an exception.
     * <p/>
     * May be called from any thead.
     */
    static public Vat getCurrentVat() {
        Runner currentRunner = Runner.getCurrentRunner();
        Vat result = currentRunner.getOptServingVat();
        T.notNull(result, "No current Vat");
        return result;
    }

    /**
     * If the current thread isn't an
     * {@link Runner#getOptCurrentRunner() external thread}, throws an
     * exception.
     */
    static public void requireExternal() {
        Runner optRunner = Runner.getOptCurrentRunner();
        T.require(null == optRunner,
                  "Must be an external (non-Runner) thread: ",
                  optRunner);
    }

    /**
     * Queue the sendAllOnly in the current vat.
     */
    static public Throwable sendAllOnly(Object rec,
                                        String verb,
                                        Object[] args) {
        return Vat.getCurrentVat().qSendAllOnly(rec, true, verb, args);
    }

    /**
     * Queue the sendAll in the current vat.
     */
    static public Ref sendAll(Object rec, String verb, Object[] args) {
        return Vat.getCurrentVat().qSendAll(rec, true, verb, args);
    }

    /**
     * May be called from any thead.
     */
    public String toString() {
        if (null == myOptName) {
            return "<Vat in " + getRunner() + ">";
        } else {
            return "<Vat " + myOptName + " in " + getRunner() + ">";
        }
    }

    /**
     * This returns the current Runner for this Vat, but beware that this may
     * change over time as a result of {@link #mergeInto merging}.
     */
    Runner getRunner() {
        return myRunner = myRunner.shorten();
    }

    /**
     * Is the current event within this Vat?
     * <p/>
     * If it is, we say we are executing <i>inside</i> this Vat.
     * <p/>
     * <tt>v.isCurrent()</tt> implies
     * <tt>{@link Vat#getCurrentVat()} == v</tt>.
     * <p/>
     * May be called from any thead.
     */
    public boolean isCurrent() {
        Runner runner = getRunner();
        return runner.isCurrent() && this == runner.getOptServingVat();
    }

    /**
     * If not {@link #isCurrent()}, throw an exception.
     * <p/>
     * May be called from any thead.
     */
    public void requireCurrent() {
        T.require(isCurrent(),
                  "Must only be called from ", this, "'s thread");
    }

    /**
     * May be called from any thead.
     *
     * @see Runner#getRunnerKind()
     */
    public String getRunnerKind() {
        return getRunner().getRunnerKind();
    }

    /**
     * Requires {@link #getRunnerKind()} to be <tt>runnerKind</tt>
     */
    public void requireKind(String runnerKind) {
        String kind = getRunnerKind();
        T.require(runnerKind.equals(kind),
                  "Need ", runnerKind, " Runner, not ", kind);
    }

    /**
     * May be called from any thead.
     *
     * @see Runner#isCurrent()
     */
    public boolean isCurrentRunner() {
        return getRunner().isCurrent();
    }

    /**
     * May be called from any thead.
     *
     * @see Runner#setPriority(int)
     */
    public void setPriority(int newPriority) {
        getRunner().setPriority(newPriority);
    }

    /**
     * Dispenses a ticket number for the next enqueued Runnable.
     * <p/>
     * Ie, the ticket number that will be being served when this next
     * Runnable will be run(). May be used for causality tracing.
     * <p/>
     * Ticket counts are incremented separately per Vat rather than per
     * Runner, and so must be understood relative to the enqueueing
     * Vat.
     */
    long takeTicket() {
        return myNextTicket++;
    }

    /**
     *
     */
    public boolean isQuiescent() {
        requireCurrent();
        return 1L == (myNextTicket - myRunner.servingTicket());
    }

    /**
     * Seeds initial computation in a vat and communications between vats.
     * <p/>
     * Use for bootstrapping to boot-comm-system. This is an inherently
     * dangerous operation -- use only if you know what you're doing; do not
     * provide this ability directly to untrusted code. Most code should
     * instead use the emaker {@link org.erights.e.elang.interp.seedVatAuthor}.
     * <p/>
     * While treating <tt>rec</tt> <i>as if</i> it were a member of this vat,
     * seed will enqueue a
     * <pre>    rec &lt;- verb(args...)</pre>
     * to occur in this vat and return a promise for the result, just
     * as if <tt>rec</tt> were a proper boot-ref to an object already in this
     * vat, and the above message had simply been sent from the current vat on
     * this boot-ref. In particular, the treatment of the arguments and return
     * result is exactly according to this story. The only cheating is in
     * regards to <tt>rec</tt> itself. This is safe only when all the mutable
     * state transitively reachable from <tt>rec</tt> is no longer reachable
     * from any other vat (typically, not from the current vat -- the vat of
     * origin), or that any possibly shared mutable state is managed in a
     * conventionally thread-safe manner.
     * <p/>
     * This is typically used by having computation in the current vat create
     * a thunk (a non-argument function) that, when invoked, creates the
     * mutable state for a new service, and creates and returns the new
     * service in the scope of that mutable state. The caller of <tt>seed</tt>
     * in the current vat then has a boot-ref to the new service, which is
     * executing in this vat. For this specific pattern, the
     * {@link #seed(Object) seed/1} function is provided as a convenience.
     * <p/>
     * Once we have auditors working, then we may provide a safe form of these
     * operations that only accept DeepFrozen objects as <tt>rec</tt>.
     */
    public Ref seed(Object rec, String verb, Object[] args) {
        //Skip the normal check that rec is PassByProxy
        //Since these checks were skipped, 'handler' is not necessarily
        //valid, but it's fully encapsulated within this method's execution.
        BootRefHandler handler = new BootRefHandler(this, rec);
        return handler.handleSendAll(verb, args);
    }

    /**
     * The message defaults to '<tt> &lt;- run()</tt>'.
     *
     * @see #seed(Object, String, Object[])
     */
    public Ref seed(Object rec) {
        return seed(rec, "run", E.NO_ARGS);
    }

    /**
     * Enqueue's something for this Runnable's thread to do.
     * <p/>
     * May be called from any thead.
     *
     * @return Why wasn't 'todo' queued?  It isn't queued if this vat
     *         or comm connection is shut down, in which case the
     *         returned problem explains why. If null is returned, then the
     *         event was queued, though it may still not arrive.
     */
    public Throwable enqueue(Runnable todo) {
        return getRunner().enqueue(new PendingRun(this, todo));
    }

    /**
     * Enqueues the delivery of <tt>msg</tt> to <tt>rec</tt>.
     * <p/>
     * The sending context is the sending context captured in <tt>msg</tt>.
     * <p/>
     * May be called from any thead.
     * <p/>
     * XXX to be made non-public. Uses outside this package should use
     * {@link BootRefHandler boot-refs} instead.
     *
     * @return Why wasn't 'todo' queued?  It isn't queued if this vat
     *         or comm connection is shut down, in which case the
     *         returned problem explains why. If null is returned, then the
     *         event was queued, though it may still not arrive.
     */
    public Throwable qSendMsg(Object rec, Message msg) {
        PendingDelivery todo = new PendingDelivery(this, rec, false, msg);
        return getRunner().enqueue(todo);
    }

    /**
     * Enqueues a 'rec <- verb(args...)' when no conventional result is
     * needed.
     * <p/>
     * May be called from any thead.
     * <p/>
     * XXX to be made non-public. Uses outside this package should use
     * {@link BootRefHandler boot-refs} instead.
     *
     * @return Why wasn't this event queued?  It isn't queued if this
     *         vat or comm connection is shut down, in
     *         which case the returned problem explains why. If null is
     *         returned, then the event was queued, though it may still not
     *         arrive.
     */
    public Throwable qSendAllOnly(Object rec,
                                  boolean nowFlag,
                                  String verb,
                                  Object[] args) {
        PendingDelivery pe = new PendingDelivery(this,
                                                 rec,
                                                 null,
                                                 nowFlag,
                                                 verb,
                                                 args);
        return getRunner().enqueue(pe);
    }

    /**
     * Enqueues a 'rec <- verb(args...)' and returns a promise for the
     * result.
     * <p/>
     * If this vat is shut down, the returned reference is
     * <i>immediately</i> broken (by a complaint explaining why). Otherwise,
     * if it becomes known that this event might never be delivered, then
     * the reference eventually becomes broken with a complaint explaining
     * why.
     * <p/>
     * May be called from any thead.
     * <p/>
     * XXX to be made non-public. Uses outside this package should use
     * {@link BootRefHandler boot-refs} instead.
     */
    public Ref qSendAll(Object rec,
                        boolean nowFlag,
                        String verb,
                        Object[] args) {
        Object[] promise = Ref.promise();
        Resolver resolver = (Resolver)promise[1];
        PendingDelivery pe = new PendingDelivery(this,
                                                 rec,
                                                 resolver,
                                                 nowFlag,
                                                 verb,
                                                 args);
        Throwable optProblem = getRunner().enqueue(pe);
        if (null == optProblem) {
            return (Ref)promise[0];
        } else {
            return Ref.broken(optProblem);
        }
    }

    /**
     * Schedules a Runnable to execute in a Runner (in the Runner's thread as
     * a separate turn), while also effectively executing as a synchronous
     * call within the requestors's
     * {@link Runner#getOptCurrentRunner() external thread}.
     * <p/>
     * <i>Note: As of 0.8.20, we require that the caller's thread be external,
     * in order to avoid a deadlock danger created by {@link #mergeInto}. The
     * danger is that if we allow Runner x to block waiting on Runner y, then
     * if y is redirected to x (if y's Vat is mergedInto one of x's Vats),
     * then x would then be waiting on x. The old guard test to check if the
     * caller is in the same Runner as the callee doesn't help, since that
     * test says they are different prior to x blocking.</i>
     * <p/>
     * In most ways this can be thought of as a symmetric rendezvous between
     * the calling external thread and the callee Runner thread. The reason
     * we <i>specify</i> that the Runnable is executed specifically in the
     * callee's Vat (and its associated Runner and thread) is so that
     * thread-scoped state (such as {@link #getCurrentVat()} will be according
     * to the callee's Vat.
     * <p/>
     * If <tt>todo</tt> throws a problem rather than successfully returning,
     * then <tt>now()</tt> rethrows that problem as well.
     * <p/>
     * If this vat is shut down, or shuts down before todo is executed, then
     * a complaint about that is thrown and 'todo' is never executed.
     * <p/>
     * May be called from any
     * {@link Runner#getOptCurrentRunner() external thread}.
     */
    public void now(Runnable todo) {
        requireExternal();
        PendingCall nr = new PendingCall(this, todo);
        Throwable optProblem = getRunner().enqueue(nr);
        if (null != optProblem) {
            throw ExceptionMgr.asSafe(optProblem);
        }
        nr.runNow();
    }

    /**
     * Wraps <tt>method.invoke(obj, args)</tt> to ensure we're executing
     * using this Vat.
     * <p/>
     * Must be called from within this Runner, and either within this Vat, or
     * between PendingEvents of this Runner. In the latter case, this
     * invocation becomes like a PendingEvent, but with a ticket count of -1.
     *
     * @see #callAll(Object, String, Object[]) callAll/3
     */
    public Object invoke(Object obj, Method method, Object[] args)
      throws IllegalAccessException, InvocationTargetException {
        Runner runner = getRunner();
        runner.requireCurrent();
        Vat optOldVat = runner.getOptServingVat();
        if (null == optOldVat) {
            //We're between PendingEvents
            runner.myOptServingVat = this;
            runner.myServingTicket = -1; //just in case
        } else {
            T.require(this == optOldVat,
                      "Obj ", obj, " in vat ", this,
                      " shouldn't be invoked from vat ", optOldVat);
        }
        try {
            return method.invoke(obj, args);
        } finally {
            //either nulls it or leaves it alone
            runner.myOptServingVat = optOldVat;
            //leaves myServingTicket as -1 or what it was
        }
    }

    /**
     * Wraps <tt>E.callAll(rec, verb, args)</tt> to ensure we're executing
     * using this Vat.
     * <p/>
     * Must be called from within this Runner, and either within this Vat, or
     * between PendingEvents of this Runner. In the latter case, this
     * invocation becomes like a PendingEvent, but with a ticket count of -1.
     *
     * @see #invoke(Object, Method, Object[]) invoke/3
     */
    public Object callAll(Object rec, String verb, Object[] args) {
        Runner runner = getRunner();
        runner.requireCurrent();
        Vat optOldVat = runner.getOptServingVat();
        if (null == optOldVat) {
            //We're between PendingEvents
            runner.myOptServingVat = this;
            runner.myServingTicket = -1; //just in case
        } else {
            T.require(this == optOldVat,
                      "Obj ", rec, " in vat ", this,
                      " shouldn't be called from vat ", optOldVat);
        }
        try {
            return E.callAll(rec, verb, args);
        } finally {
            //either nulls it or leaves it alone
            runner.myOptServingVat = optOldVat;
        }
    }

    /**
     * Returns new Vat sharing my Runner.
     * <p/>
     * The new Vat isn't mergeable whether or not I am. This makes
     * <pre>
     *     def v2 := v1.sprout("foo")</pre>
     * like
     * <pre>
     *     def v2 := vatMaker.make("headless", "foo")
     *     v2.mergeInto(v1)</pre>
     * except that it's as if the merge happens immediately.
     * <p/>
     * May be called from any thead.
     */
    public Vat sprout(String optName) {
        return new Vat(getRunner(), false, optName);
    }

    /**
     * If this Vat can still merge, it should <i>eventually</i> merge
     * itself into <tt>other</tt>'s Runner.
     * <p/>
     * If the redirect attempt succeeds, it transfers the ability to redirect
     * this Vat further to the other Vat. Likewise, when this Vat is
     * redirected, all events queued by this Vat <i>and any Vat that has
     * merged into this Vat</i> are merged into the <tt>other</tt> Vat, since
     * these earlier Vats have transfered their merging power to this Vat.
     * <p/>
     * XXX It is unclear whether this is the right abstraction, or whether we
     * should instead provide a <i>migrate</i> operation that requeues just
     * the events queued by a given Vat. Currently, we choose mergeInto, since
     * it has a simpler implementation -- just migrate an entire
     * HeadlessRunner's queue and shut down its thread.
     * <p/>
     * May be called from any thead.
     *
     * @return When the redirect operation actually happens (in its own turn
     *         to avoid atomicity problems), the returned Ref resolves to
     *         'null' if the redirect attempt succeeds, or a broken reference
     *         if it fails.
     */
    public Ref mergeInto(Vat other) {
        Runnable todo = new VatRedirector(other.getRunner());
        return qSendAll(todo, true, "run", E.NO_ARGS);
    }

    /**
     * Requests this vat to <i>eventually</i> shut down once all already
     * queued events have been processed.
     * <p/>
     * A shutdown does a merge into a {@link DeadRunner}.
     * <p/>
     * May be called from any thead.
     *
     * @return As with {@link #mergeInto}
     */
    public Ref orderlyShutdown(Throwable problem) {
        Runnable todo = new VatRedirector(new DeadRunner(problem));
        return qSendAll(todo, true, "run", E.NO_ARGS);
    }

    /**
     * optName defaults to null
     * <p/>
     * May be called from any thead.
     *
     * @see #morphInto(String, String)
     */
    public Ref morphInto(String runnerKind) {
        return morphInto(runnerKind, null);
    }

    /**
     * Like {@link #mergeInto}, but merges into a kind of Runner rather than
     * the Runner of a pre-existing Vat.
     * <p/>
     * This is just an optimization, as
     * <pre>
     *     v1.morphInto("awt")</pre>
     * is equivalent to
     * <pre>
     *     def v2 := vatMaker.make("awt")
     *     v1.mergeInto(v2)</pre>
     * but without bothering to create v2.
     * <p/>
     * May be called from any thead.
     */
    public Ref morphInto(String runnerKind, String optName) {
        Runner runner = Runner.obtainRunner(runnerKind, optName);
        Runnable todo = new VatRedirector(runner);
        return qSendAll(todo, true, "run", E.NO_ARGS);
    }

    /**
     * Does the redirection scheduled by {@link #mergeInto},
     * {@link #morphInto}, and {@link #orderlyShutdown}.
     */
    private class VatRedirector implements Runnable {

        /**
         *
         */
        private final Runner myNewRunner;

        /**
         *
         */
        public VatRedirector(Runner newRunner) {
            myNewRunner = newRunner;
        }

        /**
         * This gets run eventually inside the Vat, and so doesn't need
         * to worry about synchronizing {@link #myIsMergeable}.
         */
        public void run() {
            Vat self = Vat.this;
            T.require(myIsMergeable,
                      "Not mergeable: ", self);
            HeadlessRunner runner = (HeadlessRunner)getRunner();
            myRunner = runner.redirect(myNewRunner);
            //This assumes that if Runner#redirect exits abruptly, then it
            //hasn't done anything. XXX This assumption is dangerous.
            myIsMergeable = false;
        }
    }
}
