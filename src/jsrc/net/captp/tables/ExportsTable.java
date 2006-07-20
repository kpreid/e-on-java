package net.captp.tables;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.elib.prim.E;
import org.erights.e.elib.serial.PassByProxyGuard;
import org.erights.e.elib.tables.IntTable;

/**
 *
 */
public class ExportsTable extends CommTable {

    /**
     * Gets the index for a near export.
     */
    private IntTable myPBPMap;

    /**
     *
     */
    public ExportsTable() {
        myPBPMap = new IntTable();
    }

    /**
     *
     */
    public void smash(Throwable problem) {
        for (int i = 1; i < myCapacity; i++) {
            if (!isFree(i)) {
                E.sendOnly(myStuff[i], "__reactToLostClient", problem);
            }
        }
        super.smash(problem);
        myPBPMap = null;
    }

    /**
     * Frees the index, including its entry, if any, in the pbp map.
     */
    public void free(int index) {
        myPBPMap.removeKey(myStuff[index]);
        super.free(index);
    }

    /**
     *
     */
    public int indexFor(Object obj) {
        return myPBPMap.getInt(obj, -1);
    }

    /**
     * Allocates and returns the index of a newly exported local
     * PassByProxy object.
     * <p>
     * The wireCount is initialized to one
     *
     * @param pbp The local PassByProxy object to be exported
     * @return The index of the FarRef to be created-imported on the
     *         other end.
     */
    public int newFarPos(Object pbp) {
        pbp = PassByProxyGuard.THE_ONE.coerce(pbp, null);
        int index = bind(pbp);
        myPBPMap.putInt(pbp, index, true);
        return index;
    }
}
