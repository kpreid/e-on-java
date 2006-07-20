package org.erights.e.elang.interp;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............


/**
 * An StaticMaker on this class is the function named "__loop" in the
 * universalScope (and therefore also in the safeScope). <p>
 *
 * This wrapper is used as the E language's primitive looping construct.
 * When called as a one-argument function, it just repeatedly calls its
 * argument as a zero-argument, bool-returning function until that function
 * returns 'false'.
 *
 * @author Mark S. Miller.
 */
public class Loop {

    /**
     *
     */
    static public final Loop THE_ONE = new Loop();

    /**
     *
     */
    private Loop() {
    }

    /**
     * Keep calling loopBody until it returns false.
     */
    public void run(LoopBody loopBody) {
        while (loopBody.run()) {
        }
    }

    /**
     *
     */
    public String toString() {
        return "<__loop>";
    }
}
