package org.erights.e.elib.tables;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.serial.PassByConstruction;

import java.io.IOException;

/**
 * @author Mark S. Miller
 */
public class ConstSet extends ESet implements PassByConstruction, Selfless {

    static private final long serialVersionUID = 139247792984994555L;

    /**
     *
     */
    static public final ConstSet EmptySet = new ConstSet(ConstMap.EmptyMap);

    /**
     *
     */
    private ConstSet(ConstMap map) {
        super(map);
    }

    /**
     *
     */
    static public ConstSet make(ConstMap map) {
        return new ConstSet(map);
    }

    /**
     * Uses 'elementsList asSet()', where the elementsList is as returned by
     * getElements.
     */
    public Object[] getSpreadUncall() {
        Object[] result = {getElements(), "asSet"};
        return result;
    }

    /**
     *
     */
    public ConstSet snapshot() {
        return this;
    }

    /**
     *
     */
    public ESet readOnly() {
        return this;
    }

    /**
     * Prints as an E list send the message '.asSet()'
     */
    public void __printOn(TextWriter out) throws IOException {
        printOn("[", ", ", "].asSet()", out);
    }

    /**
     *
     */
    private ConstMap getMap() {
        return (ConstMap)myMap;
    }

    /**
     * This method enables E's magnitude comparison operators (&lt;, &lt;=,
     * &lt;=&gt;, &gt;=, &gt;) to express subset-ness of these sets.
     * <p/>
     * If this set is a strict subset of other's, return -1.0. If this set has
     * the same elements as other, return 0.0. If this set is a strict superset
     * of other, return 1.0. Otherwise return NaN.
     */
    public double op__cmp(ConstSet other) {
        return getMap().op__cmp(other.getMap());
    }
}
