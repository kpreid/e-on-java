package org.quasiliteral.quasiterm;

import org.erights.e.develop.assertion.T;
import org.erights.e.elib.base.Ejector;
import org.erights.e.elib.base.SourceSpan;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.prim.StaticMaker;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.EList;
import org.erights.e.elib.tables.FlexList;
import org.erights.e.meta.java.math.EInt;
import org.quasiliteral.astro.Astro;
import org.quasiliteral.astro.AstroArg;
import org.quasiliteral.astro.AstroBuilder;
import org.quasiliteral.astro.AstroTag;
import org.quasiliteral.term.QuasiBuilder;

import java.io.IOException;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

/**
 * A dollar hole of a quasi-literal term expression is filled in with a
 * substitution arg.
 * <p/>
 * As a ValueMaker, this evaluates to the corresponding substitution arg,
 * given that it meets all encoded conditions (see {@link QHole}).
 * <p/>
 * As a MatchMaker, this compares ("&lt;=&gt;") a specimen term against
 * the substitution argument. The substitution argument must again meet all
 * encoded conditions.
 *
 * @author Mark S. Miller
 */
public class QDollarHole extends QHole {

    static final long serialVersionUID = 3563608921615703114L;

    /**
     *
     */
    static public final StaticMaker QDollarHoleMaker =
      StaticMaker.make(QDollarHole.class);

    /**
     * Makes a hole that is filled in by a substitution arg.
     * <p/>
     * The invariants of a QDollarHole are not checked here, but rather are
     * enforced by the callers in this class and in QTermBuilder.
     * <p/>
     * For the meanings of the parameters, see the {@link QHole} constructor,
     * which has the same parameters.
     */

    QDollarHole(AstroBuilder builder,
                AstroTag optTag,
                int holeNum,
                boolean isFunctorHole,
                SourceSpan optSpan) {

        super(builder, optTag, holeNum, isFunctorHole, optSpan);
    }

    /**
     * Uses 'QDollarHoleMaker(myBuilder, myOptTag, myHoleNum,
     * myIsFunctorHole, myOptSpan)'
     */
    public Object[] getSpreadUncall() {
        Object[] result = {
            QDollarHoleMaker,
            "run",
            myBuilder,
            myOptTag,
            EInt.valueOf(myHoleNum),
            myIsFunctorHole ? Boolean.TRUE : Boolean.FALSE,
            myOptSpan
        };
        return result;
    }

    /**
     *
     */
    public AstroArg qbuild(QuasiBuilder qbuilder) {
        Astro holeNum = qbuilder.leafLong(myHoleNum, myOptSpan);
        Astro result = qbuilder.dollarHole(holeNum);
        if (null != myOptTag) {
            result = qbuilder.taggedHole(this, result);
        }
        if (myIsFunctorHole) {
            result = qbuilder.term(result, qbuilder.empty());
        }
        return result;
    }

    /**
     * If the substitution arg at [myHoleHum]+prefix is actually a list
     * for further indexing, what's the size of that list?
     * <p/>
     * If it's not a list, it could still act as a list by repetition,
     * but then it's of indeterminate size, so just return shapeSoFar.
     * If it is a list, then if shapeSoFar has already been determined (ie,
     * not -1), then require these to agree.
     */
    int startShape(ConstList args,
                   FlexList optBindings,
                   int[] prefix,
                   int shapeSoFar) {
        Object term = multiGet(args, myHoleNum, prefix, true);
        Ejector ej = new Ejector("quasi-term $-hole");
        EList list;
        try {
            list = (EList)EListGuard.coerce(term, ej);
        } catch (Throwable th) {
            ej.result(th);
            //It doesn't matter why the coercion failed. If we're
            //here, the coercion failed rather than throwing.
            return shapeSoFar;
        } finally {
            ej.disable();
        }
        int result = list.size();
        T.require(-1 == shapeSoFar || shapeSoFar == result,
                  "Inconsistent shape: " + shapeSoFar,
                  " vs " + result);
        return result;
    }

    /**
     * Do nothing.
     */
    void endShape(FlexList optBindings,
                  int[] prefix,
                  int shape) {
        // Do nothing.
    }

    /**
     * This extracts the substitution arg at [myHoleNum]+index, requires that
     * it matches, and returns a singleton list containing that arg (which
     * should be a literal term).
     */
    public ConstList substSlice(ConstList args, int[] index) {
        Object termoid = multiGet(args, myHoleNum, index, true);
        Astro optTerm = optCoerce(termoid, myIsFunctorHole);
        T.require(null != optTerm,
                  "Term ", termoid, " doesn't match ", this);
        return ConstList.EmptyList.with(optTerm);
    }

    /**
     * This compares ("&lt;=&gt;") the substitution arg at [myHoleNum]+index
     * against the specimenList[0].
     * <p/>
     * This first ensures that the substitution arg meets our own constraints.
     *
     * @return -1 or 1, depending on whether they match.
     */
    public int matchBindSlice(ConstList args,
                              ConstList specimenList,
                              FlexList bindings,
                              int[] index) {
        if (specimenList.size() <= 0) {
            return -1;
        }
        Object specimen = specimenList.get(0);
        Object termoid = multiGet(args, myHoleNum, index, true);
        Astro optTerm = optCoerce(termoid, myIsFunctorHole);
        T.require(null != optTerm,
                  "Term ", termoid, " doesn't match ", this);
        if (0.0 == E.asFloat64(E.call(optTerm, "op__cmp", specimen))) {
            return 1;
        } else {
            return -1;
        }
    }

    /**
     *
     */
    QHole asTagged(Astro ident) {
        T.require(null == myOptTag,
                  "Already tagged: ", this);
        return new QDollarHole(myBuilder,
                               ident.getTag(),
                               myHoleNum,
                               myIsFunctorHole,
                               myOptSpan);
    }

    /**
     *
     */
    QAstro asFunctor() {
        if (myIsFunctorHole) {
            return this;
        } else {
            return new QDollarHole(myBuilder,
                                   myOptTag,
                                   myHoleNum,
                                   true,
                                   myOptSpan);
        }
    }

    /**
     *
     */
    public void prettyPrintOn(TextWriter out)
      throws IOException {
        if (null != myOptTag) {
            out.print(myOptTag.getTagName());
        }
        out.print("${", EInt.valueOf(myHoleNum), "}");
    }
}
