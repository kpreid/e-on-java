// Copyright 2003 Hewlett Packard, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.elib.prim;


/**
 * @author Mark S. Miller
 */
public class StaticTable extends VTable {

    private final StaticMaker mySelf;

    /**
     *
     * @param self
     */
    StaticTable(String fqName, StaticMaker self) {
        super(fqName);
        mySelf = self;
    }

    /**
     * @return
     */
    public StaticMaker getSelf() {
        return mySelf;
    }
}
