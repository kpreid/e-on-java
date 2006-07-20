package org.erights.e.elang.smallcaps;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.elang.evm.EExpr;
import org.erights.e.elang.evm.StaticScope;
import org.erights.e.elang.scope.EvalContext;
import org.erights.e.elang.visitors.ETreeVisitor;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.StaticMaker;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.FlexList;
import org.erights.e.elib.util.OneArgFunc;

import java.io.IOException;

/**
 *
 * @author Mark S. Miller
 * @author Darius Bacon
 */
public class SmallcapsExpr extends EExpr {

    /**
     * A Bound-E AST
     */
    private final EExpr myExprTree;

    /**
     *
     */
    private final byte[] myCode;

    /**
     *
     */
    public SmallcapsExpr(EExpr exprTree, byte[] code) {
        super(exprTree.getOptSpan(), exprTree.getOptScopeLayout());
        myExprTree = exprTree;
        myCode = code;
    }

    /**
     * Uses XXX 'makeFoo(...)'
     */
    public Object[] getSpreadUncall() {
        Object[] result = { StaticMaker.make(SmallcapsExpr.class),
                            "run",
                            myExprTree,
                            myCode };
        return result;
    }

    /**
     *
     */
    protected StaticScope computeStaticScope() {
        return myExprTree.staticScope();
    }

    /**
     *
     */
    public Object welcome(ETreeVisitor visitor) {
        return myExprTree.welcome(visitor);
    }

    /**
     *
     */
    protected void subMatchBind(ConstList args,
                                Object specimen,
                                OneArgFunc optEjector,
                                FlexList bindings) {
        myExprTree.matchBind(args, specimen, optEjector, bindings);
    }

    /**
     *
     */
    public void matchBind(ConstList args,
                          Object optSpecimen,
                          OneArgFunc optEjector,
                          FlexList bindings) {
        myExprTree.matchBind(args, optSpecimen, optEjector, bindings);
    }

    /**
     *
     */
    protected Object subEval(EvalContext ctx, boolean forValue) {
        SmallcapsActivation activation =
          new SmallcapsActivation(myCode, 0, ctx);
        return activation.eval();
    }

    /**
     *
     */
    public void subPrintOn(TextWriter out, int priority)
      throws IOException {
        myExprTree.subPrintOn(out, priority);
    }
}
