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

import org.erights.e.develop.exception.ExceptionMgr;
import org.erights.e.develop.exception.ThrowableSugar;
import org.erights.e.develop.trace.Trace;
import org.erights.e.elang.scope.EvalContext;
import org.erights.e.elang.scope.ScopeLayout;
import org.erights.e.elang.visitors.ETreeVisitor;
import org.erights.e.elib.base.Ejection;
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
 * BNF: "try" block "catch" pattern block.
 * <p/>
 * Evaluates the try-block, but should it throw a Throwable, binds pattern to
 * the Throwable and evaluates the catch-block. The outcome of the catch
 * expression is the outcome of the try-block, or the outcome of the
 * catch-block if the try-block threw a Throwable.
 *
 * @author Mark S. Miller
 * @see FinallyExpr
 * @see EscapeExpr
 */
public class CatchExpr extends EExpr {

    private final EExpr myAttempt;

    private final Pattern myPattern;

    private final EExpr myCatcher;

    /**
     *
     */
    public CatchExpr(SourceSpan optSpan,
                     EExpr attempt,
                     Pattern pattern,
                     EExpr catcher,
                     ScopeLayout optScopeLayout) {
        super(optSpan, optScopeLayout);
        myAttempt = attempt;
        myPattern = pattern;
        myCatcher = catcher;
    }

    /**
     * Uses XXX 'makeFoo(...)'
     */
    public Object[] getSpreadUncall() {
        return new Object[]{
          StaticMaker.make(CatchExpr.class),
          "run",
          getOptSpan(),
          myAttempt,
          myPattern,
          myCatcher,
          getOptScopeLayout()};
    }

    /**
     *
     */
    public Object welcome(ETreeVisitor visitor) {
        return visitor.visitCatchExpr(this, myAttempt, myPattern, myCatcher);
    }

    /**
     *
     */
    protected StaticScope computeStaticScope() {
        StaticScope attemptScope = myAttempt.staticScope();
        StaticScope patternScope = myPattern.staticScope();
        StaticScope catcherScope = myCatcher.staticScope();

        return attemptScope.hide()
          .add(patternScope.add(catcherScope)).hide();
    }

    /**
     * Evaluates to the outcome of the try block, but if it throws an
     * Throwable, evaluate to the outcome of the catch block.
     * <p/>
     * Subtle but important semantic point: Not only do we rethrow the original
     * problem if the catch-pattern fails to match, we also throw the original
     * problem if the match attempt throws, since the original problem is
     * assumed to be a more important diagnostic than the new Throwable.
     * <p/>
     * If the match attempt instead does a non-local escape, then this ejection
     * dominates and the original problem is lost.
     */
    protected Object subEval(EvalContext ctx, boolean forValue) {
        try {
            return myAttempt.subEval(ctx, forValue);
        } catch (Ejection ej) {
            throw ej;
        } catch (Throwable problem) {
            Throwable leaf = ThrowableSugar.leaf(problem);
            if (leaf instanceof Ejection) {
                throw (Ejection)leaf;
            }
            try {
                myPattern.testMatch(ctx, problem, null);
            } catch (Throwable problem2) {
                throw ExceptionMgr.asSafe(problem);
            }
            if (Trace.causality.event && Trace.ON) {
                Trace.causality.eventm("caught by E-lang catcher: ", problem);
            }
            return myCatcher.subEval(ctx, forValue);
        }
    }

    /**
     *
     */
    public void subMatchBind(ConstList args,
                             Object specimen,
                             OneArgFunc optEjector,
                             FlexList bindings) {
        CatchExpr other;
        try {
            other = (CatchExpr)Ref.resolution(specimen);
        } catch (ClassCastException cce) {
            //using a try/catch since success is typical and we have
            //to pay for the test in the cast anyway
            throw Thrower.toEject(optEjector, cce);
        }
        myAttempt.subMatchBind(args, other.myAttempt, optEjector, bindings);
        myPattern.subMatchBind(args, other.myPattern, optEjector, bindings);
        myCatcher.subMatchBind(args, other.myCatcher, optEjector, bindings);
    }

    /**
     * @deprecated Use {@link #getAttempt()}
     */
    public EExpr attempt() {
        return myAttempt;
    }

    /**
     * @deprecated Use {@link #getPattern()}
     */
    public Pattern pattern() {
        return myPattern;
    }

    /**
     * @deprecated Use {@link #getCatcher()}
     */
    public EExpr katch() {
        return myCatcher;
    }

    public EExpr getAttempt() {
        return myAttempt;
    }

    public Pattern getPattern() {
        return myPattern;
    }

    public EExpr getCatcher() {
        return myCatcher;
    }

    /**
     *
     */
    public void subPrintOn(TextWriter out, int priority) throws IOException {
        out.print("try ");
        myAttempt.printAsBlockOn(out);
        out.print(" catch ");
        myPattern.subPrintOn(out, PR_PATTERN);
        out.print(" ");
        myCatcher.printAsBlockOn(out);
    }
}
