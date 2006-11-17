package com.fudco.auditclass;

// Copyright 2004 Hewlett Packard, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

/**
 * Interface for a Forbidifier, an object which tests class file imports for
 * safety.
 */
interface Forbidifier {

    /**
     * Test if a class is forbidden from use.
     *
     * @param className The name of the class to test. This will be in internal
     *                  form, e.g., the class java.lang.System will be
     *                  represented by the string "java/lang/System"
     * @return true if the class should be forbidden, false if it is acceptable
     *         to use.
     */
    boolean forbiddenClass(String className);

    /**
     * Test if a member reference is forbidden from use. Note: this is used to
     * distinguish whether a specific member of an otherwise acceptable class
     * is forbidden (in other words, forbiddenClass(className) has already
     * returned false) hence it should not generally be necessary to test the
     * class itself for acceptability here.
     *
     * @param className  The name of the class whose member is being tested.
     *                   This will be in internal form, e.g., the class
     *                   java.lang.System will be represented by the string
     *                   "java/lang/System"
     * @param memberName The name of the member (field or method) being tested
     * @param signature  The type signature of the member. This will be in
     *                   internal form, e.g., a method taking an int parameter
     *                   and returning a PrintStream will have a signature of
     *                   the form "(I)Ljava/io/PrintStream;".
     * @return true if use of the member should be forbidden, false if it is
     *         acceptable to use.
     */
    boolean forbiddenMember(String className,
                            String memberName,
                            String signature);
}
