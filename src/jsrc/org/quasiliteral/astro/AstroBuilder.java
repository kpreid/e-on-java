package org.quasiliteral.astro;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.elib.base.SourceSpan;
import org.erights.e.elib.tables.Twine;

/**
 * Used to build an Astro tree -- a tree that could represent the results of
 * parsing a string according to a grammar.
 * <p/>
 * Several arguments or return values below are declared 'Object', but
 * pseudo-declared ":Args" in the javadoc comments. For these, the concrete
 * type represents a list of {@link AstroArg}s, but the particular type used to
 * represent the list depends on the type of builder, and should be documented
 * in that builder's class comment.
 *
 * @author Mark S. Miller
 */
public interface AstroBuilder {

    /**
     * Describes the grammar whose trees we're building.
     */
    AstroSchema getSchema();

    /**
     * When we're finished building, this should be applied to the root node.
     * <p/>
     * It is normally just an identity function, but allows, for example,
     * post-parsing cleanup. This is called by the parser, but not currently by
     * the 'build' or 'qbuild' methods of other trees.
     */
    AstroArg start(AstroArg top);

    /**
     * Makes a leaf (eg, a Token) not representing literal data, but just a
     * token tag (token type indicator) as identified by 'tag'.
     * <p/>
     * The tag of the returned Astro should be according to the schema of this
     * builder, even though the argument tag may be in a different schema. In
     * this case, the correspondence is by tag name, rather than tag code.
     */
    Astro leafTag(AstroTag tag, SourceSpan optSource);

    /**
     * Makes an Astro whose tag is according to 'tag', and whose one argument
     * is a leaf according to 'data'.
     * <p/>
     * Is equivalent to:<pre>
     *     term(leafTag(typeCode, src),
     *          term(leafData(data, src)))
     * </pre>
     * Exists to support legacy usage of Antlr Tokens, and in order to allow
     * lexers, which wish to emit composites, to still emit only Tokens if
     * allowed by the concrete builder.
     */
    Astro composite(AstroTag tag, Object data, SourceSpan optSpan);

    /**
     * Makes a leaf (eg, a Token) representing literal data.
     * <p/>
     * Once coerced, this is equivalent to one of the following leaf* methods,
     * which are provided for efficiency
     */
    Astro leafData(Object data, SourceSpan optSpan);

    /**
     * A Unicode character
     */
    Astro leafChar(char data, SourceSpan optSpan);

    /**
     * Note that leafLong's data is a subrange of leafInteger's.
     * <p/>
     * The former is limited to 64 bits, and the latter isn't limited.
     */
    Astro leafLong(long data, SourceSpan optSpan);

    /**
     * A precision unlimited integer
     */
    Astro leafInteger(Number integralData, SourceSpan optSpan);

    /**
     * An IEEE double precision floating point number
     */
    Astro leafFloat64(double data, SourceSpan optSpan);

    /**
     * A String is just a bare twine
     */
    Astro leafString(String data, SourceSpan optSpan);

    /**
     * A list of characters with optional (ie, if not bare) annotation about
     * where (source position) these characters came from.
     */
    Astro leafTwine(Twine data, SourceSpan optSpan);

    /**
     * Returns an internal node (eg, an AST or Term), for which 'functor' is
     * the functor and 'args' are the arguments.
     * <p/>
     * 'functor' must be a leaf (have no arguments).
     * <p/>
     * When parsing the term syntax, this is called if the empty args list
     * appears explicitly, but not if it's left out. When usad as a quasi-term
     * pattern, this allows us to distinguish between a) 'term`@foo`' and b)
     * 'term`@foo()`'. #a matches any term, while #b matches only zero-arity
     * terms. Note that c) 'term`@foo(*)`' or d) 'term`@foo(@args*)`' will also
     * match any term, but in these cases, 'foo' will only be bound to the
     * functor of the specimen, not, as in #a, the specimen as a whole. (In #c,
     * the arguments are matched and ignored. In #d, 'args' is bound to the
     * list of arguments.)
     * <p/>
     * Similarly, this allows us to distinguish between the expressions e)
     * 'term`$foo`' and f) 'term`$foo()`'. On success, both #e and #f mean the
     * same thing. But #f additionally required the term bound to 'foo' to be
     * zero-arity, or an exception will be thrown.
     */
    Astro term(Astro functor, AstroArg args);

    /**
     * For when a functor is used as a term without being followed by an
     * explicit '()'.
     * <p/>
     * For literal Astros, this is equivalent to 'term(functor, list())', but
     * is separate for support of quasi-literals, and explained at {@link
     * #term(Astro,AstroArg)}.
     */
    Astro term(Astro functor);

    /**
     * For supporting the square bracket shorthand for terms that represent
     * tuples.
     * <p/>
     * This is equivalent to 'term(leafTag(".tuple."), args)', although an
     * individual builder/schema may have its own idea about what tagName to
     * use to mark a tuple.
     */
    Astro tuple(AstroArg args);

    /**
     * For supporting the curly bracket shorthand for terms that represent
     * named fields or attributes.
     * <p/>
     * This is equivalent to 'term(leafTag(".bag."), args)', although an
     * individual builder/schema may have its own idea about what tagName to
     * use to mark a tuple.
     */
    Astro bag(AstroArg args);

    /**
     * For supporting the ':' shorthand for a term that represents a named
     * field or attribute.
     * <p/>
     * This is equivalent to
     * <pre>    term(leafTag(".attr."), term(functor, value))</pre>
     * although an individual builder/schema may have its own idea about what
     * tagName to use to mark an attribute.
     */
    Astro attr(Astro functor, AstroArg value);

    /**
     * The empty args list
     *
     * @return :Args
     */
    AstroArg empty();

    /**
     * The two-argument args list
     */
    AstroArg seq(AstroArg first, AstroArg second);

    /**
     * The three-argument args list
     */
    AstroArg seq(AstroArg first, AstroArg second, AstroArg third);

    /**
     * The four-argument args list
     */
    AstroArg seq(AstroArg first,
                 AstroArg second,
                 AstroArg third,
                 AstroArg fourth);

    /**
     * Turns the LiteralChars into a list of arguments representing each
     * character as a single character literal.
     *
     * @return :Args
     */
    AstroArg unpack(Astro litChars);
}
