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
import org.erights.e.elib.base.ClassDesc;
import org.erights.e.elib.base.SourceSpan;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.StaticMaker;
import org.erights.e.elib.prim.Thrower;
import org.erights.e.elib.ref.Ref;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.EList;
import org.erights.e.elib.tables.FlexList;
import org.erights.e.elib.util.OneArgFunc;

import java.io.IOException;

/**
 * BNF: "[" sub-patterns "]"
 * <p>
 * The list only matches of it has exactly as many elements as there are
 * sub-patterns, and each subpattern matches its corresponding element.
 *
 * @author Mark S. Miller
 */
public class ListPattern extends Pattern {

    /**
     *
     */
    static private final ClassDesc EListGuard = ClassDesc.make(EList.class);

    /**
     *
     */
    private final Pattern[] mySubPatterns;

    /**
     *
     */
    public ListPattern(SourceSpan optSpan,
                       Pattern[] subs,
                       ScopeLayout optScopeLayout) {
        super(optSpan, optScopeLayout);
        mySubPatterns = subs;
    }

    /**
     * Uses XXX 'makeFoo(...)'
     */
    public Object[] getSpreadUncall() {
        Object[] result = { StaticMaker.make(ListPattern.class),
                            "run",
                            getOptSpan(),
                            mySubPatterns,
                            getOptScopeLayout() };
        return result;
    }

    /**
     *
     */
    public Object welcome(ETreeVisitor visitor) {
        return visitor.visitListPattern(this, mySubPatterns);
    }

    /**
     *
     */
    protected StaticScope computeStaticScope() {
        StaticScope result = StaticScope.EmptyScope;
        for (int i = 0; i < mySubPatterns.length; i++) {
            result = result.add(mySubPatterns[i].staticScope());
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
        ListPattern other;
        try {
            other = (ListPattern)Ref.resolution(specimen);
        } catch (ClassCastException cce) {
            //using a try/catch since success is typical and we have
            //to pay for the test in the cast anyway
            throw Thrower.toEject(optEjector, cce);
        }
        matchBind(mySubPatterns,
                  args,
                  other.mySubPatterns,
                  optEjector,
                  bindings);
    }

    /**
     *
     */
    void testMatch(EvalContext ctx,
                   Object specimen,
                   OneArgFunc optEjector) {
        EList list = (EList)EListGuard.coerce(specimen, optEjector);

        int len = list.size();
        if (len != mySubPatterns.length) {
            throw Thrower.toEject(optEjector,
                                  "a " + len +
                                  " size list doesn't match a " +
                                  mySubPatterns.length + " size list pattern");
        }
        for (int i = 0, max = mySubPatterns.length; i < max; i++) {
            mySubPatterns[i].testMatch(ctx, list.get(i), optEjector);
        }
    }

    /**
     * @deprecated Use {@link #getSubPatterns()}
     */
    Pattern[] subPatterns() {
        return mySubPatterns;
    }

    public Pattern[] getSubPatterns() {
        return mySubPatterns;
    }

    /**
     *
     */
    public String getOptName() {
        return null;
    }

    /**
     *
     */
    public void subPrintOn(TextWriter out, int priority) throws IOException {
        printListOn("[", mySubPatterns, ", ", "]", out, PR_PATTERN);
    }
}
