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

import net.vattp.security.MicroTime;
import org.erights.e.develop.assertion.T;
import org.erights.e.develop.exception.NestedIOException;
import org.erights.e.develop.trace.Trace;
import org.erights.e.elib.util.HexStringUtils;
import org.erights.e.elib.vat.SynchQueue;
import org.erights.e.elib.vat.Vat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.BindException;
import java.net.InetAddress;
import java.net.NoRouteToHostException;
import java.net.Socket;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Hashtable;
import java.util.Vector;

/**
 * A thread for writing data to an OutputStream that might block. Incoming
 * chunks of data are enqueued. The thread dequeues chunks and writes them.
 * Exceptions during the actual writing are sent to the RawConnection for
 * delivery back into the vat via noticeProblem().
 * <p/>
 * This class generates network packets in the following forms:
 * <p/>
 * If no authentication, no mac, no aggragation: (dataLength, data)
 * <p/>
 * If authentication, mac, aggragation: (n,commlength) (mac) (msg1len, msg1,
 * msg2len, msg2 ...) (pad)
 * <p/>
 * commLength is the length of the data in the message on the comm link.
 * <p/>
 * If commLength < 128 (2**7) then n == 1. (One high bit zero) If commLength <
 * 16,384 (2**14) then n == 2. (Two high bits are 10) If commLength < 2,097,152
 * (2**21) then n == 3. (Three high bits are 110).
 * <p/>
 * Since the maximum length packet is currently 1024*1024, the above encoding
 * is sufficent. If longer packets become supported, then If commLength < 2**28
 * then n == 4. (Four high bits are 1110) and If commLength < 2**31 then n ==
 * 5. (Five high bits are 11110). N.B. In Java, array.length is a 31 bit int,
 * limiting the maximum length we must support.
 * <p/>
 * The MAC is always calculated over the aggragated data. N.B. the message
 * building logic prepends a 4 byte message length field to each message, so
 * the MAC includes the message length(s).
 * <p/>
 * pad is padding to make the actual packet length a multiple of the basic
 * block size.
 * <p/>
 * The whole packet is authenticated with the authentication algorthm and mode
 * specified when authentication is turned on.
 *
 * @author Bill Frantz
 */
class SendThread extends Thread {

    /**
     * Notify DataPath every NOTIFY_EVERY bytes of send progress
     */
    static private final int NOTIFY_EVERY = 10000;

    private OutputStream myOutputStream;

    private Socket mySocket;

    private NetAddr myLocalAddr;

    private final String myRemoteAddr;

    private DataPath myDataPath;

    private final SynchQueue myReader;

    private final Vat myVat;    // For synchronizing into-vat calls

    /**
     * A Hashtable of the InetAddresses tried which failed due to host related
     * problems. They are not to be tried again in this attempt to connect to
     * the vat. The key is InetAddress, data is InetAddress.
     */
    private Hashtable myAddressesTried;

    // The following fields are for aggragating messages
    static private final int MAX_AGGRAGATION = 1024;

    private boolean myIsAggragating = false;

    private int myAggragateLength;

    private final byte[] myAggragation = new byte[MAX_AGGRAGATION];

    private int myAggragationCount = 0; // For event messages

    private Object myNotifications = null; // null, StreamMessage, or Vector

    // The following field is for message transformation. It is skeleton
    // support for future HTTP tunnelling.  It could also be used to
    // implement a dynamic table dependent compressiong scheme.
    private MsgTransformer myTransform = null;

    private boolean myIsStandardCBC = false;

    // The following fields are for MAC calculation
    // HASH_BLOCK_SIZE is true for MD5 and SHA1, check others
    static private final int HASH_BLOCK_SIZE = 64;

    private boolean myIsDoingMac = false;

    private boolean myIsDoingHMAC = false;

    private byte[] myMACKey;

    private MessageDigest mySHA1;

    private int myMacLen = 0;

    private byte[] mySequence;  // Sequence check

    private boolean myIsCompressingMsgLengths = false;

    /*    // The following fields are for compression
        private boolean myIsCompressing = false;
        private boolean myUseSmallZip;
    */
    // Spam only field for total comp/MAC/authentication time
    // only used or updated if we are event tracing
    private long myTotalTime = 0;

    /**
     * Make a new SendThread for an outgoing connection.
     *
     * @param remoteAddr     is the IP:port address to connect to.
     * @param path           is the DataPath object that receives notifications
     *                       as messages are sent and error occur.
     * @param reader         is the SynchQueue object to get messages from.
     * @param vat            is the Vat object to synchronize with before
     *                       calling methods in the connection DataPath object
     * @param addressesTried is a Hashtable of the InetAddresses already tried
     *                       to locate the vat which failed due to host, rather
     *                       than port related problems, and are not to be
     *                       tried again.
     */
    SendThread(String remoteAddr,
               DataPath path,
               SynchQueue reader,
               Vat vat,
               Hashtable addressesTried) {
        super("SendThread-" + remoteAddr);
        myRemoteAddr = remoteAddr;
        myDataPath = path;
        myReader = reader;
        myVat = vat;
        myAddressesTried = addressesTried;
        T.notNull(myVat, "Bad Vat");
        this.start();
    }

    /**
     * Make a new SendThread for an incoming socket.
     *
     * @param socket is the Socket for the new incoming connection.
     * @param path   is the DataPath object that receives notifications as
     *               messages are sent and error occur.
     * @param reader is the SynchQueue object to get messages from.
     * @param vat    is the Vat object to synchronize with before calling
     *               methods in the connection DataPath object
     */
    SendThread(Socket socket, DataPath path, SynchQueue reader, Vat vat) {
        super("SendThread-" + socket.getInetAddress().getHostAddress());
        mySocket = socket;
        myRemoteAddr = mySocket.getInetAddress().getHostAddress();
        myDataPath = path;
        myReader = reader;
        myVat = vat;
        T.notNull(myVat, "Bad Vat");
        this.start();
    }

    /**
     * Add an element to the aggrated message being constructed.
     *
     * @param elem is the element to be added.
     */
    private void addElement(Object elem) throws IOException {
        if (elem instanceof byte[]) {
            byte[] b = (byte[])elem;
            if (Trace.comm.verbose && Trace.ON) {
                Trace.comm.verbosem(
                  "to=" + myRemoteAddr + " addElement=" + b.length);
            }
            if (!myIsAggragating ||
              myAggragateLength + b.length + 4 > myAggragation.length) {
                flushElements();
                if (b.length + 4 > myAggragation.length) {
                    sendBytesWithLength(b);
                    return;
                }
            }
            int len = b.length;
            myAggragateLength = msgLength(len,
                                          myAggragation,
                                          myAggragateLength,
                                          myIsCompressingMsgLengths);
            System.arraycopy(b, 0, myAggragation, myAggragateLength, len);
            myAggragateLength += len;
            myAggragationCount++;
        } else if (elem instanceof StreamMessage) {
            StreamMessage sm = (StreamMessage)elem;
            byte[] b = sm.myMessage;
            if (Trace.comm.verbose && Trace.ON) {
                Trace.comm.verbosem(
                  "to=" + myRemoteAddr + " addElement=" + b.length);
            }
            if (!myIsAggragating ||
              myAggragateLength + b.length + 4 > myAggragation.length) {
                flushElements();
                if (b.length + 4 > myAggragation.length) {
                    sendBytesWithLength(b);
                    sm.myPlaceToRun.enqueue(sm.myNotification);
                    return;
                }
            }
            int len = b.length;
            myAggragateLength = msgLength(len,
                                          myAggragation,
                                          myAggragateLength,
                                          myIsCompressingMsgLengths);
            System.arraycopy(b, 0, myAggragation, myAggragateLength, len);
            myAggragateLength += len;
            myAggragationCount++;
            if (null == myNotifications) {
                myNotifications = sm;
            } else if (myNotifications instanceof StreamMessage) {
                Vector notifies = new Vector();
                notifies.addElement(myNotifications);
                notifies.addElement(sm);
                myNotifications = notifies;
            } else {
                Vector notifies = (Vector)myNotifications;
                notifies.addElement(sm);
            }
        } else {
            flushElements();    // Write previous stuff under old rules
            if (!(elem instanceof AuthSecrets)) {
                T.fail("" + elem +
                       (null == elem ? "" : (" type " + elem.getClass())) +
                       " unrecognized");
            }
            AuthSecrets c = (AuthSecrets)elem;
            if (Trace.comm.debug && Trace.ON) {
                Trace.comm.debugm("to=" + myRemoteAddr +
                                  " doing ProtocolChange, protocolSuite=" +
                                  c.myProtocolSuite);
            }
            if (StartUpProtocol.PROTO_NONE.equals(c.myProtocolSuite)) {
                myIsAggragating = false;
                myIsDoingMac = false;
                // begin daffE -> E
            } else if (StartUpProtocol.PROTO_3DES_SDH_M.equals(
              c.myProtocolSuite)) {
                myIsAggragating = true;
                myIsDoingMac = true;
                myMACKey = c.myMacKey;
                myMacLen = 20;
                byte[] raw_3des_key = TripleDESKeyConstructor.make(
                  c.myDHSecret);
                myTransform =
                  new Encrypt3DES(raw_3des_key, c.myOutgoingSequence, false);
                mySequence = null;
                setSHA1();
                // end daffE -> E
                // Begin improved E protocol
            } else if (StartUpProtocol.PROTO_3DES_SDH_M2.equals(
              c.myProtocolSuite)) {
                myIsAggragating = true;
                myIsDoingMac = true;
                myMACKey = c.myMacKey;
                myMacLen = 20;
                myIsDoingHMAC = true;
                byte[] raw_3des_key = TripleDESKeyConstructor.make(
                  c.myDHSecret);
                myTransform = new Encrypt3DES(raw_3des_key,
                                              c.myOutgoingSequence,
                                              true);
                myTransform.init();
                myIsStandardCBC = true;
                mySequence = new byte[4]; // Assume initialized to zero
                setSHA1();
                // end improved E protocol
            } else {
                throw new IOException(
                  "Invalid protocol suite type " + c.myProtocolSuite);
            }
            myIsCompressingMsgLengths = myIsAggragating;
        }
    }

    /**
     * Call a method in our DataPath
     *
     * @param thunk a Thunk that will perform the call. The thunk will be
     *              called after the Vat lock is obtained.
     */
    private void callDataPath(DataCommThunk thunk) {
        try {
            myVat.now(thunk);
        } catch (Throwable t) {
            Trace.comm.errorm("to=" + myRemoteAddr + " Error while calling " +
                              thunk, t);
            if (t instanceof VirtualMachineError) {
                throw (VirtualMachineError)t;
            }
            if (t instanceof ThreadDeath) {
                throw (ThreadDeath)t;
            }
            if (t instanceof LinkageError) {
                throw (LinkageError)t;
            }
        }
    }

    private byte[] computeMAC(byte[] b,
                              int off,
                              int len,
                              /*NilOK*/byte[] lenField) {
        if (Trace.comm.verbose && Trace.ON) {
            String bstr = HexStringUtils.bytesToReadableHexStr(b, off, len);
            Trace.comm.verbosem("to=" + myRemoteAddr +
                                " Calculating MAC on (length " + len + "):" +
                                bstr);
        }
        mySHA1.reset();                 //Initialize a new hash
        if (myIsDoingHMAC) {
            /* Calculate sha1(key ^ ipad || data) */
            byte[] pad = new byte[HASH_BLOCK_SIZE];
            replicate(pad, (byte)0x36);
            xor(pad, myMACKey);
            mySHA1.reset();
            mySHA1.update(pad);
        } else {
            mySHA1.update(myMACKey);            // The MAC key
        }
        if (null != mySequence) {
            mySHA1.update(mySequence);
            increment(mySequence);
        }
        if (null != lenField) {
            mySHA1.update(lenField);
        }
        mySHA1.update(b, off, len);
        if (myIsDoingHMAC) {
            byte[] hash = mySHA1.digest();

            /* Calculate sha1(key ^ opad || hash */
            byte[] pad = new byte[HASH_BLOCK_SIZE];
            replicate(pad, (byte)0x5c);
            xor(pad, myMACKey);
            mySHA1.update(pad);
            mySHA1.update(hash);
            return mySHA1.digest();
        } else {
            return mySHA1.digest(myMACKey);     // The MAC key again
        }
    }

    private void flushElements() throws IOException {
        if (Trace.comm.verbose && Trace.ON) {
            Trace.comm.verbosem("to=" + myRemoteAddr + " flushElements=" +
                                myAggragateLength);
        }
        if (0 != myAggragateLength) {
            sendBytes(myAggragation,
                      0,
                      myAggragateLength,
                      null,
                      myAggragateLength - (4 * myAggragationCount));
            myAggragateLength = 0;
            myAggragationCount = 0;
        }
        if (null == myNotifications) {
        } else if (myNotifications instanceof StreamMessage) {
            StreamMessage sm = (StreamMessage)myNotifications;
            sm.myPlaceToRun.enqueue(sm.myNotification);
        } else {
            Vector notifies = (Vector)myNotifications;
            for (int i = 0; i < notifies.size(); i++) {
                StreamMessage sm = (StreamMessage)notifies.elementAt(i);
                sm.myPlaceToRun.enqueue(sm.myNotification);
            }
        }
        myNotifications = null;
    }

    /**
     * Get the current Sequence for authentication
     */
    private byte[] /*NilOK*/ getSequence() {
        if (null != myTransform) {
            return myTransform.getSuspendInfo();
        } else {
            return mySequence;
        }
    }

    private void increment(byte[] value) {
        for (int i = value.length - 1; i >= 0; i--) {
            byte v = (value[i] += 1);
            if (0 != v) {
                break;
            }
        }
    }

    /**
     * xor - Exclusive OR two byte arrays.
     *
     * @param a is the input and output array.
     * @param b is the array which is XORed with a.
     */
    static private void xor(byte[] a, byte[] b) {
        int len = StrictMath.min(a.length, b.length);
        for (int i = 0; i < len; i++) {
            a[i] ^= b[i];
        }
    }


    /**
     * replicate - Copy a single byte to all elements of a byte array.
     *
     * @param a is the byte array.
     * @param v is the byte to be replicated.
     */
    static private void replicate(byte[] a, byte v) {
        for (int i = 0; i < a.length; i++) {
            a[i] = v;
        }
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

    /**
     * Calculate the message length field for a message.
     *
     * @param len        is the length of the message to have it's length
     *                   encoded.
     * @param buf        is the output buffer for the encoded length.
     * @param off        is the offset in the buffer for where to place the
     *                   encoded length.
     * @param compressed is true if compressed lengths are to be used.
     * @return is the offset of the byte after the end of the length.
     */
    static int msgLength(int len, byte[] buf, int off, boolean compressed)
      throws IOException {
        if (compressed) {
            if (len < 128) {
                buf[off++] = (byte)(len & 0x7f);
            } else if (len < 16384) {
                buf[off++] = (byte)(((len >> 8) & 0x3f) | 0x80);
                buf[off++] = (byte)(len & 0xff);
            } else if (len < 2097152) {
                buf[off++] = (byte)(((len >> 16) & 0x1f) | 0xc0);
                buf[off++] = (byte)((len >> 8) & 0xff);
                buf[off++] = (byte)(len & 0xff);
            } else {
                throw new IOException("Packet too large: " + len +
                                      " >= 2,097,152");
            }
        } else {
            buf[off++] = (byte)((len >> 24) & 0xff);
            buf[off++] = (byte)((len >> 16) & 0xff);
            buf[off++] = (byte)((len >> 8) & 0xff);
            buf[off++] = (byte)((len) & 0xff);
        }
        return off;
    }

    /**
     * Inform the DataPath about a problem
     */
    private void noticeProblem(Exception e) {
        callDataPath(new DataCommThunk(myDataPath, e));
    }

    /**
     * Body of the thread. Responsible for dequeueing messages and writing
     * them. Also for dequeueing parameter change orders (e.g. authentication
     * changes) and executing them in order. Calls DataPath.shutdown() before
     * exiting. Will exit if a null is dequeued.
     */
    public void run() {
        // First build the connection.

        try {
            NetAddr remoteNetAddr;
            if (null == mySocket) {
                if (Trace.comm.debug && Trace.ON) {
                    Trace.comm.debugm("Attempting outgoing connection to " +
                                      myRemoteAddr);
                }
                long startTime = 0;
                if (Trace.comm.timing && Trace.ON) {
                    startTime = MicroTime.queryTimer();
                }
                remoteNetAddr = new NetAddr(myRemoteAddr);
                //it's safe to not check optInetAddress() for null, since
                //remoteNetAddr must explicitly have one
                InetAddress remoteInetAddress = remoteNetAddr.optInetAddress();
                if (null != myAddressesTried.get(remoteInetAddress)) {
                    throw new NoRouteToHostException("Already failed once");
                }
                try {
                    mySocket = new Socket(remoteInetAddress,
                                          remoteNetAddr.getPort());
                    if (Trace.comm.timing && Trace.ON) {
                        Trace.comm.timingm("Socket build time=" +
                                           (MicroTime.queryTimer() -
                                            startTime) +
                                           " microseconds");
                    }
                } catch (NoRouteToHostException he) {
                    myAddressesTried.put(remoteInetAddress,
                                         remoteInetAddress);
                    throw he;
                } catch (BindException be) {
                    throw new NestedIOException(be, "BindException binding to " +
                                                    remoteNetAddr);
                }
            } else {
                remoteNetAddr = new NetAddr(mySocket.getInetAddress(),
                                            mySocket.getPort());
            }
            myLocalAddr = new NetAddr(mySocket.getLocalAddress(),
                                      mySocket.getLocalPort());

            if (Trace.comm.debug && Trace.ON) {
                Trace.comm.debugm("Acquired outgoing connection to " +
                                  myRemoteAddr + " from " + myLocalAddr);
            }
            try {
                mySocket.setTcpNoDelay(true);   // Set for send immediately
            } catch (SocketException e) {
                if (Trace.comm.event && Trace.ON) {
                    Trace.comm.eventm("Unable to setTcpNoDelay", e);
                }
            }
            myOutputStream = mySocket.getOutputStream();

            RecvThread receiver = new RecvThread(mySocket.getInputStream(),
                                                 myDataPath,
                                                 myRemoteAddr,
                                                 myVat);
            callDataPath(new DataCommThunk(myDataPath,
                                           receiver,
                                           remoteNetAddr,
                                           myLocalAddr));

        } catch (Exception e) {
            if (e instanceof IOException) {
                Trace.comm.eventm("IOException", e);
            } else {
                Trace.comm.errorm("Error", e);
            }
            noticeProblem(e);
            callDataPath(new DataCommThunk(myDataPath, getSequence(), e));
            return;
        }

// Now just send messages to the connection.
        Exception shutDownReason = null;
        Object chunk;
        try {
            if (Trace.comm.debug && Trace.ON) {
                Trace.comm.debugm("to=" + myRemoteAddr + " running...");
            }
            getchunks: while (true) {
                if (Trace.comm.verbose && Trace.ON) {
                    Trace.comm.verbosem("to=" + myRemoteAddr +
                                        " getting chunk");
                }
                chunk = myReader.dequeue(); // Block here
                if (!(chunk instanceof Number)) {
                    addElement(chunk);
                    while (myReader.hasMoreElements()) {
                        chunk = myReader.nextElement(); // Shouldn't block
                        if (DataPath.theShutDownToken == chunk) {
                            break getchunks;
                        }
                        addElement(chunk);
                    }
                    flushElements();
                    myOutputStream.flush();
                } else {
                    break;
                }
            }
            flushElements();    // Flush out last queued elements
            myOutputStream.flush();

            if (Trace.comm.debug && Trace.ON) {
                Trace.comm.debugm("to=" + myRemoteAddr +
                                  " I've been asked to shutdown");
            }
        } catch (Exception e) {
            if (Trace.comm.debug && Trace.ON) {
                Trace.comm.debugm(
                  "to=" + myRemoteAddr + " caught exception: ", e);
            }
            noticeProblem(e);
            shutDownReason = e;
        }
        try {
            mySocket.close();   // Close the socket
        } catch (IOException e) {
            Trace.comm.errorm("to=" + myRemoteAddr +
                              " Exception closing socket", e);
            if (null == shutDownReason) {
                shutDownReason = e;
            }
        }
        if (Trace.comm.debug && Trace.ON) {
            Trace.comm.debugm("to=" + myRemoteAddr + " terminated");
        }
        //Notify the DataPath that the connection has shut down
        if (null == shutDownReason) {
            shutDownReason =
              new ConnectionShutDownException("Normal Shutdown");
        }
        callDataPath(new DataCommThunk(myDataPath,
                                       getSequence(),
                                       shutDownReason));
        myDataPath = null;  // Help GC keep our secrets
        myTransform = null;
        myMACKey = null;
        mySHA1 = null;
    }

    /**
     * Perform compression, authentication, mac calculation, and send the
     * message
     *
     * @param b         The byte array to send
     * @param off       The offset in b to send from
     * @param len       The length to send
     * @param lenField  The compressed length field for the message
     * @param rawLength The length to use for comm statistics
     */
    private void sendBytes(byte[] b,
                           int off,
                           int len,
                           /*NilOK*/byte[] lenField,
                           int rawLength) throws IOException {

        if (Trace.comm.verbose && Trace.ON) {
            Trace.comm.verbosem("to=" + myRemoteAddr + " sendBytes=" + len);
        }
        if (!myIsCompressingMsgLengths) {
            int lineLen = 0;
            // If we're not compressing lengths, we're not doing mac or
            // authentication either
            if (null != lenField) {
                writeAndRecordProgress(lenField, 0, lenField.length);
                lineLen += lenField.length;
            }
            if (Trace.comm.event && Trace.ON) {
                Trace.comm.eventm("to=" + myRemoteAddr +
                                  " Sending message len=" + len);
            }
            writeAndRecordProgress(b, off, len);
            callDataPath(new DataCommThunk(myDataPath,
                                           myAggragationCount,
                                           len,
                                           null));
            return;
        }
        long authenticationTime = 0;
        long macTime = 0;
        long startTime = 0;

        if (Trace.comm.timing && Trace.ON) {
            startTime = MicroTime.queryTimer();
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(new byte[myMacLen + 4]); // Write space for length + MAC
        if (null != mySequence) {
            baos.write(mySequence);   // Write separate message sequence number
        }
        if (null != lenField) {
            baos.write(lenField);
        }
        baos.write(b, off, len);
        baos.close();
        // Get the length and the length of the sent length field
        int commLength = baos.size() - 24 - mySequence.length;

        int offset = -1;
        if (commLength < 128) {
            offset = 3;
        } else if (commLength < 16384) {
            offset = 2;
        } else if (commLength < 2097152) {
            offset = 1;
        } else {
            throw new IOException("Packet too large: " + commLength +
                                  " >= 2,097,152");
        }
        // If not a multiple of 64 bits (8 bytes), pad to 8 bytes
        if ((myIsCompressingMsgLengths || null != myTransform) &&
          0 != ((baos.size() - offset) & 7)) {
            byte[] fill = new byte[8 - ((baos.size() - offset) & 7)];
            baos.write(fill);     // Pad to multiple of 8 bytes
        }
        byte[] data = baos.toByteArray();

        if (myIsDoingMac) {
            if (Trace.comm.timing && Trace.ON) {
                startTime = MicroTime.queryTimer();
            }
            byte[] mac = computeMAC(b, off, len, lenField);
            if (Trace.comm.verbose && Trace.ON) {
                String mStr = HexStringUtils.bytesToReadableHexStr(mac);
                Trace.comm.verbosem("to=" + myRemoteAddr + " MAC is " + mStr);
            }
            System.arraycopy(mac, 0, data, 4, myMacLen);
            T.require(mac.length == myMacLen, "MAC length error");
            if (Trace.comm.timing && Trace.ON) {
                macTime += MicroTime.queryTimer() - startTime;
            }
        }

        switch (offset) {
        case 1:
            data[1] = (byte)(((commLength >> 16) & 0x1f) | 0xc0);
            data[2] = (byte)((commLength >> 8) & 0xff);
            data[3] = (byte)(commLength & 0xff);
            break;
        case 2:
            data[2] = (byte)(((commLength >> 8) & 0x3f) | 0x80);
            data[3] = (byte)(commLength & 0xff);
            break;
        case 3:
            data[3] = (byte)(commLength & 0x7f);
            break;
        default:
            T.fail("Case out of range, offset=" + offset);
        }

        if (null != myTransform) {
            if (Trace.comm.timing && Trace.ON) {
                startTime = MicroTime.queryTimer();
            }
            if (Trace.comm.verbose && Trace.ON) {
                String dStr = HexStringUtils.bytesToReadableHexStr(data, offset, data.length -
                                                                                 offset);
                Trace.comm.verbosem("to=" + myRemoteAddr + " authenticating " +
                                    (data.length - offset) + " bytes: " +
                                    dStr);
            }
            if (!myIsStandardCBC) {
                myTransform.init();
            }
            myTransform.transform(data, offset, data.length - offset);
            if (Trace.comm.timing && Trace.ON) {
                authenticationTime += MicroTime.queryTimer() - startTime;
            }
        }

        if (Trace.comm.timing && Trace.ON) {
            // Generate timing message
            int slen = data.length - offset;
            String msg = "to=" + myRemoteAddr + " ";
            msg += 0 != myAggragationCount ?
              "Sending aggragation of " + myAggragationCount :
              "Sending";
            msg += " len=" + slen;
            if (0 != authenticationTime) {
                msg += " AuthTime=" + authenticationTime;
            }
            if (0 != macTime) {
                msg += " MACTime=" + macTime;
            }
            myTotalTime += authenticationTime + macTime;
            if (0 != myTotalTime) {
                msg += " TotalTime=" + myTotalTime;
            }
            Trace.comm.timingm(msg);
        }

        writeAndRecordProgress(data, offset, data.length - offset);
        callDataPath(new DataCommThunk(myDataPath,
                                       myAggragationCount,
                                       data.length - offset,
                                       null));
        return;
    }

    /**
     * Send an aggrating mode message with only a single element in the
     * message.
     *
     * @param b is the byte array which is the data of the message
     */
    private void sendBytesWithLength(byte[] b) throws IOException {

        if (Trace.comm.verbose && Trace.ON) {
            Trace.comm.verbosem("to=" + myRemoteAddr +
                                " sendBytesWithLength=" + b.length);
        }
        byte[] len = new byte[4];
        int lenlen = msgLength(b.length, len, 0, myIsCompressingMsgLengths);
        byte[] clen = new byte[lenlen];
        System.arraycopy(len, 0, clen, 0, lenlen);

        sendBytes(b, 0, b.length, clen, b.length);
    }

    /**
     * Get an instance of the SHA1 message digest.
     */
    private void setSHA1() {
        try {
            mySHA1 = MessageDigest.getInstance("SHA");
        } catch (NoSuchAlgorithmException e) {
            Trace.comm.errorm("to=" + myRemoteAddr + " Unable to build SHA",
                              e);
            Trace.comm.notifyFatal();
        }
    }

    /**
     * Write a message in chunks and inform the datapath of progress in the
     * write.
     */
    private void writeAndRecordProgress(byte[] b, int off, int len)
      throws IOException {
        int offset = off;
        while (0 != len) {
            int thisLen = (len > NOTIFY_EVERY) ? NOTIFY_EVERY : len;
            len -= thisLen;
            myOutputStream.write(b, offset, thisLen);
            offset += thisLen;
            callDataPath(new DataCommThunk(myDataPath)); //progress notify
        }
    }
}

