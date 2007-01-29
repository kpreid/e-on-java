package org.erights.e.elib.slot;

// Copyright 2003 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.develop.assertion.T;
import org.erights.e.elib.base.Ejector;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.tables.AssocFunc;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.FlexList;
import org.erights.e.elib.util.OneArgFunc;

import java.io.IOException;

/**
 * Composes a sequence of guards to form a union type.
 * <p/>
 * Since this operation is associative, we represent directly only a sequence
 * of two. A binary tree then represents any sequence. Reports the first
 * success or the last failure.
 *
 * @author Mark S. Miller
 */
public class UnionGuard implements Guard {

    private final Guard[] mySubGuards;

    /**
     * @param subGuards
     */
    UnionGuard(ConstList subGuards) {
        T.require(2 <= subGuards.size(),
                  "UnionGuard unneeded any[",
                  subGuards,
                  "]");
        final FlexList guardList =
          FlexList.fromType(Guard.class, subGuards.size());
        subGuards.iterate(new AssocFunc() {
            public void run(Object key, Object value) {
                if (value instanceof UnionGuard) {
                    UnionGuard ug = (UnionGuard)value;
                    Guard[] subSubs = ug.mySubGuards;
                    for (int i = 0, len = subSubs.length; i < len; i++) {
                        // Already flattened, no recursion needed.
                        guardList.push(subSubs[i]);
                    }
                } else {
                    guardList.push(value);
                }
            }
        });
        mySubGuards = (Guard[])guardList.getArray(Guard.class);
    }

    /**
     *
     */
    public Object coerce(Object specimen, OneArgFunc optEjector) {
        int last = mySubGuards.length - 1;
        for (int i = 0; i < last; i++) {
            Ejector ej = new Ejector("guard acceptance test");
            try {
                return mySubGuards[i].coerce(specimen, ej);
            } catch (Throwable t) {
                // If it reports failure, ignore it and go on.
                // If it otherwise non-locally exitted, then allow that
                // through.
                ej.result(t);
            } finally {
                ej.disable();
            }
        }
        return mySubGuards[last].coerce(specimen, optEjector);
    }

    /**
     *
     */
    public Guard[] getChoices() {
        return mySubGuards;
    }

    /**
     * Prints "any[<i>leftGuard</i>, <i>rightGuard</i>]".
     */
    public void __printOn(TextWriter out) throws IOException {
        out.print("any[", mySubGuards[0]);
        for (int i = 1, len = mySubGuards.length; i < len; i++) {
            out.print(", ", mySubGuards[i]);
        }
        out.print("]");
    }
}
