package org.erights.e.elib.util;

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

import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.FlexList;

/**
 * A collection of objects which can be iterated through while being added to.
 * This collection is like a set in that a given object can only be in the
 * collection once. It is unlike a set in that is ordered. The enumeration
 * DynamicMapEnumeration allows the set to be extended while it is
 * being enumerated.
 */
public class DynamicMap {

    private final FlexList myElems;

    /**
     * Create a new dynamic collection.
     *
     * @param elems The objects which are to be the initial members of the
     *              collection.
     */
    public DynamicMap(ConstList elems) {
        myElems = FlexList.make();
        addElems(elems);
    }

    /**
     * Add new objects to the collection.
     *
     * @param elems The objects which are to be added to the collection.
     */
    public void addElems(ConstList elems) {
        addElemsAt(elems, 0);
    }

    /**
     * Add new objects to the collection at a specific location.
     *
     * @param elems The objects which are to be added to the collection.
     */

    void addElemsAt(ConstList elems, int loc) {
        int len = elems.size();
        FlexList newElems = FlexList.make(len);
        for (int i = 0; i < len; i++) {
            Object elem = elems.get(i);
            if (!myElems.contains(elem)) {
                newElems.push(elem);
            }
        }
        myElems.setRun(loc, loc, newElems);
    }

    /**
     * Return an Enumeration of the elements of the collection. This
     * Enumeration will include any elements that get added to the collection
     * before the user of the Enumeration enumerates all the elements.
     */
    public DynamicMapEnumeration elems() {
        return new DynamicMapEnumeration(this);
    }

    /**
     * Return the current size of the collection.
     */
    public int size() {
        return myElems.size();
    }

    /**
     * Return the nth member of the collection.
     */
    public Object get(int n) {
        return myElems.get(n);
    }
}
