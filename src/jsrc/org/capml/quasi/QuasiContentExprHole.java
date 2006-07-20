package org.capml.quasi;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.StaticMaker;
import org.erights.e.elib.prim.Thrower;
import org.erights.e.elib.ref.Ref;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.FlexList;
import org.erights.e.elib.util.OneArgFunc;
import org.erights.e.meta.java.math.EInt;

import java.io.IOException;

/**
 * Represents a $-hole in Content position in a quasi-literal XML tree. <p>
 * <p/>
 * This $-hole must be filled in with concrete Content, as defined in the
 * QuasiContent class comment.
 *
 * @author Mark S. Miller
 * @deprecated Use Term trees instead.
 */
public class QuasiContentExprHole extends QuasiContent {

    static final long serialVersionUID = -3526871811031899230L;

    static public final StaticMaker QuasiContentExprHoleMaker
      = StaticMaker.make(QuasiContentExprHole.class);

    private final int myIndex;

    /**
     *
     */
    public QuasiContentExprHole(int index) {
        myIndex = index;
    }

    /**
     * Uses 'QuasiContentExprHoleMaker(myIndex)'
     */
    public Object[] getSpreadUncall() {
        Object[] result = {QuasiContentExprHoleMaker,
                           "run",
                           EInt.valueOf(myIndex)};
        return result;
    }

    /**
     * args[myIndex] must be Content -- the equivalent ContentList is
     * returned.
     */
    public Object substitute(Object[] args) {
        return toContent(args[myIndex]);
    }

    /**
     * Is args[myIndex] equivalent to specimen?
     * <p/>
     * Since a $-hole doesn't contain any @-holes, 'bindings' is ignored.
     */
    public void matchBind(ConstList args,
                          Object specimen,
                          OneArgFunc optEjector,
                          FlexList bindings) {
        Object left = toContent(args.get(myIndex));
        Object right = toContent(specimen);
        if (!Ref.isSameEver(left, right)) {
            throw Thrower.toEject(optEjector,
                                  "${" + myIndex + "} (" + left +
                                  ") isn't the same as (" + right + ")");
        }
    }

    /**
     * Prints as "${<index>}"
     */
    public void prettyPrintOn(TextWriter out) throws IOException {
        out.print("${", EInt.valueOf(myIndex), "}");
    }
}
