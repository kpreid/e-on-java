package org.erights.e.elang.visitors;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.elang.evm.GuardedPattern;
import org.erights.e.elang.evm.LocalFinalNounExpr;
import org.erights.e.elang.evm.LocalSlotNounExpr;
import org.erights.e.elang.evm.NounExpr;
import org.erights.e.elang.evm.ObjectExpr;
import org.erights.e.elang.scope.ScopeLayout;
import org.erights.e.elib.base.SourceSpan;

/**
 * @author E. Dean Tribble
 */
class BindNestedFramesVisitor extends BindFramesVisitor {

    private int myNextLocal;

    /**
     *
     */
    BindNestedFramesVisitor(ScopeLayout bindings,
                            int localN,
                            int[] localsCell,
                            ObjectExpr optSource) {
        super(bindings, localsCell, optSource);
        myNextLocal = localN;
        myOptSource = optSource;
    }

    /**
     *
     */
    KernelECopyVisitor nest(GuardedPattern oName) {
        return new BindNestedFramesVisitor(myLayout.nest(oName.getOptName()),
                                           myNextLocal,
                                           myMaxLocalsCell,
                                           myOptSource);
    }

    /**
     *
     */
    KernelECopyVisitor nest() {
        return new BindNestedFramesVisitor(myLayout.nest(),
                                           myNextLocal,
                                           myMaxLocalsCell,
                                           myOptSource);
    }

    /**
     *
     */
    NounExpr newFinal(SourceSpan optSpan, String varName) {
        return new LocalFinalNounExpr(optSpan,
                                      varName,
                                      nextLocal(),
                                      getOptScopeLayout());
    }

    /**
     *
     */
    NounExpr newVar(SourceSpan optSpan, String varName) {
        return new LocalSlotNounExpr(optSpan,
                                     varName,
                                     nextLocal(),
                                     getOptScopeLayout());
    }

    /**
     *
     */
    private int nextLocal() {
        int index = myNextLocal;
        myNextLocal++;
        myMaxLocalsCell[0] = StrictMath.max(myNextLocal, myMaxLocalsCell[0]);
        return index;
    }
}
