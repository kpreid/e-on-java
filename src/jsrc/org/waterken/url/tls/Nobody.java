// Copyright 2004-2005 Waterken Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html
package org.waterken.url.tls;

/**
 * An unauthenticated {@link Host}.
 *
 * @author Tyler
 */
public final class Nobody {

    private Nobody() {
    }

    /**
     * The instance.
     */
    static private final Host INSTANCE =
      new Host(org.waterken.url.tls.sha1withrsa.Keyspace.make(),
               null,
               new javax.net.ssl.KeyManager[]{});

    /**
     * Constructs a <code>Nobody</code>.
     */
    static public Host make() {
        return INSTANCE;
    }
}
