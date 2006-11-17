package com.fudco.jclass;

// Copyright 2004 Hewlett Packard, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Constant pool entry describing a field or method of an object.
 */
public class CF_CONSTANT_NameAndType_info extends CF_cp_info {

    private int myName_index;
    private int myDescriptor_index;

    /**
     * Constructor.
     *
     * @param name_index       Index into the constant pool of the
     *                         CONSTANT_Utf8_info object containing the name of
     *                         the field or method stored as a simple name (a
     *                         Java identifier) or the special name "<init>".
     * @param descriptor_index Index into the constant pool of the
     *                         CONSTANT_Utf8_info object containing the field
     *                         or method type signature.
     */
    CF_CONSTANT_NameAndType_info(int name_index, int descriptor_index) {
        myName_index = name_index;
        myDescriptor_index = descriptor_index;
    }

    /**
     * Obtain this entry's name index.
     *
     * @return the index into the constant pool of the CONSTANT_Utf8_info
     *         object containing the name of the field or method being
     *         described.
     */
    public int name_index() {
        return myName_index;
    }

    /**
     * Obtain this entry's descriptor index.
     *
     * @return the index into the constant pool of the CONSTANT_Utf8_info
     *         object containing the field or method type signature.
     */
    public int descriptor_index() {
        return myDescriptor_index;
    }

    /**
     * Read and return a CONSTANT_NameAndType_info object from a
     * DataInputStream.
     *
     * @param in The DataInputStream to read from; this should be positioned at
     *           the first byte after the type code byte of the constant pool
     *           entry being read.
     */
    static CF_CONSTANT_NameAndType_info read(DataInputStream in)
      throws IOException {
        int name_index = in.readUnsignedShort();
        int descriptor_index = in.readUnsignedShort();
        return new CF_CONSTANT_NameAndType_info(name_index, descriptor_index);
    }
}
