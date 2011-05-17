package org.quasiliteral.quasiterm;

import org.erights.e.develop.exception.ExceptionMgr;
import org.erights.e.elib.base.ClassDesc;
import org.erights.e.elib.base.SourceSpan;
import org.erights.e.elib.oldeio.EPrintable;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.serial.JOSSPassByConstruction;
import org.erights.e.elib.serial.Persistent;
import org.erights.e.elib.slot.Guard;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.EList;
import org.erights.e.elib.tables.FlexList;
import org.erights.e.elib.tables.Selfless;
import org.quasiliteral.astro.AstroArg;
import org.quasiliteral.astro.AstroBuilder;
import org.quasiliteral.term.QuasiBuilder;

import java.io.IOException;
import java.io.StringWriter;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

/**
 * @author Mark S. Miller
 */
public abstract class QAstroArg
  implements Selfless, JOSSPassByConstruction, Persistent, AstroArg, EPrintable {

    static private final long serialVersionUID = -677636576833337794L;

    /**
     *
     */
    static final int[] EMPTY_INDEX = {};

    /**
     *
     */
    static final Guard EListGuard = ClassDesc.make(EList.class);

    /**
     * @serial Builds Terms using the Schema derived from myTag.
     */
    final AstroBuilder myBuilder;

    /**
     * @serial What source text was originally lexed or parsed to produce this
     * node?
     */
    final SourceSpan myOptSpan;

    /**
     * @param builder For building the result of a substitute.
     * @param optSpan Where is the source text this node was extracted from?
     */
    QAstroArg(AstroBuilder builder, SourceSpan optSpan) {
        myBuilder = builder;
        myOptSpan = optSpan;
    }

    /**
     * @param args  Each arg[i] is of a type matched to the corresponding
     *              dollar-hole. For example, if dollar-hole 3 (ie "${3}") has
     *              rank "?*+", then args[3] must be a list of lists of lists
     *              of terms, for which the outer list may only be zero or one
     *              long, the middle lists may be any length, and the inner
     *              lists must all have at least one element.
     * @param index Further indexes after a hole's hole-num. For example, If a
     *              dollar-hole's hole-num is 3 and index is [4,5], then the
     *              dollar-hole would evaluate to args[3][4][5].
     * @return :Astro[]
     */
    public abstract ConstList substSlice(ConstList args, int[] index);

    /**
     * @param args         See the doc on 'args' in {@link #substSlice}.
     * @param specimenList :Astro[];
     * @param bindings     Like 'args', but by extraction from specimen
     * @param index        Further indexes after a hole's hole-num. For
     *                     example, If a dollar-hole's hole-num is 3 and index
     *                     is [4,5], then the dollar-hole would access
     *                     args[3][4][5]. Similarly, an at-hole with hole-num 3
     *                     would store into bindings[3][4][5].
     * @param max          Number of elements matched must not exceed max.
     * @return How many elements of specimen are matched?  Zero indicates a
     *         successful match of no elements, so -1 is used to instead
     *         indicate a failed match.
     */
    public abstract int matchBindSlice(ConstList args,
                                       ConstList specimenList,
                                       FlexList bindings,
                                       int[] index,
                                       int max);

    /**
     * What is the least number of specimen elements this pattern might need
     * to consume in order to match?
     * <p>
     * In ignorance, the answer should default to zero.
     */
    public abstract int reserve();

    /**
     * For this substree and this index-prefix, what's the most number of index
     * elements that should be enumerated?
     * <p/>
     * If this subtree has no dollar-holes, it should just return shapeSoFar.
     * The initial shapeSoFar is -1 (meaning "indeterminate"), so a tree with
     * no dollar-holes will just return -1. A non-ranking inner node (eg, a
     * QTerm) just asks all its children, passing to each the shapeSoFar from
     * the previous.
     * <p/>
     * All the rest of the semantics is specific to dollar-hole, at-hole, or to
     * raking nodes, so see the documentation there.
     */
    abstract int startShape(ConstList args,
                            FlexList optBindings,
                            int[] prefix,
                            int shapeSoFar);

    /**
     * For this subtree and this index elements, 'shape' is the number of index
     * elements that have been successfully enumerated.
     * <p/>
     * For each prefix, startShape and endShape form the opening and closing
     * brackets around calls to matchBindSlice or substSlice.
     */
    abstract void endShape(FlexList optBindings, int[] prefix, int shape);

    /**
     * What source text was originally lexed or parsed to produce this node, or
     * a representative token of this node?
     */
    public SourceSpan getOptSpan() {
        return myOptSpan;
    }

    /**
     *
     */
    public abstract AstroArg qbuild(QuasiBuilder qbuilder);

    /**
     * What's the longest distance to the bottom?
     * <p/>
     * A leaf node is height 1. All other nodes are one more than the height of
     * their highest child. This is used for pretty printing.
     */
    public abstract int getHeight();

    /**
     *
     */
    public void __printOn(TextWriter out) throws IOException {
        out.print("qterm`");
        prettyPrintOn(out.indent("     "));
        out.print("`");
    }

    /**
     *
     */
    public String toString() {
        return asText();
    }

    /**
     *
     */
    public String asText() {
        StringWriter strWriter = new StringWriter();
        try {
            prettyPrintOn(new TextWriter(strWriter));
        } catch (Throwable th) {
            throw ExceptionMgr.asSafe(th);
        }
        return strWriter.getBuffer().toString();
    }

    /**
     *
     */
    public abstract void prettyPrintOn(TextWriter out) throws IOException;
}
