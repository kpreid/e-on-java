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


class RefColumn extends Column {

    private final Object[] myArray;

    /**
     *
     */
    private RefColumn(Object[] array) {
        super();
        myArray = array;
    }

    /**
     *
     */

    RefColumn(Class memberType, int capacity) {
        this((Object[])ArrayHelper.newArray(memberType, capacity));
    }

    /**
     *
     */
    protected Column diverge(Class membType) {
        Object[] array = (Object[])ArrayHelper.newArray(membType,
                                                        myArray.length);
        System.arraycopy(myArray, 0, array, 0, myArray.length);
        return new RefColumn(array);
    }

    /**
     *
     */

    Object get(int pos) {
        return myArray[pos];
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
        return new RefColumn(memberType(), capacity);
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
        myArray[pos] = value;
    }

    /**
     *
     */

    void vacate(int pos) {
        myArray[pos] = null;
    }
}
