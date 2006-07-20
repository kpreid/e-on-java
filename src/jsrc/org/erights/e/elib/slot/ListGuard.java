package org.erights.e.elib.slot;

// Copyright 2003 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.develop.assertion.T;
import org.erights.e.elib.base.ClassDesc;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.util.OneArgFunc;

import java.io.IOException;

/**
 * Wraps an element guard, in order to coerce a list into a list in which each
 * element has been coerced by the element guard.
 * <p/>
 * In the E language, <tt>elemGuard[]</tt> or <tt>List[eelemGuard]</tt>
 * evaluates to a ListGuard wrapping elemGuard. Read as a type declaration, it
 * means "a ConstList of elements satisfying elemGuard".
 *
 * @author Mark S. Miller
 */
public class ListGuard implements Guard {

    static public final ListGuard THE_BASE = new ListGuard(null);

    private final Guard myOptElemGuard;

    /**
     *
     */
    private ListGuard(Guard optElemGuard) {
        myOptElemGuard = optElemGuard;
    }

    /**
     * @return
     */
    public ListGuard get(Guard elemGuard) {
        T.require(null == myOptElemGuard,
                  "Already parameterized: ", this);
        T.notNull(elemGuard,
                  "Missing element guard parameter");
        return new ListGuard(elemGuard);
    }

    /**
     *
     */
    public Object coerce(Object specimen, OneArgFunc optEjector) {
        ClassDesc ConstListGuard = ClassDesc.make(ConstList.class);
        ConstList list =
          (ConstList)ConstListGuard.coerce(specimen, optEjector);
        if (null == myOptElemGuard) {
            return list;
        }
        int len = list.size();
        Object[] result = new Object[len];
        for (int i = 0; i < len; i++) {
            result[i] = myOptElemGuard.coerce(list.get(i), optEjector);
        }
        return ConstList.fromArray(result);
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
     * Returns "List" or "List[<i>elem-guard</i>]"
     */
    public void __printOn(TextWriter out) throws IOException {
        out.print("List");
        if (null != myOptElemGuard) {
            out.print("[", myOptElemGuard, "]");
        }
    }
}
