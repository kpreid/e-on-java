package com.fudco.jclass;

// Copyright 2004 Hewlett Packard, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Constant pool entry describing a reference to a field of an object.
 */
public class CF_CONSTANT_Fieldref_info extends CF_cp_ref {

    /**
     * Constructor.
     *
     * @param class_index         Index into the constant pool of the
     *                            CONSTANT_Class_info object for the class relative to
     *                            which a field reference is being expressed.
     * @param name_and_type_index Index into the constant pool of the
     *                            CONSTANT_NameAndType_info object describing the field being
     *                            referenced.
     */
    CF_CONSTANT_Fieldref_info(int class_index, int name_and_type_index) {
        super(class_index, name_and_type_index);
    }

    /**
     * Read and return a CONSTANT_Fieldref_info object from a DataInputStream.
     *
     * @param in The DataInputStream to read from; this should be positioned
     *           at the first byte after the type code byte of the constant pool entry
     *           being read.
     */
    static CF_CONSTANT_Fieldref_info read(DataInputStream in)
      throws IOException {
        int class_index = in.readUnsignedShort();
        int name_and_type_index = in.readUnsignedShort();
        return new CF_CONSTANT_Fieldref_info(class_index, name_and_type_index);
    }
}
