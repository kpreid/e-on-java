package net.captp.jcomm;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.develop.assertion.T;
import org.erights.e.elib.ref.Ref;
import org.erights.e.elib.serial.PassByConstruction;
import org.erights.e.elib.serial.Replacer;

/**
 * Used to specialize the SerializationStream for encoding a reference over a
 * CapTP connection.
 * <p/>
 * When our run method returns an {@link ObjectRefDesc}, the corresponding
 * decoded object is not simply this ObjectRefDesc. It is this ObjectRefDesc
 * as dereferenced by {@link CapTPReviver}.
 *
 * @author Mark S. Miller
 */
class CapTPReplacer extends Replacer {

    /**
     * The connection over which we are communicating
     */
    private final CapTPConnection myConn;

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

        //the following sequence of tests are cribbed from Ref.isPBC, except
        //that we don't guard it with an isNear test, so as to include broken
        //references.
        if (null == ref) {
            //null is trivially PassByCopy
            return null;
        }
        Class clazz = ref.getClass();
        if (PassByConstruction.class.isAssignableFrom(clazz)) {
            return ref;
        }
        if (clazz.isArray()) {
            //Because we try to pretend that arrays are PassByCopy lists,
            //we just pass arrays by copy. This is XXX a potential security
            //bug or at least a possible semantic confusion, since arrays are
            //not actually immutable. After passing, the original and passed
            //copy may diverge, whereas in an eventual send within a vat,
            //they wouldn't diverge.
            return ref;
        }
        if (PassByConstruction.HONORARY.has(clazz)) {
            return ref;
        }
        //end PCB testing

        if (Ref.isEventual(ref)) {
            return myConn.makeEventualDesc(Ref.toRef(ref));
        }
        if (Ref.isNear(ref)) {
            if (Ref.isPassByProxy(ref)) {
                return myConn.makeImportingDesc(ref);
            }
            T.fail("the " + clazz +
                   " is neither PassByConstruction nor PassByProxy");
        }
        T.fail("bad state " + ref);
        return null; //make compiler happy
    }
}
