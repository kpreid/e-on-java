package org.erights.e.meta.java.util;

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

import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.tables.AssocFunc;
import org.erights.e.meta.java.math.EInt;

import java.io.IOException;
import java.util.Vector;


/**
 *
 */
public class VectorSugar {

    /**
     * prevent instantiation
     */
    private VectorSugar() {
    }

    /**
     * If the index is in bounds, return the corresponding element. Otherwise
     * throw an IndexOutOfBoundsException
     */
    static public Object get(Vector self, int index)
      throws IndexOutOfBoundsException {
        return self.elementAt(index);
    }

    /**
     * Considered as a map, a Vector is a map from indices to values, so
     * iterate() will call func with each successive pair of index and value,
     * in ascending index order. It does this over a snapshot of the Vector, so
     * that changes to the Vector during enumeration don't effect the
     * enumeration.
     */
    static public void iterate(Vector self, AssocFunc func) {
        Object[] elements = self.toArray();
        int len = elements.length;
        for (int i = 0; i < len; i++) {
            func.run(EInt.valueOf(i), elements[i]);
        }
    }

    /**
     *
     */
    static public void put(Vector self, int index, Object newValue) {
        self.setElementAt(newValue, index);
    }

    /**
     * Btw, in java1.1, Vector.toString() blows up on null elements
     */
    static public void __printOn(Vector self, TextWriter out)
      throws IOException {
        out.print("Vec", self.toArray());
    }
}
