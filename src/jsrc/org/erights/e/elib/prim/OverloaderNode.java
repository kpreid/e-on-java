package org.erights.e.elib.prim;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.develop.assertion.T;
import org.erights.e.elib.base.Script;
import org.erights.e.elib.debug.CallCounter;
import org.erights.e.elib.debug.Profiler;
import org.erights.e.elib.tables.FlexMap;


/**
 * Wraps a set of JavaMemberNodes with the same message name and arity, and
 * dispatches to the unique one for which the current arguments coerce to its
 * parameter list. <p>
 * <p/>
 * If the current arguments coerce to none or more than one, throw an
 * exception.
 *
 * @author Mark S. Miller
 */
public class OverloaderNode extends MethodNode {

    private final JavaMemberNode[] myNodes;

    private final CallCounter myCallCounter;

    /**
     *
     */
    static public MethodNode make(JavaMemberNode[] nodes) {
        if (2 <= nodes.length) {
            return new OverloaderNode(nodes);
        } else if (1 == nodes.length) {
            return nodes[0];
        } else {
            T.fail("internal: must be at least one node");
            return null; //make compiler happy
        }
    }

    /**
     *
     */
    private OverloaderNode(JavaMemberNode[] nodes) {
        //XXX should check that they all have the same verb & arity
        myNodes = nodes;
        myCallCounter = Profiler.THE_ONE.register(this, null);
    }

    /**
     *
     */
    public String getDocComment() {
        return " Overloaded method";
    }

    /**
     *
     */
    public String getVerb() {
        return myNodes[0].getVerb();
    }

    /**
     *
     */
    public String getOptTypedVerb() {
        return null;
    }

    /**
     *
     */
    public int getArity() {
        return myNodes[0].getArity();
    }

    /**
     * Add all my nodes to the map
     */
    public void addJavaMemberNodesToMap(FlexMap map) {
        for (int i = 0; i < myNodes.length; i++) {
            map.put(myNodes[i].getOptTypedVerb(), myNodes[i]);
        }
    }

    /**
     * Returns the unique matching {@link JavaMemberNode} or throw an
     * exception.
     */
    public Script shorten(Object optSelf, String aVerb, int arity) {
        return this;

//        Object[] optNewArgs = null;
//        int index = -1;
//        for (int i = 0; i < myNodes.length; i++) {
//            Object[] optArgs = myNodes[i].optCoerceArgs(arity);
//            if (null == optArgs) {
//                //do nothing and continue
//            } else if (null == optNewArgs) {
//                optNewArgs = optArgs;
//                index = i;
//            } else {
//                myCallCounter.bumpBadCount();
//                throw new IllegalArgumentException
//                  ("Ambiguous overload" +
//                   (null == optSelf ? "" : " on " + optSelf.getClass()) +
//                   ": " +
//                   myNodes[index].getOptTypedVerb() + " vs " +
//                   myNodes[i].getOptTypedVerb());
//            }
//        }
//        if (null == optNewArgs) {
//            myCallCounter.bumpBadCount();
//            //XXX include more diagnostics
//            throw new IllegalArgumentException("Doesn't match any overload");
//        }
//        return myNodes[index];
    }

    /**
     * @return
     */
    public boolean canHandleR(Object optShortSelf) {
        return false; // XXX for now
    }

    /**
     * @return
     */
    public VTableEntry forVTable(VTable vTable) {
        return this; // XXX for now
    }

    /**
     *
     */
    public Object execute(Object optSelf, String verb, Object[] args) {
        //The following one line implementation:
        //    return shorten(self, verbxxx, args).execute(self, verbxxx, args);
        //would be correct, but redoes the coercion would that shorten does.
        //So instead we repeat the logic of shorten in order to use the coerced
        //optNetArgs array.

        Object[] optNewArgs = null;
        int index = -1;
        for (int i = 0; i < myNodes.length; i++) {
            Object[] optArgs = myNodes[i].optCoerceArgs(args);
            if (null == optArgs) {
                //do nothing and continue
            } else if (null == optNewArgs) {
                optNewArgs = optArgs;
                index = i;
            } else {
                IllegalArgumentException iae = new IllegalArgumentException(
                  "Ambiguous overload on " + optSelf.getClass() + ": " +
                    myNodes[index].getOptTypedVerb() + " vs " +
                    myNodes[i].getOptTypedVerb());
                throw myCallCounter.bumpBadCount(iae, optSelf, verb, args);
            }
        }
        if (null == optNewArgs) {
            //XXX include more diagnostics
            IllegalArgumentException iae =
              new IllegalArgumentException("Doesn't match any overload");
            throw myCallCounter.bumpBadCount(iae, optSelf, verb, args);
        }
        return myNodes[index].execute(optSelf, verb, optNewArgs);
    }

    /**
     *
     */
    public String toString() {
        return myNodes[0].toString() + "-overload";
    }
}
