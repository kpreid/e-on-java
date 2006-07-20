// Copyright 2004 Hewlett Packard, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.ui.awt;

import org.erights.e.develop.trace.Trace;
import org.erights.e.elib.vat.Runner;
import org.erights.e.elib.vat.RunnerMgr;

/**
 * @author Mark S. Miller
 */
public class AWTRunnerMgr implements RunnerMgr {

    /**
     *
     */
    static public final RunnerMgr THE_ONE = new AWTRunnerMgr();

    /**
     *
     */
    private AWTRunnerMgr() {
    }

    /**
     * Assumes there's one AWTRunner, the default one, and returns it.
     */
    public Runner obtain(String optName) {
        return AWTRunner.getDefault();
    }

    /**
     * @return
     */
    public Runner getOptCurrentRunner() {
        boolean isThisThread;
        try {
            isThisThread = AWTRunner.isAWTCurrent();
        } catch (Throwable th) {
            if (Trace.startup.debug && Trace.ON) {
                Trace.startup.debugm("ignored during AWT thread check", th);
            }
            return null;
        }
        if (isThisThread) {
            //XXX Assumes there's only one AWTRunner per JVM.
            return AWTRunner.getDefault();
        }
        return null;
    }
}
