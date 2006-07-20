package org.erights.e.elib.slot;

// Copyright 2003 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.develop.assertion.T;
import org.erights.e.elib.base.ClassDesc;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.ConstSet;
import org.erights.e.elib.tables.AssocFunc;
import org.erights.e.elib.util.OneArgFunc;

import java.io.IOException;

/**
 * Wraps an element guard, in order to coerce a set into a set in which each
 * element has been coerced by the element guard.
 * <p/>
 * In the E language, <tt>Set[eelemGuard]</tt>
 * evaluates to a SetGuard wrapping elemGuard. Read as a type declaration, it
 * means "a ConstSet of elements satisfying elemGuard".
 *
 * @author Mark S. Miller
 */
public class SetGuard implements Guard {

    static public final SetGuard THE_BASE = new SetGuard(null);

    private final Guard myOptElemGuard;

    /**
     *
     */
    private SetGuard(Guard optElemGuard) {
        myOptElemGuard = optElemGuard;
    }

    /**
     * @return
     */
    public SetGuard get(Guard elemGuard) {
        T.require(null == myOptElemGuard,
                  "Already parameterized: ", this);
        T.notNull(elemGuard,
                  "Missing element guard parameter");
        return new SetGuard(elemGuard);
    }

    /**
     *
     */
    public Object coerce(Object specimen, final OneArgFunc optEjector) {
        ClassDesc ConstSetGuard = ClassDesc.make(ConstSet.class);
        ConstSet set =
          (ConstSet)ConstSetGuard.coerce(specimen, optEjector);
        if (null == myOptElemGuard) {
            return set;
        }
        int len = set.size();
        final Object[] result = new Object[len];

        // kludge around Java's 'outer-vars must be final' restriction
        final int[] i = {0};
        set.iterate(new AssocFunc() {
            public void run(Object key, Object value) {
                result[i[0]++] = myOptElemGuard.coerce(value, optEjector);;
            }
        });
        return ConstList.fromArray(result).asSet();
    }

    /**
     * @return
     */
    public Guard getElemGuard() {
        if (null == myOptElemGuard) {
            return AnyGuard.THE_ONE;
        } else {
            return myOptElemGuard;
        }
    }

    /**
     * Returns "Set" or "Set[<i>elem-guard</i>]"
     */
    public void __printOn(TextWriter out) throws IOException {
        out.print("Set");
        if (null != myOptElemGuard) {
            out.print("[", myOptElemGuard, "]");
        }
    }
}
