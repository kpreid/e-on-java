// Copyright 2004-2005 Waterken Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html
package org.waterken.url.tls;

import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.Certificate;

/**
 * Cryptographic identity operations.
 */
public interface Keyspace {

    /**
     * Gets the acceptable ciphersuites.
     */
    String[] getAcceptable();

    /**
     * Creates a new identity.
     *
     * @param entropy An entropy source.
     * @return The new public/private key pair.
     */
    KeyPair create(SecureRandom entropy) throws GeneralSecurityException;

    /**
     * Creates a public key fingerprint.
     *
     * @param key The public key to identify.
     * @return The fingerprint.
     */
    String identify(PublicKey key) throws GeneralSecurityException;

    /**
     * Produces a self-signed certificate for an identity.
     *
     * @param identity A cryptographic identity.
     * @return The self-signed certificate.
     */
    Certificate certify(KeyPair identity) throws GeneralSecurityException;

    /**
     * Creates a {@link Host}.
     *
     * @param identity The cryptographic identity to be.
     */
    Host become(KeyPair identity) throws GeneralSecurityException;
}
