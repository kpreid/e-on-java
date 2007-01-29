package org.erights.e.elib.prim.tests;

import org.erights.e.elib.prim.E;
import org.erights.e.elib.vat.Vat;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

/**
 * @author Jonathan Rees
 * @author fixes by Mark S. Miller
 */
class JARTestE {

    static String sendStatus = "didn't do the call";

    static String callStatus = "didn't do the send";

    private JARTestE() {
    }

    static public void main(String[] args) {
        Vat vat = Vat.make("headless", "test E");
        vat.enqueue(new calltest());
        vat.enqueue(new sendtest());
        try {
            java.lang.Thread.sleep(1000);    //wait one second
        } catch (InterruptedException e) {
            System.err.println("interrupted");
        }
        System.out.println(sendStatus);
        System.out.println(callStatus);
        vat.orderlyShutdown(new RuntimeException("just because"));
    }

    static public class calltest implements Runnable {

        public void run() {
            E.call(this, "doIt");
        }

        public void doIt() {
            callStatus = "did the call";
        }
    }

    static public class sendtest implements Runnable {

        public void run() {
            E.sendOnly(this, "doIt");
        }

        public void doIt() {
            sendStatus = "did the send";
        }
    }
}
