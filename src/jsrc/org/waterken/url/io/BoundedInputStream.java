// Copyright 2002-2005 Waterken Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html
package org.waterken.url.io;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/**
 * A bounded input stream.
 *
 * @author Tyler
 */
public final class BoundedInputStream extends InputStream {

    private InputStream in;     // The underlying stream.
    private int remaining;      // The number of bytes remaining.
    private int marked;         // The marked position.

    private BoundedInputStream(final InputStream in, final int remaining) {
        this.in = in;
        this.remaining = remaining;
    }

    /**
     * Constructs a <code>BoundedInputStream</code>.
     *
     * @param in        The underlying stream.
     * @param remaining The number of bytes remaining.
     */
    public static InputStream make(final InputStream in, final int remaining) {
        return new BoundedInputStream(in, remaining);
    }

    // java.io.InputStream interface.

    public int read() throws IOException {
        int r;
        if (0 == remaining) {
            r = -1;
        } else {
            r = in.read();
            if (r == -1) {
                throw new EOFException();
            }
            --remaining;
        }
        return r;
    }

    public int read(final byte[] b, final int off, final int len)
      throws IOException {
        int r;
        if (0 == remaining) {
            r = -1;
        } else {
            r = in.read(b, off, Math.min(len, remaining));
            if (r == -1) {
                throw new EOFException();
            }
            remaining -= r;
        }
        return r;
    }

    public long skip(final long n) throws IOException {
        final long r = in.skip(Math.min(n, remaining));
        remaining -= (int)r;
        return r;
    }

    public int available() throws IOException {
        return Math.min(in.available(), remaining);
    }

    public void close() throws IOException {
        if (null != in) {
            remaining = 0;
            in.close();
            in = null;
        }
    }

    public void mark(final int readlimit) {
        in.mark(Math.min(readlimit, remaining));
        marked = remaining;
    }

    public void reset() throws IOException {
        in.reset();
        remaining = marked;
    }

    public boolean markSupported() {
        return in.markSupported();
    }
}
