package org.erights.e.elang.evm;

// Copyright 2009 Google, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import java.io.IOException;

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

public final class AuditorExprs extends ENode {
    static private final long serialVersionUID = 1L;
    static private final EExpr[] NO_EEXPRS = {};
    static public final AuditorExprs NO_AUDITORS =
        new AuditorExprs(null, null, NO_EEXPRS, null);

    private final EExpr myOptAs;
    private final EExpr[] myImpls;

    public AuditorExprs(SourceSpan optSpan,
                        EExpr optAs,
                        EExpr[] impls,
                        ScopeLayout optScopeLayout) {
        super(optSpan, optScopeLayout);
        myOptAs = optAs;
        myImpls = impls;
    }

    public Object[] getSpreadUncall() {
        Object[] result = { StaticMaker.make(AuditorExprs.class),
                            "run",
                            getOptSpan(),
                            myOptAs,
                            myImpls,
                            getOptScopeLayout() };
        return result;
    }

    public Object welcome(ETreeVisitor visitor) {
        return visitor.visitAuditorExprs(this, myOptAs, myImpls);
    }

    public EExpr getOptAs() {
        return myOptAs;
    }

    public EExpr[] getImpls() {
        return myImpls;
    }

    public EExpr[] getAll() {
        if (null == myOptAs) {
            return myImpls;
        }
        EExpr[] result = new EExpr[myImpls.length + 1];
        result[0] = myOptAs;
        System.arraycopy(myImpls, 0, result, 1, myImpls.length);
        return result;
    }

    protected StaticScope computeStaticScope() {
        return staticScopeOfList(getAll());
    }

    protected void subMatchBind(ConstList args,
                                Object specimen,
                                OneArgFunc optEjector,
                                FlexList bindings) {
        AuditorExprs other;
        try {
            other = (AuditorExprs)Ref.resolution(specimen);
        } catch (ClassCastException cce) {
            throw Thrower.toEject(optEjector, cce);
        }
        matchBind(myOptAs,
                  args,
                  other.myOptAs,
                  optEjector,
                  bindings);
        matchBind(myImpls,
                  args,
                  other.myImpls,
                  optEjector,
                  bindings);
    }

    public void subPrintOn(TextWriter out,
                           int priority) throws IOException {
        if (null != myOptAs) {
            out.print(" as ");
            myOptAs.subPrintOn(out, PR_ORDER);
        }
        int numImpls = myImpls.length;
        if (1 <= numImpls) {
            out.print(" implements ");
            myImpls[0].subPrintOn(out, PR_ORDER);
            for (int i = 1; i < numImpls; i++) {
                out.print(", ");
                myImpls[i].subPrintOn(out, PR_ORDER);
            }
        }
    }
}
