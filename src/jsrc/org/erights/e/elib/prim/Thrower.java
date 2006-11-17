package org.erights.e.elib.prim;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.develop.assertion.T;
import org.erights.e.develop.exception.NestedException;
import org.erights.e.elib.util.OneArgFunc;

/**
 * An StaticMaker on this class is the function named "throw" in the
 * universalScope (and therefore also in the safeScope). <p>
 *
 * @author Mark S. Miller
 */
public class Thrower {

    static public final Thrower THE_ONE = new Thrower();

    /**
     *
     */
    private Thrower() {
    }

    /**
     * Throws problem (coerced to a RuntimeException).
     */
    public void run(RuntimeException problem) {
        throw problem;
    }

    /**
     * Exits according to optEjector with the provided value, for use from E.
     * <p/>
     * If optEjector is null, then throws 'value' as a throwable problem that
     * doesn't need to be declared. Otherwise, optEjector should eject rather
     * than returning. If it does return, then 'eject' does its own
     * (backtraced) throw rather than returning.
     */
    public void eject(OneArgFunc optEjector, RuntimeException problem) {
        throw toEject(optEjector, problem);
    }

    /**
     * Exits according to optEjector with the provided value, for use from
     * Java. <p>
     * <p/>
     * If optEjector is null, then returns 'value' as a throwable problem that
     * doesn't need to be declared. Otherwise, optEjector should eject rather
     * than returning. If it does return, then 'toEject' does its own
     * (backtraced) throw rather than returning. <p>
     * <p/>
     * The caller should typically say <pre>
     * <p/>
     *     throw Thrower.toEject(optEjector, problem);
     * </pre>
     * One of the reasons why 'toEject' has its caller do a throw is to provide
     * the Java compiler with better control flow information.
     */
    static public RuntimeException toEject(OneArgFunc optEjector,
                                           Object prob) {
        RuntimeException problem = E.asRTE(prob);
        if (null == optEjector) {
            return problem;
        }
        optEjector.run(problem);
        throw new NestedException(problem,
                                  "# optEjector returned: " + optEjector);
    }

    /**
     * Just something callable from E to breakpoint on in Java.
     */
    public void breakpoint(Object diagnostic) {
        System.err.println("bp: " + diagnostic);
        T.noop();
    }

    /**
     * Prints as "throw", the name by which it's known in the universalScope.
     */
    public String toString() {
        return "throw";
    }
}
