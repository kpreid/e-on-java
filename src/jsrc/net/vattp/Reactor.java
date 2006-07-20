// Copyright 2004-2005 Waterken Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html
package net.vattp;

/**
 * Handle notification of new {@link Connection}s.
 *
 * @author Tyler
 */
public interface Reactor {

    /**
     * Notification of {@link Connection} creation.
     * <p/>
     * No records have yet been queued for sending on the connection. </p>
     *
     * @param connection The new connection.
     * @return The record handler.
     */
    Handler run(Connection connection);
}
