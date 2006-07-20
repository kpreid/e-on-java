// Copyright 2004 Hewlett Packard, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.elib.vat;

/**
 * @author Mark S. Miller
 */
public class HeadlessRunnerMgr implements RunnerMgr {

    /**
     *
     */
    static public final RunnerMgr THE_ONE = new HeadlessRunnerMgr();

    /**
     *
     */
    private HeadlessRunnerMgr() {
    }

    /**
     * Makes and returns a new HeadlessRunner.
     */
    public Runner obtain(String optName) {
        if (null == optName) {
            optName = "A Headless Runner";
        }
        return new HeadlessRunner(optName);
    }

    /**
     * Since the RunnerThread case is already taken care of, this method just
     * returns null.
     */
    public Runner getOptCurrentRunner() {
        return null;
    }
}
