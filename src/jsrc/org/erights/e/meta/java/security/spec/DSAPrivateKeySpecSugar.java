// Copyright 2003 Hewlett Packard, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.meta.java.security.spec;

import org.erights.e.elib.prim.StaticMaker;

import java.security.spec.DSAPrivateKeySpec;


/**
 * XXX Should be made virtually Selfless and implement getSpreadUncall instead.
 *
 * @author Mark S. Miller
 */
public class DSAPrivateKeySpecSugar {

    static private final StaticMaker makeDSAPrivateKeySpec =
      StaticMaker.make(DSAPrivateKeySpec.class);

    /**
     * prevent instantation
     */
    private DSAPrivateKeySpecSugar() {
    }

    /**
     * Uses 'makeDSAPrivateKeySpec(X, P, Q, G)'
     */
    static public Object[] __optUncall(DSAPrivateKeySpec self) {
        Object[] args = {self.getX(), self.getP(), self.getQ(), self.getG()};
        Object[] result = {makeDSAPrivateKeySpec, "run", args};
        return result;
    }
}
