package org.erights.e.elib.prim;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.elib.base.ClassDesc;
import org.erights.e.elib.base.Script;
import org.erights.e.elib.base.SourceSpan;
import org.erights.e.elib.base.TypeDesc;
import org.erights.e.elib.debug.CallCounter;
import org.erights.e.elib.debug.EStackItem;
import org.erights.e.elib.debug.Profiler;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.FlexList;
import org.erights.e.elib.vat.Runner;
import org.erights.e.meta.java.math.EInt;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Abstract superclass of all the CRAPI-based wrappers of
 * java.lang.reflect.Methods representing matchers for installing them in
 * VTables as matchers.
 *
 * @author Mark S. Miller
 */
public abstract class JavaMatcher implements VTableEntry, EStackItem {

    final Method myMethod;

    private final CallCounter myCallCounter;

    /**
     *
     */
    JavaMatcher(Method method) {
        myMethod = method;
        myCallCounter = Profiler.THE_ONE.register(this, null);
    }

    /**
     * Is this method a plausible matcher method?
     * <p/>
     * This isMatcher/2 only does part of the work, and should only be used
     * from the isMatcher/1 methods in the concrete subclasses of this class.
     *
     * @param method The method to be tested.
     * @param offset How many parameter positions should be skipped?
     * @return Whether method's name is "match" and the parameters after offset
     *         have the types String and ConstList.
     */
    static boolean isMatcher(Method method, int offset) {
        if (!method.getName().equals("match")) {
            return false;
        }
        Class[] paramTypes = method.getParameterTypes();
        return paramTypes.length == offset + 2 &&
          paramTypes[offset] == String.class &&
          paramTypes[offset + 1] == ConstList.class;
    }

    /**
     * Just returns this.
     */
    public Script shorten(Object optSelf, String aVerb, int arity) {
        return this;
    }

    /**
     * Do the part of {@link #execute} that's specific to a particular kind of
     * JavaMatcher.
     *
     * @param self The object whose matcher is being invoked.
     * @param pair A pair of a String and a ConstList, representing the verb
     *             and args of the original message, respectively.
     */
    abstract Object innerExecute(Object self, Object[] pair);

    /**
     *
     */
    public Object execute(Object optSelf, String verb, Object[] args) {
        Object result;
        Runner.pushEStackItem(this);
        Object[] pair = {verb, ConstList.fromArray(args)};
        try {
            result = innerExecute(optSelf, pair);
            myCallCounter.bumpOkCount();
            return result;
        } catch (Throwable problem) {
            throw myCallCounter.bumpBadCount(problem, optSelf, verb, args);
        } finally {
            Runner.popEStackItem();
        }
    }

    /**
     * @param optSelf
     * @param mTypes
     */
    public void protocol(Object optSelf, FlexList mTypes) {
        if (null == optSelf) {
            return;
        }
        TypeDesc oType =
          (TypeDesc)execute(optSelf, "__getAllegedType", E.NO_ARGS);
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
        Object result;
        Object[] args = {verb, EInt.valueOf(arity)};
        result = execute(optSelf, "__respondsTo", args);
        return ((Boolean)result).booleanValue();
    }

    /**
     *
     */
    abstract String explain();

    /**
     *
     */
    public void subPrintOn(TextWriter out, int priority) throws IOException {
        // It would be good to use the original Java parameter names here,
        // but we don't have access to them.
        out.print("match [verb, args] {");
        out.indent().lnPrint("java : \"" + explain() + "\"");
        out.lnPrint("}");
    }

    /**
     *
     */
    public String toString() {
        String result = ClassDesc.flatSig(myMethod.getDeclaringClass());
        if (Modifier.isStatic(myMethod.getModifiers())) {
            result = "static " + result;
        }
        result += "#match(String, ConstList)";
        return result;
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
        return null;
    }
}
