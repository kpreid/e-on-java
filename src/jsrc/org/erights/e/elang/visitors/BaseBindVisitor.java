// Copyright 2003 Hewlett Packard, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.elang.visitors;

import org.erights.e.elang.evm.AtomicExpr;
import org.erights.e.elang.evm.BindingPattern;
import org.erights.e.elang.evm.EExpr;
import org.erights.e.elang.evm.ENode;
import org.erights.e.elang.evm.FinalPattern;
import org.erights.e.elang.evm.NounExpr;
import org.erights.e.elang.evm.NounPattern;
import org.erights.e.elang.evm.ParseNode;
import org.erights.e.elang.evm.SlotPattern;
import org.erights.e.elang.evm.VarPattern;
import org.erights.e.elang.scope.ScopeLayout;
import org.erights.e.elib.base.SourceSpan;

/**
 * @author Mark S. Miller
 * @author Based on {@link BindFramesVisitor} by Dean Tribble
 */
public abstract class BaseBindVisitor extends KernelECopyVisitor {

    ScopeLayout myLayout;

    /**
     * @param layout
     */
    BaseBindVisitor(ScopeLayout layout) {
        myLayout = layout;
    }

    /**
     * Returns my accumulated ScopeLayout, to be used in new ENodes I
     * construct.
     */
    public ScopeLayout getOptScopeLayout() {
        return myLayout;
    }

    /**
     *
     */
    abstract NounExpr newFinal(SourceSpan optSpan, String varName);

    /**
     *
     */
    abstract NounExpr newVar(SourceSpan optSpan, String varName);

    /**************************** EExprs **************************/

    /**
     *
     */
    public Object visitNounExpr(ENode optOriginal, String varName) {
        NounExpr optNoun = myLayout.getOptNoun(varName);
        if (null == optNoun) {
            ParseNode.fail("Undefined variable: " + varName, optOriginal);
        }
        return optNoun.withScopeLayout(getOptScopeLayout());
    }

    /**
     *
     */
    public Object visitAssignExpr(ENode optOriginal,
                                  AtomicExpr noun,
                                  EExpr rValue) {
        String varName = noun.asNoun().getName();
        NounPattern optNPatt = myLayout.getOptPattern(varName);
        if (optNPatt != null && optNPatt instanceof FinalPattern) {
            ParseNode.fail("Can't assign to final variable: " + varName, noun);
        }
        return super.visitAssignExpr(optOriginal, noun, rValue);
    }

    /***************************** Patterns *************************/

    /**
     *
     */
    public Object visitFinalPattern(ENode optOriginal,
                                    AtomicExpr nounExpr,
                                    EExpr optGuardExpr) {
        String varName = nounExpr.asNoun().getName();
        myLayout.requireShadowable(varName, nounExpr);
        NounExpr newNounExpr = newFinal(nounExpr.getOptSpan(), varName);
        NounPattern result = new FinalPattern(getOptSpan(optOriginal),
                                              newNounExpr,
                                              xformEExpr(optGuardExpr),
                                              getOptScopeLayout());
        myLayout = myLayout.with(varName, result);
        return result;
    }

    /**
     *
     */
    public Object visitVarPattern(ENode optOriginal,
                                  AtomicExpr nounExpr,
                                  EExpr optGuardExpr) {
        String varName = nounExpr.asNoun().getName();
        myLayout.requireShadowable(varName, nounExpr);
        NounExpr newNounExpr = newVar(nounExpr.getOptSpan(), varName);
        NounPattern result = new VarPattern(getOptSpan(optOriginal),
                                            newNounExpr,
                                            xformEExpr(optGuardExpr),
                                            getOptScopeLayout());
        myLayout = myLayout.with(varName, result);
        return result;
    }

    /**
     *
     */
    public Object visitSlotPattern(ENode optOriginal,
                                   AtomicExpr nounExpr,
                                   EExpr optGuardExpr) {
        String varName = nounExpr.asNoun().getName();
        myLayout.requireShadowable(varName, nounExpr);
        NounExpr newNounExpr = newVar(nounExpr.getOptSpan(), varName);
        NounPattern result = new SlotPattern(getOptSpan(optOriginal),
                                             newNounExpr,
                                             xformEExpr(optGuardExpr),
                                             getOptScopeLayout());
        myLayout = myLayout.with(varName, result);
        return result;
    }

    /**
     *
     */
    public Object visitBindingPattern(ENode optOriginal,
                                      AtomicExpr nounExpr) {
        String varName = nounExpr.asNoun().getName();
        myLayout.requireShadowable(varName, nounExpr);
        NounExpr newNounExpr = newVar(nounExpr.getOptSpan(), varName);
        NounPattern result = new BindingPattern(getOptSpan(optOriginal),
                                                newNounExpr,
                                                getOptScopeLayout());
        myLayout = myLayout.with(varName, result);
        return result;
    }
}
