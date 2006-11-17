package org.quasiliteral.astro;

import org.erights.e.elib.tables.ConstList;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

/**
 * The nodes of the kind of tree made by an {@link AstroBuilder}.
 * <p/>
 * A leaf node is simply an Astro with no arguments.
 * <p/>
 * A particular AstroBuilder will make Astros of some particular kind. For
 * example, an {@link org.quasiliteral.antlr.ASTBuilder ASTBuilder} makes Antlr
 * ASTs, a TermBuilder builds a Term tree, and a QuasiTermBuilder builds a
 * QuasiTerm tree.
 *
 * @author Mark S. Miller
 */
public interface Astro extends AstroArg {

    /**
     * Builds an Astro tree like this one, but the kind of Astro that's made by
     * this builder.
     * <p/>
     * Pattern-wise, the builder functions here as both as a factory, and
     * sort-of as a visitor. It's not quite a visitor, in that it only sees the
     * tree in bottom-up order.
     */
    Astro build(AstroBuilder builder);

    /**
     * Represents the token-type of the functor.
     */
    AstroTag getTag();

    /**
     * Equivalent to 'getTag().getOptTypeCode()'
     */
    short getOptTagCode();

    /**
     * If this Astro represents a literal-data token, then this is the data,
     * and getTag() must represent the canonical corresponding token-type for
     * this kind of data in this schema.
     * <p/>
     * If the getTag() has no schema, then its getTagName() must be the
     * corresponding token-type name.
     */
    Object getOptData();

    /**
     * A convenience equivalent to '((Twine)getOptData()).bare()'
     */
    String getOptString();

    /**
     * Equivalent to 'getArgs()[0].getOptData()'.
     * <p/>
     * This exists as an optimization for composite Tokens, which act like
     * 1-argument Astros, where the 1-argument is a data argument. This
     * operation allows us to avoid creating a singleton argument list just to
     * access this data.
     * <p/>
     * getOptArgData/1 should normally be used instead
     */
    Object getOptArgData();

    /**
     * Like getOptArgData/0, but first requires getOptTagCode() == tagCode.
     */
    Object getOptArgData(short tagCode);

    /**
     * A convenience equivalent to '((Twine)getOptArgData(tagCode)).bare()'
     */
    String getOptArgString(short tagCode);

    /**
     * @return :AstroArg[]; In most domains, AstroArg is the same as Astro, so
     *         this usually returns :Astro[].
     */
    ConstList getArgs();

    /**
     * An Astro just like this one, but without any arguments, which is
     * therefore a leaf (and so may be a Token).
     * <p/>
     * Even in the mutable AST domain, if this Astro has arguments,
     * 'withoutArgs()' makes a copy rather than modifying this Astro in place.
     */
    Astro withoutArgs();

//    /**
//     * Given that this Astro is a leaf (has no arguments), this returns an
//     * Astro just like it but with these arguments.
//     * <p>
//     * Even in the mutable AST domain, if 'args' is non-empty,
//     * 'withArgs(..)' makes a copy rather than modifying this Astro in place.
//     * <p>
//     * If this Astro is not a leaf, this throws an exception even if 'args' is
//     * empty.
//     */
//    Astro withArgs(ConstList args);
}
