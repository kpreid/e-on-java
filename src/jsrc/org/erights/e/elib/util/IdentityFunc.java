package org.erights.e.elib.util;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

/**
 * Just returns its argument
 *
 * @author Mark S. Miller
 */
public class IdentityFunc implements OneArgFunc {

    /**
     * The canonical instance
     */
    static public final IdentityFunc THE_ONE = new IdentityFunc();

    /**
     *
     */
    private IdentityFunc() {
    }

    /**
     * Returns arg
     */
    public Object run(Object arg) {
        return arg;
    }
}
