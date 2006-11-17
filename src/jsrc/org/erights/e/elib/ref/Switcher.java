package org.erights.e.elib.ref;

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

import org.erights.e.develop.assertion.T;
import org.erights.e.elib.oldeio.EPrintable;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.E;

import java.io.IOException;

/**
 * The facet of a switchable promise for switching the object being offered as
 * the resolution of the promise, or for committing to the current one. The
 * Switcher can continue switching the Ref's target until commit()ted.
 *
 * @author Mark S. Miller
 * @deprecated Currently unused, so we may decide to retire this.
 */
public class Switcher implements EPrintable {

    /**
     * Once it's done, it stops pointing at the Ref.
     */
    private Ref myOptRef;

    /**
     * sRef should still be in a swicthable state, and this should be its only
     * resolver.
     */
    Switcher(Ref sRef) {
        myOptRef = sRef;
    }

    /**
     * Queues all accumulated and future messages for delivery to target. If
     * isDone(), an exception is thrown instead.
     */
    public void setTarget(Object target) {
        if (null == myOptRef) {
            T.fail("done");
        } else {
            myOptRef.setTarget(Ref.toRef(target));
        }
    }

    /**
     * @see Resolver#isDone
     */
    public boolean isDone() {
        return null == myOptRef;
    }

    /**
     * Become done. Return false is already done.
     */
    public boolean commit() {
        if (myOptRef == null) {
            return false;
        } else {
            myOptRef.commit();
            myOptRef = null;
            return true;
        }
    }

    /**
     *
     */
    public void __printOn(TextWriter out) throws IOException {
        out.print("<");
        if (isDone()) {
            out.print("Committed");
        } else {
            out.print("Uncommitted");
        }
        out.print(" Switcher>");
    }

    /**
     *
     */
    public String toString() {
        return E.toString(this);
    }
}
