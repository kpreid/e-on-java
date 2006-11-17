package com.fudco.jclass;

// Copyright 2004 Hewlett Packard, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Constant pool entry describing a float constant.
 */
public class CF_CONSTANT_Float_info extends CF_cp_info {

    private float myValue;

    /**
     * Constructor.
     *
     * @param value The float value of this constant.
     */
    CF_CONSTANT_Float_info(float value) {
        myValue = value;
    }

    /**
     * Obtain the value of this constant.
     *
     * @return the float value of this constant.
     */
    public float value() {
        return myValue;
    }

    /**
     * Read and return a CONSTANT_Float_info object from a DataInputStream.
     *
     * @param in The DataInputStream to read from; this should be positioned at
     *           the first byte after the type code byte of the constant pool
     *           entry being read.
     */
    static CF_CONSTANT_Float_info read(DataInputStream in) throws IOException {
        float value = in.readFloat();
        return new CF_CONSTANT_Float_info(value);
    }
}
