// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.elib.tables;

import org.erights.e.develop.exception.EBacktraceException;
import org.erights.e.elib.prim.E;
import org.erights.e.meta.java.math.EInt;

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.Hashtable;

/**
 * <b>Dangerous, for use by trusted code only.</b>
 * <p/>
 * For when you really want to deal with arrays rather than lists for
 * efficiency (or because you're building lists out of arrays), but want to
 * deal with the arrays in an E-like manner.
 * <p/>
 * This class must <i>not</i> be made safe, as it provides public operations
 * that mutate arrays, and that break the encapsulation of lists. Use only with
 * great care, and never to mutate the array inside a list.
 *
 * @author Mark S. Miller
 */
public class ArrayHelper {

    /**
     *
     */
    static private final Class[][] ArrayTypes = {{Boolean.class, Boolean.TYPE},
      {Byte.class, Byte.TYPE},
      {Character.class, Character.TYPE},
      {Double.class, Double.TYPE},
      {Float.class, Float.TYPE},
      {Integer.class, Integer.TYPE},
      {Long.class, Long.TYPE},
      {Short.class, Short.TYPE},
      {EInt.class, BigInteger.class},
      {Void.TYPE, Void.class},
      //notice reversal
    };
    /**
     *
     */
    static private final Hashtable ArrayTypeMap = initArrayTypeMap(0, 1);

    /**
     * prevents instantiation
     */
    private ArrayHelper() {
    }

    /**
     * We use a Hashtable rather than one of our own in order to avoid a
     * circular init-time dependency, since our tables depend on mapping scalar
     * types.
     */
    static private Hashtable initArrayTypeMap(int from, int to) {
        Hashtable result = new Hashtable(ArrayTypes.length * 2);
        for (int i = 0; i < ArrayTypes.length; i++) {
            result.put(ArrayTypes[i][from], ArrayTypes[i][to]);
        }
        return result;
    }

    /**
     * Given a class, return a preferred class for making an array of
     * (abstractly) the kinds of values described by the argument class.
     * <p/>
     * It maps {@link Void}.TYPE to Void.class.<br> It maps boxed scalar types,
     * like {@link Integer}.class, to the corresponding primitive (unboxed)
     * scalar type, like Integer.TYPE. Otherwise, it just returns its
     * argument.
     */
    static public Class typeForArray(Class elType) {
        Class result = (Class)ArrayTypeMap.get(elType);
        if (null != result) {
            return result;
        } else {
            return elType;
        }
    }

    /**
     * Like {@link Array#newInstance(Class,int)}, but the type is first
     * converted according to {@link #typeForArray(Class)}.
     */
    static public Object newArray(Class elType, int size) {
        return Array.newInstance(typeForArray(elType), size);
    }

    /**
     * Like list.getArray(), but may provide direct access to the array
     * encapsulated inside a list for speed.
     * <p/>
     * Use this for good and not for evil. In particular, it must <i>never</i>
     * be used to mutate the array inside a list, or to enable such mutation.
     *
     * @return
     */
    static public Object getFastArray(EList list) {
        if (list instanceof ArrayedList) {
            return ((ArrayedList)list).getSecretArray();
        } else {
            return list.getArray();
        }
    }

    /**
     * Returns a copy of oldArray but of length newLen.
     * <p/>
     * If newLen is larger than the original length, then the extra slots are
     * filled with the zero element for this type. This is null for any Java
     * reference type (and non-primitive class), and 0, '\0', 0.0, or false, as
     * appropriate, for corresponding scalar types.
     * <p/>
     * If newLen is smaller than the original, the extra elements are dropped.
     */
    static public Object resize(Object oldArray, int newLen) {
        //is it really this much trouble just to clone an array?
        Class valType = oldArray.getClass().getComponentType();
        int oldLen = Array.getLength(oldArray);
        //The following is Array.newInstance(..) rather than
        //EList.newArray(..) since arraycopy won't work between, for example,
        //an int[23] and a Integer[23]. Bletch!
        Object result = Array.newInstance(valType, newLen);
        System.arraycopy(oldArray,
                         0,
                         result,
                         0,
                         StrictMath.min(oldLen, newLen));
        return result;
    }

    /**
     * Returns a copy of the part of oldArray between start inclusive and bound
     * exclusive.
     * <p/>
     * Like {@link EList#run(int,int)}, but for arrays.
     */
    static public Object slice(Object oldArray, int start, int bound) {
        //is it really this much trouble just to clone an array?
        Class valType = oldArray.getClass().getComponentType();
        //The following is Array.newInstance(..) rather than
        //EList.newArray(..) since arraycopy won't work between, for example,
        //an int[23] and a Integer[23]. Bletch!
        Object result = Array.newInstance(valType, bound - start);
        System.arraycopy(oldArray, start, result, 0, bound - start);
        return result;
    }

    /**
     * Workaround for a bug in the libcgj from gcc 3.3.3
     */
    static private final boolean GCJ_WORKAROUND = true;

    /**
     * Like {@link Array#set(Object,int,Object)}, but automatically coerces
     * if necessary, and is void/null tolerant.
     */
    static public void arraySet(Object array, int index, Object val) {
        if (GCJ_WORKAROUND && null == val) {
            Class type = array.getClass().getComponentType();
            if (!type.isPrimitive()) {
                ((Object[])array)[index] = null;
                return;
            }
        }
        try {
            Array.set(array, index, val);
        } catch (IllegalArgumentException iae) {
            Class type = array.getClass().getComponentType();
            if (Void.class == type && null == val) {
                return;
            }
            val = E.as(val, type);
            try {
                Array.set(array, index, val);
            } catch (IllegalArgumentException iae2) {
                throw new EBacktraceException(iae2,
                                          "# Can't fit " + E.toQuote(val) +
                                            " into array of " + type);
            }
        }
    }

    /**
     * Like {@link System#arraycopy(Object,int,Object,int,int)}, but
     * automatically coerces if necessary, and is void/null tolerant.
     */
    static public void arraycopy(Object src,
                                 int src_position,
                                 Object dst,
                                 int dst_position,
                                 int length) {
        Class srcType = src.getClass().getComponentType();
        Class dstType = dst.getClass().getComponentType();
        if (srcType == dstType) {
            System.arraycopy(src, src_position, dst, dst_position, length);
        } else {
            for (int i = 0; i < length; i++) {
                arraySet(dst,
                         dst_position + i,
                         Array.get(src, src_position + i));
            }
        }
    }
}
