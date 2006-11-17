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

import org.erights.e.develop.exception.ExceptionMgr;
import org.erights.e.elib.base.Thunk;
import org.erights.e.elib.base.ValueThunk;
import org.erights.e.elib.oldeio.EPrintable;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.serial.Persistent;
import org.erights.e.elib.util.ArityMismatchException;

import java.io.IOException;
import java.lang.reflect.Array;

/**
 * A EMap is a finite single-valued map from keys to values. Equivalently, it
 * can be considered a finite set of pairs, where each is a pair of a key and a
 * value, and no two pairs have the same key. 'EMap' is a query-only interface
 * that's agnostic about whether the data can change and whether a EMap can be
 * cast to an object by which to change it. 'EMap' is also agnostic about the
 * basis for equality between tables. Subtypes do pin these issues down: <p>
 * <p/>
 * ConstMap guarantees immutability, uses value-based equality, and can be
 * transparently passed-by-copy over the network. <p>
 * <p/>
 * FlexMap extends EMap with mutation operations. <p>
 * <p/>
 * Based on java.util.Dictionary, but not polymorphic with it since EMaps don't
 * satisfy Dictionary's contract. In particular, Dictionaries explicitly
 * disallow nulls as keys or values, whereas EMaps explicitly allow them. <p>
 * <p/>
 * Actually, for the sake of determinism, an EMap is a sequence of key-value
 * pairs, where this sequence is the enumeration order, and is derived
 * deterministically from the operations that resulted in this map. Putting in
 * a new key adds it to the end of the sequence. Removing a key causes the last
 * key to take the place of the removed key in the sequence. Other operations,
 * like or(), specify how they determine the resulting sequence. <p>
 * <p/>
 * It is normally considered bad style to attach meaning to the sequence, but
 * is in the contract, so you may if you wish. If you do, document that, since
 * your readers should usually be able to make the no-meaning assumption.
 *
 * @author Mark S. Miller
 */
public abstract class EMap implements EPrintable, Persistent, EIteratable {

    static private final long serialVersionUID = -7066420784218105992L;

    /**
     * This object must never be stored as a value in a map. It's used in
     * <tt>insteadThunk</tt> to fetch/2 to simultaneously do a maps() test.
     */
    static private final Object ThePumpkin = new Object();


    /**
     * Only subclasses within the package
     */
    EMap() {
    }

    /**
     * Returns a ConstMap whose state is a snapshot of the state of this map at
     * the time of the snapshot() request. A ConstMap returns itself.
     */
    public abstract ConstMap snapshot();

    /**
     * Returns a read-only facet on this map. Someone holding this facet may
     * see changes, but they cannot cause them.
     */
    public abstract EMap readOnly();

    /**
     * Returns a FlexMap whose initial state is a snapshot of the state of this
     * map at the time of the diverge() request.
     * <p/>
     * Further changes to the original and/or the new map are independent --
     * they diverge.
     * <p/>
     * The new map is constrained to only hold associations from 'keyType' to
     * 'valueType'. XXX keyType and valueType should be declared as Guards
     * rather than Classes.
     */
    public FlexMap diverge(Class keyType, Class valueType) {
        FlexMap result = FlexMap.fromTypes(keyType, valueType, size());
        result.putAll(this);
        return result;
    }

    /**
     * 'keyType' and 'valueType' default to Object.class
     */
    public FlexMap diverge() {
        return diverge(Object.class, Object.class);
    }

    /**
     * What value does 'key' map to?
     *
     * @param key nullOk;
     * @return nullOk;
     * @throws IndexOutOfBoundsException if key doesn't map to anything.
     * @see org.erights.e.elib.ref.Ref#isSettled
     */
    public Object get(Object key) throws IndexOutOfBoundsException {
        Object result = fetch(key, new ValueThunk(ThePumpkin));
        if (ThePumpkin == result) {
            throw new IndexOutOfBoundsException(E.toQuote(key) + " not found");
        } else {
            return result;
        }
    }

    /**
     * Used by the expansion of map-patterns.
     *
     * @param key nullOk;
     * @return nullOk; If the key is found, returns a pair of the corresponding
     *         value and a ConstMap that's a snapshot of this one, but {@link
     *         #without} that key-value association. If the key is not found,
     *         optExtract/1 returns null.
     */
    public Object[] optExtract(Object key) {
        Object value = fetch(key, new ValueThunk(ThePumpkin));
        if (ThePumpkin == value) {
            return null;
        } else {
            Object[] result = {value, without(key)};
            return result;
        }
    }

    /**
     * Like optExtract/1, but allows one to provide a defaultValue for missing
     * keys.
     *
     * @param key nullOk;
     * @return If the key is found, returns a pair of the corresponding value
     *         and a ConstMap that's a snapshot of this one, but {@link
     *         #without} that key-value association. If the key is not found,
     *         extract/2 returns a pair of defaultValue and this map.
     */
    public Object[] extract(Object key, Object defaultValue) {
        Object value = fetch(key, new ValueThunk(ThePumpkin));
        if (ThePumpkin == value) {
            Object[] result = {defaultValue, this};
            return result;
        } else {
            Object[] result = {value, without(key)};
            return result;
        }
    }

    /**
     * Is the candidate the same as any of this table's values?
     */
    public boolean contains(Object candidate) {
        return ConstList.fromArray(getValues()).contains(candidate);
    }

    /**
     * Do these tables have any keys in common?
     */
    public boolean intersects(EMap other) {
        Object[] keys = (Object[])getKeys(Object.class);
        for (int i = 0, max = keys.length; i < max; i++) {
            if (other.maps(keys[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * How many entries are in the table?
     */
    public abstract int size();

    /**
     * Call 'func' with each key-value pair in the table, in order.
     */
    public void iterate(AssocFunc func) {
        Object[] pair = getPair();
        Object keys = pair[0];
        Object vals = pair[1];
        int len = Array.getLength(keys);
        for (int i = 0; i < len; i++) {
            func.run(Array.get(keys, i), Array.get(vals, i));
        }
    }

    /**
     * Returns a map that has the union of the domains of this map.
     * <p/>
     * If both maps have keys in common, then if strict, throw an exception.
     * Otherwise, take the value from the receiver (the left-hand operand). We
     * can think of the receiver as being in front of and occluding 'behind'.
     * <p/>
     * In the order, the 'behind' keys come first in their original order, then
     * the receiver's remaining keys in their original order.
     */
    public ConstMap or(EMap behind, boolean strict) {
        if (size() == 0) {
            return behind.snapshot();
        } else if (behind.size() == 0) {
            return snapshot();
        }
        FlexMap flex = behind.diverge();
        flex.putAll(this, strict);
        return flex.snapshot();
    }

    /**
     * Defaults to not strict.
     */
    public ConstMap or(EMap behind) {
        return or(behind, false);
    }

    /**
     * The subset of this map whose keys are keys of 'mask'. <p>
     * <p/>
     * The order of keys in the intersection is taken from the smaller of the
     * original two. If they're the same size, then the receiver's order is
     * used.
     */
    public ConstMap and(EMap mask) {
        EMap bigger;
        EMap smaller;
        if (size() > mask.size()) {
            bigger = this;
            smaller = mask;
        } else {
            smaller = this;
            bigger = mask;
        }
        Object keys = smaller.getKeys();
        int len = Array.getLength(keys);
        if (0 == len) {
            return ConstMap.EmptyMap;
        }
        FlexMap flex = FlexMap.make(len);
        for (int i = 0; i < len; i++) {
            Object key = Array.get(keys, i);
            if (bigger.maps(key)) {
                flex.put(key, get(key));
            }
        }
        return flex.snapshot();
    }


    /**
     * The subset of this map whose keys are not keys of 'mask'. <p>
     * <p/>
     * The order is the order of the receiver, as modified by removal of the
     * keys in mask in mask's order.
     */
    public ConstMap butNot(EMap mask) {
        if (size() == 0) {
            return ConstMap.EmptyMap;
        } else if (mask.size() == 0) {
            return snapshot();
        }
        FlexMap flex = diverge();
        flex.removeKeys(mask);
        return flex.snapshot();
    }

    /**
     * Is there a mapping for 'key'?
     *
     * @param key nullOk;
     */
    public boolean maps(Object key) {
        return ThePumpkin != fetch(key, new ValueThunk(ThePumpkin));
    }

    /**
     * What value does 'key' map to?  Returns <tt>insteadThunk()</tt> if key
     * doesn't map to anything. <p>
     *
     * @param key nullOk;
     * @return nullOk;
     * @see org.erights.e.elib.ref.Ref#isSettled
     */
    public abstract Object fetch(Object key, Thunk insteadThunk);

    /**
     * Defaults to an array of keyType()
     */
    public Object getKeys() {
        return getKeys(keyType());
    }

    /**
     * Returns a divergent array-of-type of all the keys in order.
     * <p/>
     * XXX Should 'type' be a Guard rather than a Class?
     */
    public abstract Object getKeys(Class type);

    /**
     * Returns a set providing a read-only view of the domain of this map.
     */
    public abstract ESet domain();

    /**
     * Defaults to an array of valueType()
     */
    public Object getValues() {
        return getValues(valueType());
    }

    /**
     * Returns a divergent array-of-type of all the values in order.
     * <p/>
     * XXX Should 'type' be a Guard rather than a Class?
     */
    public abstract Object getValues(Class type);

    /**
     * Returns a pair (a two element list) of the results of getKeys() and
     * getValues(). <p>
     * <p/>
     * Unlike calling them individually, by getting them both together, they
     * are guaranteed to correspond.
     */
    public Object[] getPair() {
        return getPair(keyType(), valueType());
    }

    /**
     * Returns a pair (a two element list) of the results of getKeys(keyType)
     * and getValues(valueType). <p>
     * <p/>
     * Unlike calling them individually, by getting them both together, they
     * are guaranteed to correspond. The default implementation here does just
     * call getKeys(keyType), and then calls getValues(valueType), as that is
     * fine for everything but the WeakValuesMap.
     * <p/>
     * XXX Should keyType and valueType be Guards rather than Classes?
     */
    public Object[] getPair(Class keyType, Class valueType) {
        Object keys = getKeys(keyType);
        Object[] result = {keys, getValues(valueType)};
        return result;
    }

    /**
     * Returns a ConstMap just like this one, except that 'key' maps to
     * 'newValue'. The order is the same as the original; if 'key' is new, it
     * is added to the end of the order. <p>
     * <p/>
     * This is currently horribly inefficient. Can be made efficient by using
     * backward deltas.
     *
     * @param key      nullOk;
     * @param newValue nullOk;
     */
    public ConstMap with(Object key, Object newValue) {
        FlexMap flex = diverge();
        flex.put(key, newValue);
        return flex.snapshot();
    }

    /**
     * Returns a ConstMap just like this one, except that there is no ConstMap
     * for 'key'. The order is the same as the original, except that if 'key'
     * was in the original, the last key in the ordering is moved into its
     * place (as in the standard removal spec). <p>
     * <p/>
     * This is currently horribly inefficient. Can be made efficient by using
     * backward deltas.
     *
     * @param key nullOk;
     */
    public ConstMap without(Object key) {
        FlexMap flex = diverge();
        flex.removeKey(key);
        return flex.snapshot();
    }

    /**
     * Returns a snapshot of this mapping, but reordered so the keys are in
     * ascending order.
     */
    public ConstMap sortKeys() {
        return sortKeys(SimpleCompFunc.THE_ONE);
    }

    /**
     * Returns a snapshot of this mapping, but reordered so the keys are in
     * ascending order according to func.
     */
    public ConstMap sortKeys(CompFunc func) {
        Object[] pair = getPair();
        Object oldKeys = pair[0];
        Object oldVals = pair[1];
        Number[] permutation = IndirectCompFunc.indirectSort(oldKeys, func);
        return permute(permutation, oldKeys, oldVals);
    }

    /**
     * Returns a snapshot of this mapping, but reordered so the values are in
     * ascending order.
     */
    public ConstMap sortValues() {
        return sortValues(SimpleCompFunc.THE_ONE);
    }

    /**
     * Returns a snapshot of this mapping, but reordered so the values are in
     * ascending order according to func.
     */
    public ConstMap sortValues(CompFunc func) {
        Object[] pair = getPair();
        Object oldKeys = pair[0];
        Object oldVals = pair[1];
        Number[] permutation = IndirectCompFunc.indirectSort(oldVals, func);
        return permute(permutation, oldKeys, oldVals);
    }

    /**
     *
     */
    private ConstMap permute(Number[] permutation,
                             Object oldKeys,
                             Object oldVals) {
        int len = permutation.length;
        Object newKeys = ArrayHelper.newArray(keyType(), len);
        Object newVals = ArrayHelper.newArray(valueType(), len);
        for (int i = 0; i < len; i++) {
            int oldI = permutation[i].intValue();
            ArrayHelper.arraySet(newKeys, i, Array.get(oldKeys, oldI));
            ArrayHelper.arraySet(newVals, i, Array.get(oldVals, oldI));
        }
        try {
            return ConstMap.fromColumns(ConstList.fromArray(newKeys),
                                        ConstList.fromArray(newVals));
        } catch (ArityMismatchException ame) {
            throw ExceptionMgr.asSafe(ame);
        }
    }

    /**
     * All keys in this map must be of this type.
     * <p/>
     * XXX Should this return a Guard rather than a Class?
     */
    public abstract Class keyType();

    /**
     * All values in this map must be of this type
     * <p/>
     * XXX Should this return a Guard rather than a Class?
     */
    public abstract Class valueType();

    /**
     * Onto out, print 'left' key0 'map' value0 'sep' ... 'right'
     */
    public void printOn(String left,
                        String map,
                        String sep,
                        String right,
                        TextWriter out) throws IOException {
        int sz = size();
        if (0 == sz) {
            out.print(left, right);
            return;
        }
        Object keys = getKeys();
        Object key = Array.get(keys, 0);
        out.print(left);
        out.quote(key);
        out.print(map);
        out.quote(get(key));
        for (int i = 1; i < sz; i++) {
            key = Array.get(keys, i);
            out.print(sep);
            out.quote(key);
            out.print(map);
            out.quote(get(key));
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
