package org.erights.e.meta.java.lang;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.develop.assertion.T;
import org.erights.e.elib.base.ClassDesc;
import org.erights.e.elib.util.OneArgFunc;
import org.erights.e.elib.oldeio.TextWriter;

import java.io.IOException;

/**
 * @author Mark S. Miller
 */
public class DoubleGuardSugar extends ClassDesc {

    /**
     * @param clazz must be Double.class
     */
    public DoubleGuardSugar(Class clazz) {
        super(clazz);
        T.require(Double.class == clazz,
                  clazz, " must represent the 'double' type");
    }

    /**
     *
     */
    protected Object subCoerceR(Object shortSpecimen, OneArgFunc optEjector) {
        if (shortSpecimen instanceof Number) {
            return new Double(((Number)shortSpecimen).doubleValue());
        }
        return super.subCoerceR(shortSpecimen, optEjector);
    }

    /**
     *
     */
    public void __printOn(TextWriter out) throws IOException {
        out.print("float64");
    }
}
