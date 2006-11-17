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
import org.erights.e.elang.syntax.ELexer;
import org.erights.e.elang.visitors.ETreeVisitor;
import org.erights.e.elib.base.SourceSpan;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.Thrower;
import org.erights.e.elib.ref.Ref;
import org.erights.e.elib.slot.FinalSlot;
import org.erights.e.elib.slot.Slot;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.FlexList;
import org.erights.e.elib.util.OneArgFunc;

import java.io.IOException;

/**
 * BNF: varName
 * <p/>
 * Returns the value of the variable of this name in the current scope.
 *
 * @author Mark S. Miller
 * @author E. Dean Tribble
 */
public abstract class NounExpr extends AtomicExpr {

    /**
     *
     */
    private final String myName;

    /**
     *
     */
    NounExpr(SourceSpan optSpan, String name, ScopeLayout optScopeLayout) {
        super(optSpan, optScopeLayout);
        myName = name;
        T.require(!name.startsWith("&"),
                  "Internal: Name vs Slot confusion: ",
                  name);
    }

    /**
     * To E code, this acts as a constructor on NounExpr
     *
     * @param optSpan
     * @param name
     * @param optScopeLayout
     * @return
     */
    static public NounExpr run(SourceSpan optSpan,
                               String name,
                               ScopeLayout optScopeLayout) {
        return new SimpleNounExpr(optSpan, name, optScopeLayout);
    }

    /**
     *
     */
    public NounExpr asNoun() {
        return this;
    }

    /**
     *
     */
    public Object welcome(ETreeVisitor visitor) {
        return visitor.visitNounExpr(this, myName);
    }

    /**
     *
     */
    protected StaticScope computeStaticScope() {
        return StaticScope.scopeRead(this);
    }

    /**
     * Default implementation of noun eval in terms of its slot.
     */
    protected Object subEval(EvalContext ctx, boolean forValue) {
        return getSlot(ctx).getValue();
    }

    /**
     *
     */
    public void subMatchBind(ConstList args,
                             Object specimen,
                             OneArgFunc optEjector,
                             FlexList bindings) {
        NounExpr other;
        try {
            other = (NounExpr)Ref.resolution(specimen);
        } catch (ClassCastException cce) {
            //using a try/catch since success is typical and we have
            //to pay for the test in the cast anyway
            throw Thrower.toEject(optEjector, cce);
        }
        if (!myName.equals(other.myName)) {
            throw Thrower.toEject(optEjector,
                                  "Mismatch: " + myName + " vs " + other
                                    .myName);
        }
    }

    /**
     * @deprecated Use {@link #getName()}
     */
    public String name() {
        return myName;
    }

    public String getName() {
        return myName;
    }

    /**
     *
     */
    public void subPrintOn(TextWriter out, int priority) throws IOException {
        ELexer.printNounOn(myName, out);
    }

    /**
     * Return the kind of object that should be stored in a frame, assuming an
     * accessor of the same type as the receiver.
     * <p/>
     * Default implementation of getRespresentation in terms of the slot. Used
     * for transferring the contents of the slot into a frame field from an
     * outer scope.
     */
    public Object getRepresentation(EvalContext ctx) {
        return getSlot(ctx);
    }

    /**
     * Default implementation of assign in terms of the slot. Note that this
     * raises the correct exception if the slot is a final slot.
     */
    public void assign(EvalContext ctx, Object value) {
        getSlot(ctx).setValue(value);
    }

    /**
     * Initialize a final variable's value when it first comes into scope.
     */
    public void initFinal(EvalContext ctx, Object value) {
        initSlot(ctx, new FinalSlot(value));
    }

    /**
     * Initialize a slot variable when it first comes into scope.
     */
    public abstract void initSlot(EvalContext ctx, Slot slot);

    /**
     * Return a slot object from the EvalContext for the noun designated by the
     * receiver.
     */
    public abstract Slot getSlot(EvalContext ctx);

    /**
     *
     */
    public abstract boolean isOuter();

    /**
     * Return a noun that could access the representation of the receiver if it
     * were at in a frame at the given index. This is used to maintain
     * final/var distinctions as slots are copied into frames.
     */
    public abstract NounExpr asFieldAt(int index);

    /**
     *
     */
    public abstract NounExpr withScopeLayout(ScopeLayout optScopeLayout);
}
