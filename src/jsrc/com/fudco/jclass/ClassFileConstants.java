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

    /* Type codes for contant pool entries */ int CONSTANT_Class = 7;
    int CONSTANT_Double = 6;
    int CONSTANT_Fieldref = 9;
    int CONSTANT_Float = 4;
    int CONSTANT_Integer = 3;
    int CONSTANT_InterfaceMethodref = 11;
    int CONSTANT_Long = 5;
    int CONSTANT_Methodref = 10;
    int CONSTANT_NameAndType = 12;
    int CONSTANT_String = 8;
    int CONSTANT_Utf8 = 1;

    /* Flag bits for classes, fields, and methods. */ int ACC_PUBLIC = 0x0001;
    int ACC_PRIVATE = 0x0002;
    int ACC_PROTECTED = 0x0004;
    int ACC_STATIC = 0x0008;
    int ACC_FINAL = 0x0010;
    int ACC_SUPER = 0x0020;
    int ACC_SYNCHRONIZED = 0x0020;
    int ACC_VOLATILE = 0x0040;
    int ACC_TRANSIENT = 0x0080;
    int ACC_NATIVE = 0x0100;
    int ACC_INTERFACE = 0x0200;
    int ACC_ABSTRACT = 0x0400;
}
