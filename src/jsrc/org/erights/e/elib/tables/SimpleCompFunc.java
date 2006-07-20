package org.erights.e.elib.tables;

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

import org.erights.e.elib.prim.E;
import org.erights.e.elib.ref.Ref;

/**
 * Compares using the natural ordering
 */


class SimpleCompFunc implements CompFunc {

    /**
     * The canonical instance
     */
    static public final CompFunc THE_ONE = new SimpleCompFunc();

    private SimpleCompFunc() {
    }

    /**
     * E.calls with "op__cmp" and the zero tests to determine
     * whether to return 0.0, -1.0, 1.0, or NaN.
     */
    public double run(Object a, Object b) {
        Object comp = Ref.resolution(E.call(a, "op__cmp", b));
        if (comp instanceof Number) {
            return ((Number)comp).doubleValue();
        }
        if (E.asBoolean(E.call(comp, "isZero"))) {
            return 0.0;
        }
        if (E.asBoolean(E.call(comp, "belowZero"))) {
            return -1.0;
        }
        if (E.asBoolean(E.call(comp, "aboveZero"))) {
            return 1.0;
        }
        return Double.NaN;
    }
}
