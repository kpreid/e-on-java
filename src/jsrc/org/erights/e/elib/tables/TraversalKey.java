package org.erights.e.elib.tables;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.elib.prim.E;
import org.erights.e.elib.ref.Ref;

/**
 * Wraps a possibly unsettled base reference, in order to be able to use it as
 * key in an EMap for purposes of traversal.
 * <p/>
 * XXX This class should probably be extended to allow the traversal needed by
 * serialization, but without revealing any encapsulated authority, as
 * explained in the Future Work section of
 * <a href= "http://www.erights.org/data/serial/jhu-paper/index.html"
 * >Safe Serialization Under Mutual Suspicion</a>.
 *
 * @author Mark S. Miller
 * @see <a href="http://www.erights.org/elib/equality/same-ref.html" >Reference
 *      Sameness</a>
 */
public class TraversalKey {

    private final Object myWrapped;

    private final int mySnapHash;

    /**
     * We capture the EQ pointers of these promises, after all forwardings at
     * the time they were captured, but without following any forwardings since
     * then.
     * <p/>
     * This works below the level of E's semantics. On a VM in which E's
     * semantics are more primitively supported, such as one in which promise
     * forwarding is handled automatically, TraversalKey may need a completely
     * different implementation in order to have the same E-level semantics.
     */
    private final FringeNode[] myFringe;

    public TraversalKey(Object wrapped) {
        myWrapped = Ref.resolution(wrapped);
        FlexList fringeBuild = FlexList.fromType(FringeNode.class);
        mySnapHash = Equalizer.sameYetHash(myWrapped, fringeBuild);
        myFringe = (FringeNode[])fringeBuild.getArray();
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof TraversalKey)) {
            return false;
        }
        TraversalKey other = (TraversalKey)obj;

        // Quick exit case.
        if (mySnapHash != other.mySnapHash) {
            return false;
        }

        // In order for two TraversalKeys to be the same, their values must be
        // the same now...
        if (!Equalizer.isSameYet(myWrapped, other.myWrapped)) {
            return false;
        }

        // ..and have been the same then, which is determined by checking
        // whether they had the same promises in their structure in the same
        // places.
        FringeNode[] otherFringe = other.myFringe;
        int len = myFringe.length;
        if (otherFringe.length != len) {
            return false;
        }
        for (int i = 0; i < len; i++) {
            if (!myFringe[i].equals(otherFringe[i])) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        return mySnapHash;
    }

    public String toString() {
        return "<key:" + E.toString(myWrapped) + ">";
    }
}
