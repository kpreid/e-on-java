package org.erights.e.meta.java.lang;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.develop.assertion.T;
import org.erights.e.elib.prim.Thrower;
import org.erights.e.elib.util.OneArgFunc;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.meta.java.math.BaseEIntGuardSugar;
import org.erights.e.meta.java.math.EInt;

import java.io.IOException;

/**
 * @author Mark S. Miller
 */
public class IntegerGuardSugar extends BaseEIntGuardSugar {

    /**
     * @param clazz must be Integer.class
     */
    public IntegerGuardSugar(Class clazz) {
        super(clazz);
        T.require(Integer.class == clazz,
                  clazz, " must be an 'int32' type");
    }

    /**
     *
     */
    protected Object subCoerceR(Object shortSpecimen, OneArgFunc optEjector) {
        if (shortSpecimen instanceof Number) {
            Number eInt = ((Number)shortSpecimen);
            if (EInt.isInInt32(eInt, optEjector)) {
                return EInt.valueOf(eInt.intValue());
            } else {
                throw Thrower.toEject(optEjector,
                                      "" + eInt + " must be in " +
                                      Integer.MIN_VALUE + ".." +
                                      Integer.MAX_VALUE);
            }
        }
        throw doesntCoerceR(shortSpecimen, optEjector);
    }

    /**
     *
     */
    public void __printOn(TextWriter out) throws IOException {
        out.print("int32");
    }
}
