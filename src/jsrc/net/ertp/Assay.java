package net.ertp;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.elib.sealing.UnsealingException;

/**
 * Represents some particular quantity and/or kind of erights as
 * issued by a particular Issuer.
 * <p/>
 * It <em>represents</em> the eright
 * but, unlike a Purse, does not <em>provide</em> the eright. For
 * example, if Alice has $5 and Bob wants $3, Alice has erights (in a
 * Purse), but Bob is expressing a desire for erights he doesn't yet
 * have. In order for a third party, like an escrow exchange agent,
 * to evaluate the acceptability to Bob of erights offered by Alice,
 * even if the escrow agent has no prior knowledge of the Issuer or
 * kinds of erights involved (like "dollars") Bob express his desire
 * using an Assay.
 * <p/>
 * So an Assay must do more than just express desired erights. It
 * must test whether a provided Purse contains adequate erights from
 * which to derive the desired erights. Since the desired erights may
 * be exclusive erights, this test must also trasfer them to a Purse
 * where they may be safely kept, such as a Purse held by the escrow
 * agent. Otherwise a successful test would give no assurance.
 * <p/>
 * This test is the 'transfer' operation of the "Electronic Rights
 * Transfer Protocol". See below for details.
 * <p/>
 * The erights represented by this Assay may be exclusive or not,
 * fungible or not, etc..., and the transfer operation may be blinded,
 * otherwise unlinkable, or not, all according to the stated policy of
 * the Assay and its Issuer, and your trust in its statement.
 *
 * @author Mark S. Miller
 */
public interface Assay {

    /**
     * The Issuer that "stands behind" this Assay.
     * <p/>
     * The returned Issuer must be one that will vouch for this Assay.
     * Typically, this will be the Issuer that issued this Assay.
     */
    Issuer getIssuer();

    /**
     * If src and dest are both of this Issuer and src contains adequate
     * erights to fund the transfer, transfer the erights described by
     * this Assay into dest.
     * <p/>
     * Given a 'src' Purse acceptable to this Issuer that's alleged
     * to have at least the erights described by this Assay, and any
     * 'dest' Purse acceptable to this Issuer, the Assay checks that
     * all of these conditions are met, and if so, <em>ideally</em>
     * transfer the erights it describes from 'src' to 'dest'.
     * Otherwise, it throws an exception and <em>ideally</em> leaves
     * the two Purses unaffected.
     * <p/>
     * What does "acceptable to" above mean?  At a minimum, the Purses
     * issued by an Issuer must be acceptable to it (indeed, must be
     * vouched-for by it). Base level Issuers of atomic erights will
     * typically do nothing more. However, to enable "Market
     * Translator" services (Nick, what's a good URL?), a virtual
     * Assay (an Assay of a virtual Issuer) may accept 'src' Purses of
     * other Issuers, extract erights and trade them on the market
     * into the kind of erights this Assay represents, and trade these
     * in turn for the kind of erights that can be deposited into
     * 'dest'. Obviously, one will often be able to do this with one
     * trade instead of two.
     * <p/>
     * Why "<em>ideally</em>" above?  A transfer attempt, whether
     * successful or not, can involve some amount of friction. As a
     * result, an unsuccessful transfer can still cause some loss to
     * the src Purse, and a successful transfer can cause the src
     * Purse to lose more than the amount transfered into the dest
     * Purse. An individual Issuer should state what kinds of
     * friction-losses its customers may be subject to. In all cases,
     * a successful trasfer should increase the erights of dest by
     * exactly the erights described by this Assay (subject to Market
     * Translator interpretation) and an unsuccessful transfer should
     * leave dest unaffected. All friction-losses are born only by
     * src.
     *
     * @throws UnsealingException           Thrown if src or dest isn't
     *                                      acceptable to the same Issuer as this Assay.
     * @throws InsufficientERightsException Thrown if src doesn't
     *                                      contain adequate erights to fund the transfer.
     */
    void transfer(Purse src, Purse dest)
      throws UnsealingException, InsufficientERightsException;

    /**
     * Return -1.0, 0.0, 1.0, or NaN as the erights represented by this Assay
     * is a strict subset, interchangeable with, a strict superset, or
     * incomparable with the erights of other.
     * <p/>
     * The E comparison operators, &lt;, &lt;= &lt;=&gt;, &gt;=, &gt;, are
     * syntactic shorthands that expand to calls to <tt>op__cmp</tt>
     * assuming the convention stated here. Therefore, you can use these
     * operators directly on near Assay objects.
     *
     * @throws UnsealingException if other isn't issued by the same Issuer.
     */
    double op__cmp(Assay other) throws UnsealingException;
}
