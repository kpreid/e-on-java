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

import org.erights.e.elang.scope.EvalContext;
import org.erights.e.elang.scope.ScopeLayout;
import org.erights.e.elang.visitors.ETreeVisitor;
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
 * BNF: "try" block "finally" block
 * <p/>
 * Evaluates the try-clause, but exiting to the enclosing context also evaluate
 * the finally-clause. If the finally-clause exits normally (evaluates to a
 * value), then the exit of the FinallyExpr as a whole is the exit of the
 * try-clause. In other words, if the try-clause evaluates to a value, then the
 * FinallyExpr evaluates to that same value. If the try-clause throws or
 * escapes, then the FinallyExpr as a whole likewise throws or escapes.
 * <p/>
 * On the other hand, if the finally-clause does a non-local exit (throws or
 * escapes) then the FinallyExpr as a whole exits in that way. A non-local
 * finally-clause exit replaces the try-clause exit as the means of exiting.
 *
 * @author Mark S. Miller
 */
public class FinallyExpr extends EExpr {

    static private final long serialVersionUID = 3848079257716668626L;

    private final EExpr myAttempt;

    private final EExpr myUnwinder;

    /**
     *
     */
    public FinallyExpr(SourceSpan optSpan,
                       EExpr attempt,
                       EExpr unwinder,
                       ScopeLayout optScopeLayout) {
        super(optSpan, optScopeLayout);
        myAttempt = attempt;
        myUnwinder = unwinder;
    }

    /**
     * Uses XXX 'makeFoo(...)'
     */
    public Object[] getSpreadUncall() {
        Object[] result = {StaticMaker.make(FinallyExpr.class),
          "run",
          getOptSpan(),
          myAttempt,
          myUnwinder,
          getOptScopeLayout()};
        return result;
    }

    /**
     *
     */
    public Object welcome(ETreeVisitor visitor) {
        return visitor.visitFinallyExpr(this, myAttempt, myUnwinder);
    }

    /**
     *
     */
    protected StaticScope computeStaticScope() {
        StaticScope attemptScope = myAttempt.staticScope();
        StaticScope finallyScope = myUnwinder.staticScope();

        return attemptScope.hide().add(finallyScope).hide();
    }

    /**
     *
     */
    protected Object subEval(EvalContext ctx, boolean forValue) {
        try {
            return myAttempt.subEval(ctx, forValue);
        } finally {
            myUnwinder.subEval(ctx, false);
        }
    }

    /**
     *
     */
    public void subMatchBind(ConstList args,
                             Object specimen,
                             OneArgFunc optEjector,
                             FlexList bindings) {
        FinallyExpr other;
        try {
            other = (FinallyExpr)Ref.resolution(specimen);
        } catch (ClassCastException cce) {
            //using a try/catch since success is typical and we have
            //to pay for the test in the cast anyway
            throw Thrower.toEject(optEjector, cce);
        }
        myAttempt.subMatchBind(args, other.myAttempt, optEjector, bindings);
        myUnwinder.subMatchBind(args, other.myUnwinder, optEjector, bindings);
    }

    /**
     * @deprecated Use {@link #getAttempt()}
     */
    public EExpr attempt() {
        return myAttempt;
    }

    /**
     * @deprecated Use {@link #getUnwinder()}
     */
    public EExpr unwinder() {
        return myUnwinder;
    }

    public EExpr getAttempt() {
        return myAttempt;
    }

    public EExpr getUnwinder() {
        return myUnwinder;
    }

    /**
     *
     */
    public void subPrintOn(TextWriter out, int priority) throws IOException {
        out.print("try ");
        myAttempt.printAsBlockOn(out);
        out.print(" finally ");
        myUnwinder.printAsBlockOn(out);
    }
}
