package net.vattp.tunnel;

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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Vector;


/**
 * The class which represents an individual HTTP proxy client vat.
 *
 * @author Bill Frantz
 */

public class HTTPClient {

    /**
     * Incoming TCP sockets. The connectionIDs are negative. The data is
     * HTTPSocketCtl.
     */
    private final Vector myInTCP = new Vector(1);

    /**
     * Outgoing TCP sockets. The connectionIDs are positive. The data is
     * HTTPSocketCtl.
     */
    private final Vector myOutTCP = new Vector(1);

    private final byte[] mySessionID;

    private final ByteArrayOutputStream myResponses =
      new ByteArrayOutputStream();


    HTTPClient(byte[] sessionID) {
        mySessionID = sessionID;
    }

    /**
     * Close a TCP connection.
     *
     * @param connectionID is the connectionID to close. If it is positive,
     *                     then the connection was built as an outgoing
     *                     connection. If it is negative, then it was built as
     *                     an incoming one. The value zero is invalid.
     */

    void close(byte connectionID) {
        HTTPSocketCtl ctl = getSocketCtl(connectionID);
        if (null == ctl) {
            byte[] msg = new byte[2];
            msg[0] = HTTPMsgID.HTTP_InvalidID;
            msg[1] = connectionID;
            queueMsg(msg);
        }

        ctl.close();

        if (connectionID > 0) {
            myOutTCP.setElementAt(null, connectionID);
        } else {
            myInTCP.setElementAt(null, -connectionID);
        }
    }

    /**
     * Handle an error that occured while handling an error for this client.
     */

    void disaster(Throwable t) {
        //xxx
    }

    /**
     * Returns the messages to be returned to the client as part of an
     * HTTP_Session message. These messages will include any incoming data on
     * the established TCP connections.
     *
     * @return is the messages to return to the client.
     */

    byte[] flushQueuedMsgs() throws IOException {
        for (int i = 1; i < myInTCP.size(); i++) {
            HTTPSocketCtl ctl = (HTTPSocketCtl)myInTCP.elementAt(i);
            queueDataMsg(-i, ctl);
        }
        for (int i = 1; i < myOutTCP.size(); i++) {
            HTTPSocketCtl ctl = (HTTPSocketCtl)myOutTCP.elementAt(i);
            queueDataMsg(i, ctl);
        }
        byte[] ret;
        synchronized (myResponses) {
            ret = myResponses.toByteArray();
            myResponses.reset();
            myResponses.write(HTTPMsgID.HTTP_Session);
            int len = mySessionID.length;
            myResponses.write(len >> 8);
            myResponses.write(len);
            myResponses.write(mySessionID, 0, mySessionID.length);
        }
        return ret;
    }

    private /*nullOK*/ HTTPSocketCtl getSocketCtl(byte connectionID) {
        if (connectionID > 0) {
            return (HTTPSocketCtl)myOutTCP.elementAt(connectionID);
        } else {
            return (HTTPSocketCtl)myInTCP.elementAt(-connectionID);
        }
    }

    /**
     * Make a new outgoing TCP connection for the client.
     *
     * @param connectionID is the one byte connection ID, a positive number.
     * @param hostPort     is the host IP/DNS name : port number.
     */

    void makeConnection(byte connectionID, String hostPort) {
        HTTPSocketCtl ctl = getSocketCtl(connectionID);
        if (null != ctl) {
            byte[] msg = new byte[2];
            msg[0] = HTTPMsgID.HTTP_InvalidID;
            msg[1] = connectionID;
            queueMsg(msg);
        }
        ctl = new HTTPSocketCtl(hostPort, this, connectionID);
        if (myOutTCP.size() <= connectionID) {
            myOutTCP.setSize(connectionID + 1);
        }
        myOutTCP.setElementAt(ctl, connectionID);
    }

    /**
     * Queue a data message to be returned to the client as part of an
     * HTTP_Session message.
     *
     * @param message is the message to queue.
     */
    private void queueDataMsg(int connectionID, HTTPSocketCtl ctl)
      throws IOException {
        if (null != ctl) {
            byte[] msg = ctl.getIncoming();
            if (null != msg) {
                synchronized (myResponses) {
                    myResponses.write(HTTPMsgID.HTTP_Data);
                    myResponses.write(connectionID);
                    int l = msg.length;
                    myResponses.write(l >> 8);
                    myResponses.write(l);
                    myResponses.write(msg);
                }
            }
            byte[] lim = ctl.getHisSendLimit();
            if (null != msg) {
                synchronized (myResponses) {
                    myResponses.write(HTTPMsgID.HTTP_OKToSend);
                    myResponses.write(connectionID);
                    myResponses.write(lim); // It's really a short
                }
            }
        }
    }

    /**
     * Queue a message to be returned to the client as part of an HTTP_Session
     * message.
     *
     * @param message is the message to queue.
     */

    void queueMsg(byte[] message) {
        // Add the message to the responses.
        myResponses.write(message, 0, message.length);
    }

    /**
     * Send data to a TCP connection.
     *
     * @param connection ID is the ID of the connection. If it is positive,
     *                   then the connection was built as an outgoing
     *                   connection. If it is negative, then it was built as an
     *                   incoming one. The value zero is invalid.
     * @param data       is the byte array to send.
     */

    void sendData(byte connectionID, byte[] data) {
        HTTPSocketCtl ctl = getSocketCtl(connectionID);
        if (null == ctl) {
            byte[] msg = new byte[2];
            msg[0] = HTTPMsgID.HTTP_InvalidID;
            msg[1] = connectionID;
            queueMsg(msg);
        }
        ctl.sendData(data);
    }

    /**
     * Set send limit for a particular connection.
     *
     * @param connection ID is the ID of the connection. If it is positive,
     *                   then the connection was built as an outgoing
     *                   connection. If it is negative, then it was built as an
     *                   incoming one. The value zero is invalid.
     * @param limit      is the new send limit.
     */

    void setSendLimit(byte connectionID, int limit) {
        HTTPSocketCtl ctl = getSocketCtl(connectionID);
        if (null == ctl) {
            byte[] msg = new byte[2];
            msg[0] = HTTPMsgID.HTTP_InvalidID;
            msg[1] = connectionID;
            queueMsg(msg);
        }
        ctl.setSendLimit(limit);
    }
}
