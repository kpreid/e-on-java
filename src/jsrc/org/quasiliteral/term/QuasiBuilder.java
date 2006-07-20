package org.quasiliteral.term;

import org.quasiliteral.astro.Astro;
import org.quasiliteral.astro.AstroArg;
import org.quasiliteral.astro.AstroBuilder;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

/**
 * AstroBuilder extended with additional building methods for the expression
 * of quasi-literal tree expressions and patterns.
 * <p/>
 * These additional methods correspond to the quasi-oriented productions of
 * term.y. XXX Once this is converted to an Antlr-based term.g, then we
 * should use Antlr's support for grammar inheritance to split it up into
 * the base term.y and the derived quasiterm.y. The former can then use only
 * an AstroBuilder for building, and only the latter would require a
 * QuasiBuilder. At such a time, this class should move to the
 * org.quasiliteral.quasiterm package, as that will no longer screw up our
 * layering.
 *
 * @author Mark S. Miller
 */
public interface QuasiBuilder extends AstroBuilder {

    /**
     * A kludge because of the current (pre-Antlr) lack of grammar
     * inheritance.
     * <p/>
     * If this is false, then the only production which work are the ones
     * inherited from AstroBuilder, and this is really an AstroBuilder in
     * QuasiBuilder clothing. This flag is also, by default, used to tell
     * the lexer whether to collapse double quasi characters -- '@', '$', and
     * '`'.
     */
    boolean doesQuasis();

    /**
     *
     */
    Astro taggedHole(Astro ident, Astro functorHole);

    /**
     * Matches anything matched by either leftArg or rightArg.
     * <p/>
     * leftArg and rightArg must be disjoint choices -- it must be statically
     * impossible for anything to match both, though
     * XXX this is not yet enforced.
     * <p/>
     * Adds a "?" to the rank of both arguments.
     */
    AstroArg onlyChoice(AstroArg leftArg, AstroArg rightArg);

    /**
     * Matches anything matched by either leftArg or rightArg.
     * <p/>
     * leftArg and rightArg are prioritized choices in the sense of
     * "packrat parsing" and "Parsing Expression Grammars". If both match, then
     * the leftArg match is used.
     * <p/>
     * Adds a "?" to the rank of both arguments.
     */
    AstroArg firstChoice(AstroArg leftArg, AstroArg rightArg);

    /**
     * Matches any data element between these bounds.
     * <p/>
     * Both args must be data elements of the same type. For example,
     * <tt>'a'..'z'</tt> turns into <tt>b.range(term`'a'`, term`'z'`)</tt>.
     *
     * @return
     */
    AstroArg range(Astro leftArg, Astro rightArg);

    /**
     * If test would not match, then match consuming nothing.
     * <p/>
     * If test would match, then fail to match.
     * <p/>
     * This is a negative syntactic predicate in the sense of
     * "packrat parsing" and "Parsing Expression Grammars". ANTLR has something
     * similar.
     * <p/>
     * A positive syntactic predicate is just a double negative, i.e., a
     * <tt>!!&lt;test&gt;</tt> expression.
     *
     * @return
     */
    AstroArg not(AstroArg test);

    /**
     * @return
     */
    AstroArg action(AstroArg syntactic, AstroArg semantic);

    /**
     * Matches exactly one term, which can be anything.
     *
     * @return
     */
    AstroArg any();

    /**
     * Turns the string into a list of arguments representing each character
     * as a character literal.
     *
     * @return :Args
     */
    AstroArg anyOf(Astro litString);

    /**
     * Matches any sequence in which both leftArg and rightArg are matched
     * exactly once.
     * <p/>
     * Does not affect the rank of the arguments, since they are still
     * exactly-once.
     * <p/>
     * XXX rewrite para:
     * If 'leftArg' is itself a bag (the result of a call to interleave),
     * then the new effective bag is rightArg + the members of leftArg. This
     * matches any sequence in which each member of the sequence matches a
     * unique member of the bag, and all the members of the bag are matched.
     * <p/>
     * This is inspired by the '&' of <a href=
     * "http://www.thaiopensource.com/relaxng/nonxml/syntax.html"
     * >RELAX-NG's Non-XML Syntax</a>, which expands to <a href=
     * "http://www.thaiopensource.com/relaxng/design.html#section:12"
     * >RELAX-NG's "interleave" tag</a>, though we don't yet know how closely
     * the semantics of the two can correspond. (If 'interleave' can
     * correspond perfectly to RELAX-NG's interleave tag, then we will so
     * specify.)
     */
    AstroArg interleave(AstroArg leftArg, AstroArg rightArg);

    /**
     * Matches a consecutive sequence of Terms matched by optSub, where the
     * number matched is constrained by quant.
     * <p/>
     * Adds quant to the rank of optSub.
     */
    AstroArg some(AstroArg optSub, char quant);

    /**
     * Matches a consecutive sequence of Terms matched by optSub separated by
     * sep.
     * <p>
     * As with {@link #some(AstroArg, char)},  the number matched is
     * constrained by quant. Adds quant to the rank of optSub.
     */
    AstroArg some(AstroArg optSub, char quant, AstroArg sep);

    /**
     * On substitution, "evaluates" to 'args[litInt]'.
     * <p/>
     * On matching, matches if 'args[litInt] &lt;=&gt; specimen'
     */
    Astro dollarHole(Astro litInt);

    /**
     * Matches the specimen by placing it in 'bindings[litInt]'.
     */
    Astro atHole(Astro litInt);

    /**
     * Only for use when defining Schema
     *
     * @return
     */
    Astro schema(AstroArg productions);

    /**
     * Only for use when defining Schema
     *
     * @return
     */
    Astro production(Astro lhs, AstroArg rhs);
}
