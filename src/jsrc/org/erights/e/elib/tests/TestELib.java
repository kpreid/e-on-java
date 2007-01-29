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

import org.erights.e.develop.exception.PrintStreamWriter;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.ref.OneArgFuncAdapter;
import org.erights.e.meta.java.math.EInt;

/**
 * TestELib -- simple tester to send a message to an object and execute a
 * closure method.
 */
public class TestELib {

    /**
     * TestClass -- inner class that has the method we'll be using as a
     * message. (Note that this doesn't need to be an inner class.)
     * <p/>
     * To run it: java org.erights.e.elib.tests.TestELib Output should be:
     * Adding one... Adding one to 5 result is 6
     */
    public class TestClass {

        /**
         * addOne -- takes an int32 value and adds one to it, then returns the
         * new value.
         */
        public Number addOne(Integer val) throws Exception {
            PrintStreamWriter.stdout()
              .println("addOne, val is " + val.intValue() + "... ");
            if (5 == val.intValue()) {
                PrintStreamWriter.stdout()
                  .println("addOne... throwing exception");
                throw new Exception("foo");
            } else {
                PrintStreamWriter.stdout()
                  .println(
                    "addOne... new value is " + val + 1 + ", returning it");
                return EInt.valueOf(val.intValue() + 1);
            }
        }
    }

    static public void main(String[] args) {
        TestELib tester = new TestELib();
        tester.testit(Integer.parseInt(args[0], 10));
    }

    /**
     * run -- this is the closure for the message send.
     *
     * @param Object resolution -- the result value from the message send.
     */
    public Object run(Object resolution) {
        PrintStreamWriter.stdout().println("result is " + resolution);
        return null;
    }

    /**
     * testit -- the test code.
     */
    public void testit(int startVal) {
        // create the testClass that we'll be sending a message to.
        TestClass target = new TestClass();

        PrintStreamWriter.stdout().println("Adding " + startVal + "...");
        // Send the message. The following is the
        // equivelent of: returnVal = target.addOne(startVal);
        // "p" is the returned promise.
        Object p = E.send(target, "addOne", EInt.valueOf(startVal));

        // Now send "__whenMoreResolved" to the promise p.
        // "this" will be the NEAR reactor for the promise.
        // What this means is that our run() method (closure)
        // will get called when the addOne returns a value.
        //E.sendOnly(p, "__whenMoreResolved", this);
        OneArgFuncAdapter.adapt(p,
                                "__whenMoreResolved",
                                this,
                                "reactToAddOneResolved");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            PrintStreamWriter.stdout().println("got exception " + ex);
        }
    }

    /**
     *
     */
    public void reactToAddOneResolved(Object resolution) {
        PrintStreamWriter.stdout()
          .println("WhenAddOneresolved... result is " + resolution);
    }
}
