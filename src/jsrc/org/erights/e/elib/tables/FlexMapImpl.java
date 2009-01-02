package org.erights.e.elib.tables;

import org.erights.e.elib.base.Thunk;

/*
The contents of this file are subject to the Electric Communities E Open
Source Code License Version 1.0 (the "License"); you may not use this file
except in compliance with the License. You may obtain a copy of the License
at http://www.communities.com/EL/.

Software distributed under the License is distributed on an "AS IS" basis,
WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
the specific language governing rights and limitations under the License.

The Original Code is the Distributed E Language Implementation, released
July 20, 1998.

The Initial Developer of the Original Code is Electric Communities.
Copyright (C) 1998 Electric Communities. All Rights Reserved.

Contributor(s): ______________________________________.
*/


/**
 * An implementation of FlexMap by MarkM & Sidney, based on an earlier design
 * by Dan.
 *
 * @author Mark S. Miller
 */
class FlexMapImpl extends FlexMap {

    static private final long serialVersionUID = -4689682749412985954L;

    /**
     * This default load factor is calculated to preserve the same marginal
     * space cost per hash map entry as java.util.Hashtable, given its default
     * load factor of 0.75. For a map of 100 entries, assuming only one word of
     * hidden boxing overhead, java.util.Hastable uses:
     * <p/>
     * sizeof(HashtableEntry) == 5*4 == 20 100 * 20 == 2000 (capacity = 100 /
     * 0.75 == 133) capacity * 4 = 533 bytes per entry == 25.33
     * <p/>
     * FlexMapImpl, by contrast, uses 13 bytes per unit capacity (pos)
     * irrespective of how many are occupied.
     * <p/>
     * 13/25.33 == 0.52 (approx)
     */
    static public final float DEFAULT_LOAD_FACTOR = 0.52f;

    /**
     * Similarly, the default initial capacity of java.util.Hashtable is 101.
     * 101 * 0.75 / 0.52 == 146 (approx). But we're using 10 instead.
     */
    static public final int DEFAULT_INIT_CAPACITY = /*146*/ 10;

    /**
     * For either map, multiplying the two defaults gives the same default
     * threshold -- how many a default map can hold without growing: 75. But
     * we're using 5 instead.
     */
//    static private final int DEFAULT_THRESH = /*75*/ 5;

    /**
     *
     */
    KeyColumn myKeys;

    /**
     *
     */
    Column myValues;

    /**
     * The current size threshold for the map, that is, the number of elements
     * to hold before growing. It is calculated as capacity * myLoadFactor.
     */
    int mySizeThreshold;

    /**
     * The load factor for the map.
     */
    float myLoadFactor;

    /**
     *
     */
    ShareCount myShareCount;

    /**
     * Reasonable defaults
     */
    FlexMapImpl() {
        this(Object.class,
             Object.class,
             DEFAULT_INIT_CAPACITY,
             DEFAULT_LOAD_FACTOR);
    }

    /**
     * Reasonable defaults
     */
    FlexMapImpl(int initialCapacity) {
        this(Object.class, Object.class, initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    /**
     * Reasonable defaults
     */
    FlexMapImpl(int initialCapacity, float loadFactor) {
        this(Object.class, Object.class, initialCapacity, loadFactor);
    }

    /**
     * Reasonable defaults
     */
    FlexMapImpl(Class keyType, Class valueType) {
        this(keyType, valueType, DEFAULT_INIT_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

//    /**
//     * Reasonable defaults
//     */
//    private FlexMapImpl(KeyColumn keys, Column values) {
//        this(keys, values, DEFAULT_LOAD_FACTOR);
//    }

    /**
     *
     */
    FlexMapImpl(KeyColumn keys, Column values, float loadFactor) {
        this(keys, values, loadFactor, new ShareCount());
    }

    /**
     *
     */
    FlexMapImpl(KeyColumn keys,
                Column values,
                float loadFactor,
                ShareCount shareCount) {
        if (keys.capacity() != values.capacity()) {
            throw new IllegalArgumentException("columns must be same size");
        }
        myKeys = keys;
        myValues = values;

        if (0.0 >= loadFactor || 1.0 < loadFactor) {
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
    FlexMapImpl(Class keyType, Class valueType, int initCapacity) {
        this(keyType, valueType, initCapacity, DEFAULT_LOAD_FACTOR);
    }

    /**
     *
     */
    FlexMapImpl(Class keyType,
                       Class valueType,
                       int initCapacity,
                       float loadFactor) {
        if (0 >= initCapacity) {
            if (0 > initCapacity) {
                throw new IllegalArgumentException(
                  "bad initialCapacity " + initCapacity);
            } else {
                //XXX Is this a kludge?  It seems so, but it might be
                //the right thing
                initCapacity = 1;
            }
        }
        if (0.0 >= loadFactor || 1.0 < loadFactor) {
            throw new IllegalArgumentException(
              "Bad value for loadFactor" + loadFactor);
        }
        myLoadFactor = loadFactor;

        myKeys = KeyColumn.make(keyType, 1 + initCapacity);
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
        if (-1 == pos) {
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
        if (-1 == pos) {
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
        FlexMapImpl snapshot = (FlexMapImpl)clone();
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
        return -1 != myKeys.findPosOf(key);
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
            if (-1 != pos) {
                myValues.put(pos, value);
                return;
            }
            rehash();
        }
    }

    /**
     *
     */
    void rehash() {
        if (!myShareCount.isExclusive()) {
            myShareCount = myShareCount.release();
        }
        KeyColumn keys = myKeys;
        Column values = myValues;
        int capacity = 1 + StrictMath.max((keys.capacity() * 3) / 2,
                                          (keys.numTaken() / mySizeThreshold));
        myKeys = (KeyColumn)keys.newVacant(capacity);
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
     *
     */
    public void removeKey(Object key, boolean strict) {
        int pos = myKeys.findPosOf(key);
        if (-1 != pos) {
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
        myKeys = (KeyColumn)myKeys.newVacant(capacity);
        myValues = myValues.newVacant(capacity);
    }

    /**
     * Unlike java.util.Hashtable, this part efficiently makes a lazy copy by
     * copy-on-write sharing. Modify operations on a shared map then cause the
     * delayed copy to happen.
     */
    public FlexMap diverge(Class kType, Class vType) {

        if (kType == keyType() && vType == valueType()) {
            return new FlexMapImpl(myKeys,
                                   myValues,
                                   myLoadFactor,
                                   myShareCount.dup());
        } else {
            return new FlexMapImpl((KeyColumn)myKeys.diverge(kType),
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
    void writeFault() {
        if (myShareCount.isExclusive()) {
            return;
        }
        myShareCount = myShareCount.release();
        myKeys = (KeyColumn)myKeys.clone();
        myValues = (Column)myValues.clone();
    }
}
