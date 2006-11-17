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
 * The guard and auditor known as "DeepPassByCopy".
 *
 * @author Mark S. Miller
 */
public class DeepPassByCopyAuditor extends BaseAuditor {

    static public final DeepPassByCopyAuditor THE_ONE =
      new DeepPassByCopyAuditor();

    /**
     *
     */
    private DeepPassByCopyAuditor() {
    }

    /**
     * Coerces shortSpecimen to be DeepPassByCopy, which includes types that
     * are not Java-subtypes of DeepPassByCopy.
     * <p/>
     * If shortSpecimen can't be coerced, exit according to optEjector.
     */
    protected Object tryCoerceR(Object shortSpecimen, OneArgFunc optEjector) {
        if (Ref.isDeepPassByCopy(shortSpecimen)) {
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
     * @throws java.io.IOException
     */
    public void __printOn(TextWriter out) throws IOException {
        out.print("DeepPassByCopy");
    }
}
