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

import org.erights.e.elib.prim.E;
import org.erights.e.elib.tables.ConstMap;
import org.erights.e.elib.tables.FlexMap;

/**
 *
 */
public class StaticScope {

    //XXX should really use an identity map, and nouns (variable
    //names) should be interned.
    static private final ConstMap EmptyMap =
      FlexMap.fromTypes(String.class, Void.class).snapshot();

    static public final StaticScope EmptyScope =
      new StaticScope(EmptyMap, EmptyMap, false, EmptyMap, EmptyMap);
    static private final StaticScope META_SCOPE =
      new StaticScope(EmptyMap, EmptyMap, true, EmptyMap, EmptyMap);

    /**
     * Maps from each name to the internal NounExpr of its first use.
     */
    private final ConstMap myNamesRead;

    /**
     * Maps from each name to the internal NounExpr of its first use.
     */
    private final ConstMap myNamesSet;

    private final boolean myMetaStateExprFlag;

    /**
     * Maps from each name to the internal NounPattern of its definition.
     */
    private final ConstMap myDefNames;

    /**
     * Maps from each name to the internal NounPattern of its definition.
     */
    private final ConstMap myVarNames;

    /**
     * @param namesRead
     * @param namesSet
     * @param metaStateExprFlag
     * @param defNames
     * @param varNames
     */
    private StaticScope(ConstMap namesRead,
                        ConstMap namesSet,
                        boolean metaStateExprFlag,
                        ConstMap defNames,
                        ConstMap varNames) {
        myNamesRead = namesRead;
        myNamesSet = namesSet;
        myMetaStateExprFlag = metaStateExprFlag;
        myDefNames = defNames;
        myVarNames = varNames;
    }

    /**
     *
     */
    static private ConstMap of(AtomicExpr atom) {
        if (atom instanceof NounExpr) {
            String name = ((NounExpr)atom).getName();
            return ConstMap.EmptyMap.with(name, atom);
        } else {
            return ConstMap.EmptyMap;
        }
    }

    /**
     *
     */
    static public StaticScope scopeRead(AtomicExpr atom) {
        return new StaticScope(of(atom), EmptyMap, false, EmptyMap, EmptyMap);
    }

    /**
     *
     */
    static public StaticScope scopeAssign(AtomicExpr atom) {
        return new StaticScope(EmptyMap, of(atom), false, EmptyMap, EmptyMap);
    }

    /**
     *
     */
    static public StaticScope scopeMeta() {
        return META_SCOPE;
    }

    /**
     *
     */
    static public StaticScope scopeDef(AtomicExpr atom) {
        return new StaticScope(EmptyMap, EmptyMap, false, of(atom), EmptyMap);
    }

    /**
     *
     */
    static public StaticScope scopeSlot(AtomicExpr atom) {
        return new StaticScope(EmptyMap, EmptyMap, false, EmptyMap, of(atom));
    }

    /**
     * For processing normal expressions left to right, where all definitions
     * are exported, but uses are hidden by definitions to their left.
     */
    public StaticScope add(StaticScope right) {
        ConstMap rightNamesRead =
          right.namesRead().butNot(myDefNames).butNot(myVarNames);
        ConstMap rightNamesSet = right.namesSet().butNot(myVarNames);
        ConstMap badAssigns = rightNamesSet.and(myDefNames);
        if (1 <= badAssigns.size()) {
            // XXX We could report a bad assignment here if we could figure out
            // how to report it. As it is, these are reliably reported during
            // transformation anyway.
            // Since we don't (yet?) report it here, we instead:
            rightNamesSet = rightNamesSet.butNot(badAssigns);
        }

        return new StaticScope(myNamesRead.or(rightNamesRead),
                               myNamesSet.or(rightNamesSet),
                               myMetaStateExprFlag || right.hasMetaStateExpr(),
                               myDefNames.or(right.defNames()),
                               myVarNames.or(right.varNames()));
    }

    /**
     * For the <tt>||</tt> expression, in which both left and right consume
     * from their joint left, contribute to their joint right, and are
     * independent of each other.
     */
    public StaticScope both(StaticScope right) {
        return new StaticScope(myNamesRead.or(right.namesRead()),
                               myNamesSet.or(right.namesSet()),
                               myMetaStateExprFlag || right.hasMetaStateExpr(),
                               myDefNames.or(right.defNames()),
                               myVarNames.or(right.varNames()));
    }

    /**
     * Does 'meta.getState()' appear inside the expression?
     */
    public boolean hasMetaStateExpr() {
        return myMetaStateExprFlag;
    }

    /**
     * How a scope hidden within curly brackets appears to its context.
     */
    public StaticScope hide() {
        return new StaticScope(myNamesRead,
                               myNamesSet,
                               myMetaStateExprFlag,
                               EmptyMap,
                               EmptyMap);
    }

    /**
     * What are the names of variables read by this expression that refer to
     * variables defined outside this expression?
     * <p/>
     * Maps from each name to the internal NounExpr of its first use.
     */
    public ConstMap namesRead() {
        return myNamesRead;
    }

    /**
     * What are the names of variables assigned to by this expression that
     * refer to variables defined outside this expression?
     * <p/>
     * Maps from each name to the internal NounExpr of its first use.
     */
    public ConstMap namesSet() {
        return myNamesSet;
    }

    /**
     * What are the names of variables used by this expression that refer to
     * variables defined outside this expression?
     * <p/>
     * Union of namesRead() and namesSet().
     * <p/>
     * Maps from each name to the internal NounExpr of its first use.
     */
    public ConstMap namesUsed() {
        return myNamesRead.or(myNamesSet);
    }

    /**
     * What variables are directly defined (by a FinalPattern) in this
     * expression that are visible after this expression (i.e., to its right)?
     * <p/>
     * Maps from each name to the internal FinalPattern of its definition.
     */
    public ConstMap defNames() {
        return myDefNames;
    }

    /**
     * What variables are indirectly defined in this expression (by a
     * SlotPattern) that are visible after this expression (i.e., to its
     * right)?
     * <p/>
     * Maps from each name to the internal SlotPattern of its definition.
     */
    public ConstMap varNames() {
        return myVarNames;
    }

    /**
     * What variables are defined in this expression that are visible after
     * this expression (i.e., to its right)?
     * <p/>
     * Union of defNames() and varNames()
     * <p/>
     * Maps from each name to the internal NounPattern of its definition.
     */
    public ConstMap outNames() {
        return myDefNames.or(myVarNames);
    }

    /**
     *
     */
    public boolean commutesWith(StaticScope other) {
        return !outNames().intersects(other.namesUsed()) &&
          !namesUsed().intersects(other.outNames());
    }

    /**
     *
     */
    public String toString() {
        return "<" + E.toString(myNamesSet.getKeys()) + " := " +
          E.toString(myNamesRead.getKeys()) + " =~ " +
          E.toString(myDefNames.getKeys()) + " + var " +
          E.toString(myVarNames.getKeys()) +
          (myMetaStateExprFlag ? ", meta.getState()" : "") + ">";
    }
}
