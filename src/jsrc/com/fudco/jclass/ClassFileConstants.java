package com.fudco.jclass;

// Copyright 2004 Hewlett Packard, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

/**
 * "Interface" defining various constants used in Java .class files.
 * <p/>
 * These constants are taken from the Java class file spec documented at
 * http://java.sun.com/docs/books/vmspec/2nd-edition/html/ClassFile.doc.html
 */
public interface ClassFileConstants {

    /* Type codes for contant pool entries */
    static final int CONSTANT_Class = 7;
    static final int CONSTANT_Double = 6;
    static final int CONSTANT_Fieldref = 9;
    static final int CONSTANT_Float = 4;
    static final int CONSTANT_Integer = 3;
    static final int CONSTANT_InterfaceMethodref = 11;
    static final int CONSTANT_Long = 5;
    static final int CONSTANT_Methodref = 10;
    static final int CONSTANT_NameAndType = 12;
    static final int CONSTANT_String = 8;
    static final int CONSTANT_Utf8 = 1;

    /* Flag bits for classes, fields, and methods. */
    static final int ACC_PUBLIC = 0x0001;
    static final int ACC_PRIVATE = 0x0002;
    static final int ACC_PROTECTED = 0x0004;
    static final int ACC_STATIC = 0x0008;
    static final int ACC_FINAL = 0x0010;
    static final int ACC_SUPER = 0x0020;
    static final int ACC_SYNCHRONIZED = 0x0020;
    static final int ACC_VOLATILE = 0x0040;
    static final int ACC_TRANSIENT = 0x0080;
    static final int ACC_NATIVE = 0x0100;
    static final int ACC_INTERFACE = 0x0200;
    static final int ACC_ABSTRACT = 0x0400;
}
