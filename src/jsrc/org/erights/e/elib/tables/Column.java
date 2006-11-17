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
 * A column of a FlexMapImpl. <p>
 * <p/>
 * A FlexMapImpl has a KeyColumn for the keys that it maps from, and a Column
 * for the values being mapped to. The indices into a column are called
 * positions.
 *
 * @author Mark S. Miller
 */


abstract class Column implements Cloneable {


    Column() {
    }

    /**
     * A shallow copy of the column. The members are shared, not copied. The
     * new Column is restricted to only holding members of type membType.
     */
    protected abstract Column diverge(Class membType);

    /**
     * Argument defaults to memberType()
     */
    protected Object clone() {
        return diverge(memberType());
    }

    /**
     *
     */

    abstract Object get(int pos);

    /**
     * All the members of the column must conform to this type
     */

    abstract Class memberType();

    /**
     * Makes a new column just like this one, except of the specified size and
     * without any members.
     */

    abstract Column newVacant(int capacity);

    /**
     *
     */

    abstract int capacity();

    /**
     *
     */

    abstract void put(int pos, Object value);

    /**
     * Stop pointing at an object from this pos. If this is a scalar column,
     * does nothing
     */

    abstract void vacate(int pos);

    /**
     * memberType defaults to Object
     */
    static public Column values(int capacity) {
        return values(Object.class, capacity);
    }

    /**
     * Make a value-column that can only hold values that conform to
     * 'memberType' and has 'capacity' positions. If the memberType is a scalar
     * type, the column will represent these values unboxed.
     */
    static public Column values(Class memberType, int capacity) {

        memberType = ArrayHelper.typeForArray(memberType);

        if (memberType == Void.class) {
            return new VoidColumn(capacity);

        } else if (memberType == Integer.TYPE) {
            return new IntColumn(capacity);

        } else if (memberType.isPrimitive()) {
            return new ScalarColumn(memberType, capacity);

        } else {
            return new RefColumn(memberType, capacity);
        }
    }
}
