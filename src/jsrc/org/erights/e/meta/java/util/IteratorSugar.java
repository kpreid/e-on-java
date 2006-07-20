// Copyright 2005 Mark S. Miller, under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.meta.java.util;

import org.erights.e.elib.tables.AssocFunc;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.FlexList;
import org.erights.e.meta.java.math.EInt;

import java.util.Iterator;

/**
 * A sweetener defining extra messages that may be e-sent to an Iterator.
 *
 * @author Mark S. Miller
 */
public class IteratorSugar {

    /**
     * prevents instantiation
     */
    private IteratorSugar() {
    }

    /**
     * Enumerates the enumeration. For each element, calls func with the
     * count as key and the element as value.
     */
    static public void iterate(Iterator self, AssocFunc func) {
        int i = 0;
        while (self.hasNext()) {
            func.run(EInt.valueOf(i), self.next());
            i++;
        }
    }

    /**
     *
     */
    static public ConstList asList(Iterator self) {
        FlexList buf = FlexList.make();
        while (self.hasNext()) {
            buf.push(self.next());
        }
        return buf.snapshot();
    }
}
