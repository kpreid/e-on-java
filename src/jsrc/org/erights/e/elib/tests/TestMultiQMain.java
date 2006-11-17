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


/**
 * TestMultiQMain -- test elib message sending using two run queues.
 * Functionality tested: org.erights.e.elib.prim.E.sendOnly()
 * org.erights.e.elib.vat.Vat  - multiple threads. org.erights.e.elib.helpers.ExternalRef
 * High level bits: We create two objects (TestMultiQ) that keep a counter.
 * Each object will get a reference to the other. One object will be told to
 * start sending to the other object. When the message is received, the
 * receiver will increment a count, and send the same message to the other
 * object. The result is that the two counts just keep increasing. See source
 * comments for more info.
 */
public class TestMultiQMain {

//
//    static public void main(String[] args) {
//        TestMultiQMain tester = new TestMultiQMain();
//        tester.testit();
//    }
//
//    public void testit() {
//        // Create a couple TestMultiQ objects... target1 and target2.
//        // Tell each TestMultiQ object what their respective names are.
//        TestMultiQ target1 = new TestMultiQ("target1");
//        TestMultiQ target2 = new TestMultiQ("target2");
//
//        // Create a couple ExternalRefs to the target objects. For each
//        // ref we will allocate a new Vat, which embodies a new run queue.
//        // Also, we'll specify the target object for each ref.
//        // Creating the new Vat instances also has the effect of creating
//        // and starting a thread for each Vat instance.
//        ExternalRef ref1 =
//          new ExternalRef(Vat.make("headless", "t1"),
//                          target1);
//        ExternalRef ref2 =
//          new ExternalRef(Vat.make("headless", "t2"),
//                          target2);
//
//        // Give the target2 ExternalRef to target1.
//        PrintStreamWriter.out().println("Sending ref2 to target1...");
//        E.sendOnly(ref1, // where to send this -- ref1 ==> target1
//                   "setTarget", // target1's message (method)
//                   ref2);           // the arg -- the ref to target2
//
//        // Give the target1 ExternalRef to target2.
//        PrintStreamWriter.out().println("Sending ref1 to target2...");
//        E.sendOnly(ref2, // where to send this -- ref2 ==> target2
//                   "setTarget", // target2's message (method)
//                   ref1);           // the arg -- the ref to target2
//
//        // Finally, tell target1 to start the process going
//        E.sendOnly(ref1, "startSending");
//
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException ex) {
//            PrintStreamWriter.out().println("got exception " + ex);
//        }
//    }
}
