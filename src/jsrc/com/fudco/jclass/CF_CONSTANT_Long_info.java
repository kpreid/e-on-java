package com.fudco.jclass;

// Copyright 2004 Hewlett Packard, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Constant pool entry describing a long constant.
 */
public class CF_CONSTANT_Long_info extends CF_cp_info {

    private long myValue;

    /**
     * Constructor.
     *
     * @param value The long value of this constant.
     */
    CF_CONSTANT_Long_info(long value) {
        myValue = value;
    }

    /**
     * Obtain the value of this constant.
     *
     * @return the long value of this constant.
     */
    public long value() {
        return myValue;
    }

    /**
     * Test if this entry is a "double size" entry: longs and doubles take up
     * two slots in the constant_pool.
     *
     * @return true: a long entry is double sized.
     */
    public boolean isDoubleSize() {
        return true;
    }

    /**
     * Read and return a CONSTANT_Long_info object from a DataInputStream.
     *
     * @param in The DataInputStream to read from; this should be positioned at
     *           the first byte after the type code byte of the constant pool
     *           entry being read.
     */
    static CF_CONSTANT_Long_info read(DataInputStream in) throws IOException {
        long value = in.readLong();
        return new CF_CONSTANT_Long_info(value);
    }
}
