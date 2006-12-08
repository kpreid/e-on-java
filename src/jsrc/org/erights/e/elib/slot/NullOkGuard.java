package org.erights.e.elib.slot;

/*
The contents of this file are subject to the Electric Communities E Open
Source Code License Version 1.0 (the "License"); you may not use this file
except in compliance with the License. You may obtain a copy of the License
at http://www.communities.com/EL/.

Software distributed under the License is distributed on an "AS IS" basis,
WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
the specific language governing rights and limitations under the License.

The Original Code is the Distributed E Language Implementation, released
July 20, 1998.

The Initial Developer of the Original Code is Electric Communities.
Copyright (C) 1998 Electric Communities. All Rights Reserved.

Contributor(s): ______________________________________.
*/

import org.erights.e.develop.assertion.T;
import org.erights.e.elib.base.ClassDesc;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.Thrower;
import org.erights.e.elib.ref.Ref;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.util.OneArgFunc;

import java.io.IOException;

/**
 * The canonical instance (THE_ONE) is bound to "nullOk" in the initial
 * environment, and accepts null as a valid value.
 * <p/>
 * As a Guard, "nullOk" accepts only null as a valid value.
 * <p/>
 * As a Guard template, "nullOk[subGuard]" produces a Guard. (This object is
 * also an instance of NullOkGuard, but no one should care.)  As a Guard, this
 * object accepts null and whatever the subGuard accepts. Non-null values are
 * coerced according to the provided subGuard.
 *
 * @author Mark S. Miller
 */
public class NullOkGuard implements Guard {

    static public final NullOkGuard THE_BASE = new NullOkGuard(null);

    private final Guard myOptSubGuard;

    /**
     *
     */
    private NullOkGuard(Guard optSubGuard) {
        myOptSubGuard = optSubGuard;
    }

    /**
     * If specimen is null, returns null. Otherwise passes it to my wrapped
     * Guard, if there is one.
     */
    public Object coerce(Object specimen, OneArgFunc optEjector) {
        //shorten first
        specimen = Ref.resolution(specimen);
        if (null == specimen) {
            return null;
        } else if (null == myOptSubGuard) {
            throw Thrower.toEject(optEjector, "must be null");
        } else {
            return myOptSubGuard.coerce(specimen, optEjector);
        }
    }

    /**
     * Makes a Guard that accepts either null or values that the argument
     * 'subGuard' accepts.
     * <p/>
     * Non-null values are coerced by the argument.
     */
    public NullOkGuard get(Guard subGuard) {
        T.require(null == myOptSubGuard, "can only combine with one Guard");
        T.notNull(subGuard, "Missing sub-guard parameter");
        return new NullOkGuard(subGuard);
    }

    /**
     * Matches a nullOk[x] guard made by this.
     */
    public Object match__get_1(Object specimen, OneArgFunc optEjector) {
        T.require(null == myOptSubGuard, "Already parameterized: ", this);
        ClassDesc kind = ClassDesc.make(NullOkGuard.class);
        NullOkGuard ofKind = (NullOkGuard)kind.coerce(specimen, optEjector);
        if (ofKind.myOptSubGuard == null) {
            throw Thrower.toEject(optEjector, "Not a parameterized nullOk");
        }
        Object[] result = {ofKind.myOptSubGuard};
        return ConstList.fromArray(result);
    }

    /**
     * @deprecated Use get/1 instead
     */
    public NullOkGuard run(Guard subGuard) {
        return get(subGuard);
    }

    /**
     * "nullOk" or "nullOk[<i>sub-guard</i>]"
     */
    public void __printOn(TextWriter out) throws IOException {
        out.print("nullOk");
        if (null != myOptSubGuard) {
            out.print("[", myOptSubGuard, "]");
        }
    }
}
