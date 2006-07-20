// Copyright 2003 Hewlett Packard, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.meta.java.math;

import org.erights.e.develop.assertion.T;
import org.erights.e.develop.format.StringHelper;
import org.erights.e.elib.base.ClassDesc;
import org.erights.e.elib.prim.Thrower;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.ref.Ref;
import org.erights.e.elib.util.OneArgFunc;
import org.erights.e.elib.oldeio.TextWriter;

import java.io.IOException;

/**
 * @author Mark S. Miller
 */
public class EIntGuardSugar extends BaseEIntGuardSugar {

    /**
     * @param clazz must be EInt.class
     */
    public EIntGuardSugar(Class clazz) {
        super(clazz);
        T.require(EInt.class == clazz,
                  clazz, " must be type EInt");
    }

    /**
     * Overrides {@link ClassDesc#coerce} in order to check that the result is
     * an {@link EInt} rather than that it's an instance of a particular Java
     * type.
     */
    protected Object tryCoerceR(Object shortSpecimen, OneArgFunc optEjector) {
        if (null == shortSpecimen) {
            throw Thrower.toEject(optEjector,
                                  new NullPointerException(
                                    "must be " +
                                    StringHelper.aan(E.toString(this)) +
                                    " rather than null"));
        }
        if (!Ref.isNear(shortSpecimen)) {
            //Must check this before isInstance, since eventual references
            //are Java-instances of some Java-types, and we wish to reject
            //these anyway
            throw Thrower.toEject(optEjector,
                                  "Must be near: " + shortSpecimen);
        }
        return subCoerceR(shortSpecimen, optEjector);
    }

    /**
     * Coerces to normal form
     */
    protected Object subCoerceR(Object shortSpecimen, OneArgFunc optEjector) {
        if (shortSpecimen instanceof Number) {
            Number eInt = (Number)shortSpecimen;
            return EInt.normal(eInt, optEjector);
        }
        throw doesntCoerceR(shortSpecimen, optEjector);
    }

    /**
     *
     */
    public void __printOn(TextWriter out) throws IOException {
        out.print("int");
    }
}
