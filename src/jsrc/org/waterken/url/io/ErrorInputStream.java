// Copyright 2002-2005 Waterken Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html
package org.waterken.url.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * A broken input stream.
 *
 * @author Tyler
 */
public final class ErrorInputStream extends InputStream {

    private final IOException error;      // The error.

    private ErrorInputStream(final IOException error) {
        this.error = error;
    }

    /**
     * Constructs a <code>ErrorInputStream</code>.
     *
     * @param error The error.
     */
    static public InputStream make(final IOException error) {
        return new ErrorInputStream(error);
    }

    // java.io.InputStream interface.

    public int read() throws IOException {
        throw error;
    }

    public int read(final byte[] b, final int off, final int len)
      throws IOException {
        throw error;
    }

    public long skip(final long n) throws IOException {
        throw error;
    }

    public int available() {
        return 0;
    }

    public void close() {
    }

    public void mark(final int readlimit) {
    }

    public void reset() throws IOException {
        throw error;
    }

    public boolean markSupported() {
        return false;
    }
}
