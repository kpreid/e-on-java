// Copyright 2003 Hewlett Packard, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.elang.interp;

import org.erights.e.elib.prim.E;
import org.erights.e.elib.prim.Selector;

/**
 * @author Mark S. Miller
 */
public class Comparer {

    static public final Comparer THE_ONE = new Comparer();

    static private final Selector COMPARE_TO = new Selector("op__cmp", 1);
    static private final Selector BELOW_ZERO = new Selector("belowZero", 0);
    static private final Selector AT_MOST_ZERO = new Selector("atMostZero", 0);
    static private final Selector IS_ZERO = new Selector("isZero", 0);
    static private final Selector AT_LEAST_ZERO =
      new Selector("atLeastZero", 0);
    static private final Selector ABOVE_ZERO = new Selector("aboveZero", 0);

    private Comparer() {
    }

    /**
     *
     */
    public Object compare(Object a, Object b) {
        Object[] args = {b};
        return COMPARE_TO.callIt(a, args);
    }

    /**
     *
     */
    public Object lessThan(Object a, Object b) {
        return BELOW_ZERO.callIt(compare(a, b), E.NO_ARGS);
    }

    /**
     *
     */
    public Object leq(Object a, Object b) {
        return AT_MOST_ZERO.callIt(compare(a, b), E.NO_ARGS);
    }

    /**
     *
     */
    public Object asBigAs(Object a, Object b) {
        return IS_ZERO.callIt(compare(a, b), E.NO_ARGS);
    }

    /**
     *
     */
    public Object geq(Object a, Object b) {
        return AT_LEAST_ZERO.callIt(compare(a, b), E.NO_ARGS);
    }

    /**
     *
     */
    public Object greaterThan(Object a, Object b) {
        return ABOVE_ZERO.callIt(compare(a, b), E.NO_ARGS);
    }
}
