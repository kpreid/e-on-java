package org.erights.e.ui.awt;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.develop.exception.ExceptionMgr;
import org.erights.e.elib.util.ClassCache;
import org.erights.e.elib.vat.PendingEvent;
import org.erights.e.elib.vat.Runner;

import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import java.awt.EventQueue;
import java.awt.Toolkit;

/**
 * Uses the AWT Event Thread as a virtual RunnerThread, so that AWT/Swing
 * widgets can be invoked synchronously from this Runner.
 * <p/>
 * XXX Dean says it's possible to create multiple AWT event threads. If we ever
 * decide to support that, this Runner should be largely rewritten.
 *
 * @author Mark S. Miller
 * @author With crucial help from Dean Tribble.
 */
final class AWTRunner extends Runner {

    /**
     *
     */
    static private final Object OUR_LOCK = new Object();

    /**
     *
     */
    static private AWTRunner THE_DEFAULT = null;

    /**
     *
     */
    private AWTRunner() {
        super();
    }

    /**
     * I can't believe they didn't make platform l&f the default.
     */
    static private void initSwingLnF() {

        try {
            String lf = System.getProperty("e.swingLnF", "platform");
            if ("none".equals(lf)) {
                return;
            }
            if ("platform".equals(lf)) {
                lf = UIManager.getSystemLookAndFeelClassName();
            }
            if (null != lf) {
                Class lfc = ClassCache.forName(lf);
                LookAndFeel lfo = (LookAndFeel)lfc.newInstance();
                UIManager.setLookAndFeel(lfo);
                Object lfo2 = UIManager.getLookAndFeel();
                if (lfo != lfo2) {
                    System.err.println("Oops, L&F is still " + lfo2);
                }
            }
        } catch (ClassNotFoundException cnfe) {
            throw ExceptionMgr.asSafe(cnfe);
        } catch (InstantiationException ie) {
            throw ExceptionMgr.asSafe(ie);
        } catch (IllegalAccessException iae) {
            throw ExceptionMgr.asSafe(iae);
        } catch (UnsupportedLookAndFeelException ulafe) {
            throw ExceptionMgr.asSafe(ulafe);
        }
    }

    /**
     * Gets the one canonical AWTRunner.
     * <p/>
     * The Runner is created the first time this is called. At that time, the
     * look-n-feel is set according to the System properties.
     */
    static AWTRunner getDefault() {
        synchronized (OUR_LOCK) {
            if (null == THE_DEFAULT) {
                //On telnet, fail early. Once we can depend on Java 1.4 we can
                //   if (GraphicsEnvironment.isHeadless()) {...
                //but for now we
                EventQueue.isDispatchThread();
                //and let it fail if we're headless.

                initSwingLnF(); //first time only
                THE_DEFAULT = new AWTRunner();
            }
            return THE_DEFAULT;
        }
    }

    /**
     * Like <tt>AWTRunner.getDefault().isCurrent()</tt> except that it avoids
     * doing a <tt>EventQueue.isDispatchThread()</tt> (or any other AWT
     * operation) if no AWTRunner has yet been created.
     * <p/>
     * Believe it or not, this is the key to getting SWT working on Linux. On
     * Linux, once an AWT operation has been invoked (or, at least, this one),
     * then SWT no longer seems to be able to work. This seems to correspond to
     * the SWT documentation.
     */
    static boolean isAWTCurrent() {
        if (null == THE_DEFAULT) {
            return false;
        } else {
            return EventQueue.isDispatchThread();
        }
    }

    /**
     *
     */
    protected boolean isCurrent() {
        return EventQueue.isDispatchThread();
    }

    /**
     *
     */
    public String toString() {
        return "<AWTRunner>";
    }

    /**
     * XXX Doesn't do anything, since I can't figure out how to get my hands on
     * {@link EventQueue#getDispatchThread()}, which isn't public.
     * <p/>
     * Note that this is about the thread's priority, and not the AWT notion of
     * priority among events within a thread.
     */
    protected void setPriority(int newPriority) {
    }

    /**
     * XXX Doesn't do anything, since I can't figure out how to get my hands on
     * {@link EventQueue#getDispatchThread()}, which isn't public.
     */
    protected void disturbEvent(Throwable t) {
    }

    /**
     * @return <tt>"awt"</tt>
     */
    protected String getRunnerKind() {
        return "awt";
    }

    /**
     *
     */
    protected Throwable enqueue(PendingEvent todo) {
        //XXX would it be safe to cache this?
        EventQueue q = Toolkit.getDefaultToolkit().getSystemEventQueue();
        AWTRunnerEvent event = new AWTRunnerEvent(todo, this);
        q.postEvent(event);
        return null; //Since the AWTRunner never shuts down.
    }

    /**
     * AWT doesn't shut down or merge, so do nothing.
     */
    protected void addDeadManSwitch(Object deadManSwitch) {
    }
}
