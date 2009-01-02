// Copyright 2004-2005 Waterken Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html
package org.waterken.url.amp;

import org.waterken.uri.Base32;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.SecureRandom;

/**
 * A simple client interface to an HTTP web-calculus application.
 *
 * @author Tyler
 */
public final class AMP {

    private AMP() {
    }

    // org.waterken.url.amp.AMP interface.

    /**
     * Generates a message identifier for an {@link #invoke invocation}.
     *
     * @param entropy A random number generator.
     * @return The generated <code>mid</code> value.
     */
    static public String request(final SecureRandom entropy) {
        final byte[] bytes = new byte[16];
        entropy.nextBytes(bytes);
        return Base32.encode(bytes);
    }

    /**
     * Calculates the pipeline URL for an {@link #invoke invocation}.
     *
     * @param cap The capability URL for the target resource.
     * @param mid The {@link #request message identifier} for the request.
     * @return The pipelined return value capability URL.
     */
    static public String pipeline(final String cap, final String mid) {
        try {
            return cap.substring(0, cap.lastIndexOf('/') + 1) +
              Base32.encode(MessageDigest.getInstance("SHA-1").digest(mid.getBytes(
                "US-ASCII")));
        } catch (final java.io.UnsupportedEncodingException e) {
            // Should never happen.
            // US-ASCII is a required charset.
            throw new UndeclaredThrowableException(e);
        } catch (final java.security.NoSuchAlgorithmException e) {
            // Should never happen.
            // SHA-1 is a required algorithm.
            throw new UndeclaredThrowableException(e);
        }
    }

    /**
     * Makes a synchronous <code>POST</code> request.
     * <p/>
     * This method is only useful for invoking a closure that returns another
     * closure. There is no support for parsing any return node. </p>
     *
     * @param cap  The capability URL for the target resource.
     * @param mid  The {@link #request message identifier} for the request.
     * @param args The XML encoding of the invocation arguments.
     * @return The return value capability URL.
     * @throws FileNotFoundException The target resource no longer exists.
     * @throws HTTPException         An unexpected HTTP response code.
     * @throws IOException           An I/O error while writing the request or
     *                               reading the response.
     */
    static public String invoke(final String cap,
                                final String mid,
                                final byte[] args)
      throws FileNotFoundException, HTTPException, IOException {
        // Send the invocation.
        final URL target = new URL(cap + "?mid=" + mid);
        final HttpURLConnection connection =
          (HttpURLConnection)target.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/xml");
        connection.setRequestProperty("Content-Length", "" + args.length);
        final OutputStream out = connection.getOutputStream();
        out.write(args);
        out.flush();
        out.close();

        // Get the return value.
        String r;
        switch (connection.getResponseCode()) {
        case HttpURLConnection.HTTP_OK:
        case HttpURLConnection.HTTP_ACCEPTED:
        case HttpURLConnection.HTTP_NO_CONTENT:
        case HttpURLConnection.HTTP_RESET:
            r = pipeline(cap, mid);
            break;
        case HttpURLConnection.HTTP_CREATED:
        case HttpURLConnection.HTTP_SEE_OTHER:
            r = connection.getHeaderField("Location");
            break;
        case HttpURLConnection.HTTP_MOVED_PERM:
            r = invoke(connection.getHeaderField("Location"), mid, args);
            break;
        case HttpURLConnection.HTTP_NOT_FOUND:
        case HttpURLConnection.HTTP_GONE:
            throw new FileNotFoundException(connection.getResponseMessage());
        default:
            throw new HTTPException(connection.getResponseCode(),
                                    connection.getResponseMessage());
        }
        return r;
    }
}
