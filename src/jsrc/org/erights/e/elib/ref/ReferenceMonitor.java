// Copyright 2004 Hewlett Packard, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.elib.ref;

/**
 * Every time a new outgoing EProxy is created on one side of a membrane, a
 * ReferenceMonitor is given the opportunity to wrap the underlying handler so
 * as to examine or intervene in the messages leaving that side of the
 * membrane.
 * <p/>
 * The {@link NullMonitor} always just returns the underlying handler.
 *
 * @author Mark S. Miller
 */
public interface ReferenceMonitor {

    /**
     * When a new underlying handler is created to represent an outgoing
     * reference on one side of a membrane, that side's reference monitor is
     * asked to provide a wrapper for that handler to be used in its stead.
     *
     * @return
     */
    EProxyHandler wrap(EProxyHandler underlying);
}
