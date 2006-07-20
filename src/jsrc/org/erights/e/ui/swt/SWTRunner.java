package org.erights.e.ui.swt;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.eclipse.swt.internal.Library;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.SWT;
import org.erights.e.develop.assertion.T;
import org.erights.e.develop.trace.Trace;
import org.erights.e.elib.vat.PendingEvent;
import org.erights.e.elib.vat.Runner;
import org.erights.e.elib.vat.RunnerThread;

/**
 * A Runner executing in an SWT event loop.
 * <p/>
 * XXX the orderlyShutdown feature got lost in the reorganization.
 *
 * @author Mark S. Miller
 */
final class SWTRunner extends Runner implements Runnable {

    /**
     * Guards mutable static state, ie, THE_DEFAULT
     */
    static private final Object OUR_LOCK = new Object();

    /**
     *
     */
    static private SWTRunner THE_DEFAULT = null;

    /**
     * The RunnerThread servicing this Runner's queue.
     * <p/>
     * If we ever go orthogonal again, myThread must not be
     * checkpointed. Ie, it must be a DISALLOWED_FIELD or 'transient'
     * or something.
     */
    private final transient RunnerThread myThread;

    /**
     * Used to wait until the RunnerThread actually creates and initializes
     * myDisplay.
     */
    private final Object myLock = new Object();

    /**
     * This Display must be created by the new RunnerThread.
     */
    private transient Display myDisplay = null;

    /**
     * Makes an SWT Vat, and starts the thread that services its queue.
     * <p/>
     * The constructor doesn't return until myDisplay is initialized.
     *
     * @param optName is the name to give to the thread created.
     */
    private SWTRunner(String optName) {
        super();
        myThread = new RunnerThread(this, optName);
        myThread.start();
        synchronized (myLock) {
            while (null == myDisplay) {
                try {
                    myLock.wait();
                } catch (InterruptedException ie) {
                    //ignore
                }
            }
        }
    }

    /**
     * Returns the default SWTRunner -- the one managing the default
     * Display.
     * <p/>
     * The first time this is called, it will create the default
     * SWTRunner. The first thing the runner's new thread does when it's
     * scheduled is create a Display. If this is the first Display created
     * in this jvm, then it will be the SWT default Display, so you should
     * not create any Display objects before the first time this is called.
     */
    static Runner getDefault() {
        synchronized (OUR_LOCK) {
            if (null == THE_DEFAULT) {
                //On telnet, fail early. Once we can depend on Java 1.4 we can
                //   if (GraphicsEnvironment.isHeadless()) {...
                //but for now we
//                EventQueue.isDispatchThread();
                //and let it fail if we're headless.

                //XXX The above EventQueue.isDispatchThread(); is commented
                //out because it prevents SWT from working on Linux.
                //See {@link AWTRunner#isAWTCurrent}.

                //This should cause us to fail early if the SWT native library
                //is unavailable.
                Library.loadLibrary("swt");

                //Since the constructor won't return until myDisplay is
                //initialized, THE_DEFAULT can only get initialized to an
                //SWTRunner with a non-null myDisplay.
                //XXX Note that during this operation we will be holding two
                //locks: OUR_LOCK and myLock.
                THE_DEFAULT = new SWTRunner("Default SWT Vat");

                // XXX Happens too late to effect rune(["--version"])
                System.setProperty("e.swtVersion", ""+SWT.getVersion());
                // XXX doesn't work on gcj
//              System.setProperty("e.swtRevision", ""+Library.getRevision());
                System.setProperty("e.swtPlatform", SWT.getPlatform());
            }
            return THE_DEFAULT;
        }
    }

    /**
     * Makes an SWTRunner managing a non-default Display.
     * <p/>
     * This should only be called once there is a default Display (as a
     * result of {@link #getDefault()}). Non-default Displays are not
     * supported by SWT on all platforms.
     */
    static Runner make(String name) {
        T.notNull(THE_DEFAULT,
                  "The default SWTRunner must be created first");
        return new SWTRunner(name);
    }

    /**
     *
     */
    public String toString() {
        return "<SWTRunner>";
    }

    /**
     *
     */
    protected String getRunnerKind() {
        return "swt";
    }

    /**
     *
     */
    protected Throwable enqueue(PendingEvent todo) {
        T.notNull(myDisplay, "SWTRunner not initialized");
        myDisplay.asyncExec(todo);
        return null;
    }

    /**
     *
     */
    protected void setPriority(int newPriority) {
        myThread.setPriority(newPriority);
    }

    /**
     *
     */
    protected void disturbEvent(Throwable t) {
        myThread.stop(t);
    }

    /**
     *
     */
    protected boolean isCurrent() {
        return Thread.currentThread() == myThread;
    }

    /**
     * Called only by Thread.start().
     * <p/>
     * (XXX It's a modularity bug for this to be public.)
     */
    public void run() {
        synchronized (myLock) {
            myDisplay = new Display();
            myLock.notifyAll();
        }
        while (true) {
            try {
                if (!myDisplay.readAndDispatch()) {
                    myDisplay.sleep();
                }
            } catch (Throwable t) {
                if (Trace.causality.error) {
                    Trace.causality.errorm
                      ("Exception made it all the way out of the run " +
                       "loop. Restarting it.",
                       t);
                }
            }
        }
    }

    /**
     * AWT doesn't shut down or merge, so do nothing.
     */
    protected void addDeadManSwitch(Object deadManSwitch) {
    }
}
