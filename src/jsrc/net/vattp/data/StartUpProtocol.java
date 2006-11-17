package net.vattp.data;

/*
This file now also contains code added by Tyler Close, not a citizen or
resident of the US. Modified code has been commented with:
     // daffE -> E

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
import net.vattp.security.MicroTime;
import org.erights.e.develop.exception.ThrowableSugar;
import org.erights.e.develop.trace.Trace;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.util.HexStringUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.DSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Vector;


/**
 * This class is used by VatTPConnection to perform the startup protocol.
 *
 * @author Bill Frantz
 */
public class StartUpProtocol implements MsgHandler {

    /*
     * Constants for the connection startup protocol.
     */

    /*
    * States for the connection startup state machine. Current state
    * is in myState below.
    */
    static private final int ST_UNSTARTED = 0;

    static private final int ST_INCOMING_EXPECT_IWANT = 1;

    static private final int ST_OUTGOING_EXPECT_IAM = 2;

    static private final int ST_INCOMING_EXPECT_GIVEINFO = 3;

    static private final int ST_OUTGOING_EXPECT_REPLYINFO = 4;

    static final int ST_INCOMING_EXPECT_GO = 5;

    static final int ST_OUTGOING_EXPECT_GOTOO = 6;

    static final int ST_EXPECT_MESSAGE = 7;

    static private final int ST_DEAD = 8;

    static private final int ST_TRY_NEXT = 9;


    /*
     * Tokens used for submessage types of the Msg.STARTUP message
     */
    static private final byte TOK_BYE = 1;

    static private final byte TOK_DUP = 2;

    static private final byte TOK_GIVEINFO = 3;

    static private final byte TOK_GO = 4;

    static private final byte TOK_GOTOO = 5;

    static private final byte TOK_IAM = 6;

    static private final byte TOK_IWANT = 7;

    static private final byte TOK_NOT_ME = 8;

    static private final byte TOK_REPLYINFO = 9;

    static private final byte TOK_TRY = 10;

    static private final byte TOK_RESUME = 11;

    static private final byte TOK_YOUCHOSE = 12;

    /* Tokens used to report errors to the remote end.
     * All are negative numbers.
     */
    static private final byte TOK_ERR_PROTOCOL = -2;

    static private final byte TOK_ERR_WRONG_ID = -3;

    static private final byte TOK_ERR_INTERNAL = -4;

    /*
    * Values for supported protocol suites:
    */
    static final String PROTO_NONE = "None";

    static final String PROTO_3DES_SDH_M = "3DES_SDH_M";

    static final String PROTO_3DES_SDH_M2 = "3DES_SDH_M2";

    /**
     * The protocol negotiation string we send, least desired last
     */
    static private String TheAuthProtocols;

    /**
     * The authentication protocols we support. Excludes none which is a
     * special case
     */
    static private String[] TheAuthProtocolTable;

    static {
        TheAuthProtocols = "";
        TheAuthProtocols += PROTO_3DES_SDH_M2 + ",";  // Improved protocol
        TheAuthProtocols += PROTO_3DES_SDH_M;         // daffE -> E
        //'Nothin ain't worth 'nothing, but it's exportable :-)
// daffE -> E       TheAuthProtocols += PROTO_AUTH_SDH_M + ",";
        //Compatibility with non-sequence checking versions.
// daffE -> E       TheAuthProtocols += PROTO_NONE_SDH_M;

        //Now convert the string to an array of protocols
        int count;
        int start;
        int end;
        String result[];

        count = 1;
        start = -1;
        while ((start = TheAuthProtocols.indexOf(',', start + 1)) >= 0) {
            count++;
        }
        result = new String[count];
        start = 0;
        for (int i = 0; i < count - 1; i++) {
            end = TheAuthProtocols.indexOf(',', start);
            result[i] = TheAuthProtocols.substring(start, end);
            start = end + 1;
        }
        result[count - 1] = TheAuthProtocols.substring(start);

        TheAuthProtocolTable = result;
    }

    static public String[] authProtocolTable() {
        return TheAuthProtocolTable;
    }

    /* String names for the Tokens for use in trace messages */
    static private final String[] tokNames = {"TOK_BYE",
      "TOK_DUP",
      "TOK_GIVEINFO",
      "TOK_GO",
      "TOK_GOTOO",
      "TOK_IAM",
      "TOK_IWANT",
      "TOK_NOT_ME",
      "TOK_REPLYINFO",
      "TOK_TRY",
      "TOK_RESUME",
      "TOK_YOUCHOSE"};

    static private final String[] errTokNames = {"***Unassigned***",
      "TOK_ERR_PROTOCOL",
      "TOK_ERR_WRONG_ID",
      "TOK_ERR_INTERNAL"};


    /**
     * The DataPath object we are working with.
     */
    private final DataPath myDataPath;

    /**
     * Whether we are build for initiating an outbound connection or responding
     * to a remote connection
     */
    private final boolean myIsIncoming;

    /**
     * The remote vatID we are communicating with
     */
    private String myRemoteVatID;

    /**
     * The suspend ID to present to the other end for a reconnect. If it is
     * null, reconnection will not be attempted. (Outbound connections only)
     */
    private /*nullOK*/ byte[] myOutgoingSuspendID;

    /**
     * The public key of the remote end
     */
    private PublicKey myHisPublicKey;

    private final KeyPair myIdentityKeys;

    /**
     * Entity from which to lookup foreign vatIDs
     */
    private final VatLocationLookup myVLS;

    /**
     * The vatID of this vat
     */
    private final String myLocalVatID;

    /**
     * The semicolon separated search path for this vat
     */
    private final String myLocalFlattenedSearchPath;

    /**
     * Current state of the startup protocol state machine
     */
    private int myState = ST_UNSTARTED;

    /**
     * Set true if we should stop attempting to make a connection before
     * outgoingSetup has been called.
     */
    private boolean myStop = false;

    /**
     * The agreed E message protocol version
     */
    private String myEMsgProtocolVersion;

    /**
     * The agreed upon protocol suite
     */
    private String myProtocolSuite;


    /**
     * The Signature object used for signing and checking signatures
     */
    //WARNING - WARNING - WARNING
    // In order to use ESecureRandom to calculate each signature in mySignature
    // we directly set the random signature seed. If we ever calculate two
    // signatures with the same seed, we have blown the security of the secret
    // key.
    // I (WSF) think it will be easier to avoid this problem if mySignature
    // remains private, and signatures are only calculated one place in the
    // code.
    private Signature mySignature;


    /**
     * Messages I've sent which are to be signed for as part of end point
     * authentication.
     */
    private final Vector myMessagesToSign = new Vector(5);

    /**
     * Messages I've received whose signature is to be checked as part of end
     * point identification.
     */
    private final Vector hisMessagesToSign = new Vector(5);


    // Data for the Diffie Hellman protocol
    private BigInteger x = null;

    static private final BigInteger g = new BigInteger("2");

    static private final BigInteger modulus = new BigInteger(
      "11973791477546250983817043765044391637751157152328012" +
        "72278994477192940843207042535379780702841268263028" +
        "59486033998465467188646855777933154987304015680716" +
        "74391647223805124273032053960564348124852668624831" +
        "01273341734490560148744399254916528366159159380290" +
        "29782321539388697349613396698017627677439533107752" + "978203");

// Constructors

    /**
     * Make a StartUpProtocol object.
     *
     * @param path                     the DataPath this StartUpProtocol is to
     *                                 serve.
     * @param isIncoming               true if this StartUpProtocol is to work
     *                                 with an incoming connection. False if
     *                                 this end is the initator.
     * @param remoteVatID              if isIncoming, then null, othersize the
     *                                 VatID for the remote vat.
     * @param identityKeys             the KeyPair which defines the identity
     *                                 of this vat.
     * @param localVatID               The vatID of the local vat.
     * @param outgoingSuspendID        The ID to resume a suspended connection
     *                                 if this DataPath is outgoing and is to
     *                                 resume a suspended connection. Otherwise
     *                                 null
     * @param localFlattenedSearchPath the search path we publish for others
     *                                 looking for the local vat in semicolon
     *                                 separated form.
     */
    StartUpProtocol(DataPath path,
                    boolean isIncoming,
                    String /*nullOK*/ remoteVatID,
                    KeyPair identityKeys,
                    String localVatID,
                    byte[] /*nullOK*/ outgoingSuspendID,
                    String localFlattenedSearchPath,
                    VatLocationLookup vls) {
        myDataPath = path;
        myIsIncoming = isIncoming;
        myIdentityKeys = identityKeys;
        myLocalFlattenedSearchPath = localFlattenedSearchPath;

        myVLS = vls;

        myLocalVatID = localVatID;

        //Register for the startup message types
        try {
            myDataPath.registerMsgHandler(Msg.STARTUP, this);
            myDataPath.registerMsgHandler(Msg.PROTOCOL_VERSION, this);
            myDataPath.registerMsgHandler(Msg.PROTOCOL_ACCEPTED, this);

            if (!myIsIncoming) {
                myOutgoingSuspendID = outgoingSuspendID;
                myRemoteVatID = remoteVatID;
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                DataOutputStream os = new DataOutputStream(baos);
                os.writeByte(Msg.PROTOCOL_VERSION);
                for (int i = 0; i < Msg.Version.length; i++) {
                    os.writeUTF(Msg.Version[i]);
                }
                sendMessageForSignature(baos.toByteArray());
                sendIWant();
                myState = ST_OUTGOING_EXPECT_IAM;
            } else {
                myState = ST_INCOMING_EXPECT_IWANT;
            }
        } catch (IOException e) {
            Trace.comm.errorm("IOException starting up", e);
            myDataPath.shutDownPath();
        }
    }
// Protocol methods. These methods are presented more or less in the order
// they will be executed during the startup protocol. Note that the methods
// used by the initator of the connection are interleaved with the methods
// used by the receiver of the connection.
//
// The whole process is started in the initator's constructor which sends
// two messages, Msg.PROTOCOL_VERSION, and Msg.STARTUP + TOK_IWANT.

    /**
     * Respond to the initator's Msg.PROTOCOL_VERSION message.
     * <p/>
     * Selects the last version sent by the remote end in their
     * PROTOCOL_VERSION packet which is also supported by this end, as the
     * protocol version to use on this connection. That version is returned in
     * a PROTOCOL_ACCEPTED message.
     *
     * @param packetIn The PROTOCOL_VERSION packet stream postioned after the
     *                 type byte.
     * @throws java.io.IOException When there is no protocol in common with the
     *                             remote end. An ERR_PROTOCOL message is sent
     *                             to the remote end.
     */
    private void checkProtocolVersion(DataInputStream packetIn, byte[] packet)
      throws IOException {
        hisMessagesToSign.addElement(packet);
        // To save their supported versions in case of error
        Vector versions = new Vector(10);
        String protocol = "";
        try {
            while (true) {
                String theirVersion = packetIn.readUTF();
                if (!"".equals(theirVersion)) {
                    versions.addElement(theirVersion);
                    for (int i = 0; i < Msg.Version.length; i++) {
                        if (theirVersion.equals(Msg.Version[i])) {
                            protocol = theirVersion;
                        }
                    }
                }
            }
        } catch (EOFException e) {
            // Handle end of input string
            if (Trace.comm.debug && Trace.ON) {
                String err = "Incoming protocol versions ";
                int size = versions.size();
                for (int i = 0; i < size - 1; i++) {
                    err += (versions.elementAt(i) + ", ");
                }
                if (size > 0) {
                    err += versions.lastElement();
                }
                Trace.comm.debugm(err + " picked " + protocol);
            }
            if ("" == protocol) {
                // Can't agree on a protocol (java interns strings)
                String err = "incoming protocol versions ";
                int size = versions.size();
                for (int i = 0; i < size - 1; i++) {
                    err += (versions.elementAt(i) + ", ");
                }
                if (size > 0) {
                    err += versions.lastElement();
                }
                err += " are not supported, use versions ";
                for (int i = 0; i < Msg.Version.length - 1; i++) {
                    err += (Msg.Version[i] + ", ");
                }
                err += Msg.Version[Msg.Version.length - 1];
                sendErrProtocol(err);
                throw new IOException(err);
            }
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream os = new DataOutputStream(baos);
        os.writeByte(Msg.PROTOCOL_ACCEPTED);
        os.writeUTF(protocol);
        sendMessageForSignature(baos.toByteArray());

        myEMsgProtocolVersion = protocol;
        packetIn.close();
    }

    // Method from the MsgHandler interface.
    /**
     * Process a connection failure
     */
    public void connectionDead(VatTPConnection /*nullOK*/willBeNull,
                               Throwable reason) {
        // Everything should be handled by the DataPath
    }

    /**
     * Perform the first calculation in Diffie Hellman key exchange.
     * <p/>
     * This method gets a random x and calculate g**x mod p.
     *
     * @return A byte array representing the result of the first Diffie Hellman
     *         calculation.
     */
    private byte[] firstDH() {
        long startTime = Trace.comm.timing ? MicroTime.queryTimer() : 0;
        x = new BigInteger(256, ESecureRandom.getESecureRandom());
        byte[] ans = g.modPow(x, modulus).toByteArray();
        if (Trace.comm.timing && Trace.ON) {
            Trace.comm
              .timingm("FirstDiffieHellman time " +
                (MicroTime.queryTimer() - startTime) + " microseconds");
        }
        return ans;
    }

    /**
     * Format a Msg.STARTUP packet for error messages.
     *
     * @param packet the byte array which is the complete packet.
     * @return the packet formated as a string.
     */
    private String formatStartupPacket(byte[] packet) {
        String ret = "";
        try {
            ByteArrayInputStream byteArrayIn =
              new ByteArrayInputStream(packet);
            DataInputStream packetIn = new DataInputStream(byteArrayIn);
            byte msgType = packetIn.readByte();
            ret = "0x" + Integer.toHexString(msgType) + " ";
            int token = packetIn.readByte();
            ret += tokName(token) + " ";
            if (token <= 0 || TOK_BYE == token || TOK_DUP == token ||
              TOK_NOT_ME == token || TOK_IAM == token) {
                ret += packetIn.readUTF();
            }
            packetIn.close();
            return ret;
        } catch (IOException e) {
            Trace.comm.errorm("IOException on byte array??", e);
        }
        return ret + "\n" + HexStringUtils.bytesToReadableHexStr(packet);
    }

    // Method for use by DataPath
    /**
     * Return the connection startup state
     *
     * @return The connection startup state code for debugging purposes.
     */


    int getState() {
        return myState;
    }

    /**
     * Process expecting an GIVEINFO <remoteVatID> <remotePath> <hisPublicKey>
     * -or- DUP message.
     *
     * @param token    the actual message token received.
     * @param packetIn is the DataInputStream on the input message positioned
     *                 after the startup protocol token.
     * @param packet   The entire message received as a string for error
     *                 messages.
     * @throws ConnectionStartupException is thrown if this MsgConnection
     *                                    should terminate.
     * @throws ConnectionStartupLocalException
     *                                    is thrown if this MsgConnection
     *                                    should terminate without notifing the
     *                                    other end.
     */
    private void handleStateIncomingExpectGIVEINFO(byte token,
                                                   DataInputStream packetIn,
                                                   byte[] packet)
      throws IOException {
        hisMessagesToSign.addElement(packet);
        if (myStop) {
            startupError(TOK_DUP, "Stopped connection");
            return;
        } else if (token == TOK_GIVEINFO) {
            myRemoteVatID = packetIn.readUTF();
            String remoteSearchPath = packetIn.readUTF();
            byte[] hisKey = new byte[packetIn.readUnsignedShort()];
            packetIn.readFully(hisKey);
            if (!isHisPublicKeyOK(myRemoteVatID, hisKey, token)) {
                return;
            }

            int i = myDataPath.identifyIncoming(myLocalVatID,
                                                myRemoteVatID,
                                                remoteSearchPath);
            switch (i) {
            case VatTPMgr.LIVES_CONTINUE: {
                myState = ST_INCOMING_EXPECT_GO;
                sendReplyInfo();
                break;
            }
            case VatTPMgr.LIVES_DUP: {
                myDataPath.stopStartUpProtocol();
                startupError(TOK_DUP, "Crossed connections");
                break;
            }
            case VatTPMgr.LIVES_NOTIFY: {
                myState = ST_INCOMING_EXPECT_GO;
                sendYouChose();
                break;
            }
            default: {
                Trace.comm.errorm("Invalid return code=" + i);
                startupError(TOK_ERR_INTERNAL, "Internal error at my end");
                break;
            }
            }
        } else if (token == TOK_DUP) {
            myDataPath.duplicatePath(
              "Incoming crossed connections" + formatStartupPacket(packet),
              false);
        } else if (token == TOK_NOT_ME) {
            startupLocalError("Other end tried to connect to self");
        } else {
            startupError(TOK_ERR_PROTOCOL,
                         "Expected " + tokName(TOK_GIVEINFO) + " got " +
                           formatStartupPacket(packet));
        }
    }

    /**
     * Process expecting a GO <auth suite> <DH parameter> -or- RESUME
     * <suspendID> -or- QUIT message.
     *
     * @param token    the actual message token received.
     * @param packetIn is the DataInputStream on the input message positioned
     *                 after the startup protocol token.
     * @param packet   The entire message received as a string for error
     *                 messages.
     * @throws ConnectionStartupException is thrown if this MsgConnection
     *                                    should terminate.
     * @throws ConnectionStartupLocalException
     *                                    is thrown if this MsgConnection
     *                                    should terminate without notifing the
     *                                    other end.
     */
    private void handleStateIncomingExpectGO(byte token,
                                             DataInputStream packetIn,
                                             byte[] packet)
      throws IOException {
        if (token == TOK_GO) {
            String protocol = packetIn.readUTF();
            if (myDataPath.resumeConnection(null)) {
                if (null != matchProtocols(protocol)) {
                    // We're doing autherization and other end wants it
                    byte[] publicDH = new byte[packetIn.readUnsignedShort()];
                    packetIn.read(publicDH);
                    byte[] sig = new byte[packetIn.readUnsignedShort()];
                    packetIn.read(sig);

                    byte[] dhparm = firstDH();
                    myProtocolSuite = protocol;
                    if (!isSecondDHOK(publicDH, sig, token)) {
                        return;     // bow out, situation already handled
                    }
                    // Get my signature on protocol
                    sig = signSent(dhparm, token);
                    if (null == sig) {
                        return;     // bow out, situation already handled
                    }
                    sendGoToo(protocol, dhparm, sig);
                } else {
                    myProtocolSuite = PROTO_NONE;
                    sendGoToo(PROTO_NONE);
                }
                startupSuccessful();
            } else {
                // the old connection has been killed at this
                // point, so we could perhaps just grab a fresh
                // new one and try again, but it's simpler at this
                // point to force the other side to destroy it's
                // connection and retry it. -emm
                startupError(TOK_BYE, "discarded resumable connection");
            }
        } else if (token == TOK_RESUME) {
            byte[] incomingSuspendID = new byte[packetIn.readUnsignedShort()];
            packetIn.readFully(incomingSuspendID);
            if (myDataPath.resumeConnection(incomingSuspendID)) {
                sendGoToo(null);
                startupSuccessful();
            } else {
                startupError(TOK_BYE, "wrong suspend id");
            }
        } else if (token == TOK_DUP) {
            myDataPath.duplicatePath(
              "Incoming crossed connections " + formatStartupPacket(packet),
              true);
        } else {
            startupError(TOK_ERR_PROTOCOL,
                         "Expected " + tokName(TOK_GO) + " got " +
                           formatStartupPacket(packet));
        }
    }

    /**
     * Process expecting a IWANT <localVatID> message.
     *
     * @param token    the actual message token received.
     * @param packetIn is the DataInputStream on the input message positioned
     *                 after the startup protocol token.
     * @param packet   The entire message received as a string for error
     *                 messages.
     * @throws ConnectionStartupException is thrown if this MsgConnection
     *                                    should terminate.
     */
    private void handleStateIncomingExpectIWANT(byte token,
                                                DataInputStream packetIn,
                                                byte[] packet)
      throws IOException {
        hisMessagesToSign.addElement(packet);
        if (myStop) {
            startupError(TOK_DUP, "Stopped connection");
            return;
        }
        if (token != TOK_IWANT) {
            /* IWANT <id> */
            startupError(TOK_ERR_PROTOCOL,
                         "Expected " + tokName(TOK_IWANT) + " got " +
                           formatStartupPacket(packet));
            return;
        }
        String wantedVatID = packetIn.readUTF();
        if (wantedVatID.equals(myLocalVatID) ||
          wantedVatID.equals(VatIdentity.WHOEVER)) {
            sendIAm();
            myState = ST_INCOMING_EXPECT_GIVEINFO;
            return;
        }
        if (myVLS != null) {
            String[] locations = myVLS.getLocations(wantedVatID);
            if (locations.length >= 1) {
                //XXX future bug: only uses the first one
                sendTry(locations[0]);
                myDataPath.tryNext("VLS lookup completed");
                return;
            }
        }
        if (Trace.comm.debug && Trace.ON) {
            Trace.comm
              .debugm("got request for " + wantedVatID + " when I am " +
                myLocalVatID);
        }
        startupError(TOK_NOT_ME, "I don't know " + wantedVatID);
    }

    /*
    * GOTOO
    *   -or-
    * BYE
    */
    /**
     * Process expecting a GOTOO <auth suite> <DH parameter> -or- BYE message.
     *
     * @param token    the actual message token received.
     * @param packetIn is the DataInputStream on the input message positioned
     *                 after the startup protocol token.
     * @param packet   The entire message received as a string for error
     *                 messages.
     * @throws ConnectionStartupException is thrown if this MsgConnection
     *                                    should terminate.
     * @throws ConnectionStartupLocalException
     *                                    is thrown if this MsgConnection
     *                                    should terminate without notifing the
     *                                    other end.
     */
    private void handleStateOutgoingExpectGOTOO(byte token,
                                                DataInputStream packetIn,
                                                byte[] packet)
      throws IOException {
        if (token == TOK_GOTOO) {
            if (null != myOutgoingSuspendID) {
                myProtocolSuite = null;
            } else {
                String protocol = packetIn.readUTF();
                if (null != matchProtocols(protocol)) {
                    myProtocolSuite = protocol;
                    byte[] publicDH = new byte[packetIn.readUnsignedShort()];
                    packetIn.read(publicDH);
                    byte[] sig = new byte[packetIn.readUnsignedShort()];
                    packetIn.read(sig);
                    if (!isSecondDHOK(publicDH, sig, token)) {
                        return;     // Bow out, situation already handled
                    }
                } else {
                    myProtocolSuite = PROTO_NONE;
                }
            }
            startupSuccessful();
        } else if (token == TOK_BYE) {
            myDataPath.cantResume(formatStartupPacket(packet));
        } else {
            startupError(TOK_ERR_PROTOCOL,
                         "Expected " + tokName(TOK_GOTOO) + " got " +
                           formatStartupPacket(packet));
        }
    }

    /**
     * Process expecting an IAM <remoteVatID> <myPublicKey> -or- TRY <path>
     * -or- NOTME -or- DUP message.
     *
     * @param token    the actual message token received.
     * @param packetIn is the DataInputStream on the input message positioned
     *                 after the startup protocol token.
     * @param packet   The entire message received as a string for error
     *                 messages.
     * @throws ConnectionStartupException is thrown if this MsgConnection
     *                                    should terminate.
     * @throws ConnectionStartupLocalException
     *                                    is thrown if this MsgConnection
     *                                    should terminate without notifing the
     *                                    other end.
     */
    private void handleStateOutgoingExpectIAM(byte token,
                                              DataInputStream packetIn,
                                              byte[] packet)
      throws IOException {
        hisMessagesToSign.addElement(packet);
        if (myStop) {
            startupError(TOK_DUP, "Stopped connection");
            return;
        } else if (token == TOK_IAM) {
            String remoteVatID = packetIn.readUTF();
            String remoteFlattenedSearchPath = packetIn.readUTF();
            if (remoteVatID.equals(myLocalVatID)) {
                ByteArrayOutputStream outbuf = new ByteArrayOutputStream();
                DataOutputStream msgOut = new DataOutputStream(outbuf);
                msgOut.writeByte(Msg.STARTUP);
                msgOut.writeByte(TOK_NOT_ME);
                msgOut.writeUTF("Connecting to self with connectToVatAt");
                myDataPath.enqueue(outbuf.toByteArray());
                myDataPath.connectToSelf();
            } else if (remoteVatID.equals(myRemoteVatID) ||
              myRemoteVatID.equals(VatIdentity.WHOEVER)) {
                byte[] hisKey = new byte[packetIn.readUnsignedShort()];
                packetIn.readFully(hisKey);
                if (!isHisPublicKeyOK(remoteVatID, hisKey, token)) {
                    return;
                }

                if (myRemoteVatID.equals(VatIdentity.WHOEVER)) {
                    myRemoteVatID = remoteVatID;
                    Object orders = myDataPath.identifyOutgoing(myLocalVatID,
                                                                myRemoteVatID,
                                                                remoteFlattenedSearchPath);
                    if (null == orders) {
                        myOutgoingSuspendID = null;
                    } else if (orders instanceof byte[]) {
                        myOutgoingSuspendID = (byte[])orders;
                    } else {
                        myDataPath.stopStartUpProtocol();
                        startupError(TOK_DUP, (String)orders);
                        return;
                    }
                }
                myState = ST_OUTGOING_EXPECT_REPLYINFO;
                sendGiveInfo();
            } else {
                startupError(TOK_ERR_WRONG_ID, "You're not who I asked for");
            }
        } else if (token == TOK_TRY) {
            ConstList tryPath = EARL.parseSearchPath(packetIn.readUTF());
            myDataPath.extendSearchPath(tryPath);
            myState = ST_TRY_NEXT;
            startupLocalError("got " + formatStartupPacket(packet));
        } else if (token == TOK_NOT_ME) {
            myState = ST_TRY_NEXT;
            startupLocalError("got " + formatStartupPacket(packet));
        } else if (token == TOK_DUP) {
            myDataPath.duplicatePath(
              "outgoing crossed connections" + formatStartupPacket(packet),
              true);
        } else {
            startupError(TOK_ERR_PROTOCOL,
                         "Expected " + tokName(TOK_IAM) + " got " +
                           formatStartupPacket(packet));
        }
    }

    /**
     * Process expecting a REPLYINFO <remotePath> -or- YOUCHOSE <remotePath>
     * -or- DUP message.
     *
     * @param token    the actual message token received.
     * @param packetIn is the DataInputStream on the input message positioned
     *                 after the startup protocol token.
     * @param packet   The entire message received as a string for error
     *                 messages.
     * @throws ConnectionStartupException is thrown if this MsgConnection
     *                                    should terminate.
     * @throws ConnectionStartupLocalException
     *                                    is thrown if this MsgConnection
     *                                    should terminate without notifing the
     *                                    other end.
     */
    private void handleStateOutgoingExpectREPLYINFO(byte token,
                                                    DataInputStream packetIn,
                                                    byte[] packet)
      throws IOException {
        hisMessagesToSign.addElement(packet);
        if (myStop) {
            startupError(TOK_DUP, "Stopped connection");
            return;
        } else if (token == TOK_REPLYINFO || token == TOK_YOUCHOSE) {
            //String remoteSearchPath = packetIn.readUTF();
            String protocols = packetIn.readUTF();
            if (TOK_YOUCHOSE == token && myDataPath.isChoiceIncoming()) {
                myDataPath.stopStartUpProtocol();
                startupError(TOK_DUP, "Crossed connections");
                return;
            }
            //String[] path = EARL.parseSearchPath(remoteSearchPath);
            //myDataPath.extendSearchPath(path);
            myState = ST_OUTGOING_EXPECT_GOTOO;
            if (null != myOutgoingSuspendID) {
                sendResume();
                return;
            }
            for (; ;) {
                int i = protocols.indexOf(',');
                String protocol;
                if (i < 0) {
                    protocol = protocols;
                    protocols = "";
                } else {
                    protocol = protocols.substring(0, i);
                    protocols = protocols.substring(i + 1);
                }
                if (protocol.equals("")) {
                    //No agreement
                    startupError(TOK_ERR_PROTOCOL,
                                 "Can't agree on an authorization protocol");
                    return;
                }
                if (null != matchProtocols(protocol)) {
                    byte[] dhparm = firstDH();
                    byte[] sig = signSent(dhparm, token);
                    if (null == sig) {
                        return;     // bow out, situation already handled
                    }
                    sendGo(protocol, dhparm, sig);
                    return;
                } else if (PROTO_NONE.equals(protocol)) {
                    sendGo(PROTO_NONE);
                    return;
                }
            }
        } else if (token == TOK_DUP) {
            myDataPath.duplicatePath(
              "Outgoing crossed connections " + formatStartupPacket(packet),
              true);
            return;
        } else {
            startupError(TOK_ERR_PROTOCOL,
                         "Expected " + tokName(TOK_REPLYINFO) + " got " +
                           formatStartupPacket(packet));
            return;
        }
    }

    /**
     * Decode the line form of his public key and check that it is for his
     * vatID.
     *
     * @param vatID  His vatID.
     * @param hisKey His DSA public key expressed as a byte array.
     * @return is true if his public key matches is vatID, false otherwise.
     * @throws ConnectionStartupException If hisKey has a bad format, or is
     *                                    invalid.
     */
    private boolean isHisPublicKeyOK(String vatID, byte[] hisKey, int token)
      throws IOException {

        try {
            X509EncodedKeySpec bobPubKeySpec = new X509EncodedKeySpec(hisKey);
            KeyFactory keyFactory = KeyFactory.getInstance("DSA");
            myHisPublicKey = keyFactory.generatePublic(bobPubKeySpec);
            // XXX TODO: dispatch on leaf exception if it's one of
            // InvalidKeyException or NumberFormatException. XXX ?? Is this
            // the right thing to do?
            if (!vatID.equals(VatIdentity.calculateVatID(myHisPublicKey))) {
                startupError(TOK_ERR_WRONG_ID,
                             "Your key is not for your vat ID");
                return false;
            }
        } catch (InvalidKeySpecException e) {
            Trace.comm.debugm("InvalidKeySpecException", e);
            startupError(TOK_ERR_WRONG_ID,
                         "You provided an invalid public key");
            return false;
        } catch (NoSuchAlgorithmException e) {
            Trace.comm.debugm("NoSuchAlgorithmException ", e);
            startupError(TOK_ERR_WRONG_ID,
                         "Internal error - DSA not supported",
                         e);
            return false;
        }
        return true;
    }

    /**
     * Perform the second calculation in Diffie Hellman key exchange.
     * <p/>
     * This method takes the remote end's g**y mod p, and the sitnature on it.
     * It checks ths signature and performs the second Diffie Hellman
     * calculation as (g**y mod p) ** x mod p.
     * <p/>
     * It then takes the resulting dh secret and calculates values for the
     * initial sequence numbers (both send path and receive path).
     *
     * @param publicdh is a byte array containing the far end's first Diffie
     *                 Hellman calculation.
     * @param sig      is a byte array containing the far end's DSS signature
     *                 on the startup protocol.
     * @param token    The startup protocol token being processed. (For
     *                 determining whether we are the initator or receipent and
     *                 error reporting.)
     * @return is true if the connection attempt should continue, false
     *         otherwise.
     */
    private boolean isSecondDHOK(byte[] publicdh, byte[] sig, byte token)
      throws IOException {
        long startTime = Trace.comm.timing ? MicroTime.queryTimer() : 0;
        if (!isSigGood(publicdh, sig, token)) {
            return false;     // Bow out, messages already issued
        }
        BigInteger dh = new BigInteger(publicdh);
        //byte[] dhSecret = dh.modPow(x,modulus).toByteArray();
        BigInteger test = dh.modPow(x, modulus);
        byte[] dhSecret = test.toByteArray();
        // Now calculate the various keys from dhSecret
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            startupError(TOK_ERR_INTERNAL, "Unable to build MD5", e);
            return false;
        }
        byte[] ivs = md5Hash(0x99, dhSecret, md5);
        byte[] sendIV;
        byte[] recvIV;
        if (TOK_GO == token) {
            // We are receipent
            sendIV = subbytearray(ivs, 8, 8);
            recvIV = subbytearray(ivs, 0, 8);
        } else {
            // We are initiator
            sendIV = subbytearray(ivs, 0, 8);
            recvIV = subbytearray(ivs, 8, 8);
        }
        if (Trace.comm.timing && Trace.ON) {
            Trace.comm
              .timingm("SecondDiffieHellman time " +
                (MicroTime.queryTimer() - startTime) + " microseconds");
        }
        myDataPath.setAuthorizationSecrets(dhSecret, sendIV, recvIV);
        return true;
    }

    /**
     * Check the signature on the data received.
     *
     * @param data  is the data to check.
     * @param sig   is the signature.
     * @param token is the startup protocol token being processed for error
     *              reporting.
     * @return is true if the signature is good. False for a bunch of "can't
     *         occur" situations, and things like invalid or no public key from
     *         the other end, or invalid signature.
     */
    private boolean isSigGood(byte[] data, byte[] sig, byte token)
      throws IOException {
        if (null == mySignature) {
            try {
                mySignature = Signature.getInstance("DSA");
            } catch (NoSuchAlgorithmException e) {
                Trace.comm.errorm("Unable to build DSA", e);
                startupError(TOK_ERR_INTERNAL, "Unable to build DSA", e);
                return false;
            }
        }
        if (null == myHisPublicKey) {
            startupError(TOK_ERR_PROTOCOL, "No public key from other end");
            return false;
        }
        try {
            mySignature.initVerify(myHisPublicKey);
        } catch (InvalidKeyException e) {
            startupError(TOK_ERR_PROTOCOL, "Invalid His Public Key", e);
            return false;
        }
        try {
            for (int i = 0; i < hisMessagesToSign.size(); i++) {
                mySignature.update((byte[])hisMessagesToSign.elementAt(i));
            }
            mySignature.update(data);
            if (!mySignature.verify(sig)) {
                startupError(TOK_ERR_PROTOCOL, "Invalid signature");
                return false;
            }
        } catch (SignatureException e) {
            startupError(TOK_ERR_PROTOCOL, "Invalid signature", e);
            return false;
        } catch (NumberFormatException e) {
            startupError(TOK_ERR_PROTOCOL, "Invalid signature", e);
            return false;
        }
        Trace.comm.eventm("Signature checked as valid");
        return true;
    }

    /**
     * Match a suggested authorization protocol with the ones we support. The
     * comparison ignores the case of the letters.
     *
     * @param protocol the one to match.
     * @return the standard cased version of the matched name or null if no
     *         match.
     */
    private String matchProtocols(String protocol) {
        for (int i = 0; i < TheAuthProtocolTable.length; i++) {
            if (protocol.equalsIgnoreCase(TheAuthProtocolTable[i])) {
                return TheAuthProtocolTable[i];
            }
        }
        return null;
    }

    /**
     * Calculate the MD5 hash of some data with a specific padding.
     * <p/>
     * The padding allows different values to be obtained from the same data by
     * varing the padding value. We use it to get the different authentication
     * values from the same Diffie Hellman shared secret.
     *
     * @param pad  The int (treated as a byte) to be used to pad the MD5
     *             calculation.
     * @param data The data to be hashed.
     * @param md5  The message digest object to be used.
     * @return A byte array representing the hash.
     */
    private byte[] md5Hash(int pad, byte[] data, MessageDigest md5) {
        byte[] mdConst = new byte[16];
        for (int i = 0; i < mdConst.length; i++) {
            mdConst[i] = (byte)pad;
        }
        md5.reset();                    //Initialize a new hash
        md5.update(mdConst);
        return md5.digest(data);
    }

    // Method from the MsgHandler interface.
    /**
     * Process the next packet of the connection startup protocol.
     *
     * @param packetArray The startup protocol message to process.
     */
    public void processMessage(byte packetArray[],
                               VatTPConnection /*nullOK*/willBeNull) {
        ByteArrayInputStream byteArrayIn =
          new ByteArrayInputStream(packetArray);
        DataInputStream packetIn = new DataInputStream(byteArrayIn);

        if (Trace.comm.verbose && Trace.ON) {
            Trace.comm
              .verbosem(HexStringUtils.bytesToReadableHexStr(packetArray));
        }

        try {
            try {
                byte msgType = packetIn.readByte();
                if (Msg.PROTOCOL_VERSION == msgType) {
                    checkProtocolVersion(packetIn, packetArray);
                } else if (Msg.PROTOCOL_ACCEPTED == msgType) {
                    processProtocolAccepted(packetIn, packetArray);
                } else if (Msg.STARTUP == msgType) {
                    //Since all startup messages have the same basic structure,
                    //we'll parse them here and pass the results to the myState
                    //based method.
                    byte token = packetIn.readByte();

                    if (Trace.comm.verbose && Trace.ON) {
                        Trace.comm
                          .verbosem("received startup packet " +
                            formatStartupPacket(packetArray));
                    }

                    if (token < 0) {
                        startupLocalError("Error " + tokName(token) +
                          " from other side: " + packetIn.readUTF());
                        return;
                    }

                    switch (myState) {
                    case ST_INCOMING_EXPECT_IWANT:
                        handleStateIncomingExpectIWANT(token,
                                                       packetIn,
                                                       packetArray);
                        break;
                    case ST_OUTGOING_EXPECT_IAM:
                        handleStateOutgoingExpectIAM(token,
                                                     packetIn,
                                                     packetArray);
                        break;
                    case ST_INCOMING_EXPECT_GIVEINFO:
                        handleStateIncomingExpectGIVEINFO(token,
                                                          packetIn,
                                                          packetArray);
                        break;
                    case ST_OUTGOING_EXPECT_REPLYINFO:
                        handleStateOutgoingExpectREPLYINFO(token,
                                                           packetIn,
                                                           packetArray);
                        break;
                    case ST_INCOMING_EXPECT_GO:
                        handleStateIncomingExpectGO(token,
                                                    packetIn,
                                                    packetArray);
                        break;
                    case ST_OUTGOING_EXPECT_GOTOO:
                        handleStateOutgoingExpectGOTOO(token,
                                                       packetIn,
                                                       packetArray);
                        break;
                    case ST_DEAD:
                        if (Trace.comm.debug && Trace.ON) {
                            Trace.comm
                              .debugm(
                                "dead MsgConnection ignoring startup packet " +
                                  formatStartupPacket(packetArray));
                        }
                        break;
                    default:
                        startupLocalError(
                          "state machine confused, in state " + myState);
                        break;
                    }
                    packetIn.close();
                }
            } catch (IOException e) {
                try {
                    startupError(TOK_ERR_PROTOCOL,
                                 "Exception handling packet: ",
                                 e);
                } catch (IOException e2) {
                    Trace.comm
                      .errorm("IOException " + e2 + " handling exception", e);
                }
            }
        } catch (Exception e) {
            Trace.comm
              .errorm("Exception during startup for\n  " + myDataPath, e);
            if (!myStop) {
                myDataPath.tryNext(
                  e.toString() + "\n" + ThrowableSugar.javaStack(e));
            } else {
                myDataPath.shutDownPath();
            }
        }
    }

    /**
     * Handle the Msg.PROTOCOL_ACCEPTED message. Determine which version of the
     * protocol was accepted and configure this end to use that version.
     *
     * @param packet the packet received.
     */
    private void processProtocolAccepted(DataInputStream packetIn,
                                         byte[] packet) throws IOException {
        hisMessagesToSign.addElement(packet);
        String theirVersion = packetIn.readUTF();
        for (int i = 0; i < Msg.Version.length; i++) {
            if (theirVersion.equals(Msg.Version[i])) {
                myEMsgProtocolVersion = Msg.Version[i];
                return;
            }
        }
        String err = "incoming protocol version " + theirVersion;
        err += " are not supported, use versions ";
        for (int i = 0; i < Msg.Version.length - 1; i++) {
            err += (Msg.Version[i] + ", ");
        }
        err += Msg.Version[Msg.Version.length - 1];
        sendErrProtocol(err);
        throw new IOException(err);
    }

    /**
     * Send a TOK_ERR_PROTOCOL packet
     *
     * @param msg is a message describing the error.
     * @throws IOException is thrown if there is a problem on the send.
     */
    private void sendErrProtocol(String msg) throws IOException {
        ByteArrayOutputStream outbuf = new ByteArrayOutputStream();
        DataOutputStream msgOut = new DataOutputStream(outbuf);
        msgOut.writeByte(Msg.STARTUP);
        msgOut.writeByte(TOK_ERR_PROTOCOL);
        msgOut.writeUTF(msg);
        myDataPath.enqueue(outbuf.toByteArray());
    }

    /**
     * Send a TOK_GIVEINFO packet
     *
     * @throws IOException is thrown if there is a problem on the send.
     */
    private void sendGiveInfo() throws IOException {
        ByteArrayOutputStream outbuf = new ByteArrayOutputStream();
        DataOutputStream msgOut = new DataOutputStream(outbuf);
        msgOut.writeByte(Msg.STARTUP);
        msgOut.writeByte(TOK_GIVEINFO);
        msgOut.writeUTF(myLocalVatID);
        msgOut.writeUTF(myLocalFlattenedSearchPath);
        byte[] key = myIdentityKeys.getPublic().getEncoded();
        msgOut.writeShort((short)(key.length));
        msgOut.write(key);
        sendMessageForSignature(outbuf.toByteArray());
    }

    /**
     * Send a TOK_GO packet
     *
     * @param protocol is the chosen authorization protocol as a String.
     * @throws IOException is thrown if there is a problem on the send.
     */
    private void sendGo(String protocol) throws IOException {
        ByteArrayOutputStream outbuf = new ByteArrayOutputStream();
        DataOutputStream msgOut = new DataOutputStream(outbuf);
        msgOut.writeByte(Msg.STARTUP);
        msgOut.writeByte(TOK_GO);
        msgOut.writeUTF(protocol);
        // No need to sign, no signature
        myDataPath.enqueue(outbuf.toByteArray());
    }

    /**
     * Send a TOK_GO packet
     *
     * @param protocol is the chosen authorization protocol as a String.
     * @param dhparm   is the public Diffie Hellman parameter
     * @param sig      is the signature for authentication.
     * @throws IOException is thrown if there is a problem on the send.
     */
    private void sendGo(String protocol, byte[] dhparm, byte[] sig)
      throws IOException {
        ByteArrayOutputStream outbuf = new ByteArrayOutputStream();
        DataOutputStream msgOut = new DataOutputStream(outbuf);
        msgOut.writeByte(Msg.STARTUP);
        msgOut.writeByte(TOK_GO);
        msgOut.writeUTF(protocol);
        msgOut.writeShort((short)(dhparm.length));
        msgOut.write(dhparm);
        msgOut.writeShort((short)(sig.length));
        msgOut.write(sig);
        // No need to sign, this one contains the signature.
        myDataPath.enqueue(outbuf.toByteArray());
    }

    /**
     * Send a TOK_GOTOO packet.
     *
     * @param protocol is the chosen authorization protocol as a String or
     *                 null. If null is specified, nothing will be sent in that
     *                 place.
     * @throws IOException is thrown if there is a problem on the send.
     */
    private void sendGoToo(String /*nullOK*/ protocol) throws IOException {
        ByteArrayOutputStream outbuf = new ByteArrayOutputStream();
        DataOutputStream msgOut = new DataOutputStream(outbuf);
        msgOut.writeByte(Msg.STARTUP);
        msgOut.writeByte(TOK_GOTOO);
        if (null != protocol) {
            msgOut.writeUTF(protocol);
        }
        // No need to sign, there is no checking
        myDataPath.enqueue(outbuf.toByteArray());
    }

    /**
     * Send a TOK_GOTOO packet
     *
     * @param protocol is the chosen authorization protocol as a String.
     * @param dhparm   is the public Diffie Hellman parameter
     * @param sig      is the signature for authentication.
     * @throws IOException is thrown if there is a problem on the send.
     */
    private void sendGoToo(String protocol, byte[] dhparm, byte[] sig)
      throws IOException {
        ByteArrayOutputStream outbuf = new ByteArrayOutputStream();
        DataOutputStream msgOut = new DataOutputStream(outbuf);
        msgOut.writeByte(Msg.STARTUP);
        msgOut.writeByte(TOK_GOTOO);
        msgOut.writeUTF(protocol);
        msgOut.writeShort((short)(dhparm.length));
        msgOut.write(dhparm);
        msgOut.writeShort((short)(sig.length));
        msgOut.write(sig);
        // No need to sign, this message contains the signature
        myDataPath.enqueue(outbuf.toByteArray());
    }

    /**
     * Send a TOK_IAM packet
     *
     * @throws IOException is thrown if there is a problem on the send.
     */
    private void sendIAm() throws IOException {
        ByteArrayOutputStream outbuf = new ByteArrayOutputStream();
        DataOutputStream msgOut = new DataOutputStream(outbuf);
        msgOut.writeByte(Msg.STARTUP);
        msgOut.writeByte(TOK_IAM);
        msgOut.writeUTF(myLocalVatID);
        msgOut.writeUTF(myLocalFlattenedSearchPath);
        byte[] key = myIdentityKeys.getPublic().getEncoded();
        msgOut.writeShort((short)(key.length));
        msgOut.write(key);
        sendMessageForSignature(outbuf.toByteArray());
    }

    /**
     * Send a TOK_IWANT packet
     *
     * @throws IOException is thrown if there is a problem on the send.
     */
    private void sendIWant() throws IOException {
        ByteArrayOutputStream outbuf = new ByteArrayOutputStream();
        DataOutputStream msgOut = new DataOutputStream(outbuf);
        msgOut.writeByte(Msg.STARTUP);
        msgOut.writeByte(TOK_IWANT);
        msgOut.writeUTF(myRemoteVatID);
        sendMessageForSignature(outbuf.toByteArray());
    }

    /**
     * Send a message to the other end and save the message for signature
     * calculation.
     *
     * @param message is the message to send.
     */
    private void sendMessageForSignature(byte[] message) {
        myMessagesToSign.addElement(message);
        myDataPath.enqueue(message);
    }

    /**
     * Send a TOK_REPLYINFO packet
     *
     * @throws IOException is thrown if there is a problem on the send.
     */
    private void sendReplyInfo() throws IOException {
        ByteArrayOutputStream outbuf = new ByteArrayOutputStream();
        DataOutputStream msgOut = new DataOutputStream(outbuf);
        msgOut.writeByte(Msg.STARTUP);
        msgOut.writeByte(TOK_REPLYINFO);
        msgOut.writeUTF(TheAuthProtocols);
        sendMessageForSignature(outbuf.toByteArray());
    }

    /**
     * Send a TOK_RESUME packet
     *
     * @throws IOException is thrown if there is a problem on the send.
     */
    private void sendResume() throws IOException {
        ByteArrayOutputStream outbuf = new ByteArrayOutputStream();
        DataOutputStream msgOut = new DataOutputStream(outbuf);
        msgOut.writeByte(Msg.STARTUP);
        msgOut.writeByte(TOK_RESUME);
        msgOut.writeShort((short)(myOutgoingSuspendID.length));
        msgOut.write(myOutgoingSuspendID);
        // We don't need to sign for resume.
        myDataPath.enqueue(outbuf.toByteArray());
    }

    /**
     * Send a TOK_TRY packet
     *
     * @throws IOException is thrown if there is a problem on the send.
     */
    private void sendTry(String location) throws IOException {
        ByteArrayOutputStream outbuf = new ByteArrayOutputStream();
        DataOutputStream msgOut = new DataOutputStream(outbuf);
        msgOut.writeByte(Msg.STARTUP);
        msgOut.writeByte(TOK_TRY);
        msgOut.writeUTF(location);
        // Don't need signature. This ends the protocol
        myDataPath.enqueue(outbuf.toByteArray());
    }

    /**
     * Send a TOK_YOUCHOSE packet
     *
     * @throws IOException is thrown if there is a problem on the send.
     */
    private void sendYouChose() throws IOException {
        ByteArrayOutputStream outbuf = new ByteArrayOutputStream();
        DataOutputStream msgOut = new DataOutputStream(outbuf);
        msgOut.writeByte(Msg.STARTUP);
        msgOut.writeByte(TOK_YOUCHOSE);
        msgOut.writeUTF(TheAuthProtocols);
        sendMessageForSignature(outbuf.toByteArray());
    }

    /**
     * Return a our signature on the data sent.
     *
     * @param data  The additional data to sign.
     * @param token The startup protocol token being processed for error
     *              reporting.
     * @return The DSS signature on the data as a byte array.
     * @throws ConnectionStartupException For a bunch of "can't occur"
     *                                    situations.
     */

    private byte[] signSent(byte[] data, byte token) throws IOException {
        long startTime = Trace.comm.timing ? MicroTime.queryTimer() : 0;
        if (null == mySignature) {
            try {
                mySignature = Signature.getInstance("DSA");
            } catch (NoSuchAlgorithmException e) {
                startupError(TOK_ERR_INTERNAL, "Unable to build DSA", e);
                return null;
            }
        }
        try {
            //WARNING - WARNING - WARNING
            // In order to use ESecureRandom to calculate each signature in
            // mySignature, we directly set the random signature seed. If we
            // ever calculate two signatures with the same seed, we have blown
            // the security of the secret key.
            // I (WSF) think it will be easier to avoid this problem if
            // mySignature remains private, and signatures are only calculated
            // one place in the code.
            PrivateKey pk = myIdentityKeys.getPrivate();
            BigInteger q = ((DSAPrivateKey)pk).getParams().getQ();
            while (true) {
                BigInteger rn =
                  new BigInteger(160, ESecureRandom.getESecureRandom(null, 0));
                if ((rn.signum() > 0) && (rn.compareTo(q) < 0)) {
                    mySignature.setParameter("KSEED", rn.toByteArray());
                    break;
                }
            }
            mySignature.initSign(pk);
        } catch (InvalidKeyException e) {
            startupError(TOK_ERR_INTERNAL, "Invalid private key???", e);
            return null;
        }
        byte[] signature = null;
        try {
            for (int i = 0; i < myMessagesToSign.size(); i++) {
                mySignature.update((byte[])myMessagesToSign.elementAt(i));
            }
            mySignature.update(data);
            signature = mySignature.sign();
        } catch (SignatureException e) {
            startupError(TOK_ERR_INTERNAL, "Unable to sign???", e);
            return null;
        }
        if (Trace.comm.timing && Trace.ON) {
            Trace.comm
              .timingm("Signing time " + (MicroTime.queryTimer() - startTime) +
                " microseconds");
        }
        return signature;
    }

    /**
     * Terminate the connection setup protocol with an error that will get
     * passed to the remote end too.
     *
     * @param errorToken The error token to be passed to the remote end
     * @param msg        A message string describing the error.
     */
    private void startupError(byte errorToken, String msg) throws IOException {
        ByteArrayOutputStream outbuf = new ByteArrayOutputStream();
        DataOutputStream msgOut = new DataOutputStream(outbuf);
        msgOut.writeByte(Msg.STARTUP);
        msgOut.writeByte(errorToken);
        msgOut.writeUTF(msg);
        myDataPath.enqueue(outbuf.toByteArray());

        if (!myStop) {
            myDataPath.tryNext(msg);
        } else {
            myDataPath.shutDownPath();
        }
    }

    /**
     * Terminate the connection setup protocol with an error that will get
     * passed to the remote end too.
     *
     * @param errorToken The error token to be passed to the remote end
     * @param msg        A message string describing the error.
     * @param t          A throwable associated with the error.
     */
    private void startupError(byte errorToken, String msg, Throwable t)
      throws IOException {
        ByteArrayOutputStream outbuf = new ByteArrayOutputStream();
        DataOutputStream msgOut = new DataOutputStream(outbuf);
        msgOut.writeByte(Msg.STARTUP);
        msgOut.writeByte(errorToken);
        msgOut.writeUTF(msg + t);
        myDataPath.enqueue(outbuf.toByteArray());

        Trace.comm.errorm("Startup Exception (" + msg + ")", t);
        if (!myStop) {
            myDataPath.tryNext(msg);
        } else {
            myDataPath.shutDownPath();
        }
    }

    /**
     * Terminate the connection setup protocol with an error.
     */
    private void startupLocalError(String msg) {
        if (!myStop) {
            myDataPath.tryNext(msg);
        } else {
            myDataPath.shutDownPath();
        }
    }

    /**
     * Startup protocol has succeeded, let the messages flow.
     */
    private void startupSuccessful() throws IOException {
        myState = ST_EXPECT_MESSAGE;
        myDataPath.unRegisterMsgHandler(Msg.STARTUP, this);
        myDataPath.unRegisterMsgHandler(Msg.PROTOCOL_VERSION, this);
        myDataPath.unRegisterMsgHandler(Msg.PROTOCOL_ACCEPTED, this);

        myDataPath.startupSuccessful(myEMsgProtocolVersion, myProtocolSuite);
    }

    // Method for use by DataPath
    /**
     * Stop the start up protocol. This method is called by the DataPath
     * relaying a call from the VatTPMgr when it determines that there are two
     * connections being built between this vat and another vat, andthat this
     * connection is the one that should be abandonded.
     */


    void stopStartUpProtocol() {
        myStop = true;
    }

    /**
     * Return a subarray of a given array.
     * <p/>
     * The subarray must be within the given array or an exception will be
     * thrown.
     *
     * @param bytes  The array.
     * @param offset The offset in bytes to the start of the subarray.
     * @param len    The length of the subarray.
     * @return A new byte array which is the subarray.
     */
    private byte[] subbytearray(byte[] bytes, int offset, int len) {
        byte[] ans = new byte[len];
        System.arraycopy(bytes, offset, ans, 0, len);
        return ans;
    }

    /**
     * Translate a startup protocol message token to a printable string for
     * error messages and the like.
     *
     * @param tok The integer startup token to represent as a string.
     * @return A string value for the token or TOK_ERR_??? if tok is negative,
     *         or TOK_??? if tok is positive.
     */
    static private String tokName(int tok) {
        if (tok < 0) {
            tok = -tok;
            if (tok > errTokNames.length) {
                return "TOK_ERR_???[-" + tok + "]";
            }
            return errTokNames[tok - 1] + "[-" + tok + "]";
        }
        if (tok == 0) {
            return "TOK_???[0]";
        }
        if (tok > tokNames.length) {
            return "TOK_???[" + tok + "]";
        }
        return tokNames[tok - 1] + "[" + tok + "]";
    }
}
