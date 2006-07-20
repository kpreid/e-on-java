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
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.sealing.Amplifiable;
import org.erights.e.elib.sealing.Brand;
import org.erights.e.elib.sealing.SealedBox;
import org.erights.e.elib.serial.PassByProxy;
import org.erights.e.elib.serial.PersistentKeyHolder;

import java.io.IOException;


/**
 * A ROMap gives read-only access to an underlying potentially mutable map.
 *
 * @author Mark S. Miller
 */
class ROMap extends EMap implements Amplifiable, PassByProxy {

    private final FlexMap myPrecious;

    /**
     * Callers *MUST* be sure to hand in the only reference to this map.
     */
    ROMap(FlexMap precious) {
        myPrecious = precious;
    }

    /**
     *
     */
    public ConstMap snapshot() {
        return myPrecious.snapshot();
    }

    /**
     *
     */
    public EMap readOnly() {
        return this;
    }

    /**
     *
     */
    public FlexMap diverge(Class keyType, Class valueType) {
        return myPrecious.diverge(keyType, valueType);
    }

    /**
     *
     */
    public ESet domain() {
        return ROSet.make(this);
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
    public Object fetch(Object key, Thunk insteadThunk) {
        return myPrecious.fetch(key, insteadThunk);
    }

    /**
     *
     */
    public Object getKeys(Class type) {
        return myPrecious.getKeys(type);
    }

    /**
     *
     */
    public Object getValues(Class type) {
        return myPrecious.getValues(type);
    }

    /**
     *
     */
    public Object[] getPair() {
        return myPrecious.getPair();
    }

    /**
     *
     */
    public Object[] getPair(Class keyType, Class valueType) {
        return myPrecious.getPair(keyType, valueType);
    }

    /**
     *
     */
    public Class keyType() {
        return myPrecious.keyType();
    }

    /**
     *
     */
    public Class valueType() {
        return myPrecious.valueType();
    }

    /**
     * Divulges itself only to a holder of
     * {@link org.erights.e.elib.serial.PersistentKeyHolder#THE_UNSEALER}.
     * <p/>
     * XXX Should provide an optional creation-time parameter of a Sealer to
     * use in addition (or instead?) as a secret divulging channel.
     *
     * @return
     */
    public SealedBox __optSealedDispatch(Brand brand) {
        if (PersistentKeyHolder.THE_BRAND == brand) {
            Object[] uncall = {myPrecious, "readOnly", E.NO_ARGS};
            return PersistentKeyHolder.THE_SEALER.seal(uncall);
        } else {
            return null;
        }
    }

    /**
     * <em>Not</em> transparently forwarded. Rather, prints as <pre>
     *      "[a => b, ...].diverge().readOnly()"
     * </pre>
     */
    public void __printOn(TextWriter out) throws IOException {
        out.print(myPrecious, ".readOnly()");
    }
}
