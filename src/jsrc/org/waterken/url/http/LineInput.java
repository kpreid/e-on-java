// Copyright 2002-2005 Waterken Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html
package org.waterken.url.http;

import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * An HTTP line reader.
 * <p/>
 * This implementation follows the guidelines set out in section 19.3 </p>
 *
 * @author Tyler
 */
public final class LineInput extends FilterInputStream {

    /**
     * The line buffer.
     */
    private byte[] buffer;

    /**
     * Constructs an <code>LineInput</code>.
     *
     * @param in            The underlying stream.
     * @param line_capacity The expected line length.
     */
    public LineInput(final InputStream in, final int line_capacity) {
        super(in);
        buffer = new byte[line_capacity];
    }

    // org.waterken.url.http.LineInput interface.

    /**
     * Reads a line of text.
     */
    public String readln() throws IOException {
        // Read until LF.
        int len = 0;
        while (true) {
            final int c = read();
            if (-1 == c) {
                throw new EOFException();
            }
            if ('\n' == c) {
                break;
            }
            if (len == buffer.length) {
                System.arraycopy(buffer,
                                 0,
                                 buffer = new byte[2 * len],
                                 0,
                                 len);
            }
            buffer[len] = (byte)c;
            ++len;
        }

        // Strip off any leading CR.
        while (0 != len && '\r' == buffer[len - 1]) {
            --len;
        }

        return new String(buffer, 0, len, "US-ASCII");
    }
}
