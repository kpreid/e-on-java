package org.erights.e.meta.org.erights.e.elib.tables;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.develop.assertion.T;
import org.erights.e.elib.base.ClassDesc;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.EList;
import org.erights.e.elib.tables.Twine;
import org.erights.e.elib.util.OneArgFunc;

/**
 * Refines the 'coerce/2' behavior of the EList type, so that it may be useful
 * as a Guard.
 *
 * @author Mark S. Miller
 */
public class EListGuardSugar extends ClassDesc {

    static private final long serialVersionUID = 110221162661724963L;

    /**
     * @param clazz must be EList or a subtype
     */
    public EListGuardSugar(Class clazz) {
        super(clazz);
        T.require(EList.class.isAssignableFrom(clazz),
                  clazz,
                  " must be a kind of EList");
    }

    /**
     * Converts an array to a ConstList, and a String to a Twine.
     */
    protected Object subCoerceR(Object shortSpecimen, OneArgFunc optEjector) {
        if (shortSpecimen.getClass().isArray()) {
            return ConstList.fromArray(shortSpecimen);
        }
        if (shortSpecimen instanceof String) {
            return Twine.fromString((String)shortSpecimen);
        }
        return super.subCoerceR(shortSpecimen, optEjector);
    }
}
