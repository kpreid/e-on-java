package org.erights.e.elib.ref;

// Copyright 2007 Kevin Reid under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.tables.Equalizer;
import org.erights.e.elib.tables.NotSettledException;

import java.io.IOException;

/**
 * A remote promise; a reference which may resolve to another and has a handler
 * which forwards messages to its eventual resolution.
 *
 * @author Kevin Reid
 */
public final class RemotePromise2 extends Proxy {

    RemotePromise2(Object handler, Object resolutionBox)
      throws NotSettledException {
        super(handler, resolutionBox);
    }

    public boolean equals(Object other) {
        // super will check that we can't have jettisoned yet
        return super.equals(other) && Equalizer.make()
          .isSameYet(myResolutionBox, ((RemotePromise2)other).myResolutionBox);
    }

    protected boolean isResolvedIfNotForwarding() {
        return false;
    }

    protected void __printOnIfNotForwarding(TextWriter out)
      throws IOException {
        out.write("<Remote Promise>");
    }
}
