package org.erights.e.elang.visitors;

import org.erights.e.elang.evm.AtomicExpr;
import org.erights.e.elang.evm.ENode;
import org.erights.e.elang.evm.FinalPattern;
import org.erights.e.elib.tables.ConstList;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............


/**
 *
 */
public class SubstVisitor extends KernelECopyVisitor {

    private final ConstList myArgs;

    /**
     *
     */
    public SubstVisitor(ConstList args) {
        myArgs = args;
    }

    /**************************** EExprs **************************/

    /**
     *
     */
    public Object visitQuasiLiteralExpr(ENode optOriginal, int index) {
        return myArgs.get(index);
    }

    /**************************** Patterns **************************/

    /**
     *
     */
    public Object visitQuasiLiteralPatt(ENode optOriginal, int index) {
        ENode node = (ENode)myArgs.get(index);
        if (node instanceof AtomicExpr) {
            return new FinalPattern(null, (AtomicExpr)node, null, null);
        } else {
            return node;
        }
    }
}
