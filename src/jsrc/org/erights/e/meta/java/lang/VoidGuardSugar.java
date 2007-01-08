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
public class VoidGuardSugar extends ClassDesc {

    static private final long serialVersionUID = 2256971417036275830L;

    /**
     * @param clazz must be Void.class
     */
    public VoidGuardSugar(Class clazz) {
        super(clazz);
        T.require(Void.class == clazz,
                  clazz,
                  " must represent the 'void' type");
    }

    /**
     * Coerces everything to null
     */
    protected Object tryCoerceR(Object shortSpecimen, OneArgFunc optEjector) {
        return null;
    }

    /**
     * Coerces everything to null
     */
    protected Object subCoerceR(Object shortSpecimen, OneArgFunc optEjector) {
        return null;
    }

    /**
     *
     */
    public void __printOn(TextWriter out) throws IOException {
        out.print("void");
    }
}
