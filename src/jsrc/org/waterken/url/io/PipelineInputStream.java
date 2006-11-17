// Copyright 2002-2005 Waterken Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html
package org.waterken.url.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * A pipeline input stream.
 *
 * @author Tyler
 */
public final class PipelineInputStream extends InputStream {

    private InputStream in;     // The underlying stream.
    private boolean marked;     // Is the stream marked?
    private int readlimit;      // The maximum mark buffer.
    private long marked_offset; // The number of bytes back to the marked
    // position.

    /**
     * Constructs a <code>PipelineInputStream</code>.
     *
     * @param in The underlying stream.
     */
    public PipelineInputStream(final InputStream in) {
        this.in = in;
    }

    // InputStream interface.

    public synchronized int read() throws IOException {
        final int r = in.read();
        if (r != -1 && marked) {
            ++marked_offset;
        }
        return r;
    }

    public synchronized int read(final byte[] b, final int off, final int len)
      throws IOException {
        final int r = in.read(b, off, len);
        if (r != -1 && marked) {
            marked_offset += r;
        }
        return r;
    }

    public synchronized long skip(final long n) throws IOException {
        final long r = in.skip(n);
        if (marked) {
            marked_offset += r;
        }
        return r;
    }

    public synchronized int available() throws IOException {
        return in.available();
    }

    public synchronized void close() throws IOException {
        if (null != in) {
            try {
                skip(Long.MAX_VALUE);
            } finally {
                in.close();
                in = null;
            }
        }
    }

    public synchronized void mark(final int readlimit) {
        in.mark(readlimit);
        marked = true;
        this.readlimit = readlimit;
        marked_offset = 0;
    }

    public synchronized void reset() throws IOException {
        in.reset();
        marked_offset = 0;
    }

    public synchronized boolean markSupported() {
        return in.markSupported();
    }

    // PipelineInputStream interface.

    /**
     * Pumps the underlying input stream into a buffer and closes it.
     */
    public synchronized void pump() throws IOException {
        if (null != in && !(in instanceof ByteArrayInputStream)) {
            try {
                // Reverse to the marked position.
                if (marked) {
                    try {
                        in.reset();
                    } catch (final IOException e) {
                        if (marked_offset <= readlimit) {
                            throw e;
                        }
                        marked = false;
                        readlimit = 0;
                        marked_offset = 0;
                    }
                }

                // Buffer the stream.
                byte[] buffer = new byte[1024];
                int n = 0;
                int i = in.read(buffer);
                while (i != -1) {
                    n += i;
                    if (n == buffer.length) {
                        System.arraycopy(buffer,
                                         0,
                                         buffer = new byte[2 * n],
                                         0,
                                         n);
                    }
                    i = in.read(buffer, n, buffer.length - n);
                }
                in.close();
                in = new ByteArrayInputStream(buffer, 0, n);

                // Advance to the current position.
                if (marked) {
                    in.mark(readlimit);
                    in.skip(marked_offset);
                }
            } catch (final IOException e) {
                in.close();
                in = ErrorInputStream.make(e);
                throw e;
            }
        }
    }
}
