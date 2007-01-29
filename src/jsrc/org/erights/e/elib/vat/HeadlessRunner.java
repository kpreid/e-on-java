package org.erights.e.elib.vat;

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

import org.erights.e.develop.assertion.T;
import org.erights.e.develop.trace.Trace;
import org.erights.e.elib.ref.ViciousCycleException;
import org.erights.e.elib.tables.FlexSet;

/**
 * A redirectable Runner, mostly for internal use.
 *
 * @author Mark S. Miller
 * @author Many improvements due to suggestions by E-Dean Tribble
 */
final class HeadlessRunner extends Runner implements Runnable {

    /**
     * The number of Runnables to dequeue and run in one go. Must be >= 1.
     */
    static private final int DEQUEUE_GRANULARITY = 25;

    /**
     * The RunnerThread servicing this Runner's queue.
     * <p/>
     * If we ever go orthogonal again, myOptThread must not be checkpointed.
     * Ie, it must be a DISALLOWED_FIELD or 'transient' or something.
     */
    private transient RunnerThread myOptThread;

    /**
     * The Runner that this Runner has been redirected to, or null if this
     * Runner is fresh.
     */
    private Runner myOptTarget;

    /**
     * Where PendingEvents are to be enqueued, or null if I've been
     * redirected.
     * <p/>
     * Note that SynchQueue is a thread-safe data structure with its own lock
     * which we hold in myQLock.
     */
    private SynchQueue myOptQ;

    /**
     * myOptQ's internal lock.
     */
    private final Object myQLock;

    /**
     * The DeadManSwitches I need to inform if I shutdown.
     */
    private FlexSet myDeadManSwitches;

    /**
     * Makes a Vat, and starts the thread that services its queue.
     *
     * @param optName is the name to give to the created thread.
     */
    HeadlessRunner(String optName) {
        super();
        myOptThread = new RunnerThread(this, optName);
        myOptTarget = null;
        myQLock = new Object();
        myOptQ = new SynchQueue(PendingEvent.class, myQLock);
        //XXX should this instead make a weak-FlexSet?
        myDeadManSwitches = FlexSet.make();
        myOptThread.start();
    }

    /**
     *
     */
    public String toString() {
        return "<runs in " + myOptThread.getName() + ">";
    }

    /**
     * @return The best Runner to use in lieu of me
     */
    Runner shorten() {
        //XXX Does this need to be synchronized on myQLock?
        if (null == myOptTarget) {
            return this;
        } else {
            return myOptTarget.shorten();
        }
    }

    /**
     * @return "headless"
     */
    protected String getRunnerKind() {
        //XXX Does this need to be synchronized on myQLock?
        if (null == myOptTarget) {
            return "headless";
        } else {
            return shorten().getRunnerKind();
        }
    }

    /**
     *
     */
    protected Throwable enqueue(PendingEvent todo) {
        synchronized (myQLock) {
            if (null == myOptTarget) {
                myOptQ.enqueue(todo);
                return null;
            } else {
                return shorten().enqueue(todo);
            }
        }
    }

    /**
     *
     */
    protected void setPriority(int newPriority) {
        myOptThread.setPriority(newPriority);
    }

    /**
     *
     */
    protected void disturbEvent(Throwable t) {
        myOptThread.stop(t);
    }

    /**
     *
     */
    protected boolean isCurrent() {
        return Thread.currentThread() == myOptThread;
    }

    /**
     * Called only by {@link Thread#start()}.
     * <p/>
     * (XXX It's a modularity bug for this to be public.) Pulls PendingEvents
     * from the queue until there aren't any more, then waits until there's
     * more to do.
     */
    public void run() {
        while (true) {
            try {
                Thread.yield();
                SynchQueue q = myOptQ;
                if (null == q) {
                    return;
                }
                //within each chunk, wait if necessary for the first one.
                PendingEvent optTodo = (PendingEvent)q.dequeue();
                int i = DEQUEUE_GRANULARITY;
                do {
                    optTodo.run();
                    //Note that a shutdown event will both empty the q
                    //(escaping from the inner do-while) and set myOptQ to
                    //null (escaping from the outer while).
                    if (0 >= i) {
                        break;
                    }
                    i--;
                    optTodo = (PendingEvent)q.optDequeue();
                } while (null != optTodo);
            } catch (Throwable t) {
                if (Trace.causality.error) {
                    Trace.causality
                      .errorm("Exception made it all the way out of the run " +
                        "loop. Restarting it.", t);
                }
            }
        }
    }

    /**
     * If the enabling conditions are met, then requeue all my events onto
     * newRunner's queue, and remember to redirect all further requests to
     * newRunner.
     * <p/>
     * The enabling conditions are <ul> <li>This Runner isn't already
     * redirected. <li>newRunner isn't already redirected to this Runner. (If
     * it is, we throw a {@link ViciousCycleException}.) </ul> XXX Should we
     * instead have an operation that only requeues the events queued by a
     * given Vat, allowing Vats to migrate rather than merge?
     */
    Runner redirect(Runner newRunner) {
        synchronized (myQLock) {
            T.require(null == myOptTarget, "Already redirected: ", this);
            Runner result = newRunner.shorten();
            if (this == result) {
                throw new ViciousCycleException(
                  "Cyclic Runner merge not allowed: " + this);
            }
            //empty my queue into his
            SynchQueue q = myOptQ;
            myOptQ = null;
            myOptThread = null;
            myOptTarget = result;
            //in case myDeadManSwitches is weak, this holds onto the set
            //strongly while we're interating.
            Object[] deadManSwitches =
              (Object[])myDeadManSwitches.getElements();
            myDeadManSwitches = null;

            while (true) {
                PendingEvent optTodo = (PendingEvent)q.optDequeue();
                if (null == optTodo) {
                    break;
                }
                //XXX Deadlock danger -- grabbing a lock while holding a lock.
                //But should be safe since we've already ensured we're not
                //in a cycle.
                result.enqueue(optTodo);
            }
            for (int i = 0, len = deadManSwitches.length; i < len; i++) {
                result.addDeadManSwitch(deadManSwitches[i]);
            }
            return result;
        }
    }

    /**
     *
     */
    protected void addDeadManSwitch(Object deadManSwitch) {
        synchronized (myQLock) {
            myDeadManSwitches.addElement(deadManSwitch);
        }
    }
}
