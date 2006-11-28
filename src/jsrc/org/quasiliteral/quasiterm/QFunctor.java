package org.quasiliteral.quasiterm;

import org.erights.e.develop.format.StringHelper;
import org.erights.e.elib.base.SourceSpan;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.prim.StaticMaker;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.FlexList;
import org.erights.e.elib.tables.Twine;
import org.erights.e.meta.java.math.EInt;
import org.quasiliteral.astro.Astro;
import org.quasiliteral.astro.AstroArg;
import org.quasiliteral.astro.AstroBuilder;
import org.quasiliteral.astro.AstroTag;
import org.quasiliteral.term.QuasiBuilder;
import org.quasiliteral.term.Term;

import java.io.IOException;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

/**
 * A quasi-literal functor of a {@link Term Term}.
 * <p/>
 * As a ValueMaker, this acts like a 0-arity Term. As a MatchMaker, this
 * matches only the functor info of a specimen term, and ignores the specimen's
 * arguments.
 *
 * @author Mark S. Miller
 */
public final class QFunctor extends QAstro {

    static private final long serialVersionUID = -4841426503764679037L;

    /**
     *
     */
    static public final StaticMaker QFunctorMaker =
      StaticMaker.make(QFunctor.class);

    /**
     * @serial Represents the token-type of the functor of this QTerm.
     */
    private final AstroTag myTag;

    /**
     * @serial If the functor represents a literal-data token, then this is the
     * data, and myTag must represent the cononical corresponding token-type
     * for this kind of data in this schema.
     */
    private final Object myOptData;

    /**
     * Makes a QTerm that matches or generates a Astro.
     * <p/>
     * The invariants of a QTerm are not checked here, but rather are enforced
     * by the callers in this class and in QTermBuilder.
     *
     * @param builder Used to build the result of a substitute
     * @param tag     Identifies a token type in a particular grammar or set of
     *                related grammars, used as the functor (or "label") of
     *                this QTerm
     * @param optData Either something that promotes to a {@link Character},
     *                {@link EInt EInt}, {@link Double}, or {@link Twine} or
     *                null. If not null, then the tag must represent the
     *                canonical literal type for this kind of data in this
     *                schema.
     * @param optSpan Where is the source text this node was extracted from?
     */
    QFunctor(AstroBuilder builder,
             AstroTag tag,
             Object optData,
             SourceSpan optSpan) {

        super(builder, optSpan);
        myTag = tag;
        myOptData = optData;
    }

    /**
     * Uses 'QFunctorMaker(myBuilder, myTag, myOptData, myOptSpan)'
     */
    public Object[] getSpreadUncall() {
        Object[] result =
          {QFunctorMaker, "run", myBuilder, myTag, myOptData, myOptSpan};
        return result;
    }

    public AstroArg withOptSpan(SourceSpan optSpan) {
        return new QFunctor(myBuilder, myTag, myOptData, optSpan);
    }

    /**
     *
     */
    public AstroArg qbuild(QuasiBuilder qbuilder) {
        if (null == myOptData) {
            return qbuilder.leafTag(myTag, myOptSpan);
        } else {
            //Assumes tag adds no more info.
            return qbuilder.leafData(myOptData, myOptSpan);
        }
    }

    /**
     * Represents the token-type of the functor of this term.
     */
    public AstroTag getTag() {
        return myTag;
    }

    /**
     * Either literal data or null. If not null, then the tag must represent
     * the canonical literal type for this kind of data in this schema.
     */
    public Object getOptData() {
        return myOptData;
    }

    /**
     * @return An empty list of QAstroArg
     */
    public ConstList getArgs() {
        return ConstList.EmptyList;
    }

    /**
     *
     */
    public Astro withoutArgs() {
        return this;
    }

//    /**
//     *
//     */
//    public Astro withArgs(ConstList qArgs) {
//        return new QTerm(myBuilder, this,
//                         QSeq.run(myBuilder, qArgs));
//    }

    /**
     * @return A single list of a single Astro, whose functor is based on
     *         literal functor info of this qfunctor
     */
    public ConstList substSlice(ConstList args, int[] index) {
        Astro tFunctor;
        if (null == myOptData) {
            tFunctor = myBuilder.leafTag(myTag, myOptSpan);
        } else {
            tFunctor = myBuilder.leafData(myOptData, myOptSpan);
        }
        return ConstList.EmptyList.with(tFunctor);
    }

    /**
     * Attempts to match against the Astro specimenList[0].
     * <p/>
     *
     * @return -1 or 1, depending on whether the functor information of
     *         specimenList[0] matches that of this qfunctor, while ignoring
     *         the args.
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
        if (null != myOptData) {
            //if myOptData is null, then it's a wildcard.
            Object optOtherData = optSpecimen.getOptData();
            if (null == optOtherData) {
                //If the pattern has data, then the specimen must as well
                return -1;
            }
            if (!myOptData.equals(optOtherData)) {
                //'equals/1' is valid for all valid literal data types except
                //Twine
                if (myOptData instanceof Twine &&
                  optOtherData instanceof Twine) {

                    String mine = ((Twine)myOptData).bare();
                    String other = ((Twine)optOtherData).bare();
                    if (!mine.equals(other)) {
                        return -1;
                    }
                } else {
                    return -1;
                }
            }
        }
        //functor info matches, so we match one (the first) element of
        //specimenList
        return 1;
    }

    /**
     * A functor as a Term is a leaf, and so has height 1
     */
    public int getHeight() {
        return 1;
    }

    /**
     *
     */
    public void prettyPrintOn(TextWriter out) throws IOException {
        String label = myTag.getTagName();
        if (null != myOptData) {
            label = E.toQuote(myOptData).bare();
            label = StringHelper.replaceAll(label, "$", "$$");
            label = StringHelper.replaceAll(label, "@", "@@");
            label = StringHelper.replaceAll(label, "`", "``");
        }
        out.print(label);
    }

    /**
     *
     */
    QAstro asFunctor() {
        return this;
    }

    /**
     * Just returns shapeSoFar, since this has no shape and no children
     */
    int startShape(ConstList args,
                   FlexList optBindings,
                   int[] prefix,
                   int shapeSoFar) {
        return shapeSoFar;
    }

    /**
     * Do nothing.
     */
    void endShape(FlexList optBindings, int[] prefix, int shape) {
        // Do nothing.
    }

    /**
     *
     */
    Astro optCoerce(Object termoid) {
        return optCoerce(termoid, true, myTag);
    }
}
