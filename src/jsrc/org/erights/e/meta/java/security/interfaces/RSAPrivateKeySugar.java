// Copyright 2003 Hewlett Packard, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.meta.java.security.interfaces;

import org.erights.e.develop.exception.ExceptionMgr;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.RSAPrivateKeySpec;

/**
 * XXX Should be made virtually Selfless and implement getSpreadUncall
 * instead.
 *
 * @author Mark S. Miller
 */
public class RSAPrivateKeySugar {

    /**
     * prevent instantation
     */
    private RSAPrivateKeySugar() {
    }

    /**
     * Uses 'keyFactory.generatePrivate(privateKeySpec)'
     */
    static public Object[] __optUncall(RSAPrivateKey self) {
        KeyFactory kf;
        try {
            kf = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            throw ExceptionMgr.asSafe(e);
        }
        KeySpec spec;
        try {
            spec = kf.getKeySpec(self, RSAPrivateKeySpec.class);
        } catch (InvalidKeySpecException e) {
            throw ExceptionMgr.asSafe(e);
        }
        Object[] args = {spec};
        Object[] result = {kf, "generatePrivate", args};
        return result;
    }
}
