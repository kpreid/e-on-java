package com.fudco.jclass;

// Copyright 2004 Hewlett Packard, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Constant pool entry containing a UTF-8 encoded sequence of characters.
 */
public class CF_CONSTANT_Utf8_info extends CF_cp_info {

    private byte[] myBytes;

    /**
     * Constructor.
     *
     * @param bytes The sequence of bytes encoding the UTF-8 string.
     */
    CF_CONSTANT_Utf8_info(byte[] bytes) {
        myBytes = bytes;
    }

    /**
     * Obtain the string as a String object.
     *
     * @return the collection of bytes this constant holds, interpreted as a
     *         UTF-8 string.
     */
    public String asString() {
        return new String(myBytes);
    }

    /**
     * Obtain the number of bytes in this string.
     *
     * @return the number of bytes in the collection of bytes this constant
     *         holds.
     */
    public int bytes_count() {
        return myBytes.length;
    }

    /**
     * Obtain the bytes.
     *
     * @return a copy of the array of bytes this constant contains.
     */
    public byte[] bytes() {
        byte[] result = new byte[myBytes.length];
        System.arraycopy(myBytes, 0, result, 0, myBytes.length);
        return result;
    }

    /**
     * Obtain some of the bytes.
     *
     * @param offset The index (zero-based) into the collection of bytes of the
     *               first byte desired.
     * @param length The number of bytes desired, starting at the given offset
     * @return a copy of the indicated sub-section of the array of bytes this
     *         constant contains.
     */
    public byte[] bytes(int offset, int length) {
        byte[] result = new byte[myBytes.length - offset];
        System.arraycopy(myBytes, offset, result, 0, length);
        return result;
    }

    /**
     * Obtain one of the bytes.
     *
     * @param offset The index (zero-based) into the collection of the byte
     *               desired.
     * @return the indicated byte from the collection of bytes
     */
    public byte bytes(int offset) {
        return myBytes[offset];
    }

    /**
     * Read and return a CONSTANT_Utf8_info object from a DataInputStream.
     *
     * @param in The DataInputStream to read from; this should be positioned at
     *           the first byte after the type code byte of the constant pool
     *           entry being read.
     */
    static CF_CONSTANT_Utf8_info read(DataInputStream in) throws IOException {
        int length = in.readUnsignedShort();
        byte[] bytes = new byte[length];
        in.readFully(bytes);
        return new CF_CONSTANT_Utf8_info(bytes);
    }
}
