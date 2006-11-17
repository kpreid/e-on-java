// Copyright 2006 Hewlett Packard, under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.elang.evm;

import org.erights.e.elang.scope.EvalContext;
import org.erights.e.elang.scope.ScopeLayout;
import org.erights.e.elang.visitors.ETreeVisitor;
import org.erights.e.elib.base.SourceSpan;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.prim.StaticMaker;
import org.erights.e.elib.prim.Thrower;
import org.erights.e.elib.ref.Ref;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.FlexList;
import org.erights.e.elib.util.OneArgFunc;

import java.io.IOException;

/**
 * BNF: "via" "(" eExpr ")" pattern
 *
 * @author Mark S. Miller
 */
public final class ViaPattern extends Pattern {

    final EExpr myViaExpr;
    final Pattern mySubPattern;

    /**
     *
     */
    public ViaPattern(SourceSpan optSpan,
                      EExpr viaExpr,
                      Pattern subPattern,
                      ScopeLayout optScopeLayout) {
        super(optSpan, optScopeLayout);
        myViaExpr = viaExpr;
        mySubPattern = subPattern;
    }

    public String getOptName() {
        return null;
    }

    public Object welcome(ETreeVisitor visitor) {
        return visitor.visitViaPattern(this, myViaExpr, mySubPattern);
    }

    protected void subMatchBind(ConstList args,
                                Object specimen,
                                OneArgFunc optEjector,
                                FlexList bindings) {
        ViaPattern other;
        try {
            other = (ViaPattern)Ref.resolution(specimen);
        } catch (ClassCastException cce) {
            //using a try/catch since success is typical and we have
            //to pay for the test in the cast anyway
            throw Thrower.toEject(optEjector, cce);
        }
        myViaExpr.subMatchBind(args, other.myViaExpr, optEjector, bindings);

        mySubPattern.subMatchBind(args,
                                  other.mySubPattern,
                                  optEjector,
                                  bindings);
    }

    /**
     * Uses 'makeViaPattern(...)'
     */
    public Object[] getSpreadUncall() {
        Object[] result = {StaticMaker.make(ViaPattern.class),
          "run",
          getOptSpan(),
          myViaExpr,
          mySubPattern,
          getOptScopeLayout()};
        return result;
    }

    /**
     *
     */
    protected StaticScope computeStaticScope() {
        return myViaExpr.staticScope().add(mySubPattern.staticScope());
    }

    /**
     *
     */
    void testMatch(EvalContext ctx, Object specimen, OneArgFunc optEjector) {
        Object viaFunc = myViaExpr.subEval(ctx, true);
        Object value = E.call(viaFunc, "run", specimen, optEjector);
        mySubPattern.testMatch(ctx, value, optEjector);
    }

    /**
     *
     */
    public EExpr getViaExpr() {
        return myViaExpr;
    }

    /**
     *
     */
    public Pattern getSubPattern() {
        return mySubPattern;
    }

    /**
     *
     */
    public void subPrintOn(TextWriter out, int priority) throws IOException {
        out.print("via (");
        myViaExpr.subPrintOn(out, PR_ORDER);
        out.print(") ");
        mySubPattern.subPrintOn(out, priority);
    }
}
