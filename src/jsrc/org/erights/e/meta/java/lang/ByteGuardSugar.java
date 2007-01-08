package org.erights.e.meta.java.lang;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.develop.assertion.T;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.util.OneArgFunc;
import org.erights.e.meta.java.math.BaseEIntGuardSugar;
import org.erights.e.meta.java.math.EInt;

import java.io.IOException;

/**
 * @author Mark S. Miller
 */
public class ByteGuardSugar extends BaseEIntGuardSugar {

    static private final long serialVersionUID = -4426160072469864202L;

    /**
     * @param clazz must be Byte.class
     */
    public ByteGuardSugar(Class clazz) {
        super(clazz);
        T.require(Byte.class == clazz,
                  clazz,
                  " must represent the 'int8' type");
    }

    /**
     *
     */
    protected Object subCoerceR(Object shortSpecimen, OneArgFunc optEjector) {
        if (shortSpecimen instanceof Number) {
            Number eInt = ((Number)shortSpecimen);
            return new Byte((byte)EInt.inRange(eInt,
                                               Byte.MIN_VALUE,
                                               Byte.MAX_VALUE,
                                               optEjector));
        }
        throw doesntCoerceR(shortSpecimen, optEjector);
    }

    /**
     *
     */
    public void __printOn(TextWriter out) throws IOException {
        out.print("int8");
    }
}
