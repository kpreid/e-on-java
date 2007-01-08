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
import org.erights.e.elib.prim.StaticMaker;
import org.erights.e.elib.prim.Thrower;
import org.erights.e.elib.ref.Ref;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.FlexList;
import org.erights.e.elib.util.OneArgFunc;

import java.io.IOException;


/**
 * BNF: "&" varName
 * <p/>
 * Returns the slot holding the noun's value
 *
 * @author Mark S. Miller
 * @author E. Dean Tribble
 */
public class SlotExpr extends EExpr {

    static private final long serialVersionUID = -1822302027328393752L;

    private final AtomicExpr myNoun;

    /**
     *
     */
    public SlotExpr(SourceSpan optSpan,
                    AtomicExpr noun,
                    ScopeLayout optScopeLayout) {
        super(optSpan, optScopeLayout);
        myNoun = noun;
    }

    /**
     * Uses XXX 'makeFoo(...)'
     */
    public Object[] getSpreadUncall() {
        Object[] result = {StaticMaker.make(SlotExpr.class),
          "run",
          getOptSpan(),
          myNoun,
          getOptScopeLayout()};
        return result;
    }

    /**
     *
     */
    public Object welcome(ETreeVisitor visitor) {
        return visitor.visitSlotExpr(this, myNoun);
    }

    /**
     *
     */
    protected StaticScope computeStaticScope() {
        return StaticScope.scopeRead(myNoun);
    }

    /**
     *
     */
    protected Object subEval(EvalContext ctx, boolean forValue) {
        return myNoun.asNoun().getSlot(ctx);
    }

    /**
     *
     */
    public void subMatchBind(ConstList args,
                             Object specimen,
                             OneArgFunc optEjector,
                             FlexList bindings) {
        SlotExpr other;
        try {
            other = (SlotExpr)Ref.resolution(specimen);
        } catch (ClassCastException cce) {
            //using a try/catch since success is typical and we have
            //to pay for the test in the cast anyway
            throw Thrower.toEject(optEjector, cce);
        }
        myNoun.subMatchBind(args, other.myNoun, optEjector, bindings);
    }

    /**
     * @deprecated Use {@link #getNoun()}
     */
    public NounExpr noun() {
        return myNoun.asNoun();
    }

    public AtomicExpr getNoun() {
        return myNoun;
    }

    /**
     *
     */
    public void subPrintOn(TextWriter out, int priority) throws IOException {
        out.print("&");
        myNoun.subPrintOn(out, PR_PRIM);
    }
}
