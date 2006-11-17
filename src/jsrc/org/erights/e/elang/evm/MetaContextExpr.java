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
import org.erights.e.elang.scope.StaticContext;
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
 * BNF: "meta.context()"
 * <p/>
 *
 * @author Mark S. Miller
 * @see Scope
 */
public class MetaContextExpr extends EExpr {

    private final StaticContext myOptContext;

    /**
     *
     */
    public MetaContextExpr(SourceSpan optSpan, ScopeLayout optScopeLayout) {
        super(optSpan, optScopeLayout);
        myOptContext = null;
    }

    /**
     *
     */
    public MetaContextExpr(SourceSpan optSpan,
                           StaticContext context,
                           ScopeLayout optScopeLayout) {
        super(optSpan, optScopeLayout);
        myOptContext = context;
    }

    /**
     * Uses XXX 'makeFoo(...)'
     */
    public Object[] getSpreadUncall() {
        Object[] result = {StaticMaker.make(MetaContextExpr.class),
          "run",
          getOptSpan(),
          myOptContext,
          getOptScopeLayout()};
        return result;
    }

    /**
     *
     */
    public Object welcome(ETreeVisitor visitor) {
        return visitor.visitMetaContextExpr(this);
    }

    /**
     *
     */
    protected StaticScope computeStaticScope() {
        return StaticScope.EmptyScope;
    }

    /**
     *
     */
    protected Object subEval(EvalContext ctx, boolean forValue) {
        T.notNull(myOptContext, "Internal: Not yet verified: ", this);
        return myOptContext;
    }

    /**
     *
     */
    public void subMatchBind(ConstList args,
                             Object specimen,
                             OneArgFunc optEjector,
                             FlexList bindings) {
        specimen = Ref.resolution(specimen);
        if (!(specimen instanceof MetaContextExpr)) {
            throw Thrower.toEject(optEjector,
                                  "Must be a meta.context(): " + specimen);
        }
    }

    public StaticContext getOptContext() {
        return myOptContext;
    }

    /**
     *
     */
    public void subPrintOn(TextWriter out, int priority) throws IOException {
        out.print("meta.context()");
    }
}
