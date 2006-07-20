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
public class JavaSugarMatcher extends JavaMatcher {

    /**
     *
     * @param method
     */
    public JavaSugarMatcher(Method method) {
        super(method);
        T.require(isMatcher(method),
                  "Not matcher: ", method);
    }

    /**
     * @return
     * @see JavaMatcher#isMatcher(Method, int)
     */
    static boolean isMatcher(Method method) {
        if (!isMatcher(method, 1)) {
            return false;
        }
        int mods = method.getModifiers();
        return Modifier.isPublic(mods) && Modifier.isStatic(mods);
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
     * @param self Becomes the first arg
     * @param pair The next (and last) two args
     * @return
     */
    Object innerExecute(Object self, Object[] pair) {

        Object[] args = {self, pair[0], pair[1]};
        try {
            return myMethod.invoke(null, args);
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
          ".match(<receiver>, String, ConstList)";
    }
}
