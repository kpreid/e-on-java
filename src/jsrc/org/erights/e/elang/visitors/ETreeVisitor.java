package org.erights.e.elang.visitors;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.elang.evm.AtomicExpr;
import org.erights.e.elang.evm.EExpr;
import org.erights.e.elang.evm.EMatcher;
import org.erights.e.elang.evm.EMethod;
import org.erights.e.elang.evm.ENode;
import org.erights.e.elang.evm.EScript;
import org.erights.e.elang.evm.Pattern;
import org.erights.e.elang.evm.GuardedPattern;


/**
 * Implementors of this interface are <i>visitors</i> for visiting
 * Expanded-E, Kernel-E, Bound-E, or Transformed-E ASTs.
 * <ul>
 * <li>E - syntactic sugar = Expanded-E.
 * <li>Expanded-E + fully qualified names = Kernel-E.
 * <li>Kernel-E + syntactic environments = Bound-E.
 * <li>Bound-E + optimizations + internal ScopeLayout info +
 *     variable location info = Transformed-E.
 * </ul>
 * Kernel-E is the most primitive form of code that objects outside the
 * TCB may submit to the TCB for "loading", and therefore it is the form
 * that's passed between machines and verified. The loader then transforms
 * this to Bound-E, which is the form handed to auditors for <a href=
 * "http://www.sims.berkeley.edu/~ping/sid/">auditing</a>.
 * <p>
 * All but Transformed-E are official parts of E. Transformed-E is instead
 * part of this implementation, and not part of the definition of E.
 * <p>
 * The notion of <i>visitor</i> used here is a variation of that defined by
 * the <i>Visitor Pattern</i> of the Design Patterns book. Here, each visit
 * method has both an optional first parameter (<tt>optoriginal</tt>), which
 * is the original node being visited, and a set of arguments, which between
 * them contain all the non-debugging information present in a Kernel-E tree.
 * For a Kernel-E tree, the <tt>optOriginal</tt> parameter, if present,
 * additionally contributes only source position information.
 *
 * @author Mark S. Miller
 */
public interface ETreeVisitor {


    /**************************** EExprs **************************/

    /**
     * varName ":=" rValue. <p>
     *
     * Does a setValue on the slot named by varName to the value of rValue
     *
     * @see <a href="{@docroot}/../elang/kernel/AssignExpr.html">The
     * Kernel-E Assignment Expression</a>
     */
    Object visitAssignExpr(ENode optOriginal,
                           AtomicExpr noun,
                           EExpr rValue);

    /**
     * recip verb "(" args*, ")". <p>
     *
     * Eval left to right, then synchronously call the verb method of the
     * value of recip with the values of the args.
     *
     * @see <a href="{@docroot}/../elang/kernel/CallExpr.html">The
     * Kernel-E Call Expression</a>
     */
    Object visitCallExpr(ENode optOriginal,
                         EExpr recip,
                         String verb,
                         EExpr[] args);

    /**
     * "def" patt ("exit" eExpr)? ":=" eExpr <p>
     *
     * Match patt against the value of rValue. Neither patt nor rValue may
     * use any variables defined by the other. This allows their scopes to
     * be order independent.
     *
     * @see <a href="{@docroot}/../elang/kernel/DefineExpr.html">The
     * Kernel-E Define Expression</a>
     */
    Object visitDefineExpr(ENode optOriginal,
                           Pattern patt,
                           EExpr optEjectorExpr,
                           EExpr rValue);

    /**
     * "escape" hatch "{" body "}". <p>
     *
     * Bind hatch to an escape hatch. If the escape hatch's run/1 is called
     * during the execution of body, the escape expression will be exited
     * early, and the run argument will be the value. If run/0 is called,
     * it's as if run(null) were called.
     *
     * @see <a href="{@docroot}/../elang/kernel/EscapeExpr.html">The
     * Kernel-E Escape Expression</a>
     */
    Object visitEscapeExpr(ENode optOriginal,
                           Pattern hatch,
                           EExpr body,
                           Pattern optArgPattern,
                           EExpr optCatcher);

    /**
     * "{" body "}". <p>
     *
     * Evaluate body, but hide all variable names it defines from the
     * surrounding scope.
     *
     * @see <a href="{@docroot}/../elang/kernel/HideExpr.html">The
     * Kernel-E Hide Expression</a>
     */
    Object visitHideExpr(ENode optOriginal, EExpr body);

    /**
     * "if" "("test")" "{" then "}" "else" "{" els "}". <p>
     *
     * Evaluate test to a boolean. If true, the value is the evaluation of
     * then. Else the value is the evaluation of els.
     *
     * @see <a href="{@docroot}/../elang/kernel/IfExpr.html">The
     * Kernel-E If Expression</a>
     */
    Object visitIfExpr(ENode optOriginal, EExpr test, EExpr then, EExpr els);

    /**
     * value. <p>
     *
     * This value is the value of the expression.
     *
     * @see <a href="{@docroot}/../elang/kernel/LiteralExpr.html">The
     * Kernel-E Literal Expression</a>
     */
    Object visitLiteralExpr(ENode optOriginal, Object value);

    /**
     * varName. <p>
     *
     * The result of a getValue() on the slot named by this varName.
     *
     * @see <a href="{@docroot}/../elang/kernel/NounExpr.html">The
     * Kernel-E Noun Expression</a>
     */
    Object visitNounExpr(ENode optOriginal, String varName);

    /**
     * "##" synopsys
     * "def" oName ("implements" auditors)? (eScript | matcher)
     * <p>
     * Define an object that responds to messages according to eScript
     * <p>
     * XXX stale paragraph, needs rewrite: Each auditor must be bound to a
     * "global" final variable, ie, a read-only variable in the incoming scope
     * for evaluating the overall expression. The value of this variable is
     * read once and its value is asked to audit the parse tree of the
     * ObjectExpr it is being asked to audit. Only if all auditors approve of
     * all the audited subtrees in the expression as a whole does execution
     * continue.
     * <p>
     * At runtime, given an auditor and an instance of an ObjectExpr audited
     * by that auditor, it must be possible to unspoofably ask that auditor
     * whether this is such an instance.
     *
     * @see <a href="{@docroot}/../elang/kernel/ObjectExpr.html">The
     * Kernel-E Object Expression</a>
     */
    Object visitObjectExpr(ENode optOriginal,
                           String docComment,
                           GuardedPattern oName,
                           EExpr[] auditors,
                           EScript eScript);

    /**
     * "$" "{" index "}". <p>
     *
     * A placeholder in a quasi-parseTree to be filled in, by substitution,
     * with a real expression. This is not part of Kernel-E.
     */
    Object visitQuasiLiteralExpr(ENode optOriginal, int index);

    /**
     * "@" "{" index "}". <p>
     *
     * A placeholder in a quasi-pattern-parseTree to capture a corresponding
     * expression in a specimen parseTree, and say where to put it in the
     * binding vector. This is not part of Kernel-E.
     */
    Object visitQuasiPatternExpr(ENode optOriginal, int index);

    /**
     * "meta.getState()".
     * <p>
     * Reifies the current environment as a ConstMap from
     * <pre>    &quot;&amp;&quot;identifier =&gt; {@link
     * org.erights.e.elib.slot.Slot Slot}</pre>
     * This should not be part of Kernel-E, but currently is.
     */
    Object visitMetaStateExpr(ENode optOriginal);

    /**
     * "meta.context()". <p>
     *
     * Reifies the current syntactic environment as a ConstMap from
     * <pre>    identifier =&gt; {@link
     * org.erights.e.elang.evm.NounPattern NounPattern}</pre>
     */
    Object visitMetaContextExpr(ENode optOriginal);

    /**
     * first "\n" second. <p>
     *
     * Do first, then evaluate to the result of second.
     *
     * @see <a href="{@docroot}/../elang/kernel/SeqExpr.html">The
     * Kernel-E Sequence Expression</a>
     */
    Object visitSeqExpr(ENode optOriginal, EExpr[] subs);

    /**
     * "&" varName. <p>
     *
     * The value is the slot named by varName.
     */
    Object visitSlotExpr(ENode optOriginal, AtomicExpr noun);

    /**
     * "try" "{" attempt "}" "catch" patt "{" catcher "}". <p>
     *
     * Evaluate attempt. If it completes successfully, then its value
     * is the value of the catch-expression. If it throws an
     * exception, match it against patt. If it succeeds, evaluate to
     * the evaluation of catcher. Otherwise rethrow the exception.
     *
     * @see <a href="{@docroot}/../elang/kernel/CatchExpr.html">The
     * Kernel-E Try-Catch Expression</a>
     */
    Object visitCatchExpr(ENode optOriginal,
                          EExpr attempt,
                          Pattern patt,
                          EExpr catcher);

    /**
     * "try" "{" attempt "}" "finally" "{" unwinder "}". <p>
     *
     * Evaluate attempt. On the way out, whether by successful
     * completion or abrupt termination (throw or escape), in all
     * cases evaluate the unwinder before leaving. If attempt
     * succeeds, then the value of the finally-expression is the value
     * of attempt.
     *
     * @see <a href="{@docroot}/../elang/kernel/FinallyExpr.html">The
     * Kernel-E Try-Finally Expression</a>
     */
    Object visitFinallyExpr(ENode optOriginal,
                            EExpr attempt,
                            EExpr unwinder);

    /***************************** Patterns *************************/


    /**
     * varName (":" guardExpr)?.
     * <p>
     * Define the coerced specimen to be the value of varName.
     *
     * @see <a href="{@docroot}/../elang/kernel/FinalPattern.html">The
     * Kernel-E Final Pattern</a>
     */
    Object visitFinalPattern(ENode optOriginal,
                             AtomicExpr nounExpr,
                             EExpr optGuardExpr);

    /**
     * "var" varName (":" guardExpr)?.
     * <p>
     * Define the coerced specimen to be the initial value held by the
     * primitive slot responsible for representing varName's current value.
     *
     * @see <a href="{@docroot}/../elang/kernel/VarPattern.html">The
     * Kernel-E Var Pattern</a>
     */
    Object visitVarPattern(ENode optOriginal,
                           AtomicExpr nounExpr,
                           EExpr optGuardExpr);

    /**
     * "&" varName (":" guardExpr)?.
     * <p>
     * Define the coerced specimen to be the slot responsible for representing
     * varName's current value.
     *
     * @see <a href="{@docroot}/../elang/kernel/SlotPattern.html">The
     * Kernel-E Slot Pattern</a>
     */
    Object visitSlotPattern(ENode optOriginal,
                            AtomicExpr nounExpr,
                            EExpr optGuardExpr);

    /**
     * "_" (":" guardExpr)?.
     * <p>
     * Matches any coerced, binds nothing.
     *
     * @see <a href="{@docroot}/../elang/kernel/IgnorePattern.html">The
     * Kernel-E Ignore Pattern</a>
     */
    Object visitIgnorePattern(ENode optOriginal, EExpr optGuardExpr);

    /**
     * "via" "(" viaExpr ")" subPattern.
     * <p>
     * Coerce the specimen by the guard and match the pattern against the
     * result.
     */
    Object visitViaPattern(ENode optOriginal,
                           EExpr viaExpr,
                           Pattern subPattern);

    /**
     * "["pattern*, "]". <p>
     *
     * Is the specimen a list exactly this long, and do each of the
     * elements match the corresponding sub-pattern?
     *
     * @see <a href="{@docroot}/../elang/kernel/ListPattern.html">The
     * Kernel-E Tuple Pattern</a>
     */
    Object visitListPattern(ENode optOriginal, Pattern[] subs);

    /**
     * "$" "{" index "}". <p>
     *
     * A placeholder in a quasi-parseTree to be filled in, by substitution,
     * with a real pattern. Not part of Kernel-E.
     */
    Object visitQuasiLiteralPatt(ENode optOriginal, int index);

    /**
     * "@" "{" index "}". <p>
     *
     * A placeholder in a quasi-pattern-parseTree to capture a corresponding
     * pattern in a specimen parseTree, and say where to put it in the
     * binding vector. Not part of Kernel-E.
     */
    Object visitQuasiPatternPatt(ENode optOriginal, int index);



    /***************************** Other *************************/


    /**
     *    "{" methods* matcher? "}"?
     * <p>
     * XXX currently, when a matcher should have been provided to
     * visitObjectExpr, instead an eScript is provided whose
     * optMethods is null. This breaks the XML translation, since the
     * XML encoding cannot distinguish between no method and an empty
     * list of methods.
     *
     * @see <a href="{@docroot}/../elang/kernel/EScript.html">The
     * Kernel-E Script Node</a>
     */
    Object visitEScript(ENode optOriginal,
                        EMethod[] optMethods,
                        EMatcher[] matchers);

    /**
     * "##" docComment <br>
     * "to" verb "(" patterns*, ")" (":" optResultGuard)? "{" body "}".
     * <p>
     * Defines a method for verb and a number of arguments matching the
     * number of patterns. When the containing object is sent such a
     * message, the arguments are matched against the patterns, and then the
     * body is evaluated. They value of body as coerced by optResultGuard is
     * finally revealed.
     *
     * @see <a href="{@docroot}/../elang/kernel/EMethod.html">The
     * Kernel-E Method Node</a>
     */
    Object visitEMethod(ENode optOriginal,
                        String docComment,
                        String verb,
                        Pattern[] patterns,
                        EExpr optResultGuard,
                        EExpr body);

    /**
     * "match" pattern "{" body "}". <p>
     *
     * When the incoming message doesn't fit any of the methods, then a pair
     * of the verb and the arguments is matched against patt, then body
     * is evaluated, and it value revealed.
     *
     * @see <a href="{@docroot}/../elang/kernel/EMatcher.html">The
     * Kernel-E EMatcher Node</a>
     */
    Object visitEMatcher(ENode optOriginal,
                         Pattern pattern,
                         EExpr body);
}
