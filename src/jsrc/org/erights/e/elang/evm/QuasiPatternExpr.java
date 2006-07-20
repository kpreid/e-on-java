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
import org.erights.e.elang.scope.ScopeLayout;
import org.erights.e.elang.visitors.ETreeVisitor;
import org.erights.e.elib.base.SourceSpan;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.StaticMaker;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.FlexList;
import org.erights.e.elib.util.AlreadyDefinedException;
import org.erights.e.elib.util.OneArgFunc;
import org.erights.e.meta.java.math.EInt;
import org.quasiliteral.base.IncompleteQuasiException;

import java.io.IOException;

/**
 * BNF: '@' '{' <number> '}'
 * <p>
 * Not part of a valid E program, but a part of an E parse tree
 * acting as a MatchMaker (as a pattern to be matched against an E
 * program).
 *
 * @author Mark S. Miller
 */
public class QuasiPatternExpr extends AtomicExpr {

    private final int myIndex;

    /**
     *
     */
    public QuasiPatternExpr(SourceSpan optSpan,
                            int index,
                            ScopeLayout optScopeLayout) {
        super(optSpan, optScopeLayout);
        myIndex = index;
    }

    /**
     * Uses XXX 'makeFoo(...)'
     */
    public Object[] getSpreadUncall() {
        Object[] result = { StaticMaker.make(QuasiPatternExpr.class),
                            "run",
                            getOptSpan(),
                            EInt.valueOf(myIndex),
                            getOptScopeLayout() };
        return result;
    }

    /**
     *
     */
    public Object welcome(ETreeVisitor visitor) {
        return visitor.visitQuasiPatternExpr(this, myIndex);
    }

    /**
     *
     */
    protected StaticScope computeStaticScope() {
        return StaticScope.EmptyScope;
        //XXX this becomes incorrect if a scope-significant tree gets matched
        //with here
    }

    /**
     *
     */
    protected Object subEval(EvalContext ctx, boolean forValue) {
        throw new IncompleteQuasiException
          ("Can't evaluate programs that contain bare \"@\"s");
    }

    /**
     *
     */
    public int index() {
        return myIndex;
    }

    /**
     *
     */
    public void subMatchBind(ConstList args,
                             Object specimen,
                             OneArgFunc optEjector,
                             FlexList bindings) {
        bindings.ensureSize(myIndex + 1);
        if (bindings.get(myIndex) != null) {
            throw new AlreadyDefinedException("conflict defining @{"
                                              + myIndex + "}");
        }
        bindings.put(myIndex, specimen);
    }

    /**
     *
     */
    public void matchBind(ConstList args,
                          Object optSpecimen,
                          OneArgFunc optEjector,
                          FlexList bindings) {
        bindings.ensureSize(myIndex + 1);
        if (bindings.get(myIndex) != null) {
            throw new AlreadyDefinedException("conflict defining @{"
                                              + myIndex + "}");
        }
        bindings.put(myIndex, optSpecimen);
    }

    /**
     *
     */
    public void subPrintOn(TextWriter out, int priority) throws IOException {
        out.print("@{" + myIndex + "}");
    }

    public NounExpr asNoun() {
        T.fail("\"@\"-holes should have already been transformed out");
        return null; //make compiler happy
    }
}
