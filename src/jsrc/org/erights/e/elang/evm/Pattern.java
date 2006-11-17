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
import org.erights.e.elang.visitors.SubstVisitor;
import org.erights.e.elib.base.SourceSpan;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.util.OneArgFunc;

import java.io.IOException;


/**
 * A Pattern 1) "evaluates" in a scope, 2) matches some specimen object, 3)
 * binding names in this scope to values derived (usually extracted) from the
 * specimen, and 4) returns whether the match was successful.
 *
 * @author Mark S. Miller
 */
public abstract class Pattern extends ENode {

    /**
     *
     */
    protected Pattern(SourceSpan optSpan, ScopeLayout optScopeLayout) {
        super(optSpan, optScopeLayout);
    }

    /**
     * @see #subPrintOn
     */
    public void __printOn(TextWriter out) throws IOException {
        out.print("epatt`");
        subPrintOn(out, PR_PATTERN);
        out.print("`");
    }

    /**
     *
     */
    public Pattern substitute(ConstList args) {
        SubstVisitor visitor = new SubstVisitor(args);
        return visitor.xformPattern(this);
    }

    /**
     * If this pattern matches the specimen, add macthing bindings to the
     * scope.
     * <p/>
     * Otherwise report the reason why not according to optEjector.
     */
    abstract void testMatch(EvalContext ctx,
                            Object specimen,
                            OneArgFunc optEjector);

    /**
     * If this pattern is the binding occurence of a name, and it would bind
     * the name to a coercion of the specimen as a whole, return the name. <p>
     * <p/>
     * Else return null. The purpose is to support the extraction of fully
     * qualified names for object-definition expressions
     */
    public abstract String getOptName();
}
