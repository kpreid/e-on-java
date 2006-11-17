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

import org.erights.e.elib.prim.MirandaMethods;
import org.erights.e.elib.ref.Ref;
import org.erights.e.elib.serial.PassByProxy;
import org.erights.e.elib.serial.Persistent;

/**
 * Corresponds to a decrypting or signature-verifying key
 *
 * @author Mark S. Miller
 */
public final class Unsealer implements PassByProxy, Persistent {

    /**
     * @serial The Brand of this Sealer/Unsealer pair
     */
    private final Brand myBrand;

    /**
     * @param brand
     */
    Unsealer(Brand brand) {
        myBrand = brand;
    }

    /**
     * The Brand of this Sealer/Unsealer pair.
     */
    public Brand getBrand() {
        return myBrand;
    }

    /**
     * If box was sealed by the Sealer of the same Brand, return its contents.
     * Otherwise throw UnsealingException.
     */
    public Object unseal(SealedBox box) throws UnsealingException {
        if (myBrand == box.getBrand()) {
            return box.myContents;
        } else {
            throw new UnsealingException("" + this + " can't unseal " + box);
        }
    }

    /**
     * If 'optBox' isn't null, it unseals with this sealer, and it's contents
     * is of type 'type', then return those contents; otherwise return null.
     */
    public Object optUnseal(SealedBox optBox, Class type) {
        if (null == optBox) {
            return null;
        }
        if (myBrand != optBox.getBrand()) {
            return null;
        }
        Object result = optBox.myContents;
        if (type.isInstance(result)) {
            return result;
        } else {
            return null;
        }
    }

    /**
     * Used in an Unsealer's role as a Guard.
     * <p/>
     * A specimen is amplified to the contents of the SealedBox it {@link
     * MirandaMethods#__optSealedDispatch offers} for being unsealed by this
     * sealer.
     *
     * @return Either null, to indicate that no box was offered, or a singleton
     *         list containing the contents of the box. The contents are
     *         enclosed in a list so that null can be used to indicate lack of
     *         match, without being confused with null as the box contents.
     * @throws UnsealingException If optBox isn't null but fails to unseal,
     *                            that's a thrown exception rather than a
     *                            polite report of failure.
     */
    public Object[] amplify(Object specimen) throws UnsealingException {
        SealedBox optBox = Ref.optSealedDispatch(specimen, myBrand);
        if (null == optBox) {
            return null;
        }
        Object[] result = {unseal(optBox)};
        return result;
    }

    /**
     * Prints using the Brand's name
     */
    public String toString() {
        return "<" + myBrand + " unsealer>";
    }
}
