// Copyright 2003 Hewlett Packard, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.meta.java.security;

import org.erights.e.elib.prim.StaticMaker;

import java.security.KeyPair;


/**
 * XXX Should be made virtually Selfless and implement getSpreadUncall instead.
 *
 * @author Mark S. Miller
 */
public class KeyPairSugar {

    static private final StaticMaker makeKeyPair =
      StaticMaker.make(KeyPair.class);

    /**
     * prevent instantation
     */
    private KeyPairSugar() {
    }

    /**
     * Uses 'makeKeyPair(pubKey, privKey)'
     */
    static public Object[] __optUncall(KeyPair self) {
        Object[] args = {self.getPublic(), self.getPrivate()};
        Object[] result = {makeKeyPair, "run", args};
        return result;
    }
}
