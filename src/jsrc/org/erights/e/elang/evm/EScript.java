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
import org.erights.e.elang.scope.ScopeLayout;
import org.erights.e.elang.visitors.ETreeVisitor;
import org.erights.e.elib.base.SourceSpan;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.StaticMaker;
import org.erights.e.elib.prim.Thrower;
import org.erights.e.elib.ref.Ref;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.FlexList;
import org.erights.e.elib.tables.FlexSet;
import org.erights.e.elib.util.OneArgFunc;

import java.io.IOException;

/**
 * <pre>
 * BNF: script ::= "{" method* matcher* "}"
 *      |      ::= matcher+
 * </pre>
 * In the first form, the optMethods instance variable will point to an array,
 * even if it's empty. In the second form, optMethods is null and matchers
 * contains at least one element.
 *
 * @author Mark S. Miller
 */
public class EScript extends ENode {

    static private final long serialVersionUID = 5253320942306051912L;

    final EMethod[] myOptMethods;

    final EMatcher[] myMatchers;

    /**
     *
     */
    public EScript(SourceSpan optSpan,
                   EMethod[] optMethods,
                   EMatcher[] matchers,
                   ScopeLayout optScopeLayout) {
        super(optSpan, optScopeLayout);
        if (optMethods == null && 1 != matchers.length) {
            T.fail("The plumbingExpr must have exactly one matcher");
        }
        myOptMethods = optMethods;
        myMatchers = matchers;

        if (null != optMethods) {
            FlexSet mverbs = FlexSet.fromType(String.class, optMethods.length);
            for (int i = 0, len = optMethods.length; i < len; i++) {
                EMethod meth = optMethods[i];
                String mverb =
                  meth.getVerb() + "/" + meth.getPatterns().length;
                T.require(!mverbs.contains(mverb),
                          "Conflicting definitions of ",
                          mverb);
                mverbs.addElement(mverb);
            }
        }
    }

    /**
     * Uses XXX 'makeFoo(...)'
     */
    public Object[] getSpreadUncall() {
        Object[] result = {StaticMaker.make(EScript.class),
          "run",
          getOptSpan(),
          myOptMethods,
          myMatchers,
          getOptScopeLayout()};
        return result;
    }

    /**
     *
     */
    public Object welcome(ETreeVisitor visitor) {
        return visitor.visitEScript(this, myOptMethods, myMatchers);
    }


    /**
     *
     */
    protected StaticScope computeStaticScope() {
        StaticScope result = StaticScope.EmptyScope;
        if (myOptMethods != null) {
            for (int i = 0, max = myOptMethods.length; i < max; i++) {
                EMethod meth = myOptMethods[i];
                result = result.add(meth.staticScope().hide());
            }
        }
        for (int i = 0, max = myMatchers.length; i < max; i++) {
            EMatcher matcher = myMatchers[i];
            result = result.add(matcher.staticScope().hide());
        }
        return result;
    }

    /**
     *
     */
    public void subMatchBind(ConstList args,
                             Object specimen,
                             OneArgFunc optEjector,
                             FlexList bindings) {
        EScript other;
        try {
            other = (EScript)Ref.resolution(specimen);
        } catch (ClassCastException cce) {
            //using a try/catch since success is typical and we have
            //to pay for the test in the cast anyway
            throw Thrower.toEject(optEjector, cce);
        }
        matchBind(myOptMethods,
                  args,
                  other.myOptMethods,
                  optEjector,
                  bindings);
        matchBind(myMatchers, args, other.myMatchers, optEjector, bindings);
    }

    public ConstList getOptMethods() {
        if (null == myOptMethods) {
            return null;
        } else {
            return ConstList.fromArray(myOptMethods);
        }
    }

    public ConstList getMatchers() {
        return ConstList.fromArray(myMatchers);
    }

    /**
     *
     */
    public void subPrintOn(TextWriter out, int priority) throws IOException {
        if (myOptMethods == null) {
            //then myOptMatcher != null. We already checked.
            //We're printing with the plumbing syntax
            for (int i = 0, max = myMatchers.length; i < max; i++) {
                out.print(" ");
                myMatchers[i].subPrintOn(out, PR_PRIM);
            }
        } else {
            out.print(" {");
            TextWriter sub = out.indent();
            for (int i = 0; i < myOptMethods.length; i++) {
                myOptMethods[i].lnPrintOn(sub, PR_PRIM);
            }
            for (int i = 0, max = myMatchers.length; i < max; i++) {
                myMatchers[i].lnPrintOn(sub, PR_PRIM);
            }
            out.lnPrint("}");
        }
    }
}
