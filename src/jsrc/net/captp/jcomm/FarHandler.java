package net.captp.jcomm;

import java.math.BigInteger;

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


/**
 * A resolved reference that's still remote is necessarily a reference to a
 * PassByProxy object.
 *
 * @author Mark S. Miller
 */
class FarHandler extends RemoteHandler {

//    private final BigInteger mySwissHash;

    /**
     * Construct a new FarHandler.
     *
     * @param conn      The CapTPConnection to communicate via
     * @param pos       The Imports or Questions map pos of the object
     * @param swissHash The identity (within the connection's remote vat) of
     *                  the object this FarRef designates.
     */
    FarHandler(CapTPConnection conn, int pos, BigInteger swissHash) {
        super(conn, pos, new ObjectID(conn.remoteVatID(), swissHash));
//        mySwissHash = swissHash;
    }

    /**
     * Always resolved, so always fresh
     */
    public boolean isFresh() {
        return true;
    }
}
