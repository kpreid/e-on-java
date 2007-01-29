package com.fudco.auditclass;

// Copyright 2004 Hewlett Packard, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

/**
 * Exemplar Forbidifier.
 * <p/>
 * Forbids usage of imported classes, methods, or fields whose names contain
 * the letter 'e' (or 'E').
 */
class TestForbidifier implements Forbidifier {

    private boolean goofyTest(String victim) {
        for (int i = 0; i < victim.length(); ++i) {
            char c = victim.charAt(i);
            if ('e' == c || 'E' == c) {
                return true;
            }
        }
        return false;
    }

    public boolean forbiddenClass(String className) {
        return goofyTest(className);
    }

    public boolean forbiddenMember(String className,
                                   String memberName,
                                   String signature) {
        return goofyTest(memberName);
    }
}
