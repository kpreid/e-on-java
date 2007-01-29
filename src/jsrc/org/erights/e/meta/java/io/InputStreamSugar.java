package org.erights.e.meta.java.io;

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

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * A sweetener defining extra messages that may be e-sent to an InputStream.
 *
 * @author Mark S. Miller
 */
public class InputStreamSugar {

    /**
     * prevent instantiation
     */
    private InputStreamSugar() {
    }

    /**
     *
     */
    static private final int BUF_SIZE = 8192;

    /**
     * A SHA hash of the rest of the stream.
     */
    static public BigInteger getCryptoHash(InputStream self)
      throws NoSuchAlgorithmException, IOException {
        MessageDigest sha = MessageDigest.getInstance("SHA");
        byte[] buf = new byte[BUF_SIZE];
        try {
            int len;
            while (0 <= (len = self.read(buf))) {
                sha.update(buf, 0, len);
            }
        } finally {
            self.close();
        }
        return new BigInteger(1, sha.digest());
    }

    /**
     * Reads the currently available bytes (presumably without blocking, since
     * they are said to be available).
     */
    static public byte[] readAvailable(InputStream self) throws IOException {
        // Despite
        // https://bugs.sieve.net/bugs/?func=detailbug&bug_id=125641&group_id=16380
        // this use of MAX_VALUE is safe, because we know we're talking about
        // Java ints.
        return readAvailable(self, Integer.MAX_VALUE);
    }

    /**
     * Reads the currently available bytes (presumably without blocking, since
     * they are said to be available), but no more that max.
     */
    static public byte[] readAvailable(InputStream self, int max)
      throws IOException {
        int size = Math.min(self.available(), max);
        byte[] result = new byte[size];
        if (0 == size) {
            return result;
        }
        int len = self.read(result);
        if (len == size) {
            return result;
        }
        byte[] section = new byte[len];
        System.arraycopy(result, 0, section, 0, len);
        return section;
    }
}
