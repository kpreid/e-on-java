package org.erights.e.elib.vat;

// Copyright 2005 Mark S. Miller under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.elib.base.SourceSpan;
import org.erights.e.elib.debug.EStackItem;
import org.erights.e.elib.oldeio.EPrintable;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.tables.ConstList;

import java.io.IOException;

/**
 * Records the context it was created in, so that it can be included in a later
 * report.
 *
 * @author Mark S. Miller
 */
public final class StackContext implements EPrintable {

    private final String myTag;

    private final boolean myOnlyOnePosFlag;

    /**
     * Capture the Java stack of the sending event.
     */
    private final Throwable myOptSendingJStack;

    /**
     * A list of {@link EStackItem}s to be stringified
     */
    private final ConstList myOptSendingEStack;

    /**
     * Captures the current stack contexts.
     */
    public StackContext(String tag) {
        this(tag, false, true);
    }

    /**
     * Optionally captures the current stack contexts.
     */
    public StackContext(String tag, boolean onlyOnePosFlag, boolean capture) {
        myTag = tag;
        myOnlyOnePosFlag = onlyOnePosFlag;
        if (capture) {
            myOptSendingJStack = new Throwable("just tracing causality");
            myOptSendingEStack = Runner.getOptEStackForTracing();
        } else {
            myOptSendingJStack = null;
            myOptSendingEStack = null;
        }
    }

    /**
     *
     */
    public void __printOn(TextWriter out) throws IOException {
        out.print("(", myTag, ")");

        if (null != myOptSendingJStack) {
            // XXX when should we show the stack tracebacks?
        }
        if (null != myOptSendingEStack) {
            for (int i = myOptSendingEStack.size() - 1; 0 <= i; i--) {
                EStackItem item = (EStackItem)myOptSendingEStack.get(i);
                out.lnPrint(" @ ");
                item.traceOn(out);
                SourceSpan optSpan = item.getOptSpan();
                if (null != optSpan) {
                    out.print(": ", optSpan);
                    if (myOnlyOnePosFlag) {
                        return;
                    }
                }
            }
        }
    }

    /**
     *
     */
    public String toString() {
        return E.toString(this);
    }
}
