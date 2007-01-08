package org.erights.e.elang.scope;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.develop.assertion.T;

/**
 * A nested scope for new local variables.
 *
 * @author E. Dean Tribble
 * @author some mods by Mark S. Miller
 */
class ScopeLayoutInnerContour extends ScopeLayoutContour {

    static private final long serialVersionUID = -1144161348419258465L;

    /**
     *
     */
    ScopeLayoutInnerContour(ScopeLayout next) {
        super(-1, next);
    }

    /**
     * Uses 'myNext.nest(null)'
     */
    public Object[] getSpreadUncall() {
        Object[] result = {myNext, "nest", null};
        return result;
    }

    /**
     * @param fqnPrefix
     * @return
     */
    public ScopeLayout withPrefix(String fqnPrefix) {
        T.fail("internal: Can't do withPrefix to an inner scope " + this);
        return null; // make compiler happy
    }

    /*
     * If null, there is no need for two contiguous boundaries, so just return
     * self.
     */
    public ScopeLayout nest() {
        return this;
    }
}
