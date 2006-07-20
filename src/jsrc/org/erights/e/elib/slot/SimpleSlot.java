package org.erights.e.elib.slot;

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

import org.erights.e.elib.oldeio.EPrintable;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.prim.StaticMaker;
import org.erights.e.elib.serial.PassByProxy;

import java.io.IOException;


/**
 * A coercion-free settable Slot.
 *
 * @author Mark S. Miller
 */
public class SimpleSlot implements Slot, EPrintable, PassByProxy {

    static public final StaticMaker SimpleSlotMaker =
      StaticMaker.make(SimpleSlot.class);

    private Object myValue;

    /**
     *
     */
    public SimpleSlot(Object initValue) {
        myValue = initValue;
    }

    /**
     * The value last set
     */
    public Object getValue() {
        return myValue;
    }

    /**
     * Sets the value to the specimen.
     */
    public void setValue(Object specimen) {
        myValue = specimen;
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
        out.print(">");
    }

    /**
     *
     */
    public String toString() {
        return E.toString(this);
    }
}
