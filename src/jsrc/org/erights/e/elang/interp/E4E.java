// Copyright 2003 Hewlett Packard, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.elang.interp;

import org.erights.e.elib.base.ClassDesc;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.ref.Ref;
import org.erights.e.elib.tables.Twine;

/**
 * This is like {@link E}, but for use from the E language rather than
 * ELib/Java.
 *
 * @author Mark S. Miller
 */
public final class E4E {

    static private final ClassDesc STRING_DESC = ClassDesc.make(String.class);
    static private final ClassDesc ARGS_DESC = ClassDesc.make(Object[].class);

    /**
     * prevent instantiation
     */
    private E4E() {
    }

    static public Object call(Object rec, String verb, Object[] args) {
        return E.callAll(rec, verb, args);
    }

    static public Object callWithPair(Object rec, Object[] pair) {
        return E.callAll(rec,
                         (String)STRING_DESC.coerce(pair[0], null),
                         (Object[])ARGS_DESC.coerce(pair[1], null));
    }

    static public Ref send(Object rec, String verb, Object[] args) {
        return E.sendAll(rec, verb, args);
    }

    static public Throwable sendOnly(Object rec, String verb, Object[] args) {
        return E.sendAllOnly(rec, verb, args);
    }

    static public Twine toQuote(Object obj) {
        return E.toQuote(obj);
    }

    static public String toString(Object obj) {
        return E.toString(obj);
    }
}
