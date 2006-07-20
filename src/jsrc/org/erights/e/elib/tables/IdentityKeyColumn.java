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


/**
 * @author Mark S. Miller
 */


class IdentityKeyColumn extends KeyColumn {

    /**
     * The maximum number of probes made so far.
     */
    private final int myMaxProbes;

    /**
     * Reasonable defaults
     */
    public IdentityKeyColumn() {
        this(Object.class, FlexMapImpl.DEFAULT_INIT_CAPACITY);
    }

    /**
     *
     */
    private IdentityKeyColumn(Object[] keys,
                              int[] pos2Rank,
                              int[] rank2Pos,
                              int numTaken,
                              int numDeleted,
                              int maxProbes) {
        super(keys, pos2Rank, rank2Pos, numTaken, numDeleted);
        myMaxProbes = maxProbes;
    }

    /**
     * Reasonable defaults
     */
    public IdentityKeyColumn(int capacity) {
        this(Object.class, capacity);
    }

    /**
     * Reasonable defaults
     */
    public IdentityKeyColumn(Class memberType) {
        this(memberType, FlexMapImpl.DEFAULT_INIT_CAPACITY);
    }

    /**
     *
     */
    public IdentityKeyColumn(Class memberType, int capacity) {
        super(memberType, capacity);
        myMaxProbes = 0;
    }

    /**
     *
     */
    protected Column diverge(Class membType) {
        int len = myKeys.length;
        Object[] keys = (Object[])ArrayHelper.newArray(membType, len);
        System.arraycopy(myKeys, 0, keys, 0, len);

        return new IdentityKeyColumn(keys,
                                     (int[])myPos2Rank.clone(),
                                     (int[])myRank2Pos.clone(),
                                     myNumTaken,
                                     myNumDeleted,
                                     myMaxProbes);
    }

    /**
     *
     */

    Column newVacant(int capacity) {
        return new IdentityKeyColumn(memberType(), capacity);
    }

    /**
     *
     */

    int findPosOf(Object key) {
        int hash = System.identityHashCode(key) & POSITIVE_MASK;
        int curPos = hash % myKeys.length;

        //do the first probe before the loop, so we only calculate
        //probeSkip if the first one misses
        Object curKey;
        int status = myPos2Rank[curPos];
        if (status == KEY_UNUSED) {
            return -1;
        }
        curKey = myKeys[curPos];
        if (curKey == key && status >= 0) {
            //we found it
            return curPos;
        }
        int probeSkip = skip(hash, myKeys.length);
        int initialProbe = curPos;
        for (; ;) {
            curPos += probeSkip;
            if (curPos >= myKeys.length) {
                curPos -= myKeys.length;
            }
            if (curPos == initialProbe) {
                // not in map
                return -1;
            }
            status = myPos2Rank[curPos];
            if (status == KEY_UNUSED) {
                // not in map
                return -1;
            }
            curKey = myKeys[curPos];
            if (curKey == key && status >= 0) {
                //we found it
                return curPos;
            }
        }
    }

    /**
     *
     */

    int store(Object key) {
        int hash = System.identityHashCode(key) & POSITIVE_MASK;
        int curPos = hash % myKeys.length;
        int initialProbe = curPos;
        int probeSkip = skip(hash, myKeys.length);
        int firstVacant = -1;

        // search the array for the key
        for (; ;) {
            Object curKey = myKeys[curPos];
            int status = myPos2Rank[curPos];
            if (status == KEY_UNUSED) {
                if (firstVacant != -1) {
                    return occupy(firstVacant, key);
                } else {
                    return occupy(curPos, key);
                }
            } else if (status == KEY_DELETED) {
                //the pos is vacant
                if (firstVacant == -1) {
                    // we found the first vacant pos of the search
                    // path. It'll be used if the element isn't
                    // eventually found
                    firstVacant = curPos;
                }
            } else if (curKey == key) {
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
                if (firstVacant != -1) {
                    return occupy(firstVacant, key);
                } else {
                    return -1;
                }
            }
        }
    }
}
