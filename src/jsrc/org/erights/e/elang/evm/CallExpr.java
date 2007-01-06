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
import org.erights.e.elang.syntax.ELexer;
import org.erights.e.elang.visitors.ETreeVisitor;
import org.erights.e.elib.base.Ejection;
import org.erights.e.elib.base.SourceSpan;
import org.erights.e.elib.debug.EStackItem;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.Selector;
import org.erights.e.elib.prim.StaticMaker;
import org.erights.e.elib.prim.Thrower;
import org.erights.e.elib.ref.Ref;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.FlexList;
import org.erights.e.elib.util.OneArgFunc;
import org.erights.e.elib.vat.Runner;

import java.io.IOException;

/**
 * BNF: eExpr "." verb "(" eExpr* ")"
 * <p/>
 * A synchronous message call. Tells the expression's value to perform the
 * request now, and to pass an outcome (return a result, throw an exception,
 * non-local escape) synchronously back to the caller.
 *
 * @author Mark S. Miller
 */
public class CallExpr extends EExpr implements EStackItem {

    private static final long serialVersionUID = 756352279313115342L;

    /**
     *
     */
    private final EExpr myRecipient;

    /**
     *
     */
    private final String myVerb;

    /**
     *
     */
    private final EExpr[] myArgs;

    /**
     *
     */
    private final Selector mySelector;

    /**
     *
     */
    public CallExpr(SourceSpan optSpan,
                    EExpr recipient,
                    String verb,
                    EExpr[] args,
                    ScopeLayout optScopeLayout) {
        super(optSpan, optScopeLayout);
        myRecipient = recipient;
        myVerb = verb.intern();
        myArgs = args;
        mySelector = new Selector(myVerb, myArgs.length);
    }

    /**
     * Uses XXX 'makeFoo(...)'
     */
    public Object[] getSpreadUncall() {
        Object[] result = {StaticMaker.make(CallExpr.class),
          "run",
          getOptSpan(),
          myRecipient,
          myVerb,
          myArgs,
          getOptScopeLayout()};
        return result;
    }

    /**
     *
     */
    public Object welcome(ETreeVisitor visitor) {
        return visitor.visitCallExpr(this, myRecipient, myVerb, myArgs);
    }

    /**
     *
     */
    protected StaticScope computeStaticScope() {
        StaticScope result = myRecipient.staticScope();
        for (int i = 0; i < myArgs.length; i++) {
            result = result.add(myArgs[i].staticScope());
        }
        return result;
    }

    /**
     *
     */
    protected Object subEval(EvalContext ctx, boolean forValue) {
        Object receiver = myRecipient.subEval(ctx, true);
        Object[] argVals = new Object[myArgs.length];
        for (int i = 0, max = argVals.length; i < max; i++) {
            argVals[i] = myArgs[i].subEval(ctx, true);
        }

        Runner.pushEStackItem(this);
        try {
            //noinspection UnnecessaryLocalVariable
            Object result = mySelector.callIt(receiver, argVals);
            return result;
        } catch (Ejection ej) {
            throw ej;
        } catch (Throwable problem) {
            SourceSpan optSpan = getOptSpan();
            String msg = "@ " + myVerb + "/" + myArgs.length;
            if (null != optSpan) {
                msg += ": " + optSpan;
            }
            throw Ejection.backtrace(problem, msg);
        } finally {
            Runner.popEStackItem();
        }
    }

    /**
     *
     */
    public void subMatchBind(ConstList args,
                             Object specimen,
                             OneArgFunc optEjector,
                             FlexList bindings) {
        CallExpr other;
        try {
            other = (CallExpr)Ref.resolution(specimen);
        } catch (ClassCastException cce) {
            //using a try/catch since success is typical and we have
            //to pay for the test in the cast anyway
            throw Thrower.toEject(optEjector, cce);
        }
        myRecipient.subMatchBind(args,
                                 other.myRecipient,
                                 optEjector,
                                 bindings);
        if (!myVerb.equals(other.myVerb)) {
            throw Thrower.toEject(optEjector,
                                  "Mismatch: ." + myVerb + " vs ." + other
                                    .myVerb);
        }
        matchBind(myArgs, args, other.myArgs, optEjector, bindings);
    }

    /**
     * @deprecated Use {@link #getRecipient()}
     */
    public EExpr recipient() {
        return myRecipient;
    }

    /**
     * @deprecated Use {@link #getVerb()}
     */
    public String verb() {
        return myVerb;
    }

    /**
     * @deprecated Use {@link #getArgs()}
     */
    public EExpr[] args() {
        return myArgs;
    }

    public EExpr getRecipient() {
        return myRecipient;
    }

    public String getVerb() {
        return myVerb;
    }

    public EExpr[] getArgs() {
        return myArgs;
    }

    /**
     *
     */
    public void traceOn(TextWriter out) throws IOException {
        out.print(myVerb, "/" + myArgs.length);
    }

    /**
     *
     */
    public void subPrintOn(TextWriter out, int priority) throws IOException {
        if (PR_CALL < priority) {
            out.print("(");
        }
        myRecipient.subPrintOn(out, PR_CALL);
        out.print(".");
        ELexer.printVerbOn(myVerb, out);
        printListOn("(", myArgs, ", ", ")", out, PR_EEXPR);
        if (PR_CALL < priority) {
            out.print(")");
        }
    }
}
