// Copyright 2005 Waterken Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html
package net.vattp.chat;

import net.vattp.Connection;
import net.vattp.Handler;
import net.vattp.Manager;
import net.vattp.Reactor;
import org.waterken.url.tls.Host;
import org.waterken.url.tls.Keyspace;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.security.KeyPair;
import java.security.SecureRandom;

/**
 * A really simple chat client.
 *
 * @author Tyler
 */
public final class Main {

    private Main() {
    }

    /**
     * Starts a chat client.
     * <p/>
     * The command line parameters are: </p> <ol> <li>The remote Vat to connect
     * to.</li> </ol>
     */
    public static void main(final String[] args) throws Exception {

        // Generate a new identity.
        final Keyspace crypto = org.waterken.url.tls.sha1withrsa.Keyspace.make();
        final SecureRandom prng = org.waterken.entropy.Entropy.make();
        final KeyPair identity = crypto.create(prng);
        final Host me = crypto.become(identity);
        System.out.print("*" + me.getFingerprint());

        // Get online.
        final Manager manager = new Manager(me, "ChatTP/1.0", org.waterken.url.dns.Locator.make(
          80), new Reactor() {
              public Handler run(final Connection connection) {
                  System.out.println(connection.getPeer() + " here");
                  connection.whenClosed(new Runnable() {
                      public void run() {
                          System.out.println(connection.getPeer() + " gone");
                      }
                  });
                  return new Handler() {
                      public void run(final byte[] record, final int len) {
                          try {
                              System.out.print(connection.getPeer() + "> ");
                              System.out.println(
                                new String(record, 0, len, "UTF-8"));
                          } catch (final UnsupportedEncodingException _) {
                              // Should never happen.
                          }
                      }
                  };
              }
          });
        final ServerSocket port = new ServerSocket(0);
        final Thread listener = new Thread(manager.listen(port));
        listener.setDaemon(true);
        listener.start();
        System.out.println("@localhost:" + port.getLocalPort());

        // Connect to some friends.
        for (int i = 0; i != args.length; ++i) {
            manager.connect(args[i]);
        }

        // Start the input loop.
        final BufferedReader in = new BufferedReader(
          new InputStreamReader(System.in));
        String line = in.readLine();
        while (!"quit".equals(line)) {
            final byte[] record = line.getBytes("UTF-8");
            final Connection[] x = manager.list();
            for (int i = 0; i != x.length; ++i) {
                x[i].send(record, record.length);
            }
            line = in.readLine();
        }

        // Quit.
        manager.close();
        System.out.println("done");
    }
}
