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
public class JavaInstanceMatcher extends JavaMatcher {

    private transient Class myOptDirectClass = null;

    /**
     *
     * @param method
     */
    public JavaInstanceMatcher(Method method) {
        super(method);
        T.require(isMatcher(method),
                  "Not matcher: ", method);
    }

    /**
     * @return
     * @see JavaMatcher#isMatcher(Method, int)
     */
    static boolean isMatcher(Method method) {
        if (!isMatcher(method, 0)) {
            return false;
        }
        int mods = method.getModifiers();
        return Modifier.isPublic(mods) && !Modifier.isStatic(mods);
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
                VTableEntry result = new JavaInstanceMatcher(myMethod);
                return result.forVTable(vTable);
            }
        } else {
            return new JavaInstanceMatcher(myMethod);
        }
    }

    /**
     * @return
     */
    Object innerExecute(Object self, Object[] pair) {
        try {
            return myMethod.invoke(self, pair);
        } catch (IllegalAccessException e) {
            throw ExceptionMgr.asSafe(e);
        } catch (IllegalArgumentException e) {
            throw ExceptionMgr.asSafe(e);
        } catch (InvocationTargetException e) {
            throw ExceptionMgr.asSafe(e);
        } catch (NullPointerException npe) {
            throw npe;
        }
    }

    /**
     * @return
     */
    String explain() {
        return "<a " + ClassDesc.simpleSig(myMethod.getDeclaringClass()) +
          ">.match(String, ConstList)";
    }
}
