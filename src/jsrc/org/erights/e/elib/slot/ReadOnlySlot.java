// Copyright 2005 Mark S. Miller, under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.elib.slot;

import org.erights.e.develop.assertion.T;
import org.erights.e.elib.oldeio.TextWriter;

import java.io.IOException;

/**
 * @author Mark S. Miller
 */
public class ReadOnlySlot extends BaseSlot {

    private final Slot mySlot;

    public ReadOnlySlot(Slot slot) {
        mySlot = slot;
    }

    /**
     * Asks mySlot
     */
    public Object get() {
        return mySlot.get();
    }

    /**
     * Refuses
     */
    public void put(Object newValue) {
        T.fail("A ReadOnlySlot may not be assigned to");

    }

    /**
     * Asks mySlot
     */
    public boolean isFinal() {
        return mySlot.isFinal();
    }

    /**
     * Returns itself
     */
    public Slot readOnly() {
        return this;
    }

    public void __printOn(TextWriter out) throws IOException {
        out.print("<ro ", mySlot, ">");
    }
}
