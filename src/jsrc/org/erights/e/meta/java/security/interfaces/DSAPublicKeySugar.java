// Copyright 2003 Hewlett Packard, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.meta.java.security.interfaces;

import org.erights.e.develop.exception.ExceptionMgr;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.DSAPublicKey;
import java.security.spec.DSAPublicKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

/**
 * XXX Should be made virtually Selfless and implement getSpreadUncall instead.
 *
 * @author Mark S. Miller
 */
public class DSAPublicKeySugar {

    /**
     * prevent instantation
     */
    private DSAPublicKeySugar() {
    }

    /**
     * Uses 'keyFactory.generatePublic(publicKeySpec)'
     */
    static public Object[] __optUncall(DSAPublicKey self) {
        KeyFactory kf;
        try {
            kf = KeyFactory.getInstance("DSA");
        } catch (NoSuchAlgorithmException e) {
            throw ExceptionMgr.asSafe(e);
        }
        KeySpec spec;
        try {
            spec = kf.getKeySpec(self, DSAPublicKeySpec.class);
        } catch (InvalidKeySpecException e) {
            throw ExceptionMgr.asSafe(e);
        }
        Object[] args = {spec};
        Object[] result = {kf, "generatePublic", args};
        return result;
    }
}
