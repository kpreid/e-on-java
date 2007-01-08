package org.erights.e.meta.java.lang;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.develop.assertion.T;
import org.erights.e.develop.format.StringHelper;
import org.erights.e.elib.base.Callable;
import org.erights.e.elib.base.ClassDesc;
import org.erights.e.elib.deflect.Deflector;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.prim.Thrower;
import org.erights.e.elib.serial.Marker;
import org.erights.e.elib.slot.AuditFailedException;
import org.erights.e.elib.slot.Audition;
import org.erights.e.elib.util.OneArgFunc;

/**
 * {@link Callable}s (eg, objects defined in the E language) coerce to
 * rubber-stamping (non-{@link Marker}) interfaces by deflection.
 *
 * @author Mark S. Miller
 * @see Deflector
 */
public class InterfaceGuardSugar extends ClassDesc {

    static private final long serialVersionUID = -4090579027555076905L;

    private final boolean myIsMarker;

    /**
     * @param clazz must represent an interface type
     */
    public InterfaceGuardSugar(Class clazz) {
        super(clazz);
        T.require(clazz.isInterface(), clazz, " must be an interface type");
        myIsMarker = Marker.class.isAssignableFrom(clazz);
    }

    /**
     * Overridden to allow non-marker interfaces to deflect null and non-near
     * references.
     */
    protected Object tryCoerceR(Object shortSpecimen, OneArgFunc optEjector) {
        if (myIsMarker) {
            return super.tryCoerceR(shortSpecimen, optEjector);
        }
        if (asClass().isInstance(shortSpecimen)) {
            //try for a cheap success
            return shortSpecimen;
        }
        Object newSpecimen = subCoerceR(shortSpecimen, optEjector);
        if (asClass().isInstance(newSpecimen)) {
            return newSpecimen;
        }
        throw Thrower.toEject(optEjector,
                              "but " + newSpecimen + " isn't " +
                                StringHelper.aan(E.toString(this)));
    }

    /**
     *
     */
    protected Object subCoerceR(Object shortSpecimen, OneArgFunc optEjector) {
        if (shortSpecimen instanceof Callable && !myIsMarker) {
            return Deflector.deflect(shortSpecimen, asClass());
        }
        return super.subCoerceR(shortSpecimen, optEjector);
    }

    /**
     * If I represent a non-{@link Marker} interface, then I'm a rubber
     * stamping auditor and will approve anything.
     * <p/>
     * XXX Otherwise, I currently reject everything, but this must be fixed.
     */
    public boolean audit(Audition audition) {
        //noinspection RedundantIfStatement
        if (myIsMarker) {
            throw new AuditFailedException(this, audition.getSource());
//            Thrower.THE_ONE.breakpoint(audition);
//            return false;
        } else {
            return true;
        }
    }
}
