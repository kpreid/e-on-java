package org.quasiliteral.term;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.develop.assertion.T;
import org.erights.e.elib.base.SourceSpan;
import org.erights.e.elib.tables.Twine;
import org.quasiliteral.astro.Astro;
import org.quasiliteral.astro.AstroArg;
import org.quasiliteral.astro.AstroBuilder;
import org.quasiliteral.astro.AstroSchema;
import org.quasiliteral.astro.AstroTag;

/**
 * Wraps an {@link AstroBuilder} to pretend to be of type {@link
 * QuasiBuilder}.
 * <p/>
 * This kludge exists only until we switch to Antlr, and make use of its
 * support for grammar inheritance. Until then, the one grammar, term.y, must
 * support both literal and quasi-literal term trees, so it is therefore
 * defined in terms of the larger subtype -- QuasiBuilder.
 *
 * @author Mark S. Miller
 */
public class QuasiBuilderAdaptor implements QuasiBuilder {

    /**
     * Builds Term trees according to the term.y grammar
     */
    static public final QuasiBuilder FOR_TERMS =
      new QuasiBuilderAdaptor(TermBuilder.FOR_TERMS);

//    /**
//     * Builds ASTs according to the term.y grammar
//     */
//    static public final QuasiBuilder FOR_ASTS =
//      new QuasiBuilderAdaptor(TermBuilder.FOR_ASTS);

    private final AstroBuilder myBuilder;

    public QuasiBuilderAdaptor(AstroBuilder builder) {
        myBuilder = builder;
    }

    /**
     *
     */
    public String toString() {
        return "<adapting " + myBuilder.toString() + ">";
    }

    public AstroSchema getSchema() {
        return myBuilder.getSchema();
    }

    public AstroArg start(AstroArg top) {
        return myBuilder.start(top);
    }

    public Astro leafTag(AstroTag tag, SourceSpan optSource) {
        return myBuilder.leafTag(tag, optSource);
    }

    public Astro composite(AstroTag tag, Object data, SourceSpan optSpan) {
        return myBuilder.composite(tag, data, optSpan);
    }

    public Astro leafData(Object data, SourceSpan optSpan) {
        return myBuilder.leafData(data, optSpan);
    }

    public Astro leafChar(char data, SourceSpan optSpan) {
        return myBuilder.leafChar(data, optSpan);
    }

    public Astro leafLong(long data, SourceSpan optSpan) {
        return myBuilder.leafLong(data, optSpan);
    }

    public Astro leafInteger(Number integralData, SourceSpan optSpan) {
        return myBuilder.leafInteger(integralData, optSpan);
    }

    public Astro leafFloat64(double data, SourceSpan optSpan) {
        return myBuilder.leafFloat64(data, optSpan);
    }

    public Astro leafString(String data, SourceSpan optSpan) {
        return myBuilder.leafString(data, optSpan);
    }

    public Astro leafTwine(Twine data, SourceSpan optSpan) {
        return myBuilder.leafTwine(data, optSpan);
    }

    public Astro term(Astro functor, AstroArg args) {
        return myBuilder.term(functor, args);
    }

    public Astro term(Astro functor) {
        return myBuilder.term(functor);
    }

    public Astro namedTerm(String tagName, AstroArg args) {
        return myBuilder.namedTerm(tagName, args);
    }

    public Astro tuple(AstroArg args) {
        return myBuilder.tuple(args);
    }

    public Astro bag(AstroArg args) {
        return myBuilder.bag(args);
    }

    public Astro attr(Astro key, Astro value) {
        return myBuilder.attr(key, value);
    }

    public AstroArg empty() {
        return myBuilder.empty();
    }

    public AstroArg seq(AstroArg first, AstroArg second) {
        return myBuilder.seq(first, second);
    }

    public AstroArg seq(AstroArg first, AstroArg second, AstroArg third) {
        return myBuilder.seq(first, second, third);
    }

    public AstroArg seq(AstroArg first,
                        AstroArg second,
                        AstroArg third,
                        AstroArg fourth) {
        return myBuilder.seq(first, second, third, fourth);
    }

    public AstroArg unpack(Astro litChars) {
        return myBuilder.unpack(litChars);
    }

    public boolean doesQuasis() {
        return false;
    }

    public Astro taggedHole(Astro ident, Astro functorHole) {
        T.fail("not quasi-ing");
        return null; //make compiler happy
    }

    public AstroArg onlyChoice(AstroArg leftArg, AstroArg rightArg) {
        T.fail("not quasi-ing");
        return null; //make compiler happy
    }

    public AstroArg firstChoice(AstroArg leftArg, AstroArg rightArg) {
        T.fail("not quasi-ing");
        return null; //make compiler happy
    }

    public AstroArg range(Astro leftArg, Astro rightArg) {
        T.fail("not quasi-ing");
        return null; //make compiler happy
    }

    public AstroArg not(AstroArg test) {
        T.fail("not quasi-ing");
        return null; //make compiler happy
    }

    public AstroArg action(AstroArg syntactic, AstroArg semantic) {
        T.fail("not quasi-ing");
        return null; //make compiler happy
    }

    public AstroArg any() {
        T.fail("not quasi-ing");
        return null; //make compiler happy
    }

    public AstroArg anyOf(Astro litString) {
        T.fail("not quasi-ing");
        return null; //make compiler happy
    }

    public AstroArg interleave(AstroArg leftArg, AstroArg rightArg) {
        T.fail("not quasi-ing");
        return null; //make compiler happy
    }

    public AstroArg some(AstroArg optSub, char quant) {
        T.fail("not quasi-ing");
        return null; //make compiler happy
    }

    public AstroArg some(AstroArg optSub, char quant, AstroArg sep) {
        T.fail("not quasi-ing");
        return null; //make compiler happy
    }

    public Astro dollarHole(Astro litInt) {
        T.fail("not quasi-ing");
        return null; //make compiler happy
    }

    public Astro atHole(Astro litInt) {
        T.fail("not quasi-ing");
        return null; //make compiler happy
    }

    public Astro schema(AstroArg productions) {
        T.fail("not quasi-ing");
        return null; //make compiler happy
    }

    public Astro production(Astro lhs, AstroArg rhs) {
        T.fail("not quasi-ing");
        return null; //make compiler happy
    }
}
