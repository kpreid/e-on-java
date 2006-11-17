package org.erights.e.elang.scope;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.elang.evm.NounPattern;
import org.erights.e.elang.evm.ParseNode;
import org.erights.e.elib.base.ValueThunk;
import org.erights.e.elib.tables.ConstMap;
import org.erights.e.elib.tables.FlexSet;
import org.erights.e.meta.java.math.EInt;

/**
 * A single contour ScopeLayout.
 *
 * @author E. Dean Tribble
 * @author some mods by Mark S. Miller
 */
class ScopeLayoutBase extends ScopeLayout {

    /**
     *
     */
    private final ConstMap mySynEnv;

    private final String myFQNPrefix;

    /**
     * @param outerCount
     * @param synEnv     Must be a map in which each association is
     *                   <pre>    varName =&gt; {@link org.erights.e.elang.evm.NounPattern}</pre>
     * @param fqnPrefix
     */
    ScopeLayoutBase(int outerCount, ConstMap synEnv, String fqnPrefix) {
        super(outerCount);
        mySynEnv = synEnv;
        myFQNPrefix = fqnPrefix;
        ensureValidFQNPrefix(myFQNPrefix);
    }

    /**
     * Uses 'ScopeLayoutMaker.make(myOuterCount, mySynEnv, myFQNPrefix)'
     */
    public Object[] getSpreadUncall() {
        Object[] result = {ScopeLayoutMaker,
          "make",
          EInt.valueOf(myOuterCount),
          mySynEnv,
          myFQNPrefix};
        return result;
    }

    /**
     *
     */
    public ConstMap getSynEnv() {
        return mySynEnv;
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
        return new ScopeLayoutBase(myOuterCount, mySynEnv, fqnPrefix);
    }

    /**
     *
     */
    public NounPattern getOptPattern(String varName) {
        return (NounPattern)mySynEnv.fetch(varName, ValueThunk.NULL_THUNK);
    }

    /**
     *
     */
    boolean contains(String varName) {
        return mySynEnv.maps(varName);
    }

    /**
     *
     */
    void addNamesTo(FlexSet names) {
        String[] varNames = (String[])mySynEnv.getKeys(String.class);
        for (int i = 0; i < varNames.length; i++) {
            names.addElement(varNames[i]);
        }
    }

    /**
     *
     */
    public void requireShadowable(String varName, ParseNode optPoser) {
        if (contains(varName)) {
            ParseNode.fail(varName + " already in scope", optPoser);
        }
    }
}
