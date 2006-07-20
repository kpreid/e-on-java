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
class ConstListImpl extends ConstList implements ArrayedList {

    static private final long serialVersionUID = 6618785990395724987L;

    /**
     * myVals is an array
     */
    private final Object myVals;

    /**
     * The caller is *trusted* not to modify vals after handing it in.
     */
    ConstListImpl(Object vals) {
        myVals = vals;
    }

    /**
     * 'ConstListMaker.fromArray(myVals)'
     */
    public Object[] getSpreadUncall() {
        Object[] result = {GetMaker(), "fromArray", myVals};
        return result;
    }

    /**
     * ConstLists don't uncall according to the form of their spreadCall, in
     * order to avoid an infinite regress.
     * <p/>
     * 'ConstListMaker.run(myVals...)'
     */
    public Object[] __optUncall() {
        Object[] result = {GetMaker(), "run", myVals};
        return result;
    }

    /**
     * The caller is *trusted* not to modify the returned array. Even though
     * this is declared 'public', it is believed (and required) to be package
     * scope, since all declarations are only in package scoped classes or
     * interfaces.
     */
    public Object getSecretArray() {
        return myVals;
    }

    /**
     *
     */
    public Object get(int index) throws IndexOutOfBoundsException {
        return Array.get(myVals, index);
    }

    /**
     * How many entries are in the table?
     */
    public int size() {
        return Array.getLength(myVals);
    }

    /**
     * All values in this table must be of this type
     */
    public Class valueType() {
        return myVals.getClass().getComponentType();
    }
}
