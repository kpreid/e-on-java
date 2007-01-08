package org.erights.e.meta.org.erights.e.elib.ref;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.develop.assertion.T;
import org.erights.e.elib.base.ClassDesc;
import org.erights.e.elib.ref.Ref;
import org.erights.e.elib.util.OneArgFunc;

/**
 * Refines the 'coerce/2' behavior of the Ref type, so that it may be useful as
 * a Guard.
 * <p/>
 * The E programmer is never expected to see 'Ref' as a guard, since this
 * wouldn't be meaningful in E. Rather, this guard behavior is supported so
 * that other values are coerces to NearRefs on deflection, when 'Ref' is used
 * as a parameter of return type in a Java interface.
 *
 * @author Mark S. Miller
 */
public class RefGuardSugar extends ClassDesc {

    static private final long serialVersionUID = -1427818468274062749L;

    /**
     * @param clazz must be Ref or a subtype
     */
    public RefGuardSugar(Class clazz) {
        super(clazz);
        T.require(Ref.class.isAssignableFrom(clazz),
                  clazz,
                  " must be a kind of Ref");
    }

    /**
     * If specimen isn't already a Ref, wrap it in a NearRef.
     */
    protected Object subCoerceR(Object shortSpecimen, OneArgFunc optEjector) {
        return Ref.toRef(shortSpecimen);
    }
}
