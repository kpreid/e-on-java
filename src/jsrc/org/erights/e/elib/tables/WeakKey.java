package org.erights.e.elib.tables;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.develop.assertion.T;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.vat.WeakPtr;

/**
 * Used to implement a key in a WeakKeyMap implementation by being a key in a
 * FlexMap. <p>
 * <p/>
 * Holds on to the actual key weakly. Is an HONORARY Selfless object, so it
 * compares based on its equals() and hashCode() methods. A WeakKey is also a
 * Runnable, since it stores itself as the reactor to be informed when its key
 * is collected. <p>
 * <p/>
 * WeakKey's implementation of equals and hashCode does not satisfy the
 * contract specified for HONORARY Selfless objects, but that's ok since the
 * WeakKey class is only used in the implementation of WeakKeyMap. It compares
 * the actual keys using identity, which makes sense only for NEAR Selfish
 * objects.
 *
 * @author Mark S. Miller
 */
class WeakKey implements Runnable {

    /**
     * since the actual key may disappear
     */
    private final int myHashCode;

    /**
     * weakly holds the actual key
     */
    private final WeakPtr myWeakPtr;

    /**
     * the map I'm in, that I need to remove myself from
     */
    private FlexMap myOptMap;

    /**
     * The key should be a NEAR Selfish object, but this is not currently
     * enforced.
     */
    WeakKey(Object key, FlexMap map) {
        if (null == key) {
            T.fail("Can't use null as a weak key");
        }
        myHashCode = System.identityHashCode(key);
        myWeakPtr = new WeakPtr(key, this);
        myOptMap = map;
    }

    /**
     *
     */
    public int hashCode() {
        return myHashCode;
    }

    /**
     *
     */
    public boolean equals(Object other) {
        if (this == other) {
            //it's reflexive so it can be removed after the actual key
            //has gone away.
            return true;
        }
        if (other instanceof WeakKey) {
            Object key = myWeakPtr.get();
            if (null == key) {
                //non-monotonic!  Two WeakKeys on the same object may be true
                //one day, but then false later when the object goes away.
                return false;
            }
            Object okey = ((WeakKey)other).myWeakPtr.get();
            return key == okey;
        }
        return false;
    }

    /**
     * Invoked sometime after the actual key has been gced.
     */
    public void run() {
        if (null != myOptMap) {
            myOptMap.removeKey(this);
            myOptMap = null;
        }
    }

    /**
     * If the actual key is still around, return it; else null.
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
