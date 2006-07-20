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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility method for constructing a 3DES key from a Diffie-Hellman secret.
 *
 * @author This file contains only code created by Tyler Close, not a citizen
 *         or resident of the US.
 */
final class TripleDESKeyConstructor {

    static private final String DIGEST_ALGORITHM_NAME = "MD5";

    static private final int DES_KEY_LENGTH = 8;

    static private final int TRIPLE_DES_KEY_LENGTH = 3 * DES_KEY_LENGTH;

    static private final int KEY_PAD_LENGTH = 16;

    /**
     * Constructs a 3DES key from a Diffie-Hellman secret.
     * <p/>
     * The individual keys are calculated from a Diffie-Hellman secret using
     * MD5 and a 16 byte pad consisting of a single byte repeated 16 times as
     * follows:
     * <p/>
     * 3DESKey1 is the first 8 bytes of md5((16,pad)(n,dhSecret)) with 0x55 for
     * the pad. 3DESKey2 is the second 8 bytes of md5((16,pad)(n,dhSecret))
     * with 0x55 for the pad. 3DESKey3 is the first 8 bytes of
     * md5((16,pad)(n,dhSecret)) with 0xaa for the pad.
     *
     * @param dhSecret the Diffie-Hellman secret
     * @return a byte[] containing the constructed key.
     * @see cryptix.provider.key.RawKey
     * @see http://www.erights.org/doc/to-be-sorted/DataComm_startup.html
     */
    static byte[] make(final byte[] dhSecret) {
        try {
            final byte[] raw_key = new byte[TRIPLE_DES_KEY_LENGTH];
            final byte[] buffer = new byte[KEY_PAD_LENGTH];
            final MessageDigest md = MessageDigest.getInstance(
              DIGEST_ALGORITHM_NAME);
            fill(buffer, (byte)0x55);
            md.update(buffer);
            byte[] key12 = md.digest(dhSecret);
            System.arraycopy(key12, 0, raw_key, 0, 2 * DES_KEY_LENGTH);
            fill(buffer, (byte)0xAA);
            md.update(buffer);
            System.arraycopy(md.digest(dhSecret),
                             0,
                             raw_key,
                             2 * DES_KEY_LENGTH,
                             DES_KEY_LENGTH);
            return raw_key;
        } catch (NoSuchAlgorithmException e) {
            Trace.comm.errorm("Unable to build " + DIGEST_ALGORITHM_NAME, e);
            Trace.comm.notifyFatal();
        }
        return null;
    }

    static private void fill(final byte[] buffer, final byte value) {
        for (int i = buffer.length; i-- != 0;) {
            buffer[i] = value;
        }
    }
}
