// Copyright 2004 Hewlett Packard, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.ui.swt;

import org.erights.e.elib.vat.Runner;
import org.erights.e.elib.vat.RunnerMgr;

/**
 * @author Mark S. Miller
 */
public class SWTRunnerMgr implements RunnerMgr {

    /**
     *
     */
    static public final RunnerMgr THE_ONE = new SWTRunnerMgr();

    /**
     *
     */
    private SWTRunnerMgr() {
    }

    /**
     * Assumes there's one SWTRunner, the default one, and returns it.
     */
    public Runner obtain(String optName) {
        return SWTRunner.getDefault();
    }

    /**
     * Since the RunnerThread case is already taken care of, this method just
     * returns null.
     */
    public Runner getOptCurrentRunner() {
        return null;
    }
}
