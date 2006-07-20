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

import org.erights.e.elib.oldeio.EPrintable;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.serial.Persistent;

import java.io.IOException;

/**
 * A wrapper around an EMap, whose set-elements are the keys of the EMap.
 * <p/>
 * XXX To be re-rationalized when we deprecate mutable collections. But
 * introduced for now in order to remove 'Set' and its bretheren from Dean's
 * transformer.
 *
 * @author Mark S. Miller
 */
public abstract class ESet implements EPrintable, Persistent, EIteratable {

    static final long serialVersionUID = -8027825640033613130L;

    /**
     * XXX Should build an ESet directly from a KeyColumn rather than an EMap.
     */
    final EMap myMap;

    /**
     * Only subclasses within the package
     */
    ESet(EMap map) {
        myMap = map;
    }

    /**
     * Returns a ConstSet whose state is a snapshot of the state of this
     * set at the time of the snapshot() request. A ConstSet returns
     * itself.
     */
    public ConstSet snapshot() {
        return ConstSet.make(myMap.snapshot());
    }

    /**
     * Returns a read-only facet on this set. Someone holding this facet
     * may see changes, but they cannot cause them.
     */
    public ESet readOnly() {
        return ROSet.make(myMap);
    }

    /**
     * Returns a FlexSet whose initial state is a snapshot of the state of
     * this set at the time of the diverge() request.
     * <p/>
     * Further changes to the original and/or the new set are independent --
     * they diverge.
     * <p/>
     * The new set is constrained to only hold object of type 'type'.
     * XXX 'type' should be declared as a Guard rather than Class.
     */
    public FlexSet diverge(Class type) {
        return FlexSet.make(myMap.diverge(type, Void.class));
    }

    /**
     * 'type' default to Object.class
     */
    public FlexSet diverge() {
        return diverge(Object.class);
    }

    /**
     * Is the candidate the same as any of the elements of the set?
     */
    public boolean contains(Object candidate) {
        return myMap.maps(candidate);
    }

    /**
     * Do these sets have any elements in common?
     */
    public boolean intersects(ESet other) {
        return myMap.intersects(other.myMap);
    }

    /**
     * How many entries are in the set?
     */
    public int size() {
        return myMap.size();
    }

    /**
     * Call 'func' with each index-value pair in the set, in order.
     */
    public void iterate(AssocFunc func) {
        ConstList.fromArray(getElements()).iterate(func);
    }

    /**
     * Returns the union of the sets.
     * <p/>
     * If sets intersect, then if strict, throw an exception.
     * <p/>
     * In the order, the 'behind' keys come first in their original
     * order, then the receiver's remaining keys in their original
     * order.
     */
    public ConstSet or(ESet behind, boolean strict) {
        return ConstSet.make(myMap.or(behind.myMap, strict));
    }

    /**
     * Defaults to not strict.
     */
    public ConstSet or(ESet behind) {
        return or(behind, false);
    }

    /**
     * The intersection of the sets.
     * <p/>
     * The order in the intersection is taken from the smaller
     * of the original two. If they're the same size, then the
     * receiver's order is used.
     */
    public ConstSet and(ESet mask) {
        return ConstSet.make(myMap.and(mask.myMap));
    }

    /**
     * The subset of this set not in 'mask'. <p>
     * <p/>
     * The order is the order of the receiver, as modified by removal
     * of the elements in mask in mask's order.
     */
    public ConstSet butNot(ESet mask) {
        return ConstSet.make(myMap.butNot(mask.myMap));
    }

    /**
     * Defaults to an array of elementType()
     */
    public Object getElements() {
        return getElements(elementType());
    }

    /**
     * Returns a divergent array-of-type of all the values in order.
     * <p/>
     * XXX Should elementType be a Guard rather than a Class?
     */
    public Object getElements(Class elementType) {
        return myMap.getKeys(elementType);
    }

    /**
     * Returns a ConstSet just like this one, except containing newElement.
     * <p/>
     * The order is the same as the original; if 'newElement' is
     * new, it is added to the end of the order. <p>
     *
     * @param newElement nullOk;
     */
    public ConstSet with(Object newElement) {
        return ConstSet.make(myMap.with(newElement, null));
    }

    /**
     * Returns a ConstSet just like this one, except that there is no
     * ConstSet for 'key'. The order is the same as the original,
     * except that if 'key' was in the original, the last key in the
     * ordering is moved into its place (as in the standard removal
     * spec). <p>
     * <p/>
     * This is currently horribly inefficient. Can be made efficient by
     * using backward deltas.
     *
     * @param element nullOk;
     */
    public ConstSet without(Object element) {
        return ConstSet.make(myMap.without(element));
    }

    /**
     * Returns a snapshot of this set, but reordered so the elements
     * are in ascending order.
     */
    public ConstSet sort() {
        return sort(SimpleCompFunc.THE_ONE);
    }

    /**
     * Returns a snapshot of this set, but reordered so the elements
     * are in ascending order according to func.
     */
    public ConstSet sort(CompFunc func) {
        return ConstSet.make(myMap.sortKeys(func));
    }

    /**
     * All elements of this set must be of this type.
     * <p/>
     * XXX This should return a Guard rather than a Class
     */
    public Class elementType() {
        return myMap.keyType();
    }

    /**
     * Onto out, print 'left' element0 'sep' ... 'right'
     */
    public void printOn(String left,
                        String sep,
                        String right,
                        TextWriter out)
      throws IOException {
        ConstList.fromArray(getElements()).printOn(left, sep, right, out);
    }

    /**
     *
     */
    public String toString() {
        return E.toString(this);
    }
}
