// Copyright 2004 Hewlett Packard, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.elib.vat;

/**
 * Each concrete RunnerMgr class should have a THE_ONE member (from E, a
 * getTHE_ONE() member) holding its instance, and should be registered in the
 * RUNNER_KINDS table in Runner.
 *
 * @author Mark S. Miller
 */
public interface RunnerMgr {

    /**
     * Obtains the a Runner of the kind managed by this RunnerMgr.
     * <p/>
     * optName, if provided, is the name of the vat that will be made in this
     * Runner.
     */
    Runner obtain(String optName);

    /**
     * If the current thread is not a RunnerThread, and does correspond to a
     * Runner managed by this RunnerMgr, return that Runner; else null.
     * <p/>
     * If the current thread is a RunnerThread, that case is handled
     * separately, and so need not be handled here.
     */
    Runner getOptCurrentRunner();
}
