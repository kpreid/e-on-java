package org.erights.e.elib.util;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.develop.assertion.T;
import org.erights.e.elib.serial.PassByProxy;
import org.erights.e.elib.serial.Persistent;

/**
 * An encapsulating forwarder around a OneArgFunc that only passes the first
 * call through. <p>
 * <p/>
 * Since the wrapped OneArgFunc may be Persistent, we should be Persistent.
 * Since we are encapsulating, we cannot be PassByConstruction, but should be
 * PassByProxy.
 *
 * @author Mark S. Miller
 */
public class Once implements OneArgFunc, PassByProxy, Persistent {

    static private final long serialVersionUID = -2577631688094868919L;

    private OneArgFunc myOptWrapped;

    /**
     * Makes an object that will only forward one run/1 message to wrapped.
     */
    public Once(OneArgFunc wrapped) {
        myOptWrapped = wrapped;
    }

    /**
     *
     */
    public Object run(Object arg) {
        if (null == myOptWrapped) {
            T.fail("The Once is used up");
        }
        OneArgFunc optWrapped = myOptWrapped;
        myOptWrapped = null;
        return optWrapped.run(arg);
    }
}
