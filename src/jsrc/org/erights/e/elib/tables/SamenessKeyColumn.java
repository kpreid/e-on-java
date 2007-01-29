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

import org.erights.e.elib.ref.Ref;


/**
 * Compares using Equalizer.same() and Equalizer.samenessHash()
 *
 * @author Mark S. Miller
 */


class SamenessKeyColumn extends KeyColumn {

    /**
     * The maximum number of probes made so far.
     */
    private final int myMaxProbes;

    /**
     * The array of computed hashes for each key in the set.
     */
    private final int[] myHashes;

    /**
     * Reasonable defaults
     */
    SamenessKeyColumn() {
        this(Object.class, FlexMapImpl.DEFAULT_INIT_CAPACITY);
    }

    private SamenessKeyColumn(Object[] keys,
                              int[] pos2Rank,
                              int[] rank2Pos,
                              int numTaken,
                              int numDeleted,
                              int maxProbes,
                              int[] hashes) {
        super(keys, pos2Rank, rank2Pos, numTaken, numDeleted);
        myMaxProbes = maxProbes;
        myHashes = hashes;
    }

    /**
     * Reasonable defaults
     */
    SamenessKeyColumn(int capacity) {
        this(Object.class, capacity);
    }

    /**
     * Reasonable defaults
     */
    SamenessKeyColumn(Class memberType) {
        this(memberType, FlexMapImpl.DEFAULT_INIT_CAPACITY);
    }

    /**
     * @param memberType
     * @param capacity
     */
    SamenessKeyColumn(Class memberType, int capacity) {
        super(memberType, capacity);
        myMaxProbes = 0;
        myHashes = new int[myKeys.length];
    }

    /**
     *
     */
    protected Column diverge(Class membType) {
        int len = myKeys.length;
        Object[] keys = (Object[])ArrayHelper.newArray(membType, len);
        System.arraycopy(myKeys, 0, keys, 0, len);

        return new SamenessKeyColumn(keys,
                                     (int[])myPos2Rank.clone(),
                                     (int[])myRank2Pos.clone(),
                                     myNumTaken,
                                     myNumDeleted,
                                     myMaxProbes,
                                     (int[])myHashes.clone());
    }

    /**
     *
     */

    Column newVacant(int capacity) {
        return new SamenessKeyColumn(memberType(), capacity);
    }

    /**
     *
     */

    int findPosOf(Object key) {
        int hash = Equalizer.samenessHash(key) & POSITIVE_MASK;
        int curPos = hash % myKeys.length;
        int initialProbe = curPos;
        int probeSkip = skip(hash, myKeys.length);

        // search the array for the key (or equivalent)
        for (; ;) {
            int status = myPos2Rank[curPos];
            if (KEY_UNUSED == status) {
                // not in map
                return -1;
            }
            Object curKey = myKeys[curPos];
            if (0 <= status && hash == myHashes[curPos] &&
              Ref.isSameEver(curKey, key)) {
                // we found it.
                return curPos;
            }
            curPos += probeSkip;
            if (curPos >= myKeys.length) {
                curPos -= myKeys.length;
            }
            if (curPos == initialProbe) {
                //not in map
                return -1;
            }
        }
    }

    /**
     *
     */

    int store(Object key) {
        int hash = Equalizer.samenessHash(key) & POSITIVE_MASK;
        int curPos = hash % myKeys.length;
        int initialProbe = curPos;
        int probeSkip = skip(hash, myKeys.length);
        int firstVacant = -1;

        // search the array for the key
        for (; ;) {
            Object curKey = myKeys[curPos];
            int status = myPos2Rank[curPos];
            if (KEY_UNUSED == status) {
                if (-1 != firstVacant) {
                    return occupy(firstVacant, key, hash);
                } else {
                    return occupy(curPos, key, hash);
                }
            } else if (KEY_DELETED == status) {
                //the pos is vacant
                if (-1 == firstVacant) {
                    // we found the first vacant pos of the search
                    // path. It'll be used if the element isn't
                    // eventually found
                    firstVacant = curPos;
                }
            } else
            if (hash == myHashes[curPos] && Ref.isSameEver(curKey, key)) {
                // we found it.
                return curPos;
            }
            curPos += probeSkip;
            if (curPos >= myKeys.length) {
                curPos -= myKeys.length;
            }
            if (curPos == initialProbe) {
                // we wrapped. Either we passed a deleted pos or
                // there's no room in the map
                if (-1 != firstVacant) {
                    return occupy(firstVacant, key, hash);
                } else {
                    return -1;
                }
            }
        }
    }

    /**
     *
     */
    private int occupy(int pos, Object key, int hash) {
        myHashes[pos] = hash;
        return occupy(pos, key);
    }

    /**
     *
     */

    void vacate(int pos) {
        super.vacate(pos);
        myHashes[pos] = 0;
    }
}
