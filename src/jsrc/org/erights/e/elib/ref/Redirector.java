package org.erights.e.elib.ref;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.elib.prim.E;
import org.erights.e.elib.serial.PassByProxy;
import org.erights.e.elib.util.DeadManSwitch;
import org.erights.e.elib.util.OneArgFunc;

/**
 * Wraps a Resolver in a way suitable for inclusion as an argument in a
 * __whenMoreResolved message.
 * <p/>
 * Turns the first response such an argument may get into a resolution of the
 * Resolver, and then become inoperative.
 *
 * @author Mark S. Miller
 */
class Redirector implements DeadManSwitch, OneArgFunc, PassByProxy {

    /**
     *
     */
    private Resolver myOptResolver;

    /**
     *
     */
    Redirector(Resolver resolver) {
        myOptResolver = resolver;
    }

    /**
     * Resolve the Resolver to the arg.
     */
    public Object run(Object arg) {
        if (null != myOptResolver) {
            myOptResolver.resolve(arg);
            myOptResolver = null;
        }
        return null;
    }

    /**
     * Smash the Resolver with the arg.
     */
    public void __reactToLostClient(Object problem) {
        if (null != myOptResolver) {
            myOptResolver.smash(E.asRTE(problem));
            myOptResolver = null;
        }
    }
}
