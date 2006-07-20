// Copyright 2004-2005 Waterken Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html
package org.waterken.url;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * A site locator.
 *
 * @author Tyler
 */
public interface Locator {

    /**
     * Opens a socket to the identified host.
     *
     * @param authority   The URL authority identifying the target host.
     * @param most_recent The most recent location, or <code>null</code> if not
     *                    known.
     * @return An open socket to the host.
     */
    Socket locate(String authority, SocketAddress most_recent)
      throws IOException;
}
