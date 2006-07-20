// Copyright 2006 Hewlett Packard, under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.elang.syntax;

import org.erights.e.elang.evm.EExpr;
import org.erights.e.elang.evm.StaticScope;
import org.erights.e.elang.scope.ScopeLayout;
import org.erights.e.elib.base.SourceSpan;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.StaticMaker;
import org.erights.e.elib.prim.Thrower;
import org.erights.e.elib.ref.Ref;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.FlexList;
import org.erights.e.elib.util.OneArgFunc;
import org.quasiliteral.astro.Astro;

import java.io.IOException;

/**
 * @author Mark S. Miller
 */
class CondAndExpr extends DelayedExpr {

    private final EExpr myLeft;
    private final EExpr myRight;

    CondAndExpr(SourceSpan optSpan,
                EExpr left,
                EExpr right,
                ScopeLayout optScopeLayout) {
        super(optSpan, optScopeLayout);
        myLeft = left;
        myRight = right;
    }

    public Object[] getSpreadUncall() {
        Object[] result = { StaticMaker.make(CondAndExpr.class),
                            "run",
                            getOptSpan(),
                            myLeft,
                            myRight,
                            getOptScopeLayout() };
        return result;
    }

    /**
     * <pre>
     *    forControl(left && right,ej)</pre>
     * expands to<pre>
     *    forControl(left,ej)
     *    forControl(right,ej)
     */
    EExpr forControl(ENodeBuilder b, Astro ej, StaticScope optUsed) {
        StaticScope optMidUsed;
        if (null == optUsed) {
            optMidUsed = null;
        } else {
            optMidUsed = myRight.staticScope().add(optUsed);
        }
        return b.sequence(b.forControl(myLeft, ej, optMidUsed),
                          b.forControl(myRight, ej, optUsed));
    }

    protected StaticScope computeStaticScope() {
        return myLeft.staticScope().add(myRight.staticScope());
    }

    protected void subMatchBind(ConstList args,
                                Object specimen,
                                OneArgFunc optEjector,
                                FlexList bindings) {
        CondAndExpr other;
        try {
            other = (CondAndExpr)Ref.resolution(specimen);
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
        out.print(" && ");
        myRight.subPrintOn(out, PR_ORDER);
        if (priority > PR_COMP) {
            out.print(")");
        }
    }
}
