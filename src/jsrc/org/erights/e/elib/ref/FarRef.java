package org.erights.e.elib.ref;

// Copyright 2007 Kevin Reid under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.slot.FinalSlot;
import org.erights.e.elib.tables.Equalizer;
import org.erights.e.elib.tables.NotSettledException;
import org.erights.e.elib.tables.TraversalKey;

import java.io.IOException;

/**
 * A resolved Proxy; one which can become broken but not another reference.
 *
 * @author Kevin Reid
 */
final class FarRef extends Proxy {

    private TraversalKey myResolutionIdentity;

    FarRef(Object handler, Object resolutionBox) throws NotSettledException {
        super(handler, resolutionBox);
        myResolutionIdentity = new TraversalKey(resolutionBox);
    }

    protected void commit() {
        Object handler = myHandler; // will be discarded by super.commit

        super.commit();

        {
            Object resolution = ((FinalSlot)myResolutionBox).get();

            if (!(Ref.isBroken(resolution))) {
                resolution = Ref.broken(E.asRTE(
                  "Attempt to resolve a Far ref handled by " +
                    E.toQuote(handler) + " to another identity (" +
                    E.toQuote(resolution) + ")."));
            }

            // A FarRef can only resolve to a DisconnectedRef to preserve its
            // identity
            resolution = new DisconnectedRef(handler,
                                              myResolutionIdentity,
                                              Ref.optProblem(resolution));

            myResolutionBox = new FinalSlot(resolution);
        }

        myResolutionIdentity = null;
    }

    public boolean equals(Object obj) {
        // super will check that we can't have jettisoned yet
        return super.equals(obj) && Equalizer.
          isSameYet(myResolutionIdentity, ((FarRef)obj).myResolutionIdentity);
    }

    protected boolean isResolvedIfNotForwarding() {
        return true;
    }

    protected void __printOnIfNotForwarding(TextWriter out)
      throws IOException {
        out.write("<Far ref>");
    }
}
