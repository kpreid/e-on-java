package com.fudco.jclass;

// Copyright 2004 Hewlett Packard, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Constant pool entry describing a class reference.
 */
public class CF_CONSTANT_Class_info extends CF_cp_info {

    private int myName_index;

    /**
     * Constructor.
     *
     * @param name_index Index into the constant pool of the CONSTANT_Utf8_info
     *                   object containing the fully-qualified class or
     *                   interface name of a class, in internal form.
     */
    CF_CONSTANT_Class_info(int name_index) {
        myName_index = name_index;
    }

    /**
     * Obtain the name index for this class entry.
     *
     * @return the index into the constant pool of the CONSTANT_Utf8_info
     *         object containing the fully-qualified class or interface name of
     *         a class, in internal form.
     */
    public int name_index() {
        return myName_index;
    }

    /**
     * Read and return a CONSTANT_Class_info object from a DataInputStream.
     *
     * @param in The DataInputStream to read from; this should be positioned at
     *           the first byte after the type code byte of the constant pool
     *           entry being read.
     */
    static CF_CONSTANT_Class_info read(DataInputStream in) throws IOException {
        int name_index = in.readUnsignedShort();
        return new CF_CONSTANT_Class_info(name_index);
    }
}

