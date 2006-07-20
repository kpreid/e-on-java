package org.erights.e.meta.org.erights.e.elib.base;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.develop.assertion.T;
import org.erights.e.elib.base.ClassDesc;
import org.erights.e.elib.base.TypeDesc;
import org.erights.e.elib.util.OneArgFunc;

/**
 * @author Mark S. Miller
 */
public class TypeDescGuardSugar extends ClassDesc {

    /**
     * @param clazz must be TypeDesc or a subtype
     */
    public TypeDescGuardSugar(Class clazz) {
        super(clazz);
        T.require(TypeDesc.class.isAssignableFrom(clazz),
                  clazz, " must be a kind of TypeDesc");
    }

    /**
     * Coerces a Class to a ClassDesc (a kind of TypeDesc)
     */
    protected Object subCoerceR(Object shortSpecimen, OneArgFunc optEjector) {
        if (shortSpecimen instanceof Class) {
            return ClassDesc.make((Class)shortSpecimen);
        }
        return super.subCoerceR(shortSpecimen, optEjector);
    }
}
