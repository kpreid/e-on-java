// Copyright 2003 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.elang.evm;

import org.erights.e.elib.prim.VTable;

/**
 * Overrides VTable in order to remember the ObjectExpr which created it.
 * <p/>
 * This is probably useful for several reasons, but the reason that caused us
 * to create this class is to prepare for serializing E objects, whether for
 * persistence or passing by copy. The ObjectExpr has the overall ScopeLayout
 * and StaticScope, which are needed to capture exactly those outers used
 * freely by this object. Should these be serialized, or should we refuse to
 * serialize an object that has outers not in the univeralScope or safeScope?
 *
 * @author Mark S. Miller
 */
class EMethodTable extends VTable {

    /**
     *
     */
    private final ObjectExpr myObjExpr;

    /**
     * @param objExpr
     */
    EMethodTable(ObjectExpr objExpr) {
        super(objExpr.getFQName());
        myObjExpr = objExpr;
    }

    /**
     * @return
     */
    public ObjectExpr getObjExpr() {
        return myObjExpr;
    }
}
