// Copyright 2004 Hewlett Packard, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.elib.ref;

/**
 * The degenerate reference monitor that never examines or intervenes.
 *
 * @author Mark S. Miller
 */
public final class NullMonitor implements ReferenceMonitor {

    static public final ReferenceMonitor THE_ONE = new NullMonitor();

    private NullMonitor() {
    }

    /**
     * Always just returns the underlying handler.
     */
    public EProxyHandler wrap(EProxyHandler underlying) {
        return underlying;
    }
}
