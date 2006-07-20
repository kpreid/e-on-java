// Copyright 2005 Mark S. Miller, under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.meta.java.util;

import org.erights.e.elib.tables.AssocFunc;

import java.util.Iterator;
import java.util.Collection;

/**
 * @author Mark S. Miller
 */
public class CollectionSugar {

    static public void iterate(Collection self, AssocFunc func) {
        IteratorSugar.iterate(self.iterator(), func);
    }
}
