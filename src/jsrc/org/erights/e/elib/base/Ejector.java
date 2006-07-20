package org.erights.e.elib.base;

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

import org.erights.e.develop.assertion.T;
import org.erights.e.develop.exception.ExceptionMgr;
import org.erights.e.develop.exception.ThrowableSugar;
import org.erights.e.elib.util.OneArgFunc;

/**
 * An Ejector implements a non-local exit construct that can return a
 * value.
 * <p/>
 * It works with an EscapeExpr to cause an escape to take place. When used
 * directly from Java, a typical pattern is:
 * <pre>
 *      Ejector ejector = new Ejector();
 *      //cause ejector to be appropriately accessible from the try
 *      //block
 *      try {
 *          //blah blah blah
 *          if (we need to exit) {
 *              ejector.run(result);
 *          }
 *          //blah blah blah
 *      } catch (Ejection ej) {
 *          Object result = ejector.result(ej);
 *      } finally {
 *          ejector.disable();
 *      }</pre>
 *
 * @author Mark S. Miller
 */
public class Ejector implements OneArgFunc, Runnable {

    private final String myName;

    private boolean myIsEnabled;

    private Object myResult;

    private Ejection myEjection;

    /**
     *
     * @param optName
     */
    public Ejector(String optName) {
        myName = null == optName ? "" : optName + ": ";
        myIsEnabled = true;
        myResult = null;
        myEjection = null;
    }

    /**
     * @return
     */
    public boolean isEnabled() {
        return myIsEnabled;
    }

    /**
     * To avoid confusion, one should always disable an Ejector in a
     * finally block on the way out of its catching context.
     */
    public void disable() {
        myIsEnabled = false;
        myEjection = null;
        myResult = null; //just to help gc
    }

    /**
     * Broken out into a separate public test so Ejection catchers can
     * easily test against several Ejectors.
     */
    public boolean isMine(Throwable t) {
        Throwable leaf = ThrowableSugar.leaf(t);
        return myEjection == leaf && leaf != null;
    }

    /**
     * Having caught a possible Ejection, the catcher asks an Ejector for the
     * result corresponding to that Ejection. If this Ejection was
     * indeed thrown by this Ejector, the corresponding result
     * returned. Otherwise, the Throwable is rethrown.
     */
    public Object result(Throwable t) {
        if (!isMine(t)) {
            throw ExceptionMgr.asSafe(t);
        }
        return myResult;
    }

    /**
     * Non-local exit returning null.
     * <p/>
     * Equivalent to 'run(null)'.
     */
    public void run() {
        run(null);
    }

    /**
     * Non-local exit returning 'result'.
     * <p/>
     * Would normally return void, but to minimize on Deflectors, and since
     * we're not returning anything anyway, we implement OneArgFunc, and so
     * must return Object.
     */
    public Object run(Object result) {
        T.require(myIsEnabled, "Ejector must be enabled");

        // The following controversial line used to not be commented out, and
        // the following comment used to be part of this method's doc-comment:
        /*
         * Currently, on invocation the ejector is also disabled. This is in
         * addition to the disabling that happens on exit from the Ejector's
         * creating {@link org.erights.e.elang.evm.EscapeExpr EscapeExpr}.
         * XXX This decision should be revisited, as it seems cleaner in theory
         * to leave it enabled and reusable while in flight. OTOH, such a reuse
         * is more likely a bug than intentional, so the current behavior may
         * be better in practice.
         */
        // Needless to say, we did revisit it, and made the other decision.
        //myIsEnabled = false;

        myResult = result;
        myEjection = new Ejection();
        throw ExceptionMgr.asSafe(myEjection);
    }

    /**
     *
     */
    public String toString() {
        if (myIsEnabled) {
            return "<" + myName + "Ejector>";
        } else {
            return "<" + myName + "disabled Ejector>";
        }
    }
}
