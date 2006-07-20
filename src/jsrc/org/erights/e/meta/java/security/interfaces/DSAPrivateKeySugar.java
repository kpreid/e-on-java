// Copyright 2003 Hewlett Packard, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.meta.java.security.interfaces;

import org.erights.e.develop.exception.ExceptionMgr;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.DSAPrivateKey;
import java.security.spec.DSAPrivateKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

/**
 * XXX Should be made virtually Selfless and implement getSpreadUncall instead.
 *
 * @author Mark S. Miller
 */
public class DSAPrivateKeySugar {

    /**
     * prevent instantation
     */
    private DSAPrivateKeySugar() {
    }

    /**
     * Uses 'keyFactory.generatePrivate(privateKeySpec)'
     */
    static public Object[] __optUncall(DSAPrivateKey self) {
        KeyFactory kf;
        try {
            kf = KeyFactory.getInstance("DSA");
        } catch (NoSuchAlgorithmException e) {
            throw ExceptionMgr.asSafe(e);
        }
        KeySpec spec;
        try {
            spec = kf.getKeySpec(self, DSAPrivateKeySpec.class);
        } catch (InvalidKeySpecException e) {
            throw ExceptionMgr.asSafe(e);
        }
        Object[] args = {spec};
        Object[] result = {kf, "generatePrivate", args};
        return result;
    }
}
