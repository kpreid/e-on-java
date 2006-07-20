package org.erights.e.elib.tables;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.develop.assertion.T;
import org.erights.e.elib.base.Thunk;
import org.erights.e.elib.base.ValueThunk;
import org.erights.e.elib.oldeio.TextWriter;

import java.io.IOException;

/**
 * A WeakValueMap is FlexMap whose values may be garbage collected.
 * <p/>
 * A WeakValueMap acts as if an external entity (ie, the garbage collector)
 * shares access to the map and occasionally decides to remove some
 * elements. As a result, a WeakValueMap which isn't being changed by the
 * program may nevertheless find itself shrinking over time. This external
 * modifications can only occur 1) in their own turn, or 2) at the beginning
 * of any operation that causes an enumeration.
 * <p/>
 * The snapshot(), readOnly(), clone(), or diverge() of a WeakValueMap is not
 * weak.
 * <p/>
 * BUG: For some unfathomable reason, the actual values never seem to get
 * collected, even though WeakValue by itself, when tested, seems to work
 * fine.
 *
 * @author Mark S. Miller
 * @see java.util.WeakHashMap
 */
public class WeakValueMap extends FlexMap {

    private final Class myValueType;

    private final FlexMap myStuff;

    /**
     *
     */
    public WeakValueMap(Class keyType, Class valueType) {
        myValueType = valueType;
        myStuff = FlexMap.fromTypes(keyType, WeakValue.class);
    }

    /**
     *
     */
    public WeakValueMap() {
        this(Object.class, Object.class);
    }

    /**
     * Does *not* remove garbage first, but affected when garbage is removed.
     */
    public int size() {
        return myStuff.size();
    }

    /**
     * Does *not* remove garbage first, which is dangerous. <p>
     * <p/>
     * When used in concert with getValues, be sure to call getValues first and
     * hold on to the result when calling getKeys, so that none of the values
     * will be become garbage between the two operations. <p>
     * <p/>
     * Or, better yet, just call getPair(), which is safe on all kinds of
     * maps.
     */
    public Object getKeys(Class type) {
        return myStuff.getKeys(type);
    }

    /**
     * Removes garbage first
     */
    public Object getValues(Class type) {
        WeakValue[] weakValues =
          (WeakValue[])myStuff.getValues(WeakValue.class);
        int len = weakValues.length;
        FlexList strongValues = FlexList.fromType(type, len);
        for (int i = 0; i < len; i++) {
            Object optValue = weakValues[i].getOptActual();
            if (null == optValue) {
                weakValues[i].run();
            } else {
                strongValues.push(optValue);
            }
        }
        return strongValues.getArray(type);
    }

    /**
     * Does not remove garbage first
     */
    public Class keyType() {
        return myStuff.keyType();
    }

    /**
     * Does not remove garbage first
     */
    public Class valueType() {
        return myValueType;
    }

    /**
     * Does not remove garbage first, except for the value itself if it has
     * been collected.
     */
    public Object fetch(Object key, Thunk insteadThunk) {
        WeakValue optWeakVal =
          (WeakValue)myStuff.fetch(key, ValueThunk.NULL_THUNK);
        if (null == optWeakVal) {
            return insteadThunk.run();
        }
        Object optValue = optWeakVal.getOptActual();
        if (null == optValue) {
            optWeakVal.run();
            return insteadThunk.run();
        }
        return optValue;
    }

    /**
     * Does not remove garbage first. <p>
     * <p/>
     * WeakValueMap doesn't support strict=true.
     */
    public void put(Object key, Object value, boolean strict) {
        if (strict) {
            T.fail("WeakValueMap doesn't support strict");
        }
        WeakValue weakValue = new WeakValue(key, value, myStuff);
        myStuff.put(key, weakValue);
    }

    /**
     * Does not remove garbage first. <p>
     * <p/>
     * WeakValueMap doesn't support strict=true.
     */
    public void removeKey(Object key, boolean strict) {
        if (strict) {
            T.fail("WeakValueMap doesn't support strict");
        }
        myStuff.removeKey(key);
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
        out.print("WeakValueMap(", myStuff, ")");
    }
}
