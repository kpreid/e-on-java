package org.erights.e.meta.java.lang;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.develop.assertion.T;
import org.erights.e.elib.util.OneArgFunc;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.meta.java.math.BaseEIntGuardSugar;
import org.erights.e.meta.java.math.EInt;

import java.io.IOException;

/**
 * @author Mark S. Miller
 */
public class LongGuardSugar extends BaseEIntGuardSugar {

    /**
     * @param clazz must be Long.class
     */
    public LongGuardSugar(Class clazz) {
        super(clazz);
        T.require(Long.class == clazz,
                  clazz, " must be a 'long' type");
    }

    /**
     *
     */
    protected Object subCoerceR(Object shortSpecimen, OneArgFunc optEjector) {
        if (shortSpecimen instanceof Number) {
            Number eInt = ((Number)shortSpecimen);
            return new Long(EInt.inRange(eInt,
                                         Long.MIN_VALUE,
                                         Long.MAX_VALUE,
                                         optEjector));
        }
        throw doesntCoerceR(shortSpecimen, optEjector);
    }

    /**
     *
     */
    public void __printOn(TextWriter out) throws IOException {
        out.print("int64");
    }
}
