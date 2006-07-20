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

import java.util.Enumeration;

/**
 * An Enumeration of the elements of a DynamicMap object.
 */
public class DynamicMapEnumeration implements Enumeration {

    private final DynamicMap myCollection;

    private int myCurrentPosition;

    /**
     * Construct a new Enumeration based on a given DynamicMap.
     */
    public DynamicMapEnumeration(DynamicMap collection) {
        myCollection = collection;
        myCurrentPosition = 0;
    }

    /**
     * Add elements at the current position.
     */
    public void addElems(ConstList elems) {
        myCollection.addElemsAt(elems, myCurrentPosition);
    }

    /**
     * Return true if there are any more elements remaining to be enumerated.
     */
    public boolean hasMoreElements() {
        return myCurrentPosition < myCollection.size();
    }

    /**
     * Return the next element in the collection.
     */
    public Object nextElement() {
        return myCollection.get(myCurrentPosition++);
    }
}
