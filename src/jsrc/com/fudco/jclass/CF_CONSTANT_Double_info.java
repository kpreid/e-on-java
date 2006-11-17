package com.fudco.jclass;

// Copyright 2004 Hewlett Packard, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Constant pool entry describing a double constant.
 */
public class CF_CONSTANT_Double_info extends CF_cp_info {

    private double myValue;

    /**
     * Constructor.
     *
     * @param value The double value of this constant.
     */
    CF_CONSTANT_Double_info(double value) {
        myValue = value;
    }

    /**
     * Obtain the value of this constant.
     *
     * @return the double value of this constant.
     */
    public double value() {
        return myValue;
    }

    /**
     * Test if this entry is a "double size" entry: longs and doubles take up
     * two slots in the constant_pool.
     *
     * @return true: a double entry is double sized.
     */
    public boolean isDoubleSize() {
        return true;
    }

    /**
     * Read and return a CONSTANT_Double_info object from a DataInputStream.
     *
     * @param in The DataInputStream to read from; this should be positioned at
     *           the first byte after the type code byte of the constant pool
     *           entry being read.
     */
    static CF_CONSTANT_Double_info read(DataInputStream in)
      throws IOException {
        double value = in.readDouble();
        return new CF_CONSTANT_Double_info(value);
    }
}
