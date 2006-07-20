package org.erights.e.elib.vat;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.develop.assertion.T;
import org.erights.e.elib.ref.Ref;

/**
 * Used to represent the identity of a PassByProxy object in the
 * boot-comm-system.
 * <p/>
 * BootRefIdentity must also encapsulate the object whose identity it
 * represents, since it represents *only* that object's indentity.
 *
 * @author Mark S. Miller
 */
class BootRefIdentity {

    /**
     *
     */
    private final Object myActual;

    /**
     *
     */
    BootRefIdentity(Object actual) {
        T.require(Ref.isPassByProxy(actual),
                  "Must be PassByProxy: ", actual);
        myActual = actual;
    }

    /**
     * Only if the other is a BootRefIdentity onto the same (Java ==) object
     */
    public boolean equals(Object other) {
        if (other instanceof BootRefIdentity) {
            return myActual == ((BootRefIdentity)other).myActual;
        } else {
            return false;
        }
    }

    /**
     * Based on {@link System#identityHashCode}
     */
    public int hashCode() {
        return System.identityHashCode(myActual);
    }
}
