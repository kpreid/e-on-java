// Copyright 2006 Hewlett Packard, under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.elib.slot;

import org.erights.e.elib.prim.E;
import org.erights.e.elib.oldeio.TextWriter;

import java.io.IOException;

/**
 * @author Mark S. Miller
 */
public abstract class BaseSlot implements Slot {

    public Object getValue() {
        return get();
    }

    public void setValue(Object newValue) {
        put(newValue);
    }

    /**
     *
     */
    public void __printOn(TextWriter out) throws IOException {
        out.print("<& ");
        out.quote(get());
        out.print(">");
    }

    /**
     *
     */
    public String toString() {
        return E.toString(this);
    }

    public Slot readOnly() {
        return new ReadOnlySlot(this);
    }
}
