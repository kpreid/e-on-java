package com.fudco.jclass;

// Copyright 2004 Hewlett Packard, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Object representing an internalized Java .class file.
 * <p/>
 * Files are read according to the format documented at
 * http://java.sun.com/docs/books/vmspec/2nd-edition/html/ClassFile.doc.html
 * <p/>
 * Method names follow the naming convention used in the Java class file spec
 * rather than what I would consider ideal Java method naming practice. This is
 * done to make clear the parallel between the various entities here and the
 * various entities in the spec document.
 */
public class ClassFile {

    private int myMagic;
    private int myMinor_version;
    private int myMajor_version;
    private CF_cp_info[] myConstant_pool;
    private int myAccess_flags;
    private int myThis_class;
    private int mySuper_class;
    private int[] myInterfaces;
    private CF_field_info[] myFields;
    private CF_method_info[] myMethods;
    private CF_attribute_info[] myAttributes;

    /**
     * Constructor.
     *
     * @param magic         The magic number identifying the class file format;
     *                      nominally it should have the value 0xCAFEBABE
     * @param minor_version Minor version number of this class file
     * @param major_version Major version number of this class file
     * @param constant_pool Array of the entries in the class' constant pool
     * @param access_flags  Flags bits denoting access permissions to and
     *                      properties of this class; values as defined in
     *                      ClassFileConstants.
     * @param this_class    Index into the constant pool of the
     *                      CONSTANT_Class_info object representing this class
     *                      itself.
     * @param super_class   Index into the constant pool of the
     *                      CONSTANT_Class_info object representing the
     *                      superclass of this class.
     * @param interfaces    Array of indexes into the constant pool of
     *                      CONSTANT_Class_info objects describing interfaces
     *                      implemented by this class.
     * @param fields        Array of field_info objects describing this class'
     *                      fields.
     * @param methods       Array of method_info objects describing this class'
     *                      methods.
     * @param attributes    Array of attribute objects describing this class'
     *                      attributes.
     */
    ClassFile(int magic,
              int minor_version,
              int major_version,
              CF_cp_info[] constant_pool,
              int access_flags,
              int this_class,
              int super_class,
              int[] interfaces,
              CF_field_info[] fields,
              CF_method_info[] methods,
              CF_attribute_info[] attributes) {
        myMagic = magic;
        myMinor_version = minor_version;
        myMajor_version = major_version;
        myConstant_pool = constant_pool;
        myAccess_flags = access_flags;
        myThis_class = this_class;
        mySuper_class = super_class;
        myInterfaces = interfaces;
        myFields = fields;
        myMethods = methods;
        myAttributes = attributes;
    }

    /**
     * Obtain this class file's magic number.
     *
     * @return the magic number from this class file.
     */
    public int magic() {
        return myMagic;
    }

    /**
     * Obtain this class file's minor version number.
     *
     * @return the minor version number from this class file.
     */
    public int minor_version() {
        return myMinor_version;
    }

    /**
     * Obtain this class file's major version number.
     *
     * @return the major version number from this class file.
     */
    public int major_version() {
        return myMajor_version;
    }

    /**
     * Obtain the number of entries in this class file's constant pool. Note
     * that per the class file spec this is really the number entries plus one,
     * as entry at index 0 is not used.
     *
     * @return the count of constant pool entries.
     */
    public int constant_pool_count() {
        return myConstant_pool.length;
    }

    /**
     * Obtain one of the entries in the constant pool.
     *
     * @param index The index number of the entry desired; this should be in
     *              the range 0 to constant_pool_count()-1.
     * @return the indicated constant pool entry.
     */
    public CF_cp_info constant_pool(int index) {
        return myConstant_pool[index];
    }

    /**
     * Obtain this class' access flags.
     *
     * @return a number whose bits denote access permissions and properties of
     *         this class.
     */
    public int access_flags() {
        return myAccess_flags;
    }

    /**
     * Obtain the constant pool index for this class' class descriptor.
     *
     * @return the index into the constant pool of a CONSTANT_Class_info entry
     *         describing this class.
     */
    public int this_class() {
        return myThis_class;
    }

    /**
     * Obtain the constant pool index for this class' superclass descriptor.
     *
     * @return the index into the constant pool of a CONSTANT_Class_info entry
     *         describing this class's superclass (this will be 0 if this is
     *         class java.lang.Object).
     */
    public int super_class() {
        return mySuper_class;
    }

    /**
     * Obtain the number of interfaces implemented by this class.
     *
     * @return the count of interfaces implemented by this class (this may be
     *         zero).
     */
    public int interfaces_count() {
        return myInterfaces.length;
    }

    /**
     * Obtain the constant pool index for one of the interfaces this class
     * implements.
     *
     * @param index The index number of the entry desired; this should be in
     *              the range 0 to interfaces_count()-1.
     * @return the constant pool entry index of the indicated interface.
     */
    public int interfaces(int index) {
        return myInterfaces[index];
    }

    /**
     * Obtain the number of fields this class has.
     *
     * @return the number of fields in this class (this may be zero).
     */
    public int fields_count() {
        return myFields.length;
    }

    /**
     * Obtain a descriptor for one of this class' fields.
     *
     * @param index The index number of the field desired; this should be in
     *              the range 0 to fields_count()-1.
     * @return a field_info object describing the indicated field.
     */
    public CF_field_info fields(int index) {
        return myFields[index];
    }

    /**
     * Obtain the number of methods this class has.
     *
     * @return the number of methods in this class (this may be zero).
     */
    public int methods_count() {
        return myMethods.length;
    }

    /**
     * Obtain a descriptor for one of this class' methods.
     *
     * @param index The index number of the method desired; this should be in
     *              the range 0 to methods_count()-1.
     * @return a method_info object describing the indicated method.
     */
    public CF_method_info methods(int index) {
        return myMethods[index];
    }

    /**
     * Obtain the number of attributes this class has.
     *
     * @return the number of attributes in this class (this may be zero).
     */
    public int attributes_count() {
        return myAttributes.length;
    }

    /**
     * Obtain a descriptor for one of this class' attributes.
     *
     * @param index The index number of the attribute desired; this should be
     *              in the range 0 to attributes_count()-1.
     * @return a attribute_info object describing the indicated attribute.
     */
    public CF_attribute_info attributes(int index) {
        return myAttributes[index];
    }

    /**
     * Read a class file from a DataInputStream and return the resulting
     * descriptor object.
     *
     * @param in The DataInputStream to read from; this should be positioned at
     *           the first byte of the class file being read.
     * @return a ClassFile object representing the class file information read
     *         from 'in'.
     */
    public static ClassFile read(DataInputStream in) throws IOException {
        int magic = in.readInt();
        int minor_version = in.readUnsignedShort();
        int major_version = in.readUnsignedShort();

        int constant_pool_count = in.readUnsignedShort();
        CF_cp_info[] constant_pool = new CF_cp_info[constant_pool_count];
        constant_pool[0] = null;
        for (int i = 1; i < constant_pool_count; ++i) {
            constant_pool[i] = CF_cp_info.readcp(in);
            if (constant_pool[i].isDoubleSize()) {
                constant_pool[++i] = null;
            }
        }

        int access_flags = in.readUnsignedShort();
        int this_class = in.readUnsignedShort();
        int super_class = in.readUnsignedShort();

        int interfaces_count = in.readUnsignedShort();
        int[] interfaces = new int[interfaces_count];
        for (int i = 0; i < interfaces_count; ++i) {
            interfaces[i] = in.readUnsignedShort();
        }

        int fields_count = in.readUnsignedShort();
        CF_field_info[] fields = new CF_field_info[fields_count];
        for (int i = 0; i < fields_count; ++i) {
            fields[i] = CF_field_info.read(in);
        }

        int methods_count = in.readUnsignedShort();
        CF_method_info[] methods = new CF_method_info[methods_count];
        for (int i = 0; i < methods_count; ++i) {
            methods[i] = CF_method_info.read(in);
        }

        int attributes_count = in.readUnsignedShort();
        CF_attribute_info[] attributes =
          new CF_attribute_info[attributes_count];
        for (int i = 0; i < attributes_count; ++i) {
            attributes[i] = CF_attribute_info.read(in);
        }

        return new ClassFile(magic,
                             minor_version,
                             major_version,
                             constant_pool,
                             access_flags,
                             this_class,
                             super_class,
                             interfaces,
                             fields,
                             methods,
                             attributes);
    }

    /**
     * Read a class file and return the resulting descriptor object.
     *
     * @param filename The pathname of the class file to read from.
     * @return a ClassFile object representing the class file information read
     *         from the indicated file.
     */
    public static ClassFile read(String filename) throws IOException {
        DataInputStream in =
          new DataInputStream(new BufferedInputStream(new FileInputStream(
            filename)));
        return read(in);
    }
}
