// Copyright 2006 Hewlett Packard, under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.elang.syntax;

import org.erights.e.elang.evm.EExpr;
import org.erights.e.elang.evm.Pattern;
import org.erights.e.elang.evm.StaticScope;
import org.erights.e.elang.scope.ScopeLayout;
import org.erights.e.elib.base.SourceSpan;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.StaticMaker;
import org.erights.e.elib.prim.Thrower;
import org.erights.e.elib.ref.Ref;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.FlexList;
import org.erights.e.elib.util.OneArgFunc;
import org.quasiliteral.astro.Astro;

import java.io.IOException;

/**
 * @author Mark S. Miller
 */
class MatchBindExpr extends DelayedExpr {

    static private final long serialVersionUID = -4808538736464418350L;

    private final EExpr mySpecimen;
    private final Pattern myPattern;

    MatchBindExpr(SourceSpan optSpan,
                  EExpr specimen,
                  Pattern pattern,
                  ScopeLayout optScopeLayout) {
        super(optSpan, optScopeLayout);
        mySpecimen = specimen;
        myPattern = pattern;
    }

    /**
     * If specimen exports, then<pre>
     *     def sp1 := specimen
     *     super.forValue(sp1 =~ pattern)</pre>
     * else<pre>
     *     super.forValue(specimen =~ pattern)</pre>
     */
    EExpr forValue(ENodeBuilder b, StaticScope optUsed) {
        String[] exports = mySpecimen.getExports(optUsed);
        if (1 <= exports.length) {
            Astro sp1 = b.newTemp("sp");
            MatchBindExpr mbe =
              new MatchBindExpr(getOptSpan(), noun(sp1), myPattern, null);
            return b.sequence(b.define(b.finalPattern(sp1), mySpecimen),
                              mbe.forValue(b, optUsed));
        } else {
            // base case
            return super.forValue(b, optUsed);
        }
    }

    /**
     * If specimen exports, then<pre>
     *     def sp1 := specimen
     *     super.forFxOnly(sp1 =~ pattern)</pre>
     * else<pre>
     *     super.forFxOnly(specimen =~ pattern)</pre>
     */
    EExpr forFxOnly(ENodeBuilder b, StaticScope optUsed) {
        String[] exports = mySpecimen.getExports(optUsed);
        if (1 <= exports.length) {
            Astro sp1 = b.newTemp("sp");
            MatchBindExpr mbe =
              new MatchBindExpr(getOptSpan(), noun(sp1), myPattern, null);
            return b.sequence(b.define(b.finalPattern(sp1), mySpecimen),
                              mbe.forFxOnly(b, optUsed));
        } else {
            // base case
            return super.forFxOnly(b, optUsed);
        }
    }

    /**
     * <pre>
     *   forControl(expr =~ patt,ej)</pre>
     * expands to<pre>
     *   def patt exit ej := expr</pre>
     */
    EExpr forControl(ENodeBuilder b, Astro ej, StaticScope optUsed) {
        return b.kerneldef(myPattern, noun(ej), mySpecimen);
    }

    /**
     * Uses 'makeMatchBind(optSpan, specimen, pattern, optScopeLayout)'
     */
    public Object[] getSpreadUncall() {
        Object[] result = {StaticMaker.make(MatchBindExpr.class),
          "run",
          getOptSpan(),
          mySpecimen,
          myPattern,
          getOptScopeLayout()};
        return result;
    }

    /**
     *
     */
    protected StaticScope computeStaticScope() {
        return mySpecimen.staticScope().add(myPattern.staticScope());
    }

    /**
     *
     */
    public void subMatchBind(ConstList args,
                             Object specimen,
                             OneArgFunc optEjector,
                             FlexList bindings) {
        MatchBindExpr other;
        try {
            other = (MatchBindExpr)Ref.resolution(specimen);
        } catch (ClassCastException cce) {
            //using a try/catch since success is typical and we have
            //to pay for the test in the cast anyway
            throw Thrower.toEject(optEjector, cce);
        }
        mySpecimen.matchBind(args, other.mySpecimen, optEjector, bindings);
        myPattern.matchBind(args, other.myPattern, optEjector, bindings);
    }

    public EExpr getSpecimen() {
        return mySpecimen;
    }

    public Pattern getPattern() {
        return myPattern;
    }

    /**
     *
     */
    public void subPrintOn(TextWriter out, int priority) throws IOException {
        if (PR_COMP < priority) {
            out.print("(");
        }
        mySpecimen.subPrintOn(out, PR_ORDER);
        out.print(" =~ ");
        myPattern.subPrintOn(out, PR_PATTERN);
        if (PR_COMP < priority) {
            out.print(")");
        }
    }
}
