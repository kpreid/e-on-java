package org.erights.e.elang.interp;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.develop.assertion.T;
import org.erights.e.elib.base.TypeDesc;
import org.erights.e.elib.prim.StaticMaker;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.slot.Audition;
import org.erights.e.elib.slot.Auditor;
import org.erights.e.elib.tables.ConstList;

/**
 * A type description object, as would be created by an <tt>interface</tt>
 * expression.
 * <p>
 * A type so defined is used both as an audtitor, to check type conformance,
 * and as a Guard to check that the value provided is an instance of an audited
 * type.
 */
public class ProtocolDesc extends TypeDesc {

    static public final StaticMaker ProtocolDescMaker =
      StaticMaker.make(ProtocolDesc.class);

    /**
     * Determines whether this ProtocolDesc is Auditor, Guard, or both.
     * <p>
     * If the value of the myOptAuditor variable...<ul>
     * <li>...is this ProtocolDesc itself, then this ProtocolDesc is both an
     *     Auditor and a Guard, corresponding to single key authentication.
     * <li>...is null, then this ProtocolDesc is just an Auditor, corresponding
     *     to the signing key of a signing key pair.
     * <li>...is neither null nor this ProtocolDesc, then ProtocolDesc is just
     *     a Guard, corresponding to the signature verification key of a
     *     signing key pair. In this case, the value of this variable is the
     *     corresponding Auditor, whose value for this variable is null.
     * </ul>
     * This is actually a rather kludgy representation. A guard-only object
     * should not be of type Auditor, and vice versa.
     */
    private final ProtocolDesc myOptAuditor;

    /**
     * Makes a single type description object that can be used both as a
     * rubber-stamping auditor and as a guard.
     * <p>
     * This can be used for the object-equivalent of single-key
     * authentication.
     */
    public ProtocolDesc(String docComment,
                        String optFQName,
                        ConstList supers,
                        ConstList auditors,
                        ConstList mTypes) {
        super(docComment, optFQName, supers, auditors, mTypes);
        myOptAuditor = this;
    }

    /**
     *
     */
    private ProtocolDesc(String docComment,
                         String optFQName,
                         ConstList supers,
                         ConstList auditors,
                         ConstList mTypes,
                         ProtocolDesc optAuditor) {
        super(docComment, optFQName, supers, auditors, mTypes);
        myOptAuditor = optAuditor;
    }

    /**
     *
     */
    static public Object[] makePair(String docComment,
                                    String optFQName,
                                    ConstList supers,
                                    ConstList auditors,
                                    ConstList mTypes) {
        ProtocolDesc stamp = new ProtocolDesc(docComment,
                                              optFQName,
                                              supers,
                                              auditors,
                                              mTypes,
                                              null);
        ProtocolDesc guard = new ProtocolDesc(docComment,
                                              optFQName,
                                              supers,
                                              auditors,
                                              mTypes,
                                              stamp);
        Object[] result = { guard, stamp };
        return result;
    }

    /**
     *
     */
    protected Auditor getAuditor() {
        if (null == myOptAuditor) {
            T.fail("Not a guard: " + E.toString(this));
        }
        return myOptAuditor;
    }

    /**
     * Used in a type's role as rubber-stamping auditor.
     */
    public boolean audit(Audition audition) {
        if (null != myOptAuditor && this != myOptAuditor) {
            T.fail("Not an auditor: " + E.toString(this));
        }
        return true;
    }
}
