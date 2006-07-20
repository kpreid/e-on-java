// Copyright 2004 Mark S. Miller, under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.elib.sealing.crypto;

import org.erights.e.develop.exception.ExceptionMgr;

import java.security.Signature;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.PrivateKey;
import java.security.SignatureException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;

/**
 * The class java.security.Signature is used in one of two disjoint ways, so
 * rather than tame it, we split its tamed functionality into Signer and
 * {@link Verifier}.
 *
 * @author Mark S. Miller
 */
public class Signer {

    private final Signature mySig;

    /**
     * algorithm defaults to SHA1withRSA, which only works if this is an RSA
     * key.
     *
     * @param privateKey
     */
    public Signer(PrivateKey privateKey) {
        this(privateKey, "SHA1withRSA");
    }

    /**
     * The algorithm must be compatible with the algorithm of the key.
     * <p>
     * For example, if the key's algorithm is RSA, the signing algorithm may
     * be SHA1withRSA.
     *
     * @param privateKey
     * @param algorithm

     */
    public Signer(PrivateKey privateKey, String algorithm) {
        try {
            mySig = Signature.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw ExceptionMgr.asSafe(e);
        }
        try {
            mySig.initSign(privateKey);
        } catch (InvalidKeyException e) {
            throw ExceptionMgr.asSafe(e);
        }
    }

    /**
     * Equivalent to makeSigner(privateKey).sign(plainText)
     *
     * @param privateKey
     * @param plainText
     * @return
     */
    static public BigInteger sign(PrivateKey privateKey, String plainText) {
        return new Signer(privateKey).sign(plainText);
    }

    /**
     * Returns the result of signing the plainText's UTF-8 encoding
     *
     * @param plainText
     * @return
     */
    public BigInteger sign(String plainText) {
        byte[] bytes = new byte[0];
        try {
            bytes = plainText.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw ExceptionMgr.asSafe(e);
        }
        try {
            mySig.update(bytes);
        } catch (SignatureException e) {
            throw ExceptionMgr.asSafe(e);
        }
        try {
            return new BigInteger(1, mySig.sign());
        } catch (SignatureException e) {
            throw ExceptionMgr.asSafe(e);
        }
    }

    /**
     *
     * @return
     */
    public String getAlgorithm() {
        return mySig.getAlgorithm();
    }
}
