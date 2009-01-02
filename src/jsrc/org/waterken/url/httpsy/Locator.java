// Copyright 2004-2005 Waterken Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html
package org.waterken.url.httpsy;

/**
 * An HTTPSY site locator.
 *
 * @author Tyler
 */
public final class Locator {

    /**
     * The default HTTPSY port = <code>80</code>.
     */
    static public final int DEFAULT_PORT = 80;

    private Locator() {
    }

    /**
     * The instance.
     */
    static private final org.waterken.url.Locator INSTANCE = org.waterken
      .url
      .tls
      .Locator
      .make(org.waterken.url.conforming.Locator.make(DEFAULT_PORT),
            org.waterken.url.tls.Nobody.make());

    /**
     * Constructs an HTTPSY locator.
     */
    static public org.waterken.url.Locator make() {
        return INSTANCE;
    }
}
