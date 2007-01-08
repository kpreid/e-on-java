package org.quasiliteral.base;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

/**
 * Thrown by a non-filled in quasi data structure (a data structure acting in
 * the role of ValueMaker or MatchMaker) when an operation is performed that
 * requires a complete (non-quasi) data structure.
 *
 * @author Mark S. Miller
 */
public class IncompleteQuasiException extends RuntimeException {

    static private final long serialVersionUID = -6442348769453186704L;

    public IncompleteQuasiException() {
    }

    public IncompleteQuasiException(String msg) {
        super(msg);
    }
}
