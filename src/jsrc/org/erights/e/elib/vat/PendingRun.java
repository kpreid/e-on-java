package org.erights.e.elib.vat;

import org.erights.e.elib.oldeio.TextWriter;

import java.io.IOException;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

/**
 * Just wraps a Runnable to enqueue it for execution with debugging info
 * turned on.
 *
 * @author Mark S. Miller
 */
class PendingRun extends PendingEvent {

    /**
     *
     */
    private final Runnable myTodo;

    /**
     *
     */
    PendingRun(Vat vat, Runnable todo) {
        super("SCrun", vat);
        myTodo = todo;
        trace();
    }

    /**
     * Just calls the Runnable's run() method without catching exceptions or
     * anything.
     * <p/>
     * Our own {@link #run()} wraps innerRun() with the needed try/catch and
     * such.
     */
    protected void innerRun() {
        myTodo.run();
    }

    /**
     *
     */
    public void __printOn(TextWriter out) throws IOException {
        out.println();
        out.print("run: ", myTodo);
        printContextOn(out.indent("--- "));
        out.println();
    }
}
