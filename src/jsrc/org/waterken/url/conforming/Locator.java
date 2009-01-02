// Copyright 2004 Waterken Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html
package org.waterken.url.conforming;

/**
 * A {@link org.waterken.url.Locator} that conforms to the local network
 * architecture.
 *
 * @author Tyler
 */
public final class Locator {

    private Locator() {
    }

    /**
     * Constructs a conforming locator.
     *
     * @param default_port The default port.
     */
    static public org.waterken.url.Locator make(final int default_port) {
        final org.waterken.url.Locator http =
          org.waterken.url.http.Locator.make();
        return http instanceof org.waterken.url.proxy.Locator ?
          org.waterken.url.tunnel.Locator.make(http, default_port) :
          org.waterken.url.dns.Locator.make(default_port);
    }
}
