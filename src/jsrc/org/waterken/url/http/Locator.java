// Copyright 2004 Waterken Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html
package org.waterken.url.http;

/**
 * An HTTP site locator.
 *
 * @author Tyler
 */
public final class Locator {

    /**
     * The default HTTP port = <code>80</code>.
     */
    public static final int DEFAULT_PORT = 80;

    private Locator() {
    }

    private static final org.waterken.url.Locator INSTANCE;

    static {
        org.waterken.url.Locator locator = org.waterken.url.dns.Locator.make(
          DEFAULT_PORT);

        // Check for proxy settings.
        final String host = System.getProperty("http.proxyHost");
        if (null != host) {
            final String port = System.getProperty("http.proxyPort");
            locator = org.waterken.url.proxy.Locator.make(locator, null !=
                                                                   port ?
                                                                   host + ":" +
                                                                   port :
                                                                   host);
        }

        INSTANCE = locator;
    }

    /**
     * Constructs an HTTP locator.
     */
    public static org.waterken.url.Locator make() {
        return INSTANCE;
    }
}
