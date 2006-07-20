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

import net.vattp.security.ESecureRandom;
import org.erights.e.develop.trace.Trace;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.util.Hashtable;

/**
 * Class to handle the suspension and shutdown messages.
 *
 * @author Bill Frantz
 */

public class HTTPConnectionHandler extends Thread {

    static private final char[] CRLF = {0x0d, 0x0a};

    static private final String lenHead = "content-length:"; //lower case

    /**
     * map of logged on clients. Key is vatID, data is HTTPClient. Note that an
     * entry in this map implies an entry in the sessions map and vicea versa.
     */
    private final Hashtable myVats;

    /**
     * map of sessions to logged on clients. Key is sessionID, data is
     * HTTPClient. Note that an entry in this map implies an entry in the vats
     * map and vicea versa.
     */
    private final Hashtable mySessions;

    private final Socket mySocket;

    private HTTPInputStream myIn;

    private final ByteArrayOutputStream myOut = new ByteArrayOutputStream();

    private final String myTcpListenAddress;


    HTTPConnectionHandler(Socket socket,
                          Hashtable vats,
                          Hashtable sessions,
                          String listenAddress) {
        mySocket = socket;
        myVats = vats;
        mySessions = sessions;
        myTcpListenAddress = listenAddress;
        setName(socket.toString() + " Thread");
        start();
    }

    private void clientError(String msg) {
        flushInput();
        PrintWriter writer = new PrintWriter(myOut);

        writer.write("HTTP/1.0 400 Bad Request - " + msg);
        writer.write(CRLF);
        writer.close();
        try {
            mySocket.close();
        } catch (IOException e) {
            Trace.tunnel.eventm("IOException", e);
        }
    }

    private void doLogon(DataInputStream in) throws IOException {
        String vatID = in.readUTF();

        int serverLen = in.readUnsignedShort();
        byte[] serverNonce = new byte[serverLen];
        in.readFully(serverNonce);

        int clientLen = in.readUnsignedShort();
        byte[] clientNonce = new byte[clientLen];
        in.readFully(clientNonce);

        int clientKeyLen = in.readUnsignedShort();
        byte[] clientKey = new byte[clientKeyLen];
        in.readFully(clientKey);

        int clientSigLen = in.readUnsignedShort();
        byte[] clientSignature = new byte[clientSigLen];
        in.readFully(clientSignature);

        Object ob = myVats.get(vatID);
        if (null != ob) {
            //xxx
        }

        //MSM: should be passed in entropy as a capability
        ESecureRandom entropy = ESecureRandom.getESecureRandom();

        BigInteger sessionID = entropy.nextSwiss();
        byte[] sid = sessionID.toByteArray();

        HTTPClient client = new HTTPClient(sid);
        myVats.put(vatID, client);
        mySessions.put(sessionID, client);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream os = new DataOutputStream(baos);
        os.write(HTTPMsgID.HTTP_LoggedOn);
        os.writeShort(sid.length);
        os.write(sid);
        os.writeUTF(myTcpListenAddress);

        returnResponse(baos.toByteArray());
    }

    /**
     *
     */
    private void doSession(DataInputStream in) throws IOException {
        // Get the session ID
        int len = in.readUnsignedShort();
        byte[] sID = new byte[len];
        in.readFully(sID);
        BigInteger sessionID = new BigInteger(1, sID);

        // Look up the session ID in the active sessions map
        HTTPClient client = (HTTPClient)mySessions.get(sessionID);
        if (null == client) {
            sendHTTPError("Invalid session ID", sID);
            return; // We're out of here
        }

        // Read the session message data segments and process them
        while (true) {
            int subType = in.read();
            if (-1 == subType) {
                break;
            }
            switch (subType) {
            case HTTPMsgID.HTTP_NewConnection:
                {
                    byte connectionID = in.readByte();
                    if (connectionID <= 0) {
                        byte[] msg = {HTTPMsgID.HTTP_InvalidID, connectionID};
                        client.close(connectionID);
                        client.queueMsg(msg);
                        break;
                    }
                    String hostPort = in.readUTF();
                    client.makeConnection(connectionID, hostPort);
                    break;
                }
            case HTTPMsgID.HTTP_Data:
                {
                    byte connectionID = in.readByte();
                    int l = in.readUnsignedShort();
                    byte[] data = new byte[l];
                    in.readFully(data);
                    client.sendData(connectionID, data);
                    break;
                }
            case HTTPMsgID.HTTP_OKToSend:
                {
                    byte connectionID = in.readByte();
                    int limit = in.readUnsignedShort();
                    client.setSendLimit(connectionID, limit);
                    break;
                }
            case HTTPMsgID.HTTP_Close:
                {
                    byte connectionID = in.readByte();
                    String reason = in.readUTF();
                    client.close(connectionID);
                    break;
                }
            case HTTPMsgID.HTTP_InvalidID:
            case HTTPMsgID.HTTP_ConnectionFailed:
            case HTTPMsgID.HTTP_ConnectionComplete:
            default:
                //xxx
                break;
            }
        }

        returnResponse(client.flushQueuedMsgs());
    }

    private void flushInput() {
        try {
            while (-1 != myIn.read()) {
                myIn.skip(Long.MAX_VALUE);
            }
        } catch (IOException e) {
            Trace.tunnel.eventm("IOException", e);
        }
    }

    private void returnResponse(byte[] response) {
        int size = response.length;
        PrintWriter writer = new PrintWriter(myOut);
        writer.write("HTTP/1.0 200 OK");
        writer.write(CRLF);
        writer.write("Content-type: application/octet-stream");
        writer.write(CRLF);
        writer.write("Content-length: " + size);
        writer.write(CRLF);
        writer.write(CRLF);
        writer.flush();
        try {
            myOut.write(response);
            myOut.close();
            mySocket.close();
        } catch (IOException e) {
            Trace.tunnel.eventm("IOException", e);
        }
    }

    public void run() {
        try {
            myIn = new HTTPInputStream(mySocket.getInputStream());

            //Ensure that this is a POST message
            if ('P' != myIn.read() || 'O' != myIn.read() ||
              'S' != myIn.read() || 'T' != myIn.read()) {
                clientError("Not a POST request");
                return;
            }

            //Chomp thru the headers and get the content length
            DataInputStream in = new DataInputStream(myIn);

            int contentLength = -1;

            for (String header = in.readLine();
                 !header.equals("");
                 header = in.readLine()) {
                if (header.toLowerCase().startsWith(lenHead)) {
                    try {
                        contentLength = Integer.parseInt(
                          header.substring(lenHead.length()));
                    } catch (NumberFormatException e) {
                        clientError("Bad Content-length: " + e);
                        return;
                    }
                    if (contentLength < 0) {
                        clientError("Bad Content-length: < 0");
                        return;
                    }
                }
            }
            if (contentLength < 0) {
                clientError("No Content-length: header");
                return;
            }

            //Now that we have the content length, and are positioned
            //at the end of the headers, we can read the content.
            //We read each submessage of the HTTP_Session message
            //into byte arrays so we can ensure we get all of them
            //before committing to processing any of them.

            myIn.setSize(contentLength);

            while (true) {
                int msgType = in.read();
                if (-1 == msgType) {
                    break;   // End of the stream
                }
                switch (msgType) {
                case HTTPMsgID.HTTP_Logon:
                    doLogon(in);
                    break;
                case HTTPMsgID.HTTP_Session:
                    doSession(in);
                    break;
                case HTTPMsgID.HTTP_Shutdown:
                    //xxx
                    break;
                case HTTPMsgID.HTTP_Error:
                    //xxx
                    break;
                    // The following two msgs are invalid from the client
                case HTTPMsgID.HTTP_LoggedOn:
                case HTTPMsgID.HTTP_SetServerNonce:
                default:
                    //xxx
                    break;
                }
            }

            //} catch(EOFException e) {
            //    Trace.tunnel.errorm("EOFException", e);
            //    clientError(e.toString());
        } catch (IOException e) {
            Trace.tunnel.errorm("IOException", e);
            clientError(e.toString());
        } catch (RuntimeException t) {
            Trace.tunnel.errorm("Exception!", t);
        }
    }

    private void sendHTTPError(String msg, byte[] sessionID) {
        flushInput();
        //xxx close everything for this user
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream os = new DataOutputStream(baos);
        try {
            os.write(HTTPMsgID.HTTP_Error);
            os.writeShort(sessionID.length);
            os.write(sessionID);
            os.writeUTF(msg);
            os.flush();
        } catch (IOException e) {
            //xxx OK, now what?
        }
        returnResponse(baos.toByteArray());
    }
}
