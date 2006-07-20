package org.erights.e.elib.slot;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.develop.exception.ExceptionMgr;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.E;

import java.io.IOException;

/**
 * A ruined slot responds to both getValue() and setValue by throwing the
 * problem that explains why it's ruined.
 *
 * @author Mark S. Miller
 */
public class RuinedSlot implements Slot {

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
    public Object getValue() {
        throw ExceptionMgr.asSafe(myProblem);
    }

    /**
     *
     */
    public void setValue(Object newValue) {
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
     * 
     * @return
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

    /**
     * @return
     */
    public String toString() {
        return E.toString(this);
    }
}
