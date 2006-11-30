package org.erights.e.elib.slot;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.develop.exception.ExceptionMgr;
import org.erights.e.elib.oldeio.TextWriter;

import java.io.IOException;

/**
 * A ruined slot responds to both get/0 and put/1 by throwing the
 * problem that explains why it's ruined.
 *
 * @author Mark S. Miller
 */
public class RuinedSlot extends BaseSlot {

    private final Throwable myProblem;

    /**
     *
     */
    public RuinedSlot(Throwable problem) {
        myProblem = problem;
    }

    /**
     *
     */
    public Object get() {
        throw ExceptionMgr.asSafe(myProblem);
    }

    /**
     *
     */
    public void put(Object newValue) {
        throw ExceptionMgr.asSafe(myProblem);
    }

    /**
     * Return false
     */
    public boolean isFinal() {
        return false;
    }

    /**
     * A RuinedSlot is read-only, and so returns itself.
     */
    public Slot readOnly() {
        return this;
    }

    /**
     *
     */
    public void __printOn(TextWriter out) throws IOException {
        out.print("<& ruined by ", myProblem, ">");

    }
}
