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

import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.serial.PassByProxy;

import java.io.IOException;

/**
 * FlexList extends EList with mutation operations. <p>
 *
 * @author Mark S. Miller
 */
public abstract class FlexList extends EList implements PassByProxy {

    static private final long serialVersionUID = 3092632111579141040L;

    /**
     * Only subclasses within the package
     */
    FlexList() {
    }

    /**
     * Returns a read-only facet on this list. Someone holding this facet may
     * see changes, but they cannot cause them.
     */
    public EList readOnly() {
        return new ROList(this);
    }

    /**
     * Places value at this index. If index is the current size of the list,
     * the list is extended with this element. Otherwise, if the index is out
     * of range, throws IndexOutOfBoundsException
     *
     * @throws IndexOutOfBoundsException if index isn't in 0..size
     */
    public abstract void put(int index, Object value)
      throws IndexOutOfBoundsException;

    /**
     * Reorders the list in place into ascending order according to func
     */

    abstract void sortInPlace(CompFunc func);

    /**
     * Put the value on the end of the list, making the list one larger.
     */
    public void push(Object value) {
        put(size(), value);
    }

    /**
     * Put all the elements of other, in order, onto the end of this list.
     */
    public void append(EList other) {
        int len1 = size();
        int len2 = other.size();
        replace(len1, len1, other, 0, len2);
    }

    /**
     * Remove and return the last element of this list.
     */
    public Object pop() {
        int len = size();
        Object result = get(len - 1);
        setSize(len - 1);
        return result;
    }

    /**
     * Make this list be exactly 'newSize', truncating or extending as
     * necessary. If this list is extended, what is it filled with?  If it is
     * of a scalar valueType(), it is filled with the zero element for that
     * scalar. Otherwise, it is filled with nulls.
     */
    public abstract void setSize(int newSize);

    /**
     * Make this list be at least 'minSize', extending as necessary.
     *
     * @see FlexList#setSize
     */
    public void ensureSize(int minSize) {
        if (minSize > size()) {
            setSize(minSize);
        }
    }

    /**
     * Replace the run starting at start with other.
     */
    public void setRun(int start, EList other) {
        setRun(start, size(), other);
    }

    /**
     * Replace from start..!bound in this list with other.
     */
    public void setRun(int start, int bound, EList other) {
        replace(start, bound, other, 0, other.size());
    }

    /**
     * '<tt>x.insert(i,v)</tt>' is the same as '<tt>x(i,i) := [v]</tt>', ie,
     * '<tt>x.setRun(i,i,[v])</tt>'.
     */
    public void insert(int start, Object value) {
        Object[] other = {value};
        replace(start, start, ConstList.fromArray(other), 0, 1);
    }

    /**
     * Replace from start..!bound in this list with lstart..!lbound in other.
     */
    public abstract void replace(int start,
                                 int bound,
                                 EList other,
                                 int lstart,
                                 int lbound);

    /**
     * Remove and return the run of this list starting at start
     */
    public ConstList removeRun(int start) {
        return removeRun(start, size());
    }

    /**
     * Remove and return the run of this list from start..!bound
     */
    public ConstList removeRun(int start, int bound) {
        ConstList result = run(start, bound);
        setRun(start, bound, ConstListImpl.EmptyList);
        return result;
    }

    /**
     * A FlexList is unconditionally transparent
     */
    public Object[] __optUncall() {
        Object[] result = {snapshot(), "diverge", E.NO_ARGS};
        return result;
    }

    /**
     * Prints use E list syntax followed by ".diverge()" (to indicate
     * flexible).
     */
    public void __printOn(TextWriter out) throws IOException {
        printOn("[", ", ", "].diverge()", out);
    }

    /**
     *
     */
    static public FlexList make() {
        return make(10);
    }

    /**
     *
     */
    static public FlexList make(int initialCapacity) {
        return new FlexListImpl(initialCapacity);
    }

    /**
     *
     */
    static public FlexList fromType(Class type) {
        return fromType(type, 10);
    }

    /**
     *
     */
    static public FlexList fromType(Class type, int initialCapacity) {
        Object arr = ArrayHelper.newArray(type, initialCapacity);
        return new FlexListImpl(arr, 0);
    }
}
