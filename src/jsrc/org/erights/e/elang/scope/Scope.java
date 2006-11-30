package org.erights.e.elang.scope;

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
import org.erights.e.elang.evm.FinalPattern;
import org.erights.e.elang.evm.NounExpr;
import org.erights.e.elang.evm.NounPattern;
import org.erights.e.elang.evm.OuterNounExpr;
import org.erights.e.elang.evm.SlotPattern;
import org.erights.e.elang.interp.LazyEvalSlot;
import org.erights.e.elang.interp.ScopeSetup;
import org.erights.e.elib.base.ClassDesc;
import org.erights.e.elib.base.Thunk;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.oldeio.UnQuote;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.slot.FinalSlot;
import org.erights.e.elib.slot.Guard;
import org.erights.e.elib.slot.Slot;
import org.erights.e.elib.tables.AssocFunc;
import org.erights.e.elib.tables.ConstMap;
import org.erights.e.elib.tables.EIteratable;
import org.erights.e.elib.tables.FlexList;
import org.erights.e.elib.tables.FlexMap;

import java.io.IOException;
import java.io.StringWriter;

/**
 * A ConstMap (sort of) from names (strings) to {@link Slot}s.
 * <p/>
 * Scopes inherit from each other in a tree, so they can be used to model
 * nesting lexical environments. The associations in the most leafward part of
 * a Scope are called "locals". <p>
 *
 * @author E. Dean Tribble
 * @author Mark S. Miller
 */
public class Scope implements EIteratable {

    static private final Guard SlotGuard = ClassDesc.make(Slot.class);

    private final ScopeLayout myScopeLayout;

    private final EvalContext myEvalContext;

    /**
     * In slotted form, each association is
     * <pre>    "&amp;"varName =&gt; Slot</pre>
     * For those associations in state of the form
     * <pre>       varName =&gt; value</pre>
     * asSlottedState(state) will convert them by wrapping the value in a
     * FinalSlot.
     */
    static public ConstMap asSlottedState(ConstMap state) {
        final FlexMap slottedMap =
          FlexMap.fromTypes(String.class, Slot.class, state.size());
        state.iterate(new AssocFunc() {
            public void run(Object key, Object value) {
                String k = (String)key;
                if (k.startsWith("&")) {
                    Slot slot = (Slot)E.as(value, Slot.class);
                    slottedMap.put(k, slot, true);
                } else {
                    slottedMap.put("&" + k, new FinalSlot(value));
                }
            }
        });
        return slottedMap.snapshot();
    }

    /**
     * In mixed form, each association is either
     * <pre>    "&amp;"varName =&gt; Slot</pre> or
     * <pre>       varName =&gt; value</pre>
     * asMixedState(state) always prefers the second form when the slot {@link
     * Slot#isFinal isFinal}.
     */
    static public ConstMap asMixedState(ConstMap state) {
        final FlexMap mixedMap =
          FlexMap.fromTypes(String.class, Object.class, state.size());
        state.iterate(new AssocFunc() {
            public void run(Object key, Object value) {
                String k = (String)key;
                if (k.startsWith("&")) {
                    Slot slot = (Slot)E.as(value, Slot.class);
                    if (slot.isFinal()) {
                        mixedMap.put(k.substring(1), slot.get(), true);
                    } else {
                        mixedMap.put(k, slot, true);
                    }
                } else {
                    mixedMap.put(k, value, true);
                }
            }
        });
        return mixedMap.snapshot();
    }

    /**
     *
     */
    static public Scope fromState(ConstMap state, String fqnPrefix) {
        //implementation should be a specialized form of ScopeMaker
        int len = state.size();
        final FlexList outersList = FlexList.fromType(Slot.class, len);
        final FlexMap synEnv =
          FlexMap.fromTypes(String.class, NounPattern.class, len);
        state.iterate(new AssocFunc() {
            public void run(Object key, Object value) {
                String name = (String)key;
                String varName;
                Slot slot;
                if (name.startsWith("&")) {
                    varName = name.substring(1);
                    slot = (Slot)SlotGuard.coerce(value, null);
                } else {
                    varName = name;
                    slot = new FinalSlot(value);
                }
                int i = outersList.size();
                outersList.push(slot);
                NounExpr nounExpr = new OuterNounExpr(null, varName, i, null);
                NounPattern patt;
                if (name.startsWith("&")) {
                    patt = new SlotPattern(null, nounExpr, null, true, null);
                } else {
                    patt = new FinalPattern(null, nounExpr, null, true, null);
                }
                synEnv.put(varName, patt, true);
            }
        });
        ScopeLayout layout =
          ScopeLayout.make(len, synEnv.snapshot(), fqnPrefix);
        //int outerSpace = outerCount + ScopeSetup.OUTER_SPACE;
        Slot[] outers = (Slot[])outersList.getArray(Slot.class);
        return outer(layout, outers);
    }

    /**
     *
     */
    static public Scope outer(ScopeLayout scopeLayout, Slot[] outers) {
        int outerCount = scopeLayout.getOuterCount();
        Slot[] newOuters;
        if (outerCount == outers.length) {
            newOuters = outers;
        } else {
//            int len = myOuters.length;
//            if (index >= len) {
//                int newLen = StrictMath.max(len + 32, index + 1);
//                Slot[] newOuters = new Slot[newLen];
//                System.arraycopy(myOuters, 0, newOuters, 0, len);
//                myOuters = newOuters;
//            }
//            myOuters[index] = value;

            newOuters = new Slot[outerCount];
            int newLen = StrictMath.min(outerCount, outers.length);
            System.arraycopy(outers, 0, newOuters, 0, newLen);
        }
        return new Scope(scopeLayout, EvalContext.make(0, newOuters));
    }

    /**
     * @param scopeLayout
     * @param evalContext
     */
    public Scope(ScopeLayout scopeLayout, EvalContext evalContext) {
        myScopeLayout = scopeLayout;
        myEvalContext = evalContext;
    }

    /**
     * Returns a new Scope like the current one, but with the new ScopeLayout,
     * which must be an "extension" of the current one.
     * <p/>
     * XXX Security Alert: It is assumed, rather than enforced, that the new
     * ScopeLayout is an extension of the current one.
     *
     * @param newScopeLayout
     */
    public Scope update(ScopeLayout newScopeLayout) {
        int outerCount = newScopeLayout.getOuterCount();
        Slot[] outers = myEvalContext.outers();
        if (-1 == outerCount || outers.length == outerCount) {
            EvalContext newEvalContext = myEvalContext;
            return new Scope(newScopeLayout, newEvalContext);
        } else {
            // XXX need to refactor
            return outer(newScopeLayout, outers);
        }
    }

    /**
     *
     */
    public Scope nestOuter() {
        return update(myScopeLayout.nestOuter());
    }

    /**
     *
     */
    public ScopeLayout getScopeLayout() {
        return myScopeLayout;
    }

    /**
     *
     */
    public String getFQNPrefix() {
        return myScopeLayout.getFQNPrefix();
    }

    /**
     *
     */
    public EvalContext newContext(int numLocals) {
        // XXX should this eliminate all the current locals?
        return myEvalContext.extended(numLocals);
    }

    /**
     *
     */
    public boolean maps(String varName) {
        return myScopeLayout.contains(varName);
    }

    /**
     *
     */
    public Slot getSlot(String varName) {
        return myScopeLayout.getNoun(varName).getSlot(myEvalContext);
    }

    /**
     * Gets the value of the slot associated with varName.
     * <p/>
     * Just a convenience implemented out of getSlot/1
     */
    public Object get(String varName) {
        return getSlot(varName).get();
    }

    /**
     * Like {@link #get(String) get/1}, except that if varName isn't found,
     * return insteadThunk() instead.
     */
    public Object fetch(String varName, Thunk insteadThunk) {
        if (maps(varName)) {
            return get(varName);
        } else {
            return insteadThunk.run();
        }
    }

    /**
     * The default put/2 is defined in the obvious fashion in terms of
     * getSlot(varName).put(newValue). <p>
     */
    public void put(String varName, Object newValue) {
        getSlot(varName).put(newValue);
    }

    /**
     * A new Scope object just like this one, but with the given prefix.
     */
    public Scope withPrefix(String fqnPrefix) {
        return new Scope(myScopeLayout.withPrefix(fqnPrefix), myEvalContext);
    }

    /**
     * Enumerates <tt>slotName =&gt; Slot</tt> associations, where a
     * <tt>slotName</tt> is <tt>"&amp;"varName</tt>
     */
    public void iterate(final AssocFunc func) {
        myScopeLayout.getVarNameSet().iterate(new AssocFunc() {
            public void run(Object i, Object name) {
                String varName = (String)name;
                func.run("&" + varName, getSlot(varName));
            }
        });
    }

    /**
     *
     */
    public ConstMap getState() {
        return ConstMap.fromIteratable(this, true);
    }

    /**
     * universalFlag defaults to true
     */
    public UnQuote bindings() throws IOException {
        return bindings(true);
    }

    /**
     * Returns a string showing the bindings in this scope in a pleasant, human
     * readable format.
     *
     * @param showSafeFlag If set, then unshadowable names from the safe scope
     *                     will be shown as well.
     */
    public UnQuote bindings(boolean showSafeFlag) throws IOException {
        StringWriter buf = new StringWriter();
        TextWriter out = new TextWriter(buf);
        printBindingsOn(showSafeFlag, out);
        return new UnQuote(StringHelper.canonical(buf.toString()));
    }

    /**
     *
     */
    private void printBindingsOn(final boolean showSafeFlag,
                                 final TextWriter out) {
        iterate(new AssocFunc() {
            public void run(Object key, Object value) {
                try {
                    String slotName = (String)key;
                    T.test(slotName.startsWith("&"));
                    String varName = slotName.substring(1);
                    if (!showSafeFlag &&
                      ScopeSetup.NonShadowable.contains(varName)) {

                        return; // really means break()
                    }
                    Slot slot = (Slot)value;
                    // we avoid another indented stream only by assuming only
                    // one line follows
                    out.print(varName, "\n    ");
                    try {
                        if (slot instanceof LazyEvalSlot) {
                            // kludgy special case to avoid forcing the
                            // LazyEvalSlot to evaluate.
                            out.print("...");
                        } else {
                            Object optValue = get(varName);
                            if (null == optValue) {
                                out.print("null");
                            } else {
                                String sig =
                                  ClassDesc.simpleSig(optValue.getClass());
                                out.print(StringHelper.aan(sig));
                            }
                        }
                    } catch (Exception ex) {
                        out.print("*** ", ex);
                    }
                    out.println();
                } catch (IOException e) {
                    throw ExceptionMgr.asSafe(e);
                }
            }
        });
    }
}
