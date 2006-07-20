package org.quasiliteral.quasiterm;

import org.erights.e.develop.assertion.T;
import org.erights.e.develop.exception.ExceptionMgr;
import org.erights.e.elib.base.Ejector;
import org.erights.e.elib.base.SourceSpan;
import org.erights.e.elib.ref.Ref;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.EList;
import org.erights.e.elib.tables.FlexList;
import org.erights.e.elib.tables.Twine;
import org.quasiliteral.astro.Astro;
import org.quasiliteral.astro.AstroBuilder;
import org.quasiliteral.astro.AstroTag;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

/**
 * Represents a dollar-hole ("${<hole-num>}") or an at-hole ("@{<hole-num>}")
 * that may be a functor-hole or a term-hole, and that may or may not insist
 * on a tag.
 *
 * @author Mark S. Miller
 */
public abstract class QHole extends QAstro {

    static final long serialVersionUID = 199932029973185772L;

    /**
     * @serial If present, represents the token tag that the corresponding
     * literal term must have, and this is a tagged-hole. If this is
     * a dollar-hole, the corresponding literal term must be the
     * substitution arg at myHoleNum. If this is an at-hole, the
     * corresponding literal term is the specimen.
     */
    final AstroTag myOptTag;

    /**
     * @serial Which hole am I?  If this is a dollar-hole, then this
     * says which substitution-arg. If this is an at-hole, then this
     * says which binding.
     */
    final int myHoleNum;

    /**
     * @serial If true, then the corresponding literal term must be
     * 0-arity. If false, then the corresponding literal term may
     * itself have any argument list, and this is a termHole
     */
    final boolean myIsFunctorHole;

    /**
     * Makes a hole that matches a term (either a substitution-arg or a
     * specimen), and either evaluates to it (if this is a dollar-hole) or
     * extracts it (if this is an at-hole).
     *
     * @param builder       Used to build the result of a substitute.
     * @param optTag        If present, represents the token tag that the
     *                      corresponding literal term must have, and this is a
     *                      tagged-hole. If this is a dollar-hole, the corresponding
     *                      literal term must be the substitution are at myHoleNum.
     *                      If this is an at-hole, the corresponding literal term is
     *                      the specimen.
     * @param holeNum       Which hole am I?  If this is a dollar-hole, then this
     *                      says which substitution-arg. If this is an at-hole,
     *                      then this says which binding.
     * @param isFunctorHole If true, then the corresponding literal term must
     *                      be 0-arity. If false, then the corresponding
     *                      literal term may itself have any argument list,
     *                      and this is a termHole.
     * @param optSpan       Where was the source text this node was extracted from?
     */
    QHole(AstroBuilder builder,
          AstroTag optTag,
          int holeNum,
          boolean isFunctorHole,
          SourceSpan optSpan) {

        super(builder, optSpan);
        myOptTag = optTag;
        myHoleNum = holeNum;
        myIsFunctorHole = isFunctorHole;
    }

    /**
     * Given a multi-dimensional list and an index path, retrieve the
     * corresponding element of the list.
     * <p/>
     * For example, if 'args' is [['a','b'],['c','d','e']], 'holeNum' is 1,
     * 'index' is [2,3], and 'repeat' is true, then the answer should be
     * 'e', since it's at args[1][2], and the repeat flag allows us to ignore
     * the 3 when we find that 'e' isn't a list. If 'repeat' had been false,
     * the presence of an additional step on the index path would have caused
     * an exception to be thrown. In either case, if an index step is out of
     * bounds, an exception is thrown regardless of the value of 'repeat'.
     */
    static Object multiGet(ConstList args,
                           int holeNum,
                           int[] index,
                           boolean repeat) {
        Object result = args.get(holeNum);
        for (int i = 0, len = index.length; i < len; i++) {
            Ejector optEj = null;
            if (repeat) {
                optEj = new Ejector("quasi-term hole");
            }
            EList list;
            try {
                list = (EList)EListGuard.coerce(result, optEj);
            } catch (Throwable th) {
                if (null == optEj) {
                    throw ExceptionMgr.asSafe(th);
                } else {
                    optEj.result(th);
                    //It doesn't matter why the coercion failed. If we're
                    //here, the coercion failed rather than throwing. This
                    //means we should simply repeat the last non-list result
                    //we got.
                    return result;
                }
            } finally {
                if (null != optEj) {
                    optEj.disable();
                }
            }
            result = list.get(index[i]);
        }
        return result;
    }

    /**
     * Given a multi-dimensional list and an index path, put newValue at that
     * position in the list.
     * <p/>
     * For example, if 'bindings' is [['a','b'],['c','d','e']] diverge(),
     * 'holeNum' is 1, 'index' is [2], and 'newValue' is 'x', then the 'e'
     * should be replaced with 'x', since it's at list[1][2]. If any index
     * step is out of bounds, the corresponding list is grown to include it
     * (see {@link FlexList#ensureSize(int)} and null is returned.
     * Alternatively, if an old value is being overwritten, then that old
     * value is also returned.
     */
    static Object multiPut(FlexList bindings,
                           int holeNum,
                           int[] index,
                           Object newValue) {
        FlexList list = bindings;
        int dest = holeNum;
        for (int i = 0, len = index.length; i < len; i++) {
            list.ensureSize(dest + 1);
            Object optNext = list.get(dest);
            if (optNext == null) {
                optNext = FlexList.make(index[i] + 1);
                list.put(dest, optNext);
            } else if (optNext instanceof FlexList) {
                //we're cool
            } else {
                optNext = EListGuard.coerce(optNext, null);
                optNext = ((EList)optNext).diverge();
            }
            list = (FlexList)optNext;
            dest = index[i];
        }
        Object result = null;
        if (list.size() > dest) {
            result = list.get(dest);
        }
        list.ensureSize(dest + 1);
        list.put(dest, newValue);
        return result;
    }

    /**
     * Tagged holes return their tag, otherwise throws.
     */
    public AstroTag getTag() {
        T.notNull(myOptTag,
                  "There ain't no tag on an untagged hole");
        return myOptTag;
    }

    /**
     * Holes have no data, so getOptData/0 always returns null.
     */
    public Object getOptData() {
        return null;
    }

    /**
     * A hole itself has no args, even though a term-hole will match a
     * corresponding literal term that does.
     *
     * @return An empty list of QAstroAr
     */
    public ConstList getArgs() {
        return ConstList.EmptyList;
    }

    /**
     * A hole itself has no args, so this just returns itself.
     */
    public Astro withoutArgs() {
        return this;
    }

//    /**
//     * Can only do this to a functor-hole, in which case it makes a
//     * {@link QTerm}.
//     */
//    public Astro withArgs(ConstList qArgs) {
//        T.require(myIsFunctorHole,
//                  "Can only add args to a functor-hole, not a term-hole: ",
//                  this);
//        return new QTerm(myBuilder, this,
//                         QSeq.run(myBuilder, qArgs));
//    }

    /**
     * A hole is a leaf, and so has height 1
     */
    public int getHeight() {
        return 1;
    }

    /**
     *
     */
    abstract QHole asTagged(Astro ident);

    /**
     *
     * @param termoid
     * @param isFunctorHole
     * @return
     */
    Astro optCoerce(Object termoid, boolean isFunctorHole) {
        return optCoerce(termoid, isFunctorHole, myOptTag);
    }
}
