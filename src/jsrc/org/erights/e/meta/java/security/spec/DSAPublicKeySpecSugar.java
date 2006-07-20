// Copyright 2003 Hewlett Packard, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.meta.java.security.spec;

import org.erights.e.elib.prim.StaticMaker;

import java.security.spec.DSAPublicKeySpec;


/**
 * XXX Should be made virtually Selfless and implement getSpreadUncall instead.
 *
 * @author Mark S. Miller
 */
public class DSAPublicKeySpecSugar {

    static private final StaticMaker makeDSAPublicKeySpec =
      StaticMaker.make(DSAPublicKeySpec.class);

    /**
     * prevent instantation
     */
    private DSAPublicKeySpecSugar() {
    }

    /**
     * Uses 'makeDSAPublicKeySpec(Y, P, Q, G)'
     */
    static public Object[] __optUncall(DSAPublicKeySpec self) {
        Object[] args = {self.getY(), self.getP(), self.getQ(), self.getG()};
        Object[] result = {makeDSAPublicKeySpec, "run", args};
        return result;
    }
}
