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
import org.erights.e.elang.scope.Scope;
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
 * BNF: "meta.getState()"
 * <p/>
 *
 * @author Mark S. Miller
 * @see org.erights.e.elang.scope.Scope
 */
public class MetaStateExpr extends EExpr {

    static private final long serialVersionUID = 3482629771935305668L;

    /**
     *
     */
    public MetaStateExpr(SourceSpan optSpan, ScopeLayout optScopeLayout) {
        super(optSpan, optScopeLayout);
    }

    /**
     * Uses XXX 'makeFoo(...)'
     */
    public Object[] getSpreadUncall() {
        Object[] result = {StaticMaker.make(MetaStateExpr.class),
          "run",
          getOptSpan(),
          getOptScopeLayout()};
        return result;
    }

    /**
     *
     */
    public Object welcome(ETreeVisitor visitor) {
        return visitor.visitMetaStateExpr(this);
    }

    /**
     *
     */
    protected StaticScope computeStaticScope() {
        return StaticScope.scopeMeta();
    }

    /**
     *
     */
    protected Object subEval(EvalContext ctx, boolean forValue) {
        return new Scope(getScopeLayout(), ctx).getState();
    }

    /**
     *
     */
    public void subMatchBind(ConstList args,
                             Object specimen,
                             OneArgFunc optEjector,
                             FlexList bindings) {
        specimen = Ref.resolution(specimen);
        if (!(specimen instanceof MetaStateExpr)) {
            throw Thrower.toEject(optEjector,
                                  "Must be a meta.getState(): " + specimen);
        }
    }

    /**
     *
     */
    public void subPrintOn(TextWriter out, int priority) throws IOException {
        out.print("meta.getState()");
    }
}
