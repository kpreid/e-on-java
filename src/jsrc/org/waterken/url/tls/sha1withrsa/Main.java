// Copyright 2004-2005 Waterken Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html
package org.waterken.url.tls.sha1withrsa;

import org.waterken.entropy.Entropy;

import java.io.File;
import java.io.FileOutputStream;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;

/**
 * Generates a public/private key pair.
 */
public final class Main {

    private Main() {
    }

    /**
     * Generates a public/private key pair.
     * <p/>
     * The parameters are: </p> <ol> <li>keystore file</li> <li>keystore
     * passphrase</li> <li>key alias</li> </ol>
     * <p/>
     * The identifier for the new site is printed to standard output. </p>
     */
    public static void main(final String[] args) throws Exception {

        // Get the arguments.
        final File key_file = new File(args.length < 1 ? "keys.jks" : args[0]);
        final String passphrase = args.length < 2 ? "nopass" : args[1];
        final String alias = args.length < 3 ? "mykey" : args[2];

        // Generate a new identity.
        final org.waterken.url.tls.Keyspace crypto = Keyspace.make();
        final SecureRandom prng = Entropy.make();
        final KeyPair identity = crypto.create(prng);

        // Produce the self-signed certificate.
        final Certificate certificate = crypto.certify(identity);

        // Create the key store.
        final KeyStore keys = KeyStore.getInstance(KeyStore.getDefaultType());
        keys.load(null, null);
        keys.setKeyEntry(alias,
                         identity.getPrivate(),
                         passphrase.toCharArray(),
                         new Certificate[]{certificate});
        final FileOutputStream out = new FileOutputStream(
          key_file.getAbsoluteFile());
        keys.store(out, passphrase.toCharArray());
        out.flush();
        out.close();

        // Print out the identifier.
        System.out.println(crypto.identify(identity.getPublic()));
    }
}
