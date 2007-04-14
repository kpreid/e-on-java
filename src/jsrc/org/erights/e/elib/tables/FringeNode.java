// Copyright 2007 Kevin Reid, under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.elib.tables;

/**
 * Entries in a TraversalKey's fringe.
 * <p/>
 * Each FringeNode stores the identity of one promise and the location at which
 * it was found in a structure.
 *
 * @author Kevin Reid
 */
class FringeNode {

    private final Object myIdentity;
    private final FringePath myPath;

    FringeNode(Object identity, FringePath path) {
        myIdentity = identity;
        myPath = path;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof FringeNode)) {
            return false;
        }
        FringeNode other = (FringeNode)obj;

        return myIdentity == other.myIdentity &&
          FringePath.equals(myPath, other.myPath);
    }

    public int hashCode() {
        // Besides being consistent with equals(), this is used by sameness
        // hashing.
        return System.identityHashCode(myIdentity) ^
          FringePath.hashCode(myPath);
    }
}