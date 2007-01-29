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
import org.erights.e.develop.trace.Trace;
import org.erights.e.elib.util.HexStringUtils;
import org.erights.e.elib.vat.Vat;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.Vector;

/**
 * A thread to read bytes from an TCP InputStream and pass them to a
 * VatTPConnection.
 * <p/>
 * Created by SendThread when a Socket becomes available. This will be either
 * after an outgoing connection succeeds, or after an incoming connection has
 * been detected in ListenThread.<p>
 *
 * @author Bill Frantz
 */
class RecvThread extends Thread {

    // Index: 2=transforming, 1=isDoingMac
    static private final int HEADER_INVALID = -1;

    static private final int HEADER_INT_LENGTH = 0;

    static private final int HEADER_VLEN_SHA1 = 1;

    static private final int HEADER_VLEN_HMAC = 2;

    // Note that the header lengths must be a multiple of the blocksize
    // of the encryption used (8 for 3DES). AES implementation take warning.
    //                                             0   1   2
    static private final int[] theHeaderLengths = {4, 24, 32};

    /**
     * Notify DataPath every NOTIFY_EVERY bytes of receive progress
     */
    static private final int NOTIFY_EVERY = 10000;

    private final InputStream myInputStream;

    private DataPath myDataPath;

    private boolean myTerminateFlag;

    private int myBufferLength;

    /**
     * The vat we will synchronize with when calling the DataPath
     */
    private final Vat myVat;

    private byte[] myHeader = new byte[4];  // Protocol negotation header

    private boolean myChangeProtocolIsOk = false;

    // The following fields are for aggragating messages
    private boolean myIsAggragating = false;

    private final Vector myMessagesToPass = new Vector();

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

    private byte[] myMAC;

    private byte[] mySequence;  // Sequence check

    // The following fields are for compression
    private boolean myIsCompressingMsgLengths = false;

    // Spam for total timing
    // only used if tracing
    private long myTotalTime = 0;

    /**
     * Construct a new RecvThread.
     *
     * @param inputStream  stream to read data from.
     * @param receiver     a FragileRootHolder to an OutputStream to write data
     *                     to.
     * @param connection   a RawConnection to send problem reports to.
     * @param bufferLength number of bytes to read at one time. A new buffer of
     *                     this length is allocated for each read, so it
     *                     shouldn't be too big.
     */
    RecvThread(InputStream inputStream,
                      DataPath connection,
                      String remoteAddr,
                      Vat vat) {
        super("RecvThread-" + remoteAddr);
        myInputStream = inputStream;
        myDataPath = connection;
        myTerminateFlag = false;
        myVat = vat;
    }

    /**
     * Call a method in our VatTPMgr
     *
     * @param thunk a Thunk that will perform the call. The thunk will be
     *              called after the Vat lock is obtained.
     */
    private void callDataPath(DataCommThunk thunk) {
        try {
            myVat.now(thunk);
        } catch (Throwable t) {
            Trace.comm.errorm("Error while calling " + thunk, t);
            if (t instanceof VirtualMachineError) {
                throw (VirtualMachineError)t;
            }
            if (t instanceof ThreadDeath) {
                throw (ThreadDeath)t;
            }
            if (t instanceof LinkageError) {
                throw (LinkageError)t;
            }
            //XXX if it's not one of these, shouldn't we at least trace it,
            //and perhaps rethrow it?
        }
    }

    /**
     * Change the authorization protocol being used to receive messages on the
     * connection. This routine must be called when the RecvThread is
     * synchronized with the Vat thread. This assertion is checked by setting
     * myChangeProtocolIsOk just before calling into the Vat with a new
     * incoming message and resetting it when the call returns. This routine
     * will throw an Assertion failure if that boolean is not true.
     *
     * @param protocolParms is the parameter bundle for the protocol suite to
     *                      use. This routine must, of course, support the
     *                      selected suite.
     */
    void changeProtocol(AuthSecrets protocolParms) {
        if (Trace.comm.verbose && Trace.ON) {
            Trace.comm.verbosem("ProtocolChange, pp=" + protocolParms);
        }
        T.require(myChangeProtocolIsOk,
                  "Must only be called while caller " +
                    "is processing an input message");
        // For calculating the header length
        int headerLengthIndex = HEADER_INVALID;

        if (StartUpProtocol.PROTO_NONE.equals(protocolParms.myProtocolSuite)) {
            myIsAggragating = false;
            myIsDoingMac = false;
            mySequence = null;
            headerLengthIndex = HEADER_INT_LENGTH;
            // begin daffE -> E
        } else if (StartUpProtocol.PROTO_3DES_SDH_M
          .equals(protocolParms.myProtocolSuite)) {
            myIsAggragating = true;
            MessageDigest md5 = setSHA1(protocolParms);
            headerLengthIndex = HEADER_VLEN_SHA1;
            mySequence = null;
            byte[] raw_3des_key =
              TripleDESKeyConstructor.make(protocolParms.myDHSecret);
            myTransform =
              new Decrypt3DES(raw_3des_key, protocolParms.myIncomingSequence);
            // end daffE -> E
            // begin improved E protocol
        } else if (StartUpProtocol.PROTO_3DES_SDH_M2
          .equals(protocolParms.myProtocolSuite)) {
            myIsAggragating = true;
            MessageDigest md5 = setSHA1(protocolParms);
            headerLengthIndex = HEADER_VLEN_HMAC;
            mySequence = new byte[4];  // Assume initialized to zero
            byte[] raw_3des_key =
              TripleDESKeyConstructor.make(protocolParms.myDHSecret);
            myTransform =
              new Decrypt3DES(raw_3des_key, protocolParms.myIncomingSequence);
            myTransform.init();
            myIsStandardCBC = true;
            myIsDoingHMAC = true;
            // end improved E protocol
        } else {
            T.fail("Invalid protocol type " + protocolParms);
        }
        myIsCompressingMsgLengths = myIsAggragating;

        int len = theHeaderLengths[headerLengthIndex];
        T.require(0 <= len, "Invalid header length code");
        myHeader = new byte[len];
    }

    private byte[] computeMAC(Vector messages) throws IOException {
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
        Enumeration itr = messages.elements();
        while (itr.hasMoreElements()) {
            byte[] b = (byte[])(itr.nextElement());
            int len = b.length;
            byte[] l = new byte[4];
            int lenlen =
              SendThread.msgLength(len, l, 0, myIsCompressingMsgLengths);
            mySHA1.update(l, 0, lenlen);
            mySHA1.update(b);                   // The message
        }
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

    private void fillArray(byte[] b, int off, int len) throws IOException {
        int offset = off;
        while (offset < off + len) {
            int thisLen = off + len - offset;
            thisLen = (NOTIFY_EVERY < thisLen) ? NOTIFY_EVERY : thisLen;
            int l = myInputStream.read(b, offset, thisLen);
            if (0 > l) {
                throw new EOFException();
            }
            offset += l;
            callDataPath(new DataCommThunk(myDataPath)); //progress notify
        }
    }

    /**
     * Get the current message sequence number
     */
    byte[] /*NilOK*/ getSequence() {
        if (null != myTransform) {
            return myTransform.getSuspendInfo();
        } else {
            return mySequence;
        }
    }

    private void increment(byte[] value) {
        for (int i = value.length - 1; 0 <= i; i--) {
            byte v = (value[i] += 1);
            if (0 != v) {
                break;
            }
        }
    }


    /**
     * isEqual - Compare two byte arrays.
     *
     * @param a is the first input array.
     * @param b is the second input array.
     * @return is true if the two arrays contain all the same bytes, else
     *         false.
     */
    static private boolean isEqual(byte[] a, byte[] b) {
        int len = a.length;
        if (len != b.length) {
            return false;
        }
        for (int i = 0; i < len; i++) {
            if (a[i] != b[i]) {
                return false;
            }
        }
        return true;
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
     * Process input data.
     */
    private void readAndProcessMessage() throws IOException {
        long authTime = 0;
        long macTime = 0;
        long startTime = 0;

        // Read the header
        fillArray(myHeader, 0, myHeader.length);

        if (null != myTransform) {
            if (Trace.comm.timing && Trace.ON) {
                startTime = MicroTime.queryTimer();
            }
            if (!myIsStandardCBC) {
                myTransform.init();
            }
            myTransform.transform(myHeader);
            if (Trace.comm.timing && Trace.ON) {
                authTime += MicroTime.queryTimer() - startTime;
            }
        }
        if (Trace.comm.verbose && Trace.ON) {
            String hdr = HexStringUtils.bytesToReadableHexStr(myHeader);
            Trace.comm.verbosem("Header: " + hdr);
        }

        int length;
        int nextItemOffset;    // Offset to the next item in the header
        if (myIsCompressingMsgLengths) {
            int l1 = myHeader[0];    // First byte of the length
            if (0 == (l1 & 0x80)) {
                // len < 128
                length = l1;
                nextItemOffset = 1;
            } else {
                if (0x80 == (l1 & 0xc0)) {
                    // len < 16,384
                    length = ((l1 & 0x3f) << 8) | ((myHeader[1] & 0xff));
                    nextItemOffset = 2;
                } else if (0xc0 == (l1 & 0xe0)) {
                    // len < 2,097,152
                    length = ((l1 & 0x1f) << 16) |
                      ((myHeader[1] & 0xff) << 8) | ((myHeader[2] & 0xff));
                    nextItemOffset = 3;
                } else if (0xe0 == (l1 & 0xf0)) {
                    // len < 2**28
                    length = ((l1 & 0x0f) << 24) |
                      ((myHeader[1] & 0xff) << 16) |
                      ((myHeader[2] & 0xff) << 8) | ((myHeader[3] & 0xff));
                    nextItemOffset = 4;
                } else {
                    throw new IOException("Invalid compressed length code" +
                      HexStringUtils.bytesToReadableHexStr(myHeader));
                }
            }
        } else {
            // Not compressing
            length = ((myHeader[0] & 0xff) << 24) |
              ((myHeader[1] & 0xff) << 16) | ((myHeader[2] & 0xff) << 8) |
              (myHeader[3] & 0xff);
            nextItemOffset = 4;
        }
        if (Trace.comm.verbose && Trace.ON) {
            Trace.comm.verbosem("incoming packet len = " + length);
        }
        if (Msg.MAX_INBOUND_MSG_LENGTH < length || 0 > length) {
            throw new IOException("Packet too large: " + length + " > " + Msg
              .MAX_INBOUND_MSG_LENGTH);
        }
        if (myIsDoingMac) {
            System.arraycopy(myHeader,
                             nextItemOffset,
                             myMAC,
                             0,
                             20); // Save MAC
            nextItemOffset += 20;
        }
        if (null != mySequence) {
            int seqLen = mySequence.length;
            byte[] theirSequence = new byte[seqLen];
            System.arraycopy(myHeader,
                             nextItemOffset,
                             theirSequence,
                             0,
                             seqLen);
            if (!isEqual(mySequence, theirSequence)) {
                if (Trace.comm.error) {
                    traceErrorMsg(theirSequence,
                                  0,
                                  theirSequence.length,
                                  "sequence error, [remote sequence number]:");
                    traceErrorMsg(mySequence,
                                  0,
                                  mySequence.length,
                                  "sequence error, [local sequence number]:");
                }
                throw new IOException("incoming packet sequence error");
            }
            nextItemOffset += seqLen;
        }
        // Calculate message length + padding and allocate a buffer
        // The length required is the length of the data portion plus the
        // padding length for the original message.
        // The padding length for the original message is calculated:
        //    msglen = (length_of_length + length_of_headers + length_of_data
        //    padlen = ((msglen + 7) & 0xfffffff8) - msglen
        // length_of_length + length_of_headers is equal to nextItemOffset
        //    (the amount of data eaten by processing them above).
        // length_of_data is length.
        byte[] message;
        if (myIsCompressingMsgLengths || null != myTransform) {
            int msglen = nextItemOffset + length;
            int padlen = ((msglen + 7) & 0xfffffff8) - msglen;
            message = new byte[length + padlen];
        } else {
            message = new byte[length];
        }
        if (nextItemOffset < myHeader.length) {
            // Copy compressed data read with header
            System.arraycopy(myHeader,
                             nextItemOffset,
                             message,
                             0,
                             myHeader.length - nextItemOffset);
        }
        if (Trace.comm.verbose && Trace.ON) {
            String msg = HexStringUtils.bytesToReadableHexStr(message);
            Trace.comm.verbosem("Initial message: " + msg);
        }

        // Read rest of message into the buffer allocated for it
        fillArray(message,
                  myHeader.length - nextItemOffset,
                  message.length - (myHeader.length - nextItemOffset));

        if (null != myTransform) {
            if (Trace.comm.timing && Trace.ON) {
                startTime = MicroTime.queryTimer();
            }
            myTransform.transform(message,
                                  (myHeader.length - nextItemOffset),
                                  message.length -
                                    (myHeader.length - nextItemOffset));
            if (Trace.comm.timing && Trace.ON) {
                authTime += MicroTime.queryTimer() - startTime;
            }
        }

        if (Trace.comm.verbose && Trace.ON) {
            String msg = HexStringUtils.bytesToReadableHexStr(message);
            Trace.comm.verbosem("Full message: " + msg);
        }

        myMessagesToPass.removeAllElements();
        if (myIsAggragating) {
            if (Trace.comm.timing && Trace.ON) {
                startTime = MicroTime.queryTimer();
            }
            ByteArrayInputStream bais = new ByteArrayInputStream(message);
            while (true) {
                length = 0;
                int input = bais.read();
                if (0 >= input) {
                    break;  // -1 for eof on bais, 0 if only msg shorter than 3
                }
                int count;
                if (myIsCompressingMsgLengths) {
                    if (0 == (input & 0x80)) {
                        length = input;
                        count = 0;
                    } else if (0x80 == (input & 0xc0)) {
                        // len < 16,384
                        length = input & 0x3f;
                        count = 1;
                    } else if (0xc0 == (input & 0xe0)) {
                        // len < 2,097,152
                        length = input & 0x1f;
                        count = 2;
                    } else if (0xe0 == (input & 0xf0)) {
                        // len < 2**28
                        length = input & 0x0f;
                        count = 3;
                    } else {
                        String msg =
                          HexStringUtils.bytesToReadableHexStr(message);
                        throw new IOException(
                          "Invalid compressed length code" + msg);
                    }
                } else {
                    count = 3;
                }
                for (int i = 0; i < count; i++) {
                    length <<= 8;
                    input = bais.read();
                    length |= input & 0xff;
                }
                if (Msg.MAX_INBOUND_MSG_LENGTH < length || 0 > length) {
                    throw new IOException("Message too large: " + length +
                      " > " + Msg.MAX_INBOUND_MSG_LENGTH);
                }

                byte[] emsg = new byte[length];
                int offset = 0;
                while (offset < emsg.length) {
                    int read = bais.read(emsg, offset, emsg.length - offset);
                    if (-1 == read) {
                        break;
                    }
                    offset += read;
                }
                if (offset != length) {
                    throw new IOException(
                      "incoming packet not aggragated properly," +
                        " expectedLength=" + length + " foundLength=" +
                        offset +
                        HexStringUtils.bytesToReadableHexStr(message));
                }
                myMessagesToPass.addElement(emsg);
            }
        } else if (message.length != length) {
            byte[] a = new byte[length];
            System.arraycopy(message, 0, a, 0, length);
            myMessagesToPass.addElement(a);
        } else {
            myMessagesToPass.addElement(message);
        }

        if (myIsDoingMac) {
            if (Trace.comm.timing && Trace.ON) {
                startTime = MicroTime.queryTimer();
            }
            byte[] digest = computeMAC(myMessagesToPass);
            if (!MessageDigest.isEqual(digest, myMAC)) {
                if (Trace.comm.error) {
                    traceErrorMsg(myMAC,
                                  0,
                                  myMAC.length,
                                  "checksum mismatch, [remote checksum]:");
                    traceErrorMsg(digest,
                                  0,
                                  digest.length,
                                  "checksum mismatch, [local checksum]:");
                    Enumeration itr = myMessagesToPass.elements();
                    while (itr.hasMoreElements()) {
                        byte[] msg = (byte[])(itr.nextElement());
                        int len = msg.length;
                        byte[] m = new byte[len + 4];
                        m[0] = (byte)((len >> 24) & 0xff);// The message length
                        m[1] = (byte)((len >> 16) & 0xff);
                        m[2] = (byte)((len >> 8) & 0xff);
                        m[3] = (byte)((len) & 0xff);
                        System.arraycopy(msg, 0, m, 4, len);
                        traceErrorMsg(m,
                                      0,
                                      m.length,
                                      "checksum mismatch, [data         ]:");
                    }
                }
                throw new IOException("incoming packet checksum mismatch");
            }
            if (Trace.comm.timing && Trace.ON) {
                macTime += MicroTime.queryTimer() - startTime;
            }
        }
        // The input data counts are maintained in the DataPath
        //myDataPath.updateReceivedCounts(msgLengths, compressedLength);

        Enumeration itr;
        if (Trace.comm.timing && Trace.ON) {
            // Generate event record message
            itr = myMessagesToPass.elements();
            String logMsg = "Processed message length(s)";
            while (itr.hasMoreElements()) {
                byte[] msg = (byte[])(itr.nextElement());
                logMsg += " " + msg.length;
            }
            if (0 != authTime) {
                logMsg += " AuthTime=" + authTime;
            }
            if (0 != macTime) {
                logMsg += " MACTime=" + macTime;
            }
            myTotalTime += authTime + macTime;
            if (0 != myTotalTime) {
                logMsg += " TotalTime=" + myTotalTime;
            }
            Trace.comm.timingm(logMsg);
        }

        itr = myMessagesToPass.elements();
        while (itr.hasMoreElements()) {
            byte[] msg = (byte[])(itr.nextElement());
            if (Trace.comm.debug && Trace.ON) {
                traceDebugMsg(msg, 0, msg.length, "incoming packet data");
            }
            myChangeProtocolIsOk = true;

            //XXX should make embargo work again
            if (Msg.E_MSG == msg[0]) {
                myDataPath.getEmbargoLock().waitTillOff();
            }

            callDataPath(new DataCommThunk(myDataPath, msg));
            myChangeProtocolIsOk = false;
        }
        myMessagesToPass.removeAllElements(); // Clean up for garbage collecter
    }

    /**
     * The actual connection receive thread -- the asynchronous part.
     */
    public void run() {
        /* At this level, all errors are considered unrecoverable. We catch
           them all and pitch them to our connection. */
        try {
            /* Loop reading blobs until somebody tells us to stop or we get
               blown away by an error. */
            if (Trace.comm.debug) {
                Trace.comm.debugm("running...");
            }
            while (!myTerminateFlag) {
                readAndProcessMessage();
            }
            if (Trace.comm.debug) {
                Trace.comm.debugm("I've been asked to shutdown");
            }
        } catch (Throwable t) {
            if (Trace.comm.debug) {
                Trace.comm.debugm("caught exception", t);
            }
            /* If the send thread exits, it will close the socket, which
               in turn will cause us to catch an IOException here. But that
               should not be treated as an error, since it's the normal way we
               get unblocked during a shutdown. So don't bother telling the
               keeper if we catch an exception and the terminate flag is set.
               Suicide is not an error (here). */
            if (!myTerminateFlag) {
                if (t instanceof IOException) {
                    Trace.comm.usagem("IOException", t);
                } else {
                    Trace.comm.errorm("Error", t);
                }
                callDataPath(new DataCommThunk(myDataPath, t));
            }
            if (t instanceof VirtualMachineError) {
                throw (VirtualMachineError)t;
            }
            if (t instanceof ThreadDeath) {
                throw (ThreadDeath)t;
            }
            if (t instanceof LinkageError) {
                throw (LinkageError)t;
            }
        } finally {
            if (Trace.comm.debug) {
                Trace.comm.debugm("terminated");
            }
            myDataPath = null;
        }
    }

    private MessageDigest setSHA1(AuthSecrets macParms) {
        try {
            mySHA1 = MessageDigest.getInstance("SHA");
        } catch (NoSuchAlgorithmException e) {
            Trace.comm.errorm(" Unable to build SHA", e);
            Trace.comm.notifyFatal();
        }
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            Trace.comm.errorm("Unable to build MD5", e);
            Trace.comm.notifyFatal();
        }
        // Calculate the MAC key
        byte[] macKey = new byte[64];
        System.arraycopy(md5Hash(0x11, macParms.myDHSecret, md5),
                         0,
                         macKey,
                         0,
                         16);
        System.arraycopy(md5Hash(0x22, macParms.myDHSecret, md5),
                         0,
                         macKey,
                         16,
                         16);
        System.arraycopy(md5Hash(0x33, macParms.myDHSecret, md5),
                         0,
                         macKey,
                         32,
                         16);
        System.arraycopy(md5Hash(0x44, macParms.myDHSecret, md5),
                         0,
                         macKey,
                         48,
                         16);
        myMACKey = macParms.myMacKey = macKey;

        //Set up to do the MAC calculation
        myIsDoingMac = true;
        myMAC = new byte[20];
        return md5;
    }

    /**
     * Shutdown this thread. The thread doesn't actually exit until the read
     * unblocks.
     */
    void shutdown() {
        myTerminateFlag = true;
    }

    private void traceDebugMsg(byte[] msg, int off, int len, String note) {
        String msgString = HexStringUtils.bytesToReadableHexStr(msg, off, len);
        Trace.comm.debugm(note + " (length " + len + ") " + msgString);
    }

    private void traceErrorMsg(byte[] msg, int off, int len, String note) {
        String msgString = HexStringUtils.bytesToReadableHexStr(msg, off, len);
        Trace.comm.errorm(note + " (length " + len + ") " + msgString);
    }
}
