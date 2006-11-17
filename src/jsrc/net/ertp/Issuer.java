package net.ertp;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import net.captp.jcomm.SturdyRef;
import org.erights.e.elib.sealing.UnsealingException;

/**
 * The generic customer interface to an issuing authority.
 * <p/>
 * An issuing authority creates and "stands behind" a given category of
 * erights.
 * <p/>
 * Issuer subtypes for particular kinds of erights must have
 * 'makeAssay(description...)' messages for making an Assay of this Issuer
 * representing erights according to description arguments. For example, an
 * eright described by a scalar quantity, like money, would have an integer as
 * its description. Since there is no description type universal to all
 * erights, no makeAssay message is included in the generic type 'Issuer'.
 *
 * @author Mark S. Miller
 */
public interface Issuer {

    /**
     * If a vouch message returns successfully, then the value it returns can
     * be trusted exactly as much as this Issuer is trusted.
     * <p/>
     * The vouch messages are normally used to ask an Issuer whether an Assay
     * or a Purse is a representative of this Issuer.
     * <p/>
     * Why do the vouce messages return the vouched-for-object, rather than
     * returning a boolean indicating that the argument can be trusted?  In
     * order to allow object some freedom in how they encode/decode themselves
     * between Vats, we cannot assume that a trustworthy decoded 'candidate',
     * as seen by a vouch method, indicates that the object the sender inquired
     * about is actually trustworthy. A successful vouching cannot assure the
     * sender that an argument is trustworthy, but does safely assure the
     * sender that the result is trustworthy.
     * <p/>
     * An Issuer must vouch for all the Purses and Assays it issues.
     *
     * @param candidate Does this Issuer stand by this candidate?
     * @return If so, it returns the candidate.
     * @throws UnsealingException If not, it typically throws UnsealingException,
     *                            but any thrown problem indicates a failure to
     *                            vouch.
     */
    Assay vouchForAssay(Assay candidate) throws UnsealingException;

    /**
     * @return
     * @see #vouchForAssay
     */
    Purse vouchForPurse(Purse candidate) throws UnsealingException;

    /**
     * Just like {@link #vouchForAssay}, but returns a SturdyRef rather than a
     * live reference.
     * <p/>
     * The expiration date of the SturdyRef is up to the Issuer, but should be
     * adequate to enable recovery of access to erights across major outages.
     *
     * @param candidate Does this Issuer stand by this candidate?
     * @return If so, it returns a SturdyRef to the candidate.
     * @throws UnsealingException If not, it throws UnsealingException.
     */
    SturdyRef sturdyVouchForAssay(Assay candidate) throws UnsealingException;

    /**
     * @return
     * @see #sturdyVouchForAssay
     */
    SturdyRef sturdyVouchForPurse(Purse candidate) throws UnsealingException;

    /**
     * Makes an empty Purse for holding the kind of erights that this Issuer
     * issues.
     *
     * @return An initially empty Purse of this Issuer.
     */
    Purse makeEmptyPurse();
}
