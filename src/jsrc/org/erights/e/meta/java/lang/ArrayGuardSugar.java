package org.erights.e.meta.java.lang;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.develop.assertion.T;
import org.erights.e.elib.base.ClassDesc;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.tables.ArrayHelper;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.util.OneArgFunc;

import java.lang.reflect.Array;

/**
 * Coerces arrays and ConstLists to the type 'T[]' (array of T), for some type
 * T, by coercing each element to T.
 *
 * @author Mark S. Miller
 */
public class ArrayGuardSugar extends ClassDesc {

    static private final long serialVersionUID = 278964491113218650L;

    /**
     * @param clazz Must be an array class
     */
    public ArrayGuardSugar(Class clazz) {
        super(clazz);
        T.require(clazz.isArray(), clazz, " must be an array type");
    }

    /**
     *
     */
    protected Object subCoerceR(Object shortSpecimen, OneArgFunc optEjector) {
        Class elType = asClass().getComponentType();

        if (shortSpecimen.getClass().isArray()) {

            //Note: the following works only under the assumption that
            //arrays are immutable and have no EQ identity.
            int len = Array.getLength(shortSpecimen);
            Object result = ArrayHelper.newArray(elType, len);
            for (int i = 0; i < len; i++) {
                Object el = Array.get(shortSpecimen, i);
                ArrayHelper.arraySet(result, i, E.as(el, elType, optEjector));
            }
            return result;

        }
        if (shortSpecimen instanceof ConstList) {
            return ((ConstList)shortSpecimen).getArray(elType);
        }
        if (shortSpecimen instanceof String) {
            return subCoerceR(((String)shortSpecimen).toCharArray(),
                              optEjector);
        }
        return super.subCoerceR(shortSpecimen, optEjector);
    }
}
