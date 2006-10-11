// Copyright 2004-2005 Waterken Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html
package org.waterken.url.dns;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * A DNS site locator.
 *
 * @author Tyler
 */
public final class Locator implements org.waterken.url.Locator,
  java.io.Serializable {

    static private final long serialVersionUID = 1616824535748530028L;

    /**
     * The default port.
     */
    private int default_port;

    private Locator() {
    }

    private Locator(final int default_port) {
        this.default_port = default_port;
    }

    /**
     * Constructs a <code>Locator</code>.
     *
     * @param default_port The default port.
     */
    public static org.waterken.url.Locator make(final int default_port) {
        return new Locator(default_port);
    }

    // org.waterken.url.Locator interface.

    /**
     * Locates a site based on an HTTP locator.
     *
     * @param authority The HTTP locator.
     */
    public Socket locate(final String authority,
                         final SocketAddress most_recent) throws IOException {
        Socket r;
        try {
            // Try the most recent location.
            r = new Socket();
            r.connect(most_recent);
        } catch (final Exception _) {
            // Parse the authority.
            final int end_userinfo = authority.indexOf('@');
            final int start_host = -1 == end_userinfo ? 0 : end_userinfo + 1;
            final int end_host = authority.indexOf(':', start_host);
            final String host = -1 == end_host ?
              authority.substring(start_host) :
              authority.substring(start_host, end_host);
            final int port = -1 == end_host ? default_port : (end_host + 1 ==
              authority.length() ?
              default_port :
              Integer.parseInt(authority.substring(end_host + 1)));

            // Search the DNS for an address.
            final InetAddress[] address = InetAddress.getAllByName(host);
            for (int i = 0; true;) {
                try {
                    r = new Socket(address[i], port);
                    break;
                } catch (final IOException e) {
                    if (++i == address.length) {
                        throw e;
                    }
                }
            }
        }
        return r;
    }
}
