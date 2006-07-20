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

import org.erights.e.develop.assertion.T;
import org.erights.e.develop.trace.Trace;
import org.erights.e.elib.util.HexStringUtils;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

/**
 * Class to handle the suspension and shutdown messages.
 *
 * @author Bill Frantz
 */
class Suspend implements MsgHandler {

    /**
     * Constuct an object to handle the SUSPEND message (which also acts as the
     * shutdown message).
     *
     * @param connection is the VatTPConnection to work with.
     */
    Suspend(VatTPConnection connection) {
        try {
            connection.registerMsgHandler(Msg.SUSPEND, this);
        } catch (IOException e) {
            Trace.comm.errorm("Already registered??");
        }
    }

    /**
     * Handle a dead connection.
     *
     * @param connection is the VatTPConnection which died.
     */
    public void connectionDead(VatTPConnection connection, Throwable reason) {
        // nothing to do
    }
//Methods from the MsgHandler interface

    /**
     * Handle incoming Suspend messages
     *
     * @param message    is the incoming message.
     * @param connection is the VatTPConnection which died.
     */
    public void processMessage(byte[] message, VatTPConnection connection) {
        T.require(Msg.SUSPEND == message[0],
                  "Message not a SUSPEND message\n",
                  HexStringUtils.bytesToReadableHexStr(message));
        if (1 == message.length) {
            // Message is a shutdown message
            if (Trace.comm.debug && Trace.ON) {
                Trace.comm.debugm("Shutting down connection " + connection);
            }
            // shutdownConnection will send shutdown message.
            connection.shutDownConnection(new ConnectionShutDownException(
              "Other End Requested Shutdown"));
            connection.close();                 // Close TCP
            return;
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(message);
        DataInputStream is = new DataInputStream(bais);
        try {
            is.readByte();  //Discard the message type
            byte[] suspendID = new byte[is.readUnsignedShort()];
            is.read(suspendID);
            connection.handleSuspend(suspendID);
        } catch (IOException e) {
            Trace.comm.errorm("IOException reading suspendID", e);
            connection.shutDownConnection();    // Will send shutdown message.
            connection.close();                 // Close TCP
        }
    }
}
