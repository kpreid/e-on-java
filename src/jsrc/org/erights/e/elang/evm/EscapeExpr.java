package org.erights.e.elang.evm;

/*
The contents of this file are subject to the Electric Communities E Open
Source Code License Version 1.0 (the "License"); you may not use this file
except in compliance with the License. You may obtain a copy of the License
at http://www.communities.com/EL/.

Software distributed under the License is distributed on an "AS IS" basis,
WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
the specific language governing rights and limitations under the License.

The Original Code is the Distributed E Language Implementation, released
July 20, 1998.

The Initial Developer of the Original Code is Electric Communities.
Copyright (C) 1998 Electric Communities. All Rights Reserved.

Contributor(s): ______________________________________.
*/

import org.erights.e.develop.assertion.T;
import org.erights.e.elang.scope.EvalContext;
import org.erights.e.elang.scope.ScopeLayout;
import org.erights.e.elang.visitors.ETreeVisitor;
import org.erights.e.elib.base.Ejector;
import org.erights.e.elib.base.SourceSpan;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.StaticMaker;
import org.erights.e.elib.prim.Thrower;
import org.erights.e.elib.ref.Ref;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.FlexList;
import org.erights.e.elib.util.OneArgFunc;

import java.io.IOException;


/**
 * <pre>BNF: &quot;escape&quot; pattern1 &quot;{&quot;
 *     expr1
 * &quot;}&quot; (&quot;catch&quot; pattern2 &quot;{&quot;
 *     expr2
 * &quot;}&quot;)?</pre>
 * <p/>
 * Evaluates 'expr1' in an environment where 'pattern1' is bound to an Ejector.
 * If the Ejector is never called, expr1 evalutes normally to the outcome of
 * the EscapeExpr.
 * <p/>
 * If Ejector is called during the execution of the expr1, expr1 exits, running
 * any finally clauses on the way out. If we are still exiting for the same
 * reason (i.e., given that non of the intervening finally clauses performed
 * their own non-local exit), then pattern2 is matched against the ejector
 * argument and, in the resulting scope, expr2 is evaluated to the outcome of
 * the escape expression.
 *
 * @author Mark S. Miller
 * @see org.erights.e.elang.evm.CatchExpr
 * @see org.erights.e.elang.evm.FinallyExpr
 */
public class EscapeExpr extends EExpr {

    static private final long serialVersionUID = 2132596738828586688L;

    private final Pattern myExitPatt;

    private final EExpr myRValue;

    private final Pattern myOptArgPattern;

    private final EExpr myOptCatcher;

    /**
     * @param optSpan
     * @param exitPatt
     * @param rValue
     * @param optArgPattern
     * @param optCatcher
     * @param optScopeLayout
     */
    public EscapeExpr(SourceSpan optSpan,
                      Pattern exitPatt,
                      EExpr rValue,
                      Pattern optArgPattern,
                      EExpr optCatcher,
                      ScopeLayout optScopeLayout) {
        super(optSpan, optScopeLayout);
        myExitPatt = exitPatt;
        myRValue = rValue;
        myOptArgPattern = optArgPattern;
        myOptCatcher = optCatcher;
        if (null == myOptArgPattern) {
            T.require(null == myOptCatcher,
                      "Can't have an escape catcher without a pattern");
        } else {
            T.notNull(myOptCatcher,
                      "Can't have a catch-pattern without a catcher");
        }
    }

    /**
     * Uses XXX 'makeFoo(...)'
     */
    public Object[] getSpreadUncall() {
        Object[] result = {StaticMaker.make(EscapeExpr.class),
          "run",
          getOptSpan(),
          myExitPatt,
          myRValue,
          myOptArgPattern,
          myOptCatcher,
          getOptScopeLayout()};
        return result;
    }

    /**
     *
     */
    public Object welcome(ETreeVisitor visitor) {
        return visitor.visitEscapeExpr(this,
                                       myExitPatt,
                                       myRValue,
                                       myOptArgPattern,
                                       myOptCatcher);
    }

    /**
     *
     */
    protected StaticScope computeStaticScope() {
        StaticScope ejScope = myExitPatt.staticScope();
        StaticScope bodyScope = myRValue.staticScope();
        StaticScope result = ejScope.add(bodyScope).hide();
        if (null == myOptArgPattern) {
            return result;
        } else {
            StaticScope argScope = myOptArgPattern.staticScope();
            StaticScope catcherScope = myOptCatcher.staticScope();
            return result.add(argScope.add(catcherScope)).hide();
        }
    }

    /**
     *
     */
    protected Object subEval(EvalContext ctx, boolean forValue) {
        String optName = myExitPatt.getOptName();
        if (null == optName) {
            optName = "escape-expr";
        }
        Ejector ejector = new Ejector(optName);
        try {
            myExitPatt.testMatch(ctx, ejector, null);
            return myRValue.subEval(ctx, forValue);
        } catch (Throwable t) {
            Object arg = ejector.result(t);
            ejector.disable();
            if (null == myOptArgPattern) {
                return arg;
            } else {
                myOptArgPattern.testMatch(ctx, arg, null);
                return myOptCatcher.subEval(ctx, forValue);
            }
        } finally {
            ejector.disable();
        }
    }

    /**
     *
     */
    public void subMatchBind(ConstList args,
                             Object specimen,
                             OneArgFunc optEjector,
                             FlexList bindings) {
        EscapeExpr other;
        try {
            other = (EscapeExpr)Ref.resolution(specimen);
        } catch (ClassCastException cce) {
            //using a try/catch since success is typical and we have
            //to pay for the test in the cast anyway
            throw Thrower.toEject(optEjector, cce);
        }
        myExitPatt.subMatchBind(args, other.myExitPatt, optEjector, bindings);
        myRValue.subMatchBind(args, other.myRValue, optEjector, bindings);
        matchBind(myOptArgPattern,
                  args,
                  other.myOptArgPattern,
                  optEjector,
                  bindings);
        matchBind(myOptCatcher,
                  args,
                  other.myOptCatcher,
                  optEjector,
                  bindings);
    }

    /**
     * @deprecated Use {@link #getExitPatt()}
     */
    public Pattern exitPattern() {
        return myExitPatt;
    }

    /**
     * @deprecated Use {@link #getRValue()}
     */
    public EExpr rValue() {
        return myRValue;
    }

    public Pattern getExitPatt() {
        return myExitPatt;
    }

    public EExpr getRValue() {
        return myRValue;
    }

    /**
     * @return
     */
    public Pattern getOptArgPattern() {
        return myOptArgPattern;
    }

    /**
     * @return
     */
    public EExpr getOptCatcher() {
        return myOptCatcher;
    }

    /**
     *
     */
    public void subPrintOn(TextWriter out, int priority) throws IOException {
        out.print("escape ");
        myExitPatt.subPrintOn(out, PR_PATTERN);
        out.print(" ");
        myRValue.printAsBlockOn(out);
        if (null != myOptArgPattern) {
            out.print(" catch ");
            myOptArgPattern.subPrintOn(out, PR_PATTERN);
            out.print(" ");
            myOptCatcher.printAsBlockOn(out);
        }
    }
}
