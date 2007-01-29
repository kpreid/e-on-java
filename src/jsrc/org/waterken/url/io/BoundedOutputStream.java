// Copyright 2002-2005 Waterken Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html
package org.waterken.url.io;

import java.io.IOException;
import java.io.OutputStream;

/**
 * A bounded output stream.
 *
 * @author Tyler
 */
public final class BoundedOutputStream extends OutputStream {

    private OutputStream out;   // The underlying stream.
    private int remaining;      // The number of bytes remaining.

    private BoundedOutputStream(final OutputStream out, final int remaining) {
        this.out = out;
        this.remaining = remaining;
    }

    /**
     * Constructs a <code>BoundedOutputStream</code>.
     *
     * @param out       The underlying stream.
     * @param remaining The number of bytes remaining.
     */
    public static OutputStream make(final OutputStream out,
                                    final int remaining) {
        return new BoundedOutputStream(out, remaining);
    }

    // java.io.OutputStream interface.

    public void write(final int b) throws IOException {
        if (1 > remaining) {
            throw new IOException("Invalid Content-Length request header!");
        }
        out.write(b);
        --remaining;
    }

    public void write(final byte[] b, final int off, final int len)
      throws IOException {
        if (remaining < len) {
            throw new IOException("Invalid Content-Length request header!");
        }
        out.write(b, off, len);
        remaining -= len;
    }

    public void flush() throws IOException {
        out.flush();
    }

    public void close() throws IOException {
        if (null != out) {
            if (0 != remaining) {
                throw new IOException("Invalid Content-Length request header!");
            }
            out.close();
            out = null;
        }
    }
}
