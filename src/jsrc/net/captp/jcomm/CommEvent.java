package net.captp.jcomm;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.develop.trace.Trace;
import org.erights.e.elib.oldeio.EPrintable;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.vat.SendingContext;

import java.io.IOException;

/**
 * Represents an event caused on one side of a {@link CapTPConnection}, to
 * happen on the other side of that connection.
 * <p/>
 * Modeled on {@link org.erights.e.elib.vat.PendingEvent}.
 *
 * @author Mark S. Miller
 */
public final class CommEvent implements EPrintable {

    /**
     * The VatID of the sending vat
     */
    private final String myOutgoingID;

    /**
     * The VatID of the receiving vat
     */
    private final String myIncomingID;

    /**
     * which message number is this of messages sent in this direction on this
     * connection?
     */
    private final int myMsgCount;

    /**
     *
     */
    private final SendingContext mySendingContext;

    /**
     *
     */
    CommEvent(String outgoingID, String incomingID, int msgCount, String tag) {
        myOutgoingID = outgoingID;
        myIncomingID = incomingID;
        myMsgCount = msgCount;
        mySendingContext = new SendingContext(tag);
    }

    /**
     * This should always be called once construction is complete.
     */
    protected void trace() {
        if (Trace.causality.debug && Trace.ON) {
            Trace.causality.debugm("", this);
        }
    }

    /**
     *
     */
    public void printContextOn(TextWriter out) throws IOException {
        out.println();
        out.print("comm: ",
                  myOutgoingID,
                  " -> ",
                  myIncomingID,
                  " #" + myMsgCount);
        mySendingContext.printContextOn(out);
    }

    /**
     * @param out
     * @throws java.io.IOException
     */
    public void __printOn(TextWriter out) throws IOException {
        out.println();
        printContextOn(out.indent("--- "));
        out.println();
    }

    /**
     * @return
     */
    public String toString() {
        return E.toString(this);
    }
}
