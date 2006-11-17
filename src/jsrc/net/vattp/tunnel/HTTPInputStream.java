package net.vattp.tunnel;

/*
The contents of this file are subject to the Electric Communities E Open
Source Code License Version 1.0 (the "License"); you may not use this file
except in compliance with the License. You may obtain a copy of the License
at http://www.communities.com/EL/.

Software distributed under the License is distributed on an "AS IS" basis,
WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
the specific language governing rights and limitations under the License.

The Original Code is the Distributed E Language Implementation, released
July 20, 1998.

The Initial Developer of the Original Code is Electric Communities.
Copyright (C) 1998 Electric Communities. All Rights Reserved.

Contributor(s): ______________________________________.
*/

import java.io.IOException;
import java.io.InputStream;


/**
 * Class to handle the suspension and shutdown messages.
 *
 * @author Bill Frantz
 */

public class HTTPInputStream extends InputStream {

    private final InputStream myStream;

    private long myLengthLimit = Long.MAX_VALUE;


    HTTPInputStream(InputStream stream) {
        myStream = stream;
    }

    /**
     * Returns the number of bytes that can be read from this input stream
     * without blocking.
     *
     * @return the number of bytes that can be read from this input stream
     *         without blocking.
     * @throws IOException if an I/O error occurs.
     */
    public int available() throws IOException {
        int i = myStream.available();
        if (i < myLengthLimit) {
            return i;
        }
        return (int)myLengthLimit;
    }

    /**
     * Closes this input stream and releases any system resources associated
     * with the stream.
     * <p/>
     *
     * @throws IOException if an I/O error occurs.
     */
    public void close() throws IOException {
        myStream.close();
    }

    /**
     * Reads the next byte of data from this input stream. The value byte is
     * returned as an <tt>int</tt> in the range <tt>0</tt> to <tt>255</tt>. If
     * no byte is available because the end of the stream has been reached, the
     * value <tt>-1</tt> is returned. This method blocks until input data is
     * available, the end of the stream is detected, or an exception is
     * thrown.
     * <p/>
     * A subclass must provide an implementation of this method.
     *
     * @return the next byte of data, or <tt>-1</tt> if the end of the stream
     *         is reached.
     * @throws IOException if an I/O error occurs.
     * @since JDK1.0
     */
    public int read() throws IOException {
        if (myLengthLimit <= 0) {
            return -1;
        }
        int ret = myStream.read();
        if (-1 == ret) {
            throw new IOException(
              "Early EOF, expected " + myLengthLimit + " bytes");
        }
        return ret;
    }

    /**
     * Reads up to <tt>len</tt> bytes of data from this input stream into an
     * array of bytes. This method blocks until some input is available. If the
     * first argument is <tt>null,</tt> up to <tt>len</tt> bytes are read and
     * discarded.
     *
     * @param b   the buffer into which the data is read.
     * @param off the start offset of the data.
     * @param len the maximum number of bytes read.
     * @return the total number of bytes read into the buffer, or <tt>-1</tt>
     *         if there is no more data because the end of the stream has been
     *         reached.
     * @throws IOException if an I/O error occurs.
     */
    public int read(byte b[], int off, int len) throws IOException {
        if (len > myLengthLimit) {
            len = (int)myLengthLimit;
        }
        if (myLengthLimit <= 0) {
            return -1;
        }
        int read = myStream.read(b, off, len);
        myLengthLimit -= read;
        return read;
    }

    /**
     * Set the stream size.
     */
    public void setSize(long size) {
        myLengthLimit = size;
    }
}
