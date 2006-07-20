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

import net.vattp.data.NetAddr;
import org.erights.e.elib.vat.SynchQueue;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * The class which controls an individual TCP socket.
 *
 * @author Bill Frantz
 */
public class HTTPSocketCtl extends Thread {

    static private final int SENDLIMIT = 512;

    /**
     *
     */
    private final Object myLock = new Object();

    private final HTTPClient myClient;

    private final byte myConnectionID;

    private final String myHostPort;

    private Socket mySocket;

    private InputStream myInputStream;

    private OutputStream myOutputStream;

    private int myMySendLimit;

    private boolean isNeedingSendLimitUpdate = false;

    private int myHisSendLimit = SENDLIMIT;

    private final SynchQueue myOutputQueue = new SynchQueue();


    HTTPSocketCtl(String hostPort, HTTPClient client, byte connectionID) {
        myClient = client;
        myHostPort = hostPort;
        myMySendLimit = 0;
        myConnectionID = connectionID;
        start();
    }


    void close() {
        myOutputQueue.enqueue(this);    // Signal thread to close
    }

    /**
     * Get the value for the send limit to be sent to the client or null
     *
     * @return is a two byte array with the value of the send limit if it
     *         should be updated, or null if there is no change.
     */
    byte[] getHisSendLimit() {
        synchronized (myLock) {
            if (isNeedingSendLimitUpdate) {
                byte[] ret = new byte[2];
                ret[0] = (byte)((myHisSendLimit >> 8) & 0xff);
                ret[1] = (byte)(myHisSendLimit & 0xff);
                isNeedingSendLimitUpdate = false;
                return ret;
            }
            return null;
        }
    }


    byte[] getIncoming() {
        synchronized (myLock) {
            if (0 == myMySendLimit || null == myInputStream) {
                return null;
            }
            try {
                int lim = myInputStream.available();
                if (lim == 0) {
                    return null;
                }
                if (lim > myMySendLimit) {
                    lim = myMySendLimit;
                }
                byte[] data = new byte[lim];
                int read = 0;
                while (lim - read > 0) {
                    read = myInputStream.read(data, read, lim - read);
                    if (-1 == read) {
                        throw new IOException("Unexpected EOF");
                    }
                }
                myMySendLimit -= lim;
                return data;
            } catch (IOException e) {
                try {
                    byte[] msg = makeSessionMsg(
                      HTTPMsgID.HTTP_ConnectionFailed,
                      "Error reading socket " + e);
                    myClient.queueMsg(msg);
                    myClient.close(myConnectionID);
                } catch (IOException e2) {
                    myClient.disaster(e2);
                }
            }
            return null;
        }
    }

    private byte[] makeSessionMsg(byte msgID, String reason)
      throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream ds = new DataOutputStream(baos);

        ds.writeByte(msgID);
        ds.writeByte(myConnectionID);
        ds.writeUTF(reason);
        ds.flush();
        return baos.toByteArray();
    }

    public void run() {
        if (null == mySocket) {
            // Build the outgoing connection
            try {
                NetAddr address = new NetAddr(myHostPort);
                //it's safe to not check optInetAddress() for null, since
                //address must explicitly have one
                //XXX is this true?
                mySocket = new Socket(address.optInetAddress(),
                                      address.getPort());
            } catch (IOException e2) {
                try {
                    byte[] msg = makeSessionMsg(
                      HTTPMsgID.HTTP_ConnectionFailed,
                      "Connection Failed " + e2);
                    myClient.queueMsg(msg);
                } catch (IOException e) {
                    myClient.disaster(e);
                }
                return;
            }
            byte[] msg = new byte[2];
            msg[0] = HTTPMsgID.HTTP_ConnectionComplete;
            msg[1] = myConnectionID;
            myClient.queueMsg(msg);
        }

        isNeedingSendLimitUpdate = true;

        //Wait for data and send it
        while (true) {
            Object qe = myOutputQueue.dequeue();
            if (qe instanceof byte[]) {
                byte[] data = (byte[])qe;
                try {
                    myOutputStream.write(data);
                } catch (IOException e) {
                    try {
                        byte[] msg = makeSessionMsg(HTTPMsgID.HTTP_Close,
                                                    "Exception on send " + e);
                        myClient.queueMsg(msg);
                    } catch (IOException e2) {
                        myClient.disaster(e2);
                    }
                }
                synchronized (myLock) {
                    myHisSendLimit += data.length;
                    isNeedingSendLimitUpdate = true;
                }
            } else if (qe instanceof HTTPSocketCtl) {
                //We got a close request
                try {
                    myOutputStream.close();
                    myInputStream.close();
                    mySocket.close();
                } catch (IOException e) {
                    try {
                        byte[] msg = makeSessionMsg(HTTPMsgID.HTTP_Close,
                                                    "Exception on close " + e);
                        myClient.queueMsg(msg);
                    } catch (IOException e2) {
                        myClient.disaster(e2);
                    }
                }
            }
        }
    }


    void sendData(byte[] data) {
        if (data.length > myHisSendLimit) {
            try {
                byte[] msg = makeSessionMsg(HTTPMsgID.HTTP_ConnectionFailed, "Sent more than limit, sent=" +
                                                                             data.length + " limit=" + myHisSendLimit);
                myClient.queueMsg(msg);
                myClient.close(myConnectionID);
            } catch (IOException e) {
                myClient.disaster(e);
            }
        }
        synchronized (myLock) {
            myHisSendLimit -= data.length;
        }
        myOutputQueue.enqueue(data);
    }


    void setSendLimit(int limit) {
        synchronized (myLock) {
            myMySendLimit = limit;
        }
    }
}
