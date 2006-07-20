// Copyright 2006 Hewlett Packard, under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.elang.syntax;

import org.erights.e.elang.evm.EExpr;
import org.erights.e.elang.evm.LiteralExpr;
import org.erights.e.elang.evm.StaticScope;
import org.erights.e.elang.scope.ScopeLayout;
import org.erights.e.elib.base.SourceSpan;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.StaticMaker;
import org.erights.e.elib.prim.Thrower;
import org.erights.e.elib.ref.Ref;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.FlexList;
import org.erights.e.elib.tables.ConstMap;
import org.erights.e.elib.util.OneArgFunc;
import org.quasiliteral.astro.Astro;

import java.io.IOException;

/**
 * @author Mark S. Miller
 */
class CondOrExpr extends DelayedExpr {

    private final EExpr myLeft;
    private final EExpr myRight;

    CondOrExpr(SourceSpan optSpan,
               EExpr left,
               EExpr right,
               ScopeLayout optScopeLayout) {
        super(optSpan, optScopeLayout);
        myLeft = left;
        myRight = right;
    }

    public Object[] getSpreadUncall() {
        Object[] result = { StaticMaker.make(CondOrExpr.class),
                            "run",
                            getOptSpan(),
                            myLeft,
                            myRight,
                            getOptScopeLayout() };
        return result;
    }

    /**
     * <pre>
     *    forControl(left || right,ej)</pre>
     * expands to<pre>
     *    escape ej1 {
     *        forControl(left,ej1)
     *        def br2 := Ref.broken("right side skipped")
     *        [&leftNames..., &rightOnlyNames/br2...]
     *    } catch ex3 {
     *        forControl(right,ej)
     *        def br4 := Ref.broken(ex3)
     *        [&rightNames..., &leftOnlyNames/br4...]
     *    } into [&outNames...]</pre>
     * or, if no exports<pre>
     *    escape ej1 {
     *        forControl(left,ej1)
     *    } catch _ {
     *        forControl(right,ej)
     *    }</pre>
     */
    EExpr forControl(ENodeBuilder b, Astro ej, StaticScope optUsed) {
        Astro ej1 = b.newTemp("ej");
        String[] exports = getExports(optUsed);
        if (exports.length >= 1) {
            Astro br2 = b.newTemp("br");
            Astro ex3 = b.newTemp("ex");
            Astro br4 = b.newTemp("br");
            LiteralExpr rightSkipped = ENodeBuilder.__RIGHT_SKIPPED;
            ConstMap leftOuts = myLeft.staticScope().outNames();
            ConstMap rightOuts = myRight.staticScope().outNames();
            return b.kdef(slotsPattern(b, null, exports), 
                          b.escape(b.finalPattern(ej1),
                                   b.sequence(b.forControl(myLeft,
                                                           ej1,
                                                           optUsed),
                                              broke(b, br2, rightSkipped),
                                              mixedTuple(b,
                                                         null,
                                                         exports,
                                                         leftOuts,
                                                         br2)),
                                   b.finalPattern(ex3),
                                   b.sequence(b.forControl(myRight,
                                                           ej,
                                                           optUsed),
                                              broke(b, br4, noun(ex3)),
                                              mixedTuple(b,
                                                         null,
                                                         exports,
                                                         rightOuts,
                                                         br4)))
            );
        } else {
            return b.escape(b.finalPattern(ej1),
                            b.forControl(myLeft, ej1, StaticScope.EmptyScope),
                            b.ignore(),
                            b.forControl(myRight, ej, StaticScope.EmptyScope));
        }
    }

    protected StaticScope computeStaticScope() {
        return myLeft.staticScope().both(myRight.staticScope());
    }

    protected void subMatchBind(ConstList args,
                                Object specimen,
                                OneArgFunc optEjector,
                                FlexList bindings) {
        CondOrExpr other;
        try {
            other = (CondOrExpr)Ref.resolution(specimen);
        } catch (ClassCastException cce) {
            //using a try/catch since success is typical and we have
            //to pay for the test in the cast anyway
            throw Thrower.toEject(optEjector, cce);
        }
        myLeft.matchBind(args, other.myLeft, optEjector, bindings);
        myRight.matchBind(args, other.myRight, optEjector, bindings);
    }

    public void subPrintOn(TextWriter out, int priority) throws IOException {
        if (priority > PR_COMP) {
            out.print("(");
        }
        myLeft.subPrintOn(out, PR_ORDER);
        out.print(" || ");
        myRight.subPrintOn(out, PR_ORDER);
        if (priority > PR_COMP) {
            out.print(")");
        }
    }
}
