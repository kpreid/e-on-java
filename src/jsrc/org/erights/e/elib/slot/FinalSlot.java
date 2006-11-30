package org.erights.e.elib.slot;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.develop.assertion.T;
import org.erights.e.elib.prim.StaticMaker;
import org.erights.e.elib.serial.PassByConstruction;
import org.erights.e.elib.tables.Selfless;

/**
 * An immutable slot whose initial value is its only value.
 *
 * @author Mark S. Miller
 */
public final class FinalSlot extends BaseSlot
  implements PassByConstruction, Selfless {

    static public final StaticMaker FinalSlotMaker =
      StaticMaker.make(FinalSlot.class);

    private final Object myValue;

    /**
     *
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
    public Object get() {
        return myValue;
    }

    /**
     * Complains that the variable is immutable
     */
    public void put(Object newValue) {
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
}
