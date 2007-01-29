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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * A java public non-final instance variable becomes a one argument method for
 * setting its value
 *
 * @author Mark S. Miller
 */


class VarSetterNode extends JavaMemberNode {

    /**
     *
     */
    private final String myVerb;

    /**
     *
     */
    VarSetterNode(Field field, boolean propFlag) {
        super(field);
        if (Modifier.isFinal(field.getModifiers())) {
            throw new Error("internal: not a settable instance variable");
        }
        String propName = super.getVerb();
        if (propFlag) {
            myVerb = asSetterVerb(propName);
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

        ((Field)myMember).set(self, args[0]);
        return null;
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
        return " Sets the variable's value";
    }

    /**
     *
     */
    public Class[] parameterTypes() {
        Class[] result = {((Field)myMember).getType()};
        return result;
    }

    /**
     *
     */
    public Class returnType() {
        return Void.class;
    }
}
