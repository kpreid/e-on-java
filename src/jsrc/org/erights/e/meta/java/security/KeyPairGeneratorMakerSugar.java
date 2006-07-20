// Copyright 2004 Mark S. Miller, under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.meta.java.security;

import org.erights.e.develop.exception.ExceptionMgr;

import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.NoSuchAlgorithmException;
import java.security.KeyPair;

/**
 * @author Mark S. Miller
 */
public class KeyPairGeneratorMakerSugar {

    /** prevent instantiation */
    private KeyPairGeneratorMakerSugar() {
    }

    /**
     * algorithm defaults to RSA; and keysize defaults to 2048.
     *
     * @param random
     * @return
     */
    static public KeyPairGenerator run(SecureRandom random) {
        return run(random, "RSA", 2048);
    }

    /**
     * Makes an initialized KeyPairGenerator
     *
     * @param random
     * @param algorithm
     * @param keysize
     * @return
     */
    static public KeyPairGenerator run(SecureRandom random,
                                       String algorithm,
                                       int keysize) {
        KeyPairGenerator result;
        try {
            result = KeyPairGenerator.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw ExceptionMgr.asSafe(e);
        }
        result.initialize(keysize, random);
        return result;
    }

    /**
     * Equivalent to makeKeyPairGenerator(entropy).generateKeyPair()
     *
     * @param random
     * @return
     */
    static public KeyPair makeKeyPair(SecureRandom random) {
        return run(random).generateKeyPair();
    }
}
