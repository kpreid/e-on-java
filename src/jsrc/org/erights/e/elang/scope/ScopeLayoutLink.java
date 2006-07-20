package org.erights.e.elang.scope;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.elang.evm.NounPattern;
import org.erights.e.elang.evm.ParseNode;
import org.erights.e.elib.tables.FlexSet;

/**
 * A ScopeLayout having at least one varName =&gt; {@link NounPattern}
 * association in the innermost contour.
 * <p>
 * Built from a single association and a previous ScopeLayout.
 *
 * @author E. Dean Tribble
 * @author some mods by Mark S. Miller
 */
class ScopeLayoutLink extends ScopeLayout {

    private final ScopeLayout myNext;

    private final String myVarName;

    private final NounPattern myNamer;

    /**
     *
     */
    ScopeLayoutLink(int outerCount,
                    ScopeLayout next,
                    String varName,
                    NounPattern namer) {
        super(outerCount);
        myNext = next;
        myVarName = varName;
        myNamer = namer;
    }

    /**
     * Uses 'myNext.with(myVarName, myNamer, null)'
     */
    public Object[] getSpreadUncall() {
        Object[] result = { myNext, "with", myVarName, myNamer, null };
        return result;
    }

    /**
     *
     */
    public ScopeLayout withPrefix(String fqnPrefix) {
        return new ScopeLayoutLink(myOuterCount,
                                   myNext.withPrefix(fqnPrefix),
                                   myVarName,
                                   myNamer);
    }

    /**
     *
     */
    public NounPattern getOptPattern(String varName) {
        if (myVarName.equals(varName)) {
            return myNamer;
        } else {
            return myNext.getOptPattern(varName);
        }
    }

    /**
     *
     */
    boolean contains(String varName) {
        return myVarName.equals(varName) || myNext.contains(varName);
    }

    /**
     *
     */
    void addNamesTo(FlexSet names) {
        myNext.addNamesTo(names);
        names.addElement(myVarName);
    }

    /**
     *
     */
    public void requireShadowable(String varName, ParseNode optPoser) {
        if (myVarName.equals(varName)) {
            ParseNode.fail(varName + " already in scope", optPoser);
        }
        myNext.requireShadowable(varName, optPoser);
    }

    /**
     * Just passes the buck.
     */
    public String getFQNPrefix() {
        return myNext.getFQNPrefix();
    }
}
