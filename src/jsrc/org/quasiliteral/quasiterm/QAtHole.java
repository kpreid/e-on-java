package org.quasiliteral.quasiterm;

import org.erights.e.develop.assertion.T;
import org.erights.e.elib.base.SourceSpan;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.prim.StaticMaker;
import org.erights.e.elib.tables.ConstList;
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
 * An at-hole of a quasi-literal term expression extracts the specimen into a
 * binding.
 * <p/>
 * An at-hole is not a valid ValueMaker, and so neither is any quasi tree that
 * contains an at-hole. If encountered during substitution, an at-hole throws.
 * It would be good to make this error occur earlier.
 * <p/>
 * As a MatchMaker, this requires the specimen to meet its constraints
 * (optional tag, optional requirement of zero arity), and then extracts it
 * into the binding at [hole-number]+index.
 *
 * @author Mark S. Miller
 */
public class QAtHole extends QHole {

    static private final long serialVersionUID = 6979086503562395304L;

    /**
     *
     */
    static public final StaticMaker QAtHoleMaker =
      StaticMaker.make(QAtHole.class);

    /**
     * Makes a hole that extracts a specimen into a binding.
     * <p/>
     * The invariants of a QAtHole are not checked here, but rather are
     * enforced by the callers in this class and in QTermBuilder.
     * <p/>
     * For the meanings of the parameters, see the {@link QHole} constructor,
     * which has the same parameters.
     */
    QAtHole(AstroBuilder builder,
            AstroTag optTag,
            int holeNum,
            boolean isFunctorHole,
            SourceSpan optSpan) {

        super(builder, optTag, holeNum, isFunctorHole, optSpan);
    }

    /**
     * Uses 'QAtHoleMaker(myBuilder, myOptTag, myHoleNum, myIsFunctorHole,
     * myOptSpan)'
     */
    public Object[] getSpreadUncall() {
        Object[] result = {QAtHoleMaker,
          "run",
          myBuilder,
          myOptTag,
          EInt.valueOf(myHoleNum),
          myIsFunctorHole ? Boolean.TRUE : Boolean.FALSE,
          myOptSpan};
        return result;
    }

    /**
     *
     */
    public AstroArg qbuild(QuasiBuilder qbuilder) {
        Astro holeNum = qbuilder.leafLong(myHoleNum, myOptSpan);
        Astro result = qbuilder.atHole(holeNum);
        if (null != myOptTag) {
            result = qbuilder.taggedHole(this, result);
        }
        if (myIsFunctorHole) {
            result = qbuilder.term(result, qbuilder.empty());
        }
        return result;
    }

    /**
     * An at-hole doesn't contribute to the shape, so just returns shapeSoFar,
     * but initializes the binding at [myHoleNum]+prefix to a new empty
     * FlexList.
     */
    int startShape(ConstList args,
                   FlexList optBindings,
                   int[] prefix,
                   int shapeSoFar) {
        T.notNull(optBindings, "no at-holes in a ValueMaker: ", this);
        multiPut(optBindings, myHoleNum, prefix, FlexList.make());
        return shapeSoFar;
    }

    /**
     * Truncate and snapshot the bindings at [myHoleNum]+prefix to shape.
     */
    void endShape(FlexList optBindings, int[] prefix, int shape) {
        T.notNull(optBindings, "no at-holes in a ValueMaker: ", this);
        FlexList list =
          (FlexList)multiGet(optBindings.snapshot(), myHoleNum, prefix, false);
        list.setSize(shape);
        multiPut(optBindings, myHoleNum, prefix, list.snapshot());
    }

    /**
     * This throws, complaining that a quasi-tree with an @-hole may not be
     * used as a ValueMaker.
     */
    public ConstList substSlice(ConstList args, int[] index) {
        T.fail(
          "A quasi-tree with an @-hole may not be used as a ValueMaker: " +
            this);
        return null; //make compiler happy
    }

    /**
     * This extracts the specimen into the binding at [myHoleNum]+index.
     * <p/>
     * This first ensures the specimen meets our own constraints.
     *
     * @return -1 or 1, depending on whether they match.
     */
    public int matchBindSlice(ConstList args,
                              ConstList specimenList,
                              FlexList bindings,
                              int[] index) {
        if (0 >= specimenList.size()) {
            return -1;
        }
        Astro optSpecimen = optCoerce(specimenList.get(0));
        if (null == optSpecimen) {
            return -1;
        }
        Object optOldValue = multiPut(bindings, myHoleNum, index, optSpecimen);
        if (null == optOldValue ||
          0.0 == E.asFloat64(E.call(optOldValue, "op__cmp", optSpecimen))) {

            return 1;
        } else {
            //XXX Should this throw?
            return -1;
        }
    }

    /**
     *
     */
    QHole asTagged(Astro ident) {
        T.require(null == myOptTag, "Already tagged: ", this);
        return new QAtHole(myBuilder,
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
            return new QAtHole(myBuilder,
                               myOptTag,
                               myHoleNum,
                               true,
                               myOptSpan);
        }
    }

    /**
     *
     */
    public void prettyPrintOn(TextWriter out) throws IOException {
        if (null != myOptTag) {
            out.print(myOptTag.getTagName());
        }
        out.print("@{", EInt.valueOf(myHoleNum), "}");
    }
}
