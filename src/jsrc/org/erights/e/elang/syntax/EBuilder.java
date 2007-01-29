package org.erights.e.elang.syntax;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.elang.evm.EExpr;
import org.erights.e.elang.evm.EMethod;
import org.erights.e.elang.evm.Evaluator;
import org.erights.e.elang.evm.LiteralExpr;
import org.erights.e.elang.evm.MetaContextExpr;
import org.erights.e.elang.evm.MetaStateExpr;
import org.erights.e.elang.evm.Pattern;
import org.erights.e.elang.evm.QuasiLiteralExpr;
import org.erights.e.elang.evm.QuasiLiteralPatt;
import org.erights.e.elang.evm.QuasiPatternExpr;
import org.erights.e.elang.evm.QuasiPatternPatt;
import org.erights.e.elang.evm.StaticScope;
import org.erights.e.elang.scope.ScopeLayout;
import org.quasiliteral.astro.Astro;

/**
 * @author Mark S. Miller
 */
public interface EBuilder extends BaseEBuilder {

    /**
     * The expression <tt>null</tt>.
     * <p/>
     * <tt>null</tt> is bound in the universal scope to the one null value. In
     * E-on-Java, this is the same as Java null.
     */
    EExpr getNULL();

    /**
     * The expression <tt>void</tt>.
     * <p/>
     * <tt>void</tt> is bound in the universal scope to a guard that coerces
     * all values to null.
     */
    EExpr getVOID();

    /**
     * The expression <tt>__break</tt>.
     * <p/>
     * The <tt>for</tt> and <tt>while</tt> loop expansions include an
     * <tt>escape&nbsp;__break&nbsp;{...</tt>, putting a variable of this name
     * into scope. The keyword <tt>break</tt> expands to a call to
     * <tt>__break</tt>.
     */
    EExpr get__BREAK();

    /**
     * The expression <tt>__continue</tt>.
     * <p/>
     * The <tt>for</tt> and <tt>while</tt> loop expansions include an
     * <tt>escape&nbsp;__continue&nbsp;{...</tt>, putting a variable of this
     * name into scope. The keyword <tt>continue</tt> expands to a call to
     * <tt>__continue</tt>.
     */
    EExpr get__CONTINUE();

    /**
     * The expression <tt>__return</tt>.
     * <p/>
     * The keyword <tt>return</tt> expands to a call to <tt>__return</tt>.
     */
    EExpr get__RETURN();

    /**
     * Makes an identifier token whose value is 'newStr', and whose source is
     * derived from 'poser'.
     * <p/>
     * 'poser' might be a String, an identifier token (an Astro), or a
     * CallExpr.<ul> <li>If it's a String, the source is empty. <li>If it's an
     * Astro, then the Astro's source is used. <li>If it's a CallExpr, then the
     * source is the expr's verb with the expr's source position. </ul>
     */
    Astro ident(Object poser, String newStr);

    /**
     * Expands <tt>lValue verb= rnValue</tt> to <tt>lValue :=
     * lValue.verb(rnValue)</tt>, sort of.
     * <p/>
     * When x is a variable name, <pre>
     *   x "verb=" z   expands to   x := x.verb(z)</pre>
     * Otherwise, we need to ensure that the lValue's subexpressions are only
     * evaluated once<pre>
     *  x.v(y...).verb= z</pre>
     * expands to<pre>
     *      def r := x
     *      def a0 := y
     *      ...
     *      r.v(a0...) := r.v(a0...).verb(z)</pre>
     * And then the {@link #assign assignment expansions} take it from there.
     */
    EExpr update(Object lValue, Object verb, Object rnValue);

    /**
     *
     */
    EExpr call(Object recipientExpr, Object poser, String verb, Object args);

    /**
     *
     */
    EExpr send(Object recipientExpr, Object verb, Object args);

    /**
     *
     */
    EExpr send(Object recipientExpr, Object poser, String verb, Object args);

    /**
     *
     */
    EExpr binop(Object recipientExpr, Object poser, String verb, Object arg);

    /**
     * Convenience for <tt>call(x, "not", list())</tt>.
     * <p/>
     * Makes the expression <tt>x.not()</tt>
     */
    EExpr not(Object poser, Object x);

    /**
     * Used for pseudo-immediate invocations of the <tt>meta</tt> or
     * <tt>pragma</tt> keywords.
     * <p/>
     * This is a bit of a syntactic hack, to enable the creation of new special
     * forms without extending the BNF of the language. Rather, if
     * <tt>meta</tt> or <tt>pragma</tt> appear as the receiver of what
     * otherwise would have been an immediate-call expression, then the builder
     * should dispatch on the verb and arity of the arguments to figure out
     * what to do.<ul> <li>'meta.getState()' evaluates to a reification of the
     * current environment. This is currently done by a kernel {@link
     * MetaStateExpr} expression, but can instead be done (and probably should
     * be done) by expanding the current scope into a map-creation expression.
     * This expansion would need to be done after scope analysis of course.
     * <li>'meta.context()' reifies the current {@link ScopeLayout}. This is
     * done by the kernel {@link MetaContextExpr}. <li>'meta.eval(Evaluator,
     * expr)' is reserved for future use. See {@link Evaluator}.
     * <li>'pragma.enable("<i>propName</i>")' changes the property
     * 'e.enable.<i>propName</i>' from 'allow' to 'true'. If it was already
     * 'true', this does nothing. If it was false, this throws an exception and
     * does not change the value. 'pragma.enable(..)' is used to enable the
     * corresponding feature on a per-compilation unit basis. See the
     * org/erights/e/elang/syntax/syntax-props-default.txt file for a list of
     * properties <li>'pragma.warn("<i>propName</i>")' sets the property
     * 'e.enable.<i>propName</i>' from 'true' to 'warn'. XXX explain "warn".
     * <li>'pragma.disable("<i>propName</i>")' sets the property
     * 'e.enable.<i>propName</i>' from 'true' to 'allow'. If it was already
     * 'allow' or 'false', this does nothing. 'pragma.disable(..)' is used to
     * disable the corresponding feature on a per-compilation unit basis. </ul>
     * All other possibilities are reserved for future use.
     */
    EExpr doMeta(Object keyword, Object verb, Object args);

    /**
     *
     */
    EExpr doMeta(Object keyword, Object poser, String verb, Object args);

    /**
     * Used for pseudo-eventual invocations of the <tt>meta</tt> or
     * <tt>pragma</tt> keywords.
     * <p/>
     * This is a bit of a syntactic hack, to enable the creation of new special
     * forms without extending the BNF of the language. Rather, if
     * <tt>meta</tt> or <tt>pragma</tt> appear as the receiver of what
     * otherwise would have been an eventual-send expression, then the builder
     * should dispatch on the verb and arity of the arguments to figure out
     * what to do.<ul> <li>'meta &lt;- eval({@link Evaluator}, expr)' is
     * reserved for future use. See {@link Evaluator}. </ul> All other
     * possibilities are reserved for future use.
     */
    EExpr doMetaSend(Object keyword, Object verb, Object args);

    /**
     *
     */
    EExpr doMetaSend(Object keyword, Object poser, String verb, Object args);

    /**
     * Makes, for example, <tt>meta::context</tt> be equivalent to
     * <tt>meta.getContext()</tt>
     */
    EExpr doMetaProp(Object keyword, Object propName);

    /**
     *
     */
    EExpr kdef(Object pattern, Object rValue);

    /**
     *
     */
    EExpr define(Object pattern, Object rValue);

    /**
     *
     */
    EExpr define(Object pattern, Object optEjectorExpr, Object rValue);

    /**
     * When a dollar-hole is '$<ident>'
     */
    EExpr dollarNoun(Object token);

    /**
     * A forward declaration. <p>
     * <p/>
     * "def name" expands to <pre>
     *     (def [name, name__Resolver] := Ref.promise(); name__Resolver)
     * </pre>
     * The value of a "def name" expression is the Resolver that will resolve
     * 'name', so, for example, '(def name)' can be used in an argument
     * position both to define 'name' and to pass the Resolver as an argument
     * to someone who will resolve it.
     */
    EExpr forward(Object identOrStr);

    /**
     *
     */
    EExpr escape(Object pattern, Object bodyExpr, Object optCatcher);

    /**
     *
     */
    EExpr ejector(Object ejNoun);

    /**
     *
     */
    EExpr ejector(Object ejNoun, Object valExpr);

    /**
     * The for loop has the following expansion:
     * <pre>
     *  for [kPattern =&gt;] vPattern in collExpr { mBody }</pre>
     * expands to
     * <pre>
     *  escape __break {
     *      var validFlag := true
     *      try {
     *          collExpr.iterate(def _(k, v) :any {
     *              require(validFlag,
     *                      "For-loop body isn't valid after for-loop exits.")
     *              if (k =~ kPattern && v =~ vPattern) {
     *                  escape __continue { mBody; null }
     *              }
     *          })
     *          null
     *      } finally {
     *          validFlag := false
     *      }
     *  }</pre>
     *
     * @param assoc      An {@link Assoc} of kPattern and a vPattern.
     * @param collExpr   Evaluates to the collection to be iterated (to be sent
     *                   the "iterate" message).
     * @param mBody      The body of the for-loop.
     * @param optCatcher The optional
     *                   <pre>    &quot;catch&quot; <i>pattern</i
     *                   <p/>
     *                   <p/>
     *                                       >
     *                   <p/>
     *                                                       &quot;{&quot;
     *
     *                   <i>eExpr</i>
     *                   <p/>
     *
     *                   &quot;}&quot;</pre>
     *                   following the for loop for receiving the break
     *                   argument.
     * @return The expression for computing the for-loop.
     * @see <a href= "https://bugs.sieve.net/bugs/?func=detailbug&bug_id=125606&group_id=16380"
     *      >'for' loop security concerns</a>
     */
    EExpr forx(Object assoc, Object collExpr, Object mBody, Object optCatcher);

    /**
     * Implements the "accumulator" syntax.
     * <p/>
     * The "accumulator" syntax has most of the advantages of list
     * comprehension (from Haskell and Python), but is more E-like. For
     * example, with this property enabled, one can write:
     * <pre>
     *     accum [] for i in 1..5 { _ + i**2 }
     * </pre>
     * to accumulate the list [ 1, 4, 9, 16, 25 ]. This expands into
     * <pre>
     * {
     *     var accum_n := []
     *     for i in 1..5 {
     *         accum_n += { i**2 }
     *     }
     *     accum_n
     * }
     * </pre>
     * Note that this is more flexible but more verbose than a list
     * comprehension, and less flexible and comparably verbose to a Smalltalk
     * injection.
     * <p/>
     * More formally, the transformation is from
     * <pre>
     *     "accum" starterExpr accumulator
     * </pre> to <pre>
     * {
     *     var accum_n := starter-expr
     *     accumExpr
     *     accum_n
     * }
     * </pre>
     * where accumulator is one of {@link #accumFor}, {@link #accumIf}, or
     * {@link #accumWhile}. They each have an accumBody parameter. The
     * corresponding argument is one of {@link #accumFor}, {@link #accumIf},
     * {@link #accumWhile}, or {@link #accumBody}, and returns a pair of the
     * name of the temporary variable (shown as "accum-n" above) and an
     * expression (shown as "accumExpr" above).
     *
     * @param starterExpr The initial value to be accumulated on to.
     * @param accumulator A pair of a temp-name and an accumExpr.
     * @return An expression that evaluates to the accumulated value.
     */
    EExpr accumulate(Object starterExpr, Object accumulator);

    /**
     * Use a for-loop to {@link #accumulate} for each member of a collection.
     *
     * @param assoc     An {@link Assoc} of a key-pattern and a value-pattern.
     * @param collExpr  Evaluates to the collection to be iterated (to be sent
     *                  the "iterate" message).
     * @param accumBody A pair of a temp-name and an accumExpr.
     * @return A pair of a temp-name and the obvious for-loop EExpr.
     */
    Object[] accumFor(Object assoc, Object collExpr, Object accumBody);

    /**
     * Use an if-expr to {@link #accumulate} iff a condition is true.
     */
    Object[] accumIf(Object condExpr, Object accumBody);

    /**
     * Use a while-loop to {@link #accumulate} while a condition is true.
     */
    Object[] accumWhile(Object condExpr, Object accumBody);

    /**
     * Actually does the accumulation into the temporary accumulation
     * variable.
     *
     * @param verb    The messageName that says how the temporary should be
     *                updated.
     * @param rnValue The argument-list for computing a new value.
     * @return A pair of a new temp-name and the expression
     *         <pre>    temp-name verb= rnValue</pre>
     */
    Object[] accumBody(Object verb, Object rnValue);

    /**
     *
     */
    EExpr ifx(Object condExpr, Object thenExpr);

    /**
     *
     */
    EExpr lessThan(Object x, Object poser, Object y);

    /**
     *
     */
    EExpr leq(Object x, Object poser, Object y);

    /**
     *
     */
    EExpr asBigAs(Object x, Object poser, Object y);

    /**
     *
     */
    EExpr geq(Object x, Object poser, Object y);

    /**
     *
     */
    EExpr greaterThan(Object x, Object poser, Object y);

    /**
     *
     */
    EExpr till(Object start, Object poser, Object bound);

    /**
     *
     */
    EExpr thru(Object start, Object poser, Object stop);

    /**
     * [a => b, c => d]  expands to  __makeMap fromPairs([[a, b], [c, d]).
     * <p/>
     * This exapansion satisfies the requirement that it preserves order.
     */
    EExpr map(Object assocList);

    /**
     * '["k" => p            ] | r' to 'via (__extract("k")) [p, r]' '["k" => p
     * default {e}] | r' to 'via (__extract("k", fn {e})) [p, r]' '["k" => p :=
     * e ] | r' to 'via (__extract.depr("k", e)) [p, r]' </pre> Note that the
     * ':=' syntax for default values is deprecated. Should this be removed,
     * the depr/2 method may be removed as well.
     * <p/>
     * When the original has no rest pattern, the expanded rest pattern is
     * ':__Empty'.
     * <p/>
     * XXX At some future time, it is probably worth making the expansion more
     * complex in order to handle an entire map-pattern all at once.
     */
    Pattern mapPattern(Object assocList, Object optRestPatt);

    /**
     * Represents a key => value association.
     * <p/>
     * It depends on context whether either key of value is an expression or a
     * pattern.
     */
    Assoc assoc(Object key, Object value);

    /**
     * '=> nounExpr'  expands to  '"varName" => nounExpr'
     */
    Assoc exporter(Object nameExpr);

    /**
     * '=> namerPatt' expands to '"varName" => namerPatt' and '=> (expr) :
     * namerPatt' expands to '"varName" => (expr) : namerPatt'
     */
    Assoc importer(Object namerPatt);

    /**
     *
     */
    EExpr mod(Object val, Object poser, Object nModulus);

    /**
     *
     */
    EExpr doco(Object doco, Object oDecl);

    /**
     *
     */
    MsgPatt methHead(Object poser,
                     String verb,
                     Object patts,
                     Object optResultGuard);

    /**
     *
     */
    EMethod to(Object doco, Object msgPatt, Object bodyExpr);

    /**
     * For defining an eScript that consists of exactly one method
     */
    ObjDecl methDecl(Object msgPatt, Object bodyExpr, boolean bindReturn);

    /**
     * Expands to
     * <pre>
     *     def _(params,...) :any { bodyExpr }
     * </pre>
     */
    ObjDecl fnDecl(Object poser, Object params, Object bodyExpr);

    /**
     * Expands to
     * <pre>
     *     recip.sep__control_n(fn params { body })
     * </pre>
     */
    EExpr control(Object recip, Object sep, Object params, Object body);

    /**
     * Expands to
     * <pre>
     *     recip.sep__control_n_m(fn {[[args], fn params {body}]})
     * </pre>
     */
    EExpr control(Object recip,
                  Object sep,
                  Object args,
                  Object params,
                  Object body);

    /**
     * What to do when no result guard was explicitly written.
     * <p/>
     * As of 0.8.23g, this still defaults to e`void`, but is now switchable. At
     * a later time, the default will switch to null, which acts like e`any`.
     */
    EExpr defaultOptResultGuard(Object poser);

    /**
     * What to do when no when-result guard was explicitly written.
     * <p/>
     * This still defaults to e`void`, but is now switchable. With easy-return,
     * the default as of 0.9, this returns null, which acts like e`any`.
     */
    EExpr defaultOptWhenGuard(Object poser);

    /**
     * (== eExpr)  expands to   via (__is(eExpr)) _
     */
    Pattern patternEquals(Object eExpr);

    /**
     * When an at-hole is '@<ident>' or '@_'
     */
    Pattern atNoun(Object token);

    /**
     * syntax`foo $bar baz $zip zorp` expands to syntax_quasiParser.
     * valueMaker("foo ${0} baz ${1} zorp").substitute([bar, zip])
     */
    EExpr quasiExpr(Object syntax, Object quasiList);

    /**
     * $$ expands to ${0}
     */
    QuasiLiteralExpr quasiLiteralExpr();

    /**
     *
     */
    QuasiLiteralExpr quasiLiteralExpr(Object optLitIndex);

    /**
     *
     */
    QuasiLiteralPatt quasiLiteralPatt(Object litIndex);

    /**
     * The pattern syntax`foo $bar baz @zip zap` expands to via (__matchBind(
     * syntax__quasiParser.matchMaker("foo ${0} baz @{0} zap"), [bar])) [zip]
     */
    Pattern quasiPattern(Object syntax, Object quasiList);

    /**
     *
     */
    QuasiPatternExpr quasiPatternExpr(Object litIndex);

    /**
     *
     */
    QuasiPatternPatt quasiPatternPatt(Object litIndex);

    /**
     *
     */
    EExpr same(Object x, Object poser, Object y);

    /**
     *
     */
    EExpr sequence(Object x, Object y);

    /**
     *
     */
    EExpr sequence(Object x, Object y, Object z);

    /**
     * switch (eExpr) { match pattern1 { body1 } match pattern2 { body2 } }
     * expands to { def temp = eExpr if (temp =~ pattern1) { body1 } else if
     * (temp =~ pattern2) { body2 } else { throw("no match: " + temp) } }
     */
    EExpr switchx(Object specimen, Object matchers);

    /**
     * [x, y, z]       expands to  __makeList(x, y, z)
     */
    EExpr tuple(Object argList);

    /**
     * uriToken must be a URI, and have both protocol and body.
     * <p/>
     * &lt;http:foo&gt; expands to http__uriGetter["foo"]. &lt;x:foo&gt;
     * expands to file__uriGetter["x:foo"].
     */
    EExpr uriExpr(Object uriToken);

    /**
     * The while loop expands as follows:
     * <pre>
     *  while (cond) { body }</pre>
     * expands to
     * <pre>
     *  escape __break {
     *      __loop( def _() :boolean {
     *          if (cond) {
     *              escape __continue { body }
     *              true
     *          } else {
     *              false
     *          }
     *      })
     *  }</pre>
     */
    EExpr whilex(Object condExpr, Object bodyExpr, Object optCatcher);

    /**
     *
     */
    EExpr oType(Object doco,
                Object optOName,
                Object typeParams,
                Object mTypes);

    /**
     *
     */
    EExpr oType(Object doco,
                Object optOName,
                Object typeParams,
                Object optAudit,
                Object decl,
                Object mTypes);

    /**
     *
     */
    EExpr mType(Object doco, Object verb, Object pTypes, Object optRetType);

    /**
     *
     */
    EExpr pType(Object optName, Object optType);

    /**
     * Make vacancies for new temporary variable names.
     *
     * @param ident An Astro representing the identifier as it appears in the
     *              source code.
     * @return The variable name as far as the program is concerned. So long as
     *         ident doesn't have the form of a temporary variable name, this
     *         will be the same as ident.
     */
    Astro varName(Object ident);

    /**
     * Mangles an identifier into a variable name by appending a known suffix.
     *
     * @param ident An Astro representing the identifier as it appears in the
     *              source code.
     * @return The resulting variable name for use in the program.
     */
    Astro mangle(Object ident, String suffix);

//    /**
//     * Used to transform 'a.name' into 'a.getName(args)'.
//     * <p>
//     * 'name' is changed into 'Name' using the standard JavaBeans rule:
//     * 'toUpperCase' is applied to the name[0].
//     * <p>
//     * As a result of the assignment conversion rules,
//     * 'a.name := y' is trasformed into 'a.setName(y)'
//     *
//     * @param propName An Astro representing the identifier as it appears
//     *        in the source code.
//     * @return The resulting verb for use in the program.
//     */
//    public Astro prop(Object propName);

    /**
     *
     */
    EExpr when(Object exprs, Object poser, Object tailList);

    /**
     *
     */
    EExpr whenSeq(Object exprs, Object poser, Object whenRest);

    /**
     *
     */
    EExpr callFacet(Object recipientExpr, Object vcurry);

    /**
     *
     */
    EExpr sendFacet(Object recipientExpr, Object vcurry);

    /**
     *
     */
    EExpr propSlot(Object recipientExpr, Object propName);

    /**
     *
     */
    EExpr propValue(Object recipientExpr, Object propName);

    /**
     *
     */
    EExpr sendPropSlot(Object recipientExpr, Object propName);

    /**
     *
     */
    EExpr sendPropValue(Object recipientExpr, Object propName);

    /**
     *
     */
    LiteralExpr litStr(Object optName);

    /**
     * '[a, b] + rest' expands to 'via (__splitList(2)) [a, b, rest]'
     */
    Pattern cdrPattern(Object subs, Object rest);

    /**
     * 'p ? e' expands to 'via (__suchThat) [p, via (__suchThat(e)) _]'
     */
    Pattern suchThat(Object pattern, Object condExpr);

    /**
     *
     */
    Pattern finalPattern(Object atom);

    /**
     *
     */
    Pattern varPattern(Object atom);

    /**
     *
     */
    Pattern slotPattern(Object atom);

    /**
     *
     */
    Pattern ignore();

    /**
     * Binds (resolves) a forward declaration.
     * <p/>
     * "bind foo" expands to <pre>
     *     via (__bind(foo__Resolver)) _
     * </pre>
     */
    Pattern bindDefiner(Object identOrStr);

    /**
     * Binds (resolves) a forward declaration. <p>
     * <p/>
     * "bind name :int" expands to <pre>
     *     via (__bind(name__Resolver, int)) _
     * </pre>
     */
    Pattern bindDefiner(Object identOrStr, Object optGuardExpr);

    /**
     *
     */
    Pattern[] finalOName(Object atom);

    /**
     *
     */
    Pattern[] varOName(Object atom);

    /**
     *
     */
    Pattern[] ignoreOName();

    /**
     * Like {@link #bindDefiner(Object)}, but when used as the oName of an
     * object definition expression, causes
     * <pre>    bind foo {}</pre>
     * to expand to
     * <pre>    def via (__bind(foo__Resolver)) _ := {def foo {}}</pre>
     * rather than
     * <pre>    def via (__bind(foo__Resolver)) _ := def _ {}</pre>
     */
    Pattern[] bindOName(Object identOrStr);

    /**
     * Expands to
     * <pre>    Guard.coerce(guardExpr, null).coerce(expr, null)</pre>
     * XXX Perhaps in a match-bind context, the nulls should instead be the
     * current ejector, which we'd need to reify.
     */
    EExpr cast(Object expr, Object poser, Object guardExpr);

    /**
     * @see DelayedExpr#forControl(ENodeBuilder,Astro,StaticScope)
     */
    EExpr condAnd(Object left, Object poser, Object right);

    /**
     * @see DelayedExpr#forControl(ENodeBuilder,Astro,StaticScope)
     */
    EExpr condOr(Object left, Object poser, Object right);

    /**
     * @see DelayedExpr#forControl(ENodeBuilder,Astro,StaticScope)
     */
    EExpr matchBind(Object specimen, Object poser, Object pattern);

    /**
     *
     */
    Pattern callPattern(Object target,
                        Object poser,
                        String verb,
                        Object params);

    /**
     *
     */
    Pattern callPattern(Object target, Object verb, Object params);
}
