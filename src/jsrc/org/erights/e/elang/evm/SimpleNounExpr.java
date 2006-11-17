package org.erights.e.elang.evm;

import org.erights.e.develop.assertion.T;
import org.erights.e.elang.scope.EvalContext;
import org.erights.e.elang.scope.ScopeLayout;
import org.erights.e.elib.base.SourceSpan;
import org.erights.e.elib.prim.StaticMaker;
import org.erights.e.elib.slot.Slot;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

/**
 * @author Mark S. Miller
 */
public class SimpleNounExpr extends NounExpr {

    /**
     *
     */
    public SimpleNounExpr(SourceSpan optSpan,
                          String name,
                          ScopeLayout optScopeLayout) {
        super(optSpan, name, optScopeLayout);
    }

    /**
     * Uses XXX 'makeFoo(...)'
     */
    public Object[] getSpreadUncall() {
        Object[] result = {StaticMaker.make(SimpleNounExpr.class),
          "run",
          getOptSpan(),
          getName(),
          getOptScopeLayout()};
        return result;
    }

    /**
     * @return
     */
    public boolean isOuter() {
        T.fail("This node should have been transformed");
        return false; //make compiler happy
    }

    /**
     *
     */
    public Slot getSlot(EvalContext ctx) {
        T.fail("This node should have been transformed");
        return null; //make compiler happy
    }

    /**
     *
     */
    public NounExpr asFieldAt(int index) {
        T.fail("This node should have been transformed");
        return null; //make compiler happy
    }

    /**
     *
     */
    public NounExpr withScopeLayout(ScopeLayout optScopeLayout) {
        return new SimpleNounExpr(getOptSpan(), getName(), optScopeLayout);
    }

    /**
     *
     */
    public void initSlot(EvalContext ctx, Slot slot) {
        T.fail("This node should have been transformed");
    }
}
