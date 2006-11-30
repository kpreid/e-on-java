package org.erights.e.elang.evm;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.elib.tables.ConstMap;

/**
 * Evaluates and matches <a href= "http://www.erights.org/elang/kernel/index.html"
 * >Kernel-E</a> expressions and patterns explicitly, so that this evaluation
 * can happen, for example, remotely.
 * <p/>
 * XXX This section is now obsolete, as the concept of SlotGuards has
 * disappeared.
 * <p/>
 * Evaluators also support the E syntactic sugar
 * <pre>    meta.eval(evaluator, expr)</pre>
 * and
 * <pre>    meta &lt;- eval(evaluator, expr)</pre>
 * as a convenience for evaluating the expression in the lexical scope of the
 * caller.
 * <p/>
 * E is an expression/pattern language. This means that execution proceeds by
 * evaluating expressions in a lexical scope, and matching patterns in a
 * lexical scope against a specimen (an arbitrary value which the pattern may
 * match). Expressions and patterns may recursively contain both expressions
 * and patterns. The result of matching a pattern against a specimen is not
 * just an answer -- "Did the specimen match?" -- but also bindings of values
 * (typically extracted from the specimen) to variables named in the pattern.
 * Indeed, this is the only form of variable definition in E.
 * <p/>
 * An E program is an fully ordered left-to-right tree of expressions and
 * patterns, with a set of nested <a href="http://www.erights.org/elang/blocks/ifExpr.html"
 * >scope boxes</a> statically imposed on this tree by particular types of
 * expressions and patterns. When a pattern defines a variable, this name is in
 * scope left to right from the point of definition until the end of its
 * containing scope box.
 * <p/>
 * Putting it all together, a node (an expression or pattern) has two
 * scope-based interfaces to the outside world: <p> <ul>
 * <li><p><i>bindingsIn</i> are the those variables used freely by the node --
 * those that it uses but doesn't define, and that therefore must be in scope
 * where the node starts. We exempt from bindingsIn the {@link
 * org.erights.e.elang.interp.ScopeSetup#NonShadowable NonShadowable} elements
 * of the safeScope. </p> <li><p><i>bindingsOut</i> are those variables defined
 * by the node that are still in scope following the node, and that may be
 * needed by code to the right of the node within the same enclosing scope box.
 * In a static compilation context, this should only include variables that
 * actually are used by a node to the right of the present node.</p> </ul> In
 * one crucial way, the Evaluator is less general than the notion of evaluation
 * defined by Kernel-E: To accomodate remote execution, bindingsIn and
 * bindingsOut must represent only final variables, since they are a ConstMap
 * mapping these names to values, rather than settable locations. While this
 * difference is visible to the Kernel-E programmer, it can be made largely
 * invisible to the E programmer. Except for variables recognized to fall into
 * some special cases, the Deslotifying compilation phase transforms, for
 * example, the mutable variable "foo" into corresponding final variable
 * "foo__Slot", bound to an explicit Slot object holding the value of the "foo"
 * variable. Use of "foo" as a rValue then becomes "foo__Slot.get()" and
 * assignment becomes "foo__Slot.put(newValue)". Except for outer (ie,
 * top-level) mutable scopes (used in the interactive read-eval-print loop,
 * "rune"), all non-final variables and assignment statements could have been
 * transformed away this way. (We chose not to in general in order to better
 * support compilation, and because of the needs of the interactive
 * read-eval-print loop.)  However, when transforming for use with an
 * Evaluator, all the in and out variables must be so transformed.
 * <p/>
 * The syntactic sugar <pre>
 *     meta.eval(evaluator, expr)
 * </pre>
 * expands into a call to <tt>evaluator</tt> to evaluate the provided
 * expression in the lexical context of this sugar. Assuming the evaluator
 * evaluates the expression according to the normal rules, then the expression
 * evaluation should have the same effects it normally would have had. The
 * Deslotifying transformation of the E compiler must turn all the variables in
 * and out of expr into
 * final variables, as explained above. For example, <pre>
 *     var x := 3
 *     def y := 7
 *     ... x ... x := 4 ... y ...
 *     ... meta.eval(visualizingEvaluator, x ** y =~ pow) ...
 *     ... pow ...
 * <p/>
 * expands to
 * <p/>
 *     def x__Slot := settable.makeSlot(3)
 *     def y := 7
 *     ... x__Slot ... (x__Slot.put(4)) ... y ...
 *     def [pow, pow__Resolver] := Ref.promise()
 *     ... visualizingEvaluator.eval(e`x__Slot.get() ** y =~ pow`,
 *                                   ["x__Slot" => x__Slot, "y" => y],
 *                                   true,
 *                                   ["pow" => pow_Resolver]) ...
 *     ... pow ...
 * </pre>
 * If the "meta.eval(...)" occurs in a context where the value it evaluates to
 * is statically seen as unneeded, the "true" above would instead be "false".
 * <p/>
 * If <tt>pow</tt> were defined as <tt>var pow</tt>, it would be transformed
 * into the final variable <tt>pow__Slot</tt>.
 * <p/>
 * Similarly
 * <pre>
 *     meta &lt;- eval(evaluator, expr)
 * </pre>
 * expands into an eventual send to evaluator to eventually evaluate the
 * provided expression in the lexical context of this sugar. For
 * example <pre>
 *     meta &lt;- eval(remoteEvaluator, def powOverThere(x) :any { x**2 })
 *     def y := powOverThere &lt;- (3)
 * <p/>
 * expands to
 * <p/>
 *     def [powOverThere, powOverThere__Resolver] := Ref.promise()
 *     remoteEvaluator &lt;- eval(e`def powOverThere(x) :any { x**2 }`,
 *                             [].asMap(),
 *                             false,
 *                             ["powOverThere" => powOverThere__Resolver])
 *     def y := powOverThere &lt;- (3)
 * </pre>
 * The powOverThere function is defined on the remote machine or whatever, but
 * is locally named "powOverThere". If this machine is VatA and the remote
 * machine is VatC, we could now hand powOverThere to an object on VatB, who
 * would then obtain a reference directly connected to VatC. If VatA then goes
 * off-line, VatB would still be able to use powOverThere on VatC.
 * <p/>
 * With the eventual form, the expression evaluates in the current lexical
 * scope, just as if it were being implicitly evaluated, but it evaluates at a
 * later time in its own turn, and potentially in another vat. If this appears
 * in a context where its value might be used, it evaluates to a promise for
 * the outcome of evaluating the expression, and the third argument of the
 * expansion would be "true". Because of the delay, any Ejectors from the
 * containing context will already be used up, so the expression cannot perform
 * a non-local escape to an escape clause that has already exited (ie, Ejectors
 * are dynamic-extent continuations).
 * <p/>
 * If the evaluation is remote, then all bindings in and out will be as
 * transformed by passage through the Pluribus. The non-obvious implication of
 * this is that any use or assignment of any variable transformed by
 * Deslotifying will fail if the resulting Slot is PassByProxy (the default).
 * When using "meta &lt;- eval(...)" to do remote evaluation, all in and out
 * variables must either be final, or use Slot types that are PassByCopy or
 * PassByConstruction (like the lamportSlot).
 *
 * @author Mark S. Miller
 */
public interface Evaluator {

    /**
     * Evaluates an E expression in a provided lexical scope.
     * <p/>
     * When invoked asynchronously ("&lt;-"), the invoker of eval() cannot
     * distinguish between eval() evaluating to a broken reference as a value,
     * vs eval() throwing a problem. use evalToSingleton() when you want to
     * avoid this ambiguity. (Note: This ambiguity will often be desired. The
     * "meta &lt;- eval(...)" purposely produces this ambiguity.)
     *
     * @param eExpr       The expression to be evaluated.
     * @param bindingsIn  Provides bindings for those variables used freely in
     *                    eExpr, other than those defined in the safe scope
     *                    (like "true").
     * @param forValue    Says whether anyone cares about the value this eExpr
     *                    evaluates to. If false, eval() should evaluate for
     *                    effect only and return null.
     * @param bindingsOut Provides a mapping from variable names to Resolvers
     *                    for those variable names that eExpr defines, and that
     *                    some expression in the successor scope uses.
     * @return If forValue is true, eval() returns the value that eExpr
     *         evaluates to. If forValue is false, then eval() should return
     *         null, and its caller should ignore the return value, whatever it
     *         is.
     * @throws Throwable If eExpr exits non-locally, then eval() performs the
     *                   same non-local exit. The two kinds of non-local exit
     *                   are throwing a problem (Throwable) and Ejecting.
     *                   Ejecting only works when the Ejector is still good,
     *                   which can only happen when eval() is invoked
     *                   synchronously.
     */
    Object eval(EExpr eExpr,
                ConstMap bindingsIn,
                boolean forValue,
                ConstMap bindingsOut) throws Throwable;

    /**
     * Just like eval(), except that, when evaluating for a value,
     * evalToSingleton() returns a singleton list containing the value.
     * <p/>
     * This way an asynchronous invoker ("&lt;-") can distinguish between a
     * successful evaluation to a broken reference as a value vs a thrown
     * problem.
     */
    Object[] evalToSingleton(EExpr eExpr,
                             ConstMap bindingsIn,
                             boolean forValue,
                             ConstMap bindingsOut) throws Throwable;

    /**
     * Matches pattern against specimen, either failing or producing bindings.
     * <p/>
     * Unlike eval(), no syntactic sugar is currently defined for matchBind().
     *
     * @param pattern     The pattern to be matched against the specimen.
     * @param bindingsIn  Provides bindings for those variables used freely in
     *                    pattern, other than those defined in the safe scope
     *                    (like "true").
     * @param forTest     Says whether this should indicate failure by
     *                    returning false. If forTest, then on failure all the
     *                    bindingsOut must be broken with a problem report
     *                    explaining the reason for match failure. If forTest
     *                    is false, then the problem report is thrown on
     *                    failure, and bindingsOut should be ignored.
     * @param bindingsOut Provides a mapping from variable names to Resolvers
     *                    for those variable names that pattern defines, and
     *                    that some expression or pattern in the successor
     *                    scope uses. The values of these 'out' variables are
     *                    typically values extracted from specimen by
     *                    matching.
     * @param specimen    The object to be matched against the pattern.
     * @return If forTest is true, matchBind() returns whether the match
     *         succeeded. If forTest is false, then matchBind() only returns
     *         true, since if the match fails matchBind() throws rather than
     *         returning.
     * @throws Throwable If pattern exits non-locally, then matchBind()
     *                   performs the same non-local exit. The two kinds of
     *                   non-local exit are throwing a problem (Throwable) and
     *                   Ejecting. Also, if forTest is false and the match
     *                   fails, matchBind() throws a problem explaining the
     *                   failure.
     */
    boolean matchBind(Pattern pattern,
                      ConstMap bindingsIn,
                      boolean forTest,
                      ConstMap bindingsOut,
                      Object specimen) throws Throwable;
}
