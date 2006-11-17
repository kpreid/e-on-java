package org.erights.e.elib.tables;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.develop.assertion.T;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.vat.WeakPtr;

/**
 * Used to implement a key in a WeakValueMap implementation by being a value in
 * a FlexMap. <p>
 * <p/>
 * Holds on to the actual value weakly. Is an HONORARY Selfless object, so it
 * compares based on its equals() and hashCode() methods. A WeakValue is also a
 * Runnable, since it stores itself as the reactor to be informed when its
 * value is collected. <p>
 * <p/>
 * WeakValue's implementation of equals and hashCode does not satisfy the
 * contract specified for HONORARY Selfless objects, but that's ok since the
 * WeakValue class is only used in the implementation of WeakValueMap.
 *
 * @author Mark S. Miller
 */
class WeakValue implements Runnable {

    /**
     * So I can remove the association that maps to me.
     */
    private final Object myKey;

    /**
     * weakly holds the actual value
     */
    private final WeakPtr myWeakPtr;

    /**
     * the map I'm in, that I need to remove myself from
     */
    private FlexMap myOptMap;

    /**
     *
     */
    WeakValue(Object key, Object value, FlexMap map) {
        if (null == value) {
            T.fail("Can't use null as a weak value");
        }
        myKey = key;
        myWeakPtr = new WeakPtr(value, this);
        myOptMap = map;
    }

    /**
     * Invoked sometime after the actual value has been gced.
     */
    public void run() {
        if (null != myOptMap) {
            myOptMap.removeKey(myKey);
            myOptMap = null;
        }
    }

    /**
     * If the actual value is still around, return it; else null.
     */
    Object getOptActual() {
        return myWeakPtr.get();
    }

    /**
     *
     */
    public String toString() {
        Object optActual = myWeakPtr.get();
        if (null == optActual) {
            return "--gced--";
        } else {
            return E.toString(optActual);
        }
    }
}
