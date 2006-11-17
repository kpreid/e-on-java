package org.erights.e.elib.vat;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.develop.exception.ExceptionMgr;
import org.erights.e.elib.oldeio.TextWriter;

import java.io.IOException;

/**
 * Used for {@link Vat#now(Runnable)}
 *
 * @author E-Dean Tribble
 * @author Mark S. Miller
 */
class PendingCall extends PendingEvent {

    /**
     * Acts as a condition variable on "null == myOptTodo"
     */
    private final Object myLock = new Object();

    /**
     * If non-null, it is the thunk to be executed. <p>
     * <p/>
     * Iff null, then we assume the thunk has been executed.
     */
    private Runnable myOptTodo;

    /**
     * If myOptTodo threw a problem, then this is the problem. <p>
     * <p/>
     * Meaningful iff null == myOptTodo. If myOptProblem is also null, then
     * myOptTodo completed successfully.
     */
    private Throwable myOptProblem;

    /**
     * Captures creation context as sending context
     */
    PendingCall(Vat vat, Runnable todo) {
        super("SCnow", vat);
        myOptTodo = todo;
        trace();
    }

    /**
     * Called in the thread doing the now() or callNow(). <p>
     * <p/>
     * Blocks until myOptTodo completes in the RunnerThread, at which point the
     * outcome of myOptTodo becomes the outcome of the runNow(), and therefore
     * the outcome of now().
     */
    void runNow() {
        synchronized (myLock) {
            while (null != myOptTodo) {
                try {
                    myLock.wait();
                } catch (InterruptedException ie) {
                    //ignore interrupt & continue waiting for condition
                }
            }
        }
        if (null != myOptProblem) {
            throw ExceptionMgr.asSafe(myOptProblem);
        }
    }

    /**
     * Called in the RunnerThread.
     */
    public void innerRun() {
        try {
            myOptTodo.run();
        } catch (Throwable problem) {
            //XXX should this catch all Throwables or only Exceptions?
            myOptProblem = problem;
            report("Problem in turn", problem);
        }
        myOptTodo = null;
        synchronized (myLock) {
            myLock.notifyAll();
        }
    }

    /**
     *
     */
    public void __printOn(TextWriter out) throws IOException {
        out.println();
        if (null != myOptTodo) {
            out.print("now: ", myOptTodo, ")");
        } else if (null != myOptProblem) {
            out.print("threw: ", myOptProblem);
        } else {
            out.print("done");
        }
        printContextOn(out.indent("--- "));
        out.println();
    }
}
