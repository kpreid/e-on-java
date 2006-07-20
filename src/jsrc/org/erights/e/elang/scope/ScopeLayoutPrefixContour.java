package org.erights.e.elang.scope;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.develop.assertion.T;
import org.erights.e.elib.prim.E;

/**
 * A nested scope for a new fully qualified prefix, for tagging nested object
 * definitions.
 *
 * @author E. Dean Tribble
 * @author some mods by Mark S. Miller
 */
class ScopeLayoutPrefixContour extends ScopeLayoutContour {

    private final String myFQNPrefix;

    /**
     *
     */
    ScopeLayoutPrefixContour(ScopeLayout next, String fqnPrefix) {
        super(-1, next);
        myFQNPrefix = fqnPrefix;
        ensureValidFQNPrefix(myFQNPrefix);
    }

    /**
     * Uses 'myNext.nest(myFQNPrefix)'
     */
    public Object[] getSpreadUncall() {
        Object[] result = { myNext, "nest", myFQNPrefix };
        return result;
    }

    /**
     *
     */
    public String getFQNPrefix() {
        return myFQNPrefix;
    }

    /**
     *
     */
    public ScopeLayout withPrefix(String fqnPrefix) {
        T.fail("internal: Can't do withPrefix to an inner scope " +
          E.toString(this));
        return null; // make compiler happy
    }
}
