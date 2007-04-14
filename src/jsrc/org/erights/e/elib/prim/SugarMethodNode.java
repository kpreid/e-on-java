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

import org.erights.e.elib.tables.FlexMap;
import org.erights.e.elib.util.AlreadyDefinedException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;


/**
 * How a static method of a sugar class is made to appear as an instance method
 * of an instance of the class being sugarred.
 *
 * @author Mark S. Miller
 */
public class SugarMethodNode extends JavaMemberNode {

    /**
     * XXX for now, just optimize the instance case
     */
    private transient Class myOptDirectClass = null;

    /**
     *
     */
    public SugarMethodNode(Method method) {
        super(method);
        if (!Modifier.isStatic(method.getModifiers())) {
            throw new Error("internal: not a static method");
        }
    }

    /**
     *
     */
    static public void defineMembers(VTable vTable, Class clazz)
      throws AlreadyDefinedException {
        Method[] jMeths = clazz.getMethods();
        for (int i = 0; i < jMeths.length; i++) {
            Method jMeth = jMeths[i];
            if (Modifier.isStatic(jMeth.getModifiers())) {
                if (JavaSugarMatcher.isMatcher(jMeth)) {
                    JavaMatcher matcher = new JavaSugarMatcher(jMeth);
                    vTable.setOptOtherwise(matcher);
                } else {
                    MethodNode meth = new SugarMethodNode(jMeth);
                    vTable.addMethod(meth, SafeJ.ALL);
                }
            }
        }
    }

    public boolean canHandleR(Object optShortSelf) {
        if (null == optShortSelf) {
            return Object.class == myOptDirectClass;
        } else {
            return optShortSelf.getClass() == myOptDirectClass;
        }
    }

    public VTableEntry forVTable(VTable vTable) {
        if (vTable instanceof InstanceTable) {
            if (null == myOptDirectClass) {
                myOptDirectClass = ((InstanceTable)vTable).getDirectClass();
                return this;
            } else {
                VTableEntry result = new SugarMethodNode((Method)myMember);
                return result.forVTable(vTable);
            }
        } else {
            return new SugarMethodNode((Method)myMember);
        }
    }

    /**
     *
     */
    public Object innerExecute(Object self, Object[] args) throws
      IllegalAccessException, IllegalArgumentException,
      InvocationTargetException {

        Method meth = (Method)myMember;
        Object[] newArgs = new Object[args.length + 1];
        newArgs[0] = self;
        System.arraycopy(args, 0, newArgs, 1, args.length);
        return meth.invoke(null, newArgs);
    }

    /**
     *
     */
    public Class receiverType() {
        return ((Method)myMember).getParameterTypes()[0];
    }

    /**
     *
     */
    public String getDocComment() {
        return " Sugar method";
    }

    /**
     * Always returns null, because sugar methods are designed for E and
     * so will not have overloading conflicts; furthermore, allowing them
     * would cause Miranda methods to be visible with typed forms, which
     * interferes with an object attempting to hide its Miranda behavior.
     */
    public String getOptTypedVerb() {
        return null;
    }

    /**
     *
     */
    public boolean isJavaTypedParameterNode() {
        return false;
    }

    /**
     *
     */
    public void addJavaMemberNodesToMap(FlexMap map) {
    }

    /**
     *
     */
    public Class[] parameterTypes() {
        Class[] types = ((Method)myMember).getParameterTypes();
        Class[] result = new Class[types.length - 1];
        System.arraycopy(types, 1, result, 0, result.length);
        return result;
    }

    /**
     *
     */
    public Class returnType() {
        return ((Method)myMember).getReturnType();
    }
}
