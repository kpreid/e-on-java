package org.erights.e.elib.tables;

import org.erights.e.develop.assertion.T;
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
 * Used to memoize the source strings/twine fed to QuasiParsers, and the
 * mapping to the ValueMaker or MatchMaker it generated.
 * <p/>
 * Once we go to the new hole-array protocol, this will have to be added to the
 * memoization as well.
 * <p/>
 * Because IdentityCacheTable exposes EQ-ness of the keys, it is unsafe, and so
 * (until we can audit for determinism) can only be used by trusted
 * quasiParsers.
 * <p/>
 * Note: keys may not be null, but values may be null
 *
 * @author Mark S. Miller
 */
public class IdentityCacheTable {

    static private final int POSITIVE_MASK = 0x7FFFFFFF;

    /**
     * String or Twine other than SimpleTwine or EmptyTwine
     */
    private final Object[] myKeys;

    private final Column myValues;

    private final int mySize;

    /**
     *
     */
    public IdentityCacheTable(Class valueType, int size) {
        myKeys = new Object[size];
        myValues = Column.values(valueType, size);
        mySize = size;
    }

    /**
     *
     */
    static private Object realKey(Twine key) {
        T.notNull(key, "key may not be null");
        if (key instanceof SimpleTwine || key instanceof EmptyTwine) {
            return key.bare();
        } else {
            return key;
        }
    }

    /**
     *
     */
    public Object fetch(Twine key, Thunk insteadThunk) {
        Object kee = realKey(key);
        int index = (System.identityHashCode(kee) & POSITIVE_MASK) % mySize;
        if (myKeys[index] == kee) {
            return myValues.get(index);
        } else {
            return insteadThunk.run();
        }
    }

    /**
     *
     */
    public void put(Twine key, Object value) {
        Object kee = realKey(key);
        int index = (System.identityHashCode(kee) & POSITIVE_MASK) % mySize;
        myKeys[index] = kee;
        myValues.put(index, value);
    }
}
