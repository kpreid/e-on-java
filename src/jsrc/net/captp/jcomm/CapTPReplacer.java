package net.captp.jcomm;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.develop.assertion.T;
import org.erights.e.elib.base.ClassDesc;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.prim.StaticMaker;
import org.erights.e.elib.ref.Ref;
import org.erights.e.elib.serial.PassByConstructionAuditor;
import org.erights.e.elib.serial.RemoteCall;
import org.erights.e.elib.serial.Replacer;
import org.erights.e.elib.slot.AuditChecker;
import org.erights.e.elib.tables.ConstList;

/**
 * Used to specialize the SerializationStream for encoding a reference over a
 * CapTP connection.
 * <p/>
 * When our run method returns an {@link ObjectRefDesc}, the corresponding
 * decoded object is not simply this ObjectRefDesc. It is this ObjectRefDesc as
 * dereferenced by {@link CapTPReviver}.
 *
 * @author Mark S. Miller
 */
class CapTPReplacer extends Replacer {

    /**
     * The connection over which we are communicating
     */
    private final CapTPConnection myConn;

    static private final StaticMaker RemoteCallMaker =
      StaticMaker.make(RemoteCall.class);
    static private final ClassDesc ConstListGuard =
      ClassDesc.make(ConstList.class);

    /**
     *
     */
    CapTPReplacer(CapTPConnection conn) {
        myConn = conn;
    }

    /**
     * Replace any proxiable object (an implementer of PassByProxy, a Far
     * reference, a Promise, or a Broken reference) with an appropriate
     * over-the-wire representation, or if it's not a proxiable object, make
     * sure we're actually permitted to pass it by construction.
     */
    public Object substitute(Object ref) {
        ref = Ref.resolution(ref);

        if (Ref.isJOSSPBCRef(ref)) {
            return ref;
        }
        if (AuditChecker.THE_ONE.run(PassByConstructionAuditor.THE_ONE, ref)) {
            // XXX failures in the __optUncall, or a bogus uncall, should
            // result in sending a broken reference
            ConstList uncall = (ConstList)ConstListGuard.coerce(E.call(ref,
                                                                       "__optUncall"),
                                                                null);
            T.require(3 == uncall.size(),
                      "PassByConstruction object returned uncall not " +
                        "of length 3");
            return E.callAll(RemoteCallMaker,
                             "run",
                             (Object[])uncall.getArray());
        }
        //end PBC testing

        if (Ref.isEventual(ref)) {
            return myConn.makeEventualDesc(Ref.toRef(ref));
        }
        if (Ref.isNear(ref)) {
            if (Ref.isPassByProxy(ref)) {
                return myConn.makeImportingDesc(ref);
            }
            T.fail("the " + ref.getClass() +
              " is neither PassByConstruction nor PassByProxy");
        }
        T.fail("bad state " + ref);
        return null; //make compiler happy
    }
}
