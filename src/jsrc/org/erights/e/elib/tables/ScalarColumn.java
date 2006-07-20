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

import java.lang.reflect.Array;


/**
 * @author Mark S. Miller
 */
class ScalarColumn extends Column {

    private final Object myArray;

    /**
     *
     */
    ScalarColumn(Class memberType, int capacity) {
        this(ArrayHelper.newArray(memberType, capacity));
    }

    /**
     *
     */
    private ScalarColumn(Object array) {
        super();
        myArray = array;
    }

    /**
     *
     */
    protected Column diverge(Class membType) {
        int len = capacity();
        Object array = ArrayHelper.newArray(membType, len);
        System.arraycopy(myArray, 0, array, 0, len);
        return new ScalarColumn(array);
    }

    /**
     *
     */
    Object get(int pos) {
        return Array.get(myArray, pos);
    }

    /**
     *
     */
    Class memberType() {
        return myArray.getClass().getComponentType();
    }

    /**
     *
     */
    Column newVacant(int capacity) {
        return new ScalarColumn(memberType(), capacity);
    }

    /**
     *
     */
    int capacity() {
        return Array.getLength(myArray);
    }

    /**
     *
     */
    void put(int pos, Object value) {
        ArrayHelper.arraySet(myArray, pos, value);
    }

    /**
     *
     */
    void vacate(int pos) {
        //don't need to do anything
    }
}
