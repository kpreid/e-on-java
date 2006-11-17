package org.erights.e.elib.tests;

/*
The contents of this file are subject to the Electric Communities E Open
Source Code License Version 1.0 (the "License"); you may not use this file
except in compliance with the License. You may obtain a copy of the License
at http://www.communities.com/EL/.

Software distributed under the License is distributed on an "AS IS" basis,
WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
the specific language governing rights and limitations under the License.

The Original Code is the Distributed E Language Implementation, released
July 20, 1998.

The Initial Developer of the Original Code is Electric Communities.
Copyright (C) 1998 Electric Communities. All Rights Reserved.

Contributor(s): ______________________________________.
*/

//import org.erights.e.elib.ref.ExternalRef;

/**
 * Worker classes for the mult-queue tester. This class is instantiated by
 * TestMultiQMain.java. TestMultiQ is a simple class that 1) receives a
 * reference to another "partner" TestMultiQ class, and 2) upon receiving a
 * message to add an integer value to an internal counter, testMultiQ updates
 * the internal counter, and then sends the same message to its partner
 * instance (otherRef). See TestMultiQMain for the top level story.
 */
public class TestMultiQ {

//
//    private ExternalRef otherRef = null;   // our ref to the "other" TestMultiQ.
//
//    private String myName = null;     // the name of this instance.
//
//    private int myIntVal = 0;      // the counter.
//
//    /**
//     * Constructor -- set the name of this TestMultiQ.
//     */
//    public TestMultiQ(String name) {
//        myName = name;
//    }
//
//    /**
//     * addVal(Integer val) takes a value and adds it to the internal counter.
//     * An addVal message, with an argument of 1, is then sent to the "other" TestMultiQ
//     * instance.
//     */
//    public Integer addVal(Integer val) {
//        PrintStreamWriter.out().println(myName + " : Adding " + val + " to " + myIntVal);
//        myIntVal += val.intValue();
//        E.sendOnly(otherRef, "addVal", new Integer(1));
//        return new Integer(myIntVal);
//    }
//
//    /**
//     * run -- required to support closures, which this test doesnt use.
//     */
//    public Object run(Object resolution) {
//        PrintStreamWriter.out().println(myName + " : result is " + resolution);
//        return null;
//    }
//
//    /**
//     * setTarget -- Used to set the ExternalRef of the "other" TestMultiQ.
//     */
//    public void setTarget(ExternalRef ref) {
//        PrintStreamWriter.out().println(myName + " : setTarget(" + ref + ")");
//        otherRef = ref;
//        // Set the thread name so our name will show up in thread dumps.
//        Thread.currentThread().setName(myName);
//    }
//
//    /**
//     * startSending -- start the whole thing running bu sending an addval(1) to
//     * the "other" testMultiQ.
//     */
//    public void startSending() {
//        PrintStreamWriter.out().println(myName + " : startSending");
//        E.sendOnly(otherRef, "addVal", new Integer(1));
//    }
}
