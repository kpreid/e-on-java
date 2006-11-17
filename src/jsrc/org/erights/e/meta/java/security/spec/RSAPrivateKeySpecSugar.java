// Copyright 2003 Hewlett Packard, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.meta.java.security.spec;

import org.erights.e.elib.prim.StaticMaker;

import java.security.spec.RSAPrivateKeySpec;


/**
 * XXX Should be made virtually Selfless and implement getSpreadUncall
 * instead.
 *
 * @author Mark S. Miller
 */
public class RSAPrivateKeySpecSugar {

    static private final StaticMaker makeRSAPrivateKeySpec =
      StaticMaker.make(RSAPrivateKeySpec.class);

    /**
     * prevent instantation
     */
    private RSAPrivateKeySpecSugar() {
    }

    /**
     * Uses 'makeRSAPrivateKeySpec(modulus, privExponent)'
     */
    static public Object[] __optUncall(RSAPrivateKeySpec self) {
        Object[] args = {self.getModulus(), self.getPrivateExponent()};
        Object[] result = {makeRSAPrivateKeySpec, "run", args};
        return result;
    }
}
