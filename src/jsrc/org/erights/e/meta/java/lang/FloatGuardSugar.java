package org.erights.e.meta.java.lang;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.develop.assertion.T;
import org.erights.e.elib.base.ClassDesc;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.util.OneArgFunc;

import java.io.IOException;

/**
 * @author Mark S. Miller
 */
public class FloatGuardSugar extends ClassDesc {

    static private final long serialVersionUID = 8495850632691001411L;

    /**
     * @param clazz must be Float.class
     */
    public FloatGuardSugar(Class clazz) {
        super(clazz);
        T.require(Float.class == clazz, clazz, " must be a float32 type");
    }

    /**
     *
     */
    protected Object subCoerceR(Object shortSpecimen, OneArgFunc optEjector) {
        if (shortSpecimen instanceof Number) {
            return new Float(((Number)shortSpecimen).floatValue());
        }
        return super.subCoerceR(shortSpecimen, optEjector);
    }

    /**
     *
     */
    public void __printOn(TextWriter out) throws IOException {
        out.print("float32");
    }
}
