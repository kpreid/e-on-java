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
import org.erights.e.elib.serial.RemoteCall;

import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;

/**
 * @author Mark S. Miller
 */
class ConstMapImpl extends ConstMap {

    static private final long serialVersionUID = -2814488931279192860L;

    /**
     * This class should never be directly unserialized, so this instance
     * variable need not be declared serial.
     */
    private final transient FlexMap myTable;

    /**
     * Callers *MUST* be sure to hand in the only reference to this map.
     */
    ConstMapImpl(FlexMap map) {
        myTable = map;
    }

    /**
     * Unserialzes to a promise for 'ConstMapMaker.fromColumns(key, values)'
     */
    private Object writeReplace() throws ObjectStreamException {
        Object[] args = {getKeys(), getValues()};
        return new RemoteCall(GetMaker(), "fromColumns", args);
    }

    /**
     *
     */
    public FlexMap diverge(Class keyType, Class valueType) {
        return myTable.diverge(keyType, valueType);
    }

    /**
     *
     */
    public int size() {
        return myTable.size();
    }

    /**
     *
     */
    public void iterate(AssocFunc func) {
        myTable.iterate(func);
    }

    /**
     *
     */
    public Object fetch(Object key, Thunk insteadThunk) {
        return myTable.fetch(key, insteadThunk);
    }

    /**
     *
     */
    public Object getKeys(Class type) {
        return myTable.getKeys(type);
    }

    /**
     *
     */
    public Object getValues(Class type) {
        return myTable.getValues(type);
    }

    /**
     *
     */
    public Class keyType() {
        return myTable.keyType();
    }

    /**
     *
     */
    public Class valueType() {
        return myTable.valueType();
    }

    /* We can't be instantiated directly. The sender should have used ConstMap.fromColumns() instead. */
    private void readObject(ObjectInputStream stream) throws NotSerializableException {
        throw new NotSerializableException("ConstMapImpl must be serialized specially.");
    }
}
