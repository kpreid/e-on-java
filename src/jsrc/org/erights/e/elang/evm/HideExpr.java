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
 * BNF: '{' expr '}'
 * <p/>
 * Evaluates expr in a lexically nested scope
 *
 * @author Mark S. Miller
 */
public class HideExpr extends EExpr {

    private final EExpr myBlock;

    /**
     *
     */
    public HideExpr(SourceSpan optSpan,
                    EExpr block,
                    ScopeLayout optScopeLayout) {
        super(optSpan, optScopeLayout);
        myBlock = block;
    }

    /**
     * Uses XXX 'makeFoo(...)'
     */
    public Object[] getSpreadUncall() {
        Object[] result = {StaticMaker.make(HideExpr.class),
          "run",
          getOptSpan(),
          myBlock,
          getOptScopeLayout()};
        return result;
    }

    /**
     *
     */
    public Object welcome(ETreeVisitor visitor) {
        return visitor.visitHideExpr(this, myBlock);
    }

    /**
     * @deprecated Use {@link #getBlock()}
     */
    public EExpr block() {
        return myBlock;
    }

    public EExpr getBlock() {
        return myBlock;
    }

    /**
     *
     */
    protected StaticScope computeStaticScope() {
        return myBlock.staticScope().hide();
    }

    /**
     *
     */
    protected Object subEval(EvalContext ctx, boolean forValue) {
        return myBlock.subEval(ctx, forValue);
    }

    /**
     *
     */
    public void subMatchBind(ConstList args,
                             Object specimen,
                             OneArgFunc optEjector,
                             FlexList bindings) {
        HideExpr other;
        try {
            other = (HideExpr)Ref.resolution(specimen);
        } catch (ClassCastException cce) {
            //using a try/catch since success is typical and we have
            //to pay for the test in the cast anyway
            throw Thrower.toEject(optEjector, cce);
        }
        myBlock.subMatchBind(args, other.myBlock, optEjector, bindings);
    }

    /**
     *
     */
    public void subPrintOn(TextWriter out, int priority) throws IOException {
        myBlock.printAsBlockOn(out);
    }
}
