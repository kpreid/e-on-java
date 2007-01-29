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

import java.math.BigInteger;

/**
 * The first-time encoding of an exported Promise over the wire, received as an
 * imported RemotePromise. <p>
 * <p/>
 * This is a separate class because the first time we export we also need to
 * hook up the redirector, whereas the rest of the time we can just use
 * ImportDesc(importPos).
 *
 * @author Chip Morningstar
 * @author Mark S. Miller
 */
class NewRemotePromiseDesc implements ObjectRefDesc {

    static private final long serialVersionUID = -6186892267621515701L;

    private final int myImportPos;

    private final int myRdrPos;

    private final BigInteger myRdrBase;

    /**
     *
     */
    private void validate() {
        T.requireSI(1 <= myImportPos,
                    "importPos must be positive: ",
                    myImportPos);
        T.requireSI(-1 >= myRdrPos, "rdrPos must be negative: ", myRdrPos);
        T.require(null != myRdrBase && 1 <= myRdrBase.signum(),
                  "rdrBase must be positive: ",
                  myRdrBase);
    }

    /**
     * Constructor.
     */
    NewRemotePromiseDesc(int importPos, int rdrPos, BigInteger rdrBase) {
        myImportPos = importPos;
        myRdrPos = rdrPos;
        myRdrBase = rdrBase;
        validate();
    }

    /**
     * What the other side exported, we dereference as the RemotePromise we
     * will now import.
     */
    public Object dereference(CapTPConnection conn) {

        Object result =
          conn.newRemotePromise(myImportPos, myRdrPos, myRdrBase);

        if (conn.debug(myImportPos)) {
            conn.debugm(myImportPos, "deref " + toString());
        }
        return result;
    }

    /**
     *
     */
    public String toString() {
        return "NewRemotePromiseDesc(" + myImportPos + ", " + myRdrPos + ", " +
          myRdrBase + ")";
    }
}
