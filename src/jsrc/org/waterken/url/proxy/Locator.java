// Copyright 2004-2005 Waterken Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html
package org.waterken.url.proxy;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * An HTTP proxy client.
 *
 * @author Tyler
 */
public final class Locator
  implements org.waterken.url.Locator, java.io.Serializable {

    static private final long serialVersionUID = 7158316475308008071L;

    private org.waterken.url.Locator network;   // The underlying network.
    private String address;                     // The proxy address.

    private Locator() {
    }

    private Locator(final org.waterken.url.Locator network,
                    final String address) {
        this.network = network;
        this.address = address;
    }

    /**
     * Constructs a <code>Locator</code>.
     *
     * @param network The underlying network.
     * @param address The proxy address.
     */
    static public org.waterken.url.Locator make(final org.waterken.url.Locator network,
                                                final String address) {
        return new Locator(network, address);
    }

    // org.waterken.url.Locator interface.

    /**
     * Opens a connection to the proxy.
     *
     * @param _           Ignored.
     * @param most_recent The most recent address of the target host.
     */
    public Socket locate(final String _, final SocketAddress most_recent)
      throws IOException {
        return network.locate(address, most_recent);
    }
}
