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

import org.erights.e.elib.ref.Ref;

/**
 * Handles an unresolved remote reference (a RemotePromise).
 *
 * @author Mark S. Miller
 */
class RemotePromiseHandler extends RemoteHandler {

    /**
     * Flag to remember whether any E-level messages have been sent over me.
     */
    private boolean myFreshFlag = true;

    /**
     * Construct a new RemotePromiseHandler.
     *
     * @param conn The CapTPConnection to communicate via
     * @param pos  The Imports or Questions map pos of the object
     */
    RemotePromiseHandler(CapTPConnection conn, int pos) {
        super(conn, pos, null);
    }

    /**
     * Override to also clear myFreshFlag
     */
    public void handleRegularSendAllOnly(String verb, Object[] args) {
        myFreshFlag = false;
        super.handleRegularSendAllOnly(verb, args);
    }

    /**
     * Override to also clear myFreshFlag
     */
    public Ref handleRegularSendAll(String verb, Object[] args) {
        myFreshFlag = false;
        return super.handleRegularSendAll(verb, args);
    }

    /**
     * Have no E-level messages yet been sent over me?
     */
    public boolean isFresh() {
        return myFreshFlag;
    }
}
