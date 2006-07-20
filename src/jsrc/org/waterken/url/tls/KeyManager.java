// Copyright 2004-2005 Waterken Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html
package org.waterken.url.tls;

import java.net.Socket;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

/**
 * A single key {@link javax.net.ssl.X509KeyManager}.
 */
public final class KeyManager implements javax.net.ssl.X509KeyManager,
  java.io.Serializable {

    static final long serialVersionUID = -4659593570639874485L;

    /**
     * The private key.
     */
    private PrivateKey key;

    /**
     * The certificate chain.
     */
    private X509Certificate[] certificate;

    private KeyManager() {
        certificate = new X509Certificate[]{};
    }

    private KeyManager(final PrivateKey key,
                       final X509Certificate[] certificate) {
        this.key = key;
        this.certificate = certificate;
    }

    /**
     * Constructs a <code>KeyManager</code>.
     *
     * @param key         The private key.
     * @param certificate The certificate chain.
     */
    public static javax.net.ssl.X509KeyManager make(final PrivateKey key,
                                                    final X509Certificate[] certificate) {
        return new KeyManager(key, certificate);
    }

    // javax.net.ssl.X509KeyManager interface.

    /**
     * The singleton identifier.
     */
    private static final String PUMPKIN = "pumpkin";

    public String chooseClientAlias(final String[] keyType,
                                    final Principal[] issuers,
                                    final Socket socket) {
        int i = keyType.length;
        while (i-- != 0 && !keyType[i].equals(key.getAlgorithm())) {
        }
        return -1 != i ? PUMPKIN : null;
    }

    public String chooseServerAlias(final String keyType,
                                    final Principal[] _,
                                    final Socket __) {
        return keyType.equals(key.getAlgorithm()) ? PUMPKIN : null;
    }

    public String[] getClientAliases(final String keyType,
                                     final Principal[] issuers) {
        return getServerAliases(keyType, issuers);
    }

    public String[] getServerAliases(final String keyType,
                                     final Principal[] _) {
        return keyType.equals(key.getAlgorithm()) ?
          new String[]{PUMPKIN} :
          null;
    }

    public X509Certificate[] getCertificateChain(final String alias) {
        return PUMPKIN.equals(alias) ? certificate : null;
    }

    public PrivateKey getPrivateKey(final String alias) {
        return PUMPKIN.equals(alias) ? key : null;
    }
}
