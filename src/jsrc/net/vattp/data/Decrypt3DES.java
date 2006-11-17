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
 * Implementation of triple DES-EDE decryption in Cypher Block Chaining mode.
 *
 * @author Bill Frantz
 */
class Decrypt3DES extends MsgTransformer {

    private final byte[] myIV;

    private final byte[] myDESKeys;

    private final byte[] myPreviousBlock = new byte[8];

    private byte[] myCurrentBlock = null;

    private DES myDes1, myDes2, myDes3;

    private final byte[] myPad = new byte[8];


    Decrypt3DES(byte[] desKeys, byte[] iv) {
        myDESKeys = desKeys;
        myIV = iv;
        System.arraycopy(myIV, 0, myPreviousBlock, 0, 8);
        try {
            byte[] key = new byte[8];
            System.arraycopy(myDESKeys, 0, key, 0, 8);
            myDes1 = new DES();
            myDes1.initDecrypt(key);
            System.arraycopy(myDESKeys, 8, key, 0, 8);
            myDes2 = new DES();
            myDes2.initEncrypt(key);
            System.arraycopy(myDESKeys, 16, key, 0, 8);
            myDes3 = new DES();
            myDes3.initDecrypt(key);
        } catch (InvalidKeyException ike) {
            Trace.comm.errorm("Problem initializing DES keys", ike);
            throw new NestedException(ike, "# Problem initializing DES keys");
        }
    }


    byte[] getSuspendInfo() {
        byte[] val = new byte[8];
        System.arraycopy(myIV, 0, val, 0, 8);
        increment(val);
        return val;
    }


    void init() {
        if (null != myCurrentBlock) {
            increment(myIV);
        } else {
            myCurrentBlock = new byte[8];
        }
        System.arraycopy(myIV, 0, myPreviousBlock, 0, 8);
    }


    void transform(byte[] buffer, int off, int len) {

        long startTime = Trace.comm.timing ? MicroTime.queryTimer() : 0;
        T.require(0 == (len & 0x7),
                  "Buffer length not a multiple of 8, len = " + len);

        for (int cursor = off; cursor < off + len; cursor += 8) {
            //Save current block's cyphertext for CBC mode
            System.arraycopy(buffer, cursor, myCurrentBlock, 0, 8);
            //Decrypt a block - 3DES Decrypt(Encrypt(Decrypt()))
            myDes3.update(buffer, cursor, 8, buffer, cursor);
            myDes2.update(buffer, cursor, 8, buffer, cursor);
            myDes1.update(buffer, cursor, 8, buffer, cursor);
            xor8(buffer, cursor, myPreviousBlock);  // Do CBC mode
            System.arraycopy(myCurrentBlock, 0, myPreviousBlock, 0, 8);
        }
        if (Trace.comm.timing && Trace.ON) {
            Trace.comm
              .timingm("Pkt3DESDecrypt(" + buffer.length + "), time " +
                (MicroTime.queryTimer() - startTime) + " microseconds");
        }
    }

    private void xor8(byte[] inOut, int off, byte[] in) {
        for (int i = 0; i < 8; i++) {
            inOut[off + i] ^= in[i];
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

    static private String eightToHex(byte[] b, int offset) {
        String ret = "";
        for (int i = offset; i < offset + 8; i++) {
            ret += "0123456789abcdef".charAt((b[i] >> 4) & 0xf);
            ret += "0123456789abcdef".charAt(b[i] & 0xf);
        }
        return ret;
    }
}

