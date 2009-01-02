// Copyright 2004-2005 Waterken Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html
package net.vattp;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

/**
 * A record stream between the localhost and a specified peer.
 * <p/>
 * The implementation is thread safe. </p>
 *
 * @author Tyler
 */
public final class Connection {

    private final String localhost; // The localhost fingerprint.
    private final String peer;      // The peer fingerprint.
    private int timeout;            // The SO_TIMEOUT value.
    private Runnable[] closed;      // The closed notifications.
    private Handler handler;        // The received record handler.

    private byte[] buffer;      // The output buffer.
    private int buffer_size;    // The number of bytes in the buffer.
    private Socket socket;      // The socket to communicate over.
    private OutputStream out;   // The output stream.

    Connection(final String localhost, final String peer) {
        this.localhost = localhost;
        this.peer = peer;
        timeout = 60 * 1000;
        closed = new Runnable[]{};

        buffer = new byte[1024];
    }

    void init(final Handler handler) {
        this.handler = handler;
    }

    void failed() {
        boolean r;
        synchronized (this) {
            r = null == socket;
        }
        if (r) {
            close();
        }
    }

    synchronized Runnable connect(final Socket socket, final boolean master)
      throws CrossedConnection {
        if (null != this.socket) {
            throw new CrossedConnection();
        }
        this.socket = socket;
        return new Runnable() {

            public void run() {
                try {
                    // Setup the output stream.
                    synchronized (Connection.this) {
                        out = socket.getOutputStream();

                        if (master) {
                            // Notify the other side of connection setup.
                            out.write(0);
                            out.flush();
                        }

                        // Output the buffered messages.
                        if (0 != buffer_size) {
                            out.write(buffer, 0, buffer_size);
                            buffer_size = 0;
                        }

                        // Don't need a large buffer anymore.
                        buffer = new byte[MAX_LENGTH_BYTES];
                    }

                    // Begin processing received records.
                    socket.setSoTimeout(timeout);
                    final InputStream in = socket.getInputStream();
                    byte[] record = new byte[1024];
                    while (true) {
                        // Read in the record length.
                        int len = 0;
                        int b = _read(in);
                        while (0 != (b & 0x0080)) {
                            len <<= 7;
                            len |= b & 0x7F;
                            b = _read(in);
                        }
                        len <<= 7;
                        len |= b;

                        // Read in the record data.
                        if (record.length < len) {
                            record = new byte[len];
                        }
                        for (int i = 0; i != len;) {
                            final int n = in.read(record, i, len - i);
                            if (-1 == n) {
                                throw new EOFException();
                            }
                            i += n;
                        }

                        handler.run(record, len);
                    }
                } catch (final IOException _) {
                } finally {
                    close();
                }
            }

            private int _read(final InputStream in) throws IOException {
                final int b = in.read();
                if (-1 == b) {
                    throw new EOFException();
                }
                return b;
            }
        };
    }

    // net.vattp.Connection interface.

    /**
     * Gets the localhost fingerprint.
     */
    public String getLocalhost() {
        return localhost;
    }

    /**
     * Gets the peer fingerprint.
     */
    public String getPeer() {
        return peer;
    }

    /**
     * The maximum number of bytes required to encode an int.
     */
    static private final int MAX_LENGTH_BYTES = 5;

    /**
     * Sends a record.
     *
     * @param record The record data.
     * @param len    The record length.
     */
    public void send(final byte[] record, final int len) {
        try {
            synchronized (this) {
                if (null != closed) {
                    if (null != out) {
                        // The connection is live, send the message immediately.
                        _puti(len);
                        out.write(buffer, 0, buffer_size);
                        buffer_size = 0;
                        out.write(record, 0, len);
                    } else {
                        // Buffer the message pending connection setup.
                        final int needed =
                          buffer_size + MAX_LENGTH_BYTES + len;
                        if (buffer.length < needed) {
                            System.arraycopy(buffer,
                                             0,
                                             buffer = new byte[2 * needed],
                                             0,
                                             buffer_size);
                        }
                        _puti(len);
                        System.arraycopy(record, 0, buffer, buffer_size, len);
                        buffer_size += len;
                    }
                }
            }
        } catch (final IOException _) {
            close();
        }
    }

    /**
     * Output an arbitrary length natural number.
     */
    private void _puti(int i) {
        if (0x0080 > i) {
            buffer[buffer_size++] = (byte)i;
        } else {
            _putxi(i >> 7);
            buffer[buffer_size++] = (byte)(i & 0x007F);
        }
    }

    private void _putxi(int i) {
        if (0x0080 <= i) {
            _putxi(i >> 7);
        }
        buffer[buffer_size++] = (byte)(i | 0x0080);
    }

    /**
     * Flushes any buffered records.
     */
    public void flush() {
        try {
            synchronized (this) {
                if (null != out) {
                    out.flush();
                }
            }
        } catch (final IOException _) {
            close();
        }
    }

    /**
     * Enable/disable SO_TIMEOUT.
     *
     * @param timeout The specified timeout, in milliseconds.
     */
    public void setSoTimeout(int timeout) {
        try {
            synchronized (this) {
                this.timeout = timeout;
                if (null != socket) {
                    socket.setSoTimeout(timeout);
                }
            }
        } catch (final SocketException _) {
            close();
        }
    }

    /**
     * Closes the connection.
     */
    public void close() {
        Runnable[] x;
        synchronized (this) {
            x = closed;
            closed = null;
            try {
                if (null != out) {
                    out.flush();
                    out.close();
                }
                if (null != socket) {
                    socket.close();
                }
            } catch (final IOException _) {
            } finally {
                socket = null;
                out = null;
            }
        }
        if (null != x) {
            for (int i = 0; i != x.length; ++i) {
                x[i].run();
            }
        }
    }

    /**
     * Register a {@link #close close} notification.
     *
     * @param notification The notification.
     */
    public void whenClosed(final Runnable notification) {
        boolean added;
        synchronized (this) {
            added = null != closed;
            if (added) {
                final int n = closed.length;
                System.arraycopy(closed,
                                 0,
                                 closed = new Runnable[n + 1],
                                 0,
                                 n);
                closed[n] = notification;
            }
        }
        if (!added) {
            notification.run();
        }
    }
}
