// Copyright 2002-2005 Waterken Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html
package org.waterken.url.http;

import java.io.IOException;
import java.io.OutputStream;

/**
 * A <code>chunked</code> output stream.
 *
 * @author Tyler
 */
public final class ChunkedOutputStream extends OutputStream {

    private OutputStream out;   // The underlying stream.
    private byte[] buffer;      // The output buffer.
    private int i;              // The end of the buffered data.

    private ChunkedOutputStream(final int chunk_size, final OutputStream out) {
        this.out = out;
        buffer = new byte[chunk_size];
    }

    /**
     * Constructs a <code>ChunkedOutputStream</code>.
     *
     * @param chunk_size The chunk size.
     * @param out        The underlying stream.
     */
    public static OutputStream make(final int chunk_size,
                                    final OutputStream out) {
        return new ChunkedOutputStream(chunk_size, out);
    }

    // java.io.OutputStream interface.

    public void write(final int b) throws IOException {
        buffer[i] = (byte)b;
        if (++i == buffer.length) {
            flush();
        }
    }

    public void write(final byte[] b, int off, int len) throws IOException {
        while (len != 0) {
            if (i == buffer.length) {
                flush();
            }
            final int n = Math.min(len, buffer.length - i);
            System.arraycopy(b, off, buffer, i, n);
            i += n;
            off += n;
            len -= n;
        }
    }

    /**
     * Writes out a chunk.
     */
    public void flush() throws IOException {
        _flush();
        out.flush();
    }

    private void _flush() throws IOException {
        if (0 != i) {
            out.write(Integer.toHexString(i).getBytes("US-ASCII"));
            out.write(new byte[]{'\r', '\n'});
            out.write(buffer, 0, i);
            out.write(new byte[]{'\r', '\n'});
            i = 0;
        }
    }

    public void close() throws IOException {
        if (null != out) {
            _flush();
            out.write(new byte[]{'0'});
            out.write(new byte[]{'\r', '\n'});
            out.write(new byte[]{'\r', '\n'});
            out.flush();
            out.close();
            out = null;
        }
    }
}
