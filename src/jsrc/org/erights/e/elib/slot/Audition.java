// Copyright 2003 Hewlett Packard, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.elib.slot;

/**
 * While an Auditor is being asked to audit an objExpr, it is also given a
 * audition which it can ask to ask other auditors to audit the same objExpr.
 * <p/>
 * This allows an Auditor to cause dependent auditing.
 *
 * @author Mark S. Miller
 */
public interface Audition {

    /**
     * Asks this audition to ask that auditor, so that it can witness that
     * auditor's answer.
     */
    void ask(Auditor auditor);

    /**
     * This is the source for the ObjectExpr as a whole.
     * <p/>
     * The return value is typed "Object" rather than "ObjectExpr" to avoid an
     * upward layer dependency from elib to elang.
     * <p/>
     * Note that the relevant scope for {@link #getOptGuard(String)} is the
     * script rather that the ObjectExpr as a whole.
     */
    Object getSource();

    /**
     * Given that fieldName is a name used freely by the script, this returns
     * the guard which guarded the initialization of &amp;fieldName.
     * <p/>
     * If the defining occurrence is an unguarded SlotPattern, this returns
     * null.
     */
    Guard getOptGuard(String fieldName);
}
