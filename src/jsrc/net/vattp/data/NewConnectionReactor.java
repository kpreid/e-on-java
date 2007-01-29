package net.vattp.data;

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
 * Interface for noticing new connections.
 *
 * @author Bill Frantz
 */
public interface NewConnectionReactor {

    /**
     * React to the arrival of a new connection. If the object implementing
     * NewConnectionReactor is registered through addNewConnectionReactor on a
     * VatTPMgr object, this invocation of it is expected to connect up
     * MsgHandlers for all the messages processed by higher layers in the
     * protocol. This notification is presented for all DataConnections,
     * whether the connection originated on this machine or on the remote
     * machine.
     *
     * @param connection is the new VatTPConnection object.
     */
    void reactToNewConnection(VatTPConnection connection);
}
