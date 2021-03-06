package net.captp.jcomm;

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

import org.erights.e.develop.assertion.T;
import org.erights.e.elib.ref.Ref;

/**
 * The re-encoding of an exported reference. <p>
 * <p/>
 * Will be decoded into an already existing imported remote reference.
 *
 * @author Chip Morningstar
 * @author Mark S. Miller
 */
class ImportDesc implements ObjectRefDesc {

    static private final long serialVersionUID = 5581130955096436339L;

    private final int myImportPos;

    /**
     *
     */
    private void validate() {
        T.requireSI(1 <= myImportPos, "must be positive: ", myImportPos);
    }

    /**
     * Constructor.
     */
    ImportDesc(int importPos) {
        myImportPos = importPos;
        validate();
    }

    /**
     * What the other side exported, we dereference as what we imported.
     * <p/>
     * Increments the wireCount
     */
    public Object dereference(CapTPConnection conn) {
        validate();
        Ref result = conn.getImport(myImportPos);

        if (conn.debug(myImportPos)) {
            conn.debugm(myImportPos, "deref " + toString());
        }
        return result;
    }

    /**
     *
     */
    public String toString() {
        return "ImportDesc(" + myImportPos + ")";
    }
}
