// Copyright 2004-2005 Waterken Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html
package org.waterken.url.tls;

import org.waterken.url.http.LineInput;
import org.waterken.url.http.TokenList;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URLDecoder;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collections;

/**
 * A TLS/1.0 host.
 *
 * @author Tyler
 */
public final class Host implements java.io.Serializable {

    static private final long serialVersionUID = -5803768622635370747L;

    /**
     * The keyspace.
     */
    private Keyspace keyspace;

    /**
     * The {@link Keyspace#identify fingerprint}.
     */
    private String fingerprint;

    /**
     * The client authentication key manager.
     */
    private KeyManager[] key_manager;

    private Host() {
        key_manager = new KeyManager[]{};
    }

    /**
     * Constructs a <code>Host</code>.
     *
     * @param keyspace    The keyspace.
     * @param fingerprint The {@link Keyspace#identify fingerprint}.
     * @param key_manager The client authentication key manager.
     */
    public Host(final Keyspace keyspace,
                final String fingerprint,
                final KeyManager[] key_manager) {
        this.keyspace = keyspace;
        this.fingerprint = fingerprint;
        this.key_manager = key_manager;
    }

    // org.waterken.url.tls.Locator interface.

    /**
     * Identifies the requested host.
     *
     * @param incoming The incoming socket.
     * @return The requested host {@link Keyspace#identify fingerprint} in
     *         canonical form.
     */
    public static String receive(final Socket incoming) throws IOException {
        final LineInput in = new LineInput(incoming.getInputStream(), 1024);

        // Read the Request-Line.
        String request_line = in.readln();

        // Initial empty lines are ignored.
        while ("".equals(request_line)) {
            request_line = in.readln();
        }

        // Parse the Method.
        final int start_method = 0;
        int end_method = start_method;
        final int end_request_line = request_line.length();
        while (end_method != end_request_line &&
          " \t".indexOf(request_line.charAt(end_method)) == -1) {
            ++end_method;
        }
        final String method = request_line.substring(start_method,
                                                     end_method);

        // Skip SP.
        int start_request_uri = end_method;
        while (start_request_uri != end_request_line &&
          " \t".indexOf(request_line.charAt(start_request_uri)) != -1) {
            ++start_request_uri;
        }

        // Parse the Request-URI.
        int end_request_uri = start_request_uri;
        while (end_request_uri != end_request_line &&
          " \t".indexOf(request_line.charAt(end_request_uri)) == -1) {
            ++end_request_uri;
        }
        final String request_uri = request_line.substring(start_request_uri,
                                                          end_request_uri);

        // Skip SP.
        int start_http_version = end_request_uri;
        while (start_http_version != end_request_line &&
          " \t".indexOf(request_line.charAt(start_http_version)) != -1) {
            ++start_http_version;
        }

        // Parse the HTTP-Version
        final String http_version = start_http_version == end_request_line ?
          "HTTP/0.9" :
          request_line.substring(start_http_version);

        if (!"GET".equals(method)) {
            // Notify the client of the invalid method.
            final OutputStream out = incoming.getOutputStream();
            out.write("HTTP/1.1 405 Method Not Allowed\r\n".getBytes(
              "US-ASCII"));
            out.write("Content-Length: 0\r\n".getBytes("US-ASCII"));
            out.write("Connection: close\r\n".getBytes("US-ASCII"));
            out.write("\r\n".getBytes("US-ASCII"));
            out.flush();
            out.close();
            incoming.close();

            throw new IOException("Expected a GET request");
        }

        if (!http_version.startsWith("HTTP/0.")) {
            // Check for Upgrade header.
            String header = in.readln();
            while (!"".equals(header) &&
              !header.regionMatches(true,
                                    0,
                                    "Upgrade:",
                                    0,
                                    "Upgrade:".length())) {
                header = in.readln();
            }

            // Check for TLS/1.0.
            final String[] protocol = "".equals(header) ?
              new String[]{} :
              TokenList.decode(header.substring("Upgrade:".length()));
            int i = protocol.length;
            while (i-- != 0 && !"TLS/1.0".equals(protocol[i])) {
            }
            if (-1 == i) {
                // Notify the client that something is here.
                final OutputStream out = incoming.getOutputStream();
                out.write("HTTP/1.1 204 No Content\r\n".getBytes("US-ASCII"));
                out.write("Connection: close\r\n".getBytes("US-ASCII"));
                out.write("\r\n".getBytes("US-ASCII"));
                out.flush();
                out.close();
                incoming.close();

                throw new IOException("Upgrade not requested");
            }

            // Discard the rest of the headers.
            while (!"".equals(in.readln())) {
            }
        }

        // The key-id is encoded in the last path segment of the Request-URI.
        final String segment = request_uri.substring(
          request_uri.lastIndexOf('/') + 1);
        return URLDecoder.decode(segment, "US-ASCII").toLowerCase();
    }

    /**
     * Reject an incoming connection.
     *
     * @param incoming The incoming socket.
     */
    public static void reject(final Socket incoming) throws IOException {
        final OutputStream out = incoming.getOutputStream();
        out.write("HTTP/1.1 404 Not Found\r\n".getBytes("US-ASCII"));
        out.write("Content-Length: 0\r\n".getBytes("US-ASCII"));
        out.write("Connection: close\r\n".getBytes("US-ASCII"));
        out.write("\r\n".getBytes("US-ASCII"));
        out.flush();
        out.close();
        incoming.close();
    }

    /**
     * Setup an incoming socket for a server-side TLS handshake.
     * <p/>
     * If client authentication is required, call {@link
     * SSLSocket#setNeedClientAuth} on the returned socket before initiating
     * the handshake. </p>
     *
     * @param incoming The incoming socket.
     * @param protocol The application protocol, eg: "HTTP/1.1".
     * @return The upgraded socket.
     */
    public SSLSocket accept(final Socket incoming, final String protocol)
      throws IOException, NoSuchAlgorithmException, KeyManagementException {
        // Notify the client of protocol upgrade.
        final OutputStream out = incoming.getOutputStream();
        out.write("HTTP/1.1 101 Switching Protocols\r\n".getBytes("US-ASCII"));
        out.write(("Upgrade: TLS/1.0, " + protocol + "\r\n").getBytes(
          "US-ASCII"));
        out.write("Connection: Upgrade\r\n".getBytes("US-ASCII"));
        out.write("\r\n".getBytes("US-ASCII"));
        out.flush();

        // Upgrade the socket.
        final SSLSocket ssl = (SSLSocket)getFactory().createSocket(incoming,
                                                                   incoming.getInetAddress()
                                                                   .getHostAddress(),
                                                                   incoming.getPort(),
                                                                   true);
        ssl.setEnabledCipherSuites(getEnabled());
        ssl.setUseClientMode(false);
        return ssl;
    }

    /**
     * Identifies the remote peer.
     *
     * @param authenticated The authenticated socket.
     * @return The peer {@link Keyspace#identify fingerprint} in canonical
     *         form.
     */
    public String identify(final SSLSocket authenticated)
      throws GeneralSecurityException, SSLPeerUnverifiedException {
        final Certificate[] chain = authenticated.getSession()
          .getPeerCertificates();
        return keyspace.identify(chain[chain.length - 1].getPublicKey());
    }

    /**
     * Gets the {@link Keyspace#identify fingerprint}.
     */
    public String getFingerprint() {
        return fingerprint;
    }

    /**
     * The acceptable and supported ciphersuites.
     */
    private transient String[] enabled;

    /**
     * Gets the enabled ciphersuites.
     */
    public String[] getEnabled() {
        if (null == enabled) {
            try {
                // Eliminate the unsupported ciphersuites.
                int n = 0;
                final String[] supported = getFactory()
                  .getSupportedCipherSuites();
                final String[] acceptable = keyspace.getAcceptable();
                String[] tmp = new String[acceptable.length];
                for (int i = 0; i != acceptable.length; ++i) {
                    final String candidate = acceptable[i];
                    for (int j = 0; j != supported.length; ++j) {
                        if (candidate.equals(supported[j])) {
                            tmp[n++] = candidate;
                            break;
                        }
                    }
                }
                System.arraycopy(tmp, 0, tmp = new String[n], 0, n);
                enabled = tmp;
            } catch (final NoSuchAlgorithmException _) {
                enabled = new String[]{};
            } catch (final KeyManagementException _) {
                enabled = new String[]{};
            }
        }
        return (String[])enabled.clone();
    }

    /**
     * Creates a client.
     *
     * @param network The network to communicate on.
     */
    public org.waterken.url.Locator talk(
      final org.waterken.url.Locator network) {
        return Locator.make(network, this);
    }

    /**
     * The SSL socket factory.
     */
    private transient SSLSocketFactory ssl_factory;

    /**
     * Constructs the SSL socket factory.
     */
    SSLSocketFactory getFactory() throws NoSuchAlgorithmException,
      KeyManagementException {
        if (null == ssl_factory) {
            // Build a socket factory that initially trusts anybody with a
            // valid certificate chain.
            final SSLContext context = SSLContext.getInstance("TLSv1");
            context.init(key_manager, new TrustManager[]{new X509TrustManager() {
                public void checkClientTrusted(final X509Certificate[] chain,
                                               final String authType)
                  throws CertificateException {
                    checkServerTrusted(chain, authType);
                }

                public void checkServerTrusted(final X509Certificate[] chain,
                                               final String authType)
                  throws CertificateException {
                    // Validate the certificate chain.
                    try {
                        final CertPath path = CertificateFactory.getInstance(
                          "X.509").generateCertPath(Arrays.asList(chain));
                        final TrustAnchor ta = new TrustAnchor(
                          chain[chain.length - 1], null);
                        final PKIXParameters params = new PKIXParameters(
                          Collections.singleton(ta));
                        params.setRevocationEnabled(false);
                        CertPathValidator.getInstance("PKIX").validate(path,
                                                                       params);
                    } catch (final NoSuchAlgorithmException e) {
                        throw (CertificateException)new CertificateException().initCause(
                          e);
                    } catch (final InvalidAlgorithmParameterException e) {
                        throw (CertificateException)new CertificateException().initCause(
                          e);
                    } catch (final CertPathValidatorException e) {
                        throw (CertificateException)new CertificateException().initCause(
                          e);
                    }
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[]{};
                }
            }}, null);
            ssl_factory = context.getSocketFactory();
        }
        return ssl_factory;
    }
}
