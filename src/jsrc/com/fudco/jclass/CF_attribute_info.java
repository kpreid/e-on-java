package com.fudco.jclass;

// Copyright 2004 Hewlett Packard, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Object representing an internalized Java .class file attribute descriptor.
 */
public class CF_attribute_info {

    private int myAttribute_name_index;
    private byte myInfo[];

    /**
     * Constructor.
     *
     * @param attribute_name_index Index into the constant pool of the
     *                             CONSTANT_Utf8_info object containing the
     *                             attribute name.
     * @param info                 The bytes of the attribute info themselves.
     */
    CF_attribute_info(int attribute_name_index, byte info[]) {
        myAttribute_name_index = attribute_name_index;
        myInfo = info;
    }

    /**
     * Obtain this attribute's name index.
     *
     * @return the index into the constant pool of the CONSTANT_Utf8_info
     *         object containing the attribute name.
     */
    public int attribute_name_index() {
        return myAttribute_name_index;
    }

    /**
     * Obtain the number of bytes of attribute data.
     *
     * @return the number of bytes in the collection of attribute data bytes in
     *         this attribute.
     */
    public int info_count() {
        return myInfo.length;
    }

    /**
     * Obtain the attribute bytes.
     *
     * @return a copy of the array of data bytes for this attribute.
     */
    public byte[] info() {
        byte result[] = new byte[myInfo.length];
        System.arraycopy(myInfo, 0, result, 0, myInfo.length);
        return result;
    }

    /**
     * Obtain some of the attribute bytes.
     *
     * @param offset The index (zero-based) into the attribute bytes of the
     *               first byte desired.
     * @param length The number of bytes desired, starting at the given offset
     * @return a copy of the indicated sub-section of the array of data bytes
     *         for this attribute.
     */
    public byte[] info(int offset, int length) {
        byte result[] = new byte[myInfo.length - offset];
        System.arraycopy(myInfo, offset, result, 0, length);
        return result;
    }

    /**
     * Obtain one of the attribute bytes.
     *
     * @param offset The index (zero-based) into the attribute bytes of the
     *               byte desired.
     * @return the indicated byte from the array of attribute bytes.
     */
    public byte info(int offset) {
        return myInfo[offset];
    }

    /**
     * Read and return an attribute_info object from a DataInputStream.
     *
     * @param in The DataInputStream to read from; this should be positioned at
     *           the first byte of the method descriptor being read.
     */
    static public CF_attribute_info read(DataInputStream in)
      throws IOException {
        int attribute_name_index = in.readUnsignedShort();
        int attribute_length = in.readInt();
        byte info[] = new byte[attribute_length];
        in.readFully(info);
        return new CF_attribute_info(attribute_name_index, info);
    }
}
