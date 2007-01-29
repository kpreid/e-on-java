// Copyright 2004-2005 Waterken Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html
package net.vattp;

import org.waterken.uri.Authority;
import org.waterken.url.Locator;
import org.waterken.url.tls.Host;

import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSocket;
import java.io.EOFException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * A {@link Connection} manager.
 * <p/>
 * The implementation is thread safe. </p>
 *
 * @author Tyler
 */
public final class Manager {

    private final Host me;          // The localhost cryptographic identity.
    private final String protocol;  // The top-level protocol identifier.
    private final Locator network;  // The network for outbound connections.
    private final Reactor reactor;  // The connection birth notification.

    private volatile boolean terminate = false; // Stop listening?
    private final Map connections;      // The [ peer id => connection ].

    /**
     * Constructs a <code>Manager</code>.
     *
     * @param me       The localhost cryptographic identity.
     * @param protocol The top-level protocol identifier.
     * @param network  The network for outbound connections.
     * @param reactor  The connection birth notification.
     */
    public Manager(final Host me,
                   final String protocol,
                   final Locator network,
                   final Reactor reactor) {
        this.me = me;
        this.protocol = protocol;
        this.network = network;
        this.reactor = reactor;

        connections = new HashMap();
    }

    // net.vattp.Manager interface.

    /**
     * Creates a listen loop.
     *
     * @param port The port to listen on.
     * @return The listen loop.
     */
    public Runnable listen(final ServerSocket port) {
        return new Runnable() {

            public void run() {
                try {
                    while (!terminate) {
                        try {
                            final Socket incoming = port.accept();
                            new Thread() {
                                public void run() {
                                    try {
                                        final String x =
                                          Host.receive(incoming);
                                        if (me.getFingerprint().equals(x)) {
                                            final SSLSocket s =
                                              me.accept(incoming, protocol);
                                            s.setNeedClientAuth(true);
                                            s.startHandshake();
                                            final Runnable body =
                                              _accept(me.identify(s), s);
                                            if (null != body) {
                                                body.run();
                                            }
                                        } else {
                                            Host.reject(incoming);
                                        }
                                    } catch (final IOException _) {
                                        try {
                                            incoming.close();
                                        } catch (final IOException __) {
                                        }
                                    } catch (final NoSuchAlgorithmException _) {
                                        try {
                                            incoming.close();
                                        } catch (final IOException __) {
                                        }
                                    } catch (final KeyManagementException _) {
                                        try {
                                            incoming.close();
                                        } catch (final IOException __) {
                                        }
                                    } catch (final GeneralSecurityException _) {
                                        try {
                                            incoming.close();
                                        } catch (final IOException __) {
                                        }
                                    }
                                }
                            }.start();
                        } catch (final InterruptedIOException _) {
                            // Timeout occured. Just go around again, checking
                            // for termination before blocking again.
                        }
                    }
                } catch (final IOException _) {
                } finally {
                    try {
                        port.close();
                    } catch (final IOException _) {
                    }
                }
            }
        };
    }

    private Runnable _accept(final String peer, final SSLSocket socket) {
        Runnable r;
        try {
            if (terminate) {
                throw new EOFException();
            }
            if (0 > me.getFingerprint().compareTo(peer)) {
                // I'm the master.
                r = _open(peer, null).connect(socket, true);
            } else {
                // I'm the slave.
                if (0 != socket.getInputStream().read()) {
                    throw new EOFException();
                }
                r = _open(peer, null).connect(socket, false);
            }
        } catch (final SSLPeerUnverifiedException _) {
            try {
                socket.close();
            } catch (final IOException __) {
            }
            r = null;
        } catch (final IOException _) {
            try {
                socket.close();
            } catch (final IOException __) {
            }
            r = null;
        }
        return r;
    }

    private Connection _open(final String peer, final Runnable initiate) {
        Connection r;
        boolean fresh;
        synchronized (connections) {
            r = (Connection)connections.get(peer);
            if (null == r) {
                final Connection x = new Connection(me.getFingerprint(), peer);
                connections.put(peer, x);
                x.whenClosed(new Runnable() {
                    public void run() {
                        synchronized (connections) {
                            final Object v = connections.remove(peer);
                            if (x != v) {
                                connections.put(peer, v);
                            }
                        }
                    }
                });
                x.init(reactor.run(x));
                r = x;
                fresh = true;
            } else {
                fresh = false;
            }
        }
        if (fresh && null != initiate) {
            new Thread(initiate).start();
        }
        return r;
    }

    /**
     * Connect to the identified vat.
     *
     * @param authority The URL authority identifying the remote vat.
     * @return The peer connection.
     */
    public Connection connect(final String authority) {
        final String peer = Authority.fingerprint(authority);
        return _open(peer, new Runnable() {
            public void run() {
                try {
                    final Runnable body = _accept(peer,
                                                  (SSLSocket)me.talk(network)
                                                    .locate(authority, null));
                    if (null != body) {
                        body.run();
                    }
                } catch (final IOException _) {
                    Connection x;
                    synchronized (connections) {
                        x = (Connection)connections.get(peer);
                    }
                    if (null != x) {
                        x.failed();
                    }
                }
            }
        });
    }

    /**
     * Stop servicing all connections.
     */
    public void close() {
        terminate = true;
        while (true) {
            Connection x;
            synchronized (connections) {
                x = connections.isEmpty() ?
                  null :
                  (Connection)connections.values().iterator().next();
            }
            if (null == x) {
                break;
            }
            x.close();
        }
    }

    /**
     * Lists the currently active connections.
     */
    public Connection[] list() {
        synchronized (connections) {
            return (Connection[])connections.values()
              .toArray(new Connection[]{});
        }
    }
}
