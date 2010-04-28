package org.erights.e.elang.evm;

/*
Copyright University of Southampton IT Innovation Centre, 2010,
under the terms of the MIT X license, available from
http://www.opensource.org/licenses/mit-license.html
*/

import org.erights.e.elang.scope.EvalContext;
import org.erights.e.elang.scope.Scope;
import org.erights.e.elib.base.Thunk;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.ref.Ref;

import java.io.IOException;

/**
 * Holds transformed E and its Scope for later execution.
 * This object provides only the ability to see or run the transformed E.
 */
public final class CompiledE implements Thunk {
    private EExpr myTransformed;
    private Scope myScope;
    private int myMaxLocals;

    // package
    CompiledE(EExpr transformed, Scope scope, int maxLocals) {
        myTransformed = transformed;
        myScope = scope;
        myMaxLocals = maxLocals;
    }

    public Object run() {
        EvalContext ctx = myScope.newContext(myMaxLocals);
        return Ref.resolution(myTransformed.subEval(ctx, true));
    }

    public void __printOn(TextWriter out) throws IOException {
        out.print("compiled-");
        myTransformed.__printOn(out);
    }
}
