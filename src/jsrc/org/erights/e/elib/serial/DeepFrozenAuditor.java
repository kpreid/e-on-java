package org.erights.e.elib.serial;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.ref.Ref;
import org.erights.e.elib.slot.BaseAuditor;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.util.OneArgFunc;

import java.io.IOException;

/**
 * The guard and auditor known as "DeepFrozen".
 *
 * @author Mark S. Miller
 */
public class DeepFrozenAuditor extends BaseAuditor {

    static public final DeepFrozenAuditor THE_ONE = new DeepFrozenAuditor();

    /**
     *
     */
    private DeepFrozenAuditor() {
    }

    /**
     * Coerces shortSpecimen to be DeepFrozen, which includes types that are
     * not Java-subtypes of DeepFrozen.
     * <p/>
     * If shortSpecimen can't be coerced, exit according to optEjector.
     */
    protected Object tryCoerceR(Object shortSpecimen, OneArgFunc optEjector) {
        if (Ref.isDeepFrozen(shortSpecimen)) {
            if (shortSpecimen != null && shortSpecimen.getClass().isArray()) {
                //Because we try to pretend that arrays are PassByCopy lists,
                //we coerce it to a ConstList.
                return ConstList.fromArray(shortSpecimen);
            } else {
                return shortSpecimen;
            }
        } else {
            return super.tryCoerceR(shortSpecimen, optEjector);
        }
    }

    /**
     * @param out
     * @throws IOException
     */
    public void __printOn(TextWriter out) throws IOException {
        out.print("DeepFrozen");
    }
}
