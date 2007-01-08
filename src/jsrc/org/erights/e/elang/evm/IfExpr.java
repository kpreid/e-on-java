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
 * BNF: "if" "(" cond-expr ")" "{" then-expr "}" "else" "{" else-expr "}"
 * <p/>
 * Implements the "if" expression. If the cond-expr evaluates to true, the
 * then-expr is evaluated in a child of the scope produced by the cond-expr.
 * Otherwise, the else-expr is evaluated in a child of the outer scope.
 *
 * @author Mark S. Miller
 */
public class IfExpr extends EExpr {

    static private final long serialVersionUID = -6351631208557413950L;

    private final EExpr myTest;

    private final EExpr myThen;

    private final EExpr myElse;

    /**
     *
     */
    public IfExpr(SourceSpan optSpan,
                  EExpr test,
                  EExpr then,
                  EExpr els,
                  ScopeLayout optScopeLayout) {
        super(optSpan, optScopeLayout);
        myTest = test;
        myThen = then;
        myElse = els;
    }

    /**
     * Uses XXX 'makeFoo(...)'
     */
    public Object[] getSpreadUncall() {
        Object[] result = {StaticMaker.make(IfExpr.class),
          "run",
          getOptSpan(),
          myTest,
          myThen,
          myElse,
          getOptScopeLayout()};
        return result;
    }

    /**
     *
     */
    public Object welcome(ETreeVisitor visitor) {
        return visitor.visitIfExpr(this, myTest, myThen, myElse);
    }

    /**
     *
     */
    protected StaticScope computeStaticScope() {
        StaticScope testScope = myTest.staticScope();
        StaticScope thenScope = myThen.staticScope();
        StaticScope elseScope = myElse.staticScope();
        return testScope.add(thenScope).hide().add(elseScope).hide();
    }

    /**
     *
     */
    protected Object subEval(EvalContext ctx, boolean forValue) {
        if (myTest.evalBool(ctx)) {
            //the then part executes in a scope containing bindings
            //from the conditional
            return myThen.subEval(ctx, forValue);
        } else {
            //else is evaluated in a new scope with no bindings from
            //the conditional
            return myElse.subEval(ctx, forValue);
        }
    }

    /**
     *
     */
    public void subMatchBind(ConstList args,
                             Object specimen,
                             OneArgFunc optEjector,
                             FlexList bindings) {
        IfExpr other;
        try {
            other = (IfExpr)Ref.resolution(specimen);
        } catch (ClassCastException cce) {
            //using a try/catch since success is typical and we have
            //to pay for the test in the cast anyway
            throw Thrower.toEject(optEjector, cce);
        }
        myTest.subMatchBind(args, other.myTest, optEjector, bindings);
        myThen.subMatchBind(args, other.myThen, optEjector, bindings);
        myElse.subMatchBind(args, other.myElse, optEjector, bindings);
    }

    /**
     * @deprecated Use {@link #getTest()}
     */
    public EExpr test() {
        return myTest;
    }

    /**
     * @deprecated Use {@link #getThen()}
     */
    public EExpr then() {
        return myThen;
    }

    /**
     * @deprecated Use {@link #getElse()}
     */
    public EExpr els() {
        return myElse;
    }

    public EExpr getTest() {
        return myTest;
    }

    public EExpr getThen() {
        return myThen;
    }

    public EExpr getElse() {
        return myElse;
    }

    /**
     *
     */
    public void subPrintOn(TextWriter out, int priority) throws IOException {
        out.print("if (");
        myTest.subPrintOn(out.indent("      "), PR_EEXPR);
        out.print(") ");
        myThen.printAsBlockOn(out);
        out.print(" else ");
        myElse.printAsBlockOn(out);
    }
}
