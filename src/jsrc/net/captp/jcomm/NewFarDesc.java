package net.captp.jcomm;

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

import org.erights.e.develop.assertion.T;
import org.erights.e.elib.ref.Ref;

import java.math.BigInteger;

/**
 * The first-time encoding of an exported pass-by-proxy object over the wire,
 * to be imported as a new Far reference. <p>
 * <p/>
 * This is a separate class because the first time we export we also need to
 * include the swissHash, whereas the rest of the time we can just use
 * ImportDesc(importPos).
 *
 * @author Chip Morningstar
 * @author Mark S. Miller
 */
class NewFarDesc implements ObjectRefDesc {

    static private final long serialVersionUID = 2229110355967576618L;

    private final int myImportPos;

    private final BigInteger mySwissHash;

    /**
     *
     */
    private void validate() {
        T.requireSI(myImportPos >= 1,
                    "importPos must be positive: ",
                    myImportPos);
        T.require(null != mySwissHash && mySwissHash.signum() >= 1,
                  "swissHash must be positive: ",
                  mySwissHash);
    }

    /**
     * Constructor.
     */
    NewFarDesc(int importPos, BigInteger swissHash) {
        myImportPos = importPos;
        mySwissHash = swissHash;
        validate();
    }

    /**
     * What the other side exported, we dereference as the "new" Far reference
     * we will now import.
     */
    public Object dereference(CapTPConnection conn) {
        validate();
        Ref result = conn.newFarRef(myImportPos, mySwissHash);

        if (conn.debug(myImportPos)) {
            conn.debugm(myImportPos, "deref " + toString());
        }
        return result;
    }

    /**
     *
     */
    public String toString() {
        return "NewFarDesc(" + myImportPos + ", " + mySwissHash + ")";
    }
}
