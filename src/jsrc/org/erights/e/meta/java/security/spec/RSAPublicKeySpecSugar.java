// Copyright 2003 Hewlett Packard, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.meta.java.security.spec;

import org.erights.e.elib.prim.StaticMaker;

import java.security.spec.RSAPublicKeySpec;


/**
 * XXX Should be made virtually Selfless and implement getSpreadUncall
 * instead.
 *
 * @author Mark S. Miller
 */
public class RSAPublicKeySpecSugar {

    static private final StaticMaker makeRSAPublicKeySpec =
      StaticMaker.make(RSAPublicKeySpec.class);

    /**
     * prevent instantation
     */
    private RSAPublicKeySpecSugar() {
    }

    /**
     * Uses 'makeRSAPublicKeySpec(modulus, pubExponent)'
     */
    static public Object[] __optUncall(RSAPublicKeySpec self) {
        Object[] args = {self.getModulus(), self.getPublicExponent()};
        Object[] result = {makeRSAPublicKeySpec, "run", args};
        return result;
    }
}
