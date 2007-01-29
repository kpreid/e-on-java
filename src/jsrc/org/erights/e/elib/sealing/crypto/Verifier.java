// Copyright 2004 Mark S. Miller, under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.elib.sealing.crypto;

import org.erights.e.develop.exception.ExceptionMgr;
import org.erights.e.meta.java.math.BigIntegerSugar;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;

/**
 * The class java.security.Signature is used in one of two disjoint ways, so
 * rather than tame it, we split its tamed functionality into {@link Signer}
 * and Verifier.
 *
 * @author Mark S. Miller
 */
public class Verifier {

    private final Signature mySig;

    /**
     * algorithm defaults to SHA1withRSA, which only works if this is an RSA
     * key.
     */
    public Verifier(PublicKey publicKey) {
        this(publicKey, "SHA1withRSA");
    }

    /**
     * The algorithm must be compatible with the algorithm of the key, and be
     * the same as the algorithm used to generate the signatures we're
     * checking.
     * <p/>
     * For example, if the key's algorithm is RSA, the signing algorithm may be
     * SHA1withRSA.
     */
    public Verifier(PublicKey publicKey, String algorithm) {
        try {
            mySig = Signature.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw ExceptionMgr.asSafe(e);
        }
        try {
            mySig.initVerify(publicKey);
        } catch (InvalidKeyException e) {
            throw ExceptionMgr.asSafe(e);
        }
    }

    /**
     * Equivalent to makeVerifier(publicKey).verify(plainText, signature)
     */
    static public boolean verify(PublicKey publicKey,
                                 String plainText,
                                 BigInteger signature) {
        return new Verifier(publicKey).verify(plainText, signature);
    }

    /**
     * Is this a valid signature of the plainText's UTF-8 encoding?
     */
    public boolean verify(String plainText, BigInteger signature) {
        byte[] bytes;
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
            return mySig.verify(BigIntegerSugar.toBase2ByteArray(signature));
        } catch (SignatureException e) {
            throw ExceptionMgr.asSafe(e);
        }
    }

    /**
     *
     */
    public String getAlgorithm() {
        return mySig.getAlgorithm();
    }
}
