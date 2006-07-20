// Copyright 2002-2005 Waterken Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html
package org.waterken.url.httpsy;

import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;

/**
 * An HTTPSY URL handler.
 *
 * @author Tyler
 */
public final class Handler extends org.waterken.url.http.Handler {

    /**
     * Constructs a <code>Handler</code>.
     */
    public Handler() {
        super(Locator.make(), "HTTP/1.1");
    }

    // java.net.URLStreamHandler interface.

    protected void parseURL(final URL u,
                            final String spec,
                            int start,
                            final int limit) {
        // Parse the authority component if present.
        if (spec.startsWith("//", start)) {
            // Parse the authority.
            final int start_authority = start + "//".length();
            start = spec.indexOf('/', start_authority);
            if (start < 0 || start > limit) {
                start = spec.indexOf('?', start_authority);
                if (start < 0 || start > limit) {
                    start = limit;
                }
            }
            final String authority = spec.substring(start_authority, start);

            // Parse the userinfo.
            final int end_userinfo = authority.indexOf('@');
            final String userinfo = end_userinfo < 0 ?
              authority :
              authority.substring(0, end_userinfo);

            setURL(u,
                   u.getProtocol(),
                   "localhost",
                   -1,
                   authority,
                   userinfo,
                   u.getPath(),
                   u.getQuery(),
                   u.getRef());
        }

        // Delegate the rest to the parent implementation.
        super.parseURL(u, spec, start, limit);
    }

    /**
     * @return {@link Locator#DEFAULT_PORT}.
     */
    protected int getDefaultPort() {
        return Locator.DEFAULT_PORT;
    }

    // org.waterken.url.http.Handler interface.

    protected String home(final URL url) throws IOException {
        return URLDecoder.decode(url.getUserInfo(), "US-ASCII").toLowerCase();
    }
}
