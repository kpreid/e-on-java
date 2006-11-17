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
import org.erights.e.develop.trace.Trace;
import org.erights.e.meta.java.math.BigIntegerSugar;
import org.erights.e.meta.java.security.PublicKeySugar;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;


/**
 * Useful functions for manipulating the public key pair representing a vat's
 * identity.
 *
 * @author Bill Frantz
 * @author Mark S. Miller
 */
public class VatIdentity {

    /**
     * VatID to use for connecting to unknown vats at a specific IP address
     */
    static final String WHOEVER = "0";

    /**
     * prevent instantiation
     */
    private VatIdentity() {
    }

    /**
     * Make a new VatIdentity object. Constructing this object will create a
     * new keypair which will define a new identity.
     */
    static public KeyPair generateKeyPair(ESecureRandom entropy) {

        KeyPairGenerator gen = null;
        try {
            gen = KeyPairGenerator.getInstance("DSA");
        } catch (NoSuchAlgorithmException e) {
            Trace.comm.errorm("Can't load routines for DSA", e);
            Trace.comm.notifyFatal();

        }

        gen.initialize(1024, entropy);
        return gen.generateKeyPair();
    }

    /**
     * Calculate the vatID from a public key.
     * <p/>
     * The vatID is the SHA1 hash of the public key expressed in YURL32.
     *
     * @param key is the public key to hash
     * @return the SHA1 hash of the public key in YURL32.
     */
    static public String calculateVatID(PublicKey key) {

        if (Trace.comm.event && Trace.ON) {
            Trace.comm.eventm("calculateVatID for " + key);
        }

        BigInteger hash = PublicKeySugar.getFingerprint(key);
        String vid = BigIntegerSugar.toYURL32(hash);

        if (Trace.comm.debug && Trace.ON) {
            Trace.comm
              .debugm("calculated vat Id " + vid + " from PublicKey " + key);
        }
        return vid;
    }
}
