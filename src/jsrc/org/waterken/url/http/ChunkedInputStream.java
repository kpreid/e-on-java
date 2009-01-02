// Copyright 2002-2005 Waterken Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html
package org.waterken.url.http;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/**
 * A <code>chunked</code> input stream.
 *
 * @author Tyler
 */
public final class ChunkedInputStream extends InputStream {

    private InputStream in;     // The underlying stream.
    private boolean started;    // Has the first chunk been read?
    private long chunk_size;    // The remaining bytes in the current chunk.
    private boolean done;       // Has the last chunk been read?
    private boolean marked_started; // The marked started indicator.
    private long marked_chunk_size; // The marked position.
    private boolean marked_done;    // The marked done indicator.

    private ChunkedInputStream(final InputStream in) {
        this.in = in;
    }

    /**
     * Constructs a <code>ChunkedInputStream</code>.
     *
     * @param in The underlying stream.
     */
    static public InputStream make(final InputStream in) {
        return new ChunkedInputStream(in);
    }

    /**
     * Reads in the next chunk.
     */
    private void _next() throws IOException {
        // Skip over the trailing CRLF from the previous chunk.
        int c;
        if (started) {
            c = in.read();
            if (-1 == c) {
                throw new EOFException();
            }
            if ('\r' != c) {
                throw new IOException("Expected CR");
            }
            c = in.read();
            if (-1 == c) {
                throw new EOFException();
            }
            if ('\n' != c) {
                throw new IOException("Expected LF");
            }
        }
        started = true;

        // Read in the chunk-size.
        while (true) {
            c = in.read();
            if (-1 == c) {
                throw new EOFException();
            }
            if ('0' <= c && '9' >= c) {
                chunk_size = chunk_size * 16 + (c - '0');
            } else if ('a' <= c && 'f' >= c) {
                chunk_size = chunk_size * 16 + 10 + (c - 'a');
            } else if ('A' <= c && 'F' >= c) {
                chunk_size = chunk_size * 16 + 10 + (c - 'A');
            } else {
                break;
            }
        }
        if (0 == chunk_size) {
            done = true;
        }

        // Skip over the chunk-extension.
        int preceding = '0';
        boolean open_quote = false;
        while (!('\r' == c && '\\' != preceding && !open_quote)) {
            preceding = c;
            c = in.read();
            if (-1 == c) {
                throw new EOFException();
            }
            if ('\"' == c && '\\' != preceding) {
                open_quote = !open_quote;
            }
        }
        c = in.read();
        if (-1 == c) {
            throw new EOFException();
        }
        if ('\n' != c) {
            throw new IOException("Expected LF");
        }
    }

    // java.io.InputStream interface.

    public int read() throws IOException {
        int r;
        if (done) {
            r = -1;
        } else {
            if (0 == chunk_size) {
                _next();
                r = read();
            } else {
                r = in.read();
                if (-1 == r) {
                    throw new EOFException();
                }
                --chunk_size;
            }
        }
        return r;
    }

    public int read(final byte[] b, int off, int len) throws IOException {
        int n;
        if (done) {
            n = -1;
        } else {
            n = 0;
            while (0 < len) {
                if (0 == chunk_size) {
                    _next();
                    if (done) {
                        break;
                    }
                }
                final int d = in.read(b, off, (int)Math.min(len, chunk_size));
                if (-1 == d) {
                    throw new EOFException();
                }
                chunk_size -= d;
                n += d;
                off += d;
                len -= d;
            }
        }
        return n;
    }

    public long skip(long n) throws IOException {
        long k = 0;
        if (!done) {
            while (0 < n) {
                if (0 == chunk_size) {
                    _next();
                    if (done) {
                        break;
                    }
                }
                final long d = in.skip(Math.min(n, chunk_size));
                chunk_size -= d;
                k += d;
                n -= d;
            }
        }
        return k;
    }

    public int available() throws IOException {
        return done ? 0 : in.available();
    }

    public void close() throws IOException {
        if (null != in) {
            done = true;
            in.close();
            in = null;
        }
    }

    public void mark(final int readlimit) {
        in.mark(readlimit);
        marked_started = started;
        marked_chunk_size = chunk_size;
        marked_done = done;
    }

    public void reset() throws IOException {
        in.reset();
        started = marked_started;
        chunk_size = marked_chunk_size;
        done = marked_done;
    }

    public boolean markSupported() {
        return in.markSupported();
    }
}
