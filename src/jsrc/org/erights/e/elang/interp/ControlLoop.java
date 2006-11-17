package org.erights.e.elang.interp;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

/**
 * @author Mark S. Miller
 * @see "org.erights.e.elang.comtrolLoopMakerAuthor"
 */
public interface ControlLoop {

    /**
     * Called by an E program to stall the interpreter at the next top level
     * expression, until continueAtTop or exitAtTop is called.
     */
    void blockAtTop();

    /**
     * Allows an E interpreter that has been block(ed)AtTop to continue
     */
    void continueAtTop();

    /**
     * Blocks until ref is resolved, or until we are made to continue for other
     * reasons.
     */
    Object waitAtTop(Object ref);

    /**
     * optProblem defaults to null, indicating success.
     */
    void exitAtTop();

    /**
     * Causes the E interpreter to exit the next time it's between top-level
     * expression evaluations.
     * <p/>
     * If optProblem is null, then this is a successful (even if premature)
     * exit. Otherwise, it's an exceptional exit complaining of the problem.
     * For the main interpreter, a normal exit exits with exitCode 0. An
     * exceptional exit complains and exits with exitCode -1.
     * <p/>
     * If block(ed)AtTop, an exitAtTop will happen immediately, rather than
     * waiting for a continueAtTop.
     */
    void exitAtTop(Throwable optProblem);

    /**
     *
     */
    Object getNextExitStatus();

    /**
     * Returns a vow which will resolve to the loop's final exit status, once
     * it does exit.
     * <p/>
     * While this control loop is still running, the returned
     * finalExitStatusVow will remain unresolved. Once this loop has exited,
     * the vow will be resolved to true for success or a broken reference for
     * failure.
     */
    Object getFinalExitStatusVow();
}
