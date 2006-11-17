package org.erights.e.elib.tables;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.develop.assertion.T;
import org.erights.e.elib.base.Thunk;
import org.erights.e.elib.oldeio.TextWriter;

import java.io.IOException;

/**
 * A WeakKeyMap is a FlexMap whose keys may be garbage collected.
 * <p/>
 * A WeakKeyMap acts as if an external entity (ie, the garbage collector)
 * shares access to the map and occasionally decides to remove some elements.
 * As a result, a WeakKeyMap which isn't being changed by the program may
 * nevertheless find itself shrinking over time. This external modifications
 * can only occur 1) in their own turn, or 2) at the beginning of any operation
 * that causes an enumeration.
 * <p/>
 * The snapshot(), readOnly(), clone(), or diverge() of a WeakKeyMap is not
 * weak.
 *
 * @author Mark S. Miller
 * @see java.util.WeakHashMap
 */
public class WeakKeyMap extends FlexMap {

    private final Class myKeyType;

    private final FlexMap myStuff;

    /**
     *
     */
    public WeakKeyMap(Class keyType, Class valueType) {
        myKeyType = keyType;
        myStuff = FlexMap.fromTypes(WeakKey.class, valueType);
    }

    /**
     *
     */
    public WeakKeyMap() {
        this(Object.class, Object.class);
    }

    /**
     * Does *not* remove garbage first, but affected when garbage is removed.
     */
    public int size() {
        return myStuff.size();
    }

    /**
     * Removes garbage first
     */
    public boolean contains(Object candidate) {
        //XXX Assumes 'keys' is held on to until the end of the method. Does
        //the Java Language Spec allow it to be optimized away?  What about
        //actual implementations?
        Object keys = getKeys(myKeyType);
        return super.contains(candidate);
    }

    /**
     * Removes garbage first
     */
    public Object getKeys(Class type) {
        WeakKey[] weakKeys = (WeakKey[])myStuff.getKeys(WeakKey.class);
        int len = weakKeys.length;
        FlexList strongKeys = FlexList.fromType(type, len);
        for (int i = 0; i < len; i++) {
            Object optKey = weakKeys[i].getOptActual();
            if (null == optKey) {
                weakKeys[i].run();
            } else {
                strongKeys.push(optKey);
            }
        }
        return strongKeys.getArray(type);
    }

    /**
     * Does *not* remove garbage first, which is dangerous. <p>
     * <p/>
     * When used in concert with getKeys, be sure to call getKeys first and
     * hold on to the result when calling getValues, so that none of the keys
     * will be become garbage between the two operations. <p>
     * <p/>
     * Or, better yet, just call getPair(), which is safe on all kinds of
     * maps.
     */
    public Object getValues(Class type) {
        return myStuff.getValues(type);
    }

    /**
     * Does not remove garbage first
     */
    public Class keyType() {
        return myKeyType;
    }

    /**
     * Does not remove garbage first
     */
    public Class valueType() {
        return myStuff.valueType();
    }

    /**
     * Does not remove garbage first, but cannot retrieve garbage since the key
     * cannot be supplied.
     */
    public Object fetch(Object key, Thunk insteadThunk) {
        WeakKey weakKey = new WeakKey(key, myStuff);
        return myStuff.fetch(weakKey, insteadThunk);
    }

    /**
     * Does not remove garbage first. <p>
     * <p/>
     * WeakKeyMap doesn't support strict=true.
     */
    public void put(Object key, Object value, boolean strict) {
        if (strict) {
            T.fail("WeakKeyMap doesn't support strict");
        }
        WeakKey weakKey = new WeakKey(key, myStuff);
        myStuff.put(weakKey, value);
    }

    /**
     * Does not remove garbage first. <p>
     * <p/>
     * WeakKeyMap doesn't support strict=true.
     */
    public void removeKey(Object key, boolean strict) {
        if (strict) {
            T.fail("WeakKeyMap doesn't support strict");
        }
        WeakKey weakKey = new WeakKey(key, myStuff);
        myStuff.removeKey(weakKey);
    }

    /**
     * Does not remove garbage first
     */
    public void removeAll() {
        myStuff.removeAll();
    }

    /**
     * Does not remove garbage first, which is weird, but the other would be
     * weirder.
     */
    public void __printOn(TextWriter out) throws IOException {
        out.print("WeakKeyMap(", myStuff, ")");
    }
}
