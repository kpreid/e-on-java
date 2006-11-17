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
 * BNF: eExpr "\n" eExpr
 * <p/>
 * Do one and then the other. Evaluates to the value of the second.
 *
 * @author Mark S. Miller
 */
public class SeqExpr extends EExpr {

    private final EExpr[] mySubs;

    /**
     *
     */
    public SeqExpr(SourceSpan optSpan,
                   EExpr[] subs,
                   ScopeLayout optScopeLayout) {
        super(optSpan, optScopeLayout);
        FlexList accum = FlexList.fromType(EExpr.class, subs.length);
        appendAllTo(accum, subs);
        mySubs = (EExpr[])accum.getArray();
    }

    /**
     * Uses XXX 'makeFoo(...)'
     */
    public Object[] getSpreadUncall() {
        Object[] result = {StaticMaker.make(SeqExpr.class),
          "run",
          getOptSpan(),
          mySubs,
          getOptScopeLayout()};
        return result;
    }

    /**
     *
     */
    public Object welcome(ETreeVisitor visitor) {
        return visitor.visitSeqExpr(this, mySubs);
    }

    /**
     *
     */
    protected StaticScope computeStaticScope() {
        StaticScope result = mySubs[0].staticScope();
        // NOTE the loop begins at 1, not 0
        for (int i = 1, max = mySubs.length; i < max; i++) {
            result = result.add(mySubs[i].staticScope());
        }
        return result;
    }

    /**
     *
     */
    protected Object subEval(EvalContext ctx, boolean forValue) {
        // only compute a value for the last sub
        int last = mySubs.length - 1;
        for (int i = 0; i < last; i++) {
            mySubs[i].subEval(ctx, false);
        }
        return mySubs[last].subEval(ctx, forValue);
    }

    /**
     *
     */
    public void subMatchBind(ConstList args,
                             Object specimen,
                             OneArgFunc optEjector,
                             FlexList bindings) {
        SeqExpr other;
        try {
            other = (SeqExpr)Ref.resolution(specimen);
        } catch (ClassCastException cce) {
            //using a try/catch since success is typical and we have
            //to pay for the test in the cast anyway
            throw Thrower.toEject(optEjector, cce);
        }

        int len = mySubs.length;
        EExpr[] spSubs = other.mySubs;
        int spLen = spSubs.length;
        if (len > spLen) {
            throw Thrower.toEject(optEjector,
                                  "Arity mismatch: " + mySubs + " vs " + other
                                    .mySubs);
        }
        if (len < spLen) {
            // The specimen is longer than the pattern, so group the extra
            // subsequence of the specimen as an initial SeqExpr.
            // We group the initial extras together rather than the final
            // extras, since program analyzers will typically be more
            // interested in the final one -- since it may be evaluated for
            // value.
            int clumpLen = spLen - len + 1;
            EExpr[] clump = new EExpr[clumpLen];
            System.arraycopy(other.mySubs, 0, clump, 0, clumpLen);
            spSubs = new EExpr[len];
            spSubs[0] = new SeqExpr(null, clump, null);
            System.arraycopy(other.mySubs, clumpLen, spSubs, 1, len - 1);
        }
        for (int i = 0; i < len; i++) {
            mySubs[i].subMatchBind(args, spSubs[i], optEjector, bindings);
        }
    }

    /**
     *
     */
    public void subPrintOn(TextWriter out, int priority) throws IOException {
        if (priority > PR_EEXPR) {
            out.print("(");
        }
        boolean first = true;
        for (int i = 0, max = mySubs.length; i < max; i++) {
            if (!first) {
                out.println();
            }
            first = false;
            mySubs[i].subPrintOn(out, PR_EEXPR);
        }
        if (priority > PR_EEXPR) {
            out.print(")");
        }
    }

    /**
     * @deprecated Use {@link #getSubs()}
     */
    public EExpr[] subs() {
        return mySubs;
    }

    public EExpr[] getSubs() {
        return mySubs;
    }

    /**
     * Append subs in place of the receiver.
     */
    void appendTo(FlexList accum) {
        appendAllTo(accum, mySubs);
    }
}
