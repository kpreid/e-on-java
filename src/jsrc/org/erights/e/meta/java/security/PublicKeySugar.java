// Copyright 2004 Mark S. Miller, under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.meta.java.security;

import org.erights.e.meta.java.math.BigIntegerSugar;

import java.math.BigInteger;
import java.security.PublicKey;

/**
 * @author Mark S. Miller
 */
public class PublicKeySugar {

    /**
     * prevents instantiation
     */
    private PublicKeySugar() {
    }

    /**
     * @param self
     * @return
     */
    static public BigInteger getFingerprint(PublicKey self) {
        BigInteger encoded = new BigInteger(1, self.getEncoded());
        return BigIntegerSugar.cryptoHash(encoded);
    }
}
