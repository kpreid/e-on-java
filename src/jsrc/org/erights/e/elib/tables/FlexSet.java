package org.erights.e.elib.tables;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.serial.PassByProxy;

import java.io.IOException;

/**
 * @author Mark S. Miller
 */
public class FlexSet extends ESet implements PassByProxy {

    static private final long serialVersionUID = -3212198606865527309L;

    /**
     *
     */
    private FlexSet(FlexMap map) {
        super(map);
    }

    /**
     *
     */
    static public FlexSet make() {
        return new FlexSet(FlexMap.make());
    }

    /**
     *
     */
    static FlexSet make(FlexMap map) {
        return new FlexSet(map);
    }

    /**
     *
     */
    static public FlexSet fromType(Class type) {
        return new FlexSet(FlexMap.fromTypes(type, Void.class));
    }

    /**
     *
     */
    static public FlexSet fromType(Class type, int capacity) {
        return new FlexSet(FlexMap.fromTypes(type, Void.class, capacity));
    }

    /**
     *
     */
    private FlexMap getMap() {
        return (FlexMap)myMap;
    }

    /**
     * Prints as an E list send the message 'asSet() diverge()'
     */
    public void __printOn(TextWriter out) throws IOException {
        printOn("[", ", ", "].asSet().diverge()", out);
    }

    /**
     * 'strict' defaults to false
     */
    public void addElement(Object newElement) {
        addElement(newElement, false);
    }

    /**
     * Adds newElement to the set.
     * <p/>
     * If it's already there, then if strict, throw an exception.
     */
    public void addElement(Object newElement, boolean strict) {
        getMap().put(newElement, null, strict);
    }

    /**
     * 'strict' defaults to false
     */
    public void addAll(EIteratable other) {
        addAll(other, false);
    }

    /**
     *
     */
    public void addAll(EIteratable other, final boolean strict) {
        other.iterate(new AssocFunc() {

            public void run(Object ignored, Object element) {
                addElement(element, strict);
            }
        });
    }

    /**
     * 'strict' defaults to false
     */
    public void remove(Object element) {
        remove(element, false);
    }

    /**
     * Removes the element from the set.
     * <p/>
     * If it isn't there to be removed, then if strict, throw an exception.
     */
    public void remove(Object element, boolean strict) {
        getMap().removeKey(element, strict);
    }

    /**
     * 'strict' defaults to false
     */
    public void removeAll(EIteratable other) {
        removeAll(other, false);
    }

    /**
     *
     */
    public void removeAll(EIteratable other, final boolean strict) {
        other.iterate(new AssocFunc() {
            public void run(Object ignored, Object element) {
                remove(element, strict);
            }
        });
    }
}
