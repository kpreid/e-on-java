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
 * Carries a reference, but reveals it only to one who has this
 * Brand's Unsealer.
 *
 * @author Mark S. Miller
 */
public class SealedBox implements PassByProxy, Persistent {

    /**
     * @serial The capability I encapsulate.
     */
    final Object myContents;

    /**
     * @serial The Brand of the {@link Sealer} that sealed this box,
     * and therefore the Brand of the {@link Unsealer} required to
     * obtain myContents.
     */
    private final Brand myBrand;

    /**
     *
     */
    SealedBox(Object contents, Brand brand) {
        myContents = contents;
        myBrand = brand;
    }

    /**
     * The Brand of the {@link Sealer} that sealed this box,
     * and therefore the Brand of the {@link Unsealer} required to
     * obtain my contents.
     */
    public Brand getBrand() {
        return myBrand;
    }

    /**
     * Prints using the Brand's name
     */
    public String toString() {
        return "<sealed by " + myBrand + ">";
    }
}
