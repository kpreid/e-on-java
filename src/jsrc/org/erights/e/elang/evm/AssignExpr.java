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
 * BNF: varName ":=" eExpr
 * <p/>
 * Changes the binding for 'varName' in the current environment to be the value
 * of 'eExpr'. 'varName' must already be bound. Equivalent to
 * <pre>    (&varName).put(eExpr); eExpr</pre>
 * except that eExpr is only evaluated once. Therefore, the value returned is
 * the value of eExpr, rather than the value stored in the slot (since put/1
 * may do arbitrary coercions).
 *
 * @author Mark S. Miller
 */
public class AssignExpr extends EExpr {

    static private final long serialVersionUID = -3329301239758013581L;

    static private final StaticMaker AssignExprMaker =
      StaticMaker.make(AssignExpr.class);

    private final AtomicExpr myNoun;

    private final EExpr myRValue;

    /**
     *
     */
    public AssignExpr(SourceSpan optSpan,
                      AtomicExpr noun,
                      EExpr rValue,
                      ScopeLayout optScopeLayout) {
        super(optSpan, optScopeLayout);
        myNoun = noun;
        myRValue = rValue;
    }

    /**
     * Uses 'makeAssignExpr(optSpan, optScopeLayout, noun, rValue)'
     */
    public Object[] getSpreadUncall() {
        Object[] result = {AssignExprMaker,
          "run",
          getOptSpan(),
          myNoun,
          myRValue,
          getOptScopeLayout()};
        return result;
    }

    /**
     *
     */
    public Object welcome(ETreeVisitor visitor) {
        return visitor.visitAssignExpr(this, myNoun, myRValue);
    }

    /**
     *
     */
    protected StaticScope computeStaticScope() {
        // PING: Why add the noun to the scope when it should already be in
        //       the scope?
        // MarkM: Because a StaticScope is a bottom up representation,
        //        used primarily to guide the transformation from E to
        //        Expanded-E. The computeStaticScope methods like this
        //        one are where it comes from.
        StaticScope leftScope = StaticScope.scopeAssign(myNoun);
        return leftScope.add(myRValue.staticScope());
    }

    /**
     *
     */
    protected Object subEval(EvalContext ctx, boolean forValue) {
        Object result = myRValue.subEval(ctx, true);
        try {
            getNoun().assign(ctx, result);
        } catch (Throwable problem) {
            throw Ejection.backtrace(problem,
                                     "# assigning " + getNoun().getName());
        }
        return result;
    }

    /**
     *
     */
    public void subMatchBind(ConstList args,
                             Object specimen,
                             OneArgFunc optEjector,
                             FlexList bindings) {
        AssignExpr other;
        try {
            other = (AssignExpr)Ref.resolution(specimen);
        } catch (ClassCastException cce) {
            //using a try/catch since success is typical and we have
            //to pay for the test in the cast anyway
            throw Thrower.toEject(optEjector, cce);
        }
        myNoun.subMatchBind(args, other.myNoun, optEjector, bindings);
        myRValue.subMatchBind(args, other.myRValue, optEjector, bindings);
    }

    /**
     * @deprecated Use {@link #getNoun()}
     */
    public NounExpr noun() {
        return myNoun.asNoun();
    }

    /**
     * @deprecated Use {@link #getRValue()}
     */
    public EExpr rValue() {
        return myRValue;
    }

    public NounExpr getNoun() {
        return myNoun.asNoun();
    }

    public EExpr getRValue() {
        return myRValue;
    }

    /**
     *
     */
    public void subPrintOn(TextWriter out, int priority) throws IOException {
        if (priority > PR_ASSIGN) {
            out.print("(");
        }
        myNoun.subPrintOn(out, PR_PRIM);
        out.print(" := ");
        myRValue.subPrintOn(out, PR_ASSIGN);
        if (priority > PR_ASSIGN) {
            out.print(")");
        }
    }
}
