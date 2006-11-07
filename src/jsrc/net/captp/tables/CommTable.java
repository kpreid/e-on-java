package net.captp.tables;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.develop.assertion.T;
import org.erights.e.elib.oldeio.EPrintable;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.tables.ArrayHelper;

import java.io.IOException;
import java.lang.reflect.Array;

/**
 * Just some common mechanism made available to the CommTable implementations.
 * <p>
 * <p/>
 * CommTables are defined in terms of indices (always positive), not position.
 * At a higher level, positions use positive or negative to encode choice of
 * table (questions vs imports, answers vs exports). This can be a bit
 * confusing because CommTable internally uses negated indices for free list
 * entries, and these two uses of negation are completely independent.
 * <p/>
 * The rest of CapTP depends on the tables, but for the sake of unit testing,
 * each table stands alone to the greatest reasonable degree. Since
 * AnswersTable adds almost nothing to CommTable, you can unit test CommTable
 * by testing AnswersTable.
 *
 * @author Mark S. Miller
 */
public abstract class CommTable implements EPrintable {

    /**
     * Used to indicate the absence of any other object
     */
    static private final Object ThePumpkin = new Object();

    /**
     * Default initial capacity
     */
    static private final int INIT_CAPACITY = 16;

    static private final int GROWTH_FACTOR = 2;

    /**
     * How many allocated entries do I have?
     */
    private int mySize;

    /**
     * What is the size of my parallel arrays?
     */
    int myCapacity;

    /**
     * Keeps track of the allocation of my indices. <p>
     * <p/>
     * myFreeList[0] is unused and always has the value 0. For all i >= 1, if
     * myFreeList[i] >= 1, it's an allocation count. Otherwise, let next :=
     * -myFreeList[i]. If next >= 1, it's the index of the next free entry in
     * myFreeList. If next == 0, we're at the end of the list.
     */
    private int[] myFreeList;

    /**
     * Let first = -myFreeHead; If first >= 1, it's the index of the first free
     * entry in myFreeList. If first == 0, the list is empty.
     */
    private int myFreeHead;

    /**
     * The actual contents of the table.
     */
    Object[] myStuff;

    /**
     * Starts will all inidices free.
     */
    public CommTable() {
        mySize = 0;
        myCapacity = INIT_CAPACITY;
        myFreeList = new int[INIT_CAPACITY];
        myFreeList[0] = 0;
        //point at the first allocatable entry
        myFreeHead = -1;
        for (int i = 1; i < myCapacity; i++) {
            //each entry points at the next
            myFreeList[i] = -(i + 1);
        }
        //overwrite the last entry
        myFreeList[myCapacity - 1] = 0;
        myStuff = new Object[myCapacity];
        for (int i = 0; i < myCapacity; i++) {
            myStuff[i] = ThePumpkin;
        }
    }

    /**
     * Drop all state and make sure nothing ever works again.
     */
    public void smash(Throwable problem) {
        mySize = -1;
        myCapacity = -1;
        myFreeList = null;
        myFreeHead = 1;
        myStuff = null;
    }

    /**
     * How many allocated entries?
     */
    public int size() {
        return mySize;
    }

    /**
     * Is this index free?  If it's past the end, yes. If it's before the
     * beginning, it's not valid, so no.
     */
    boolean isFree(int index) {
        if (index >= myCapacity) {
            return true;
        }
        if (0 >= index) {
            throw new IllegalArgumentException("bad index: " + index);
        }
        return 0 >= myFreeList[index];
    }

    /**
     * Complain if not free
     */
    public void mustBeFree(int index) {
        if (!isFree(index)) {
            throw new IllegalArgumentException("not free: " + index);
        }
    }

    /**
     * Complain if not allocated
     */
    public void mustBeAlloced(int index) {
        if (isFree(index)) {
            throw new IllegalArgumentException("not alloced: " + index);
        }
    }

    /**
     * What the next capacity big enough to represent index?
     */
    private int bigEnough(int index) {
        if (0 >= index) {
            throw new IllegalArgumentException("bad index: " + index);
        }
        int result = myCapacity;
        while (index >= result) {
            //XXX it's stupid to have an iterative algorithm. How do I
            //calculate the smallest power of 2 > index?
            result += GROWTH_FACTOR;
        }
        return result;
    }

    /**
     * Returns array or a copy of array sized to capacity.
     */
    static private Object grow(Object array, int capacity) {
        int len = Array.getLength(array);
        if (len >= capacity) {
            return array;
        }
        return ArrayHelper.resize(array, capacity);
    }

    /**
     * Become big enough to hold index. <p>
     * <p/>
     * Newly added elements are on the (newly grown) free list.
     */
    private void growToHold(int index) {
        int oldCapacity = myCapacity;
        myCapacity = bigEnough(index);
        if (oldCapacity == myCapacity) {
            return;
        }
        myFreeList = (int[])grow(myFreeList, myCapacity);
        myStuff = (Object[])grow(myStuff, myCapacity);
        for (int i = oldCapacity; i < myCapacity; i++) {
            //each entry points at the next
            myFreeList[i] = -(i + 1);
            myStuff[i] = ThePumpkin;
        }
        //overwrite the last entry
        myFreeList[myCapacity - 1] = myFreeHead;
        myFreeHead = -oldCapacity;
    }

    /**
     * Deallocates an allocated index. <p>
     * <p/>
     * Subclasses may override and send-super in order to clear their parallel
     * arrays.
     */
    public void free(int index) {
        mustBeAlloced(index);
        myFreeList[index] = myFreeHead;
        myFreeHead = -index;
        myStuff[index] = ThePumpkin;
        mySize--;
    }

    /**
     * Increment index's allocation count. <p>
     * <p/>
     * index must already be allocated
     */
    public void incr(int index) {
        mustBeAlloced(index);
        myFreeList[index]++;
    }

    /**
     * Decrement index's allocation count delta, and free it if it reaches
     * zero.
     * <p/>
     * On entry, index must be allocated.
     *
     * @return whether the entry got freed
     */
    public boolean decr(int index, int delta) {
        mustBeAlloced(index);
        int newCount = myFreeList[index] - delta;
        if (0 >= newCount) {
            free(index);
            return true;
        } else {
            myFreeList[index] = newCount;
            return false;
        }
    }

    /**
     * Allocate a particular index. <p>
     * <p/>
     * On entry, index must be free. <p>
     * <p/>
     * Since the free list is singly linked, we can't generally do this in
     * constant time. However, by far the typical case is for the requested
     * index to be the same as the one that zero-argument alloc would have
     * allocated, so we need merely assure that this case is constant time.
     */
    private void alloc(int index) {
        mustBeFree(index);
        growToHold(index);
        if (index == -myFreeHead) {
            //we win
            myFreeHead = myFreeList[index];
            myFreeList[index] = 1;
            mySize++;
            return;
        }
        //we lose. Search the free list for -index
        int i = -myFreeHead;
        while (0 != i) {
            int next = -myFreeList[i];
            if (index == next) {
                myFreeList[i] = myFreeList[index];
                myFreeList[index] = 1;
                mySize++;
                return;
            }
            i = next;
        }
        T.fail("internal: broken free list");
    }


    /**
     * Gets the object at the allocated index.
     */
    public Object get(int index) {
        mustBeAlloced(index);
        Object result = myStuff[index];
        //noinspection ObjectEquality
        if (ThePumpkin == result) {
            T.fail("export: " + index + " is a pumpkin");
        }
        return result;
    }

    /**
     *
     */
    public void put(int index, Object value) {
        put(index, value, false);
    }

    /**
     *
     */
    public void put(int index, Object value, boolean strict) {
        if (isFree(index)) {
            alloc(index);
            myStuff[index] = value;
        } else if (strict) {
            throw new IllegalArgumentException("not alloced: " + index);
        } else {
            myStuff[index] = value;
        }
    }

    /**
     * Allocates a free index, put value there, and returns that index.
     * <p/>
     * Subclasses may override and send-super to initialize their parallel
     * arrays.
     * <p/>
     * The wireCount is initialized to one
     */
    public int bind(Object value) {
        if (0 == myFreeHead) {
            growToHold(myCapacity);
        }
        int result = -myFreeHead;
        mustBeFree(result);
        myFreeHead = myFreeList[result];
        myFreeList[result] = 1;
        myStuff[result] = value;
        mySize++;
        return result;
    }

    /**
     *
     */
    public void __printOn(TextWriter out) throws IOException {
        if (myFreeList == null) {
            out.print("<smashed table>");
            return;
        }
        out.print("<", getClass(), "[");
        for (int i = 1; i < myCapacity; i++) {
            if (!isFree(i)) {
                out.print("\n  " + i, ":", myStuff[i]);
            }
        }
        out.print("\n], free: [");
        for (int i = -myFreeHead; 0 != i; i = -myFreeList[i]) {
            out.print(" " + i);
        }
        out.print("]>");
    }
}
