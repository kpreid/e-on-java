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
import org.erights.e.elib.sealing.Amplifiable;
import org.erights.e.elib.sealing.SealedBox;
import org.erights.e.elib.serial.PassByProxy;
import org.erights.e.elib.serial.PersistentKeyHolder;

import java.io.IOException;

/**
 * A ROList gives read-only access to an underlying potentially mutable list.
 *
 * @author Mark S. Miller
 * @see ConstList
 * @see FlexList
 */
class ROList extends EList implements Amplifiable, PassByProxy {

    static private final long serialVersionUID = -4176032163767504933L;

    /**
     * the list we're protecting
     */
    private final FlexList myPrecious;

    /**
     *
     */
    ROList(FlexList precious) {
        myPrecious = precious;
    }

    /**
     *
     */
    public ConstList snapshot() {
        return myPrecious.snapshot();
    }

    /**
     * Just returns itself rather than forwarding
     */
    public EList readOnly() {
        return this;
    }

    /**
     *
     */
    public FlexList diverge(Class valueType) {
        return myPrecious.diverge(valueType);
    }

    /**
     *
     */
    public Object get(int index) throws IndexOutOfBoundsException {
        return myPrecious.get(index);
    }

    /**
     *
     */
    public boolean contains(Object candidate) {
        return myPrecious.contains(candidate);
    }

    /**
     *
     */
    public int size() {
        return myPrecious.size();
    }

    /**
     *
     */
    public void iterate(AssocFunc func) {
        myPrecious.iterate(func);
    }

    /**
     *
     */
    public Class valueType() {
        return myPrecious.valueType();
    }

    /**
     *
     */
    public ConstList add(Object other) {
        return myPrecious.add(other);
    }

    /**
     *
     */
    public ConstList multiply(int n) {
        return myPrecious.multiply(n);
    }

    /**
     *
     */
    public ConstMap asMap() {
        return myPrecious.asMap();
    }

    /**
     *
     */
    public ConstMap asKeys() {
        return myPrecious.asKeys();
    }

    /**
     *
     */
    public boolean includes(EList candidate) {
        return myPrecious.includes(candidate);
    }

    /**
     *
     */
    public ConstList run(int start) {
        return myPrecious.run(start);
    }

    /**
     *
     */
    public ConstList run(int start, int bound) {
        return myPrecious.run(start, bound);
    }

    /**
     *
     */
    public int indexOf1(Object candidate) {
        return myPrecious.indexOf1(candidate);
    }

    /**
     *
     */
    public int indexOf1(Object candidate, int start) {
        return myPrecious.indexOf1(candidate, start);
    }

    /**
     *
     */
    public int lastIndexOf1(Object candidate) {
        return myPrecious.lastIndexOf1(candidate);
    }

    /**
     *
     */
    public int lastIndexOf1(Object candidate, int start) {
        return myPrecious.lastIndexOf1(candidate, start);
    }

    /**
     *
     */
    public int startOf(EList candidate) {
        return myPrecious.startOf(candidate);
    }

    /**
     *
     */
    public int startOf(EList candidate, int start) {
        return myPrecious.startOf(candidate, start);
    }

    /**
     *
     */
    public int lastStartOf(EList candidate) {
        return myPrecious.lastStartOf(candidate);
    }

    /**
     *
     */
    public int lastStartOf(EList candidate, int start) {
        return myPrecious.lastStartOf(candidate, start);
    }

    /**
     * Divulges itself only to a holder of {@link PersistentKeyHolder#THE_UNSEALER}.
     * <p/>
     * XXX Should provide an optional creation-time parameter of a Sealer to
     * use in addition (or instead?) as a secret divulging channel.
     */
    public SealedBox __optSealedDispatch(Object brand) {
        if (PersistentKeyHolder.THE_BRAND == brand) {
            Object[] uncall = {myPrecious, "readOnly", E.NO_ARGS};
            return PersistentKeyHolder.THE_SEALER.seal(uncall);
        } else {
            return null;
        }
    }

    /**
     * <em>Not</em> transparently forwarded. Rather, prints as <pre>
     *      "[a, b, ...].diverge().readOnly()"
     * </pre>
     */
    public void __printOn(TextWriter out) throws IOException {
        out.print(myPrecious, ".readOnly()");
    }
}
