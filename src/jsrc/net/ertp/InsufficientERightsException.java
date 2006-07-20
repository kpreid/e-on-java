package net.ertp;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

/**
 * Thrown when an operation fails because one of the parties provided less
 * erights than were needed.
 * <p/>
 * This corresponds to the banking notion of "Insufficient Funds", generalized
 * to arbitrary erights.
 *
 * @author Mark S. Miller
 */
public class InsufficientERightsException extends Exception {

    public InsufficientERightsException() {
    }

    public InsufficientERightsException(String msg) {
        super(msg);
    }
}

