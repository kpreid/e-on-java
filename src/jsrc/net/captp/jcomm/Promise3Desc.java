package net.captp.jcomm;

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

import net.captp.tables.Vine;
import org.erights.e.develop.trace.Trace;
import org.erights.e.elib.tables.ConstList;

/**
 * The encoding of a RemotePromise over the wire to someone other than the
 * vat it points into.
 *
 * @author Chip Morningstar
 * @author Mark S. Miller
 */
class Promise3Desc implements ObjectRefDesc {

    static private final long serialVersionUID = -3922505236390332859L;

    private final ConstList mySearchPath;

    private final String myHostID;

    private final long myNonce;

    private final Object myOptFarVine;

    //XXX Possible SECURITY BUG: Needs a validate() method.

    /**
     * Constructor.
     */
    Promise3Desc(ConstList searchPath,
                 String hostID,
                 long nonce,
                 Object optFarVine) {
        mySearchPath = searchPath;
        myHostID = hostID;
        myNonce = nonce;
        myOptFarVine = optFarVine;
    }

    /**
     * What the other side imported (from somebody other than
     * ourselves), we dereference as a RemotePromise to the same
     * arrowhead.
     */
    public Object dereference(CapTPConnection conn) {
        if (Trace.captp.debug && Trace.ON) {
            Trace.captp.debugm(conn + ".deref " + toString());
        }
        Vine optVine = null;
        if (null != myOptFarVine) {
            optVine = new Vine(myOptFarVine);
        }
        return conn.getLookup(mySearchPath,
                              myHostID,
                              myNonce,
                              optVine);
    }

    /**
     *
     */
    public String toString() {
        return "Promise3Desc(" + mySearchPath + ",\n    "
          + myHostID.substring(0, 4) + ",\n    "
          + myNonce + ",\n    "
          + myOptFarVine + ")";
    }
}
