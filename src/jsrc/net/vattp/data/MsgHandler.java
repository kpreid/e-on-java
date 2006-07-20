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
 * Interface to receive incoming messages.
 * <p/>
 * An object which implements the MsgHandler interface can be registered with a
 * VatTPConnection object to handle incoming messages. It's processMessage()
 * method will be called when a message of msgType is received from the remote
 * vat. It's connectionDead() method will be called when the connection dies.
 * <p/>
 * <p>The important method(s) are processMessage() and connectionDead().
 *
 * @author Bill Frantz
 */
public interface MsgHandler {

    /**
     * Receive notification that the connection is dead.
     * <p/>
     * Either the connection has been shut down with VatTPConnection.shutDownConnection,
     * the underlying TCP connection has failed, a bad MAC has been received on
     * the connection, or an internal error has caused the connection to fail.
     * <p/>
     * If an object is registered to handle more than one message type, it will
     * receive one connectionDead notification for each message type for which
     * it is registered.
     *
     * @param connection The VatTPConnection object which has just died.
     * @param reason     is a Throwable which describes why the connection
     *                   died.
     */
    public void connectionDead(VatTPConnection connection, Throwable reason);

    /**
     * Process an incoming message from the VatTPConnection.
     *
     * @param message    is the incoming message. The first byte (message[0])
     *                   is the message type (see class Msg). A handler which
     *                   is registered for more than one message can use the
     *                   initial byte to determine what is the message type of
     *                   the current message.
     * @param connection is the VatTPConnection object on which the the message
     *                   arrived.
     * @see Msg
     * @see VatTPConnection
     */
    public void processMessage(byte[] message, VatTPConnection connection);
}
