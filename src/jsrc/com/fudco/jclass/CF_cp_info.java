package com.fudco.jclass;

// Copyright 2004 Hewlett Packard, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Common base class of Java class file constant pool entries.
 * <p/>
 * Entries are read according to the format documented at
 * http://java.sun.com/docs/books/vmspec/2nd-edition/html/ClassFile.doc.html
 */
public abstract class CF_cp_info implements ClassFileConstants {

    /**
     * Base constructor. Declared only to ensure proper scoping.
     */
    CF_cp_info() {
    }

    /**
     * Test if this entry is a "double size" entry: longs and doubles take up
     * two slots in the constant_pool.
     *
     * @return false: the base case is that an entry is not double sized.
     */
    public boolean isDoubleSize() {
        return false;
    }

    /**
     * Read and return a constant pool entry from a DataInputStream.
     * <p/>
     * Note that this method is named 'readcp' (instead of 'read', which is
     * what we want it to be) because all the various subclasses define static
     * methods 'read' and the compiler would believe them to be overriding this
     * method, which is not allowed with static methods (and which we obviously
     * aren't doing and it's unambiguous because the static reference has to
     * explicitly mention the class name anyway. Grrr.)
     *
     * @param in The DataInputStream to read from; this should be positioned at
     *           the first byte of the constant pool entry being read.
     * @return a cp_info object representing the contant pool entry read from
     *         'in'.
     */
    static CF_cp_info readcp(DataInputStream in) throws IOException {
        int tag = in.readUnsignedByte();
        switch (tag) {
        case CONSTANT_Class:
            return CF_CONSTANT_Class_info.read(in);
        case CONSTANT_Fieldref:
            return CF_CONSTANT_Fieldref_info.read(in);
        case CONSTANT_Methodref:
            return CF_CONSTANT_Methodref_info.read(in);
        case CONSTANT_InterfaceMethodref:
            return CF_CONSTANT_InterfaceMethodref_info.read(in);
        case CONSTANT_String:
            return CF_CONSTANT_String_info.read(in);
        case CONSTANT_Integer:
            return CF_CONSTANT_Integer_info.read(in);
        case CONSTANT_Float:
            return CF_CONSTANT_Float_info.read(in);
        case CONSTANT_Long:
            return CF_CONSTANT_Long_info.read(in);
        case CONSTANT_Double:
            return CF_CONSTANT_Double_info.read(in);
        case CONSTANT_NameAndType:
            return CF_CONSTANT_NameAndType_info.read(in);
        case CONSTANT_Utf8:
            return CF_CONSTANT_Utf8_info.read(in);
        default:
            throw new IOException("invalid constant pool tag " + tag);
        }
    }
}
