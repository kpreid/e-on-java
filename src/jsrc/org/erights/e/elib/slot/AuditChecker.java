package org.erights.e.elib.slot;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

/**
 * This implements the object available as "<tt>__auditedBy</tt>" in the
 * universal scope, which represents the lookup facet on the registry of
 * successful audits.
 * <pre>    __auditedBy(auditor, specimen)</pre>
 * returns a boolean indicating whether a given specimen was instantiated with
 * approval from a given auditor. Even if the answer is no, that
 * auditor-as-guard may still successfully coerce it to something, so
 * <tt>__auditedBy</tt> is really only for use within auditors-as-guards.
 * Others should just use the guard behaviors of these auditors.
 *
 * @author Ka-Ping Yee
 * @author with some changes by Mark S. Miller
 */
public class AuditChecker {

    /**
     *
     */
    static public final AuditChecker THE_ONE = new AuditChecker();

    /**
     *
     */
    private AuditChecker() {
    }

    /**
     *
     */
    public boolean run(Auditor auditor, Object specimen) {
        if (specimen instanceof Auditable) {
            return ((Auditable)specimen).isApprovedBy(auditor);
        }
        //Other cases, like ClassDesc.
        return false;
    }
}
