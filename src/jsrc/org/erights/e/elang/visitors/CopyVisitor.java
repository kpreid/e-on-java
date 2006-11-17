package org.erights.e.elang.visitors;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.elang.evm.ENode;
import org.erights.e.elang.evm.QuasiLiteralExpr;
import org.erights.e.elang.evm.QuasiLiteralPatt;
import org.erights.e.elang.evm.QuasiPatternExpr;
import org.erights.e.elang.evm.QuasiPatternPatt;


/**
 *
 */
public class CopyVisitor extends KernelECopyVisitor {

    /**
     *
     */
    public CopyVisitor(ETreeVisitor decorator) {
        super(decorator);
    }

    /**
     *
     */
    public CopyVisitor() {
    }

    /**************************** EExprs **************************/

    /**
     *
     */
    public Object visitQuasiLiteralExpr(ENode optOriginal, int index) {
        return new QuasiLiteralExpr(getOptSpan(optOriginal),
                                    index,
                                    getOptScopeLayout());
    }


    /**
     *
     */
    public Object visitQuasiPatternExpr(ENode optOriginal, int index) {
        return new QuasiPatternExpr(getOptSpan(optOriginal),
                                    index,
                                    getOptScopeLayout());
    }

    /***************************** Patterns *************************/

    /**
     *
     */
    public Object visitQuasiLiteralPatt(ENode optOriginal, int index) {
        return new QuasiLiteralPatt(getOptSpan(optOriginal),
                                    index,
                                    getOptScopeLayout());
    }


    /**
     *
     */
    public Object visitQuasiPatternPatt(ENode optOriginal, int index) {
        return new QuasiPatternPatt(getOptSpan(optOriginal),
                                    index,
                                    getOptScopeLayout());
    }
}
