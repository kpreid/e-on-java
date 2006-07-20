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
import org.erights.e.elang.scope.EvalContext;
import org.erights.e.elang.scope.ScopeLayout;
import org.erights.e.elib.base.SourceSpan;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.StaticMaker;
import org.erights.e.elib.slot.FinalSlot;
import org.erights.e.elib.slot.Slot;
import org.erights.e.meta.java.math.EInt;

import java.io.IOException;

/**
 * What an object expression evaluates to.
 *
 * @author E. Dean Tribble
 */
public class FrameFinalNounExpr extends NestNounExpr {

    private final int myIndex;

    FrameFinalNounExpr(SourceSpan optSpan,
                       String name,
                       int index,
                       ScopeLayout optScopeLayout) {
        super(optSpan, name, optScopeLayout);
        myIndex = index;
    }

    /**
     * Uses XXX 'makeFoo(...)'
     */
    public Object[] getSpreadUncall() {
        Object[] result = { StaticMaker.make(FrameFinalNounExpr.class),
                            "run",
                            getOptSpan(),
                            getName(),
                            EInt.valueOf(myIndex),
                            getOptScopeLayout() };
        return result;
    }

    /**
     *
     */
    public Slot getSlot(EvalContext ctx) {
        return new FinalSlot(ctx.field(myIndex));
    }

    /**
     *
     */
    protected Object subEval(EvalContext ctx, boolean forValue) {
        return ctx.field(myIndex);
    }

    /**
     *
     */
    public void subPrintOn(TextWriter out, int priority) throws IOException {
        super.subPrintOn(out, priority);
//        out.print("$ff", new Integer(myIndex));
    }

    /**
     * Return the kind of object that should be stored in a frame, assuming
     * an accessor of the same type as the receiver.
     */
    public Object getRepresentation(EvalContext ctx) {
        return ctx.field(myIndex);
    }

    /**
     *
     */
    public NounExpr asFieldAt(int index) {
        return new FrameFinalNounExpr(getOptSpan(),
                                      getName(),
                                      index,
                                      getOptScopeLayout());
    }

    /**
     *
     */
    public NounExpr withScopeLayout(ScopeLayout optScopeLayout) {
        return new FrameFinalNounExpr(getOptSpan(),
                                      getName(),
                                      myIndex,
                                      optScopeLayout);
    }

    /**
     *
     */
    public void initFinal(EvalContext ctx, Object value) {
        ctx.initField(myIndex, value);
    }

    /**
     * Shouldn't happen
     */
    public void initSlot(EvalContext ctx, Slot slot) {
        T.fail("Internal: Not a slot variable: " + this);
    }
}
