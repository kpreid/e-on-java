package org.erights.e.elib.tables;

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

import java.lang.reflect.Array;

/**
 * FlexList extends EList with mutation operations. <p>
 *
 * @author Mark S. Miller
 */


class FlexListImpl extends FlexList implements ArrayedList {

    /**
     * myVals is an array
     */
    private Object myVals;

    /**
     * mySize <= myVals.length
     */
    private int mySize;

    /**
     * The caller is *trusted* not to modify vals after handing it in.
     */
    FlexListImpl(Object vals, int size) {
        myVals = vals;
        mySize = size;
    }

    /**
     *
     */
    FlexListImpl(int capacity) {
        myVals = new Object[capacity];
        mySize = 0;
    }

    /**
     * The caller is *trusted* not to modify the returned array. Even though
     * this is declared 'public', it is believed (and required) to be package
     * scope, since all declarations are only in package scoped classes or
     * interfaces.
     */
    public Object getSecretArray() {
        return myVals;
    }

    /**
     *
     */
    public ConstList snapshot() {
        Class valType = valueType();
        Object array = ArrayHelper.newArray(valType, mySize);
        System.arraycopy(myVals, 0, array, 0, mySize);
        if (valType == Character.TYPE) {
            return Twine.fromString(new String((char[])array));
        } else {
            return new ConstListImpl(array);
        }
    }

    /**
     *
     */
    public Object get(int index) throws IndexOutOfBoundsException {
        if (index >= mySize) {
            throw new IndexOutOfBoundsException(index
                                                + " must be below "
                                                + mySize);
        }
        return Array.get(myVals, index);
    }

    /**
     * How many entries are in the table?
     */
    public int size() {
        return mySize;
    }

    /**
     * All values in this table must be of this type
     */
    public Class valueType() {
        return myVals.getClass().getComponentType();
    }

    /**
     * Places value at this index. If index is the current size
     * of the list, the list is extended with this element. Otherwise, if
     * the index is out of range, throws IndexOutOfBoundsException
     *
     * @throws IndexOutOfBoundsException if index isn't in 0..size
     */
    public void put(int index, Object value)
      throws IndexOutOfBoundsException {
        if (index == mySize) {
            setSize(mySize + 1);
        } else if (index > mySize) {
            throw new IndexOutOfBoundsException(index
                                                + " must be at most "
                                                + mySize);
        }
        ArrayHelper.arraySet(myVals, index, value);
    }

    /**
     * Reorders the list in place into ascending order according to func
     */
    void sortInPlace(CompFunc func) {
        if (valueType().isPrimitive()) {
            FlexList flex = diverge();
            flex.sortInPlace(func);
            setRun(0, mySize, flex);

        } else {
            qsort((Object[])myVals, 0, mySize, func);
        }
    }

    /**
     *
     */
    static private void qsort(Object[] a,
                              int start,
                              int bound,
                              CompFunc func) {
        if (start >= bound - 1) {
            //bottom out: a zero or one element array is already sorted
            return;
        }

        /*
         * Partition
         */
        //should first randomize the choice of pivot, such as by swapping
        //a[start] with a[(start+1 ..! bound) random]
        Object pivot = a[start];
        //nextBelow is the index of the first unallocated slot for elements
        //leq the pivot. Similarly, nextAbove is the first unallocated slot
        //for elements not leq the pivot.
        int nextBelow = start + 1;
        int nextAbove = bound - 1;
        //while we aren't fully partitioned
        while (nextBelow <= nextAbove) {
            Object specimen = a[nextBelow];
            double comp = func.run(specimen, pivot);
            if (comp <= 0.0) {
                //don't need to swap, since it'll already be below
                nextBelow++;
            } else if (comp > 0.0) {
                a[nextBelow] = a[nextAbove];
                a[nextAbove] = specimen;
                nextAbove--;
            } else /* Double.isNaN(comp) */ {
                throw new IllegalArgumentException
                  ("partial order not yet implemented");
            }
        }
        //At this point, start <= nextAbove < nextBelow <= bound
        //and a[start+1..nextAbove] <= pivot < a[nextBelow..!bound]
        //so we swap pivot and a[nextAbove]
        a[start] = a[nextAbove];
        a[nextAbove] = pivot;

        /*
         * We are partitioned. Recur
         */
        qsort(a, start, nextAbove, func); //remember that nextAbove is used as
        //an exclusive bound
        qsort(a, nextBelow, bound, func);
    }

    /**
     * Overwrites this run with the zero element for my valueType(). May
     * be called on runs exceeding my size but within my capacity. That's
     * why it's private.
     */
    private void zero(int start, int bound) {
        Class clazz = valueType();
        if (clazz.isPrimitive()) {
            //XXX there's gotta be a cheaper way to do this
            int len = bound - start;
            Object zeros = ArrayHelper.newArray(clazz, len);
            System.arraycopy(zeros, 0, myVals, start, len);
        } else {
            Object[] vals = (Object[])myVals;
            for (int i = start; i < bound; i++) {
                vals[i] = null;
            }
        }
    }

    /**
     * Make this list be exactly 'newSize', truncating or extending as
     * necessary. If this list is extended, what is it filled with?
     * If it is of a scalar valueType(), it is filled with the zero
     * element for that scalar. Otherwise, it is filled with nulls.
     */
    public void setSize(int newSize) {
        if (newSize == mySize) {
            return;
        } else if (newSize < mySize) {
            zero(newSize, mySize);

        } else /*newSize > mySize*/ {
            int capacity = Array.getLength(myVals);
            if (newSize > capacity) {
                //otherwise no need to zero, since already zeroed

                //This capacity + newSize rule needs to be examined.
                //It will at least double, and it will be big enough,
                //so maybe it's fine.
                Object newVals = ArrayHelper.newArray(valueType(),
                                                      capacity + newSize);
                System.arraycopy(myVals, 0, newVals, 0, mySize);
                myVals = newVals;
            }
        }
        mySize = newSize;
    }

    /**
     * Replace from start..!bound in this list with lstart..!lbound in
     * other.
     */
    public void replace(int start, int bound,
                        EList other, int lstart, int lbound) {
        int slen1 = bound - start;
        int slen2 = lbound - lstart;
        int diff = slen2 - slen1;

        if (0 != diff) {
            int newSize = mySize + diff;
            int slideSize = mySize - bound;
            //ensureSize may change mySize, so we do mySize-based math
            //first.
            ensureSize(newSize);
            //the stuff after my run (the destination) needs to slide
            //over. Note that arraycopy is defined to do the right
            //thing in case of overlap.
            System.arraycopy(myVals, bound,
                             myVals, start + slen2, slideSize);
            mySize = newSize;
        }
        //pay no more attention to bound, slen1, or diff. Our size is
        //now fine, and we need to fill in our start..!(start + slen2)
        //from other's lstart..!lbound.

        if (other instanceof ArrayedList) {
            Object otherVals = ((ArrayedList)other).getSecretArray();
            try {
                ArrayHelper.arraycopy(otherVals, lstart, myVals, start, slen2);
                return;
            } catch (ArrayStoreException ase) {
                //This exception merely means we should try again the
                //painful way, since it can fail under conditions that
                //are fine for E. Therefore, we fall thru.
            }
        }
        for (int i = 0; i < slen2; i++) {
            put(start + i, other.get(lstart + i));
        }
    }
}
