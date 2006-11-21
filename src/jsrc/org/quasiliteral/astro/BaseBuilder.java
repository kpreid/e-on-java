package org.quasiliteral.astro;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.develop.assertion.T;
import org.erights.e.elib.base.SourceSpan;
import org.erights.e.elib.tables.Twine;
import org.erights.e.meta.java.lang.CharacterMakerSugar;
import org.erights.e.meta.java.math.EInt;

/**
 * @author Mark S. Miller
 */
public abstract class BaseBuilder implements AstroBuilder {

    /**
     *
     */
    private final AstroSchema mySchema;

    /**
     *
     */
    protected BaseBuilder(AstroSchema schema) {
        mySchema = schema;
    }

    /**
     *
     */
    public AstroSchema getSchema() {
        return mySchema;
    }

    /**
     * Just returns top
     */
    public AstroArg start(AstroArg top) {
        return top;
    }

    /**
     * Actually makes the kind of leaf this builder makes.
     * <p/>
     * This is '*Internal' and 'protected' because it assumes that the general
     * invariants among the arguments are already ensured. It is up to the
     * callers of leafInternal to ensure this.
     *
     * @param tag     Identifies a token type in a particular grammar or set of
     *                related grammars.
     * @param optData null, or something that promotes to a {@link Character},
     *                {@link EInt}, {@link Double}, or {@link Twine} presumably
     *                calculated from lexing this token. If not null, then if
     *                the tag is the corresponding literal-type-tag, then this
     *                is still a leaf. Otherwise, it's a composite.
     * @param optSpan Where this token was presumably extracted from.
     */
    protected abstract Astro leafInternal(AstroTag tag,
                                          Object optData,
                                          SourceSpan optSpan);

    /**
     * @return :AstroToken
     */
    public Astro leafTag(AstroTag tag, SourceSpan optSource) {
        return leafInternal(tag, null, optSource);
    }

    /**
     * Returns a complex token rather than a AST.
     *
     * @param data Nust not be null
     * @return :AstroToken
     */
    public Astro composite(AstroTag tag, Object data, SourceSpan optSpan) {
        data = AstroTag.optPromoteData(data);
        return leafInternal(tag, data, optSpan);
    }

    /**
     * @param data Must not be null
     * @return :AstroToken
     */
    public Astro leafData(Object data, SourceSpan optSpan) {
        data = AstroTag.optPromoteData(data);
        return leafInternal(mySchema.getTypeTag(data.getClass()),
                            data,
                            optSpan);
    }

    public Astro leafChar(char data, SourceSpan optSpan) {
        return leafInternal(mySchema.getLiteralCharTag(),
                            CharacterMakerSugar.valueOf(data),
                            optSpan);
    }

    public Astro leafLong(long data, SourceSpan optSpan) {
        return leafInternal(mySchema.getLiteralIntegerTag(),
                            EInt.valueOf(data),
                            optSpan);
    }

    public Astro leafInteger(Number integralData, SourceSpan optSpan) {
        return leafInternal(mySchema.getLiteralIntegerTag(),
                            integralData,
                            optSpan);
    }

    public Astro leafFloat64(double data, SourceSpan optSpan) {
        return leafInternal(mySchema.getLiteralFloat64Tag(),
                            new Double(data),
                            optSpan);
    }

    public Astro leafString(String data, SourceSpan optSpan) {
        return leafInternal(mySchema.getLiteralStringTag(),
                            Twine.fromString(data),
                            optSpan);
    }

    public Astro leafTwine(Twine data, SourceSpan optSpan) {
        return leafInternal(mySchema.getLiteralStringTag(), data, optSpan);
    }


    public Astro term(Astro functor) {
        return term(functor, empty());
    }

    public Astro namedTerm(String tagName, AstroArg args) {
        AstroTag tag = mySchema.obtainTagForName(tagName);
        return term(leafTag(tag, null), args);
    }

    public Astro tuple(AstroArg args) {
        return namedTerm(".tuple.", args);
    }

    public Astro bag(AstroArg args) {
        return namedTerm(".bag.", args);
    }

    public Astro attr(Astro key, Astro value) {
        return namedTerm(".attr.", seq(key, value));
    }

    public AstroArg seq(AstroArg first, AstroArg second, AstroArg third) {
        return seq(seq(first, second), third);
    }

    public AstroArg seq(AstroArg first,
                        AstroArg second,
                        AstroArg third,
                        AstroArg fourth) {
        return seq(seq(first, second, third), fourth);
    }

    /**
     *
     */
    public AstroArg unpack(Astro litChars) {
        T.require("LiteralChars" == litChars.getTag().getTagName(),
                  "Expected LiteralChars rather than ",
                  litChars);
        String str = ((Twine)litChars.getOptArgData()).bare();
        AstroArg result = empty();
        for (int i = 0, len = str.length(); i < len; i++) {
            result = seq(result, leafChar(str.charAt(i), null));
        }
        return result;
    }
}
