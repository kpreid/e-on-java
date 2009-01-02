// Copyright 2004-2005 Waterken Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html
package org.waterken.url.tunnel;

import org.waterken.url.http.LineInput;

import java.io.IOException;
import java.io.OutputStream;
import java.net.NoRouteToHostException;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * RFC 2817 connection tunneling.
 *
 * @author Tyler
 */
public final class Locator
  implements org.waterken.url.Locator, java.io.Serializable {

    static private final long serialVersionUID = 5008216486101224538L;

    /**
     * The proxy locator.
     */
    private org.waterken.url.Locator proxy;

    /**
     * The default port.
     */
    private int default_port;

    private Locator() {
    }

    private Locator(final org.waterken.url.Locator proxy,
                    final int default_port) {
        this.proxy = proxy;
        this.default_port = default_port;
    }

    /**
     * Constructs a <code>Locator</code>.
     *
     * @param proxy        The proxy locator.
     * @param default_port The default port.
     */
    static public org.waterken.url.Locator make(final org.waterken.url.Locator proxy,
                                                final int default_port) {
        return new Locator(proxy, default_port);
    }

    // org.waterken.url.Locator interface.

    /**
     * Locates a site based on an HTTP locator.
     *
     * @param authority The HTTP locator.
     */
    public Socket locate(String authority, final SocketAddress most_recent)
      throws IOException {

        final Socket r = proxy.locate(authority, most_recent);

        // Canonicalize the authority.
        final int end_host = authority.indexOf(':');
        if (-1 == end_host) {
            authority += ":" + default_port;
        } else if (authority.length() - 1 == end_host) {
            authority += default_port;
        }

        // Write out the CONNECT request.
        final OutputStream out = r.getOutputStream();
        out.write("CONNECT ".getBytes("US-ASCII"));
        out.write(authority.getBytes("US-ASCII"));
        out.write(" HTTP/1.0\r\n".getBytes("US-ASCII"));
        out.write("\r\n".getBytes("US-ASCII"));
        out.flush();

        // Read in the CONNECT response.
        final LineInput in = new LineInput(r.getInputStream(), 128);
        final String line = in.readln();

        // Parse the Status-Line.
        final int begin_http_version = 0;
        int end_http_version = "HTTP/1.".length();
        while (-1 == " \t".indexOf(line.charAt(end_http_version))) {
            ++end_http_version;
        }
        final String http_version =
          line.substring(begin_http_version, end_http_version);
        int begin_status_code = end_http_version + 1;
        while (-1 != " \t".indexOf(line.charAt(begin_status_code))) {
            ++begin_status_code;
        }
        int end_status_code = begin_status_code + 1;
        while (end_status_code != line.length() &&
          -1 == " \t".indexOf(line.charAt(end_status_code))) {
            ++end_status_code;
        }
        final int status_code =
          Integer.parseInt(line.substring(begin_status_code, end_status_code));

        // Any 2xx response indicates a successful tunnel.
        if (200 > status_code || 300 <= status_code) {
            throw new NoRouteToHostException();
        }

        // Discard the remaining headers.
        while (!"".equals(in.readln())) {
        }

        return r;
    }
}
