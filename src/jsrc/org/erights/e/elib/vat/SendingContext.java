package org.erights.e.elib.vat;

import org.erights.e.develop.trace.Trace;
import org.erights.e.elib.oldeio.EPrintable;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.E;

import java.io.IOException;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

/**
 * Records the context it was created in, so that it can be included in a later
 * report.
 *
 * @author Mark S. Miller
 */
public final class SendingContext implements EPrintable {

    /**
     * The Vat of the event that asked for this to be queued, or null if the
     * request came from outside any vats.
     */
    private final Vat myOptSendingVat;

    /**
     * The ticket number within myOptSendingVat of the event that asked for
     * this to be queued, or -1 if the request came from outside any vats.
     */
    private final long mySendingTicket;

    /**
     * Optionally capture the stack of the sending event.
     * <p/>
     * This is captured if causality is traced at the debug level or higher,
     * and so is not captured by default. If causality is indeed traced at the
     * debug level or higher, then this is captured whether or not the request
     * happens in a vat.
     */
    private final StackContext myStackContext;

    /**
     *
     */
    private SendingContext myOptNextContext;

    /**
     * Captures the current context as the sending context.
     */
    public SendingContext(String tag) {
        this(tag, null);
    }

    /**
     * Captures the current context as the sending context.
     *
     * @param tag One of "SCsend", "SCresolve", "SCmsg", "SCcloser", "SCnow",
     *            or "SCrun"
     */
    public SendingContext(String tag, SendingContext optNext) {
        myOptSendingVat = Vat.getOptCurrentVat();
        if (null == myOptSendingVat) {
            mySendingTicket = -1;
        } else {
            mySendingTicket = myOptSendingVat.getRunner().servingTicket();
        }
        myStackContext = new StackContext(tag, false, Trace.causality.debug);
        myOptNextContext = optNext;
    }

    /**
     *
     */
    public void appendContext(SendingContext optNext) {
        if (myOptNextContext == null) {
            myOptNextContext = optNext;
        } else {
            myOptNextContext.appendContext(optNext);
        }
    }

//    /**
//     * Trace at warning level the context captured by this object on creation.
//     */
//    public void report(String msg) {
//        if (Trace.causality.warning && Trace.ON) {
//            msg += " <" + myOptSendingVat + "," + mySendingTicket + "(" + myTag + ")>";
//            if (null == myOptSendingJStack) {
//                Trace.causality.warningm(msg);
//            } else {
//                Trace.causality.warningm(msg, myOptSendingJStack);
//            }
//        }
//    }

    /**
     *
     */
    public void printContextOn(TextWriter out) throws IOException {
        if (null != myOptNextContext) {
            myOptNextContext.printContextOn(out);
        }
        out.println();
        if (null == myOptSendingVat) {
            out.print("sent by external thread");
        } else {
            out.print("sent by: ", myOptSendingVat, ":" + mySendingTicket);
        }
        out.print(myStackContext);
    }

    /**
     * @param out
     * @throws IOException
     */
    public void __printOn(TextWriter out) throws IOException {
        out.println();
        printContextOn(out.indent("--- "));
        out.println();
    }

    /**
     *
     */
    public String toString() {
        return E.toString(this);
    }
}
