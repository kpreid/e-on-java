// Copyright 2003 Hewlett Packard, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.meta.java.security;

import org.erights.e.elib.prim.StaticMaker;

import java.security.KeyFactory;


/**
 * XXX Should be made virtually Selfless and implement getSpreadUncall instead.
 *
 * @author Mark S. Miller
 */
public class KeyFactorySugar {

    static private final StaticMaker makeKeyFactory =
      StaticMaker.make(KeyFactory.class);

    /**
     * prevent instantation
     */
    private KeyFactorySugar() {
    }

    /**
     * Uses 'makeKeyFactory.getInstance(algorithString)'
     */
    static public Object[] __optUncall(KeyFactory self) {
        Object[] args = {self.getAlgorithm()};
        Object[] result = {makeKeyFactory, "getInstance", args};
        return result;
    }
}
