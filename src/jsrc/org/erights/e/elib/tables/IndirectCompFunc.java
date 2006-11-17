package org.erights.e.elib.tables;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.meta.java.math.EInt;

import java.lang.reflect.Array;

/**
 * Created with knowledge of an underlying array that must remain stable during
 * the sort (or whatever), and of a comparison function to be applied to
 * elements of that array. Compares two ints according the the comparison of
 * the array elements at those indices.
 */


class IndirectCompFunc implements CompFunc {

    private final CompFunc mySubFunc;

    private final Object myArray;


    private IndirectCompFunc(CompFunc subFunc, Object array) {
        mySubFunc = subFunc;
        myArray = array;
    }

    /**
     * Returns subFunc(array[a], array[b])
     */
    public double run(Object a, Object b) {
        int aIndex = ((Number)a).intValue();
        int bIndex = ((Number)b).intValue();
        return mySubFunc.run(Array.get(myArray, aIndex),
                             Array.get(myArray, bIndex));
    }

    /**
     * Returns an array of indices sorted according to the elements of array
     * they index into. This is a permutation. The original array is
     * unchanged.
     */
    static public Number[] indirectSort(Object array) {
        return indirectSort(array, SimpleCompFunc.THE_ONE);
    }

    /**
     * Returns an array of indices sorted according to the elements of array
     * they index into.
     * <p/>
     * This is a permutation. The original array is unchanged.
     */
    static public Number[] indirectSort(Object array, CompFunc func) {
        int len = Array.getLength(array);
        Number[] identity = new Number[len];
        for (int i = 0; i < len; i++) {
            identity[i] = EInt.valueOf(i);
        }
        ConstList list = ConstList.fromArray(identity);
        func = new IndirectCompFunc(func, array);
        return (Number[])list.sort(func).getArray(Number.class);
    }
}
