package com.fudco.jclass;

// Copyright 2004 Hewlett Packard, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Constant pool entry describing a constant of type String.
 */
public class CF_CONSTANT_String_info extends CF_cp_info {

    private int myString_index;

    /**
     * Constructor.
     *
     * @param string_index Index into the constant pool of the
     *                     CONSTANT_Utf8_info object containing the String's value.
     */
    CF_CONSTANT_String_info(int string_index) {
        myString_index = string_index;
    }

    /**
     * Obtain the string index for this constant.
     *
     * @return the index into the constant pool of the CONSTANT_Utf8_info
     *         object containing the String's value.
     */
    public int string_index() {
        return myString_index;
    }

    /**
     * Read and return a CONSTANT_String_info object from a DataInputStream.
     *
     * @param in The DataInputStream to read from; this should be positioned
     *           at the first byte after the type code byte of the constant pool entry
     *           being read.
     */
    static CF_CONSTANT_String_info read(DataInputStream in)
      throws IOException {
        int string_index = in.readUnsignedShort();
        return new CF_CONSTANT_String_info(string_index);
    }
}
