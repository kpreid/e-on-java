package org.erights.e.elib.slot;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.develop.assertion.T;
import org.erights.e.elib.oldeio.EPrintable;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.prim.StaticMaker;
import org.erights.e.elib.serial.PassByConstruction;
import org.erights.e.elib.tables.Selfless;

import java.io.IOException;

/**
 * An immutable slot whose initial value is its only value.
 *
 * @author Mark S. Miller
 */
public final class FinalSlot
  implements Slot, PassByConstruction, Selfless, EPrintable {

    static public final StaticMaker FinalSlotMaker =
      StaticMaker.make(FinalSlot.class);

    private final Object myValue;

    /**
     *
     * @param initValue
     */
    public FinalSlot(Object initValue) {
        myValue = initValue;
    }

    /**
     * Uses <tt>FinalSlotMaker(myValue)</tt>
     */
    public Object[] getSpreadUncall() {
        Object[] result = {FinalSlotMaker, "run", myValue};
        return result;
    }

    /**
     * @return the value this slot is immutably bound to.
     */
    public Object getValue() {
        return myValue;
    }

    /**
     * Complains that the variable is immutable
     */
    public void setValue(Object newValue) {
        T.fail("Final variables may not be changed");
    }

    /**
     * Returns true
     */
    public boolean isFinal() {
        return true;
    }

    /**
     * Returns itself
     */
    public Slot readOnly() {
        return this;
    }

    /**
     *
     */
    public void __printOn(TextWriter out) throws IOException {
        out.print("<& ");
        out.quote(myValue);
        out.print(">");
    }

    /**
     *
     */
    public String toString() {
        return E.toString(this);
    }
}
