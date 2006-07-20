package org.erights.e.elib.tables;

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

import org.erights.e.elib.serial.PassByProxy;

import java.lang.reflect.Modifier;

/**
 * Based on the GenericHashtable in the vat by Dan Bornstein
 *
 * @author Mark S. Miller
 */
abstract class KeyColumn extends Column {

    /**
     * Used to ensure ints are positive
     */
    static public final int POSITIVE_MASK = 0x7FFFFFFF;

    /**
     * The sparse array of keys by position. null is a valid entry.
     * The value of an entry is only valid if indicated by the
     * corresponding myPos2Rank entry.
     */

    final Object[] myKeys;

    /**
     * A sparse array parallel to myKeys. Each element contains
     * either KEY_UNUSED, KEY_DELETED, or an index into myRank2Pos
     * indicating where the association at this position fits into the
     * ordering.
     */
    final int[] myPos2Rank;

    /**
     * A value in myPos2Rank indicating that this position has never
     * been occupied since the last rehash. Note that KEY_UNUSED is
     * negative, so it can't conflict with an array index.
     */
    static final int KEY_UNUSED = -1;

    /**
     * A value in myPos2Rank indicating that this position used to be
     * occupied since the last rehash, but no longer is. Note that
     * KEY_DELETED is negative, so it can't conflict with an array
     * index.
     */
    static final int KEY_DELETED = -2;

    /**
     * A dense array, listing positions in their enumeration order.
     * <p/>
     * The allocated size of myRank2Pos is actually the same as the
     * sparse arrays, but it's dense in 0..!myNumTaken, and unused
     * after that.
     */
    final int[] myRank2Pos;


    /**
     * The number of key currently in the column.
     */
    int myNumTaken;

    /**
     * The number of keys currently marked deleted in the column.
     */
    int myNumDeleted;

    /**
     * Little sorted array of primes for use to size key columns.
     * The elements grow exponentially at somewhat less than 2x.
     */
    static private final int possibleSizes[] = {
        17, 23, 37, 53, 79, 109, 151, 211, 293, 421, 593, 829,
        1171, 1637, 2293,
        3209, 4493, 6299, 8819, 12347, 17257, 24197, 33871, 47431, 66403,
        92959, 130147, 182209, 255107, 357139, 500009, 700027, 980047, 1372051,
        1920901, 2689261, 3764953, 5270939, 7379327, 10331063, 14463487,
        20248897, 28348447, 39687871, 55563023, 77788201, 108903523,
        152464943, 213450911, 298831279, 418363789, 585709217, 819993047,
        1147990271, 1607186393
    };


    KeyColumn(Object[] keys,
              int[] pos2Rank,
              int[] rank2Pos,
              int numTaken,
              int numDeleted) {
        myKeys = keys;
        myPos2Rank = pos2Rank;
        myRank2Pos = rank2Pos;
        myNumTaken = numTaken;
        myNumDeleted = numDeleted;
    }

    /**
     * Construct a new, empty KeyColumn, with the specified parameters.
     *
     * @param memberType All keys will be (implicitly) checked for
     *                   conformance to this type.
     * @param capacity   The column will hold at least this number of
     *                   positions. To find out the actual number, call capacity() on the
     *                   constructed column.
     */
    KeyColumn(Class memberType, int capacity) {
        capacity = firstSize(capacity);
        myKeys = (Object[])ArrayHelper.newArray(memberType, capacity);
        myPos2Rank = new int[capacity];
        for (int i = 0; i < capacity; i++) {
            myPos2Rank[i] = KEY_UNUSED;
        }
        myRank2Pos = new int[capacity];
        myNumTaken = 0;
        myNumDeleted = 0;
    }

    /**
     * Makes a key column that's equivalent to a
     * SamenessKeyColumn(memberType, capacity), but may select a more
     * efficient implementation based on how instances of memberType compare
     * for sameness.
     */
    static KeyColumn make(Class memberType, int capacity) {

        if (memberType == String.class) {
            //broken out bootstrap case
            return new EqualityKeyColumn(memberType, capacity);

        } else if (memberType == Class.class) {
            //broken out bootstrap case
            return new IdentityKeyColumn(memberType, capacity);

        } else if (memberType == Object.class) {
            //broken out bootstrap case
            return new SamenessKeyColumn(memberType, capacity);

        } else if (Selfless.class.isAssignableFrom(memberType) ||
          memberType.isArray()) {
            //explicitly Selfless
            return new SamenessKeyColumn(memberType, capacity);

        } else if (Selfless.HONORARY.has(memberType)) {
            //honorary Selfless can just use equals() and hashCode()
            return new EqualityKeyColumn(memberType, capacity);

        } else if (Modifier.isFinal(memberType.getModifiers()) ||
          //If it's final, then we know that instances (of subclasses)
          //don't fall in the above two categories.
          PassByProxy.class.isAssignableFrom(memberType) ||
          PassByProxy.HONORARY.has(memberType))
        //PassByProxy objects (actual or HONORARY) may not be
        //Selfless (actual or HONORARY).
        {
            return new IdentityKeyColumn(memberType, capacity);

        } else {
            //The general case
            return new SamenessKeyColumn(memberType, capacity);
        }
    }

    /**
     * Returns the pos at which key resides, or -1 if the key is
     * absent from the map.
     */
    abstract int findPosOf(Object key);

    /**
     * Returns the first good size for a key column that's no less
     * than candidate. This will be a prime number so that we get
     * better distribution of elements through the map.
     */
    static private int firstSize(int candidate) {
        for (int i = 0; i < possibleSizes.length; i++) {
            if (candidate <= possibleSizes[i]) {
                return possibleSizes[i];
            }
        }
        throw new IllegalArgumentException("too big");
    }

    /**
     *
     */
    Object get(int pos) {
        return myKeys[pos];
    }

    /**
     * Given a pos, say whether this pos contains a valid key.
     */
    boolean isPosTaken(int pos) {
        //XXX is the >=0 test necessary?
        return (pos >= 0 && myPos2Rank[pos] >= 0);
    }

    /**
     *
     */
    Class memberType() {
        return myKeys.getClass().getComponentType();
    }

    /**
     *
     */
    int capacity() {
        return myKeys.length;
    }

    /**
     * Get the number of keys in the column
     */
    int numTaken() {
        return myNumTaken;
    }

    /**
     * Caller should only read, and only between 0..!numTaken()
     */
    int[] rank2Pos() {
        return myRank2Pos;
    }

    /**
     *
     */
    void put(int pos, Object value) {
        throw new Error("internal: don't 'put' on a KeyColumn");
    }

    /**
     *
     */
    static int skip(int hash, int len) {
        int result = (hash + (hash / len)) % len;
        return StrictMath.max(1, result);
    }

    /**
     * Put the given key into the map, and return its pos. If the key
     * is equivalent (according to the concrete column's equality
     * function) to a key already in the map, that pos is returned.
     * If the key is novel but the map is too small to add it, a -1 is
     * returned. <p>
     * <p/>
     * If the key already exists, no ordering information is
     * affected. If a novel key is inserted, it's position gets the
     * next rank.
     *
     * @param key the key to place in the map
     * @return the pos at which the key now resides in the
     *         map, or -1 if we need more room.
     */
    abstract int store(Object key);

    /**
     *
     */
    int occupy(int pos, Object key) {
        myKeys[pos] = key;
        if (myPos2Rank[pos] == KEY_DELETED) {
            myNumDeleted--;
        }
        myPos2Rank[pos] = myNumTaken;
        myRank2Pos[myNumTaken] = pos;
        myNumTaken++;
        return pos;
    }

    /**
     * cause pos not to contain a valid key
     */
    void vacate(int pos) {
        if (pos < 0) {
            //XXX is this test necessary?
            return;
        }
        int rank = myPos2Rank[pos];
        if (rank < 0) {
            return;
        }
        myKeys[pos] = null;
        myNumTaken--;
        int oldLastPos = myRank2Pos[myNumTaken];
        myRank2Pos[rank] = oldLastPos;
        //the next two assignments must occur in this order, in case
        //rank is the new myNumTaken
        myPos2Rank[oldLastPos] = rank;
        myPos2Rank[pos] = KEY_DELETED;
        myNumDeleted++;
    }
}
