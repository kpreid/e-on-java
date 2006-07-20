package org.erights.e.elang.scope;

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

import org.erights.e.elib.slot.Slot;

/**
 * Instances maintain the outer, object-frame, and local nouns during
 * the evaluation of an E expression.
 * <p>
 * Three groups of variables comprise the execution state of the
 *
 * @author E. Dean Tribble
 */
public class EvalContext {

//    static private final int LOCAL_COUNT = 64;

    static private final Object[] NO_FIELDS = {};

    // Idealy, this should cache no-longer-used local arrays.
    private final Object[] myLocals;

    private final Object[] myFields;

    private final Slot[] myOuters;

    /**
     *
     * @param localCount
     * @param fields
     * @param outers
     * @return
     */
    static public EvalContext make(int localCount,
                                   Object[] fields,
                                   Slot[] outers) {
        return new EvalContext(new Object[localCount], fields, outers);
    }

    /**
     *
     * @param localCount
     * @param outers
     * @return
     */
    static public EvalContext make(int localCount, Slot[] outers) {
        return new EvalContext(new Object[localCount], NO_FIELDS, outers);
    }

    /**
     *
     * @param locals
     * @param fields
     * @param outers
     */
    private EvalContext(Object[] locals,
                        Object[] fields,
                        Slot[] outers) {
        myLocals = locals;
        myFields = fields;
        myOuters = outers;
    }

    /**
     * returns an EvalContext for a new Scope that inherits from this one.
     * This new Scope has an empty set of locals.
     */
    public EvalContext extended(int localCount) {
        if (0 == localCount) {
            return this;
        }
        int len = myLocals.length;
        int newLen = len + localCount;
        Object[] newLocals = new Object[newLen];
        System.arraycopy(myLocals, 0, newLocals, 0, len);
        return new EvalContext(newLocals, myFields, myOuters);
    }

    public Object local(int index) {
        return myLocals[index];
    }

    public Object field(int index) {
        return myFields[index];
    }

    public Slot outer(int index) {
        return myOuters[index];
    }

    public Slot[] outers() {
        return myOuters;
    }

    public void initLocal(int index, Object value) {
        if (index >= myLocals.length) {
            System.err.println("Assign: " + index +
                               " within: " + myLocals.length);
        }
        myLocals[index] = value;
    }

    public void initField(int index, Object value) {
        myFields[index] = value;
    }

    public void initOuter(int index, Slot value) {
        myOuters[index] = value;
    }
}
