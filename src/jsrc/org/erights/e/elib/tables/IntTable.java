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
 * An optimization to provide a non-boxing protocol for int gets and puts.
 *
 * @author Mark S. Miller
 */
public class IntTable extends FlexMapImpl {

    static private final long serialVersionUID = -7999483480918011366L;

    /**
     * Reasonable defaults
     */
    public IntTable() {
        this(new EqualityKeyColumn(), DEFAULT_LOAD_FACTOR);
    }

    /**
     * Reasonable defaults
     */
    public IntTable(Class keyType) {
        this(new EqualityKeyColumn(keyType));
    }

    /**
     * Reasonable defaults
     */

    private IntTable(KeyColumn keys) {
        this(keys, DEFAULT_LOAD_FACTOR);
    }

    /**
     *
     */

    private IntTable(KeyColumn keys, float loadFactor) {
        super(keys, new IntColumn(keys.capacity()), loadFactor);
    }

    /**
     * JAY -- had to add this to clear up a foo2j bug where it was not finding
     * constructor XXX Is this still necessary?  --MarkM
     */

    private IntTable(KeyColumn keys,
                     Column values,
                     float loadFactor,
                     ShareCount shareCount) {
        super(keys, values, loadFactor, shareCount);
    }

    /**
     *
     */
    private IntTable(KeyColumn keys,
                     IntColumn values,
                     float loadFactor,
                     ShareCount shareCount) {
        super(keys, values, loadFactor, shareCount);
    }

    /**
     *
     */
    public Object clone() {
        /* JAY -- put in explicit casts to work around foo2j bug. */

        return new IntTable(myKeys,
                            myValues,
                            myLoadFactor,
                            myShareCount.dup());
    }

    /**
     * unboxed value optimization
     *
     * @see org.erights.e.elib.tables.EMap#get
     */
    public int getInt(Object key) throws IndexOutOfBoundsException {
        int pos = myKeys.findPosOf(key);
        if (pos == -1) {
            throw new IndexOutOfBoundsException("key not found");
        }
        return ((IntColumn)myValues).getInt(pos);
    }

    /**
     * unboxed value optimization
     *
     * @see org.erights.e.elib.tables.EMap#get
     */
    public int getInt(Object key, int instead) {
        int pos = myKeys.findPosOf(key);
        if (pos == -1) {
            return instead;
        }
        return ((IntColumn)myValues).getInt(pos);
    }

    /**
     * unboxed value optimization
     *
     * @see org.erights.e.elib.tables.FlexMap#put
     */
    public void putInt(Object key, int value) {
        putInt(key, value, false);
    }

    /**
     * unboxed value optimization
     *
     * @see org.erights.e.elib.tables.FlexMap#put
     */
    public void putInt(Object key, int value, boolean strict) {
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
                ((IntColumn)myValues).putInt(pos, value);
                return;
            }
            rehash();
        }
    }
}
