package org.erights.e.elang.scope;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.elang.evm.NounPattern;
import org.erights.e.elang.evm.ParseNode;
import org.erights.e.elib.tables.FlexSet;

/**
 * A ScopeLayout consisting of an empty innermost contour on top of some other
 * ScopeLayout.
 *
 * @author E. Dean Tribble
 * @author some mods by Mark S. Miller
 */
abstract class ScopeLayoutContour extends ScopeLayout {

    final ScopeLayout myNext;

    ScopeLayoutContour(int outerCount, ScopeLayout next) {
        super(outerCount);
        myNext = next;
    }

    /**
     *
     * @return
     */
    public String getFQNPrefix() {
        return myNext.getFQNPrefix();
    }

    /**
     *
     */
    public NounPattern getOptPattern(String varName) {
        return myNext.getOptPattern(varName);
    }

    /**
     *
     */
    boolean contains(String varName) {
        return myNext.contains(varName);
    }

    /**
     *
     */
    void addNamesTo(FlexSet names) {
        myNext.addNamesTo(names);
    }

    /**
     * Succeeds, since all names are shadowable here.
     */
    public void requireShadowable(String varName, ParseNode optPoser) {
    }
}
