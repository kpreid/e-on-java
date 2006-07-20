// Copyright 2002-2005 Waterken Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html
package org.waterken.url.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * A byte count limited input stream.
 * <p/>
 * This input stream will only allow a preset maximum number of bytes to be
 * read from the underlying stream. </p>
 *
 * @author Tyler
 */
public final class LimitedInputStream extends InputStream {

    private InputStream in; // The underlying stream.
    private long limit;     // The maximum number of bytes that can be read.
    private long marked;    // The marked position.

    private LimitedInputStream(final InputStream in, final long limit) {
        this.in = in;
        this.limit = limit;
    }

    /**
     * Constructs a <code>LimitedInputStream</code>.
     *
     * @param in    The underlying stream.
     * @param limit The maximum number of bytes that can be read.
     */
    public static InputStream make(final InputStream in, final long limit) {
        return new LimitedInputStream(in, limit);
    }

    // java.io.InputStream interface.

    public int read() throws IOException {
        if (0 == limit) {
            throw new TooMuchData();
        }
        final int r = in.read();
        if (r != -1) {
            --limit;
        }
        return r;
    }

    public int read(final byte[] b) throws IOException {
        if (0 == limit) {
            throw new TooMuchData();
        }
        final int n = limit < b.length ?
          in.read(b, 0, (int)limit) :
          in.read(b);
        if (n != -1) {
            limit -= n;
        }
        return n;
    }

    public int read(final byte[] b, final int off, final int len)
      throws IOException {
        if (0 == limit) {
            throw new TooMuchData();
        }
        final int n = in.read(b, off, (int)Math.min(limit, len));
        if (n != -1) {
            limit -= n;
        }
        return n;
    }

    public long skip(long n) throws IOException {
        if (0 == limit) {
            n = 0;
        } else {
            n = in.skip(Math.min(limit, n));
            if (n > 0) {
                limit -= n;
            }
        }
        return n;
    }

    public int available() throws IOException {
        return (int)Math.min(limit, in.available());
    }

    public void close() throws IOException {
        in.close();
    }

    public void mark(final int readlimit) {
        in.mark(readlimit);
        marked = limit;
    }

    public void reset() throws IOException {
        in.reset();
        limit = marked;
    }

    public boolean markSupported() {
        return in.markSupported();
    }

    // LimitedInputStream interface.

    /**
     * Gets the current stream limit.
     */
    public long getLimit() {
        return limit;
    }
}
