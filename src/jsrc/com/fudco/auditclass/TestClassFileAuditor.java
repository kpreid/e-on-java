package com.fudco.auditclass;

// Copyright 2004 Hewlett Packard, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import com.fudco.jclass.ClassFile;

import java.io.IOException;

/**
 * Program for testing the Java class file auditor.
 * <p/>
 * Usage: java com.fudco.auditclass.TestClassFileAuditor <classfilename>...
 */
public class TestClassFileAuditor {

    private TestClassFileAuditor() {
    }

    static public void main(String[] args) {
        Forbidifier forbidifier = new TestForbidifier();
        for (int i = 0; i < args.length; ++i) {
            try {
                ClassFile classFile = ClassFile.read(args[i]);
                ClassFileAuditor auditor =
                  new ClassFileAuditor(classFile, forbidifier);
                if (auditor.audit()) {
                    System.err.println(args[i] + " is unacceptable");
                } else {
                    System.err.println(args[i] + " is OK");
                }
            } catch (IOException e) {
                System.err
                  .println("problem reading class file " + args[i] + ": " + e);
            }
        }
    }
}
