package org.quasiliteral.quasiterm;

import org.erights.e.develop.assertion.T;
import org.erights.e.elib.base.SourceSpan;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.StaticMaker;
import org.erights.e.elib.tables.ArrayHelper;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.FlexList;
import org.erights.e.meta.java.lang.CharacterMakerSugar;
import org.quasiliteral.astro.Astro;
import org.quasiliteral.astro.AstroArg;
import org.quasiliteral.astro.AstroBuilder;
import org.quasiliteral.term.QuasiBuilder;

import java.io.IOException;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

/**
 * @author Mark S. Miller
 */
public final class QSome extends QAstroArg {

    static private final long serialVersionUID = 3043579604546641336L;

    /**
     *
     */
    static public final StaticMaker QSomeMaker = StaticMaker.make(QSome.class);

    /**
     *
     */
    private final QAstroArg myOptSubPattern;

    /**
     *
     */
    private final char myQuant;

    /**
     * @param quant One of '?', '+', or '*'.
     */
    public QSome(AstroBuilder builder,
                 QAstroArg optSubPattern,
                 char quant,
                 SourceSpan optSpan) {
        super(builder, optSpan);
        myOptSubPattern = optSubPattern;
        myQuant = quant;
    }

    /**
     * Uses 'QSomeMaker(myBuilder, myOptSubPattern, myQuant, myOptSpan)'
     */
    public Object[] getSpreadUncall() {
        Object[] result = {QSomeMaker,
          "run",
          myBuilder,
          myOptSubPattern,
          CharacterMakerSugar.valueOf(myQuant),
          myOptSpan};
        return result;
    }

    public AstroArg withOptSpan(SourceSpan optSpan) {
        return new QSome(myBuilder, myOptSubPattern, myQuant, optSpan);
    }

    /**
     *
     */
    static private boolean inBounds(int num, char quant) {
        switch (quant) {
        case'?':
            return num == 0 || num == 1;
        case'+':
            return num >= 1;
        case'*':
            return num >= 0;
        default:
            T.fail("Must be '?', '+', or '*': " + quant);
            return false; //make compiler happy
        }
    }

    /**
     *
     */
    public ConstList substSlice(ConstList args, int[] index) {
        T.notNull(myOptSubPattern,
                  "A ValueMaker must have a sub-pattern: ",
                  this);
        int shape = myOptSubPattern.startShape(args, null, index, -1);
        T.require(shape >= 0, "Indeterminate repetition: ", this);
        FlexList result = FlexList.fromType(Astro.class, shape);
        int lastDim = index.length;
        int[] subIndex = (int[])ArrayHelper.resize(index, lastDim + 1);
        for (int i = 0; i < shape; i++) {
            subIndex[lastDim] = i;
            ConstList slice = myOptSubPattern.substSlice(args, subIndex);
            result.append(slice);
        }
        myOptSubPattern.endShape(null, index, shape);
        T.require(inBounds(result.size(), myQuant),
                  "Improper quantity: " + shape,
                  " vs " + myQuant);
        return result.snapshot();
    }

    /**
     *
     */
    public int matchBindSlice(ConstList args,
                              ConstList specimenList,
                              FlexList bindings,
                              int[] index) {
        if (null == myOptSubPattern) {
            int result = specimenList.size();
            switch (myQuant) {
            case'?':
                return StrictMath.min(result, 1);
            case'+':
                return result <= 0 ? -1 : result;
            case'*':
                return result;
            default:
                T.fail("Unrecognized: " + myQuant);
            }
            return -666; //just so the compiler will know
        }
        int maxShape = myOptSubPattern.startShape(args, bindings, index, -1);
        int result = 0;
        int lastDim = index.length;
        int[] subIndex = (int[])ArrayHelper.resize(index, lastDim + 1);
        int shapeSoFar = 0;
        for (; maxShape == -1 || shapeSoFar < maxShape; shapeSoFar++) {
            if (specimenList.size() == 0) {
                break;
            }
            if (myQuant == '?' && result >= 1) {
                break;
            }
            subIndex[lastDim] = shapeSoFar;
            int more = myOptSubPattern.matchBindSlice(args,
                                                      specimenList,
                                                      bindings,
                                                      subIndex);
            if (-1 == more) {
                break;
            }
            T.require(more >= 1 || maxShape != -1,
                      "Patterns of indeterminate rank must make progress: ",
                      this,
                      " vs ",
                      specimenList);
            result += more;
            specimenList = specimenList.run(more, specimenList.size());
        }
        myOptSubPattern.endShape(bindings, index, shapeSoFar);
        T.require(inBounds(result, myQuant),
                  "Improper quantity: " + result,
                  " vs " + myQuant);
        return result;
    }

    /**
     *
     */

    int startShape(ConstList args,
                   FlexList optBindings,
                   int[] prefix,
                   int shapeSoFar) {
        if (null == myOptSubPattern) {
            return shapeSoFar;
        } else {
            return myOptSubPattern.startShape(args,
                                              optBindings,
                                              prefix,
                                              shapeSoFar);
        }
    }

    /**
     *
     */

    void endShape(FlexList optBindings, int[] prefix, int shape) {
        if (null == myOptSubPattern) {
            // Do nothing.
        } else {
            myOptSubPattern.endShape(optBindings, prefix, shape);
        }
    }

    /**
     *
     */
    public AstroArg qbuild(QuasiBuilder qbuilder) {
        //XXX what if myOptSubPattern is only an AstroArg, not an Astro?
        if (null == myOptSubPattern) {
            return qbuilder.some(null, myQuant);
        } else {
            AstroArg sub = myOptSubPattern.qbuild(qbuilder);
            return qbuilder.some(sub, myQuant);
        }
    }

    /**
     * If I have a sub-pattern, my height is the same as its.
     * <p/>
     * Otherwise, my height is 1, which is the height of a leaf.
     */
    public int getHeight() {
        if (null == myOptSubPattern) {
            return 1;
        } else {
            return myOptSubPattern.getHeight();
        }
    }

    /**
     *
     */
    public void prettyPrintOn(TextWriter out) throws IOException {
        if (null != myOptSubPattern) {
            myOptSubPattern.prettyPrintOn(out);
        }
        out.print("" + myQuant);
    }
}
