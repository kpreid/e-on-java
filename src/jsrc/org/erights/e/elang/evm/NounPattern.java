package org.erights.e.elang.evm;

/*
The contents of this file are subject to the Electric Communities E Open
Source Code License Version 1.0 (the "License"); you may not use this file
except in compliance with the License. You may obtain a copy of the License
at http://www.communities.com/EL/.

Software distributed under the License is distributed on an "AS IS" basis,
WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
the specific language governing rights and limitations under the License.

The Original Code is the Distributed E Language Implementation, released
July 20, 1998.

The Initial Developer of the Original Code is Electric Communities.
Copyright (C) 1998 Electric Communities. All Rights Reserved.

Contributor(s): ______________________________________.
*/

import org.erights.e.develop.assertion.T;
import org.erights.e.elang.interp.ScopeSetup;
import org.erights.e.elang.scope.ScopeLayout;
import org.erights.e.elib.base.SourceSpan;

/**
 * BNF: ( | "var" | "&amp;") ID (':' expr)?
 * <p/>
 * For the real BNF, see the concrete subclasses.
 * <p/>
 * The identifier on the left is the defining occurrence of a variable name.
 * The expression on the right is a "guard expression". The object it evaluates
 * to will be treated as a Guard, and asked to make a value initialized from a
 * specimen, as if:
 * <pre>    def initialValue := guard.coerce(specimen, optEjector)</pre>
 * This initialValue is then used to define the variable of this name in the
 * scope starting immediately after this name. The way it's used depends on the
 * concrete subclass.
 * <p/>
 * But wait!  That scope includes the guard expression?!?  This circularity is
 * forbidden in the kernel-level language (e.g., in the parse node
 * FinalPattern). By imposing a well-formedness critereon, we can evaluate in
 * right-to-left order without violating left-to-right scoping.
 * <p/>
 * Unlike the "def" expression, if this circularity occurs in regular
 * (non-Kernel) E, this is still an error, rather than being rewritten, since
 * this circularity here is more likely to indicate programmer confusion than
 * an attempt to do something fancy.
 *
 * @author Mark S. Miller
 * @see DefineExpr
 * @see FinalPattern
 * @see SlotPattern
 */
public abstract class NounPattern extends GuardedPattern {

    static private final long serialVersionUID = -4240696966775660261L;

    final AtomicExpr myNoun;

    /**
     * If noun's name would shadow a non-shadowable, throw a (XXX to be
     * defined) exception instead.
     * <p/>
     * If the pattern would not be well-formed, throw a (XXX to be defined)
     * exception instead. This must be handled by the subclass' constructor.
     *
     * @param optSpan
     * @param noun
     * @param optGuardExpr
     * @param canShadow
     * @param optScopeLayout
     */
    NounPattern(SourceSpan optSpan,
                AtomicExpr noun,
                EExpr optGuardExpr,
                boolean canShadow,
                ScopeLayout optScopeLayout) {
        super(optSpan, optGuardExpr, optScopeLayout);
        myNoun = noun;
        if (myNoun instanceof NounExpr) {
            String varName = myNoun.asNoun().getName();
            // NOTE non-shadowable variables seem like a bad idea....
            if (!canShadow && ScopeSetup.NonShadowable.contains(varName)) {
                ParseNode.fail("can't redefine " + varName, noun);
            }
        }
        ensureWellFormed();
    }

    /**
     *
     */
    private void ensureWellFormed() {
        String optName = getOptName();
        if (null != optName && null != myOptGuardExpr) {
            StaticScope guardScope = myOptGuardExpr.staticScope();
            if (guardScope.namesUsed().maps(optName)) {
                T.fail("kernel guard cycle not allowed: " + optName);
            }
        }
    }

    /**
     * Fails if the nounExpr part is still quasi.
     */
    public NounExpr getNoun() {
        return myNoun.asNoun();
    }

    /**
     * Returns null if the nounExpr part is still quasi.
     */
    public String getOptName() {
        if (myNoun instanceof NounExpr) {
            return myNoun.asNoun().getName();
        } else {
            return null;
        }
    }

    /**
     *
     */
    public abstract NounPattern withNounExpr(NounExpr newNounExpr);
}
