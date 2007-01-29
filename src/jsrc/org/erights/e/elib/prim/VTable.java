package org.erights.e.elib.prim;

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

import org.erights.e.develop.assertion.T;
import org.erights.e.develop.exception.ExceptionMgr;
import org.erights.e.develop.format.StringHelper;
import org.erights.e.elib.base.ClassDesc;
import org.erights.e.elib.base.Script;
import org.erights.e.elib.base.ValueThunk;
import org.erights.e.elib.tables.ConstMap;
import org.erights.e.elib.tables.FlexList;
import org.erights.e.elib.tables.FlexMap;
import org.erights.e.elib.util.AlreadyDefinedException;


/**
 * E's mechanism for method dispatch
 *
 * @author Mark S. Miller
 */
public abstract class VTable implements Script {

    /**
     * The fully qualified behavior name, sort of. XXX explain
     */
    private final String myFQName;

    /**
     * Maps from intered verb strings to either <p> a MethodNode, for verbs
     * that are only defined for one arity, or <p> An array of MethodNodes,
     * with a MethodNode for every occupied arity and a null for the rest.
     */
    private final FlexMap myMethods;

    /**
     * The script to execute if none of the methods match
     */
    private VTableEntry myOptOtherwise;

    /**
     * @param fqName The fully qualified behavior name.
     */
    protected VTable(String fqName) {
        myFQName = fqName;
        myMethods = FlexMap.interning(Object.class);
        myOptOtherwise = null;
    }

    /**
     * 'methods' must be a map from mverbs (mangled verbs) to MethodNodes, as
     * would be returned by 'methods()'.
     * <p/>
     * 'safeJ' indicates which typedVerbs to include.
     */
    public void addMethods(ConstMap methods, SafeJ safeJ) {
        String[] mverbs = (String[])methods.getKeys(String.class);
        for (int i = 0; i < mverbs.length; i++) {
            String mverb = mverbs[i];
            MethodNode meth = (MethodNode)methods.get(mverb);
            addMethod(mverb, meth, safeJ);
        }
    }

    /**
     * Throw the method into the pile, if appropriate, and in an order
     * independent way.
     * <p/>
     * 'verb' may actually be a verb or an mverb (mangled verb). If it's a
     * mangled verb, this checks that the arities agree. If the optTypedVerb of
     * the method is accepted by safeJ, then the method is added under the
     * provided verb. If this verb/arity conflicts with one already in the map,
     * then the conflict is resolved according to {@link #resolveConflict}.
     */
    private boolean addMethod(String verb, MethodNode newMeth, SafeJ safeJ)
      throws AlreadyDefinedException {

        String optTypedVerb = newMeth.getOptTypedVerb();

        if (null != optTypedVerb && !safeJ.shouldAllow(optTypedVerb)) {
            return false;
        }
        int newArity = newMeth.getArity();

        int slash = verb.lastIndexOf('/');
        if (-1 != slash) {
            String arityStr = verb.substring(slash + 1);
            int marity = Integer.parseInt(arityStr);
            if (marity != newArity) {
                T.fail("arity mismatch");
            }
            verb = verb.substring(0, slash);
        }
        verb = verb.intern();
        newMeth = (MethodNode)newMeth.forVTable(this);

        Object m = myMethods.fetch(verb, ValueThunk.NULL_THUNK);
        if (m == null) {
            //only verb so far with this arity.
            myMethods.put(verb, newMeth, true);

        } else if (m instanceof MethodNode) {
            //newMeth is second arrival with this verb.
            MethodNode oldMeth = (MethodNode)m;
            int oldArity = oldMeth.getArity();
            if (oldArity == newArity) {
                //they have same arity, conflicts resolve to a single method
                myMethods.put(verb, resolveConflict(oldMeth, newMeth));
            } else {
                //different arities, need array
                int maxArity = StrictMath.max(oldArity, newArity);
                MethodNode[] arityTable = new MethodNode[maxArity + 1];
                arityTable[oldArity] = oldMeth;
                arityTable[newArity] = newMeth;
                myMethods.put(verb, arityTable);
            }
        } else {
            //existing methods already indexed by arity
            MethodNode[] arityTable = (MethodNode[])m;
            if (arityTable.length <= newArity) {
                //can't fit the new arity, so grow it
                MethodNode[] newTable = new MethodNode[newArity + 1];
                System.arraycopy(arityTable,
                                 0,
                                 newTable,
                                 0,
                                 arityTable.length);
                arityTable = newTable;
                myMethods.put(verb, arityTable);
            }
            MethodNode oldMeth = arityTable[newArity];
            if (oldMeth == null) {
                //new arrival is first with this arity.
                arityTable[newArity] = newMeth;
            } else {
                //the new arrival conflicts. Store back the resolution
                arityTable[newArity] = resolveConflict(oldMeth, newMeth);
            }
        }
        return true;
    }

    /**
     * Throw the method into the pile, if appropriate, and in an order
     * independent way.
     *
     * @return whether the addition was allowed.
     */
    public boolean addMethod(MethodNode newMeth, SafeJ safeJ)
      throws AlreadyDefinedException {
        boolean result = addMethod(newMeth.getVerb(), newMeth, safeJ);
        String optTypedVerb = newMeth.getOptTypedVerb();
        if (optTypedVerb != null) {
            result |= addMethod(optTypedVerb, newMeth, safeJ);
        }
        return result;
    }

    /**
     * Returns a matching method or matcher, or throws an exception.
     */
    public Script shorten(Object optSelf, String aVerb, int arity) {
        Script optMethod = optMethod(aVerb, arity);
        if (null != optMethod) {
            return optMethod;
        }
        if (null != myOptOtherwise) {
            return myOptOtherwise;
        }
        String selfStr;
        if (null == optSelf) {
            selfStr = "<null>.";
        } else {
            String typeName;
            typeName = ClassDesc.simpleName(myFQName);
            //selfStr = "<" + typeName + ":" + self + ">.";
            selfStr = "<" + StringHelper.aan(typeName) + ">.";
        }
        String diagnostic = selfStr + aVerb + "/" + arity;
        NoSuchMethodException nsme = new NoSuchMethodException(diagnostic);
        throw ExceptionMgr.asSafe(nsme);
    }

    /**
     *
     */
    public boolean canHandleR(Object optShortSelf) {
        return false;
    }

    /**
     *
     */
    public Object execute(Object optSelf, String verb, Object[] args) {
        Script script = shorten(optSelf, verb, args.length);
        return script.execute(optSelf, verb, args);
    }

    /**
     * Returns a table mapping mverbs (mangled verbs) to MethodNodes.
     * <p/>
     * A mangled verb is 'verb + "/" + arity'
     */
    public ConstMap methods() {
        FlexMap flex = FlexMap.fromTypes(String.class, MethodNode.class);
        String[] verbs = (String[])myMethods.getKeys(String.class);
        for (int j = 0; j < verbs.length; j++) {
            String verb = verbs[j];
            Object m = myMethods.get(verb);
            if (m instanceof MethodNode) {
                MethodNode meth = (MethodNode)m;
                flex.put(verb + "/" + meth.getArity(), meth);
            } else {
                MethodNode[] arityTable = (MethodNode[])m;
                for (int i = 0; i < arityTable.length; i++) {
                    MethodNode meth = arityTable[i];
                    if (meth != null) {
                        flex.put(verb + "/" + i, meth);
                    }
                }
            }
        }
        return flex.snapshot();
    }

    /**
     * Like optMethod/2, but uses an mverb (mangled verb) rather than a
     * separate verb and arity.
     */
    public MethodNode optMethod(String mverb) {
        int slash = mverb.lastIndexOf('/');
        if (-1 == slash) {
            T.fail("missing slash");
        }
        String verb = mverb.substring(0, slash);
        String arityStr = mverb.substring(slash + 1);
        int arity = Integer.parseInt(arityStr);
        return optMethod(verb, arity);
    }

    /**
     * If there's a method in the vTable for this verb and arity, return it.
     * Else return null.
     */
    public MethodNode optMethod(String verb, int arity) {
        MethodNode result;

        //can't assume that verb is interned, but can assume that it's
        //typically interned. Since interning is expensive, first try
        //looking up up without interning it.
        Object m = myMethods.fetch(verb, ValueThunk.NULL_THUNK);
        if (m == null) {
            verb = verb.intern();
            m = myMethods.fetch(verb, ValueThunk.NULL_THUNK);
            if (m == null) {
                return null;
            }
        }
        if (m instanceof MethodNode) {
            result = (MethodNode)m;
        } else {
            //if it's not a MethodNode, it'd better be an array of MethodNodes
            MethodNode[] arityTable = (MethodNode[])m;
            if (arity < arityTable.length) {
                return arityTable[arity];
            } else {
                return null;
            }
        }
        if (arity == result.getArity()) {
            return result;
        } else {
            return null;
        }
    }

    /**
     * Fully qualified behavior name.
     */
    public String getFQName() {
        return myFQName;
    }

    /**
     *
     */
    public VTableEntry getOptOtherwise() {
        return myOptOtherwise;
    }

    /**
     * Overwrites the property.
     * <p/>
     * As with the addMethod methods, this should only be used when
     * initializing a vTable.
     */
    public void setOptOtherwise(VTableEntry optOtherwise) {
        if (null == optOtherwise) {
            myOptOtherwise = null;
        } else {
            myOptOtherwise = optOtherwise.forVTable(this);
        }
    }

    /**
     * XXX cheesy indeed, as it doesn't distinguish inheritance vs override vs
     * overload
     */
    private MethodNode resolveConflict(MethodNode a, MethodNode b) {
        if (b instanceof JavaMemberNode || b instanceof OverloaderNode) {
            FlexMap map = FlexMap.interning(JavaMemberNode.class);
            a.addJavaMemberNodesToMap(map);
            b.addJavaMemberNodesToMap(map);
            MethodNode overloader =
              OverloaderNode.make((JavaMemberNode[])map.getValues());
            return (MethodNode)overloader.forVTable(this);
        } else {
            return b;
        }
    }

    /**
     *
     */
    public void protocol(Object optSelf, FlexList mTypes) {
        //do otherwise first so methods can override it
        if (myOptOtherwise != null) {
            myOptOtherwise.protocol(optSelf, mTypes);
        }
        ConstMap meths = methods();
        String[] mverbs = (String[])meths.getKeys(String.class);
        for (int i = 0; i < mverbs.length; i++) {
            String mverb = mverbs[i];
            MethodNode meth = (MethodNode)meths.get(mverb);

            //doesn't do the following line...
            //meth.protocol(self, mTypes);
            //... because this loses the "which verb" info. Instead...

            mTypes.push(meth.makeMessageType(mverb));
        }
    }

    /**
     *
     */
    public boolean respondsTo(Object optSelf, String verb, int arity) {
        MethodNode meth = optMethod(verb, arity);
        if (meth != null) {
            return true;
        } else if (myOptOtherwise == null) {
            return false;
        } else {
            return myOptOtherwise.respondsTo(optSelf, verb, arity);
        }
    }

    /**
     *
     */
    public String toString() {
        return "<vTable " + myFQName + ">";
    }
}
