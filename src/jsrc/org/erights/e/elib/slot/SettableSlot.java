package org.erights.e.elib.slot;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.elib.oldeio.EPrintable;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.prim.StaticMaker;
import org.erights.e.elib.serial.PassByProxy;
import org.erights.e.elib.util.OneArgFunc;

import java.io.IOException;

/**
 * A mutable Slot that remembers a current value, and remembers a Guard
 * for coercing new values.
 *
 * @author Mark S. Miller
 */
public class SettableSlot implements Slot, EPrintable, PassByProxy {

    static public final StaticMaker SettableSlotMaker =
      StaticMaker.make(SettableSlot.class);

    private final Guard myGuard;

    private Object myValue;

    /**
     * Provide an already coerced value, and therefore don't need to provide
     * an optEjector.
     */
    public SettableSlot(Guard guard,
                        Object specimen,
                        OneArgFunc optEjector) {
        myGuard = guard;
        myValue = guard.coerce(specimen, optEjector);
    }

    /**
     * The most recently stored value.
     */
    public Object getValue() {
        return myValue;
    }

    /**
     * Store the coercion of the specimen. Since no ejector can be provided,
     * coercion failure is always thrown.
     */
    public void setValue(Object specimen) {
        myValue = myGuard.coerce(specimen, null);
    }

    /**
     * Return false
     */
    public boolean isFinal() {
        return false;
    }

    public Slot readOnly() {
        return new ReadOnlySlot(this);
    }

    /**
     *
     */
    public void __printOn(TextWriter out) throws IOException {
        out.print("<var ");
        out.quote(myValue);
        out.print(" :", myGuard, ">");
    }

    /**
     *
     */
    public String toString() {
        return E.toString(this);
    }
}
