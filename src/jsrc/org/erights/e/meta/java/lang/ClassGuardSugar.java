package org.erights.e.meta.java.lang;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.develop.assertion.T;
import org.erights.e.elib.base.Callable;
import org.erights.e.elib.base.ClassDesc;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.util.OneArgFunc;

/**
 * @author Mark S. Miller
 */
public class ClassGuardSugar extends ClassDesc {

    /**
     * @param clazz must be Class.class
     */
    public ClassGuardSugar(Class clazz) {
        super(clazz);
        T.require(Class.class == clazz,
                  clazz, " must be Class.class");
    }

    /**
     *
     */
    protected Object subCoerceR(Object shortSpecimen, OneArgFunc optEjector) {
        if (shortSpecimen instanceof ClassDesc) {
            return ((ClassDesc)shortSpecimen).asClass();
        }
        if (shortSpecimen instanceof Callable) {
            Callable spec = (Callable)shortSpecimen;
            if (spec.respondsTo("asClass", 0)) {
                //XXX tremendous kludge, but it's an easy "solution" to a hard
                //problem.
                // We should eventually be able to replace asClass() with
                // __conformTo(ClassDesc.make(Class.class)), (i.e.,
                // __conformTo(this)) in which case we won't need to handle it
                // specially here.
                Object specAsClass = spec.callAll("asClass", E.NO_ARGS);
                return (Class)coerce(specAsClass, optEjector);
            }
        }
        return super.subCoerceR(shortSpecimen, optEjector);
    }
}
