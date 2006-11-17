package org.erights.e.elang.scope;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.develop.assertion.T;

/**
 * A nested scope for new outer variables.
 * <p/>
 * This is a peculiar case which exists only to support the interactive
 * cmdLoop, in which each top level expression is in a nested scope.
 *
 * @author E. Dean Tribble
 * @author some mods by Mark S. Miller
 */
class ScopeLayoutOuterContour extends ScopeLayoutContour {

    /**
     *
     */
    ScopeLayoutOuterContour(ScopeLayout next) {
        super(next.getOuterCount(), next);
        T.require(myOuterCount >= 0, "Scope confusion");
    }

    /**
     * Uses 'myNext.nestOuter()'
     */
    public Object[] getSpreadUncall() {
        Object[] result = {myNext, "nestOuter"};
        return result;
    }

    /**
     * @param fqnPrefix
     * @return
     */
    public ScopeLayout withPrefix(String fqnPrefix) {
        return new ScopeLayoutOuterContour(myNext.withPrefix(fqnPrefix));
    }
}
