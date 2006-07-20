package org.quasiliteral.quasiterm;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.develop.assertion.T;
import org.erights.e.elib.base.SourceSpan;
import org.erights.e.elib.base.ValueThunk;
import org.erights.e.elib.tables.IdentityCacheTable;
import org.erights.e.elib.tables.Twine;
import org.quasiliteral.astro.Astro;
import org.quasiliteral.astro.AstroArg;
import org.quasiliteral.astro.AstroBuilder;
import org.quasiliteral.astro.AstroTag;
import org.quasiliteral.astro.BaseBuilder;
import org.quasiliteral.base.MatchMaker;
import org.quasiliteral.base.QuasiExprParser;
import org.quasiliteral.base.QuasiPatternParser;
import org.quasiliteral.base.ValueMaker;
import org.quasiliteral.term.QuasiBuilder;
import org.quasiliteral.term.TermBuilder;
import org.quasiliteral.term.TermParser;

/**
 * @author Mark S. Miller
 */
public class QBuilder extends BaseBuilder
  implements QuasiBuilder, QuasiExprParser, QuasiPatternParser {

    /**
     *
     */
    static public final QBuilder term__quasiParser =
      new QBuilder(TermBuilder.FOR_TERMS);

    /**
     *
     */
    private final AstroBuilder myBuilder;

    /**
     * Warning: mutable static state. Must ensure that it's not detectably
     * mutable.
     */
    private transient IdentityCacheTable myCache = null;

    /**
     *
     */
    public QBuilder(AstroBuilder builder) {
        super(builder.getSchema());
        myBuilder = builder;
    }

    /**
     *
     */
    public ValueMaker valueMaker(Twine template, int[] dlrHoles) {
        T.fail("XXX new quasi valueMaker API not yet implemented");
        return null; //make compiler happy
    }

    /**
     *
     */
    public ValueMaker valueMaker(Twine template) {
        if (null == myCache) {
            myCache = new IdentityCacheTable(QAstro.class, 100);
        }
        QAstro result = (QAstro)myCache.fetch(template, ValueThunk.NULL_THUNK);
        if (null == result) {
            result = (QAstro)TermParser.run(template, this);
            myCache.put(template, result);
        }
        return result;
    }

    /**
     *
     */
    public MatchMaker matchMaker(Twine template,
                                 int[] dlrHoles,
                                 int[] atHoles) {
        T.fail("XXX new quasi matchMaker API not yet implemented");
        return null; //make compiler happy
    }

    /**
     *
     */
    public MatchMaker matchMaker(Twine template) {
        return (QAstro)valueMaker(template);
    }

    /**
     *
     */
    protected Astro leafInternal(AstroTag tag,
                                 Object optData,
                                 SourceSpan optSpan) {
        return new QFunctor(myBuilder, tag, optData, optSpan);
    }

    /**
     * For QTerms, this is non-atomic
     */
    public Astro composite(AstroTag tag, Object data, SourceSpan optSpan) {
        return term(leafTag(tag, optSpan),
                    leafData(data, optSpan));
    }

    /**
     * Returns the QTerm '<functor>(<args>...)'.
     * <p/>
     * Note that the QTerm constructor will first convert 'functor' using
     * {@link QAstro#asFunctor()}, so if 'functor' is originally a term-hole,
     * the functor of the resulting QTerm will be a corresponding
     * functor-hole.
     */
    public Astro term(Astro functor, AstroArg args) {
        return new QTerm(myBuilder,
                         (QAstro)functor,
                         (QAstroArg)args);
    }

    /**
     * Just returns 'functor' itself.
     * <p/>
     * Note that, if 'functor' is a term-hole (one that doesn't constrain
     * the literal term to be zero-arity), then so will the result, since it's
     * the same.
     */
    public Astro term(Astro functor) {
        return functor;
    }

    /**
     *
     */
    public AstroArg empty() {
        return new QEmptySeq(myBuilder);
    }

    /**
     *
     */
    public AstroArg seq(AstroArg first, AstroArg second) {
        return QPairSeq.run(myBuilder, (QAstroArg)first, (QAstroArg)second);
    }

    /**
     *
     */
    public boolean doesQuasis() {
        return true;
    }

    /**
     *
     */
    public Astro taggedHole(Astro ident, Astro functorHole) {
        return ((QHole)functorHole).asTagged(ident);
    }

    /**
     *
     */
    public AstroArg onlyChoice(AstroArg leftArg, AstroArg rightArg) {
        T.fail("XXX not yet implemented");
        return null; //make compiler happy
    }

    /**
     *
     */
    public AstroArg firstChoice(AstroArg leftArg, AstroArg rightArg) {
        T.fail("XXX not yet implemented");
        return null; //make compiler happy
    }

    /**
     *
     */
    public AstroArg range(Astro leftArg, Astro rightArg) {
        T.fail("XXX not yet implemented");
        return null; //make compiler happy
    }

    /**
     *
     */
    public AstroArg not(AstroArg test) {
        T.fail("XXX not yet implemented");
        return null; //make compiler happy
    }

    /**
     * @return
     */
    public AstroArg action(AstroArg syntactic, AstroArg semantic) {
        T.fail("XXX not yet implemented");
        return null; //make compiler happy
    }

    /**
     *
     */
    public AstroArg any() {
        T.fail("XXX not yet implemented");
        return null; //make compiler happy
    }

    /**
     * @return
     */
    public AstroArg anyOf(Astro litString) {
        T.fail("XXX not yet implemented");
        return null; //make compiler happy
    }

    /**
     *
     */
    public AstroArg interleave(AstroArg leftArg, AstroArg rightArg) {
        T.fail("XXX not yet implemented");
        return null; //make compiler happy
    }

    /**
     *
     */
    public AstroArg some(AstroArg optSub, char quant) {
        return new QSome(myBuilder,
                         (QAstroArg)optSub,
                         quant,
                         null == optSub ? null : optSub.getOptSpan());
    }

    /**
     *
     */
    public AstroArg some(AstroArg optSub, char quant, AstroArg sep) {
        T.fail("XXX not yet implemented");
        return null; //make compiler happy
    }

    /**
     * The dollar-hole that's returned is initially a term-hole, but may get
     * converted to a functor-hole by {@link QAstro#asFunctor()}.
     */
    public Astro dollarHole(Astro litInt) {
        int holeNum = ((Integer)litInt.getOptData()).intValue();
        return new QDollarHole(myBuilder,
                               null,
                               holeNum,
                               false,
                               litInt.getOptSpan());
    }

    /**
     * The at-hole that's returned is initially a term-hole, but may get
     * converted to a functor-hole by {@link QAstro#asFunctor()}.
     */
    public Astro atHole(Astro litInt) {
        int holeNum = ((Integer)litInt.getOptData()).intValue();
        return new QAtHole(myBuilder,
                           null,
                           holeNum,
                           false,
                           litInt.getOptSpan());
    }

    /**
     * complains
     *
     * @return
     */
    public Astro schema(AstroArg productions) {
        T.fail("Implemented only by a SchemaBuilder");
        return null; // make comiler happy
    }

    /**
     * complains
     *
     * @return
     */
    public Astro production(Astro lhs, AstroArg rhs) {
        T.fail("Implemented only by a SchemaBuilder");
        return null; // make comiler happy
    }
}
