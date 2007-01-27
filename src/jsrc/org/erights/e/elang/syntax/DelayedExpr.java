// Copyright 2006 Hewlett Packard, under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.elang.syntax;

import org.erights.e.develop.assertion.T;
import org.erights.e.elang.evm.EExpr;
import org.erights.e.elang.evm.NounExpr;
import org.erights.e.elang.evm.Pattern;
import org.erights.e.elang.evm.StaticScope;
import org.erights.e.elang.scope.EvalContext;
import org.erights.e.elang.scope.ScopeLayout;
import org.erights.e.elang.visitors.ETreeVisitor;
import org.erights.e.elib.base.SourceSpan;
import org.erights.e.elib.tables.ConstMap;
import org.quasiliteral.astro.Astro;

/**
 * Represents an E expression whose expansion to Kernel-E depends on context.
 * <p/>
 * In E-on-Java as parsed by the byacc/J-generated parser, most expansion
 * happens immediately during parsing. A DelayedExpr represents an expression
 * whose expansion should be delayed, so that it can be based on information
 * from its context.
 * <p/>
 * If optUsed is null, then all exported bindings must indeed be exported.
 * Otherwise, optUsed represents the bindings used by the expressions to the
 * "right" of this one in its scope box. The expansion of this expression need
 * then export only the intersection of its apparent exports and optUsed.
 *
 * @author Mark S. Miller
 */
abstract class DelayedExpr extends EExpr {

    static private final long serialVersionUID = 1798663867526490638L;

    DelayedExpr(SourceSpan optSpan, ScopeLayout optScopeLayout) {
        super(optSpan, optScopeLayout);
    }

    EExpr broke(ENodeBuilder b, Astro br, EExpr problem) {
        return b.kdef(b.finalPattern(br),
                      b.call(ENodeBuilder.REF,
                             this,
                             "broken",
                             b.list(problem)));
    }

    NounExpr noun(Object identOrStr) {
        return BaseENodeBuilder.noun(identOrStr);
    }

    /**
     * Given an array of names, return an expression that will make a tuple
     * from the slots of the variables of these names
     */
    EExpr mixedTuple(ENodeBuilder b,
                     EExpr optPrefix,
                     String[] exports,
                     ConstMap optExportsOk,
                     Astro optBr) {
        int offset = (null == optPrefix) ? 0 : 1;
        EExpr[] nameExprs = new EExpr[offset + exports.length];
        if (null != optPrefix) {
            nameExprs[0] = optPrefix;
        }
        for (int i = 0; i < exports.length; i++) {
            String export = exports[i];
            if (null != optExportsOk && !optExportsOk.maps(export)) {
                nameExprs[offset + i] = noun(optBr);
            } else {
                nameExprs[offset + i] =
                  b.slotExpr(BaseEBuilder.NO_POSER, noun(export));
            }
        }
        return b.tuple(nameExprs);
    }

    /**
     * Given an array of names, return an expression that will make a tuple
     * from the slots of the variables of these names
     */
    EExpr slotsTuple(ENodeBuilder b, EExpr optPrefix, String[] exports) {
        return mixedTuple(b, optPrefix, exports, null, null);
    }

    /**
     * Given an array of names, return a tuple pattern that will bind all these
     * names to slots when matched with an array of slots with the same shape.
     * <p/>
     */
    Pattern slotsPattern(ENodeBuilder b, Pattern optPrefix, String[] exports) {
        int offset = (null == optPrefix) ? 0 : 1;
        Pattern[] slotPatts = new Pattern[offset + exports.length];
        if (null != optPrefix) {
            slotPatts[0] = optPrefix;
        }
        for (int i = 0; i < exports.length; i++) {
            slotPatts[offset + i] = b.slotPattern(exports[i]);
        }
        return b.listPattern(slotPatts);
    }


    /**
     * Expand to an expression whose value is correct.
     * <p/>
     * The default implementation here expands to<pre>
     *     def [rs4, &exports...] := escape ej1 {
     *         forControl(expr,ej1)
     *         [true, &exports...]
     *     } catch ex2 {
     *         def br3 := Ref.broken(ex2)
     *         [false, br3...]
     *     }
     *     rs4</pre>
     * or, if no exports<pre>
     *     escape ej1 {
     *         forControl(expr,ej1)
     *         true
     *     } catch _ {
     *         false
     *     }</pre>
     */
    EExpr forValue(ENodeBuilder b, StaticScope optUsed) {
        Astro ej1 = b.newTemp("ej");
        String[] exports = getExports(optUsed);
        if (1 <= exports.length) {
            Astro ex2 = b.newTemp("ex");
            Astro br3 = b.newTemp("br");
            Astro rs4 = b.newTemp("rs");
            EExpr escExpr = b.escape(b.finalPattern(ej1),
                                     b.sequence(forControl(b, ej1, optUsed),
                                                slotsTuple(b,
                                                           ENodeBuilder.TRUE,
                                                           exports)),
                                     b.finalPattern(ex2),
                                     b.sequence(broke(b, br3, noun(ex2)),
                                                mixedTuple(b,
                                                           ENodeBuilder.FALSE,
                                                           exports,
                                                           ConstMap.EmptyMap,
                                                           br3)));
            return b.sequence(b.kdef(slotsPattern(b,
                                                  b.finalPattern(rs4),
                                                  exports), escExpr),
                              noun(rs4));
        } else {
            return b.escape(b.finalPattern(ej1),
                            b.sequence(forControl(b,
                                                  ej1,
                                                  StaticScope.EmptyScope),
                                       ENodeBuilder.TRUE),
                            b.ignore(),
                            ENodeBuilder.FALSE);
        }
    }

    /**
     * Expand to an expression whose value is not used, and which therefore
     * need not be the same as the original expression.
     * <p/>
     * The default implementation here expands to<pre>
     *     def [&exports...] := escape ej1 {
     *         forControl(expr,ej1)
     *         [&exports...]
     *     } catch ex2 {
     *         def br3 := Ref.broken(ex2)
     *         [br3...]
     *     }</pre>
     * or, if no exports<pre>
     *     escape ej1 {
     *         forControl(expr,ej1)
     *     }</pre>
     */
    EExpr forFxOnly(ENodeBuilder b, StaticScope optUsed) {
        Astro ej1 = b.newTemp("ej");
        String[] exports = getExports(optUsed);
        if (1 <= exports.length) {
            Astro ex2 = b.newTemp("ex");
            Astro br3 = b.newTemp("br");
            return b.kdef(slotsPattern(b, null, exports),
                          b.escape(b.finalPattern(ej1),
                                   b.sequence(forControl(b, ej1, optUsed),
                                              slotsTuple(b, null, exports)),
                                   b.finalPattern(ex2),
                                   b.sequence(broke(b, br3, noun(ex2)),
                                              mixedTuple(b,
                                                         null,
                                                         exports,
                                                         ConstMap.EmptyMap,
                                                         br3))));
        } else {
            return b.escape(b.finalPattern(ej1),
                            forControl(b, ej1, StaticScope.EmptyScope),
                            null);
        }
    }

    /**
     * Expand to an expression whose value is only branched on.
     * <p/>
     * If the expression's value is true, fall through into a context described
     * by optUsed. If false, eject to ej. Otherwise, throw a complaint that the
     * value isn't boolean.
     */
    abstract EExpr forControl(ENodeBuilder b, Astro ej, StaticScope optUsed);

    /**
     * Complain that a DelayedExpr isn't eval-able
     */
    protected Object subEval(EvalContext ctx, boolean forValue) {
        T.fail("A DelayedExpr isn't eval-able");
        return null; //make compiler happy
    }

    /**
     * Complain that a DelayedExpr isn't visit-able
     */
    public Object welcome(ETreeVisitor visitor) {
        T.fail("A DelayedExpr isn't visit-able");
        return null; //make compiler happy
    }
}
