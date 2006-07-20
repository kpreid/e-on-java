// Copyright 2002-2005 Waterken Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html
package org.waterken.url.http;

import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.Map;

/**
 * An HTTP URL handler.
 *
 * @author Tyler
 */
public class Handler extends URLStreamHandler {

    /**
     * The site locator.
     */
    protected org.waterken.url.Locator locator;

    /**
     * The assumed server HTTP version.
     */
    protected String assumed_http_version;

    /**
     * The active sessions: [ peer identifier =&gt; soft session ]
     */
    protected Map sessions = new java.util.HashMap();

    /**
     * The garbage collected sessions.
     */
    protected ReferenceQueue gced = new ReferenceQueue();

    protected Handler(final org.waterken.url.Locator locator,
                      final String assumed_http_version) {
        this.locator = locator;
        this.assumed_http_version = assumed_http_version;
    }

    public Handler() {
        this(Locator.make(), "HTTP/1.0");
    }

    // java.net.URLStreamHandler interface.

    protected URLConnection openConnection(final URL url) throws IOException {
        Session session;
        synchronized (sessions) {
            // Remove any GCed sessions.
            while (true) {
                final Reference r = gced.poll();
                if (null == r) {
                    break;
                }
                sessions.values().remove(r);
            }

            // Get the cached session.
            final String peer = home(url);
            final Reference sr = (Reference)sessions.get(peer);
            session = (Session)(null != sr ? sr.get() : null);
            if (null == session) {
                session = new Session(locator, assumed_http_version, false);
                sessions.put(peer,
                             new java.lang.ref.SoftReference(session, gced));
            }
        }
        return session.request(url);
    }

    /**
     * @return {@link Locator#DEFAULT_PORT}.
     */
    protected int getDefaultPort() {
        return Locator.DEFAULT_PORT;
    }

    // org.waterken.url.http.Handler interface.

    /**
     * Gets the remote peer identifier.
     *
     * @param url The target resource.
     */
    protected String home(final URL url) throws IOException {
        int port = url.getPort();
        if (port == -1) {
            port = getDefaultPort();
        }
        return url.getHost().toLowerCase() + ":" + port;
    }
}
