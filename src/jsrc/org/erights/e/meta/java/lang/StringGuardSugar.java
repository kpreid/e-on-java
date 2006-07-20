package org.erights.e.meta.java.lang;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.develop.assertion.T;
import org.erights.e.elib.base.ClassDesc;
import org.erights.e.elib.tables.Twine;
import org.erights.e.elib.util.OneArgFunc;

/**
 * Twine coerces to String by becoming bare.
 *
 * @author Mark S. Miller
 */
public class StringGuardSugar extends ClassDesc {

    /**
     * @param clazz must be String.class
     */
    public StringGuardSugar(Class clazz) {
        super(clazz);
        T.require(String.class == clazz,
                  clazz, " must be String.class");
    }

    /**
     *
     */
    protected Object subCoerceR(Object shortSpecimen, OneArgFunc optEjector) {
        if (shortSpecimen instanceof Twine) {
            return ((Twine)shortSpecimen).bare();
        }
        return super.subCoerceR(shortSpecimen, optEjector);
    }
}
