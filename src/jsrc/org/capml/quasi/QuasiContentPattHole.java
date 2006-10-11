package org.capml.quasi;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.StaticMaker;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.FlexList;
import org.erights.e.elib.util.OneArgFunc;
import org.erights.e.meta.java.math.EInt;
import org.quasiliteral.base.IncompleteQuasiException;

import java.io.IOException;

/**
 * Represents a @-hole in Content position in a quasi-literal XML tree. <p>
 * <p/>
 * This @-hole will match against any specimen, and put it in the bindings at
 * the hole's index.
 *
 * @author Mark S. Miller
 * @deprecated Use Term trees instead.
 */
public class QuasiContentPattHole extends QuasiContent {

    static private final long serialVersionUID = -2774222631122956274L;

    static public final StaticMaker QuasiContentPattHoleMaker
      = StaticMaker.make(QuasiContentPattHole.class);

    private final int myIndex;

    /**
     *
     */
    public QuasiContentPattHole(int index) {
        myIndex = index;
    }

    /**
     * Uses 'QuasiContentPattHoleMaker(myIndex)'
     */
    public Object[] getSpreadUncall() {
        Object[] result = {QuasiContentPattHoleMaker,
                           "run",
                           EInt.valueOf(myIndex)};
        return result;
    }

    /**
     * If it's got @-holes, it's incomplete as a ValueMaker, so throw
     * IncompleteQuasiException
     */
    public Object substitute(Object[] args) {
        throw new IncompleteQuasiException
          ("can't have @-holes in a ValueMaker");
    }

    /**
     * puts specimen into bindings[myIndex] and succeed.
     * <p/>
     * Specimen must be Content or String. The equivalent Content is placed
     * into bindings.
     */
    public void matchBind(ConstList args,
                          Object specimen,
                          OneArgFunc optEjector,
                          FlexList bindings) {
        bind(bindings, myIndex, toContent(specimen));
    }

    /**
     * Prints as "@{<index>}"
     */
    public void prettyPrintOn(TextWriter out) throws IOException {
        out.print("@{", EInt.valueOf(myIndex), "}");
    }
}
