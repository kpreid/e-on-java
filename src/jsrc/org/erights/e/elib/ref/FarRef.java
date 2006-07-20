package org.erights.e.elib.ref;

import org.erights.e.develop.assertion.T;
import org.erights.e.elib.oldeio.TextWriter;

import java.io.IOException;

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


/**
 * A FarRef is a EProxy intended to be a Resolved Ref to a particular
 * PassByProxy object in a remote Vat. <p>
 * <p/>
 * A FarRef starts out EVENTUAL but may become BROKEN. However, it continues
 * to have whatever settled identity it was born with, and so may be used as
 * a key in EMaps (hashtables). Once a FarRef becomes BROKEN, it severs its
 * connection with its handler. A FarRef is an HONORARY Selfless object
 * (since it's not transparent -- it encapsulates its myIdentity).
 *
 * @author Mark S. Miller
 */
class FarRef extends EProxy {

    /**
     * My settled identity is according to .equals() on this settled object.
     */
    final Object myIdentity;

    /**
     *
     */
    FarRef(Object identity, EProxyHandler handler) {
        super(handler);
        myIdentity = identity;
    }

    /**
     * As an HONORARY Selfless object, my .equals() and .hashCode()
     * determine sameness. <p>
     * <p/>
     * NOTE: Uses myIdentity's .equals(), which is safe, as myIdentity must
     * be an honorary Selfless object.
     */
    public boolean equals(Object other) {
        other = Ref.resolution(other);
        if (other instanceof FarRef) {
            return myIdentity.equals(((FarRef)other).myIdentity);
        } else if (other instanceof DisconnectedRef) {
            return myIdentity.equals(((DisconnectedRef)other).myIdentity);
        } else {
            return false;
        }
    }

    /**
     * As an HONORARY Selfless object, my .equals() and .hashCode()
     * determine sameness. <p>
     * <p/>
     * NOTE: Uses myIdentity's .hashCode(), which is safe, as myIdentity must
     * be an honorary Selfless object.
     */
    public int hashCode() {
        return myIdentity.hashCode();
    }

    /**
     *
     */
    public boolean isResolved() {
        return true;
    }

    /**
     * To set me is to commit me
     */
    void setTarget(Ref newTarget) {
        if (null != myOptTarget) {
            T.fail("Already resolved");
        }
        Throwable optProblem = Ref.optProblem(newTarget);
        if (null == optProblem) {
            if (equals(newTarget)) {
                //XXX we may need to allow resolution to another FarRef
                //with the same identity.
                T.fail("Redundant FarRef identity not allowed.");
            }
            T.fail("FarRef may only be smashed: " + newTarget);
        }
        EProxyHandler handler = myOptHandler;

        myOptHandler = null;
        myOptTarget = new DisconnectedRef(optProblem, myIdentity);

        //XXX should we allow throws to propogate?
        handler.handleResolution(this);
    }

    /**
     *
     * @param out
     * @throws IOException
     */
    public void __printOn(TextWriter out) throws IOException {
        if (null == myOptTarget) {
            out.print("<Far ref>");
        } else {
            myOptTarget.__printOn(out);
        }
    }
}
