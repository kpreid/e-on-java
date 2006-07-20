package org.erights.e.elib.sealing;

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

import org.erights.e.elib.serial.PassByProxy;
import org.erights.e.elib.serial.Persistent;

/**
 * Corresponds to an encrypting or signing key.
 *
 * @author Mark S. Miller
 */
public final class Sealer implements PassByProxy, Persistent {

    /**
     * @serial The Brand of this Sealer/Unsealer pair.
     */
    private final Brand myBrand;

    /**
     *
     */
    Sealer(Brand brand) {
        myBrand = brand;
    }

    /**
     * Brand of this Sealer/Unsealer pair
     */
    public Brand getBrand() {
        return myBrand;
    }

    /**
     * @return a SealedBox containing contents that can only be
     *         unsealed with the {@link Unsealer} of this Brand.
     */
    public SealedBox seal(Object contents) {
        return new SealedBox(contents, myBrand);
    }

    /**
     * Prints using the Brand's name
     */
    public String toString() {
        return "<" + myBrand + " sealer>";
    }
}
