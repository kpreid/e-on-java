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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

/**
 * Turns a java constructor into a "run" method on the corresponding
 * StaticMaker, as seen thru E.call*() and E.send*().
 *
 * @author Mark S. Miller
 */
class ConstructorNode extends JavaMemberNode {

    /**
     *
     */
    private final String myVerb;

    private transient StaticMaker myOptSelf = null;

    /**
     *
     */
    private ConstructorNode(Constructor constr, String verb) {
        super(constr);
        myVerb = verb;
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
        Constructor[] constrs = clazz.getConstructors();
        for (int i = 0; i < constrs.length; i++) {
            vTable.addMethod(new ConstructorNode(constrs[i], "run"),
                             safeJ);
        }
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
                VTableEntry result = new ConstructorNode(
                  (Constructor)myMember,
                  myVerb);
                return result.forVTable(vTable);
            }
        } else {
            return new ConstructorNode((Constructor)myMember, myVerb);
        }
    }

    /**
     *
     */
    public Object innerExecute(Object self, Object[] args)
      throws InstantiationException, IllegalAccessException,
      IllegalArgumentException, InvocationTargetException {

        return ((Constructor)myMember).newInstance(args);
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
        return " Constructor";
    }

    /**
     *
     */
    public String getVerb() {
        return myVerb;
    }

    /**
     *
     */
    public Class[] parameterTypes() {
        return ((Constructor)myMember).getParameterTypes();
    }

    /**
     * The return type is the declaring class of the Constructor, since we're
     * not concerned about 'super(...)' invocations.
     */
    public Class returnType() {
        return myMember.getDeclaringClass();
    }
}
