package org.erights.e.elib.slot;

import org.erights.e.elib.oldeio.EPrintable;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............


/**
 * A Slot implements a potentially mutable variable as an object with a "value"
 * property. <p>
 * <p/>
 * This supertype only provides the accessor function, since the setting
 * function is optional.
 *
 * @author Mark S. Miller
 */
public interface Slot extends EPrintable {

    /**
     * @deprecated Use {@link #get()}.
     */
    Object getValue();

    /**
     * Get the current value of the variable.
     */
    Object get();

    /**
     * @deprecated Use {@link #put(Object)}.
     */
    void setValue(Object newValue);

    /**
     * Sets the current value of the variable to be a coercion of specimen to a
     * value that meets the constraints represented by this slot.
     * <p/>
     * get() will then return this value. If the variable is final
     * (immutable), then throw an informative exception instead.
     */
    void put(Object newValue);

    /**
     * Says whether the Slot acts like a {@link FinalSlot} -- successive
     * get()s on the same slot will always give the same value.
     * <p/>
     * Note that final implies read-only, but read-only does not imply final.
     */
    boolean isFinal();

    /**
     * Returns a facet allowing get/0 but not put/1.
     * <p/>
     * <tt>slot.readOnly().isFinal()</tt> should be the same as
     * <tt>slot.isFinal()</tt>.
     */
    Slot readOnly();
}
