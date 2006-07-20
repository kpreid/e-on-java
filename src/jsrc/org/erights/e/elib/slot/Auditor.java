package org.erights.e.elib.slot;

import org.erights.e.elib.oldeio.EPrintable;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............


/**
 * <p/>
 * XXX Auditor will also require DeepFrozen
 *
 * @author Mark S. Miller
 */
public interface Auditor extends EPrintable /*, Marker */ {

    /**
     * Asks an auditor: Do you give your stamp of approval to the code of an
     * object definition expression (the <i><tt>objExpr</tt></i>), and thereby
     * endorse those instances requesting your endorsement?
     * <p/>
     * Approval is reported by returning true. Disapproval is typically
     * reported by throwing an exception (or otherwise non-locally exiting). If
     * an Auditor returns false, then the ObjectExpr's evaluation proceeds, but
     * without this Auditor on its approvers list. Normally, this only happens
     * if this Auditor is also asking the audition to ask other implied
     * Auditors to audit in its stead.
     * <p/>
     * <tt>objExpr</tt> should be an {@link org.erights.e.elang.evm.ObjectExpr}
     * as a Kernel-E parse tree with syntactic environment for an object
     * definition expression.
     * <p/>
     * A rubber stamping auditor (or a "rubber-stamp") is an auditor that
     * always approves of any <tt>objExpr</tt> it is asked to audit. A
     * rubber-stamp need not (and generally does not) examine <tt>objExpr</tt>,
     * which is why there can be Auditors at the elib level even though object
     * expressions don't exist till the elang level. Likewise, auditors that
     * always reject may be defined at the elib level.
     *
     * @return
     * @noinspection UnnecessaryFullyQualifiedName
     */
    boolean audit(Audition audition) throws AuditFailedException;
}
