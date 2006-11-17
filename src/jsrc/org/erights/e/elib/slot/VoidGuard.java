package org.erights.e.elib.slot;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.util.OneArgFunc;

import java.io.IOException;

/**
 * Bound to "void" in the initial environment, it coerces all values to null.
 * <p/>
 * When a method or function does not explicitly specify a return type, the
 * default return type is "void".
 *
 * @author Mark S. Miller
 */
public class VoidGuard implements Guard {

    static public final VoidGuard THE_ONE = new VoidGuard();

    /**
     * prevent external instantiation
     */
    private VoidGuard() {
    }

    /**
     * Coerce everything to null.
     */
    public Object coerce(Object specimen, OneArgFunc optEjector) {
        return null;
    }

    /**
     *
     */
    public void __printOn(TextWriter out) throws IOException {
        out.print("void");
    }
}
