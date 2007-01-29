package org.erights.e.elib.vat;

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

import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.prim.Message;
import org.erights.e.elib.ref.Resolver;

import java.io.IOException;

/**
 * A PendingDelivery is a Runnable that will run() a single delivery event.
 * <p/>
 * A PendingDelivery is a pair of a Message and the object to deliver it to.
 * <p/>
 * NOTE!  All the methods of this class trust their callers to 1) only provide
 * interned strings as verb, and 2) not modify an args array after passing it
 * in. Btw, all source code literal strings are automatically intern()ed.
 *
 * @author Mark S. Miller
 */
class PendingDelivery extends PendingEvent {

    private final Object myReceiver;

    private final Resolver myOptResolver;

    private final boolean myNowFlag;

    private final String myVerb;

    private final Object[] myArgs;

    /**
     * Reify a pending delivery as sent by the current context.
     *
     * @param vat The Vat onto which this PendingEvent will be queued. Note
     *            that this may be different than {@link Vat#getCurrentVat()}.
     */
    PendingDelivery(Vat vat,
                    Object rec,
                    Resolver optResolver,
                    boolean nowFlag,
                    String verb,
                    Object[] args) {
        super("SCsend", vat);
        myReceiver = rec;
        myOptResolver = optResolver;
        myNowFlag = nowFlag;
        myVerb = verb;
        myArgs = args;
        trace();
    }

    /**
     * Reify a pending delivery as sent by msg's sending context.
     *
     * @param vat The Vat onto which this PendingEvent will be queued. Note
     *            that this may be different than {@link Vat#getCurrentVat()}.
     */
    PendingDelivery(Vat vat, Object rec, boolean nowFlag, Message msg) {
        super("SCresolve", vat, msg.getSendingContext());
        myReceiver = rec;
        myOptResolver = msg.getOptResolver();
        myNowFlag = nowFlag;
        myVerb = msg.getVerb();
        myArgs = msg.getArgs();
        trace();
    }

    /**
     * Actually do the delivery and report the outcome.
     */
    public void innerRun() {
        Object value;
        try {
            if (myNowFlag) {
                value = E.callAll(myReceiver, myVerb, myArgs);
            } else {
                if (null == myOptResolver) {
                    E.sendAllOnly(myReceiver, myVerb, myArgs);
                    value = null;
                } else {
                    value = E.sendAll(myReceiver, myVerb, myArgs);
                }
            }
        } catch (Throwable problem) {
            if (null != myOptResolver) {
                myOptResolver.smash(problem);
            }
            report("Problem in turn", problem);
            return;
        }
        if (null != myOptResolver) {
            myOptResolver.resolve(value);
        }
    }

    /**
     *
     */
    public void __printOn(TextWriter out) throws IOException {
        out.lnPrint(E.abbrevCall(myReceiver,
                                 myNowFlag ? "." : " <- ",
                                 myVerb,
                                 myArgs));
        printContextOn(out.indent("--- "));
        out.println();
    }
}
