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

import org.erights.e.develop.trace.Trace;

import java.net.Socket;

/**
 * This class is used by SendThread, RecvThread and ListenThread to schedule
 * calls to classes inside the vat (in DataPath or VatTPMgr). The call is
 * scheduled by instantitating an instance of this object through the
 * constructor appropriate for the method to be called. Then that instance is
 * passed to Vat's now(...) method.
 *
 * @author Bill Frantz
 */
class DataCommThunk implements Runnable {

    static private final int NOTICE_PROBLEM = 1;

    static private final int SHUT_DOWN_FINISHED = 2;

    static private final int SEND_FINISHED = 3;

    static private final int NEW_INCOMING_MSG = 4;

    static private final int ACCEPT_RECEIVER = 5;

    static private final int NOTICE_PROBLEM_CM = 6;

    static private final int INCOMING_SOCKET = 7;

    static private final int SEND_PROGRESS = 8;

    private int type = 0;

    private DataPath myDataPath;    // For Send/RecvThread calls

    private VatTPMgr myConnMgr; // For ListenThread calls

    private Throwable myException;

    private int myWrittenLength;

    private int myWrittenCount;

    private StreamMessage myContinuation;

    private byte[] myMessage;

    private RecvThread myRecvThread;

    private Socket mySocket;

    private NetAddr myRemoteNetAddr;

    private NetAddr myLocalNetAddr;

    private String myAddress;

    /**
     * Construct a DataCommThunk to call noticeProblem() in a VatTPMgr object.
     *
     * @param connMgr the VatTPMgr object to invoke.
     * @param e       the Exception object describing the problem.
     */
    DataCommThunk(VatTPMgr connMgr, Throwable e) {
        myConnMgr = connMgr;
        myException = e;
        type = NOTICE_PROBLEM_CM;
    }

    /**
     * Construct a DataCommThunk to call newInboundSocket in a VatTPMgr
     * object.
     *
     * @param connMgr the DataPath object to invoke.
     * @param socket  The byte array which is the message.
     */
    DataCommThunk(VatTPMgr connMgr, Socket socket) {
        myConnMgr = connMgr;
        mySocket = socket;
        type = INCOMING_SOCKET;
    }

    /**
     * Construct a DataCommThunk to call recordSendProgress() in a DataPath
     * object.
     *
     * @param conn the DataPath object to invoke.
     */
    DataCommThunk(DataPath conn) {
        myDataPath = conn;
        type = SEND_PROGRESS;
    }

    /**
     * Construct a DataCommThunk to call newIncomingMsg() in a DataPath
     * object.
     *
     * @param conn    the DataPath object to invoke.
     * @param message The byte array which is the message.
     */
    DataCommThunk(DataPath conn, byte[] message) {
        myDataPath = conn;
        myMessage = message;
        type = NEW_INCOMING_MSG;
    }

    /**
     * Construct a DataCommThunk to call shutDownFinished() in a DataPath
     * object.
     *
     * @param conn   the DataPath object to invoke shutdownFinished().
     * @param sendIV is the next IV to use after the connection is resumed.
     * @param reason is a dummy to make this constructor different from the one
     *               for new incoming data.
     */
    DataCommThunk(DataPath conn, byte[] sendIV, Exception reason) {
        myDataPath = conn;
        myMessage = sendIV;
        myException = reason;
        type = SHUT_DOWN_FINISHED;
    }

    /**
     * Construct a DataCommThunk to call sendFinished() in a DataPath object.
     *
     * @param conn         the DataPath object to invoke.
     * @param written      the number of bytes actually sent to TCP.
     * @param continuation is the streaming continuation or null.
     */
    DataCommThunk(DataPath conn, int count, int length,
                  /*NullOK*/StreamMessage continuation) {
        myDataPath = conn;
        myWrittenCount = count;
        myWrittenLength = length;
        myContinuation = continuation;
        type = SEND_FINISHED;
    }

    /**
     * Construct a DataCommThunk to call noticeProblem() in a DataPath object.
     *
     * @param conn the DataPath object to invoke.
     * @param e    the Exception object describing the problem.
     */
    DataCommThunk(DataPath conn, Throwable e) {
        myDataPath = conn;
        myException = e;
        type = NOTICE_PROBLEM;
    }

    /**
     * Construct a DataCommThunk to call newInboundSocket in a VatTPMgr
     * object.
     *
     * @param conn the DataPath object to invoke.
     * @param The  byte array which is the message.
     */
    DataCommThunk(DataPath conn,
                  RecvThread recv,
                  NetAddr remoteNetAddr,
                  NetAddr localNetAddr) {
        myDataPath = conn;
        myRecvThread = recv;
        myRemoteNetAddr = remoteNetAddr;
        myLocalNetAddr = localNetAddr;
        type = ACCEPT_RECEIVER;
    }

    /**
     * Actually do the invocation this Thunk was created to perform. Normally
     * called by Vat's now method after it has acquired the Vat lock.
     */
    public void run() {
        switch (type) {
        case NOTICE_PROBLEM:
            myDataPath.noticeProblem(myException);
            return;
        case SHUT_DOWN_FINISHED:
            myDataPath.shutDownFinished(myMessage, myException);
            return;
        case SEND_FINISHED:
            myDataPath.sendFinished(myWrittenCount,
                                    myWrittenLength,
                                    myContinuation);
            return;
        case NEW_INCOMING_MSG:
            myDataPath.newIncomingMsg(myMessage);
            return;
        case ACCEPT_RECEIVER:
            myDataPath.acceptReceiver(myRecvThread,
                                      myRemoteNetAddr,
                                      myLocalNetAddr);
            return;
        case NOTICE_PROBLEM_CM:
            myConnMgr.noticeProblem(myException);
            return;
        case INCOMING_SOCKET:
            myConnMgr.newInboundSocket(mySocket);
            return;
        case SEND_PROGRESS:
            myDataPath.recordSendProgress();
            return;
        default:
            Trace.comm.errorm("Invalid type value=" + type);
            throw new Error("Invalid type value=" + type);
        }
    }

    public String toString() {
        return super.toString() + " Type=" + type;
    }
}
