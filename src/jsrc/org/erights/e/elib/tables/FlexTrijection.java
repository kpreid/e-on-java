// Copyright 2003 Hewlett Packard, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.elib.tables;

import org.erights.e.develop.assertion.T;
import org.erights.e.elib.base.ClassDesc;
import org.erights.e.elib.base.Thunk;
import org.erights.e.elib.ref.Ref;
import org.erights.e.elib.slot.AnyGuard;
import org.erights.e.elib.slot.Guard;
import org.erights.e.elib.util.AlreadyDefinedException;

/**
 * A mutable single valued mapping whose <>trijective</i> inverse is also a
 * mutable single valued mapping.
 * <p/>
 * Given two sets X and Y, a function f that maps from X to Y, and a function
 * g that maps from Y to X; f and g are
 * <a href="http://mathworld.wolfram.com/Bijective.html"
 * >bijective</a> inverses of each other iff<pre>
 *     for all x in X { g(f(x)) == x } &amp;&amp;
 *     for all y in Y { f(g(y)) == y }.</pre>
 * A Bijection is then a function whose bijective inverse that's also a
 * Bijection.
 * <p/>
 * We define here the related property that f and g are are <i>trijective
 * inverses iff<pre>
 *     for all x in X { f(g(f(x))) == f(x) } &amp;&amp;
 *     for all y in Y { g(f(g(y))) == g(y) }.</pre>
 * A Trijection is then a function whose trijective inverse is also a
 * Trijection.
 * <p/>
 * A FlexTrijection is a FlexMap you can ask for its inverse. The two
 * FlexTrijection are facets on the same mutable state, so you can query and
 * update this state through either or both. The facet representing f will
 * operates on all of X as its domain, but only on that subset of Y that's in
 * the range of f -- those y's in Y for which there's an x in X such that
 * f(x) == y. Likewise, the facet representing g operated on all of Y as its
 * domain, but only on that subset of X that's in the range of g.
 * <p/>
 * A Trijection generally has many trijective inverses, but only has one
 * minimal trijective inverse. g is a minimal trijective inverse of f if the
 * domain of g is exactly the range of f. If f and g are both minimal
 * trijective inverses of each other, then they are also bijective inverses.
 * <p/>
 * The basic mutation operation on a FlexMap is <tt>f[x] := y</tt>. On a
 * FlexTrijection, this also has the effect <tt>g[y] := x</tt>. If stores have
 * only been performed on f but never on g, then g will be the minimal
 * trijective inverse of f. If all stores have been performed only on f, and if
 * all these stores have been <i>strict</i>, ie, of the form
 * <pre>f.put(x, y, true)</pre>
 * then f and g will be bijective inverses.
 * <p/>
 * On a store, what actually gets stored is x and y after being coerced, which
 * we call cx and cy. To avoid confusion, FlexTrijection arranges that each
 * coercion happens once. Since cx and cy are both being used as keys they must
 * <i>both</i> be settled. Where this isn't appropriate (as with the exit map),
 * then {@link TraversalKey} should be used as a guard for the appropriate
 * set.
 *
 * @author Mark S. Miller
 * @author with thanks to Alan Karp and Norm Hardy
 */
public class FlexTrijection extends FlexMap {

    private final Guard myDomainGuard;
    private final Guard myRangeGuard;
    private final FlexMap myForwardMap;
    private final FlexMap myBackMap;
    private final FlexTrijection myInverse;

    /**
     *
     * @param domainGuard
     * @param rangeGuard
     * @param forwardMap
     * @param backMap
     * @param inverse
     */
    private FlexTrijection(Guard domainGuard,
                           Guard rangeGuard,
                           FlexMap forwardMap,
                           FlexMap backMap,
                           FlexTrijection inverse) {
        myDomainGuard = domainGuard;
        myRangeGuard = rangeGuard;
        myForwardMap = forwardMap;
        myBackMap = backMap;
        myInverse = inverse;
    }

    /**
     * @param optDomainGuard defaults to :any
     * @param optRangeGuard  defaults to :any
     */
    public FlexTrijection(Guard optDomainGuard,
                          Guard optRangeGuard) {
        if (null == optDomainGuard) {
            myDomainGuard = AnyGuard.THE_ONE;
        } else {
            myDomainGuard = optDomainGuard;
        }
        if (null == optRangeGuard) {
            myRangeGuard = AnyGuard.THE_ONE;
        } else {
            myRangeGuard = optRangeGuard;
        }
        myForwardMap = FlexMap.make();
        myBackMap = FlexMap.make();
        myInverse = new FlexTrijection(myRangeGuard,
                                       myDomainGuard,
                                       myBackMap,
                                       myForwardMap,
                                       this);
    }

    /**
     * Defaults to :any, :any
     */
    public FlexTrijection() {
        this(null, null);
    }

    /**
     * Store f[x] := y and g[y] := x
     * <p/>
     * If strict is true, it's more strict than the normal notion: it insists
     * that both the key and value be unique in their columns.
     */
    public void put(Object key, Object value, boolean strict) {
        key = myDomainGuard.coerce(key, null);
        value = myRangeGuard.coerce(value, null);
        if (strict) {
            if (myForwardMap.maps(key)) {
                throw new AlreadyDefinedException("present: " + key);
            }
        } else {
            T.require(Ref.isSettled(key),
                      "Must be settled: ", key);
        }
        myBackMap.put(value, key, strict);
        myForwardMap.put(key, value, strict);
    }

    /**
     * Removes g[f[x]] and f[x].
     */
    public void removeKey(Object key, boolean strict) {
        key = myDomainGuard.coerce(key, null);
        if (myForwardMap.maps(key)) {
            Object value = myForwardMap.get(key);
            myBackMap.removeKey(value, false);
            myForwardMap.removeKey(key, strict);
        } else if (strict) {
            throw new IllegalArgumentException("no " + key + " to remove");
        }
    }

    /**
     *
     */
    public void removeAll() {
        myForwardMap.removeAll();
        myBackMap.removeAll();
    }

    /**
     * @return
     */
    public int size() {
        return myForwardMap.size();
    }

    /**
     * @return
     */
    public Object fetch(Object key, Thunk insteadThunk) {
        key = myDomainGuard.coerce(key, null);
        return myForwardMap.fetch(key, insteadThunk);
    }

    /**
     * @return
     */
    public Object getKeys(Class type) {
        return myForwardMap.getKeys(type);
    }

    /**
     * @return
     */
    public Object getValues(Class type) {
        return myForwardMap.getValues(type);
    }

    /**
     * XXX This one is stupid until we generally shift from keyTypes to
     * keyGuards.
     *
     * @return Object.class
     */
    public Class keyType() {
        if (myDomainGuard instanceof ClassDesc) {
            return ((ClassDesc)myDomainGuard).asClass();
        } else {
            return Object.class;
        }
    }

    /**
     * XXX This one is stupid until we generally shift from valueTypes to
     * Guards.
     *
     * @return Object.class
     */
    public Class valueType() {
        if (myRangeGuard instanceof ClassDesc) {
            return ((ClassDesc)myRangeGuard).asClass();
        } else {
            return Object.class;
        }
    }

    /**
     * @return
     */
    public FlexTrijection getInverse() {
        return myInverse;
    }
}
