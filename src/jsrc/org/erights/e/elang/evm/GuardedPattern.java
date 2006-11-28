// Copyright 2005 Mark S. Miller, under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.elang.evm;

import org.erights.e.elang.scope.EvalContext;
import org.erights.e.elang.scope.ScopeLayout;
import org.erights.e.elib.base.SourceSpan;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.slot.Guard;
import org.erights.e.elib.util.OneArgFunc;

/**
 * @author Mark S. Miller
 */
public abstract class GuardedPattern extends Pattern {

    final EExpr myOptGuardExpr;

    /**
     *
     */
    GuardedPattern(SourceSpan optSpan,
                   EExpr optGuardExpr,
                   ScopeLayout optScopeLayout) {
        super(optSpan, optScopeLayout);
        myOptGuardExpr = optGuardExpr;
    }

    /**
     *
     */
    Object coercedSpecimen(EvalContext ctx,
                           Object specimen,
                           OneArgFunc optEjector) {
        if (null == myOptGuardExpr) {
            return specimen;
        } else {
            //Because of the well-formedness criteria, we may safely evaluate
            //this in the "wrong" order
            Object vg = myOptGuardExpr.subEval(ctx, true);
            Guard guard = (Guard)E.as(vg, Guard.class);
            return guard.coerce(specimen, optEjector);
        }
    }

    /**
     * Returns the expression, if any, to the right of the colon
     */
    public EExpr getOptGuardExpr() {
        return myOptGuardExpr;
    }
}
