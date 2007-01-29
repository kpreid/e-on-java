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
import org.erights.e.elib.util.ArityMismatchException;

import java.io.IOException;
import java.lang.reflect.Array;


/**
 * A modifiable map.
 *
 * @author Mark S. Miller
 */
public abstract class FlexMap extends EMap implements PassByProxy {

    static private final long serialVersionUID = -1291723173416949415L;

    /**
     * Only subclasses within the package
     */
    FlexMap() {
    }

    /**
     *
     */
    public ConstMap snapshot() {
        return new ConstMapImpl((FlexMap)clone());
    }

    /**
     *
     */
    public EMap readOnly() {
        return new ROMap(this);
    }

    /**
     *
     */
    public ESet domain() {
        return ROSet.make(this);
    }

    /**
     * Like 'put' of three arguments, but defaults 'strict' to false.
     */
    public void put(Object key, Object value) {
        put(key, value, false);
    }

    /**
     * Causes 'key' to map to 'value'. If 'strict' is false (the default), this
     * will overwrite a previous value if necessary. If 'strict' is true, this
     * only succeeds if there is not already an association for 'key' in the
     * map. If 'strict' is true and there is an already an association, even to
     * the same value, this throws an Exception instead (XXX currently an
     * IllegalArgumentException) and leaves the map unmodified. <p>
     * <p/>
     * Unlike Dictionary, this doesn't return the old value. If you want it,
     * use 'get' first. <p>
     * <p/>
     * If the key is overwritten, then the key order is unchanged. If the key
     * is novel, it's added to the end of the order.
     *
     * @throws NotSettledException if the key is not settled
     * @see org.erights.e.elib.ref.Ref#isSettled
     */
    public abstract void put(Object key, Object value, boolean strict);

    /**
     * Defaults 'strict' to false
     */
    public void putAll(EMap other) {
        putAll(other, false);
    }

    /**
     * Add all the associations of 'other' to this map. If there's a conflict,
     * blow up if we're strict. Otherwise overwrite. <p>
     * <p/>
     * The order of the original keys is unchanged. This order is followed by
     * the novel keys in their order in 'other'.
     */
    public void putAll(EMap other, boolean strict) {
        Object[] pair = other.getPair();
        Object keys = pair[0];
        Object vals = pair[1];
        int len = Array.getLength(keys);
        for (int i = 0; i < len; i++) {
            put(Array.get(keys, i), Array.get(vals, i), strict);
        }
    }

    /**
     * Defaults 'strict' to false
     */
    public void removeKey(Object key) {
        removeKey(key, false);
    }

    /**
     * Removes the given key (or its equivalent, according to the equal
     * function) from the collection. If 'strict' is false (the default), this
     * does nothing if 'key' is not currently a key in the collection. If
     * 'strict' is true and 'key' isn't already there to be removed, this
     * throws an Exception (XXX currently an IllegalArgumentException). <p>
     * <p/>
     * Unlike Dictionary, this does not return the old value. If you want this
     * for a FlexMap, use 'get' first. <p>
     * <p/>
     * If 'key' wasn't in the table, the table (including its order) is
     * unmodified. Otherwise, the last key in the table is moved in the
     * ordering to take the place of the removed 'key'.
     *
     * @param key the key to remove
     */
    public abstract void removeKey(Object key, boolean strict);

    /**
     * defaults to not strict
     */
    public void removeKeys(EMap mask) {
        removeKeys(mask, false);
    }

    /**
     * Remove from this map all associations whose key is in 'mask'. <p>
     * <p/>
     * The order is the original order modified by successive removals of keys
     * in mask's order.
     */
    public void removeKeys(EMap mask, boolean strict) {
        Object keys = mask.getKeys();
        int sz = Array.getLength(keys);
        for (int i = 0; i < sz; i++) {
            removeKey(Array.get(keys, i), strict);
        }
    }

    /**
     * Removes all associations from this map, leaving this map empty. <p>
     * <p/>
     * Rather than doing a write-fault (which would make a private copy to be
     * immediately dropped) this decrements the sharing count and
     * re-initializes.
     */
    public abstract void removeAll();


    /**
     * Arguments defaults to keyType() and valueType().
     * @noinspection CloneDoesntCallSuperClone
     */
    public Object clone() {
        return diverge(keyType(), valueType());
    }

    /**
     * A FlexMap is unconditionally transparent
     *
     * @return
     */
    public Object[] __optUncall() {
        Object[] result = {snapshot(), "diverge", E.NO_ARGS};
        return result;
    }

    /**
     * Prints using E language notation
     */
    public void __printOn(TextWriter out) throws IOException {
        if (0 == size()) {
            out.print("[].asMap().diverge()");
        } else {
            printOn("[", " => ", ", ", "].diverge()", out);
        }
    }


    /**
     *
     */
    static public FlexMap make() {
        return new FlexMapImpl();
    }

    /**
     *
     */
    static public FlexMap make(int capacity) {
        return new FlexMapImpl(capacity);
    }

    /**
     *
     */
    static public FlexMap fromTypes(Class keyType, Class valType) {
        return new FlexMapImpl(keyType, valType);
    }

    /**
     *
     */
    static public FlexMap fromTypes(Class keyType,
                                    Class valType,
                                    int capacity) {
        return new FlexMapImpl(keyType, valType, capacity);
    }

    /**
     * @see #interning(Class,int)
     */
    static public FlexMap interning(Class valType) {
        return new FlexMapImpl(String.class, valType);
    }

    /**
     * The resulting table should optimize for the assumption that keys are
     * typically interned, but should work regardless.
     * <p/>
     * A good strategy would be to use an IdentityKeyColumn internally, and
     * first try looking up the presented key by identity. If this fails,
     * intern the key and try again. Only interned keys would be stored. XXX We
     * currently don't do any such optimization.
     * <p/>
     * Were we to do this optimization, the interned keys may not ever be
     * garbage collected. One would hope that Java's interning table were weak,
     * but no where does it state this in the spec.
     */
    static public FlexMap interning(Class valType, int capacity) {
        return new FlexMapImpl(String.class, valType, capacity);
    }

    /**
     * Helps with a literal pattern, an array of keys and a matching array of
     * values, as gotten from getColumn(). The new map has the same order.
     */
    static FlexMap fromColumns(ConstList keys, ConstList values)
      throws ArityMismatchException {
        int size = keys.size();
        int vsize = values.size();
        if (size != vsize) {
            throw new ArityMismatchException(size + " vs " + vsize);
        }
        Class kType = keys.valueType();
        Class vType = values.valueType();
        FlexMap result = new FlexMapImpl(kType, vType, size);
        for (int i = 0; i < size; i++) {
            result.put(keys.get(i), values.get(i), true);
        }
        return result;
    }

    /**
     * Helps with another convenient literal pattern, an array of key-element
     * pairs. Each key-element pair is put into the map in order.
     */
    static public FlexMap fromPairs(Object[][] pairs, boolean strict) {
        FlexMap result = make(pairs.length);
        for (int i = 0; i < pairs.length; i++) {
            Object[] pair = pairs[i];
            if (2 != pair.length) {
                throw new IllegalArgumentException("must be a pair");
            }
            result.put(pair[0], pair[1], strict);
        }
        return result;
    }
}
