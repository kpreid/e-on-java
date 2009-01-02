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

import org.erights.e.elib.base.ClassDesc;
import org.erights.e.elib.base.Ejector;
import org.erights.e.elib.base.MessageDesc;
import org.erights.e.elib.base.ParamDesc;
import org.erights.e.elib.base.Script;
import org.erights.e.elib.base.SourceSpan;
import org.erights.e.elib.debug.CallCounter;
import org.erights.e.elib.debug.EStackItem;
import org.erights.e.elib.debug.Profiler;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.FlexMap;
import org.erights.e.elib.util.OneArgFunc;
import org.erights.e.elib.vat.Runner;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;


/**
 * Abstract superclass of all the CRAPI-based wrappers of
 * java.lang.reflect.Members for installing them in VTables as MethodNodes.
 *
 * @author Mark S. Miller
 */
public abstract class JavaMemberNode extends MethodNode implements EStackItem {

    final Member myMember;

    private final CallCounter myCallCounter;

    /**
     *
     */
    JavaMemberNode(Member member) {
        myMember = member;
        myCallCounter = Profiler.THE_ONE.register(this, null);
    }

    /**
     *
     */
    public MessageDesc makeMessageType(String verb) {
        Class[] paramTypes = parameterTypes();
        ParamDesc[] paramDescs = new ParamDesc[paramTypes.length];
        for (int i = 0; i < paramTypes.length; i++) {
            paramDescs[i] = new ParamDesc(null, paramTypes[i]);
        }
        return new MessageDesc(getDocComment(),
                               verb,
                               ConstList.fromArray(paramDescs),
                               returnType());
    }

    /**
     *
     */
    public String getVerb() {
        //Note: the result isn't necessarily interned, but that's ok.
        String result = myMember.getName();
        if (result.endsWith("__drop")) {
            result = result.substring(0, result.length() - "__drop".length());
        }
        return result;
    }

    /**
     *
     */
    public String getOptTypedVerb() {
        StringBuffer result = new StringBuffer();
        result.append(getVerb()).append('(');
        Class[] types = parameterTypes();
        for (int i = 0; i < types.length; i++) {
            if (1 <= i) {
                result.append(", ");
            }
            result.append(ClassDesc.flatSig(types[i]));
        }
        result.append(')');
        return result.toString().intern();
    }

    /**
     *
     */
    public int getArity() {
        return parameterTypes().length;
    }

    /**
     *
     */
    public boolean isJavaTypedParameterNode() {
        return true;
    }

    /**
     * Add this JavaMemberNode to the map
     */
    public void addJavaMemberNodesToMap(FlexMap map) {
        map.put(getOptTypedVerb(), this);
    }

    /**
     *
     */
    Object[] optCoerceArgs(Object[] args) {
        Ejector ej = new Ejector("optCoerceArgs");
        try {
            return coerceArgs(args, ej);
        } catch (Throwable t) {
            ej.result(t);
            return null;
        } finally {
            ej.disable();
        }
    }

    /**
     *
     */
    private Object[] coerceArgs(Object[] args, OneArgFunc optEjector) {
        Class[] paramTypes = parameterTypes();
        Object[] result = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            result[i] = E.as(args[i], paramTypes[i], optEjector);
        }
        return result;
    }

    /**
     * Just returns this.
     */
    public Script shorten(Object optSelf, String aVerb, int arity) {
        return this;
    }

    public boolean canHandleR(Object optShortSelf) {
        return false; // XXX for now
    }

    /**
     * @throws IllegalArgumentException if the number of actual and formal
     *                                  parameters differ, or if an unwrapping
     *                                  or method invocation conversion fails.
     */
    abstract Object innerExecute(Object self, Object[] args) throws
      InstantiationException, IllegalAccessException, IllegalArgumentException,
      InvocationTargetException;

    /**
     *
     */
    public Object execute(Object optSelf, String verb, Object[] args) {
        try {
            Object result;
            // TODO the typical case probably requires coercion (esp. ints)
            //make the typical case faster: first try invoking without
            //coercion. If we get into trouble, then try coercing the
            //arguments.
            Runner.pushEStackItem(this);
            try {
                result = innerExecute(optSelf, args);
            } catch (IllegalArgumentException ex) {
                Object newSelf;
                Object[] newArgs;
                newSelf = E.as(optSelf, receiverType());
                newArgs = coerceArgs(args, null);
                try {
                    result = innerExecute(newSelf, newArgs);
                } catch (IllegalArgumentException ex2) {
                    //XXX eventually, better diagnostics
                    throw ex2;
                }
            } finally {
                Runner.popEStackItem();
            }
            myCallCounter.bumpOkCount();
            return result;
        } catch (Throwable problem) {
            throw myCallCounter.bumpBadCount(problem, optSelf, verb, args);
        }
    }

    /**
     *
     */
    protected Member member() {
        return myMember;
    }

    /**
     *
     */
    public abstract Class[] parameterTypes();

    /**
     *
     */
    public abstract Class returnType();

    /**
     *
     */
    protected abstract Class receiverType();

    /**
     *
     */
    public String toString() {
        String result = ClassDesc.flatSig(myMember.getDeclaringClass());
        if (Modifier.isStatic(myMember.getModifiers())) {
            result = "static " + result;
        }
        result += "#" + getOptTypedVerb();
        return result;
    }

    /**
     *
     */
    public void traceOn(TextWriter out) throws IOException {
        out.print(toString());
    }

    public SourceSpan getOptSpan() {
        return null;
    }

    static public String asGetterVerb(String propName) {
        return "get" + Character.toUpperCase(propName.charAt(0)) +
          propName.substring(1);
    }

    static public String asSetterVerb(String propName) {
        return "set" + Character.toUpperCase(propName.charAt(0)) +
          propName.substring(1);
    }
}
