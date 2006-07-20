// Copyright 2003 Hewlett Packard, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.elib.prim;

import org.erights.e.elib.base.Script;

/**
 * Those things that live in a VTable
 *
 * @author Mark S. Miller
 */
public interface VTableEntry extends Script {

    /**
     * Return a variant of this Script which will be stored in the given
     * vTable.
     * <p/>
     * It should extract information from the vTable that would allow its
     * {@link Script#canHandle} method to give a quick but safe answer.
     *
     * @return
     */
    public abstract VTableEntry forVTable(VTable vTable);
}
