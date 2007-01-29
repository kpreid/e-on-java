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

/**
 * The encoding of an remote ref over the wire to the ref's originator, to be
 * imported as the object designated by that ref. <p>
 * <p/>
 * If myIncomingPos > 0, it decodes to an export-table entry. <br> If
 * myIncomingPos == 0, an invalid way to refer to the nonce locator.<br> If
 * myIncomingPos < 0, it decodes to an answers-table entry. <p>
 *
 * @author Chip Morningstar
 * @author Mark S. Miller
 */
class IncomingDesc implements ObjectRefDesc {

    static private final long serialVersionUID = 7071220903919564143L;

    private final int myIncomingPos;

    /**
     *
     */
    private void validate() {
        T.requireSI(0 != myIncomingPos,
                    "can't directly reify nonce locator: ",
                    myIncomingPos);
    }

    /**
     * Constructor.
     */
    IncomingDesc(int incomingPos) {
        myIncomingPos = incomingPos;
        validate();
    }

    /**
     * What the other side imported, we dereference as an object we exported.
     */
    public Object dereference(CapTPConnection conn) {
        if (conn.debug(myIncomingPos)) {
            conn.debugm(myIncomingPos, "deref " + toString());
        }
        return conn.getIncoming(myIncomingPos);
    }

    /**
     *
     */
    public String toString() {
        return "IncomingDesc(" + myIncomingPos + ")";
    }
}
