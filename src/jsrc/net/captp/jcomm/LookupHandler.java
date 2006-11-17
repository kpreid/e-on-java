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


/**
 * A handler for the special remote reference at position zero to the other
 * side's NonceLocator
 *
 * @author Mark S. Miller
 */
class LookupHandler extends RemoteHandler {

    /**
     * Construct a new LookupHandler.
     *
     * @param conn The CapTPConnection to communicate via.
     */
    LookupHandler(CapTPConnection conn) {
        super(conn, 0, null);
    }

    /**
     * Shouldn't get redirected, so always fresh.
     * <p/>
     * XXX kludge: even though this is made to appear as an unresolved remote
     * reference, ie, a remote promise.
     */
    public boolean isFresh() {
        return true;
    }
}
