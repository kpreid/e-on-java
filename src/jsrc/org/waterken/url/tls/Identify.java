// Copyright 2002-2005 Waterken Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html
package org.waterken.url.tls;

import org.waterken.uri.Base32;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.cert.Certificate;

/**
 * Calculates a public key identifier.
 */
public final class Identify {

    private Identify() {
    }

    /**
     * The parameters are: <ol> <li>keystore file</li> <li>keystore
     * passphrase</li> <li>key alias</li> <ol>
     */
    public static void main(final String[] args) throws Exception {

        // Get the arguments.
        final File key_file = new File(args.length < 1 ? "keys.jks" : args[0]);
        final String passphrase = args.length < 2 ? "nopass" : args[1];
        final String alias = args.length < 3 ? "mykey" : args[2];

        // Load the key store.
        final KeyStore keys = KeyStore.getInstance(KeyStore.getDefaultType());
        final FileInputStream keys_in = new FileInputStream(
          key_file.getAbsoluteFile());
        keys.load(keys_in, passphrase.toCharArray());
        keys_in.close();

        // Identify the public key.
        final Certificate[] chain = keys.getCertificateChain(alias);
        final PublicKey key = chain[chain.length - 1].getPublicKey();
        final MessageDigest hash = MessageDigest.getInstance("SHA-1");
        final String identifier = Base32.encode(hash.digest(key.getEncoded()));
        System.out.println(identifier);
    }
}
