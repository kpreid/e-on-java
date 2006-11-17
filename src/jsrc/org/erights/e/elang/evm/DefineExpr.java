// Copyright 2006 Hewlett Packard, under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.elang.evm;

import org.erights.e.elang.scope.EvalContext;
import org.erights.e.elang.scope.ScopeLayout;
import org.erights.e.elang.visitors.ETreeVisitor;
import org.erights.e.elib.base.SourceSpan;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.prim.StaticMaker;
import org.erights.e.elib.prim.Thrower;
import org.erights.e.elib.ref.Ref;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.ConstMap;
import org.erights.e.elib.tables.FlexList;
import org.erights.e.elib.util.OneArgFunc;

import java.io.IOException;

/**
 * BNF: "def" pattern ("exit" eExpr)? ":=" eExpr
 *
 * @author Mark S. Miller
 */
public class DefineExpr extends EExpr {

    private final Pattern myPattern;

    private final EExpr myOptEjectorExpr;

    private final EExpr myRValue;

    /**
     *
     */
    public DefineExpr(SourceSpan optSpan,
                      Pattern pattern,
                      EExpr optEjectorExpr,
                      EExpr rValue,
                      ScopeLayout optScopeLayout) {
        super(optSpan, optScopeLayout);
        myPattern = pattern;
        myOptEjectorExpr = optEjectorExpr;
        myRValue = rValue;
        ensureWellFormed();
    }

    private void ensureEmpty(ConstMap conflicts, String msg) {
        if (conflicts.size() >= 1) {
            ENode node = ((ENode[])conflicts.getValues(ENode.class))[0];
            fail(msg + ": " + E.toString(conflicts.getKeys()), node);
        }
    }

    private void ensureWellFormed() {
        StaticScope left = myPattern.staticScope();
        StaticScope right = myRValue.staticScope();
        if (null != myOptEjectorExpr) {
            right = myOptEjectorExpr.staticScope().add(right);
        }
        ensureEmpty(left.outNames().and(right.namesUsed()),
                    "Use on right isn't really in scope of definition");
        ensureEmpty(right.outNames().and(left.namesUsed()),
                    "Use on left would get captured by definition on right");
    }

    /**
     * Uses 'makeDefineExpr(optSpan, patt, optEj, rValue, optScope)'
     */
    public Object[] getSpreadUncall() {
        Object[] result = {StaticMaker.make(DefineExpr.class),
          "run",
          getOptSpan(),
          myPattern,
          myOptEjectorExpr,
          myRValue,
          getOptScopeLayout()};
        return result;
    }

    /**
     *
     */
    public Object welcome(ETreeVisitor visitor) {
        return visitor.visitDefineExpr(this,
                                       myPattern,
                                       myOptEjectorExpr,
                                       myRValue);
    }

    /**
     *
     */
    protected StaticScope computeStaticScope() {
        // Although this is calculated pattern first, ensureWellFormed()
        // guarantees that we'd get the same result if we used the pattern
        // last. (XXX modulo meta.getState().)
        StaticScope result = myPattern.staticScope();
        if (null != myOptEjectorExpr) {
            result = result.add(myOptEjectorExpr.staticScope());
        }
        return result.add(myRValue.staticScope());
    }

    /**
     *
     */
    protected Object subEval(EvalContext ctx, boolean forValue) {
        OneArgFunc optEjector = null;
        if (null != myOptEjectorExpr) {
            optEjector = (OneArgFunc)E.as(myOptEjectorExpr.subEval(ctx, true),
                                          OneArgFunc.class);
        }
        Object result = myRValue.subEval(ctx, true);
        myPattern.testMatch(ctx, result, optEjector);
        return result;
    }

    /**
     *
     */
    public void subMatchBind(ConstList args,
                             Object specimen,
                             OneArgFunc optEjector,
                             FlexList bindings) {
        DefineExpr other;
        try {
            other = (DefineExpr)Ref.resolution(specimen);
        } catch (ClassCastException cce) {
            //using a try/catch since success is typical and we have
            //to pay for the test in the cast anyway
            throw Thrower.toEject(optEjector, cce);
        }
        myPattern.subMatchBind(args, other.myPattern, optEjector, bindings);
        matchBind(myOptEjectorExpr,
                  args,
                  other.myOptEjectorExpr,
                  optEjector,
                  bindings);
        myRValue.subMatchBind(args, other.myRValue, optEjector, bindings);
    }

    /**
     *
     */
    public void subPrintOn(TextWriter out, int priority) throws IOException {
        if (priority > PR_ASSIGN) {
            out.print("(");
        }
        out.print("def ");
        myPattern.subPrintOn(out, PR_PATTERN);
        if (null != myOptEjectorExpr) {
            out.print(" exit ");
            myOptEjectorExpr.subPrintOn(out, PR_CALL);
        }
        out.print(" := ");
        myRValue.subPrintOn(out, PR_ASSIGN);
        if (priority > PR_ASSIGN) {
            out.print(")");
        }
    }

    /**
     *
     */
    public Pattern getPattern() {
        return myPattern;
    }

    /**
     *
     */
    public EExpr getOptEjectorExpr() {
        return myOptEjectorExpr;
    }

    /**
     *
     */
    public EExpr getRValue() {
        return myRValue;
    }
}
