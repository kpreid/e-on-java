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

import org.erights.e.develop.assertion.T;
import org.erights.e.elib.base.Ejector;
import org.erights.e.elib.base.Script;
import org.erights.e.elib.base.SourceSpan;
import org.erights.e.elib.base.TypeDesc;
import org.erights.e.elib.debug.CallCounter;
import org.erights.e.elib.debug.EStackItem;
import org.erights.e.elib.debug.Profiler;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.prim.VTable;
import org.erights.e.elib.prim.VTableEntry;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.ConstMap;
import org.erights.e.elib.tables.FlexList;
import org.erights.e.elib.util.OneArgFunc;
import org.erights.e.elib.vat.Runner;
import org.erights.e.meta.java.math.EInt;

import java.io.IOException;


/**
 * Wraps a non-empty list of {@link EMatcher}s to adapt it to VTableEntry &
 * EStackItem
 *
 * @author Mark S. Miller
 */
public class EMatchersNode implements VTableEntry, EStackItem {

    private final String myTypeName;

    private final EMatcher[] myMatchers;

    private final CallCounter myCallCounter;

    private transient EMethodTable myOptScript = null;

    /**
     *
     */
    public EMatchersNode(String typeName, EMatcher[] matchers) {
        myTypeName = typeName;
        myMatchers = matchers;
        myCallCounter = Profiler.THE_ONE.register(toString(),
                                                  myMatchers[0].getOptSpan());
    }

    /**
     * Just returns this.
     */
    public Script shorten(Object optSelf, String aVerb, int arity) {
        return this;
    }

    /**
     * @param optShortSelf
     * @return
     */
    public boolean canHandleR(Object optShortSelf) {
        if (null == optShortSelf) {
            return false;
        }
        if (!(optShortSelf instanceof EImpl)) {
            return false;
        }
        EImpl self = (EImpl)optShortSelf;
        if (null == myOptScript) {
            return false;
        }
        return myOptScript == self.script();
    }

    /**
     * @param vTable
     * @return
     */
    public VTableEntry forVTable(VTable vTable) {
        if (null == myOptScript) {
            myOptScript = (EMethodTable)vTable;
            return this;
        } else {
            T.fail("EMatcher inhertance not implemented");
            return null; // make the compiler happy
        }
    }

    /**
     *
     */
    public Object execute(Object optSelf, String verb, Object[] args) {
        Runner.pushEStackItem(this);
        try {
            return execute(optSelf, verb, args, null);
        } catch (Throwable ex2) {
            throw myCallCounter.bumpBadCount(ex2, optSelf, verb, args);
        } finally {
            Runner.popEStackItem();
        }
    }

    /**
     *
     */
    public Object execute(Object optSelf,
                          String verb,
                          Object[] args,
                          OneArgFunc optEjector) {
        int last = myMatchers.length -1;
        for (int i = 0; i < last; i++) {
            Ejector ej = new Ejector("match-dispatch");
            try {
                Object result = myMatchers[i].execute(optSelf,
                                                      verb,
                                                      args,
                                                      ej);
                myCallCounter.bumpOkCount();
                return result;
            } catch (Throwable ex1) {
                ej.result(ex1);
            } finally {
                ej.disable();
            }
        }
        Object result = myMatchers[last].execute(optSelf,
                                                 verb,
                                                 args,
                                                 optEjector);
        myCallCounter.bumpOkCount();
        return result;
    }

    /**
     *
     */
    public void protocol(Object optSelf, FlexList mTypes) {
        if (null == optSelf) {
            return;
        }
        Ejector ej = new Ejector("protocol");
        try {
            Object optType = execute(optSelf,
                                     "__getAllegedType",
                                     E.NO_ARGS,
                                     ej);
            TypeDesc optTD = (TypeDesc)E.as(optType, TypeDesc.class);
            if (null == optTD) {
                return;
            }
            ConstMap msgTypes = optTD.getMessageTypes();
            Object[] rest = (Object[])msgTypes.getValues();
            mTypes.append(ConstList.fromArray(rest));
        } catch (Throwable ex) {
            ej.result(ex);
        } finally {
            ej.disable();
        }
    }

    /**
     *
     */
    public boolean respondsTo(Object optSelf, String verb, int arity) {
        if (null == optSelf) {
            return false;
        }
        Ejector ej = new Ejector("__respondsTo");
        try {
            Object[] args = {verb, EInt.valueOf(arity)};
            Object optResult = execute(optSelf,
                                       "__respondsTo",
                                       args,
                                       ej);
            if (null == optResult) {
                return false;
            } else {
                return E.asBoolean(optResult);
            }
        } catch (Throwable ex) {
            ej.result(ex);
            return false;
        } finally {
            ej.disable();
        }
    }

    /**
     *
     */
    public String toString() {
        String str = "#match(String, ConstList)";
        if (null == myTypeName) {
            return "<missing type name>" + str;
        } else {
            return myTypeName + str;
        }
    }

    /**
     *
     */
    public void traceOn(TextWriter out) throws IOException {
        out.print(toString());
    }

    /**
     * @return
     */
    public SourceSpan getOptSpan() {
        return myMatchers[0].getOptSpan();
    }
}
