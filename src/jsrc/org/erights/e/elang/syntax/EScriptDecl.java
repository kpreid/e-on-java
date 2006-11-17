package org.erights.e.elang.syntax;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.elang.evm.EMatcher;
import org.erights.e.elang.evm.EMethod;
import org.erights.e.elang.evm.EScript;

/**
 * Just a bundling of state for use during parsing
 *
 * @author Mark S. Miller
 */
class EScriptDecl {

    private final EMethod[] myOptMethods;

    private final EMatcher[] myMatchers;

    /**
     *
     */
    EScriptDecl(EMethod[] optMethods, EMatcher[] matchers) {
        myOptMethods = optMethods;
        myMatchers = matchers;
    }

    /**
     *
     */
    EMethod[] getOptMethods() {
        return myOptMethods;
    }

    /**
     *
     */
    EMatcher[] getMatchers() {
        return myMatchers;
    }

    /**
     *
     */
    EScriptDecl withMatcher(EMatcher newLast) {
        int oldLen = myMatchers.length;
        EMatcher[] matchers = new EMatcher[oldLen + 1];
        System.arraycopy(myMatchers, 0, matchers, 0, oldLen);
        matchers[oldLen] = newLast;
        return new EScriptDecl(myOptMethods, matchers);
    }

    /**
     *
     */
    EScript makeEScript(ENodeBuilder eBuilder) {
        return new EScript(null, myOptMethods, myMatchers, null);
    }
}
