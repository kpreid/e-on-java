package org.erights.e.elib.tables;

import org.erights.e.develop.assertion.T;
import org.erights.e.meta.java.math.EInt;

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


class IntColumn extends Column {

    /**
     *
     */
    private final int[] myArray;

    /**
     *
     */
    private IntColumn(int[] array) {
        super();
        myArray = array;
    }

    /**
     *
     */
    IntColumn(int capacity) {
        this(new int[capacity]);
    }

    /**
     *
     */
    protected Column diverge(Class membType) {
        T.require(membType == Integer.TYPE,
                  "XXX IntColumn.diverge(non-int) not yet implemented");
        int[] array = new int[myArray.length];
        System.arraycopy(myArray, 0, array, 0, myArray.length);
        return new IntColumn(array);
    }

    /**
     *
     */
    Object get(int pos) {
        return EInt.valueOf(myArray[pos]);
    }

    /**
     *
     */
    int getInt(int pos) {
        return myArray[pos];
    }

    /**
     *
     */
    Class memberType() {
        return Integer.TYPE;
    }

    /**
     *
     */
    Column newVacant(int capacity) {
        return new IntColumn(capacity);
    }

    /**
     *
     */
    int capacity() {
        return myArray.length;
    }

    /**
     *
     */
    void put(int pos, Object value) {
        myArray[pos] = ((Integer)value).intValue();
    }

    /**
     *
     */
    void putInt(int pos, int value) {
        myArray[pos] = value;
    }

    /**
     *
     */
    void vacate(int pos) {
        //don't need to do anything
    }
}
