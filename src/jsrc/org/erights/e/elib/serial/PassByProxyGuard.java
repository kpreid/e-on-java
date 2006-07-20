package org.erights.e.elib.serial;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.elib.base.ClassDesc;
import org.erights.e.elib.prim.Thrower;
import org.erights.e.elib.ref.Ref;
import org.erights.e.elib.util.OneArgFunc;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.slot.Guard;

import java.io.IOException;

/**
 * The guard known as "PassByProxy".
 * <p/>
 * There's no reason to make this be an auditor as well, since E-language
 * objects are PassByProxy by default.
 *
 * @author Mark S. Miller
 */
public final class PassByProxyGuard implements Guard {

    static public final PassByProxyGuard THE_ONE =
      new PassByProxyGuard();

    /**
     *
     */
    private PassByProxyGuard() {
    }

    /**
     * Coerces specimen to be
     * pass-by-proxy, which includes types that are not Java-subtypes of
     * PassByProxy. If the specimen can't be coerced, exit according
     * to optEjector.
     */
    public Object coerce(Object specimen, OneArgFunc optEjector) {
        //shorten first
        specimen = Ref.resolution(specimen);
        if (Ref.isPassByProxy(specimen)) {
            return specimen;
        } else {
            throw Thrower.toEject(optEjector,
                                  new ClassCastException(ClassDesc.sig(
                                    specimen.getClass()) +
                                                         " isn't PassByProxy"));
        }
    }

    /**
     *
     */
    public void __printOn(TextWriter out) throws IOException {
        out.print("PassByProxy");
    }
}
