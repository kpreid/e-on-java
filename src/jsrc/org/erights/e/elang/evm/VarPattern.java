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
import org.erights.e.elib.slot.Guard;
import org.erights.e.elib.slot.SettableSlot;
import org.erights.e.elib.slot.SimpleSlot;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.FlexList;
import org.erights.e.elib.util.OneArgFunc;

import java.io.IOException;

/**
 * BNF: 'var' ID (':' expr)?
 * <p/>
 * Defines a variable whose primitive slot holds the specimen (as coerced by
 * the guard, if any).
 *
 * @author Mark S. Miller
 */
public final class VarPattern extends NounPattern {

    /**
     * If 'varName' would shadow a non-shadowable, throw a (XXX to be defined)
     * exception instead.
     * <p/>
     * If the VarPattern would not be well-formed, throw a (XXX to be defined)
     * exception instead.
     */
    public VarPattern(SourceSpan optSpan,
                      AtomicExpr noun,
                      EExpr optGuardExpr,
                      ScopeLayout optScopeLayout) {
        super(optSpan, noun, optGuardExpr, false, optScopeLayout);
    }

    /**
     *
     */
    public VarPattern(SourceSpan optSpan,
                      AtomicExpr noun,
                      EExpr optGuardExpr,
                      boolean canShadow,
                      ScopeLayout optScopeLayout) {
        super(optSpan, noun, optGuardExpr, canShadow, optScopeLayout);
    }

    /**
     * Uses XXX 'makeFoo(...)'
     */
    public Object[] getSpreadUncall() {
        Object[] result = {StaticMaker.make(VarPattern.class),
          "run",
          getOptSpan(),
          getNoun(),
          getOptGuardExpr(),
          getOptScopeLayout()};
        return result;
    }

    /**
     *
     */
    public Object welcome(ETreeVisitor visitor) {
        return visitor.visitVarPattern(this, myNoun, myOptGuardExpr);
    }

    /**
     *
     */
    protected StaticScope computeStaticScope() {
        StaticScope result = StaticScope.scopeSlot(myNoun);
        if (null == myOptGuardExpr) {
            return result;
        } else {
            return result.add(myOptGuardExpr.staticScope());
        }
    }

    /**
     *
     */
    public void subMatchBind(ConstList args,
                             Object specimen,
                             OneArgFunc optEjector,
                             FlexList bindings) {
        VarPattern other;
        try {
            other = (VarPattern)specimen;
        } catch (ClassCastException cce) {
            throw Thrower.toEject(optEjector, cce);
        }
        myNoun.subMatchBind(args, other.myNoun, optEjector, bindings);
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
        if (null == myOptGuardExpr) {
            getNoun().initSlot(ctx, new SimpleSlot(specimen));
        } else {
            //Because of the well-formedness criteria, we may safely evaluate
            //this in the "wrong" order
            Object vg = myOptGuardExpr.subEval(ctx, true);
            Guard guard = (Guard)E.as(vg, Guard.class);
            getNoun().initSlot(ctx,
                               new SettableSlot(guard, specimen, optEjector));
        }
    }

    /**
     *
     */
    public NounPattern withNounExpr(NounExpr newNounExpr) {
        return new VarPattern(getOptSpan(), newNounExpr, null,
                              // XXX is this right?
                              getOptScopeLayout());
    }

    /**
     *
     */
    public void subPrintOn(TextWriter out, int priority) throws IOException {
        out.print("var ");
        myNoun.subPrintOn(out, priority);
        if (null != myOptGuardExpr) {
            out.print(" :");
            myOptGuardExpr.subPrintOn(out, PR_ORDER);
        }
    }
}
