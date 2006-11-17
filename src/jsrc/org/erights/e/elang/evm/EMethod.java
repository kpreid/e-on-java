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
import org.erights.e.elang.syntax.ELexer;
import org.erights.e.elang.visitors.ETreeVisitor;
import org.erights.e.elib.base.MessageDesc;
import org.erights.e.elib.base.SourceSpan;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.prim.StaticMaker;
import org.erights.e.elib.prim.Thrower;
import org.erights.e.elib.prim.VTable;
import org.erights.e.elib.ref.Ref;
import org.erights.e.elib.slot.Guard;
import org.erights.e.elib.slot.VoidGuard;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.FlexList;
import org.erights.e.elib.util.OneArgFunc;
import org.erights.e.meta.java.math.EInt;

import java.io.IOException;


/**
 * BNF: docComment "method" verb "(" patterns ")" (":" optResultGuard)? "{"
 * expr "}"
 * <p/>
 * A method defined in E, suitable for storing in a VTable
 *
 * @author Mark S. Miller
 * @see VTable
 */
public class EMethod extends ENode {

    private final String myDocComment;

    private final String myVerb;

    private final Pattern[] myPatterns;

    private final EExpr myOptResultGuard;

    private final EExpr myBody;

    private final int myLocalCount;

    /**
     * The constructor interns the verb
     */
    public EMethod(SourceSpan optSpan,
                   String docComment,
                   String verb,
                   Pattern[] patterns,
                   EExpr optResultGuard,
                   EExpr body,
                   ScopeLayout optScopeLayout) {
        super(optSpan, optScopeLayout);
        if (docComment == null) {
            docComment = "Oops, sysopsis was null";
        }
        myDocComment = docComment;
        myVerb = verb.intern();
        myPatterns = patterns;
        myOptResultGuard = optResultGuard;
        myBody = body;
        myLocalCount = -1;
    }

    /**
     * Create the EMethod with the number of locals to allocate to a stack
     * frame.
     * <p/>
     * This method assumes the verb is interned.
     */
    public EMethod(SourceSpan optSpan,
                   String docComment,
                   String verb,
                   Pattern[] patterns,
                   EExpr optResultGuard,
                   EExpr body,
                   int localCount,
                   ScopeLayout optScopeLayout) {
        super(optSpan, optScopeLayout);
        if (docComment == null) {
            docComment = "Oops, sysopsis was null";
        }
        myDocComment = docComment;
        myVerb = verb;
        myPatterns = patterns;
        myOptResultGuard = optResultGuard;
        myBody = body;
        myLocalCount = localCount;
    }

    /**
     * Uses XXX 'makeFoo(...)'
     */
    public Object[] getSpreadUncall() {
        return new Object[]{StaticMaker.make(EMethod.class),
          "run",
          getOptSpan(),
          myDocComment,
          myVerb,
          myPatterns,
          myOptResultGuard,
          myBody,
          EInt.valueOf(myLocalCount),
          getOptScopeLayout()};
    }

    /**
     *
     */
    public Object welcome(ETreeVisitor visitor) {
        return visitor.visitEMethod(this,
                                    myDocComment,
                                    myVerb,
                                    myPatterns,
                                    myOptResultGuard,
                                    myBody);
    }

    /**
     * When staticScope() is first requested on a given node, it calls
     * computeStaticScope() to do the actual computation, which is then
     * remembered.
     */
    protected StaticScope computeStaticScope() {
        StaticScope result = StaticScope.EmptyScope;
        for (int i = 0; i < myPatterns.length; i++) {
            result = result.add(myPatterns[i].staticScope());
        }
        if (null != myOptResultGuard) {
            result = result.add(myOptResultGuard.staticScope());
        }
        return result.add(myBody.staticScope()).hide();
    }

    /**
     *
     */
    Object execute(EImpl self, Object[] args) {
        EvalContext ctx = self.newContext(myLocalCount);
        for (int i = 0, max = args.length; i < max; i++) {
            myPatterns[i].testMatch(ctx, args[i], null);
        }
        if (null == myOptResultGuard) {
            //Evaluate forValue, to prepare for the new standard
            return myBody.subEval(ctx, true);
        } else {
            Object vg = myOptResultGuard.subEval(ctx, true);
            Guard optGuard = (Guard)E.as(vg, Guard.class);
            //Evaluate forValue, unless the return guard is ":void".
            Object result = myBody.subEval(ctx, optGuard != VoidGuard.THE_ONE);
            return optGuard.coerce(result, null);
        }
    }

    /**
     * Ignores docComment
     */
    public void subMatchBind(ConstList args,
                             Object specimen,
                             OneArgFunc optEjector,
                             FlexList bindings) {
        EMethod other;
        try {
            other = (EMethod)Ref.resolution(specimen);
        } catch (ClassCastException cce) {
            //using a try/catch since success is typical and we have
            //to pay for the test in the cast anyway
            throw Thrower.toEject(optEjector, cce);
        }
        if (!myVerb.equals(other.myVerb)) {
            throw Thrower.toEject(optEjector,
                                  "Mismatch: " + myVerb + " vs " + other
                                    .myVerb);
        }
        matchBind(myPatterns, args, other.myPatterns, optEjector, bindings);
        matchBind(myOptResultGuard,
                  args,
                  other.myOptResultGuard,
                  optEjector,
                  bindings);
        myBody.subMatchBind(args, other.myBody, optEjector, bindings);
    }

    /**
     * @deprecated Use {@link #getDocComment()}
     */
    public String docComment() {
        return myDocComment;
    }

    /**
     * @deprecated Use {@link #getVerb()}
     */
    public String verb() {
        return myVerb;
    }

    /**
     * @deprecated Use {@link #getPatterns()}
     */
    public Pattern[] patterns() {
        return myPatterns;
    }

    /**
     * @deprecated Use {@link #getBody()}
     */
    public EExpr body() {
        return myBody;
    }

    public String getDocComment() {
        return myDocComment;
    }

    public String getVerb() {
        return myVerb;
    }

    public Pattern[] getPatterns() {
        return myPatterns;
    }

    /**
     *
     */
    public EExpr getOptResultGuard() {
        return myOptResultGuard;
    }

    public EExpr getBody() {
        return myBody;
    }

    /**
     *
     */
    public void subPrintOn(TextWriter out, int priority) throws IOException {
        out.println();
        MessageDesc.synopsize(out, myDocComment);
        out.print("method ");
        ELexer.printVerbOn(myVerb, out);
        printListOn("(", myPatterns, ", ", ")", out, PR_PATTERN);
        if (null != myOptResultGuard) {
            out.print(" :");
            myOptResultGuard.subPrintOn(out, PR_ORDER);
        }
        out.print(" ");
        myBody.printAsBlockOn(out);
    }
}
