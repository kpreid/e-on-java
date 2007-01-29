// Copyright 2004-2005 Waterken Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html
package org.waterken.url.tls;

import org.waterken.uri.Authority;
import org.waterken.uri.Base32;
import org.waterken.url.http.Session;
import org.waterken.url.http.UpgradeProtocol;

import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSocket;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.net.InetSocketAddress;
import java.net.NoRouteToHostException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * A {@link Host} locator.
 *
 * @author Tyler
 */
public final class Locator
  implements org.waterken.url.Locator, java.io.Serializable {

    static private final long serialVersionUID = 9148342181345156610L;

    /**
     * The underlying network.
     */
    private org.waterken.url.Locator network;

    /**
     * The client to authenticate as.
     */
    private Host client;

    private Locator() {
    }

    private Locator(final org.waterken.url.Locator network,
                    final Host client) {
        this.network = network;
        this.client = client;
    }

    /**
     * Constructs a <code>Locator</code>.
     *
     * @param network The underlying network.
     * @param client  The client to authenticate as.
     */
    public static org.waterken.url.Locator make(final org.waterken.url.Locator network,
                                                final Host client) {
        return new Locator(network, client);
    }

    // org.waterken.url.Locator interface.

    /**
     * Locates a site based on a YURL authority.
     */
    public Socket locate(final String authority,
                         final SocketAddress most_recent) throws IOException {

        // Extract the authority components.
        final String key_id = Authority.fingerprint(authority);
        final String[] hint = Authority.hint(authority);

        // Establish a connection.
        final Race race = new Race(key_id);

        // Try reconnecting to the most recent site.
        try {
            final InetSocketAddress addr = (InetSocketAddress)most_recent;
            race.start("http://" + addr.getAddress().getHostAddress() + ":" +
              addr.getPort() + "/id/" + key_id, 0);
        } catch (final Exception _) {
        }

        // Use the hints to locate the server.
        for (int i = hint.length; 0 != i--;) {
            race.start("http://" + hint[i] + "/id/" + key_id, 1);
        }

        return race.judge();
    }

    /**
     * Simple random number generator for load balancing.
     */
    private static final Random entropy = new Random();

    private final class Race {

        /**
         * The key identifier.
         */
        private final String key_id;

        /**
         * The URL strings that have already been tried.
         */
        private final Set tried = Collections.synchronizedSet(new HashSet());

        /**
         * The number of remaining candidates.
         */
        private int candidates;

        /**
         * The first successfully authenticated host.
         */
        private volatile SSLSocket winner;

        private Race(final String key_id) {
            this.key_id = key_id;
        }

        /**
         * Attempts a TLS/1.0 upgrade.
         *
         * @param target The root of the search path.
         * @param depth  The maximum redirection depth.
         */
        synchronized void start(final String target, final int depth) {
            ++candidates;
            new Thread(new Runnable() {
                public void run() {
                    try {
                        final SSLSocket s = _connect(target, depth);
                        synchronized (Race.this) {
                            if (null == winner) {
                                winner = s;
                                Race.this.notify();
                            } else {
                                s.close();
                            }
                        }
                    } catch (final Exception _) {
                    } finally {
                        synchronized (Race.this) {
                            if (0 == --candidates) {
                                Race.this.notify();
                            }
                        }
                    }
                }
            }).start();
        }

        private SSLSocket _connect(final String target, final int depth) throws
          IOException, NoSuchAlgorithmException, KeyManagementException {

            // Check that the race is still on.
            if (null != winner) {
                return null;
            }

            // Check redirect depth.
            if (0 > depth) {
                throw new IOException("Too many redirects");
            }

            // Check that the target hasn't already been tried.
            if (!tried.add(target)) {
                throw new IOException("Already tried");
            }

            // Build connection.
            final Session session = new Session(network, "HTTP/1.0", true);
            final Session.Connection connection =
              session.request(new URL(target));

            // Setup the upgrade request.
            connection.setRequestProperty("accept", "text/uri-list");
            connection.setRequestProperty("accept-charset", "US-ASCII");
            connection.setRequestProperty("upgrade", "TLS/1.0");
            connection.addRequestProperty("connection", "upgrade");

            // Attempt the upgrade.
            SSLSocket r;
            try {
                connection.connect();

                // Upgrade not accepted, check for redirection.
                String[] redirect = new String[8];
                int redirect_size = 0;
                try {
                    final int status_code = connection.getResponseCode();
                    if (300 == status_code) {
                        // Check for URI list in response body.
                        final String content_type =
                          connection.getContentType();
                        if (null != content_type &&
                          content_type.startsWith("text/uri-list")) {
                            // Build the redirect list.
                            final BufferedReader in =
                              new BufferedReader(new InputStreamReader(
                                connection.getInputStream(),
                                "US-ASCII"));
                            for (String s = in.readLine();
                                 null != s;
                                 s = in.readLine()) {
                                if (!s.startsWith("#")) {
                                    if (redirect.length == redirect_size) {
                                        System.arraycopy(redirect,
                                                         0,
                                                         redirect = new String[
                                                           2 * redirect_size],
                                                         0,
                                                         redirect_size);
                                    }
                                    redirect[redirect_size++] = s;
                                }
                            }
                            in.close();
                        } else {
                            // Redirect to preferred Location.
                            redirect[redirect_size++] =
                              connection.getHeaderField("Location");
                        }
                    } else if (300 < status_code && 400 > status_code) {
                        // Redirect to Location.
                        redirect[redirect_size++] =
                          connection.getHeaderField("Location");
                    } else {
                        // No redirect provided.
                        throw new SSLPeerUnverifiedException(
                          "Upgrade not accepted.");
                    }
                } finally {
                    connection.disconnect();
                }

                // Randomly search the redirect list for the identified host.
                while (true) {
                    if (0 == redirect_size) {
                        throw new NoRouteToHostException();
                    }

                    final int i = entropy.nextInt(redirect_size);
                    try {
                        r = _connect(redirect[i], depth - 1);
                        break;
                    } catch (final Exception _) {
                    }
                    System.arraycopy(redirect,
                                     i + 1,
                                     redirect,
                                     i,
                                     redirect_size - (i + 1));
                    --redirect_size;
                }
            } catch (final UpgradeProtocol e) {
                // Do the upgrade.
                r = null;
                final Socket plain = e.getSocket();
                try {
                    final SSLSocket ssl = (SSLSocket)client.getFactory()
                      .createSocket(plain,
                                    plain.getInetAddress().getHostAddress(),
                                    plain.getPort(),
                                    true);

                    // Restrict the acceptable ciphersuites.
                    ssl.setEnabledCipherSuites(new String[]{
                      "TLS_DHE_RSA_WITH_AES_128_CBC_SHA",
                      "TLS_DHE_DSS_WITH_AES_128_CBC_SHA",
                      "TLS_RSA_WITH_AES_128_CBC_SHA",
                      "SSL_DHE_RSA_WITH_3DES_EDE_CBC_SHA",
                      "SSL_DHE_DSS_WITH_3DES_EDE_CBC_SHA",
                      "SSL_RSA_WITH_3DES_EDE_CBC_SHA"});

                    // Initiate the TLS handshake and verify the signing key
                    // hash.
                    final Certificate[] chain = ssl.getSession()
                      .getPeerCertificates();
                    MessageDigest hash;
                    switch (key_id.length()) {
                    case 32:
                        hash = MessageDigest.getInstance("SHA-1");
                        break;
                    default:
                        throw new SSLPeerUnverifiedException(
                          "Unknown message digest algorithm.");
                    }
                    int i = chain.length;
                    while (0 != i-- &&
                      !key_id.equals(Base32.encode(hash.digest(chain[i].getPublicKey().getEncoded())))) {
                    }
                    if (0 > i) {
                        throw new SSLPeerUnverifiedException(
                          "Wrong peer certificate.");
                    }

                    // If remaining in the HTTP protocol, suck out the response.
                    final String[] protocol = e.getProtocol();
                    for (int j = protocol.length; 0 != j--;) {
                        if (protocol[j].startsWith("HTTP/")) {
                            connection.resume(ssl);
                            InputStream in = connection.getErrorStream();
                            if (null == in) {
                                in = connection.getInputStream();
                            }
                            in.close();
                            break;
                        }
                    }

                    r = ssl;
                } finally {
                    if (null == r) {
                        plain.close();
                    }
                }
            }
            return r;
        }

        synchronized SSLSocket judge() throws IOException {
            if (null == winner && 0 != candidates) {
                try {
                    wait();
                } catch (final InterruptedException _) {
                    throw new InterruptedIOException();
                }
            }
            if (null == winner) {
                throw new FileNotFoundException();
            }
            return winner;
        }
    }
}
