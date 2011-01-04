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

import net.vattp.security.ESecureRandom;
import org.erights.e.develop.assertion.T;
import org.erights.e.develop.trace.Trace;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.util.ConditionLock;
import org.erights.e.elib.util.DynamicMap;
import org.erights.e.elib.util.DynamicMapEnumeration;
import org.erights.e.elib.util.HexStringUtils;
import org.erights.e.elib.vat.Vat;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.KeyPair;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Allow sending and receiving data to a single remote vat.
 * <p/>
 * <p>The important method(s) are shutDownConnection(), sendMsg, and
 * registerMsgHandler.
 * <p/>
 * <p>The valid message types are defined in class Msg.
 *
 * @author Bill Frantz
 */
public final class VatTPConnection {

    /**
     * The VatTPMgr we report status changes to.
     */
    private final VatTPMgr myConnMgr;

    /**
     * The DataPath object we are using. During crossed connection resolution,
     * the outbound path.
     */
    private /*nullOK*/ DataPath myDataPath;

    /**
     * During crossed connection resolution, the incoming DataPath. Otherwise
     * null.
     */
    private /*nullOK*/ DataPath myIncomingDataPath;

    /**
     * Array of MsgHandlers indexed by message type
     */
    private final MsgHandler[] myMsgHandlers =
      new MsgHandler[Msg.HIGH_MSG_TYPE + 1];

    /**
     * An enumeration of the places to search for the remote vat
     */
    private DynamicMapEnumeration mySiteSearch;

    /**
     * A Hashtable of the InetAddresses tried which failed due to host related
     * problems. They are not to be tried again in this attempt to connect to
     * the vat. The key is InetAddress, data is InetAddress.
     */
    private Hashtable myAddressesTried;


    /**
     * The first place to look for the remote vat when resuming the connection.
     * This value is "" when there is no better guesses than come from a VLS
     * lookup.
     */
    private String myFirstAddressToTry = "";

    /**
     * The places to search for the remote vat as a semicolon separated list.
     */
    private String myFlattenedRemoteSearchPath;

    /**
     * The location the current DataPath are (to) (have been) speaking to.
     */
    private String myRemoteAddr;

    /**
     * The InetAddress of the remote end, or null if no connection has been
     * made
     */
    private NetAddr myRemoteNetAddr = null;

    /**
     * The InetAddress of the local end, or null if no connection has been
     * made
     */
    private NetAddr myLocalNetAddr = null;

    /**
     * The VatID of the other end of the connectiion. May be null.
     */
    private final String myRemoteVatID;

    /**
     * The Vat to have the SendThread and RecvThread synchronize on when
     * calling methods in this object.
     */
    private final Vat myVat;

    /**
     * The key pair which defines the identity of this vat
     */
    private final KeyPair myIdentityKeys;

    private final String myLocalVatID;

    /**
     * The semicolon separated search path for this vat
     */
    private final String myLocalFlattenedSearchPath;

    /**
     * The reason shutDownConnection has been called
     */
    private Throwable myShutdownReason = null;

    static private final byte STARTING = 1;

    static private final byte RUNNING = 2;

    static private final byte SUSPENDING = 3;

    static private final byte SUSPENDED = 4;

    static private final byte RESUMING = 5;

    static private final byte DIEING = 6;

    static private final byte DEAD = 7;

    /**
     * The current state of this VatTPConnection
     */
    private byte myState;

    /**
     * Place to queue outgoing messages while myState != RUNNING
     */
    private Vector myPendingOutput = null;

    /**
     * Place to accumulate the results of our search attempts
     */
    private Vector myProblemAccumulator;


    /**
     * Whether we are initiating the connection or responding to a remotely
     * initiated connection.
     */
    private boolean myIsIncoming;


    /**
     * The suspend ID to present to the other end for a resume, or null for a
     * new connection
     */
    private byte[] /*nullOK*/ myOutgoingSuspendID;

    /**
     * The suspend ID we asked the other end to present to resume the
     * connection, or null if the connection is not suspended.
     */
    private byte[] /*nullOK*/ myLocalSuspendID;

    // The following field is used by RecvThread and SendThread.
    // When the connection shuts down, the IVs are updated
    // so the connection can be resumed from suspension. The authentication
    // keys, and protocol versions are set when the connection
    // is first established.

    /**
     * The protocol versions and authorization parameters
     */
    private AuthSecrets myProtocolParms;

    // The following fields contain performance counters

    /**
     * The total bytes of messages received over this VatTPConnection
     */
    private long bytesReceived = 0;

    /**
     * The total number of messages received over this VatTPConnection
     */
    private long messagesReceived = 0;

    /**
     * The size of the largest message received over this VatTPConnection
     */
    private int maxReceivedMessageSize = 0;

    /**
     * The total bytes of messages sent over this VatTPConnection
     */
    private long bytesSent = 0;

    /**
     * The total number of messages sent over this VatTPConnection
     */
    private long messagesSent = 0;

    /**
     * The size of the largest message sent over this VatTPConnection
     */
    private int maxSentMessageSize = 0;


    /**
     * Make a new VatTPConnection
     *
     * @param connMgr                   The VatTPMgr to notify of status
     *                                  changes.
     * @param remoteVatID               the vatID of the remote vat
     * @param flattenedRemoteSearchPath is a pathlist of Strings each of which
     *                                  is the IP address and port number of a
     *                                  place to look for the remote vat. The
     *                                  IP address can be a DNS name or a dot
     *                                  repsentation of the 32 bit IP number.
     *                                  Most commonly, the Strings will be the
     *                                  location of the Vat Location Servers
     *                                  (VLSs) with which the remote vat is
     *                                  believed to register.
     * @param identityKeys              is the KeyPair which defines the
     *                                  identity of this vat.
     * @param vat                       is the Vat whose thread we use to
     *                                  synchronize calls into the VatTPConnection
     *                                  object from the SendThread and
     *                                  ReceiveThread
     * @param localFlattenedSearchPath  Is the search path we publish for
     *                                  references to us, flattened into a
     *                                  string.
     * @param localFlattenedSearchPath  Is the search pathlist we publish for
     *                                  references to us.
     */
    VatTPConnection(VatTPMgr connMgr,
                    String remoteVatID,
                    String flattenedRemoteSearchPath,
                    KeyPair identityKeys,
                    String localVatID,
                    Vat vat,
                    String localFlattenedSearchPath) {
        myConnMgr = connMgr;
        myRemoteVatID = remoteVatID;
        myIsIncoming = false;
        myIdentityKeys = identityKeys;
        myLocalVatID = localVatID;
        myVat = vat;
        myOutgoingSuspendID = null;
        myLocalFlattenedSearchPath = localFlattenedSearchPath;
        myState = STARTING;
        //Save to resume the connection
        myFlattenedRemoteSearchPath = flattenedRemoteSearchPath;
        //Make a list of locations to try for the new connection
        // myFirstAddressToTry will always be "" here.
        DynamicMap searchCollection =
          new DynamicMap(EARL.parseSearchPath(myFlattenedRemoteSearchPath));
        mySiteSearch = searchCollection.elems();
        myAddressesTried = new Hashtable(1);

        // Make a place to keep the search results
        myProblemAccumulator = new Vector(1, 1);
        // Start the search for the remote ID.
        tryNextAddress();
        if (Trace.comm.usage && Trace.ON) {
            Trace.comm.usagem("VatTPConnection constructor done " + this);
        }
    }

    /**
     * Make a new VatTPConnection for an incoming connection
     *
     * @param connMgr                  The VatTPMgr to notify of status
     *                                 changes.
     * @param identityKeys             is the KeyPair which defines the
     *                                 identity of this vat.
     * @param localVatID               is the vatID of the local vat.
     * @param vat                      is the Vat whose thread we use to
     *                                 synchronize calls into the VatTPConnection
     *                                 object from the SendThread and
     *                                 ReceiveThread
     * @param remoteVatID              is the vatID of the remote vat.
     * @param path                     The DataPath object controlling the TCP
     *                                 connection.
     * @param localFlattenedSearchPath Is the search path we publish for
     *                                 references to us, flattened into a
     *                                 string.
     * @param localFlattenedSearchPath Is the search pathlist we publish for
     *                                 references to us.
     * @param isIncoming               is true if this connection is an
     *                                 incoming connection, otherwise false.
     */
    VatTPConnection(VatTPMgr connMgr,
                    KeyPair identityKeys,
                    String localVatID,
                    Vat vat,
                    String remoteVatID,
                    DataPath path,
                    String localFlattenedSearchPath,
                    boolean isIncoming) throws IOException {
        myConnMgr = connMgr;
        myIsIncoming = isIncoming;
        myIdentityKeys = identityKeys;
        myLocalVatID = localVatID;
        myVat = vat;
        myRemoteVatID = remoteVatID;
        myLocalFlattenedSearchPath = localFlattenedSearchPath;
        myState = STARTING;
        // Make a place to keep the search results
        myProblemAccumulator = new Vector(1, 1);
        connectPath(path, remoteVatID, isIncoming);
        if (Trace.comm.event && Trace.ON) {
            Trace.comm.eventm("VatTPConnection constructor done " + this);
        }
    }

    /**
     *
     */
    public ConditionLock getEmbargoLock() {
        return myDataPath.getEmbargoLock();
    }

    /**
     * Close the TCP link on this connection.
     */
    void close() {
        myDataPath.shutDownPath();
    }

    /**
     * Connect a DataPath object for an incoming connection to an existing
     * VatTPConnection object. This call is where we first know that there is a
     * crossed connection problem.
     *
     * @param incomingPath is the DataPath object for the incoming connection.
     * @param remoteVatID  is the vatID of the remote vat.
     * @param isIncoming   is true if the DataPath represents an incoming TCP
     *                     connection, is false if it is an outgoing TCP
     *                     connection.
     * @return LIVES_CONTINUE - Continue setting up this connection <br>
     *         LIVES_DUP - This connection is a duplicate, discard it. <br>
     *         LIVES_NOTIFY - Notify the other end of a duplicate connection.
     *         The other end must decide which connection to keep.
     */
    int connectPath(DataPath incomingPath,
                    String remoteVatID,
                    boolean isIncoming) {
        int ret;    // Our return value

        T.require(remoteVatID.equals(myRemoteVatID),
                  "Wrong VatTPConnection\n  ",
                  this,
                  "\n  ",
                  incomingPath,
                  "\n  ",
                  remoteVatID);
        if (null == myDataPath) {
            myDataPath = incomingPath;
            myFlattenedRemoteSearchPath =
              myDataPath.connectConnection(this, myProtocolParms);
            if (SUSPENDED == myState) {
                myState = RESUMING;
            }
            return VatTPMgr.LIVES_CONTINUE;  //New path is only one
        }

        // We must decide which connection to keep
        int outState = myDataPath.getStartupState();
        if (!isIncoming) {
            ret = VatTPMgr.LIVES_DUP; //Zap anonymous outgoing
        } else if (StartUpProtocol.ST_OUTGOING_EXPECT_GOTOO == outState) {
            ret = VatTPMgr.LIVES_DUP;    //Outgoing is too far along.
        } else if (StartUpProtocol.ST_EXPECT_MESSAGE == outState) {
            if (Trace.comm.debug && Trace.ON) {
                Trace.comm
                  .debugm("Killing incoming\n  " + incomingPath +
                    "\n  in favor of\n  " + myDataPath);
            }
            ret = VatTPMgr.LIVES_DUP;    //Outgoing is too far along.
        } else if (0 < myRemoteVatID.compareTo(myLocalVatID)) {
            // The far end is in charge
            ret = VatTPMgr.LIVES_NOTIFY;
            myIncomingDataPath = incomingPath;
            myFlattenedRemoteSearchPath =
              myIncomingDataPath.connectConnection(this, myProtocolParms);
        } else {
            // We must decide. Outbound not EXPECT_GOTO or EXPECT_MESSAGE,
            // That means we keep the incoming and stop the outgoing.
            if (Trace.comm.debug && Trace.ON) {
                Trace.comm
                  .debugm("Killing outgoing\n  " + myDataPath +
                    "\n  in favor of\n  " + incomingPath);
            }
            myDataPath.stopStartUpProtocol();
            myDataPath = incomingPath;
            myFlattenedRemoteSearchPath =
              myDataPath.connectConnection(this, myProtocolParms);
            ret = VatTPMgr.LIVES_CONTINUE;
        }
        if (Trace.comm.debug && Trace.ON) {
            Trace.comm.debugm("Returning " + ret + " for " + incomingPath);
        }
        return ret;
    }

    /**
     * Duplicate crossed connection DataPath exiting.
     *
     * @param reason is the reason this is a duplicate.
     * @param path   is the path which has been declared duplicate.
     */
    void duplicatePath(String reason, DataPath path) {
        recordConnectionFailure(reason);
        if (myDataPath == path) {
            myDataPath = myIncomingDataPath;
        }
        myIncomingDataPath = null;
    }

    /**
     * Enqueue a message for output.
     *
     * @param message the message to queue
     * @throws IOException if state is DEAD or DIEING
     */
    private void enqueue(Object message) throws IOException {
        if (DEAD == myState || DIEING == myState) {
            throw new IOException(
              "Dead or dying VatTPConnection, state=" + myState);
        }
        if (RUNNING == myState) {
            int sus = myDataPath.getStartupState();
            T.require(StartUpProtocol.ST_EXPECT_MESSAGE == sus,
                      "state =" + sus);
            myDataPath.enqueue(message);
        } else {
            if (null == myPendingOutput) {
                myPendingOutput = new Vector(5);
            }
            myPendingOutput.addElement(message);
            if (SUSPENDED == myState) {
                startResume();
            }
        }
    }
//The following routines are used by StartUpProtocol to communicate is state
//success, failure etc., to make inquiries as to the state of other
//VatTPConnection objects, and to send messages.


    /**
     * Add an address to the list of addresses to try.
     *
     * @param addresses An array of Strings of the addresses to add.
     */
    void extendSearchPath(ConstList addresses) {
        mySiteSearch.addElems(addresses);
    }

    /**
     * Get the NetAddr of the local end, or null if no connection has been
     * made. If the connection is suspended or dead, this address will be the
     * last address used when there was a live connection.
     *
     * @return the NetAddr of the local end or null if no connection has been
     *         made.
     */
    public /*nullOK*/ NetAddr getLocalNetAddr() {
        return myLocalNetAddr;
    }

    /**
     * Get the vatID of the local end.
     *
     * @return the vatID of the local identity as a string.
     */
    public String getLocalVatID() {
        return myLocalVatID;
    }

    /**
     * Get the outgoing suspend ID for resuming a connection.
     *
     * @return is the suspend ID or null if the connection is not suspended.
     */
    byte[] /*nullOK*/ getOutgoingSuspendID() {
        return myOutgoingSuspendID;
    }

    /**
     * Get the NetAddr of the remote end, or null if no connection has been
     * made. If the connection is suspended or dead, this address will be the
     * last address used when there was a live connection.
     *
     * @return the NetAddr of the remote end or null if no connection has been
     *         made.
     */
    public /*nullOK*/ NetAddr getRemoteNetAddr() {
        return myRemoteNetAddr;
    }

    /**
     * Get the search path to the remote end
     */
    public ConstList getRemoteSearchPath() {
        return EARL.parseSearchPath(myFlattenedRemoteSearchPath);
    }

    /**
     * Get the vatID of the remote end.
     *
     * @return the vatID of the remote end as a string.
     */
    public String getRemoteVatID() {
        return myRemoteVatID;
    }

    /**
     * Get the state of this VatTPConnection.
     *
     * @return is the current state (STARTING, RUNNING, DIEING etc.) of this
     *         VatTPConnection object.
     */
    int getState() {
        return myState;
    }

    /**
     * Handle the death of the connection
     */
    private void handleConnectionDeath(Throwable reason) {
        myState = DEAD;         // Tell referencers to allow garbage collection
        myConnMgr.deathNotification(this);
        for (int i = 0; i < myMsgHandlers.length; i++) {
            MsgHandler h = myMsgHandlers[i];
            if (null != h) {
                if (Trace.comm.event && Trace.ON) {
                    Trace.comm.eventm(this + " calls connectionDead in " + h);
                }
                h.connectionDead(this, reason);
            }
        }
    }

    /**
     * Suspend this connection in response to a request from the other end.
     *
     * @param suspendID is the ID we will need to present to resume the
     *                  connection.
     */
    void handleSuspend(byte[] suspendID) throws IOException {
        myOutgoingSuspendID = suspendID;
        if (RUNNING == myState) {
            suspend();
        } else if (SUSPENDING != myState) {
            Trace.comm.errorm("Invalid state for handleSuspend=" + myState);
        }
        myDataPath.shutDownPath();
        myDataPath = null;
    }

    /**
     * Notice that an identified incoming DataPath has failed to complete the
     * startup protocol.
     *
     * @param path is the DataPath object which failed.
     */
    void identifiedPathDied(String reason, DataPath path) {
        if (path == myIncomingDataPath) {
            myIncomingDataPath = null;
        }
        if (path == myDataPath) {
        }
    }

    /**
     * Decide whether to keep incoming connection or outgoing when other end
     * asks us to chose on an outgoing connection.
     *
     * @return true - Keep the incoming connection of the pair. <br>     false
     *         - Keep the outgoing connection of the pair.
     */
    boolean isChoiceIncoming() {
        VatTPConnection incoming = null;

        if (null == myIncomingDataPath) {
            if (Trace.comm.debug && Trace.ON) {
                Trace.comm.debugm("Returning false, no incoming");
            }
            return false;
        } else {
            int inState = myIncomingDataPath.getStartupState();
            if (StartUpProtocol.ST_INCOMING_EXPECT_GO == inState) {
                if (Trace.comm.debug && Trace.ON) {
                    Trace.comm.debugm("Returning true, incoming expecting go");
                }
                myDataPath.stopStartUpProtocol();
                myDataPath = myIncomingDataPath;
                myIncomingDataPath = null;
                myDataPath.connectConnection(this, myProtocolParms);
                return true;        //Incoming is too far along.
            } else if (StartUpProtocol.ST_EXPECT_MESSAGE == inState) {
                Trace.comm
                  .errorm("Incoming running, why are we chosing?" +
                    " IncomingState=" + inState);
                myDataPath.stopStartUpProtocol();
                myDataPath = myIncomingDataPath;
                myIncomingDataPath = null;
                myDataPath.connectConnection(this, myProtocolParms);
                return true;
            } else {
                myIncomingDataPath.stopStartUpProtocol();
                if (Trace.comm.debug && Trace.ON) {
                    Trace.comm.debugm("Returning false, killing incoming");
                }
                myIncomingDataPath = null;
                return false;
            }
        }
    }

    /*
     * Make a report of the results of all the connection attempts
     *
     * @return a report of the connection results for each TCP connection,
     *         one connection attempt per line.
     */
    private String makeConnectionStatusReport() {
        if (null == myProblemAccumulator) {
            return "**Running Connection**";
        }
        if (0 == myProblemAccumulator.size()) {
            if (myIsIncoming) {
                return "**Incoming Connection**";
            } else {
                return "**Outbound - No Status Reported";
            }
        }
        StringBuffer connectStatusReport = new StringBuffer(300);
        for (int i = 0; i < myProblemAccumulator.size(); i++) {
            Object problem = myProblemAccumulator.elementAt(i);
            if (problem instanceof String) {
                connectStatusReport.append(problem).append('\n');
            } else {
                Throwable evt = (Throwable)problem;
                String subMsg = evt.getMessage();
                if (evt instanceof NoRouteToHostException) {
                    ;
                } else if (evt instanceof UnknownHostException) {
                    ;
                } else if (evt instanceof ConnectException &&
                  (subMsg.startsWith("Connection refused"))) {
                    ;
                } else if (evt instanceof SocketException &&
                  (subMsg.startsWith("Connection reset by peer"))) {
                    ;
                } else {
                    Trace.comm.errorm("Error during connection attempt", evt);
                }
                connectStatusReport.append(evt.toString()).append('\n');
            }
        }
        return connectStatusReport.toString();
    }

    /**
     * Handle a new incoming message from the remote end.
     *
     * @param message the incoming message as a byte array. The first character
     *                is the message type as defined in class Msg.
     */
    void newIncomingMsg(byte[] message) {
        if (Trace.comm.verbose && Trace.ON) {
            String msg = HexStringUtils.bytesToReadableHexStr(message);
            Trace.comm.verbosem(msg);
        }

        bytesReceived += message.length;
        messagesReceived++;
        if (maxReceivedMessageSize < message.length) {
            maxReceivedMessageSize = message.length;
        }

        int msgType = message[0] & 0xff;
        if (Msg.HIGH_MSG_TYPE >= msgType) {
            MsgHandler handler = myMsgHandlers[msgType];
            if (null != handler) {
                if (Trace.comm.event && Trace.ON) {
                    String msg = HexStringUtils.bytesToReadableHexStr(message);
                    Trace.comm
                      .eventm(this + " calls processMessage in " + handler +
                        "\n" + msg);
                }
                handler.processMessage(message, this);
                return;
            }
            //Ignore unhandled messages during shutdown
            if (DIEING == myState || DEAD == myState) {
                return;
            }
        }
        Trace.comm
          .errorm("No handler for incoming message type\n" +
            HexStringUtils.bytesToReadableHexStr(message));
    }

    /**
     * Notice problem on the connection. This method is called by the Send and
     * Recv threads when they encounter a fatal error and give up.
     *
     * @param problem The exception which describes the problem
     */
    void noticeProblem(Throwable problem) {
        switch (myState) {
        case DEAD:
        case DIEING:
        case RUNNING:
            myShutdownReason = problem;
            break;
        case RESUMING:  // Same as STARTING
        case STARTING:
            if (Trace.comm.debug && Trace.ON) {
                Trace.comm
                  .debugm("accumulating problem report: " + this + ": ",
                          problem);
            }
            recordConnectionFailure(problem);
            break;
        case SUSPENDING:
        case SUSPENDED:
            break;
        default:
            Trace.comm.errorm("Unhandled internal state=" + myState, problem);
            break;
        }
    }

    /**
     * Check if a connection should be resumed.
     *
     * @param suspendID The secret that the other side must know to resume a
     *                  connection or null if this is a new connection.
     * @return true if the connection should be completed. <p>false if the
     *         connection should be abandoned.
     */
    boolean noticeRemoteResume(byte[] suspendID) {
        if (null != suspendID) {
            if (null == myLocalSuspendID) {
                return false;
            }
            if (myLocalSuspendID.length != suspendID.length) {
                return false;
            }
            for (int i = 0; i < suspendID.length; i++) {
                if (myLocalSuspendID[i] != suspendID[i]) {
                    return false;
                }
            }
            return true;
        }
        if (null != myLocalSuspendID) {
            return false;
        }
        return true;
    }

    /**
     * Record why this try failed. When the path is shutdown, the next location
     * in the search order will be tried.
     *
     * @param msg A String or Throwable describing the reason for abandoning
     *            this try.
     */
    void recordConnectionFailure(Object msg) {
        if (null != myProblemAccumulator) {
            myProblemAccumulator.addElement(msg);
        }
    }

    /**
     * Register a MsgHandler for a particular message type. It will be called
     * whenever a new message of the specified type arrives. It will also be
     * called when the connection dies.
     *
     * @param msgType is the message type. It must be chosen from the types
     *                defined in the definition class Msg. An IOException is
     *                thrown if an attempt is made to register more than one
     *                handler for a message type.
     *                <p/>
     *                <p>An assertion is made that the msgType is not out of
     *                range. An assertion is made that no attempts are made to
     *                register a handler after the reactToNewConnection method
     *                of the object registered with the VatTPMgr has returned.
     * @param handler is an object which implements the MsgHandler interface.
     *                It will be called with processMessage when a message of
     *                msgType is received from the remote vat. It will be
     *                called with connectionDead when this connection dies.
     * @throws IOException is thrown if there is already a different handler
     *                     registered for this message type.
     * @see Msg
     * @see MsgHandler
     * @see VatTPMgr
     */
    public void registerMsgHandler(byte msgType, MsgHandler handler)
      throws IOException {
        myVat.requireCurrent();
        if (Trace.comm.event && Trace.ON) {
            Trace.comm
              .eventm("registerMsgHandler=" + msgType + "(" + handler +
                ") on " + this);
        }
        T.require(0 < msgType || Msg.HIGH_MSG_TYPE >= msgType,
                  "msgType=" + msgType,
                  " out of range (1 .. " + Msg.HIGH_MSG_TYPE,
                  ")");
        T.require(STARTING == myState || RESUMING == myState,
                  "Called after the NewConnectionReactor returns\n  handler=",
                  handler,
                  "\n  myState=" + myState);
        if (null != myMsgHandlers[msgType] &&
          myMsgHandlers[msgType] != handler) {
            throw new IOException(myMsgHandlers[msgType] +
              " already registered for msgType=" + msgType);
        }
        myMsgHandlers[msgType] = handler;
    }

    /**
     * sendFinished called when a message has been completely passed to TCP
     *
     * @param length       The size of the message passed to TCP.
     * @param continuation The notification passed to sendMessage.
     */
    void sendFinished(int count, int length, StreamMessage /*NullOK*/ msg) {
        bytesSent += length;
        messagesSent += count;
        if (maxSentMessageSize < length) {
            maxSentMessageSize = length;
        }
        if (null != msg) {
            msg.myPlaceToRun.enqueue(msg.myNotification);
        }
    }

    /**
     * Send a message to the remote vat.
     *
     * @param message is the message to be sent. It must not be altered after
     *                the call to sendMsg. The first byte of message is the
     *                message type and must be chosen from the types defined in
     *                the definition class Msg. (These two restrictions allow
     *                sendMsg to avoid copying the message into a private
     *                buffer.)
     * @throws IOException is thrown if the message can not be queued to be
     *                     sent because the connection has broken, if the
     *                     message is longer than, Msg.MAX_OUTBOUND_MSG_LENGTH,
     *                     or if there is no listener registered to process
     *                     messages of this message's message type.
     * @see Msg
     */
    public void sendMsg(byte[] message) throws IOException {
        myVat.requireCurrent();
        if (Trace.comm.event && Trace.ON) {
            Trace.comm
              .eventm("sendMsg on " + this + "\n" +
                HexStringUtils.bytesToReadableHexStr(message));
        }
        if (Msg.MAX_OUTBOUND_MSG_LENGTH < message.length) {
            throw new IOException("Outbound message length=" + message.length +
              " greater than limit=" + Msg.MAX_OUTBOUND_MSG_LENGTH);
        }
        if (Trace.comm.debug && Trace.ON) {
            Trace.comm
              .debugm("enqueueing to " + myRemoteAddr + "\n" +
                HexStringUtils.bytesToReadableHexStr(message));
        }
        int msgType = message[0] & 0xff;
        if (Msg.HIGH_MSG_TYPE < msgType || null == myMsgHandlers[msgType]) {
            throw new IOException(
              "No MsgHandler registered for message type=" + msgType);
        }
        enqueue(message);
    }

    /**
     * Send a message to the remote vat with notification that it has been
     * queued for processing by the TCP/IP stack on the local machine. This
     * call is designed to allow messaging with flow control. By only sending
     * short message segments, and waiting for notification before sending the
     * next segment, a caller can limit the buffering required by the
     * communication system.
     *
     * @param message      is the message to be sent. It must not be altered
     *                     after the call to sendMsg. The first byte of message
     *                     is the message type and must be chosen from the
     *                     types defined in the definition class Msg. (These
     *                     two restrictions allow sendMsg to avoid copying the
     *                     message into a private buffer.)
     * @param notification is the Runnable which will be enqueued when the
     *                     message has been passed to the platform's TCP
     *                     stack.
     * @param placeToRun   is the Vat where the notification will be enqueued.
     * @throws IOException is thrown if the message can not be queued to be
     *                     sent because the connection has broken, if the
     *                     message is longer than, Msg.MAX_OUTBOUND_MSG_LENGTH,
     *                     or if there is no listener registered to process
     *                     messages of this message's message type.
     * @see Msg
     */

    public void sendMsg(byte[] message, Runnable notification, Vat placeToRun)
      throws IOException {
        myVat.requireCurrent();
        if (Trace.comm.event && Trace.ON) {
            Trace.comm
              .eventm("sendMsg, Notify=" + notification + " Vat=" +
                placeToRun + " on " + this + "\n" +
                HexStringUtils.bytesToReadableHexStr(message));
        }
        if (Msg.MAX_OUTBOUND_MSG_LENGTH < message.length) {
            throw new IOException("Outbound message length=" + message.length +
              " greater than limit=" + Msg.MAX_OUTBOUND_MSG_LENGTH);
        }
        if (Trace.comm.debug && Trace.ON) {
            Trace.comm
              .debugm("enqueueing to " + myRemoteAddr + "\n" +
                HexStringUtils.bytesToReadableHexStr(message));
        }
        int msgType = message[0] & 0xff;
        if (Msg.HIGH_MSG_TYPE < msgType || null == myMsgHandlers[msgType]) {
            throw new IOException(
              "No MsgHandler registered for message type=" + msgType);
        }
        enqueue(new StreamMessage(message, notification, placeToRun));
    }

    /**
     * Causes a clean shutdown of the connection. All messages sent (XXX markm
     * asks: "All messages sent" or "All messages queued to be sent"?)
     * previously will be sent. When the shut down is complete, the
     * connectionDead method will be called on all registered MsgHandlers.
     */
    public void shutDownConnection() {
        shutDownConnection(new ConnectionShutDownException(
          "This end requested shutdown"));
    }

    /**
     * Causes a clean shutdown of the connection. All messages sent (XXX markm
     * asks: "All messages sent" or "All messages queued to be sent"?)
     * previously will be sent. When the shut down is complete, the
     * connectionDead method will be called on all registered MsgHandlers.
     * <p/>
     * XXX markm: I made this public, and changed ProxyConnection to call this
     * with a non-ConnectionShutDownException. As far as I can tell, this
     * shouldn't break anything. But Bill, it would be good for you to double
     * check this.
     *
     * @param reason indicates why the connection is shutting down
     */
    public void shutDownConnection(Throwable reason) {
        // myVat.requireCurrent();  -- fails for a DeadRunner
        if (Trace.comm.event && Trace.ON) {
            Trace.comm.eventm("shutDownConnection " + this);
        }
        T.notNull(reason, "Must provide a reason for shutdown");
        if (RUNNING == myState) {
            myDataPath.enqueue(new byte[]{Msg.SUSPEND});
            myState = DIEING;
            myConnMgr.enterHospice(this, myRemoteVatID);
            myShutdownReason = reason;
        } else if (SUSPENDED == myState) {
            startResume();  // Resume it to kill it
            myShutdownReason = reason;
        }
    }

    /**
     * shutDownFinished called when all queued messages have been sent and the
     * socket closed.
     */
    void shutDownFinished(Throwable reason) {
        if (STARTING == myState) {
            if (!myIsIncoming) {
                tryNextAddress();
            }
        } else if (SUSPENDING == myState) {
            myConnMgr.connectionSuspended(this, myRemoteVatID);
            myState = SUSPENDED;
            if (Trace.comm.usage && Trace.ON) {
                Trace.comm.usagem("connection suspended " + this);
            }
            if (null != myPendingOutput) {
                // If we have pending output
                startResume();      // try to resume
            }
        } else {
            Throwable rr = (reason instanceof ConnectionShutDownException &&
              null != myShutdownReason) ? myShutdownReason : reason;
            if (Trace.comm.usage && Trace.ON) {
                if (Trace.comm.event) {
                    Trace.comm
                      .eventm("Connection died " + this + "\n" +
                        myConnMgr.toString() + "\n" + rr);
                } else {
                    Trace.comm
                      .usagem("Connection died " + myRemoteAddr + "|" +
                        myRemoteVatID + "\n" + rr);
                }
            }
            handleConnectionDeath(rr);
        }
    }

    /**
     * Start trying to resume the connection.
     */
    private void startResume() {
        if (Trace.comm.usage && Trace.ON) {
            Trace.comm.usagem("Resuming connection " + this);
        }
        myIsIncoming = false;
        myState = RESUMING;
        //Make a list of locations to try for the new connection
        String path = ("".equals(myFirstAddressToTry) ?
          myFlattenedRemoteSearchPath :
          myFirstAddressToTry + ";" + myFlattenedRemoteSearchPath);
        DynamicMap searchCollection =
          new DynamicMap(EARL.parseSearchPath(path));
        mySiteSearch = searchCollection.elems();
        myAddressesTried = new Hashtable(1);

        // Make a place to keep the search results
        myProblemAccumulator = new Vector(1, 1);
        // Start the search for the remote ID.
        tryNextAddress();
    }

    /**
     * Called by StartUpProtocol when a connection to a remote vat has been
     * successfully made. The StartUpProtocol object has unregistered its
     * MsgHandlers and is ready to become garbage.
     *
     * @param path          is the DataPath object which made the connection.
     * @param remoteVatID   is the vatID of the remote vat.
     * @param remoteNetAddr is the remote end's network address.
     * @param localNetAddr  is the local end's network address.
     * @param remotePort    is the remote port number we tried to connect to,
     *                      or 0 if this is an inbound connection (or the port
     *                      number is unavailable for some other reason). This
     *                      port number will be used if we need to resume the
     *                      connection later.
     * @param authParms     is the AuthSecrets object which contains the
     *                      protocol versions and authorization parameters for
     *                      the connection.
     */
    void startupSuccessful(DataPath path,
                           String remoteVatID,
                           NetAddr remoteNetAddr,
                           NetAddr localNetAddr,
                           int remotePort,
                           AuthSecrets authParms) {
        T.require(myRemoteVatID.equals(remoteVatID),
                  "DataPath's remote vatID=",
                  remoteVatID,
                  " not equal my remote vatID=",
                  myRemoteVatID);
        myProtocolParms = authParms;
        //it's safe to not check optInetAddress() for null, since
        //remoteNetAddr must explicitly have one
        myRemoteAddr = remoteNetAddr.optInetAddress().getHostAddress() + ":" +
          remoteNetAddr.getPort();
        myRemoteNetAddr = remoteNetAddr;
        myLocalNetAddr = localNetAddr;
        if (null != myIncomingDataPath) {
            // We have two DataPaths
            if (path == myIncomingDataPath) {
                myDataPath.stopStartUpProtocol();
                myDataPath = myIncomingDataPath;
            } else {
                T.require(path == myDataPath, path, "\n", myDataPath);
                myIncomingDataPath.stopStartUpProtocol();
            }
            myIncomingDataPath = null;
        }
        if (0 < remotePort) {
            // We know where to retry to resume later
            //it's safe to not check optInetAddress() for null, since
            //remoteNetAddr must explicitly have one
            myFirstAddressToTry = remoteNetAddr.optInetAddress()
              .getHostAddress() + ":" + remotePort;
        }
        if (RESUMING == myState) {
            if (Trace.comm.debug && Trace.ON) {
                Trace.comm
                  .debugm("\nResume successfull after:\n" +
                    makeConnectionStatusReport());
            }
        } else {
            new Suspend(this);  // Object to handle SUSPEND messages
            if (Trace.comm.debug && Trace.ON) {
                Trace.comm
                  .debugm("\nStartup successfull after:\n" +
                    makeConnectionStatusReport());
            }
        }
        myConnMgr.startupSuccessful(this, myRemoteVatID, RESUMING == myState);
        myState = RUNNING;
        if (null != myShutdownReason) {
            // Someone has asked to shut down
            shutDownConnection(myShutdownReason);
            return;
        }
        myProblemAccumulator = null;
        if (null != myPendingOutput) {
            for (int i = 0; i < myPendingOutput.size(); i++) {
                myDataPath.enqueue(myPendingOutput.elementAt(i));
            }
            myPendingOutput = null;
        }
    }

    /**
     * Suspend the connection. Note that this method is NOT part of the
     * published API. It is public to enable testing of the suspend/resume
     * logic. (Popular demand could cause it to remain public.)
     * <p/>
     * <p>Suspended connections will be automatically resumed when data is sent
     * to them with sendMsg().
     *
     * @throws IOException if the connection isn't in the RUNNING state.
     */
    public void suspend() throws IOException {
        myVat.requireCurrent();
        if (RUNNING != myState) {
            throw new IOException(
              "Attempt to suspend a non-running connection, state=" + myState);
        }
        myLocalSuspendID = new byte[20];

        //MSM: should be passed in entropy as a capability
        ESecureRandom entropy = ESecureRandom.getESecureRandom();

        entropy.nextBytes(myLocalSuspendID);
        if (Trace.comm.debug && Trace.ON) {
            Trace.comm
              .debugm("Suspend Connection " + this +
                HexStringUtils.bytesToReadableHexStr(myLocalSuspendID));
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream os = new DataOutputStream(baos);
        os.writeByte(Msg.SUSPEND);
        os.writeShort(myLocalSuspendID.length);
        os.write(myLocalSuspendID);
        os.flush();
        enqueue(baos.toByteArray());
        myConnMgr.connectionSuspending(this, myRemoteVatID);
        myState = SUSPENDING;
    }

    /**
     * Describe this connection object.
     */
    public String toString() {
        return super.toString() + "\n  to " + myRemoteAddr + "|" +
          myRemoteVatID + "\n  searchpath=" + myFlattenedRemoteSearchPath +
          " state=" + myState;
    }

    /**
     * Start an attempt to connect to the next (first) address in the list of
     * addresses to try.
     */
    private void tryNextAddress() {
        if (null != mySiteSearch && mySiteSearch.hasMoreElements()) {
            myRemoteAddr = (String)mySiteSearch.nextElement();

            //Build a DataPath to try the connection.
            myDataPath = new DataPath(null,
                                      // No VatTPMgr.
                                      this,
                                      myRemoteVatID,
                                      myRemoteAddr,
                                      myAddressesTried,
                                      myIdentityKeys,
                                      myLocalVatID,
                                      myVat,
                                      myOutgoingSuspendID,
                                      myProtocolParms,
                                      myLocalFlattenedSearchPath);
        } else {
            String report = makeConnectionStatusReport();
            if (Trace.comm.usage && Trace.ON) {
                Trace.comm
                  .usagem(toString() + "\nConnection attempt failed, " +
                    "search path exhausted\n" + report);
            }
            handleConnectionDeath(new IOException(report));
        }
    }

    /**
     * Remove the registration for a handler for a particular message type.
     *
     * @param msgType is the message type. It must be chosen from the types
     *                defined in the definition class Msg. It is a fatal error
     *                if the msgType is out of range.
     * @param handler is the object currently registered as the handler.
     * @throws IOException is thrown if the handler passed is not the handler
     *                     registered for this message type.
     * @see Msg
     * @see MsgHandler
     */
    void unRegisterMsgHandler(byte msgType, MsgHandler handler)
      throws IOException {
        myVat.requireCurrent();
        if (Trace.comm.event && Trace.ON) {
            Trace.comm
              .eventm("unRegisterMsgHandler=" + msgType + "(" + handler +
                ") on " + this);
        }
        if (0 >= msgType || Msg.HIGH_MSG_TYPE < msgType) {
            Trace.comm
              .errorm("msgType=" + msgType + " out of range (1 .. " + Msg
                .HIGH_MSG_TYPE);
            Trace.comm.notifyFatal();
        }
        if (handler != myMsgHandlers[msgType]) {
            throw new IOException("Registered=" + myMsgHandlers[msgType] +
              " is not the same as " + handler + " for msgType=" + msgType);
        }
        myMsgHandlers[msgType] = null;
    }
}
