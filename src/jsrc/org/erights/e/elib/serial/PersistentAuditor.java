package org.erights.e.elib.serial;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.elib.ref.Ref;
import org.erights.e.elib.slot.BaseAuditor;
import org.erights.e.elib.util.OneArgFunc;
import org.erights.e.elib.oldeio.TextWriter;

import java.io.IOException;

/**
 * The guard (and auditor) known as "Persistent" and the auditor (and guard)
 * known as "PersistentAuditor".
 * <p/>
 * PersistentAuditor.THE_ONE is the auditor (and guard) known as
 * PersistentAuditor and placed in the universalScope, since it represents no
 * authority. It is the result of calling the PersistentAuditor constructor
 * with an empty map as an unscope. (XXX Even this map should include bindings
 * for the objects in the universalScope. Actually, Ref.isPersistent() should
 * know about those objects, in order to report that they are persistent.)
 * <p/>
 * The guard (and auditor) known as Persistent is in the privileged scope and
 * knows about the unscope from that same privileged scope. It reports that
 * any unscope-key is persistent. Since this is a mutable set, this object
 * conveys the authority to sense mutations to that set.
 *
 * @author Mark S. Miller
 */
public class PersistentAuditor extends BaseAuditor {

    static public final PersistentAuditor THE_ONE =
      new PersistentAuditor();

    /**
     *
     */
    private PersistentAuditor() {
    }

    /**
     * Coerces shortSpecimen to be Persistent.
     * <p/>
     * If the shortSpecimen can't be coerced, exit according to optEjector.
     */
    protected Object tryCoerceR(Object shortSpecimen, OneArgFunc optEjector) {
        if (Ref.isPersistent(shortSpecimen)) {
            return shortSpecimen;
        } else {
            return super.tryCoerceR(shortSpecimen, optEjector);
        }
    }

    /**
     *
     * @param out
     * @throws java.io.IOException
     */
    public void __printOn(TextWriter out) throws IOException {
        out.print("Persistent");
    }
}
