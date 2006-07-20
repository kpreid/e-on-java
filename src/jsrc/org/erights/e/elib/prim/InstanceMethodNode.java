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

import org.erights.e.elib.util.AlreadyDefinedException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * How a java instance method is installed in a VTable
 *
 * @author Mark S. Miller
 */


class InstanceMethodNode extends JavaMemberNode {

    private transient Class myOptDirectClass = null;

    /**
     *
     */
    public InstanceMethodNode(Method method) {
        super(method);
        if (Modifier.isStatic(method.getModifiers())) {
            throw new Error("internal: not an instance method");
        }
    }

    /**
     *
     */
    static public void defineMembers(VTable vTable,
                                     Class clazz,
                                     SafeJ safeJ)
      throws AlreadyDefinedException {
        if (!Modifier.isPublic(clazz.getModifiers())) {
            return;
        }
        VarGetterNode.defineMembers(vTable, clazz, false, safeJ);
        Method[] jMeths = clazz.getDeclaredMethods();
        if (null != jMeths) {
            for (int i = 0, len = jMeths.length; i < len; i++) {
                Method jMeth = jMeths[i];
                int mods = jMeth.getModifiers();
                if (Modifier.isPublic(mods) && !Modifier.isStatic(mods)) {
                    if (JavaInstanceMatcher.isMatcher(jMeth)) {
                        JavaMatcher matcher = new JavaInstanceMatcher(jMeth);
                        vTable.setOptOtherwise(matcher);
                    } else {
                        MethodNode meth = new InstanceMethodNode(jMeth);
                        vTable.addMethod(meth, safeJ);
                    }
                }
            }
        }
    }

    /**
     * @return
     */
    public boolean canHandleR(Object optShortSelf) {
        if (null == optShortSelf) {
            return Object.class == myOptDirectClass;
        } else {
            return optShortSelf.getClass() == myOptDirectClass;
        }
    }

    /**
     * @return
     */
    public VTableEntry forVTable(VTable vTable) {
        if (vTable instanceof InstanceTable) {
            if (null == myOptDirectClass) {
                myOptDirectClass = ((InstanceTable)vTable).getDirectClass();
                return this;
            } else {
                VTableEntry result = new InstanceMethodNode((Method)myMember);
                return result.forVTable(vTable);
            }
        } else {
            return new InstanceMethodNode((Method)myMember);
        }
    }

    /**
     *
     */
    public Object innerExecute(Object self, Object[] args)
      throws IllegalAccessException, IllegalArgumentException,
      InvocationTargetException {

        return ((Method)myMember).invoke(self, args);
    }

    /**
     *
     */
    public Class receiverType() {
        return myMember.getDeclaringClass();
    }

    /**
     *
     */
    public String getDocComment() {
        return " Instance method";
    }

    /**
     *
     */
    public Class[] parameterTypes() {
        return ((Method)myMember).getParameterTypes();
    }

    /**
     *
     */
    public Class returnType() {
        return ((Method)myMember).getReturnType();
    }
}
