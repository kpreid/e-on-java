package com.fudco.jclass;

// Copyright 2004 Hewlett Packard, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............


/**
 * Shared base class for constant pool entries which represent references
 * (Fieldref, Methodref, and InterfaceMethodref).
 */
public abstract class CF_cp_ref extends CF_cp_info {

    private int myClass_index;
    private int myName_and_type_index;

    /**
     * Common constructor.
     *
     * @param class_index         Index into the constant pool of the
     *                            CONSTANT_Class_info object for the class or
     *                            interface relative to which a reference is
     *                            being expressed.
     * @param name_and_type_index Index into the constant pool of the
     *                            CONSTANT_NameAndType_info object describing
     *                            the method or field being referenced.
     */
    CF_cp_ref(int class_index, int name_and_type_index) {
        myClass_index = class_index;
        myName_and_type_index = name_and_type_index;
    }

    /**
     * Obtain the class index for this reference.
     *
     * @return the index into the constant pool of the CONSTANT_Class_info
     *         entry describing the class relative to which this reference is
     *         being expressed.
     */
    public int class_index() {
        return myClass_index;
    }

    /**
     * Obtain the name-and-type index for this reference.
     *
     * @return the index into the constant pool of the CONSTANT_NameAndType_info
     *         entry describing the method or field being * referenced.
     */
    public int name_and_type_index() {
        return myName_and_type_index;
    }
}
