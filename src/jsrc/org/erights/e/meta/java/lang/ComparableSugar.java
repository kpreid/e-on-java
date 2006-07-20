// Copyright 2004 Hewlett Packard, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.meta.java.lang;

/**
 * @author Mark S. Miller
 */
public class ComparableSugar {

    /**
     * prevents instantiation
     */
    private ComparableSugar() {
    }

    /**
     * This should really be handled by a .safej renaming
     *
     * @return
     */
    static public int op__cmp(Comparable self, Object other) {
        return self.compareTo(other);
    }
}
