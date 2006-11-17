package org.erights.e.elang.visitors;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.elang.evm.AtomicExpr;
import org.erights.e.elang.evm.EExpr;
import org.erights.e.elang.evm.ENode;
import org.erights.e.elang.evm.GuardedPattern;
import org.erights.e.elib.base.ValueThunk;
import org.erights.e.elib.tables.FlexMap;


/**
 * 'renamings' contains a set of oldName => newName pairs. 'rename' returns a
 * new expression like this one, except that wherever 'oldName' is used in this
 * expression to refer to a definition of 'oldName' not provided by this
 * expression, 'newName' is substituted. It is assumed that 'newName' cannot
 * conflict with any name already in this expression, which is safe if
 * 'newName' is a newly generated temporary name. In the resulting expression's
 * staticScope's namesUsed, 'oldName' should not appear.<p>
 * <p/>
 * For each 'oldName' that appears in this expressions's staticScope's outNames
 * (and therefore in the result's staticScope's outNames), that 'oldName' =>
 * 'newName' association is removed from 'renamings'. Therefore, 'renamings' is
 * left in a valid state to apply to expressions at the same scope level, but
 * to the right of this one. <p>
 * <p/>
 * Note to implementors: The Java Language Specification defines that argument
 * evaluation order is left to right (as does E). Therefore, you may make
 * several calls to 'rename' using the same 'renamings', confident that the
 * earlier ones will modify the 'renamings' as seen by the later ones. Should
 * these methods be translated to a language without this guarantee (like C++),
 * this ordering will have to be recoded.
 */
public class RenameVisitor extends CopyVisitor {

    private final FlexMap myRenamings;

    /**
     *
     */
    public RenameVisitor(FlexMap renamings) {
        myRenamings = renamings;
    }

    /**
     * @return A RenameVisitor with a diverge() of the current renamings map.
     */
    KernelECopyVisitor nest(GuardedPattern oName) {
        return new RenameVisitor(myRenamings.diverge());
    }

    /**
     * @return A RenameVisitor with a diverge() of the current renamings map.
     */
    KernelECopyVisitor nest() {
        return new RenameVisitor(myRenamings.diverge());
    }

    /**************************** EExprs **************************/

    /**
     *
     */
    public Object visitNounExpr(ENode optOriginal, String varName) {
        String newVarName =
          (String)myRenamings.fetch(varName, new ValueThunk(varName));
        return super.visitNounExpr(optOriginal, newVarName);
    }

    /***************************** Patterns *************************/

    /**
     *
     */
    public Object visitFinalPattern(ENode optOriginal,
                                    AtomicExpr nounExpr,
                                    EExpr optGuardExpr) {
        myRenamings.removeKey(nounExpr.asNoun().getName());
        return super.visitFinalPattern(optOriginal, nounExpr, optGuardExpr);
    }

    /**
     *
     */
    public Object visitVarPattern(ENode optOriginal,
                                  AtomicExpr nounExpr,
                                  EExpr optGuardExpr) {
        myRenamings.removeKey(nounExpr.asNoun().getName());
        return super.visitVarPattern(optOriginal, nounExpr, optGuardExpr);
    }

    /**
     *
     */
    public Object visitSlotPattern(ENode optOriginal,
                                   AtomicExpr nounExpr,
                                   EExpr optGuardExpr) {
        myRenamings.removeKey(nounExpr.asNoun().getName());
        return super.visitSlotPattern(optOriginal, nounExpr, optGuardExpr);
    }
}
