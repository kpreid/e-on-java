package com.fudco.auditclass;

// Copyright 2004 Hewlett Packard, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import com.fudco.jclass.CF_CONSTANT_Class_info;
import com.fudco.jclass.CF_CONSTANT_NameAndType_info;
import com.fudco.jclass.CF_CONSTANT_Utf8_info;
import com.fudco.jclass.CF_cp_info;
import com.fudco.jclass.CF_cp_ref;
import com.fudco.jclass.CF_field_info;
import com.fudco.jclass.CF_method_info;
import com.fudco.jclass.ClassFile;
import com.fudco.jclass.ClassFileConstants;

/**
 * Reference implementation of the class file auditor.
 */
public class ClassFileAuditor implements ClassFileConstants {

    private AuditUtils u;
    private ClassFile myClassFile;
    private Forbidifier myForbidifier;

    /**
     * Prepare to audit.
     *
     * @param classFile   The class file to be audited
     * @param forbidifier Forbidifier for testing class and method references
     */
    public ClassFileAuditor(ClassFile classFile, Forbidifier forbidifier) {
        myClassFile = classFile;
        myForbidifier = forbidifier;
        u = new AuditUtils(myClassFile);
    }

    /**
     * Perform the audit. The audit checks for:
     * -- non-final static fields
     * -- static native methods
     * -- class and member references that the forbidifier finds unacceptable
     * <p/>
     * Since this is a reference implementation, it finds all problems rather
     * than rejecting the class file upon detection of the first problem. It
     * also prints a description of each problem found to System.err.
     *
     * @return true if the class has one or more problems that render it
     *         unacceptable.
     */
    public boolean audit() {
        boolean result = false;

        for (int i = 0; i < myClassFile.fields_count(); ++i) {
            result |= auditField(myClassFile.fields(i));
        }

        for (int i = 0; i < myClassFile.methods_count(); ++i) {
            result |= auditMethod(myClassFile.methods(i));
        }

        for (int i = 0; i < myClassFile.constant_pool_count(); ++i) {
            CF_cp_info cpEntry = myClassFile.constant_pool(i);
            if (cpEntry instanceof CF_cp_ref) {
                result |= auditMemberRef((CF_cp_ref)cpEntry);
            }
        }
        return result;
    }

    /**
     * Test a field descriptor as to whether the field is acceptable. It is
     * unacceptable if it is static and not final.
     *
     * @param field Descriptor for the field to be tested
     * @return true if the field is unacceptable, false if it is OK.
     */
    private boolean auditField(CF_field_info field) {
        int flags = field.access_flags();
        if ((flags & ACC_STATIC) != 0 && (flags & ACC_FINAL) == 0) {
            System.err.println("non-final static field: " +
                               AuditUtils.pSig(
                                 u.pString(field.descriptor_index()),
                                 null,
                                 u.pString(field.name_index())));
            return true;
        } else {
            return false;
        }
    }

    /**
     * Test a member reference as to whether it is acceptable. It is
     * unacceptable if the forbidifier doesn't like it.
     *
     * @param ref Descriptor for the reference to be tested
     * @return true if the reference is unacceptable, false if it is OK.
     */
    private boolean auditMemberRef(CF_cp_ref ref) {
        CF_CONSTANT_Class_info classInfo = (CF_CONSTANT_Class_info)
          myClassFile.constant_pool(ref.class_index());
        String className =
          ((CF_CONSTANT_Utf8_info)
          myClassFile.constant_pool(classInfo.name_index())).asString();
        if (myForbidifier.forbiddenClass(className)) {
            System.err.println("forbidden unsafe class: " +
                               AuditUtils.pClass(className));
            return true;
        }

        CF_CONSTANT_NameAndType_info ntInfo = (CF_CONSTANT_NameAndType_info)
          myClassFile.constant_pool(ref.name_and_type_index());
        String memberName =
          ((CF_CONSTANT_Utf8_info)
          myClassFile.constant_pool(ntInfo.name_index())).asString();
        String signature =
          ((CF_CONSTANT_Utf8_info)
          myClassFile.constant_pool(ntInfo.descriptor_index())).asString();
        if (myForbidifier.forbiddenMember(className, memberName, signature)) {
            System.err.println("forbidden unsafe member: " +
                               AuditUtils.pSig(signature,
                                               AuditUtils.pClass(className),
                                               memberName));
            return true;
        }
        return false;
    }

    /**
     * Test a method descriptor as to whether the method is acceptable. It is
     * unacceptable if it is static and native.
     *
     * @param method Descriptor for the method to be tested
     * @return true if the method is unacceptable, false if it is OK.
     */
    private boolean auditMethod(CF_method_info method) {
        int flags = method.access_flags();
        if ((flags & ACC_STATIC) != 0 && (flags & ACC_NATIVE) != 0) {
            System.err.println("static native method: " +
                               AuditUtils.pSig(
                                 u.pString(method.descriptor_index()),
                                 null,
                                 u.pString(method.name_index())));
            return true;
        } else {
            return false;
        }
    }
}
