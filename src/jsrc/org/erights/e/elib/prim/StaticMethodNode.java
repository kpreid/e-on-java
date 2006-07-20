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
 * How a java static is made to appear as an instance method of an
 * StaticMaker when seen thru E.call*() or E.send*().
 *
 * @author Mark S. Miller
 */

public class StaticMethodNode extends JavaMemberNode {

    static private final MethodNode TheTypeVerb = wrapMethod("asType");

    private transient StaticMaker myOptSelf = null;

    /**
     *
     */
    public StaticMethodNode(Method method) {
        super(method);
        if (!Modifier.isStatic(method.getModifiers())) {
            throw new Error("internal: not a static method");
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
        VarGetterNode.defineMembers(vTable, clazz, true, safeJ);
        Method[] jMeths = clazz.getDeclaredMethods();
        if (null != jMeths) {
            for (int i = 0; i < jMeths.length; i++) {
                Method jMeth = jMeths[i];
                int mods = jMeth.getModifiers();
                if (Modifier.isPublic(mods) && Modifier.isStatic(mods)) {
                    if (JavaStaticMatcher.isMatcher(jMeth)) {
                        JavaMatcher matcher = new JavaStaticMatcher(jMeth);
                        vTable.setOptOtherwise(matcher);
                    } else {
                        MethodNode meth = new StaticMethodNode(jMeth);
                        vTable.addMethod(meth, safeJ);
                    }
                }
            }
        }
        vTable.addMethod(TheTypeVerb, SafeJ.ALL);
    }

    /**
     * @return
     */
    public boolean canHandleR(Object optShortSelf) {
        if (null == optShortSelf) {
            return false;
        }
        return myOptSelf == optShortSelf;
    }

    /**
     * @return
     */
    public VTableEntry forVTable(VTable vTable) {
        if (vTable instanceof StaticTable) {
            if (null == myOptSelf) {
                myOptSelf = ((StaticTable)vTable).getSelf();
                return this;
            } else {
                VTableEntry result = new StaticMethodNode((Method)myMember);
                return result.forVTable(vTable);
            }
        } else {
            return new StaticMethodNode((Method)myMember);
        }
    }

    /**
     *
     */
    public Object innerExecute(Object self, Object[] args)
      throws IllegalAccessException, IllegalArgumentException,
      InvocationTargetException {

        return ((Method)myMember).invoke(null, args);
    }

    /**
     *
     */
    public Class receiverType() {
        return StaticMaker.class;
    }

    /**
     *
     */
    public String getDocComment() {
        return " Static method";
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

    /**
     * This indirect nonesense is needed because static init
     * expressions may not have Exceptions in need of declaration.
     */
    static private MethodNode wrapMethod(String verb) {
        Method meth;
        try {
            meth = StaticMaker.class.getDeclaredMethod(verb, new Class[0]);
        } catch (NoSuchMethodException ex) {
            throw new Error("where's StaticMaker." + verb + "()? " + ex);
        }
        return new InstanceMethodNode(meth);
    }
}
