package org.erights.e.elang.interp;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

/**
 * A no-argument, boolean returning function.
 * <p/>
 * Used for the body of loops
 *
 * @author Mark S. Miller
 * @see Loop
 */
public interface LoopBody {

    /**
     * This method will be repeatedly called until it returns false.
     */
    boolean run();
}
