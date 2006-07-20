package com.fudco.jclass;

// Copyright 2004 Hewlett Packard, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Constant pool entry describing an integer constant (type int, char, byte, or
 * short).
 */
public class CF_CONSTANT_Integer_info extends CF_cp_info {

    private int myValue;

    /**
     * Constructor.
     *
     * @param value The integer value of this constant.
     */
    CF_CONSTANT_Integer_info(int value) {
        myValue = value;
    }

    /**
     * Obtain the value of this constant.
     *
     * @return the integer value of this constant.
     */
    public int value() {
        return myValue;
    }

    /**
     * Read and return a CONSTANT_Integer_info object from a DataInputStream.
     *
     * @param in The DataInputStream to read from; this should be positioned
     *           at the first byte after the type code byte of the constant pool entry
     *           being read.
     */
    static CF_CONSTANT_Integer_info read(DataInputStream in)
      throws IOException {
        int value = in.readInt();
        return new CF_CONSTANT_Integer_info(value);
    }
}
