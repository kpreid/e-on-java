package org.erights.e.elib.tables;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.develop.exception.ExceptionMgr;
import org.erights.e.develop.exception.EBacktraceException;
import org.erights.e.elib.base.ClassDesc;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.prim.ScriptMaker;
import org.erights.e.elib.ref.Ref;
import org.erights.e.elib.util.ClassCache;
import org.erights.e.elib.vat.SynchQueue;

import java.lang.reflect.Array;
import java.util.Hashtable;

/**
 * Implements E's sameness semantics, which should be used only through the Ref
 * class.
 * <p/>
 * The static methods are the recursive cycle-breaking sameness algorithm. An
 * Equalizer instance is a  hypothetical comparison pair as used by the
 * algorithm. Equalizer instances are honorary Selfless objects, so that their
 * .equals() and .hashCode() will be used to compare them.
 *
 * @author Mark S. Miller
 * @see Ref
 */
public final class Equalizer {

    /**
     * A random guess at a good value.
     */
    static private final int HASH_DEPTH = 10;

    /**
     * All instances of the left hand (key) types simplify (for purposes of
     * sameness comparison) to instances of the right hand (value) types.
     * <p/>
     * This is much like {@link ScriptMaker#Promotions ScriptMaker.Promotions},
     * but with some differences.
     * <p/>
     * XXX Should ConstLists simplify into arrays, rather than just describing
     * themselves (using {@link ConstList#getSpreadUncall}) using arrays? The
     * corrent code probably doesn't judge an array and a corresponding
     * ConstList as the same. They probably should be judged to be the same.
     */
    static private final String[][] Simplifications = {
      {"java.lang.Byte", "org.erights.e.meta.java.math.EInt"},
      {"java.lang.Short", "org.erights.e.meta.java.math.EInt"},
      {"java.lang.Integer", "org.erights.e.meta.java.math.EInt"},
      {"java.lang.Long", "org.erights.e.meta.java.math.EInt"},
      {"java.math.BigInteger", "org.erights.e.meta.java.math.EInt"},

      {"java.lang.Float", "java.lang.Double"},

      //The table virtually includes all subclasses of ClassDesc as well,
      //which are added lazily by OptSimplification/1 below.
      //XXX BUG: A class promoted and simplified isn't necessarily the same
      //class. For example, '<type:byte> != <type:java.lang.Byte>', but
      //the promotions of these two classes are (E) ==.
      {"org.erights.e.elib.base.ClassDesc", "java.lang.Class"},

      //Though String promotes to Twine, only SimpleTwine and EmptyTwine
      //simplify back to String. This is less irregular than it seems,
      //since Twine with no extra info will actually be a SimpleTwine or
      //EmptyTwine.
      {"org.erights.e.elib.tables.EmptyTwine", "java.lang.String"},
      {"org.erights.e.elib.tables.SimpleTwine", "java.lang.String"},};

    /**
     * Maps fq class names to the fqName of the classes they simplify to. <p>
     * <p/>
     * TheSimplifications is initialized lazily in order to avoid possible
     * circular static initialization dependencies. Uses legacy Hashtable
     * rather than EMap in order to avoid a circular dependency, and to get
     * thread safety for mutable static cache state.
     *
     * @noinspection StaticNonFinalField
     */
    static private Hashtable TheSimplifications = null;

    /**
     * Map a class to the class it simplifies to, or null if none
     */
    static public Class OptSimplification(Class clazz) {
        if (null == TheSimplifications) {
            //noinspection NonThreadSafeLazyInitialization
            TheSimplifications = new Hashtable();
            for (int i = 0; i < Simplifications.length; i++) {
                TheSimplifications.put(Simplifications[i][0],
                                       Simplifications[i][1]);
            }
        }
        String clazzName = clazz.getName();
        String simpName = (String)TheSimplifications.get(clazzName);
        if (null == simpName) {
            if (ClassDesc.class.isAssignableFrom(clazz)) {
                TheSimplifications.put(clazzName, "java.lang.Class");
                return Class.class;
            } else {
                return null;
            }
        }
        try {
            return ClassCache.forName(simpName);
        } catch (Exception ex) {
            throw new EBacktraceException(ex,
                                      "# simplification not found: " +
                                        simpName);
        }
    }

    /** 
     * Map a class to the class it simplifies to, or itself. Added for use by
     * MirandaMethods#__getAllegedType.
     * 
     * @author Kevin Reid
     */
    static public Class Simplification(Class clazz) {
        Class simplification = OptSimplification(clazz);
        if (null == simplification) {
            return clazz;
        } else {
            return simplification;
        }
    }

    static private final int INITIAL_SIZE = 30;

    static private final SynchQueue TheCachedEqualizers =
      new SynchQueue(Equalizer.class);

    private Object[] myLefts;

    private Object[] myRights;

    private int myMaxSofar;

    /**
     *
     */
    static public Equalizer make() {
        Equalizer result = (Equalizer)TheCachedEqualizers.optDequeue();
        return null == result ? new Equalizer() : result;
    }

    /**
     *
     */
    private Equalizer() {
        myLefts = new Object[INITIAL_SIZE];
        myRights = new Object[INITIAL_SIZE];
        myMaxSofar = 0;
    }

    /**
     *
     */
    static private Object simplify(Object ref) {
        ref = Ref.resolution(ref);
        if (null == ref) {
            return null;
        }
        Class optSimpClass = OptSimplification(ref.getClass());
        if (null == optSimpClass) {
            return ref;
        } else {
            return E.as(ref, optSimpClass);
        }
    }

    /**
     * The implementation of Ref.isSameEver(left, right)
     *
     * @see Ref#isSameEver
     */
    static public boolean isSameEver(Object left, Object right)
      throws NotSettledException {

        if (left == right) {
            return true;
        }
        Equalizer eq = make();
        try {
            return eq.sameEver(left, right);
        } finally {
            TheCachedEqualizers.enqueue(eq);
        }
    }

    /**
     *
     */
    static public boolean isSameYet(Object left, Object right) {

        if (left == right) {
            return true;
        }
        Equalizer eq = make();
        try {
            return eq.sameYet(left, right);
        } finally {
            TheCachedEqualizers.enqueue(eq);
        }
    }

    /**
     * The implementation of 'Ref isSettled(ref)'
     *
     * @see Ref#isSettled(Object)
     */
    static public boolean isSettled(Object obj) {
        try {
            return samenessFringe(obj, null, null);
        } catch (NotSettledException nse) {
            throw ExceptionMgr.asSafe(nse);
        }
    }

    /**
     * Two settled objects that are the same() must have the same
     * samenessHash().
     * <p/>
     * Only settled objects may be hashed with this method.
     */
    static int samenessHash(Object obj) {
        SamenessHashCacher cacher = null;
        int result;
        if (null != obj && obj instanceof SamenessHashCacher) {
            cacher = (SamenessHashCacher)obj;
            result = cacher.mySamenessHashCache;
            if (-1 != result) {
                return result;
            }
        }
        try {
            result = samenessHash(obj, HASH_DEPTH, null, null);
        } catch (NotSettledException e) {
            throw ExceptionMgr.asSafe(e);
        }
        if (null != cacher) {
            cacher.mySamenessHashCache = result;
        }
        return result;
    }

    /**
     * With this method, unsettled objects may be hashed; but their hash only
     * lasts until they settle further.
     * <p/>
     * This is useless for many purposes, but is good enough to build a {@link
     * TraversalKey} wrapper, which can be used as a key in tables in order to
     * finitely walk cyclic unsettled structures without a linear search.
     */
    static int sameYetHash(Object obj, FlexList fringe) {
        int result;
        try {
            result = samenessHash(obj, HASH_DEPTH, null, fringe);
        } catch (NotSettledException e) {
            throw ExceptionMgr.asSafe(e);
        }
        FringeNode[] fringeA = (FringeNode[])fringe.getArray(FringeNode.class);
        for (int i = 0, len = fringeA.length; i < len; i++) {
            result ^= fringeA[i].hashCode();
        }
        return result;
    }

    /**
     * Parallels the recursive logic of same/3, except that we make things more
     * efficient by leaving objects in sofar.
     * <p/>
     * In all cases, if <tt>obj</tt> is settled, then we return a hashCode for
     * it such that <tt>x == y</tt> implies <tt>hash(x) == hash(y)</tt>. This
     * must be true even in the presence of cycles, and even if one is wound
     * more tightly than the other. In this case, the optFringe argument is
     * ignored.
     * <p/>
     * If <tt>optFringe</tt> is null, then we require that <tt>obj</tt> is
     * settled: If <tt>obj</tt> isn't settled, we throw a NotSettledException.
     * <p/>
     * If <tt>optFringe</tt> isn't null and <tt>obj</tt> is not settled, then
     * the Java == identity of the promises at the <i>current</i> fringe of
     * <tt>obj</tt> are placed in optFringe, and a hash is returned taking this
     * <i>current</i> fringe into account. Should any of the promises at the
     * fringe of x later be forwarded, even to another promise, a sameHash of
     * obj then <i>should</i> return a different hash, and <i>must</i> return a
     * different fringe. Otherwise, TraversalKey cannot satisfy the contract
     * for stable settled sameness.
     */
    static private int samenessHash(Object obj,
                                    int hashDepth,
                                    FringePath path,
                                    FlexList optFringe)
      throws NotSettledException {

        if (0 >= hashDepth) {
            if (samenessFringe(obj, path, optFringe)) {
                // obj is settled
                return -1;
            } else if (null == optFringe) {
                // obj fails our requirement to be settled
                throw new NotSettledException("Must be settled");
            } else {
                // obj isn't settled. The accumulated fringe should be
                // taken into account, but that's best done by our caller.
                return -1;
            }
        }

        // rest of isSettled happens after simplification.
        obj = simplify(obj);

        // null is only the same as itself
        if (null == obj) {
            return 0;
        }

        // sameness is recursive thru arrays
        if (obj.getClass().isArray()) {
            int len = Array.getLength(obj);
            int result = len;
            for (int i = 0; i < len; i++) {
                result ^= i ^ samenessHash(Array.get(obj, i),
                                           hashDepth - 1,
                                           optFringe == null ?
                                             null :
                                             new FringePath(i, path),
                                           optFringe);
            }
            return result;
        }

        // sameness is recursive thru transparent Selfless objects
        if (obj instanceof Selfless) {
            Selfless a = (Selfless)obj;
            return samenessHash(a.getSpreadUncall(),
                                hashDepth,
                                path,
                                optFringe);
        }

        // sameness defaults to .equals() for honorary Selfless objects
        if (Selfless.HONORARY.has(obj.getClass())) {
            return obj.hashCode();
        }

        if (Ref.isResolved(obj)) {
            // In this case, sameness is identical to Java's '=='
            return System.identityHashCode(obj);
        } else if (null == optFringe) {
            throw new NotSettledException("must be settled");
        } else {
            optFringe.push(new FringeNode(obj, path));
            // obj is an unresolved promise. Our caller will take its
            // hash into account.
            return -1;
        }
    }

    /**
     * Parallels the recursive logic of same/3, except that we make things more
     * efficient by leaving objects in sofar.
     * <p/>
     * In all cases, if <tt>original</tt> is settled, then we return true, even
     * in the presence of cycles. In this case, the optFringe argument is
     * ignored.
     * <p/>
     * In all cases, if <tt>original</tt> is unsettled, then we return false.
     * <p/>
     * If <tt>optFringe</tt> is null and <tt>original</tt> isn't settled, then
     * we <i>should</i> return false as soon as possible -- when we encounter
     * the first unresolved promise.
     * <p/>
     * If <tt>optFringe</tt> isn't null and <tt>original</tt> is not settled,
     * then the Java == identity of the promises at the <i>current</i> fringe
     * of <tt>original</tt> are placed in optFringe. Should any of the promises
     * at the fringe or later be forwarded, even to another promise, a
     * samenessFringe of <tt>original</tt> afterwards <i>must</i> accumulate a
     * different fringe. Otherwise, TraversalKey cannot satisfy the contract
     * for stable settled sameness.
     */
    static private boolean samenessFringe(Object original,
                                          FringePath path,
                                          FlexList optFringe)
      throws NotSettledException {
        IdentityMap sofar = new IdentityMap(Object.class, Void.class);
        return samenessFringe(original, sofar, path, optFringe);
    }

    static private boolean samenessFringe(Object original,
                                          IdentityMap sofar,
                                          FringePath path,
                                          FlexList optFringe)
      throws NotSettledException {

        // It's the hypothesis we're already investigating.
        // Must happen before simplification, since it depends on EQ.
        if (sofar.maps(original)) {
            return true;
        }

        // rest of isSettled happens after simplification.
        Object obj = simplify(original);

        // null is only the same as itself
        if (null == obj) {
            return true;
        }
        if (Ref.isDeepFrozen(original)) {
            return true;
        }
        if (original instanceof SamenessHashCacher) {
            SamenessHashCacher cacher = (SamenessHashCacher)original;
            if (-1 != cacher.mySamenessHashCache) {
                return true;
            }
        }
        if (sofar.maps(obj)) {
            return true;
        }

        // sameness is recursive thru arrays
        if (obj.getClass().isArray()) {
            sofar.put(original, null);
            int len = Array.getLength(obj);
            boolean result = true;
            for (int i = 0; i < len; i++) {
                result &= samenessFringe(Array.get(obj, i),
                                         sofar,
                                         optFringe == null ?
                                           null :
                                           new FringePath(i, path),
                                         optFringe);
                if (!result && null == optFringe) {
                    // Report an unresolved promise early
                    return false;
                }
            }
            return result;
        }

        // sameness is recursive thru transparent Selfless objects
        if (obj instanceof Selfless) {
            sofar.put(original, null);
            Selfless a = (Selfless)obj;
            return samenessFringe(a.getSpreadUncall(), sofar, path, optFringe);
        }

        // Honorary Selfless objects are settled
        if (Selfless.HONORARY.has(obj.getClass())) {
            return true;
        }

        if (Ref.isResolved(obj)) {
            // In this case, sameness is identical to Java's '=='
            return true;
        } else {
            if (null != optFringe) {
                optFringe.push(new FringeNode(obj, path));
            }
            return false;
        }
    }

    /**
     *
     */
    private void clear() {
        for (int i = 0; i < myMaxSofar; i++) {
            myLefts[i] = null;
            myRights[i] = null;
        }
        myMaxSofar = 0;
    }

    private boolean findSofar(Object left, Object right, int sofar) {
        int lhash = System.identityHashCode(left);
        int rhash = System.identityHashCode(right);
        if (rhash < lhash) {
            Object t = left;
            left = right;
            right = t;
        }
        for (int i = 0, max = sofar; i < max; i++) {
            if (left == myLefts[i] && right == myRights[i]) {
                return true;
            }
        }
        return false;
    }

    private int pushSofar(Object left, Object right, int sofar) {
        if (sofar >= myMaxSofar) {
            myMaxSofar = sofar + 1;
            int len = myLefts.length;
            if (sofar >= len) {
                int newLen = (len * 2) + 32;
                Object[] newLefts = new Object[newLen];
                System.arraycopy(myLefts, 0, newLefts, 0, len);
                myLefts = newLefts;
                Object[] newRights = new Object[newLen];
                System.arraycopy(myLefts, 0, newLefts, 0, len);
                myRights = newRights;
            }
        }
        int lhash = System.identityHashCode(left);
        int rhash = System.identityHashCode(right);
        if (rhash < lhash) {
            Object t = left;
            left = right;
            right = t;
        }
        // add candidate to sofar
        myLefts[sofar] = left;
        myRights[sofar] = right;
        int sofarther = sofar + 1;
        return sofarther;
    }

    /**
     *
     */
    public boolean sameEver(Object left, Object right)
      throws NotSettledException {

        Boolean optResult = optSame(left, right);
        if (null == optResult) {
            throw new NotSettledException("Not sufficiently settled: " +
              E.toQuote(left) + " == " + E.toQuote(right));
        } else {
            return optResult.booleanValue();
        }
    }

    public boolean sameYet(Object left, Object right) {
        Boolean optResult = optSame(left, right);
        if (null == optResult) {
            return false;
        } else {
            return optResult.booleanValue();
        }
    }

    /**
     * The implementation of Ref.same(left, right)
     *
     * @see Ref#isSameEver
     */
    public Boolean optSame(Object left, Object right) {
        try {
            return optSame(left, right, 0);
        } finally {
            clear();
        }
    }

    /**
     *
     */
    private Boolean optSame(Object left, Object right, int sofar) {

        if (left == right) {
            return Boolean.TRUE;
        }

        // TODO Should the original or the simplified version be stored in the
        // cache?
        // Rest of sameness happens after simplification.
        left = simplify(left);
        right = simplify(right);

        if (left == right) {
            return Boolean.TRUE;
        }

        if (!Ref.isResolved(left) || !Ref.isResolved(right)) {
            // at least one is an unresolved promise, and they are not the
            // same unresolved promise. Therefore, we know we don't know.
            return null;
        }

        // null is only the same as itself
        if (null == left || null == right) {
            return Boolean.FALSE;
        }

        // It's the hypothesis we're already investigating.
        // Must happen before simplification, since it depends on EQ.
        boolean res = findSofar(left, right, sofar);
        if (res) {
            return Boolean.TRUE;
        }

        // sameness is recursive thru arrays
        final boolean leftArray = left.getClass().isArray();
        final boolean rightArray = right.getClass().isArray();
        if (leftArray && rightArray) {
            int len = Array.getLength(left);
            if (Array.getLength(right) != len) {
                return Boolean.FALSE;
            }
            int sofarther = pushSofar(left, right, sofar);
            for (int i = 0; i < len; i++) {
                final Object newLeft = Array.get(left, i);
                final Object newRight = Array.get(right, i);
                Boolean optResult = optSame(newLeft, newRight, sofarther);
                if (null == optResult) {
                    return null;
                } else if (!optResult.booleanValue()) {
                    return Boolean.FALSE;
                }
            }
            return Boolean.TRUE;
        } else if (leftArray || rightArray) {
            return Boolean.FALSE;
        }

        // sameness is recursive thru transparent Selfless objects
        final boolean leftSelfless = left instanceof Selfless;
        final boolean rightSelfless = right instanceof Selfless;
        if (leftSelfless && rightSelfless) {
            int sofarther = pushSofar(left, right, sofar);
            Selfless a = (Selfless)left;
            Selfless b = (Selfless)right;
            return optSame(a.getSpreadUncall(),
                           b.getSpreadUncall(),
                           sofarther);
        } else if (leftSelfless || rightSelfless) {
            return Boolean.FALSE;
        }

        // sameness defaults to .equals() for honorary Selfless objects.
        // Note that we check this after "instanceof Selfless", so if it's
        // both, it's considered to be really Selfless (as opposed to
        // honorarily Selfless)
        Class leftClass = left.getClass();
        Class rightClass = right.getClass();
        if (Selfless.HONORARY.has(leftClass) &&
          Selfless.HONORARY.has(rightClass)) {

            if (left.equals(right)) {
                return Boolean.TRUE;
            } else {
                return Boolean.FALSE;
            }
//        } else if (Selfless.HONORARY.has(leftClass) ||
//          Selfless.HONORARY.has(rightClass)) {
//
//            return Boolean.FALSE;
        }

        // otherwise, sameness is identical to Java's '==' which we've
        // already tested. If we arrive here, it must've been false.
        return Boolean.FALSE;
    }
}
