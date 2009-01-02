package org.erights.e.elang.evm;

//Copyright 2009 Google, Inc. under the terms of the MIT X license
//found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.develop.assertion.T;
import org.erights.e.elang.scope.EvalContext;
import org.erights.e.elang.scope.ScopeLayout;
import org.erights.e.elang.visitors.ETreeVisitor;
import org.erights.e.elib.base.SourceSpan;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.StaticMaker;
import org.erights.e.elib.prim.Thrower;
import org.erights.e.elib.ref.Ref;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.FlexList;
import org.erights.e.elib.util.OneArgFunc;

import java.io.IOException;


/**
 * BNF: "&&" varName
 * <p/>
 * Returns the binder holding the noun's value
 *
 * @author Mark S. Miller
 * @author E. Dean Tribble
 */
public class BindingExpr extends EExpr {

    static private final long serialVersionUID = 1L;

    private final AtomicExpr myNoun;

    /**
     *
     */
    public BindingExpr(SourceSpan optSpan,
                       AtomicExpr noun,
                       ScopeLayout optScopeLayout) {
        super(optSpan, optScopeLayout);
        myNoun = noun;
    }

    /**
     * Uses XXX 'makeFoo(...)'
     */
    public Object[] getSpreadUncall() {
        Object[] result = {StaticMaker.make(BindingExpr.class),
                           "run",
                           getOptSpan(),
                           myNoun,
                           getOptScopeLayout()};
        return result;
    }

    /**
     *
     */
    public Object welcome(ETreeVisitor visitor) {
        return visitor.visitBindingExpr(this, myNoun);
    }

    /**
     *
     */
    protected StaticScope computeStaticScope() {
        return StaticScope.scopeRead(myNoun);
    }

    /**
     *
     */
    protected Object subEval(EvalContext ctx, boolean forValue) {
        T.fail("BindingExpr evaluation not yet implemented");
        // XXX FIXME TODO fix to get binder rather than slot
        return myNoun.asNoun().getSlot(ctx);
    }

    /**
     *
     */
    public void subMatchBind(ConstList args,
                             Object specimen,
                             OneArgFunc optEjector,
                             FlexList bindings) {
        BindingExpr other;
        try {
            other = (BindingExpr)Ref.resolution(specimen);
        } catch (ClassCastException cce) {
            //using a try/catch since success is typical and we have
            //to pay for the test in the cast anyway
            throw Thrower.toEject(optEjector, cce);
        }
        myNoun.subMatchBind(args, other.myNoun, optEjector, bindings);
    }

    public AtomicExpr getNoun() {
        return myNoun;
    }

    /**
     *
     */
    public void subPrintOn(TextWriter out, int priority) throws IOException {
        out.print("&&");
        myNoun.subPrintOn(out, PR_PRIM);
    }
}
