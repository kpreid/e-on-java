// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.elib.prim;

import org.erights.e.develop.assertion.T;
import org.erights.e.develop.exception.ExceptionMgr;
import org.erights.e.elib.base.ClassDesc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * @author Mark S. Miller
 */
public class JavaStaticMatcher extends JavaMatcher {

    private transient StaticMaker myOptSelf = null;

    /**
     * @param method
     */
    public JavaStaticMatcher(Method method) {
        super(method);
        T.require(isMatcher(method), "Not matcher: ", method);
    }

    /**
     * @return
     * @see JavaMatcher#isMatcher(Method,int)
     */
    static boolean isMatcher(Method method) {
        if (!isMatcher(method, 0)) {
            return false;
        }
        int mods = method.getModifiers();
        return Modifier.isPublic(mods) && Modifier.isStatic(mods);
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
                VTableEntry result = new JavaStaticMatcher(myMethod);
                return result.forVTable(vTable);
            }
        } else {
            return new JavaStaticMatcher(myMethod);
        }
    }

    /**
     * @param self ignored
     * @return
     */
    Object innerExecute(Object self, Object[] pair) {

        try {
            return myMethod.invoke(null, pair);
        } catch (IllegalAccessException e) {
            throw ExceptionMgr.asSafe(e);
        } catch (IllegalArgumentException e) {
            throw ExceptionMgr.asSafe(e);
        } catch (InvocationTargetException e) {
            throw ExceptionMgr.asSafe(e);
        }
    }

    /**
     * @return
     */
    String explain() {
        return ClassDesc.simpleSig(myMethod.getDeclaringClass()) +
          ".match(String, ConstList)";
    }
}
