// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.elib.prim;

import org.erights.e.elib.base.Callable;
import org.erights.e.elib.base.Script;
import org.erights.e.elib.base.TypeDesc;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.FlexList;

/**
 * @author Mark S. Miller
 */
final class CallableScript implements Script {

    /**
     * The behavior of all Callables are described by the one CallableScript
     */
    static final Script THE_ONE = new CallableScript();

    /**
     *
     */
    private CallableScript() {
    }

    /**
     * Delegate's the shortening to optSelf's {@link Callable#optShorten}
     */
    public Script shorten(Object optSelf, String aVerb, int arity) {
        if (null == optSelf) {
            return this;
        } else {
            Script optResult = ((Callable)optSelf).optShorten(aVerb, arity);
            if (null == optResult) {
                return this;
            } else {
                return optResult;
            }
        }
    }

    /**
     * @return
     */
    public boolean canHandleR(Object optShortSelf) {
        return false;
    }

    /**
     * @return
     */
    public Object execute(Object optSelf, String verb, Object[] args) {
        return ((Callable)optSelf).callAll(verb, args);
    }

    /**
     *
     * @param optSelf
     * @param mTypes
     */
    public void protocol(Object optSelf, FlexList mTypes) {
        if (null == optSelf) {
            return;
        }
        TypeDesc oType = ((Callable)optSelf).getAllegedType();
        Object[] rest = (Object[])oType.getMessageTypes().getValues();
        mTypes.append(ConstList.fromArray(rest));
    }

    /**
     * @return
     */
    public boolean respondsTo(Object optSelf, String verb, int arity) {
        if (null == optSelf) {
            return false;
        }
        return ((Callable)optSelf).respondsTo(verb, arity);
    }
}
