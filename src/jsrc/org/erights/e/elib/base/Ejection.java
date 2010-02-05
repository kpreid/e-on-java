package org.erights.e.elib.base;

import org.erights.e.develop.exception.EBacktraceException;
import org.erights.e.develop.exception.ThrowableSugar;

/*
The contents of this file are subject to the Electric Communities E Open
Source Code License Version 1.0 (the "License"); you may not use this file
except in compliance with the License. You may obtain a copy of the License
at http://www.communities.com/EL/.

Software distributed under the License is distributed on an "AS IS" basis,
WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
the specific language governing rights and limitations under the License.

The Original Code is the Distributed E Language Implementation, released
July 20, 1998.

The Initial Developer of the Original Code is Electric Communities.
Copyright (C) 1998 Electric Communities. All Rights Reserved.

Contributor(s): ______________________________________.
*/

/**
 * Used to implement non-exceptional non-local exits, ie, continuations.
 *
 * @author Mark S. Miller
 * @see org.erights.e.elib.base.Ejector
 */
public class Ejection extends RuntimeException {

    static private final long serialVersionUID = -1852407709780438363L;

    /**
     * Returns problem as a RuntimeException annotated by optMsg.
     * <p/>
     * If problem is or wraps an Ejection, then the Ejection is returned
     * unwrapped and optMsg is ignored.
     *
     * @param optMsg may equivalently be null or "", in which case it will be
     *               ignored by eStack(). See {@link
     *               org.erights.e.develop.exception.EBacktraceThrowable}
     *               for the convention that optMsg should follow.
     */
    static public RuntimeException backtrace(Throwable problem,
                                             String optMsg) {
        Throwable leaf = ThrowableSugar.leaf(problem);
        if (leaf instanceof Ejection) {
            return (Ejection)leaf;
        }
        if (null == optMsg) {
            optMsg = "";
        }
        if (problem instanceof RuntimeException && 0 == optMsg.length()) {
            return (RuntimeException)problem;
        }
        return new EBacktraceException(problem, optMsg);
    }

    /**
     *
     */
    public Ejection() {
    }

    /**
     * @param s
     */
    public Ejection(String s) {
        super(s);
    }

    /**
     * Make sure to not print the stack trace that was not collected.
     */
    public void printStackTrace(java.io.PrintStream s) {
        s.println("Ejection: " + getMessage());
    }

    /**
     * @param s
     */
    public void printStackTrace(java.io.PrintWriter s) {
        s.println("Ejection: " + getMessage());
    }

    /**
     * Suppress stack trace construction.
     */
    public Throwable fillInStackTrace() {
        // DO NOTHING
        return this;
    }
}
