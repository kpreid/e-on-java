package com.fudco.auditclass;

// Copyright 2004 Hewlett Packard, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import com.fudco.jclass.CF_CONSTANT_Utf8_info;
import com.fudco.jclass.ClassFile;
import com.fudco.jclass.ClassFileConstants;

/**
 * Utility routines for pretty-printing stuff found in a Java class file.
 */
class AuditUtils implements ClassFileConstants {

    private ClassFile myClassFile;

    /**
     * Constructor.
     *
     * @param classFile Class file for resolving UTF-8 string references.
     */
    AuditUtils(ClassFile classFile) {
        myClassFile = classFile;
    }

    /**
     * Convert a fully qualified class name from internal to normal form.
     *
     * @param in The internal class name (e.g., "java/lang/String")
     * @return the normal form of 'in' (e.g., "java.lang.String")
     */
    static String pClass(String in) {
        String result = "";
        for (int i = 0; i < in.length(); ++i) {
            char c = in.charAt(i);
            if (c == '/') {
                result += '.';
            } else {
                result += c;
            }
        }
        return result;
    }

    /**
     * Convert a internal form type signature to a friendlier form.
     *
     * @param in        The type signature string in internal form
     * @param baseClass The class name against which a member reference of the
     *                  given type is being expressed, or null if no class is
     *                  implied
     * @param name      The name of the member whose type is being described
     * @return a prettified version of the parameters.
     *         <p/>
     *         For example, if passed "(I)Lcom/fudco/foo/Bar;", "Wingo", and
     *         "lmf", the result will be the string "com.fudco.foo.Bar
     *         Wingo.lmf(int)"
     */
    static String pSig(String in, String baseClass, String name) {
        int inptr[] = {0};
        return pSigScan(in, inptr, baseClass, name);
    }

    /**
     * Internal worker routine in support of pSig()
     */
    static String pSigScan(String in,
                           int inptr[],
                           String baseClass,
                           String name) {
        String pre;
        String post = "";

        switch (in.charAt(inptr[0]++)) {
        case'B':
            pre = "byte";
            break;
        case'C':
            pre = "char";
            break;
        case'D':
            pre = "double";
            break;
        case'F':
            pre = "float";
            break;
        case'I':
            pre = "int";
            break;
        case'J':
            pre = "long";
            break;
        case'S':
            pre = "short";
            break;
        case'V':
            pre = "void";
            break;
        case'Z':
            pre = "boolean";
            break;
        case'[':
            pre = pSigScan(in, inptr, null, null) + "[]";
            break;
        case'L': {
            char c = in.charAt(inptr[0]++);
            pre = "";
            while (c != ';') {
                if (c == '/') {
                    pre += '.';
                } else {
                    pre += c;
                }
                c = in.charAt(inptr[0]++);
            }
            break;
        }
        case'(':
            post = "(";
            while (in.charAt(inptr[0]) != ')') {
                if (post.length() > 1) {
                    post += ", ";
                }
                post += pSigScan(in, inptr, null, null);
            }
            post += ")";
            inptr[0]++;
            pre = pSigScan(in, inptr, null, null);
            break;
        default:
            pre = "huh??";
            break;
        }
        if (name == null) {
            return pre + post;
        } else if (baseClass == null) {
            return pre + " " + name + post;
        } else {
            return pre + " " + baseClass + "." + name + post;
        }
    }

    /**
     * Obtain a printable string given an index into the constant pool of a
     * UTF-8 constant.
     *
     * @param index The index of the string desired.
     * @return the indicated string. The string is massaged so that it prints
     *         nicely: newlines are converted to "\n" and quotes to '\"'
     */
    String pString(int index) {
        CF_CONSTANT_Utf8_info info =
          (CF_CONSTANT_Utf8_info)myClassFile.constant_pool(index);
        String raw = info.asString();
        String result = "";

        for (int i = 0; i < raw.length(); ++i) {
            if (raw.charAt(i) == '\n') {
                result += "\\n";
            } else if (raw.charAt(i) == '"') {
                result += "\\\"";
            } else {
                result += raw.charAt(i);
            }
        }
        return result;
    }
}
