package net.captp.jcomm;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.elib.ref.Ref;
import org.erights.e.elib.serial.Reviver;

/**
 * Used to specialize the UnserializationStream for receiving state serialized
 * over a CapTP connection.
 *
 * @author Mark S. Miller
 */
class CapTPReviver extends Reviver {

    /**
     * The connection over which we are communicating
     */
    private final CapTPConnection myConn;

    /**
     *
     */
    CapTPReviver(CapTPConnection conn) {
        myConn = conn;
    }

    /**
     * Replace ObjectRefDescs in the input stream with the object reference
     * they {@link ObjectRefDesc#dereference(CapTPConnection) dereference} to.
     */
    public Object substitute(Object ref) {
        ref = Ref.resolution(ref);
        if (ref instanceof ObjectRefDesc) {
            return ((ObjectRefDesc)ref).dereference(myConn);
        } else {
            return ref;
        }
    }
}
