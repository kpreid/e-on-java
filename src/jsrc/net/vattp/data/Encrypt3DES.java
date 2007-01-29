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
package net.vattp.data;

//import cryptix.provider.cipher.DES;

import net.vattp.security.MicroTime;
import org.erights.e.develop.assertion.T;
import org.erights.e.develop.exception.NestedException;
import org.erights.e.develop.trace.Trace;

import java.security.InvalidKeyException;

/**
 * Implementation of triple DES-EDE encryption in Cypher Block Chaining mode.
 *
 * @author Bill Frantz
 */
class Encrypt3DES extends MsgTransformer {

    private final byte[] myIV;

    private final byte[] myDESKeys;

    private final boolean myIsStandardCBC;

    private final byte[] myPreviousBlock = new byte[8];

    private final byte[] myPad = new byte[8];

    private DES myDes1, myDes2, myDes3;


    Encrypt3DES(byte[] desKeys, byte[] iv, boolean isStandardCBC) {
        myDESKeys = desKeys;
        myIV = iv;
        myIsStandardCBC = isStandardCBC;
        try {
            byte[] key = new byte[8];
            System.arraycopy(myDESKeys, 0, key, 0, 8);
            myDes1 = new DES();
            myDes1.initEncrypt(key);
            System.arraycopy(myDESKeys, 8, key, 0, 8);
            myDes2 = new DES();
            myDes2.initDecrypt(key);
            System.arraycopy(myDESKeys, 16, key, 0, 8);
            myDes3 = new DES();
            myDes3.initEncrypt(key);
        } catch (InvalidKeyException ike) {
            Trace.comm.errorm("Problem initializing DES keys", ike);
            throw new NestedException(ike, "# Problem initializing DES keys");
        }
        System.arraycopy(myIV, 0, myPreviousBlock, 0, 8);
    }


    void init() {
    }  // NOP


    byte[] getSuspendInfo() {
        if (Trace.comm.debug && Trace.ON) {
            Trace.comm.debugm("Returning IV=" + eightToHex(myIV, 0));
        }
        if (myIsStandardCBC) {
            return myPreviousBlock;
        } else {
            return myIV;
        }
    }


    void transform(byte[] buffer, int off, int len) {
        long startTime = Trace.comm.timing ? MicroTime.queryTimer() : 0;
        T.require(0 == (len & 7),
                  "Length must be a multiple of 8, len=" + len);
        if (!myIsStandardCBC) {
            System.arraycopy(myIV, 0, myPreviousBlock, 0, 8);
            increment(myIV);
        }
        for (int cursor = off; cursor < off + len; cursor += 8) {
            if (Trace.comm.verbose && Trace.ON) {
                Trace.comm.verbosem("Plaintext=" + eightToHex(buffer, cursor));
            }
            xor(buffer, cursor, myPreviousBlock);   // Do CBC mode
            //Encrypt myCurrentBlock with 3DES Encrypt(Decrypt(Encrypt()))
            myDes1.update(buffer, cursor, 8, buffer, cursor);
            myDes2.update(buffer, cursor, 8, buffer, cursor);
            myDes3.update(buffer, cursor, 8, buffer, cursor);
            System.arraycopy(buffer, cursor, myPreviousBlock, 0, 8);
        }
        if (Trace.comm.timing && Trace.ON) {
            Trace.comm
              .timingm("Pkt3DESEncrypt(" + buffer.length + "), time " +
                (MicroTime.queryTimer() - startTime) + " microseconds");
        }
    }


    private void xor(byte[] inOut, int offset, byte[] in) {
        for (int i = 0; 8 > i; i++) {
            inOut[offset + i] ^= in[i];
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

    static private String eightToHex(byte[] b, int offset) {
        String ret = "";
        for (int i = offset; i < offset + 8; i++) {
            ret += "0123456789abcdef".charAt((b[i] >> 4) & 0xf);
            ret += "0123456789abcdef".charAt(b[i] & 0xf);
        }
        return ret;
    }
}

