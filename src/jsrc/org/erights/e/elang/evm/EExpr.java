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
import org.erights.e.elang.scope.Scope;
import org.erights.e.elang.scope.ScopeLayout;
import org.erights.e.elang.visitors.BindFramesVisitor;
import org.erights.e.elang.visitors.SubstVisitor;
import org.erights.e.elang.visitors.VerifyEVisitor;
import org.erights.e.elib.base.SourceSpan;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.ref.Ref;
import org.erights.e.elib.serial.DeepPassByCopy;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.FlexList;
import org.erights.e.elib.tables.Memoizer;
import org.erights.e.elib.util.OneArgFunc;
import org.erights.e.meta.java.math.EInt;

import java.io.IOException;

/**
 * Those ParseNodes that--after expansion--define the kernel expressions
 * evaluated by the E Virtual Machine.
 *
 * @author Mark S. Miller
 * @author E. Dean Tribble
 */
public abstract class EExpr extends ENode {

    static private final long serialVersionUID = -7918236472800706525L;

    static private class Transformer implements OneArgFunc, DeepPassByCopy {

        static private final long serialVersionUID = 5695854634368095134L;

        Transformer() {
        }

        public Object run(Object arg) {
            ConstList args = (ConstList)arg;
            EExpr eExpr = (EExpr)args.get(0);
            ScopeLayout scopeLayout = (ScopeLayout)args.get(1);
            VerifyEVisitor vev = new VerifyEVisitor(scopeLayout);
            EExpr verifiedExpr = vev.xformEExpr(eExpr);

            // Do variable layout and a few simple optimizations.
            BindFramesVisitor bfv = BindFramesVisitor.make(scopeLayout);
            EExpr realExpr = bfv.xformEExpr(verifiedExpr);

            Object[] result = {realExpr,
              bfv.getOptScopeLayout(),
              EInt.valueOf(bfv.maxLocals())};
            return result;
        }

        /**
         * XXX We only say we implement this, but don't really, since we really
         * only need to be DeepFrozen; but currently the only way to declare
         * ourselves to be DeepFrozen is to claim to be DeepPassByCopy.
         */
        public Object[] getSpreadUncall() {
            T.fail("XXX not yet implemented");
            return null; //make compiler happy
        }
    }

    static private final Memoizer OurTransformer =
      new Memoizer(new Transformer(), 1000);

    /**
     *
     */
    protected EExpr(SourceSpan optSpan, ScopeLayout optScopeLayout) {
        super(optSpan, optScopeLayout);
    }

    /**
     * @see #subPrintOn
     */
    public void __printOn(TextWriter out) throws IOException {
        out.print("e`");
        subPrintOn(out, PR_EEXPR);
        out.print("`");
    }

    /**
     *
     */
    public EExpr substitute(ConstList args) {
        SubstVisitor visitor = new SubstVisitor(args);
        return visitor.xformEExpr(this);
    }

    /**
     * Verifies and Transforms from Expanded-E or Kernel-E to the internal
     * Transformed-E.
     * <p/>
     * The transform method was factored out from eval so that the interpreter
     * loop can print the transformed expression for debugging purposes.
     * <p/>
     * Now that this only takes a ScopeLayout as argument (rather than a
     * Scope), the [Expanded-E, ScopeLayout] => Transformed-E mapping can be
     * memoized. Once we compile Transformed-E to JVM bytecodes, this will
     * allow us to cache the class files and classes as well.
     *
     * @return A triple of <ul> <li>a Transformed-E expression, <li>the new
     *         ScopeLayout, and <li>the number of locals it needs to evaluate.
     *         </ul>
     */
    static private Object[] transform(EExpr self, ScopeLayout scopeLayout) {
        Object[] args = {self, scopeLayout};
        return (Object[])((ConstList)OurTransformer.run(args)).getArray();
    }

    /**
     * To be invoked only on a Transformed-E expression to do the actual
     * evaluation.
     * <p/>
     * This is the rest of {@link #eval(Scope) eval/1} after factoring out
     * {@link #transform(EExpr,ScopeLayout) transform/2}, and is likewise
     * public only for debugging purposes.
     * <p/>
     * XXX We need to enforce that this is only invoked on a Transformed-E
     * expression. The Transformed-E EExpr should probably remember the number
     * of locals it needs, so we don't have to worry about a bad value being
     * passed in.
     */
    static private Object eval(EExpr self, Scope scope, int maxLocals) {
        EvalContext ctx = scope.newContext(maxLocals);
        return Ref.resolution(self.subEval(ctx, true));
    }

    /**
     * Used to evaluate this expression in a scope to a value.
     */
    public Object eval(Scope scope) {
        return evalToPair(scope)[0];
    }

    /**
     * Used to evaluate this expression in a scope to a pair of a value and a
     * new scope.
     */
    public Object[] evalToPair(Scope scope) {
        ScopeLayout oldLayout = scope.getScopeLayout();
        Object[] triple = transform(this, oldLayout);
        EExpr realExpr = (EExpr)triple[0];
        Scope newScope = scope.update((ScopeLayout)triple[1]);
        int maxLocals = ((Integer)triple[2]).intValue();
        Object value = eval(realExpr, newScope, maxLocals);
        Object[] result = {value, newScope};
        return result;
    }

    /**
     * The recursive part that does the work
     */
    protected abstract Object subEval(EvalContext ctx, boolean forValue);

    /**
     * Like subEval, but returns the result coerced to a boolean.
     */
    boolean evalBool(EvalContext ctx) {
        return E.asBoolean(subEval(ctx, true));
    }

    /**
     *
     */
    public void printAsBlockOn(TextWriter out) throws IOException {
        out.print("{");
        TextWriter os2 = out.indent();
        os2.println();
        subPrintOn(os2, PR_EEXPR);
        out.lnPrint("}");
    }

    /**
     * Append the sequence of operations representated by this node to the
     * argument.
     */
    void appendTo(FlexList accum) {
        accum.push(this);
    }

    /**
     * Recursively append all the supplied nodes to accum.
     *
     * @param accum the List to accumulate the results into.
     * @param all   the EExprs to accumulate.
     */
    static void appendAllTo(FlexList accum, EExpr[] all) {
        for (int i = 0, max = all.length; i < max; i++) {
            all[i].appendTo(accum);
        }
    }
}
