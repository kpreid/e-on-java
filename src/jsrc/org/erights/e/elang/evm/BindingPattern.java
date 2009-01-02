package org.erights.e.elang.evm;

//Copyright 2009 Google, Inc. under the terms of the MIT X license
//found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.develop.assertion.T;
import org.erights.e.elang.scope.EvalContext;
import org.erights.e.elang.scope.ScopeLayout;
import org.erights.e.elang.visitors.ETreeVisitor;
import org.erights.e.elib.base.SourceSpan;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.prim.StaticMaker;
import org.erights.e.elib.prim.Thrower;
import org.erights.e.elib.slot.Slot;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.FlexList;
import org.erights.e.elib.util.OneArgFunc;

import java.io.IOException;

/**
 * BNF: '&&' ID
 * <p/>
 * Defines an indirect variable whose binder is the specimen
 *
 * @author Mark S. Miller
 */
public final class BindingPattern extends NounPattern {

    static private final long serialVersionUID = -1945315190159077332L;

    /**
     * If 'varName' would shadow a non-shadowable, throw a (XXX to be defined)
     * exception instead.
     * <p/>
     * If the BindingPattern would not be well-formed, throw a (XXX to be
     * defined) exception instead.
     */
    public BindingPattern(SourceSpan optSpan,
                          AtomicExpr noun,
                          ScopeLayout optScopeLayout) {
        super(optSpan, noun, null, false, optScopeLayout);
    }

    /**
     *
     */
    public BindingPattern(SourceSpan optSpan,
                       AtomicExpr noun,
                       boolean canShadow,
                       ScopeLayout optScopeLayout) {
        super(optSpan, noun, null, canShadow, optScopeLayout);
    }

    /**
     * Uses XXX 'makeFoo(...)'
     */
    public Object[] getSpreadUncall() {
        Object[] result = {StaticMaker.make(BindingPattern.class),
          "run",
          getOptSpan(),
          getNoun(),
          getOptScopeLayout()};
        return result;
    }

    /**
     *
     */
    public Object welcome(ETreeVisitor visitor) {
        return visitor.visitBindingPattern(this, myNoun);
    }

    /**
     *
     */
    protected StaticScope computeStaticScope() {
        return StaticScope.scopeSlot(myNoun);
    }

    /**
     *
     */
    public void subMatchBind(ConstList args,
                             Object specimen,
                             OneArgFunc optEjector,
                             FlexList bindings) {
        BindingPattern other;
        try {
            other = (BindingPattern)specimen;
        } catch (ClassCastException cce) {
            throw Thrower.toEject(optEjector, cce);
        }
        myNoun.subMatchBind(args, other.myNoun, optEjector, bindings);
    }

    /**
     *
     */
    void testMatch(EvalContext ctx, Object specimen, OneArgFunc optEjector) {
        T.fail("BindingPattern match not yet implemented");
        // XXX FIXME TODO fix to init binder rather than slot
        getNoun().initSlot(ctx,
                           (Slot)E.as(coercedSpecimen(ctx,
                                                      specimen,
                                                      optEjector),
                                      Slot.class));
    }

    /**
     *
     */
    public NounPattern withNounExpr(NounExpr newNounExpr) {
        return new BindingPattern(getOptSpan(), newNounExpr,
                               // XXX is this right?
                               getOptScopeLayout());
    }

    /**
     *
     */
    public void subPrintOn(TextWriter out, int priority) throws IOException {
        out.print("&&");
        myNoun.subPrintOn(out, priority);
    }
}
