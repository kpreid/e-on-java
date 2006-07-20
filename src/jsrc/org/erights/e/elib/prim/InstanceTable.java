// Copyright 2003 Hewlett Packard, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.elib.prim;

import org.erights.e.elib.base.ClassDesc;

/**
 * @author Mark S. Miller
 */
public class InstanceTable extends VTable {

    private final Class myDirectClass;

    /**
     *
     * @param directClass
     */
    InstanceTable(Class directClass) {
        super(ClassDesc.sig(directClass));
        myDirectClass = directClass;
    }

    /**
     * @return
     */
    public Class getDirectClass() {
        return myDirectClass;
    }
}
