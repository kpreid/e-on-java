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

import org.erights.e.elib.tables.FlexList;
import org.erights.e.elib.util.AlreadyDefinedException;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * A java public instance variable becomes an E zero-arg method which
 * reads its value
 *
 * @author Mark S. Miller
 */
class VarGetterNode extends JavaMemberNode {

    static private final Class[] NO_CLASSES = {};

    /**
     *
     */
    private final String myVerb;

    /**
     *
     */
    public VarGetterNode(Field field, boolean propFlag) {
        super(field);
        String propName = super.getVerb();
        if (propFlag) {
            myVerb = asGetterVerb(propName);
        } else {
            myVerb = propName;
        }
    }

    /**
     *
     */
    public String getVerb() {
        return myVerb;
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
    public Object innerExecute(Object self, Object[] args)
      throws IllegalArgumentException, IllegalAccessException {

        return ((Field)myMember).get(self);
    }

    /**
     *
     */
    public Class receiverType() {
        if (Modifier.isStatic(myMember.getModifiers())) {
            return StaticMaker.class;
        } else {
            return myMember.getDeclaringClass();
        }
    }

    /**
     *
     */
    public String getDocComment() {
        return " Gets the variable's value";
    }

    /**
     *
     */
    public Class[] parameterTypes() {
        return NO_CLASSES;
    }

    /**
     *
     */
    public Class returnType() {
        return ((Field)myMember).getType();
    }

    /**
     *
     */
    static private boolean[] PropFlags = null;

    /**
     *
     */
    static public void defineMembers(VTable vTable,
                                     Class clazz,
                                     boolean isStatic,
                                     SafeJ safeJ)
      throws AlreadyDefinedException {
        if (null == PropFlags) {
            FlexList props = FlexList.fromType(Boolean.TYPE);
            if (Boolean.getBoolean("e.safej.bind-var-to-varName")) {
                props.push(Boolean.FALSE);
            }
            if (Boolean.getBoolean("e.safej.bind-var-to-propName")) {
                props.push(Boolean.TRUE);
            }
            PropFlags = (boolean[])props.getArray(Boolean.TYPE);
        }

        if (!Modifier.isPublic(clazz.getModifiers())) {
            return;
        }
        Field[] fields = clazz.getDeclaredFields();
        if (null != fields) {
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                int mods = field.getModifiers();
                if (!Modifier.isPublic(mods)) {
                    continue;
                }
                if (Modifier.isStatic(mods) != isStatic) {
                    continue;
                }
                for (int propCase = 0;
                     propCase < PropFlags.length;
                     propCase++) {

                    boolean propFlag = PropFlags[propCase];
                    MethodNode getter = new VarGetterNode(field, propFlag);
                    if (Modifier.isFinal(mods)) {
                        if (field.getType().isPrimitive()) {
                            //final scalars can't be suppressed
                            vTable.addMethod(getter, SafeJ.ALL);
                        } else {
                            vTable.addMethod(getter, safeJ);
                        }
                    } else {
                        vTable.addMethod(getter, safeJ);
                        MethodNode setter = new VarSetterNode(field, propFlag);
                        vTable.addMethod(setter, safeJ);
                    }
                }
            }
        }
    }
}
