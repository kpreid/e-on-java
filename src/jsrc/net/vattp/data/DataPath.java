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
import org.erights.e.elib.util.HexStringUtils;
import org.erights.e.elib.vat.SynchQueue;
import org.erights.e.elib.vat.Vat;
import org.erights.e.extern.timer.Clock;
import org.erights.e.extern.timer.TickReactor;
import org.erights.e.extern.timer.Timer;

import java.io.IOException;
import java.math.BigInteger;
import java.net.Socket;
import java.security.KeyPair;
import java.util.Hashtable;

/**
 * Manage the physical connection to a single remote vat.
 * <p/>
 * <p>The important method(s) are acceptReceiver(), shartupSuccessful(),
 * shutDownPath(), shutDownFinished(), sendMsg, sendFinished()
 * registerMsgHandler(), and unRegisterMsgHandler().
 * <p/>
 * <p>The valid message types are defined in class Msg. We implement the
 * MsgHandler interface to handle the PING and PONG message types. We implement
 * the TickReactor interface know when to send PING messages, and when to panic
 * because no messages have been received.
 *
 * @author Bill Frantz
 */
class DataPath implements MsgHandler, TickReactor {

    /**
     * enqueued to effectively close the SynchQueue
     */
    static final Object theShutDownToken = BigInteger.valueOf(0);

    /**
     * The pong message to send when a ping message is received. It is
     * constant, so we only need one of them.
     */
    static private final byte[] thePongMsg = {Msg.PONG};

    /**
     * The ping message to send when a when no messages have been received
     * within .
     */
    static private final byte[] thePingMsg = {Msg.PING};

    /**
     * The VatTPConnection we report status changes to. It is connected late in
     * the startup protocol for an incoming connection
     */
    private VatTPConnection /*nullOK*/ myDataConnection;

    /**
     * The VatTPMgr we report the remote identity to for an incoming
     * connection. Null for outgoing connections unless they are connectToVatAt
     * connections.
     */
    private final VatTPMgr /*nullOK*/ myConnMgr;

    /**
     * Array of MsgHandlers indexed by message type
     */
    private MsgHandler[] myMsgHandlers = new MsgHandler[Msg.HIGH_MSG_TYPE + 1];

    /**
     * SynchQueue for queueing outbound messages. This is initialized during
     * construction, and set to null in shutDownPath.
     */
    private SynchQueue myWriter;

    /**
     * The RecvThread receiving messages for this VatTPConnection
     */
    private RecvThread myRecvThread;

    /**
     * The SendThread we use for output. We keep this reference so we can do a
     * mySendThread.stop() if the ping timeout pops.
     */
    private final SendThread mySendThread;

    /**
     * The time we last received a part of an inbound message or sent a part of
     * an outbound message. Used to ensure that the connection is still
     * connected, timeout the start up protocol etc. Initialized to object
     * creation time to avoid invalid timeouts.
     */
    private long lastNetActivity = System.currentTimeMillis();

    /**
     * The time we last sent a ping, or zero if we have received a message
     * which canceled the ping timeout.
     */
    private long timePingSent = 0;

    /**
     * The Clock object we are using to timeout messages
     */
    private Clock myClock;

    /**
     * The location the current Send/Recv Threads are (to) (have been) speaking
     * to.
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
     * The port number we are trying to connect to, or 0 for an inbound
     * DataPath. This port will be used to modify the search path after a
     * successful connection, so when the connection is resumed, the first
     * address tried will be the IP:port we successfully connected to.
     */
    private int myRemotePortNumber = 0;

    /**
     * The VatID of the other end of the connectiion. May be null.
     */
    private String myRemoteVatID;

    /**
     * The Vat to have the SendThread and RecvThread synchronize on when
     * calling methods in this object.
     */
    private Vat myVat;

    /**
     * The key pair which defines the identity of this vat
     */
    private KeyPair myIdentityKeys;

    /**
     * The local vatID. The hash of the local vat's public key
     */
    private String myLocalVatID;

    /**
     * The semicolon separated search path for this vat
     */
    private String myLocalFlattenedSearchPath;

    /**
     * The search path the other end provided on an incoming connection. We
     * save this and give it to the VatTPConnection at the end of the startup
     * protocol so we have a search path to use to resume the connection after
     * it has been suspended.
     */
    private String mySavedFlattenedSearchPath;

    /**
     * While connecting, the StartUpProtocol object. Otherwise null
     */
    private StartUpProtocol myStartUpProtocol;

//The following fields are used only to create the StartUpProtocol object.

    /**
     * Whether we are initiating the connection or responding to a remotely
     * initiated connection.
     */
    private final boolean myIsIncoming;


    /**
     * Values for autherization parameters and protocol versions
     */
    private AuthSecrets myProtocolParms;

    // The following fields contain performance counters

    /**
     * The total bytes of messages received over this DataPath
     */
    private long bytesReceived = 0;

    /**
     * The total number of messages received over this DataPath
     */
    private long messagesReceived = 0;

    /**
     * The size of the largest message received over this DataPath
     */
    private int maxReceivedMessageSize = 0;

    /**
     * The total bytes of messages sent over this DataPath
     */
    private long bytesSent = 0;

    /**
     * The total number of messages sent over this DataPath
     */
    private long messagesSent = 0;

    /**
     * The size of the largest message sent over this DataPath
     */
    private int maxSentMessageSize = 0;


    /**
     * for debugging purposes
     */
    private final ConditionLock myEmbargoLock = new ConditionLock();

    /**
     * Make a new DataPath for an incoming connection
     *
     * @param connMgr                  The VatTPMgr to notify of status
     *                                 changes.
     * @param tcpConnection            The Socket to the TCP connection.
     * @param identityKeys             is the KeyPair which defines the
     *                                 identity of this vat.
     * @param vat                      is the Vat whose thread we use to
     *                                 synchronize calls into the DataPath
     *                                 object from the SendThread and
     *                                 ReceiveThread
     * @param localFlattenedSearchPath Is the search path we publish for
     *                                 references to us, flattened into a
     *                                 string.
     * @param localFlattenedSearchPath Is the search path we publish for
     *                                 references to us, flattened into a
     *                                 string
     */
    DataPath(VatTPMgr connMgr,
             Socket tcpConnection,
             KeyPair identityKeys,
             String localVatID,
             Vat vat,
             String localFlattenedSearchPath,
             VatLocationLookup vls) {

        myConnMgr = connMgr;
        myIsIncoming = true;
        SynchQueue reader =
          commonConstructionSetup(identityKeys, localVatID, null,
                                  //no suspendID, incoming
                                  vat, localFlattenedSearchPath, vls);

        myRemoteAddr = tcpConnection.getInetAddress().getHostAddress();

        //Build a SendThread to handle the messages
        mySendThread = new SendThread(tcpConnection, this, reader, myVat);

        if (Trace.comm.debug && Trace.ON) {
            Trace.comm.debugm("DataPath constructor done " + this);
        }
    }

    /**
     * Make a new DataPath
     *
     * @param connMgr                  is the VatTPMgr if this DataPath is
     *                                 supporting a "connectToVatAt" operation.
     *                                 Otherwise it is null.
     * @param connection               The VatTPConnection to notify of status
     *                                 changes.
     * @param remoteVatID              the vatID of the remote vat
     * @param remoteAddr               is the dotted IP address or DNS name and
     *                                 port number of the place to try for the
     *                                 remote vat.
     * @param addressesTried           is a Hashtable of the InetAddresses
     *                                 already tried to locate the vat which
     *                                 failed due to host, rather than port
     *                                 related problems, and are not to be
     *                                 tried again.
     * @param identityKeys             is the KeyPair which defines the
     *                                 identity of this vat.
     * @param vat                      is the Vat whose thread we use to
     *                                 synchronize calls into the DataPath
     *                                 object from the SendThread and
     *                                 ReceiveThread
     * @param outgoingSuspendID        Is the Suspend ID we are to send to the
     *                                 remote vat for resuming a connection, or
     *                                 null for a new connection.
     * @param macKey                   is the key used for message
     *                                 authentication for resume, or null for a
     *                                 new connection.
     * @param protocolParms            is the AuthSecrets object which has the
     *                                 resume state for protocol versions an
     *                                 protocol parameters for resuming a
     *                                 connection, or null for a new
     *                                 connection.
     * @param localFlattenedSearchPath Is the search path we publish for
     *                                 references to us, flattened into a
     *                                 string.
     */
    DataPath(VatTPMgr /*nullOK*/ connMgr,
             VatTPConnection /*nullOK*/ connection,
             String remoteVatID,
             String remoteAddr,
             Hashtable addressesTried,
             KeyPair identityKeys,
             String localVatID,
             Vat vat,
             byte[] /*nullOK*/ outgoingSuspendID,
             AuthSecrets /*nullOK*/ protocolParms,
             String localFlattenedSearchPath) {
        myConnMgr = connMgr;
        myDataConnection = connection;
        myRemoteVatID = remoteVatID;
        myIsIncoming = false;
        if (null == protocolParms) {
            myProtocolParms = new AuthSecrets();
        } else {
            myProtocolParms = protocolParms;
        }
        SynchQueue reader = commonConstructionSetup(identityKeys,
                                                    localVatID,
                                                    outgoingSuspendID,
                                                    vat,
                                                    localFlattenedSearchPath,
                                                    //no VLS for outgoing connections
                                                    null);

        myRemoteAddr = remoteAddr;
        // Get the port number from the address
        int colon = remoteAddr.indexOf(':');
        if (colon >= 0) {
            myRemotePortNumber =
              Integer.parseInt(remoteAddr.substring(colon + 1));
        }

        //Build the SendThread and RecvThread for this address

        //Build a SendThread to handle the messages
        mySendThread =
          new SendThread(remoteAddr, this, reader, myVat, addressesTried);

        if (Trace.comm.debug && Trace.ON) {
            Trace.comm.debugm("DataPath constructor done " + this);
        }
    }

    /**
     *
     */
    ConditionLock getEmbargoLock() {
        return myEmbargoLock;
    }

    /**
     * Accept the RecvThread from the SendThread when it has been created.
     *
     * @param receiver      is the RecvThread object.
     * @param remoteNetAddr is the remote end's address.
     * @param localNetAddr  is the local end's address.
     */
    void acceptReceiver(RecvThread receiver,
                        NetAddr remoteNetAddr,
                        NetAddr localNetAddr) {
        myRecvThread = receiver;
        myRemoteNetAddr = remoteNetAddr;
        myLocalNetAddr = localNetAddr;
        //it's safe to not check optInetAddress() for null, since
        //remoteNetAddr must explicitly have one
        myRemoteAddr = remoteNetAddr.optInetAddress().getHostAddress() + ":" +
          remoteNetAddr.getPort();
        myRecvThread.start();
        if (null == myWriter) {
            // If we are dying, shutdown the recv thread
            myRecvThread.shutdown();
        }
    }

    /**
     * Handle the case where the resume ID's don't match.
     *
     * @param reason The reason for abandoning the attempt.
     */
    void cantResume(String reason) {
        shutDownPath();
        String msg = myRemoteAddr + ": " + reason;
        myDataConnection.identifiedPathDied(msg, this);
    }

    /**
     * Do the common setup operations for the two constructor methods.
     *
     * @param identityKeys             is the public/private key pair that
     *                                 define this vat.
     * @param localVatID               is the vatID of this vat.
     * @param suspendID                is the suspend ID to send during the
     *                                 startup protocol, or null if this is an
     *                                 incoming connection or a new
     *                                 connection.
     * @param vat                      is the Vat to synchronize on when other
     *                                 threads call methods which are "in
     *                                 vat".
     * @param localFlattenedSearchPath is the search path for this vat as a
     *                                 String, the elements separated by
     *                                 semicolons.
     * @param vls                      is the object which gives VLS look
     *                                 functionality, or null if this vat
     *                                 doesn't support that functionality or if
     *                                 this is an outgoing connection.
     * @return is the SynchQueue object for reading the output queue.
     */
    private SynchQueue commonConstructionSetup(KeyPair identityKeys,
                                               String localVatID,
                                               byte[] suspendID,
                                               Vat vat,
                                               String localFlattenedSearchPath,
                                               VatLocationLookup vls) {
        myIdentityKeys = identityKeys;
        myLocalVatID = localVatID;
        myVat = vat;
        T.notNull(myVat, "Bad Vat");
        myLocalFlattenedSearchPath = localFlattenedSearchPath;

        // Make a queue for outbound messages on the new connection.
        //XXX should provide a type parameter
        SynchQueue reader = myWriter = new SynchQueue();

        //Register to handle the PING and PONG messages.
        try {
            registerMsgHandler(Msg.PING, this);
            registerMsgHandler(Msg.PONG, this);
        } catch (IOException e) {
            Trace.comm.errorm("Fatal error registering pingpong", e);
            Trace.comm.notifyFatal();
        }
        //Start a keep-alive ticker running.
        myClock = Timer.theTimer().every(Msg.TICK_RATE, this);
        myClock.start();

        // Make a startup protocol
        myStartUpProtocol = new StartUpProtocol(this,
                                                myIsIncoming,
                                                myRemoteVatID,
                                                myIdentityKeys,
                                                myLocalVatID,
                                                suspendID,
                                                myLocalFlattenedSearchPath,
                                                vls);
        return reader;
    }

    /**
     * Connect this DataPath to a VatTPConnection. Used for incoming sockets
     * and anonymous outgoing sockets to connect the DataPath after the remote
     * end has been identified.
     *
     * @param connection    is the VatTPConnection object to connect to.
     * @param protocolParms is the protocol versions and parameters to use for
     *                      resuming a connection, or null for none.
     * @return is the flattened search path to use to find the remote end.
     */
    String connectConnection(VatTPConnection connection,
                             AuthSecrets protocolParms) {
        myDataConnection = connection;
        if (null == protocolParms) {
            myProtocolParms = new AuthSecrets();
        } else {
            myProtocolParms = protocolParms;
        }
        String ret = mySavedFlattenedSearchPath;
        mySavedFlattenedSearchPath = null;
        return ret;
    }

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
     * @param connection The VatTPConnection object which has just died. For
     *                   this use, it may be null.
     * @param reason     is a Throwable describing why the connection was shut
     *                   down.
     */
    public void connectionDead(VatTPConnection connection, Throwable reason) {
        //Do nothing - part of the MsgHandler interface.
    }

    /**
     * Notify the connectToVatAt caller that he has tried to connect to self
     */
    void connectToSelf() {
        T.require(null == myDataConnection,
                  "Remote end should not be identitifed");
        shutDownPath();
        myConnMgr.connectToSelf(this);
    }

    /**
     * Duplicate crossed connection DataPath exiting.
     *
     * @param reason The reason for abandoning the attempt.
     */
    void duplicatePath(String reason, boolean isIdentified) {
        shutDownPath();
        String msg = myRemoteAddr + ": " + reason;
        if (!isIdentified) {
            myConnMgr.unidentifiedConnectionDied(this, msg, null);
        } else {
            if (null != myDataConnection) {
                myDataConnection.duplicatePath(msg, this);
            }
        }
        myDataConnection = null;
    }

    /**
     * Enqueue a message for output.
     *
     * @param message the message to queue
     */
    void enqueue(Object message) {
        if (Trace.comm.verbose && Trace.ON) {
            Trace.comm.verbosem("enqueue " + message);
        }
        //If the path has been shutdown, myWriter will be null. If a client
        //is trying to write, that should be caught by the VatTPConnection.
        //This means that the writer is StartUpProtocol of DataPath. If the
        //path is going away, their messages can be trashed.
        if (null == myWriter) {
            return;
        }
        myWriter.enqueue(message);
        if (message instanceof byte[]) {
            byte[] d = (byte[])message;
            ESecureRandom.provideEntropy(d, 1);
        }
    }
//The following routines are used by StartUpProtocol to communicate is state
//success, failure etc., to make inquiries as to the state of other
//DataPath objects, and to send messages.

    /**
     * Add an address to the list of addresses to try.
     *
     * @param addresses An array of Strings of the addresses to add.
     */
    void extendSearchPath(ConstList addresses) {
        myDataConnection.extendSearchPath(addresses);
    }

    /**
     * Return the remote address we are connected to.
     */
    String getRemoteAddress() {
        return myRemoteAddr;
    }

    /**
     * Get the startup state of this DataPath.
     *
     * @return the current state of this DataPath
     */
    int getStartupState() {
        if (null == myStartUpProtocol) {
            return StartUpProtocol.ST_EXPECT_MESSAGE;
        }
        return myStartUpProtocol.getState();
    }

    /**
     * Identify the remote vat's vatID for an incoming connection.
     *
     * @param remoteVatID      the VatID of the remote vat.
     * @param remoteSearchPath the search path to use to find the remote vat in
     *                         array form.
     * @return the code received from VatTPMgr.newIncomingConnectionIdentified
     */
    int identifyIncoming(String localVatID,
                         String remoteVatID,
                         String remoteSearchPath) throws IOException {
        myRemoteVatID = remoteVatID;

        //Save the search path to give to the VatTPConnection when startup
        //is finished. It will need the search path to resume a suspended
        //connection.
        mySavedFlattenedSearchPath = remoteSearchPath;
        return myConnMgr.newConnectionIdentified(this,
                                                 remoteVatID,
                                                 localVatID,
                                                 true);
    }

    /**
     * Identify the remote vat's vatID for an outgoing "to WHOEVER" connection.
     * Called by the StartUpProtocol.
     *
     * @param remoteVatID               is the VatID of the remote vat.
     * @param remoteFlattenedSearchPath is the search path to use to find the
     *                                  remote vat.
     * @return an object the type of which determines what to do next. If the
     *         object is a String, then the startup protocol should terminate
     *         the connection attempt. If the object is a byte array, then
     *         there is a suspended connection to the remote vat and the array
     *         is the suspendID. If the object is null, then the connection is
     *         a new connection and the StartUpProtocol should proceed.
     */
    Object identifyOutgoing(String localVatID,
                            String remoteVatID,
                            String remoteFlattenedSearchPath)
      throws IOException {
        myRemoteVatID = remoteVatID;

        //Save the search path to give to the VatTPConnection when startup
        //is finished. It will need the search path to resume a suspended
        //connection.
        mySavedFlattenedSearchPath = remoteFlattenedSearchPath;
        int s = myConnMgr.newConnectionIdentified(this,
                                                  remoteVatID,
                                                  localVatID,
                                                  false);
        if (VatTPMgr.LIVES_DUP == s) {
            return "Connection alreay exists or in progress";
        }
        return myDataConnection.getOutgoingSuspendID();
    }

    /**
     * Decide whether to keep incoming connection or outgoing when other end
     * asks us to chose on an outgoing connection.
     *
     * @return true - Keep the incoming connection of the pair. <br>     false
     *         - Keep the outgoing connection of the pair.
     */
    boolean isChoiceIncoming() {
        return myDataConnection.isChoiceIncoming();
    }

    /**
     * Handle a new incoming message from the remote end.
     *
     * @param message the incoming message as a byte array. The first character
     *                is the message type as defined in class Msg.
     */
    void newIncomingMsg(byte[] message) {
        String msg = HexStringUtils.bytesToReadableHexStr(message);
        if (Trace.comm.verbose && Trace.ON) {
            Trace.comm.verbosem(msg);
        }

        //Help the random number generator
        ESecureRandom.provideEntropy(message, 1);

        lastNetActivity = System.currentTimeMillis();
        bytesReceived += message.length;
        messagesReceived++;
        if (maxReceivedMessageSize < message.length) {
            maxReceivedMessageSize = message.length;
        }

        if (null == myMsgHandlers) {
            myDataConnection.newIncomingMsg(message);
            return;
        }
        int msgType = message[0] & 0xff;
        if (msgType <= Msg.HIGH_MSG_TYPE) {
            MsgHandler handler = myMsgHandlers[msgType];
            if (null != handler) {
                if (Trace.comm.debug && Trace.ON) {
                    Trace.comm
                      .debugm(this + " calls processMessage in " + handler +
                        "\n" + msg);
                }
                handler.processMessage(message, myDataConnection);
                return;
            }
        }
        Trace.comm
          .errorm("No handler for incoming message type\n  " + this + msg);
    }

    /**
     * Notice problem on the connection. This method is called by the Send and
     * Recv threads when they encounter a fatal error and give up.
     *
     * @param problem The exception which describes the problem
     */
    void noticeProblem(Throwable problem) {
        shutDownPath();
        if (null != myDataConnection) {
            myDataConnection.noticeProblem(problem);
        } else if (null != myConnMgr) {
            myConnMgr.unidentifiedConnectionDied(this, null, problem);
        }
    }
//Methods from the TickReactor interface.

    /**
     * Called by Clocks on their targets after each tick.
     *
     * @param tick is the current tick count for the Clock - ignored
     */
    public void run(long tick) {
        long now = System.currentTimeMillis();
        if ((now - lastNetActivity) > Msg.PING_SENDTIME) {
            if (0 == timePingSent && null != myWriter) {
                enqueue(thePingMsg);    //Send a ping
                if (Trace.comm.event && Trace.ON) {
                    Trace.comm.eventm("Ping " + this);
                }
                timePingSent = System.currentTimeMillis();
            } else {
                if ((now - timePingSent) > Msg.PING_TIMEOUT) {
                    if (null != myWriter) {
                        shutDownPath();
                        if (Trace.comm.usage && Trace.ON) {
                            Trace.comm.usagem("Connection timeout " + this);
                        }
                    } else if ((now - timePingSent) > (2 * Msg.PING_TIMEOUT)) {
                        if (null != myClock) {
                            Trace.comm.errorm("Shutdown timeout " + this);
                            Exception reason =
                              new IOException("Shutdown Timeout");
                            //XXX Thread.stop() has been deprecated
                            mySendThread.stop(reason);
                            myProtocolParms = null;
                            shutDownFinished(null, reason);
                        }
                        timePingSent = Long.MAX_VALUE; // and shut up
                    }
                }
            }
        } else {
            timePingSent = 0;
        }
    }

    //Methods from the MsgHandler interface.
    /**
     * Process an incoming message from the VatTPConnection.
     *
     * @param message    is the incoming message. The first byte (message[0])
     *                   is the message type (see class Msg). A handler which
     *                   is registered for more than one message can use the
     *                   initial byte to determine what is the message type of
     *                   the current message.
     * @param connection is the VatTPConnection object on which the the message
     *                   arrived. For this use, it may be null.
     * @see Msg
     * @see VatTPConnection
     */
    public void processMessage(byte[] message, VatTPConnection connection) {
        if (Msg.PING == message[0]) {
            // got a ping, send pong
            enqueue(thePongMsg);
        }
        //Do nothing with a PONG message. The time received will have already
        //been updated, and that is all we need for keep alive.
    }

    /**
     * Record that sending data has made progress for ping/pong logic.
     */
    void recordSendProgress() {
        lastNetActivity = System.currentTimeMillis();
    }

    /**
     * Register a MsgHandler for a particular message type. It will be called
     * whenever a new message of the specified type arrives. It will also be
     * called when the connection dies.
     *
     * @param msgType is the message type. It must be chosen from the types
     *                defined in the definition class Msg. It is an error to
     *                register more than one handler for a message type. It is
     *                a fatal error if the msgType is out of range. It is a
     *                fatal error to register a handler after the
     *                reactToNewConnection method of the object registered with
     *                the VatTPMgr has returned.
     * @param handler is an object which implements the MsgHandler interface.
     *                It will be called with processMessage when a message of
     *                msgType is received from the remote vat. It will be
     *                called with connectionDead when this connection dies.
     * @throws IOException is thrown if there is already a handler registered
     *                     for this message type.
     * @see Msg
     * @see MsgHandler
     * @see VatTPMgr
     */
    void registerMsgHandler(int msgType, MsgHandler handler)
      throws IOException {
        if (Trace.comm.debug && Trace.ON) {
            Trace.comm
              .debugm("registerMsgHandler=" + msgType + "(" + handler +
                ") on " + this);
        }
        T.require(msgType > 0 || msgType <= Msg.HIGH_MSG_TYPE,
                  "msgType=" + msgType,
                  " out of range (1 .. " + Msg.HIGH_MSG_TYPE,
                  ")");
        if (null != myMsgHandlers[msgType]) {
            throw new IOException(myMsgHandlers[msgType] +
              " already registered for msgType=" + msgType);
        }
        myMsgHandlers[msgType] = handler;
    }

    /**
     * Check if a connection should be resumed.
     *
     * @param suspendID The secret that the other side must know to resume a
     *                  connection or null if this is a new connection.
     * @return true if the connection should be completed. <p>false if the
     *         connection should be abandoned.
     */
    boolean resumeConnection(byte[] suspendID) {
        return myDataConnection.noticeRemoteResume(suspendID);
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
     * Set the initial sequences to be used with a connection. Called by
     * StartUpProtocol when they are calculated.
     *
     * @param dhSecret     is the Diffie Hellman shared secret.
     * @param sendSequence is the first sequence number to be used to send data
     *                     to the remote vat.
     * @param recvSequence is the first sequence number to be used to receive
     *                     data from the remote vat.
     */
    void setAuthorizationSecrets(byte[] dhSecret,
                                 byte[] sendSequence,
                                 byte[] recvSequence) {
        myProtocolParms.myDHSecret = dhSecret;
        myProtocolParms.myOutgoingSequence = sendSequence;
        myProtocolParms.myIncomingSequence = recvSequence;
    }

    /**
     * shutDownFinished called when all queued messages have been sent and the
     * socket closed.
     *
     * @param sendSequence is the next sequence number to use on the send path
     *                     after resume.
     * @param reason       is a Throwable which describes why the shutdown.
     */
    void shutDownFinished(byte[] sendSequence, Throwable reason) {
        myWriter = null;
        myClock.stop();
        myClock = null;
        if (null == myMsgHandlers) {
            if (null != myProtocolParms) {
                myProtocolParms.myOutgoingSequence = sendSequence;
            }
            try {
                myDataConnection.unRegisterMsgHandler(Msg.PING, this);
                myDataConnection.unRegisterMsgHandler(Msg.PONG, this);
            } catch (IOException e) {
                Trace.comm.errorm("Exception unregistering ping/pong", e);
            }
        } else {
            for (int i = 0; i < myMsgHandlers.length; i++) {
                MsgHandler h = myMsgHandlers[i];
                if (null != h) {
                    if (Trace.comm.debug && Trace.ON) {
                        Trace.comm
                          .debugm(this + " calls connectionDead in " + h);
                    }
                    h.connectionDead(myDataConnection, reason);
                }
            }
        }
        if (null != myDataConnection) {
            myDataConnection.shutDownFinished(reason);
        }
        if (Trace.comm.debug && Trace.ON) {
            Trace.comm.debugm("connection dead " + this);
        }
    }

    /**
     * Causes a shutdown of the path. Any queued messages will be sent.
     */
    void shutDownPath() {
        if (Trace.comm.debug && Trace.ON) {
            Trace.comm
              .debugm("shutDownPath " + this + "\nfor " + myDataConnection);
        }
        // Enqueue a shutdown to have the SendThread send the queued messages
        // and then shutdown. SendThread will call shutdownFinished when the
        // shutdown process is finished.

        if (null != myWriter) {
            enqueue(theShutDownToken); //Shutdown if not already shutting down
        }
        myWriter = null;
        lastNetActivity = System.currentTimeMillis(); //Shutdown timeout
        if (null != myRecvThread) {
            // We have protocol parameters, and have completed startup
            if (null != myProtocolParms && null == myMsgHandlers) {
                myProtocolParms.myIncomingSequence =
                  myRecvThread.getSequence();
            }
            myRecvThread.shutdown();
            myRecvThread = null;    //Receive shutdown
        }
    }

    /**
     * Called by StartUpProtocol when a connection to a remote vat has been
     * successfully made. The StartUpProtocol object has unregistered its
     * MsgHandlers and is ready to become garbage.
     *
     * @param eMsgProtocolVersion the version of the E message protocl
     *                            selected.
     * @param protocolSuite       the type of authentication selected.
     */
    void startupSuccessful(String eMsgProtocolVersion, String protocolSuite)
      throws IOException {

        if (null != protocolSuite) {
            myProtocolParms.myEProtocolVersion = eMsgProtocolVersion;
            myProtocolParms.myProtocolSuite = protocolSuite;
        }

        //We must synchronously inform ReceiveThread of the change in protocol
        myRecvThread.changeProtocol(myProtocolParms);
        //The RecvThread has calculated the necessary authentication parameters
        //and placed them in myProtocolParms. The send thread can use them.
        enqueue(myProtocolParms);
        myStartUpProtocol = null;   // Done with startup

        //Reregister as the handler for the ping and pong messages.
        myDataConnection.registerMsgHandler(Msg.PING, this);
        myDataConnection.registerMsgHandler(Msg.PONG, this);

        if (Trace.comm.usage && Trace.ON) {
            Trace.comm
              .usagem("Link to " + myRemoteVatID + " uses CommProtocol " +
                myProtocolParms.myProtocolSuite + ", Emsg protocol=" +
                myProtocolParms.myEProtocolVersion);
        }

        myDataConnection.startupSuccessful(this,
                                           myRemoteVatID,
                                           myRemoteNetAddr,
                                           myLocalNetAddr,
                                           myRemotePortNumber,
                                           myProtocolParms);
        myMsgHandlers = null;   // Done handling messages
    }

    /**
     * Stop the start up protocol on this DataPath. This method is called by
     * the VatTPMgr when it determines that there are two connections being
     * built between this vat and another vat, and that this connection is the
     * one that should be abandonded.
     */
    void stopStartUpProtocol() {
        myStartUpProtocol.stopStartUpProtocol();
        myDataConnection = null;    // We've been disowned
    }

    /**
     * Describe this path object.
     */
    public String toString() {
        return super.toString() + "|" + myRemoteAddr + "|" + myRemoteVatID +
          ", startupState=" + getStartupState();
    }

    /**
     * Stop TCP to this address and try the next on on the list.
     *
     * @param reason is a message describing the reason for abandoning this
     *               try.
     */
    void tryNext(String reason) {
        shutDownPath();
        String msg = myRemoteAddr + ": " + reason;
        if (null == myDataConnection) {
            myConnMgr.unidentifiedConnectionDied(this, msg, null);
        } else {
            if (!myIsIncoming) {
                myDataConnection.recordConnectionFailure(msg);
                return;
            }
            myDataConnection.identifiedPathDied(msg, this);
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
    void unRegisterMsgHandler(int msgType, MsgHandler handler)
      throws IOException {
        if (Trace.comm.debug && Trace.ON) {
            Trace.comm
              .debugm("unRegisterMsgHandler=" + msgType + "(" + handler +
                ") on " + this);
        }
        if (msgType <= 0 || msgType > Msg.HIGH_MSG_TYPE) {
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
