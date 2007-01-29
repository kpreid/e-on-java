package org.quasiliteral.quasiterm;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.develop.assertion.T;
import org.erights.e.elib.base.SourceSpan;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.StaticMaker;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.FlexList;
import org.quasiliteral.astro.AstroArg;
import org.quasiliteral.astro.AstroBuilder;
import org.quasiliteral.term.QuasiBuilder;

import java.io.IOException;

/**
 * A quasi-literal Term sequence, that matches or generates an actual sequence
 * of {@link org.quasiliteral.astro.Astro}s.
 *
 * @author Mark S. Miller
 */
public final class QPairSeq extends QAstroArg {

    static private final long serialVersionUID = 1333026251208303110L;

    /**
     *
     */
    static public final StaticMaker QPairSeqMaker =
      StaticMaker.make(QPairSeq.class);

    /**
     *
     */
    private final QAstroArg myLeft;

    /**
     *
     */
    private final QAstroArg myRight;

    /**
     * Just used to decide how to pretty print.
     * <p/>
     * Initialized lazily. 0 if uninitialized, so does not need to be
     * recalculated on revival.
     */
    private transient int myHeight = 0;

    /**
     * Makes a QTerm that matches or generates a Astro.
     * <p/>
     * The invariants of a QTerm are not checked here, but rather are enforced
     * by the callers in this class and in QTermBuilder.
     *
     * @param builder Used to build the results of a substitute
     * @param left    Do these first
     * @param right   Then do these
     */
    private QPairSeq(AstroBuilder builder,
                     QAstroArg left,
                     QAstroArg right,
                     SourceSpan optSpan) {
        super(builder, optSpan);
        myLeft = left;
        myRight = right;
    }

    /**
     * @return
     */
    static QAstroArg run(AstroBuilder builder,
                         QAstroArg left,
                         QAstroArg right) {
        if (left instanceof QEmptySeq) {
            return right;
        } else if (right instanceof QEmptySeq) {
            return left;
        } else {
            return new QPairSeq(builder, left, right, null);
        }
    }

    /**
     * Uses 'QPairSeqMaker(myBuilder, myLeft, myRight)'
     */
    public Object[] getSpreadUncall() {
        Object[] result =
          {QPairSeqMaker, "run", myBuilder, myLeft, myRight, myOptSpan};
        return result;
    }

    public AstroArg withOptSpan(SourceSpan optSpan) {
        return new QPairSeq(myBuilder, myLeft, myRight, optSpan);
    }

    /**
     *
     */
    public AstroArg qbuild(QuasiBuilder qbuilder) {
        return qbuilder.seq(myLeft, myRight);
    }

    /**
     * @return The concatentation of the substSlices of myQArgs.
     */
    public ConstList substSlice(ConstList args, int[] index) {
        ConstList left = myLeft.substSlice(args, index);
        ConstList right = myRight.substSlice(args, index);
        return left.add(right);
    }

    /**
     * Matches the arg list by a naive greedy algorithm.
     */
    public int matchBindSlice(ConstList args,
                              ConstList specimenList,
                              FlexList bindings,
                              int[] index) {
        int leftNum =
          myLeft.matchBindSlice(args, specimenList, bindings, index);
        if (-1 >= leftNum) {
            return -1;
        }
        specimenList = specimenList.run(leftNum, specimenList.size());
        int rightNum =
          myRight.matchBindSlice(args, specimenList, bindings, index);
        if (-1 >= rightNum) {
            return -1;
        }
        return leftNum + rightNum;
    }

    /**
     * The distance to the bottom is simply the max of the distance of each of
     * my two parts.
     */
    public int getHeight() {
        if (0 >= myHeight) {
            myHeight = StrictMath.max(myLeft.getHeight(), myRight.getHeight());
        }
        return myHeight;
    }

    /**
     *
     */
    public void prettyPrintOn(TextWriter out) throws IOException {
        int h = getHeight();
        if (0 >= h) {
            T.fail("internal: bad height " + h);
        }
        myLeft.prettyPrintOn(out);
        if (1 == h) {
            //If it only contains leaves, do it on one line
            out.print(", ");
        } else {
            //print each child lined up.
            out.println(",");
        }
        myRight.prettyPrintOn(out);
    }

    /**
     * A QPairSeq has whatever shape its args agree on
     */
    int startShape(ConstList args,
                   FlexList optBindings,
                   int[] prefix,
                   int shapeSoFar) {
        shapeSoFar = myLeft.startShape(args, optBindings, prefix, shapeSoFar);
        return myRight.startShape(args, optBindings, prefix, shapeSoFar);
    }

    /**
     * Just delegate to all children
     */
    void endShape(FlexList optBindings, int[] prefix, int shape) {
        myLeft.endShape(optBindings, prefix, shape);
        myRight.endShape(optBindings, prefix, shape);
    }
}
