package org.quasiliteral.quasiterm;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.StaticMaker;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.FlexList;
import org.erights.e.elib.base.SourceSpan;
import org.quasiliteral.astro.AstroArg;
import org.quasiliteral.astro.AstroBuilder;
import org.quasiliteral.term.QuasiBuilder;

/**
 * A quasi-literal empty Term sequence, that matches or generates an actual
 * empty sequence of {@link org.quasiliteral.astro.Astro}s.
 *
 * @author Mark S. Miller
 */
public final class QEmptySeq extends QAstroArg {

    static private final long serialVersionUID = 5776461753172189641L;

    /**
     *
     */
    static public final StaticMaker QEmptySeqMaker =
      StaticMaker.make(QEmptySeq.class);

    /**
     * Makes a QTerm that matches or generates a Astro.
     * <p/>
     * The invariants of a QTerm are not checked here, but rather are enforced
     * by the callers in this class and in QTermBuilder.
     *
     * @param builder Used to build the results of a substitute
     */
    public QEmptySeq(AstroBuilder builder, SourceSpan optSpan) {
        super(builder, optSpan);
    }

    /**
     * Uses 'QEmptySeqMaker(myBuilder)'
     */
    public Object[] getSpreadUncall() {
        Object[] result = {QEmptySeqMaker, "run", myBuilder, myOptSpan};
        return result;
    }

    public AstroArg withOptSpan(SourceSpan optSpan) {
        return new QEmptySeq(myBuilder, optSpan);
    }

    /**
     * Returns qbuilder.empty()
     */
    public AstroArg qbuild(QuasiBuilder qbuilder) {
        return qbuilder.empty();
    }

    /**
     * Returns the empty list
     */
    public ConstList substSlice(ConstList args, int[] index) {
        return ConstList.EmptyList;
    }

    /**
     * Matches 0 items
     */
    public int matchBindSlice(ConstList args,
                              ConstList specimenList,
                              FlexList bindings,
                              int[] index) {
        return 0;
    }

    /**
     * Returns 1
     */
    public int getHeight() {
        return 1;
    }

    /**
     * Show nothing
     */
    public void prettyPrintOn(TextWriter out) {
    }

    /**
     * Returns shapeSoFar
     */
    int startShape(ConstList args,
                   FlexList optBindings,
                   int[] prefix,
                   int shapeSoFar) {
        return shapeSoFar;
    }

    /**
     * Does nothing
     */
    void endShape(FlexList optBindings, int[] prefix, int shape) {
    }
}
