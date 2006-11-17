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
 * BNF: "_"
 * <p/>
 * Matches anything succesfully, but binds nothing.
 *
 * @author Mark S. Miller
 */
public class IgnorePattern extends GuardedPattern {

    /**
     *
     */
    public IgnorePattern(SourceSpan optSpan,
                         EExpr optGuardExpr,
                         ScopeLayout optScopeLayout) {
        super(optSpan, optGuardExpr, optScopeLayout);
    }

    /**
     * Uses 'makeIgnorePattern(optSpan, myOptGuardExpr, optScopeLayout)'
     */
    public Object[] getSpreadUncall() {
        Object[] result = {StaticMaker.make(IgnorePattern.class),
          "run",
          getOptSpan(),
          getOptGuardExpr(),
          getOptScopeLayout()};
        return result;
    }

    /**
     *
     */
    public Object welcome(ETreeVisitor visitor) {
        return visitor.visitIgnorePattern(this, myOptGuardExpr);
    }

    /**
     *
     */
    protected StaticScope computeStaticScope() {
        if (null == myOptGuardExpr) {
            return StaticScope.EmptyScope;
        } else {
            return myOptGuardExpr.staticScope();
        }
    }

    /**
     *
     */
    public void subMatchBind(ConstList args,
                             Object specimen,
                             OneArgFunc optEjector,
                             FlexList bindings) {
        IgnorePattern other;
        try {
            other = (IgnorePattern)Ref.resolution(specimen);
        } catch (ClassCastException cce) {
            throw Thrower.toEject(optEjector, cce);
        }
        matchBind(myOptGuardExpr,
                  args,
                  other.myOptGuardExpr,
                  optEjector,
                  bindings);
    }

    /**
     *
     */
    void testMatch(EvalContext ctx, Object specimen, OneArgFunc optEjector) {
        coercedSpecimen(ctx, specimen, optEjector);
    }

    /**
     *
     */
    public String getOptName() {
        return null;
    }

    /**
     * Prints '_' or '_ : guardExpr'
     */
    public void subPrintOn(TextWriter out, int priority) throws IOException {
        out.print("_");
        if (null != myOptGuardExpr) {
            out.print(" :");
            myOptGuardExpr.subPrintOn(out, PR_ORDER);
        }
    }
}
