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

import org.erights.e.elang.scope.EvalContext;
import org.erights.e.elang.scope.ScopeLayout;
import org.erights.e.elang.visitors.ETreeVisitor;
import org.erights.e.elib.base.SourceSpan;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.prim.StaticMaker;
import org.erights.e.elib.prim.Thrower;
import org.erights.e.elib.slot.Slot;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.FlexList;
import org.erights.e.elib.util.OneArgFunc;

import java.io.IOException;

/**
 * BNF: '&' ID (':' expr)?
 * <p/>
 * Defines an indirect variable whose slot is the specimen (as coerced by the
 * guard, if any).
 *
 * @author Mark S. Miller
 */
public final class SlotPattern extends NounPattern {

    /**
     * If 'varName' would shadow a non-shadowable, throw a (XXX to be defined)
     * exception instead.
     * <p/>
     * If the SlotPattern would not be well-formed, throw a (XXX to be defined)
     * exception instead.
     */
    public SlotPattern(SourceSpan optSpan,
                       AtomicExpr noun,
                       EExpr optGuardExpr,
                       ScopeLayout optScopeLayout) {
        super(optSpan, noun, optGuardExpr, false, optScopeLayout);
    }

    /**
     *
     */
    public SlotPattern(SourceSpan optSpan,
                       AtomicExpr noun,
                       EExpr optGuardExpr,
                       boolean canShadow,
                       ScopeLayout optScopeLayout) {
        super(optSpan, noun, optGuardExpr, canShadow, optScopeLayout);
    }

    /**
     * Uses XXX 'makeFoo(...)'
     */
    public Object[] getSpreadUncall() {
        Object[] result = {StaticMaker.make(SlotPattern.class),
          "run",
          getOptSpan(),
          getNoun(),
          getOptGuardExpr(),
          getOptScopeLayout()};
        return result;
    }

    /**
     *
     */
    public Object welcome(ETreeVisitor visitor) {
        return visitor.visitSlotPattern(this, myNoun, myOptGuardExpr);
    }

    /**
     *
     */
    protected StaticScope computeStaticScope() {
        StaticScope result = StaticScope.scopeSlot(myNoun);
        if (null == myOptGuardExpr) {
            return result;
        } else {
            return result.add(myOptGuardExpr.staticScope());
        }
    }

    /**
     *
     */
    public void subMatchBind(ConstList args,
                             Object specimen,
                             OneArgFunc optEjector,
                             FlexList bindings) {
        SlotPattern other;
        try {
            other = (SlotPattern)specimen;
        } catch (ClassCastException cce) {
            throw Thrower.toEject(optEjector, cce);
        }
        myNoun.subMatchBind(args, other.myNoun, optEjector, bindings);
        matchBind(myOptGuardExpr,
                  args,
                  other.myOptGuardExpr,
                  optEjector,
                  bindings);
    }

    /**
     *
     */
    void testMatch(EvalContext ctx, Object specimen, OneArgFunc optEjector) {
        getNoun().initSlot(ctx,
                           (Slot)E.as(coercedSpecimen(ctx,
                                                      specimen,
                                                      optEjector),
                                      Slot.class));
    }

    /**
     *
     */
    public NounPattern withNounExpr(NounExpr newNounExpr) {
        return new SlotPattern(getOptSpan(), newNounExpr, null,
                               // XXX is this right?
                               getOptScopeLayout());
    }

    /**
     *
     */
    public void subPrintOn(TextWriter out, int priority) throws IOException {
        out.print("&");
        myNoun.subPrintOn(out, priority);
        if (null != myOptGuardExpr) {
            out.print(" :");
            myOptGuardExpr.subPrintOn(out, PR_ORDER);
        }
    }
}
