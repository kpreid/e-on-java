package org.erights.e.elang.interp;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.elang.evm.FinalPattern;
import org.erights.e.elang.evm.NounExpr;
import org.erights.e.elang.evm.NounPattern;
import org.erights.e.elang.evm.OuterNounExpr;
import org.erights.e.elang.scope.Scope;
import org.erights.e.elang.scope.ScopeLayout;
import org.erights.e.elib.slot.FinalSlot;
import org.erights.e.elib.slot.RuinedSlot;
import org.erights.e.elib.slot.Slot;
import org.erights.e.elib.tables.ConstSet;
import org.erights.e.elib.tables.FlexList;
import org.erights.e.elib.tables.FlexMap;
import org.erights.e.elib.tables.Twine;

/**
 * Used to make {@link org.erights.e.elang.scope.Scope Scope}s.
 * <p>
 * Note that you can use a ScopeMaker to make scopes containing unshadowable
 * names. This is how the safeScope itself is built.
 *
 * @author E. Dean Tribble
 */
class ScopeMaker {

    static private final int DEFAULT_SIZE = 50;

    /**
     * A list of slots to be accessed by indexing into the outer scope.
     * <p>
     * These are the ones defined by {@link #init(String, Object) init/2},
     * {@link #init(String, Object, String) init/3},
     * {@link #initSlot}, or
     * {@link #ruin}.
     */
    private final FlexList myOuters;

    /**
     * All the association are in
     * <pre>    varName =&gt; {@link NounPattern}</pre>
     * form.
     */
    private final FlexMap mySynEnv;

    /**
     *
     */
    public ScopeMaker() {
        this(FlexList.fromType(Slot.class, DEFAULT_SIZE),
             FlexMap.fromTypes(String.class, NounPattern.class, DEFAULT_SIZE));
    }

    /**
     *
     * @param outers
     * @param synEnv Must be a map in which each association is
     * <pre>    varName =&gt; {@link NounPattern}</pre>
     */
    private ScopeMaker(FlexList outers, FlexMap synEnv) {
        myOuters = outers;
        mySynEnv = synEnv;
    }

    /**
     *
     */
    public ScopeMaker copy() {
        return new ScopeMaker(myOuters.diverge(Slot.class),
                              mySynEnv.diverge(String.class,
                                               NounPattern.class));
    }

    /**
     *
     */
    public ConstSet getVarNames() {
        return mySynEnv.domain().snapshot();
    }

    /**
     *
     */
    public Scope make(String fqnPrefix) {
        ScopeLayout layout = ScopeLayout.make(myOuters.size(),
                                              mySynEnv.snapshot(),
                                              fqnPrefix);
        //int outerSpace = outerCount + ScopeSetup.OUTER_SPACE;
        Slot[] outers = (Slot[])myOuters.getArray(Slot.class);
        return Scope.outer(layout, outers);
    }

    /**
     * Generate a bindings for a noun that will be compiled into
     * transformed code.
     */
    public void comp(String name, Object value) {
        // XXX Optimization turned off until we reconcile with DeepPassByCopy
//        bind(name, new LiteralNounExpr(null, name, value, null));
        init(name, value);
    }

    /**
     * Generate a lazy bindings for a noun that will be compiled into
     * transformed code.
     */
    public void comp(String name, Object scope, String srcstr) {
        // XXX Optimization turned off until we reconcile with DeepPassByCopy
//        Slot slot = new LazyEvalSlot(scope, Twine.fromString(srcstr));
//        bind(name, new LiteralSlotNounExpr(null, name, slot, null));
        init(name, scope, srcstr);
    }

    /**
     *
     */
    public void init(String name, Object value) {
        Slot slot = new FinalSlot(value);
        initSlot(name, slot);
    }

    /**
     *
     */
    public void init(String name, Object scope, String srcstr) {
        Slot slot = new LazyEvalSlot(scope, Twine.fromString(srcstr));
        initSlot(name, slot);
    }

    /**
     *
     */
    public void ruin(String name, String complaint) {
        initSlot(name, new RuinedSlot(new RuntimeException(complaint)));
    }

    /**
     *
     */
    public void initSlot(String name, Slot slot) {
        int i = myOuters.size();
        myOuters.push(slot);
        bind(name, new OuterNounExpr(null, name, i, null));
    }

    /**
     *
     */
    private void bind(String varName, NounExpr nounExpr) {
        mySynEnv.put(varName,
                     new FinalPattern(null, nounExpr, null, true, null),
                     true);
    }
}
