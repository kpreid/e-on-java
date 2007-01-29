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

import org.erights.e.elib.base.Thunk;
import org.erights.e.elib.oldeio.EPrintable;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.ref.Ref;
import org.erights.e.elib.serial.Persistent;
import org.erights.e.meta.java.math.EInt;

import java.io.IOException;

/**
 * A EList is a sequence of values.
 * <p/>
 * 'EList' is a query-only interface that's agnostic about whether the data can
 * change and whether a EList can be cast to an object by which to change it.
 * 'EList' is also agnostic about the basis for equality between tables.
 * Subtypes do pin these issues down: <p>
 * <p/>
 * ConstList guarantees immutability, uses value-based equality, and can be
 * transparently passed-by-copy over the network. <p>
 * <p/>
 * FlexList extends EList with mutation operations. <p>
 *
 * @author Mark S. Miller
 * @see org.erights.e.elib.tables.ConstList
 * @see org.erights.e.elib.tables.FlexList
 */
public abstract class EList implements EPrintable, Persistent, EIteratable {

    static private final long serialVersionUID = 4755060696966322025L;

    /**
     * Only subclasses within the package
     */
    EList() {
    }

    /**
     * Returns a ConstList whose state is a snapshot of the state of this list
     * at the time of the snapshot() request. A ConstList returns itself.
     */
    public abstract ConstList snapshot();

    /**
     * Returns a read-only facet on this list. Someone holding this facet may
     * see changes, but they cannot cause them.
     */
    public abstract EList readOnly();

    /**
     * Returns a FlexList whose initial state is a snapshot of the state of
     * this list at the time of the diverge() request.
     * <p/>
     * Further changes to the original and/or the new list are independent --
     * they diverge.
     * <p/>
     * The new list is constrained to only hold values of 'valueType'. XXX
     * valueType should be of type Guard rather than Class.
     */
    public FlexList diverge(Class valueType) {
        FlexList result = FlexList.fromType(valueType, size());
        result.append(this);
        return result;
    }

    /**
     * 'valueType' defaults to Object.class
     */
    public FlexList diverge() {
        return diverge(Object.class);
    }

    /**
     * A snapshot of the list sorted into ascending order
     */
    public ConstList sort() {
        return sort(SimpleCompFunc.THE_ONE);
    }

    /**
     * A snapshot of the list sorted into ascending order according to func
     */
    public ConstList sort(CompFunc func) {
        FlexList flex = diverge();
        flex.sortInPlace(func);
        return flex.snapshot();
    }

    /**
     * Returns a divergent array of type.
     * <p/>
     * XXX Should 'type' be of type Guard rather than Class?
     */
    public Object getArray(Class type, int start, int bound) {
        Object result = ArrayHelper.newArray(type, bound - start);
        if (this instanceof ArrayedList) {
            Object src = ((ArrayedList)this).getSecretArray();
            ArrayHelper.arraycopy(src, start, result, 0, bound - start);
        } else {
            for (int i = start; i < bound; i++) {
                ArrayHelper.arraySet(result, i, get(i));
            }
        }
        return result;
    }

    /**
     * start,bound defaults to everything (0,size()).
     */
    public Object getArray(Class type) {
        return getArray(type, 0, size());
    }

    /**
     * type defaults to valueType() and start,bound defaults to everything
     * (0,size()).
     */
    public Object getArray() {
        return getArray(valueType(), 0, size());
    }

    /**
     * What value does 'index' map to?
     *
     * @return nullOk; the value at index may be null.
     * @throws IndexOutOfBoundsException if index isn't in 0..!size.
     */
    public abstract Object get(int index) throws IndexOutOfBoundsException;

    /**
     * @return
     */
    public Object fetch(int index, Thunk insteadThunk) {
        if (0 <= index && index < size()) {
            return get(index);
        } else {
            return insteadThunk.run();
        }
    }

    /**
     * 'a.last()' is equivalent to 'a[a.size()-1]'.
     *
     * @throws IndexOutOfBoundsException if this list is empty.
     */
    public Object last() throws IndexOutOfBoundsException {
        return get(size() - 1);
    }

    /**
     * Does the candidate appear as a value in this list?
     */
    public boolean contains(Object candidate) {
        return -1 != indexOf1(candidate, 0);
    }

    /**
     * How many entries are in the list?
     */
    public abstract int size();

    /**
     * Call 'func' with each index-value pair in the table in order.
     */
    public void iterate(AssocFunc func) {
        int len = size();
        for (int i = 0; i < len; i++) {
            func.run(EInt.valueOf(i), get(i));
        }
    }

    /**
     * All values in this table must be of this type
     * <p/>
     * XXX Should this return a Guard rather than a Class?
     */
    public abstract Class valueType();

    /**
     * Concatenates (snapshots of) this list and other.
     * <p/>
     * XXX If they have a common class, we should use it.
     * <p/>
     * The default implementation here insists that other coerces to an EList,
     * but this is overridden in the Twine subclass.
     */
    public ConstList add(Object other) {
        EList otherList = (EList)E.as(other, EList.class);
        int len1 = size();
        int len2 = otherList.size();
        if (0 == len1) {
            return otherList.snapshot();
        } else if (0 == len2) {
            return snapshot();
        }
        FlexList flex = new FlexListImpl(len1 + len2);
        flex.append(this);
        flex.append(otherList);
        return flex.snapshot();
    }

    /**
     * A ConstList equivalent to 'this snapshot() + [value]' (unless
     * this.snapshot() is a Twine, in which case "+" will concatenate the
     * printed form of the [value] list).
     */
    public ConstList with(Object value) {
        FlexList flex = diverge();
        flex.push(value);
        return flex.snapshot();
    }

    /**
     * Return a ConstList just like this list except that the element at index
     * is value.
     * <p/>
     * To do the side-effect-free operation corresponding to the
     * conventional <pre>
     *     foo[index] := value
     * </pre>
     * do <pre>
     *     foo with= (index, value)
     * </pre>
     * Unlike the conventional assignment, this doesn't change the list, but
     * rather assigns to the foo variable a new list derived from the original
     * list by the with/2 operation. This expands to <pre>
     *     foo := foo.with(index, value)
     * </pre>
     */
    public ConstList with(int index, Object value) {
        FlexList flex = diverge();
        flex.put(index, value);
        return flex.snapshot();
    }

    /**
     * Return the result of concatenating n snapshots of this list
     */
    public ConstList multiply(int n) {
        if (0 == n) {
            return ConstListImpl.EmptyList;
        } else if (1 == n) {
            return snapshot();
        } else if (0 > n) {
            throw new IllegalArgumentException(n + " musn't be negative");
        }
        int len = size();
        FlexList flex = new FlexListImpl(len * n);
        for (int i = 0; i < n; i++) {
            flex.append(this);
        }
        return flex.snapshot();
    }

    /**
     * Turns [a, b, ...] into [0 => a, 1 => b, ...]
     */
    public ConstMap asMap() {
        int len = size();
        FlexMap flex = FlexMap.make(len);
        for (int i = 0; i < len; i++) {
            flex.put(EInt.valueOf(i), get(i));
        }
        return flex.snapshot();
    }

    /**
     * Turns [a, b, ...] into [a => null, b => null, ...]
     */
    public ConstMap asKeys() {
        int len = size();
        FlexMap flex = FlexMap.make(len);
        for (int i = 0; i < len; i++) {
            flex.put(get(i), null);
        }
        return flex.snapshot();
    }

    /**
     * Returns a set whose elements are the values in this list
     */
    public ConstSet asSet() {
        return ConstSet.make(asKeys());
    }

    /**
     * Does this list include candidate as a sub-list?
     */
    public boolean includes(EList candidate) {
        return -1 != startOf(candidate, 0);
    }

    /**
     * Returns a snapshot of the run starting at start.
     * <p/>
     * Historical note: The parameter to this method used to be an
     * integer-region, and this method (together with setRun/2 and removeRun/1)
     * used to be unimplemented. In thinking about implementing it, I decided
     * that the better part of valor was to avoid needless coupling -- we
     * should avoid making the concept of lists dependent on the (otherwise
     * higher level) concept of regions. Therefore, we've changed this to the
     * more conventional (Java-like) choice.
     */
    public ConstList run(int start) {
        return run(start, size());
    }

    /**
     * Returns a snapshot of the sublist from 'start' (inclusive) to 'bound'
     * (exclusive).
     */
    public ConstList run(int start, int bound) {
        FlexList flex = new FlexListImpl(bound - start);
        flex.replace(0, 0, this, start, bound);
        return flex.snapshot();
    }

    /**
     * The first index at which 'candidate' appears in this list, or -1 if
     * none.
     */
    public int indexOf1(Object candidate) {
        return indexOf1(candidate, 0);
    }

    /**
     * The first index >= 'start' at which 'candidate' appears in this list, or
     * -1 if none.
     */
    public int indexOf1(Object candidate, int start) {
        int len = size();
        for (int i = start; i < len; i++) {
            if (Ref.isSameEver(get(i), candidate)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * The last index at which 'candidate' appears in this list, or -1 if
     * none.
     * <p/>
     * Note that choosing '-1' leads to a programming pun that's too tempting
     * to avoid (though it should be documented). If you want the index of, for
     * example, the beginning of the last dot-separated substring of a string,
     * you can say "lastIndexOf1('.')+1". If there is no dot, this returns 0,
     * which is the correct answer for this case.
     */
    public int lastIndexOf1(Object candidate) {
        return lastIndexOf1(candidate, size() - 1);
    }

    /**
     * The last index <= 'start' at which 'candidate' appears in this list, or
     * -1 if none.
     */
    public int lastIndexOf1(Object candidate, int start) {
        for (int i = start; 0 <= i; i--) {
            if (Ref.isSameEver(get(i), candidate)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * The first index at which 'candidate' begins as a sub-list of this list,
     * or -1 if none.
     */
    public int startOf(EList candidate) {
        return startOf(candidate, 0);
    }

    /**
     * The first index >= start at which 'candidate' begins as a sub-list of
     * this list, or -1 if none.
     */
    public int startOf(EList candidate, int start) {
        int len1 = size();
        int len2 = candidate.size();
        if (0 == len2) {
            //a zero-length list is a sublist of everything
            return 0;
        } else if (len2 > len1) {
            //if candidate is bigger, fail early
            return -1;
        }
        int bound = len1 - len2 + 1;
        Object elem = candidate.get(0);
        for (int i = start; i < bound; i++) {
            if (Ref.isSameEver(get(i), elem)) {
                match:
                {
                    for (int j = 1; j < len2; j++) {
                        if (!Ref.isSameEver(get(i + j), candidate.get(j))) {
                            //go on to the next i
                            break match;
                        }
                    }
                    //only gets executed if we don't break out of the loop,
                    //in which case all j elements were the same
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * The last index at which 'candidate' begins as a sub-list of this list,
     * or -1 if none.
     */
    public int lastStartOf(EList candidate) {
        return lastStartOf(candidate, size());
    }

    /**
     * The last index <= start at which 'candidate' appears as a sub-list of
     * this list, or -1 if none.
     */
    public int lastStartOf(EList candidate, int start) {
        int len1 = size();
        int len2 = candidate.size();
        if (0 == len2) {
            //a zero-length list is a sublist of everything
            return start;
        } else if (len2 > len1) {
            //if candidate is bigger, fail early
            return -1;
        }
        start = StrictMath.min(start, len1 - len2);
        Object elem = candidate.get(0);
        for (int i = start; 0 <= i; i--) {
            if (Ref.isSameEver(get(i), elem)) {
                match:
                {
                    for (int j = 1; j < len2; j++) {
                        if (!Ref.isSameEver(get(i + j), candidate.get(j))) {
                            //go on to the next i
                            break match;
                        }
                    }
                    //only gets executed if we don't break out of the loop,
                    //in which case all j elements were the same
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * Prints 'left', the values separated by 'sep', and 'right'. <p>
     * <p/>
     * 'left' value0 'sep' ... 'right'
     */
    public void printOn(String left, String sep, String right, TextWriter out)
      throws IOException {
        out.print(left);
        int len = size();
        if (1 <= len) {
            out.quote(get(0));
            for (int i = 1; i < len; i++) {
                out.print(sep);
                out.quote(get(i));
            }
        }
        out.print(right);
    }

    /**
     *
     */
    public String toString() {
        return E.toString(this);
    }
}
