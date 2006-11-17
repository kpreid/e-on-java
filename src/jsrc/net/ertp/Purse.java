package net.ertp;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.elib.sealing.UnsealingException;

/**
 * A mutable vessel holding transferable erights.
 *
 * @author Mark S. Miller
 */
public interface Purse {

    /**
     * The Issuer of the erights that can be held in this Purse.
     */
    Issuer getIssuer();

    /**
     * An Assay describing the erights <em>currently</em> contained in this
     * Purse.
     */
    Assay getAssay();

    /**
     * If src is a Purse acceptable to the Issuer of this Purse, transfer all
     * of its erights into this Purse, and return an Assay describing how much
     * was transfered into this Purse (as opposed to how much was lost from
     * src).
     * <p/>
     * Otherwise, throw an UnsealingException
     */
    Assay depositAll(Purse src) throws UnsealingException;
}
