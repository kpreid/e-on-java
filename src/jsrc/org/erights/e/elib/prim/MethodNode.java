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

import org.erights.e.elib.base.MessageDesc;
import org.erights.e.elib.base.ParamDesc;
import org.erights.e.elib.slot.AnyGuard;
import org.erights.e.elib.slot.Guard;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.FlexList;
import org.erights.e.elib.tables.FlexMap;

/**
 * An element of a VTable
 *
 * @author Mark S. Miller
 * @see org.erights.e.elib.prim.VTable
 */
public abstract class MethodNode implements VTableEntry {

    /**
     *
     */
    protected MethodNode() {
    }

    /**
     *
     */
    public abstract String getDocComment();

    /**
     * Returns a message selector string (not necessarily interned).
     */
    public abstract String getVerb();

    /**
     * Returns null or an interned string containing the parameter type
     * signature in canonical "flat signature" form.
     * <p/>
     * Used to allow the invocation of Java methods that are overloaded by
     * type.
     */
    public abstract String getOptTypedVerb();

    /**
     * E Polymorphism is based on verb & arity alone.
     */
    public abstract int getArity();

    /**
     * If this MethodNode is or consists of JavaMemberNodes, add these
     * JavaMemberNodes to map indexed by their optTypedVerb (their flat
     * signature).
     */
    public abstract void addJavaMemberNodesToMap(FlexMap map);

    /**
     * Returns a description of the message this method responds to.
     * Should be overridden by subclasses that can be more informative
     */
    public MessageDesc makeMessageType(String verb) {
        Guard any = AnyGuard.THE_ONE;
        ParamDesc[] pType1 = {new ParamDesc(null, any)};
        ConstList pTypes = ConstList.fromArray(pType1).multiply(getArity());
        return new MessageDesc(getDocComment(),
                               verb,
                               pTypes,
                               any);
    }

    /**
     *
     */
    public void protocol(Object optSelf, FlexList mTypes) {
        mTypes.push(makeMessageType(getVerb()));
    }

    /**
     *
     */
    public boolean respondsTo(Object optSelf, String aVerb, int anArity) {
        if (anArity == getArity()) {
            if (aVerb.equals(getVerb())) {
                return true;
            }
            String otv = getOptTypedVerb();
            return otv != null && aVerb.equals(otv);
        }
        return false;
    }
}
