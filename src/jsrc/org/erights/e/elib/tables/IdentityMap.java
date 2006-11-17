package org.erights.e.elib.tables;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.develop.exception.ExceptionMgr;
import org.erights.e.elib.base.Thunk;

/**
 * Like a FlexMap, but based on identity, and not as fancy.
 * <p/>
 * Not for general purpose use. This uses the underlying primitive pointer
 * identity (Lisp EQ or Java ==) rather that E's sameness. Since this former
 * depends on the implementation in ways not deterministically derivable from
 * the E spec, this class is <a href="http://www.erights.org/elib/legacy/taming.html">unsafe</a>.
 *
 * @author Mark S. Miller
 */
public class IdentityMap implements EIteratable {

    /**
     *
     */
    private IdentityKeyColumn myKeys;

    /**
     *
     */
    private Column myValues;

    /**
     * The current size threshold for the map, that is, the number of elements
     * to hold before growing. It is calculated as capacity * myLoadFactor.
     */
    private int mySizeThreshold;

    /**
     * The load factor for the map.
     */
    private float myLoadFactor;

    /**
     *
     */
    private ShareCount myShareCount;

    /**
     * Reasonable defaults
     */
    public IdentityMap() {
        this(Object.class,
             Object.class,
             FlexMapImpl.DEFAULT_INIT_CAPACITY,
             FlexMapImpl.DEFAULT_LOAD_FACTOR);
    }

    /**
     * Reasonable defaults
     */
    public IdentityMap(int initialCapacity) {
        this(Object.class,
             Object.class,
             initialCapacity,
             FlexMapImpl.DEFAULT_LOAD_FACTOR);
    }

    /**
     * Reasonable defaults
     */
    public IdentityMap(int initialCapacity, float loadFactor) {
        this(Object.class, Object.class, initialCapacity, loadFactor);
    }

    /**
     * Reasonable defaults
     */
    public IdentityMap(Class keyType, Class valueType) {
        this(keyType,
             valueType,
             FlexMapImpl.DEFAULT_INIT_CAPACITY,
             FlexMapImpl.DEFAULT_LOAD_FACTOR);
    }

    /**
     * Reasonable defaults
     */
    private IdentityMap(IdentityKeyColumn keys, Column values) {
        this(keys, values, FlexMapImpl.DEFAULT_LOAD_FACTOR);
    }

    /**
     *
     */
    private IdentityMap(IdentityKeyColumn keys,
                        Column values,
                        float loadFactor) {
        this(keys, values, loadFactor, new ShareCount());
    }

    /**
     *
     */
    private IdentityMap(IdentityKeyColumn keys,
                        Column values,
                        float loadFactor,
                        ShareCount shareCount) {
        if (keys.capacity() != values.capacity()) {
            throw new IllegalArgumentException("columns must be same size");
        }
        myKeys = keys;
        myValues = values;

        if (loadFactor <= 0.0 || 1.0 < loadFactor) {
            throw new IllegalArgumentException(
              "Bad value for loadFactor" + loadFactor);
        }
        myLoadFactor = loadFactor;
        mySizeThreshold = (int)(myKeys.capacity() * myLoadFactor);
        myShareCount = shareCount;

        if (myKeys.numTaken() >= mySizeThreshold) {
            rehash();
        }
    }

    /**
     *
     */
    public IdentityMap(Class keyType, Class valueType, int initCapacity) {
        this(keyType,
             valueType,
             initCapacity,
             FlexMapImpl.DEFAULT_LOAD_FACTOR);
    }

    /**
     *
     */
    public IdentityMap(Class keyType,
                       Class valueType,
                       int initCapacity,
                       float loadFactor) {

        if (initCapacity <= 0) {
            throw new IllegalArgumentException(
              "bad initialCapacity " + initCapacity);
        }
        if (loadFactor <= 0.0 || 1.0 < loadFactor) {
            throw new IllegalArgumentException(
              "Bad value for loadFactor" + loadFactor);
        }
        myLoadFactor = loadFactor;
        myKeys = new IdentityKeyColumn(keyType, 1 + initCapacity);
        myValues = Column.values(valueType, myKeys.capacity());

        mySizeThreshold = (int)(myKeys.capacity() * myLoadFactor);
        myShareCount = new ShareCount();
        if (myKeys.numTaken() >= mySizeThreshold) {
            rehash();
        }
    }


    /**
     * Returns the value to which the key is mapped in this map. Unlike
     * java.util.Dictionary, a map doesn't indicate a lookup failure by
     * returning null, since null is a valid value. map throws an exception
     * instead.
     */
    public Object get(Object key) throws IndexOutOfBoundsException {
        int pos = myKeys.findPosOf(key);
        if (pos == -1) {
            throw new IndexOutOfBoundsException("not found: " + key);
        }
        return myValues.get(pos);
    }

    /**
     * Returns the value to which the key is mapped in this map. If key is not
     * mapped, return <tt>insteadThunk()</tt> instead.
     */
    public Object fetch(Object key, Thunk insteadThunk) {
        int pos = myKeys.findPosOf(key);
        if (pos == -1) {
            return insteadThunk.run();
        }
        return myValues.get(pos);
    }

    /**
     * The number of keys in the collection
     */
    public int size() {
        return myKeys.numTaken();
    }

    /**
     *
     */
    public void iterate(AssocFunc func) {
        IdentityMap snapshot;
        try {
            snapshot = (IdentityMap)clone();
        } catch (CloneNotSupportedException cnse) {
            throw ExceptionMgr.asSafe(cnse);
        }
        try {
            KeyColumn keys = snapshot.myKeys;
            int[] rank2Pos = keys.rank2Pos();
            int len = keys.numTaken();
            Column values = snapshot.myValues;

            for (int rank = 0; rank < len; rank++) {
                int pos = rank2Pos[rank];
                func.run(keys.get(pos), values.get(pos));
            }
        } finally {
            //avoid unnecessary copying
            snapshot.removeAll();
        }
    }

    /**
     * Returns true if the specified object is a key in the collection, as
     * defined by the equality function of the collection.
     *
     * @param key the object to look for
     * @return true if the key is in the collection
     */
    public boolean maps(Object key) {
        return myKeys.findPosOf(key) != -1;
    }

    /**
     *
     */
    public Object getKeys(Class type) {
        int[] rank2Pos = myKeys.rank2Pos();
        int len = myKeys.numTaken();
        Object result = ArrayHelper.newArray(type, len);

        for (int rank = 0; rank < len; rank++) {
            int pos = rank2Pos[rank];
            ArrayHelper.arraySet(result, rank, myKeys.get(pos));
        }
        return result;
    }

    /**
     *
     */
    public Object getValues(Class type) {
        int[] rank2Pos = myKeys.rank2Pos();
        int len = myKeys.numTaken();
        Object result = ArrayHelper.newArray(type, len);

        for (int rank = 0; rank < len; rank++) {
            int pos = rank2Pos[rank];
            ArrayHelper.arraySet(result, rank, myValues.get(pos));
        }
        return result;
    }

    /**
     * Defaults to not strict
     */
    public void put(Object key, Object value) {
        put(key, value, false);
    }

    /**
     *
     */
    public void put(Object key, Object value, boolean strict) {
        writeFault();

        //XXX this should instead be done with one lookup
        if (strict && maps(key)) {
            throw new IllegalArgumentException(key + " already in map");
        }

        if ((myKeys.numTaken() + 1) >= mySizeThreshold) {
            //just in case the key is novel
            rehash();
        }
        while (true) {
            int pos = myKeys.store(key);
            if (pos != -1) {
                myValues.put(pos, value);
                return;
            }
            rehash();
        }
    }

    /**
     *
     */
    private void rehash() {
        if (!myShareCount.isExclusive()) {
            myShareCount = myShareCount.release();
        }
        KeyColumn keys = myKeys;
        Column values = myValues;
        int capacity = 1 + StrictMath.max((keys.capacity() * 3) / 2,
                                          (keys.numTaken() / mySizeThreshold));
        myKeys = (IdentityKeyColumn)keys.newVacant(capacity);
        capacity = myKeys.capacity();
        myValues = myValues.newVacant(capacity);
        mySizeThreshold = (int)(capacity * myLoadFactor);

        int[] rank2Pos = keys.rank2Pos();
        int len = keys.numTaken();

        for (int rank = 0; rank < len; rank++) {
            int pos = rank2Pos[rank];
            put(keys.get(pos), values.get(pos));
        }
    }

    /**
     * Defaults to not strict
     */
    public void removeKey(Object key) {
        removeKey(key, false);
    }

    /**
     *
     */
    public void removeKey(Object key, boolean strict) {
        int pos = myKeys.findPosOf(key);
        if (pos != -1) {
            writeFault();
            myKeys.vacate(pos);
            myValues.vacate(pos);

        } else if (strict) {
            throw new IllegalArgumentException(key + " not in map");
        }
    }

    /**
     * Rather than doing a write-fault (which would make a private copy to be
     * immediately dropped) this decrements the sharing count and
     * re-initializes.
     */
    public void removeAll() {
        myShareCount = myShareCount.release();
        int capacity = myKeys.capacity();
        myKeys = (IdentityKeyColumn)myKeys.newVacant(capacity);
        myValues = myValues.newVacant(capacity);
    }

    /**
     * Unlike java.util.Hashtable, this part efficiently makes a lazy copy by
     * copy-on-write sharing. Modify operations on a shared map then cause the
     * delayed copy to happen.
     */
    public IdentityMap diverge(Class kType, Class vType) {

        if (kType == keyType() && vType == valueType()) {
            return new IdentityMap(myKeys,
                                   myValues,
                                   myLoadFactor,
                                   myShareCount.dup());
        } else {
            return new IdentityMap((IdentityKeyColumn)myKeys.diverge(kType),
                                   myValues.diverge(vType),
                                   myLoadFactor,
                                   new ShareCount());
        }
    }

    /**
     * All keys in this map must be of this type
     */
    public Class keyType() {
        return myKeys.memberType();
    }

    /**
     * All values in this map must be of this type
     */
    public Class valueType() {
        return myValues.memberType();
    }

    /**
     *
     */
    private void writeFault() {
        if (myShareCount.isExclusive()) {
            return;
        }
        myShareCount = myShareCount.release();
        myKeys = (IdentityKeyColumn)myKeys.clone();
        myValues = (Column)myValues.clone();
    }
}
