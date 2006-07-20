package org.erights.e.elib.serial;

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

import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.ref.StemCell;
import org.erights.e.elib.tables.ConstList;

import java.io.IOException;
import java.io.ObjectStreamException;

/**
 * A RemoteDelivery unserializes into the result of <pre>
 * <p/>
 *     recipient <- verb(args...)
 * </pre>
 * allowing arbitrary execution *after* deserialization, but only within
 * capability constraints. All pointers serialized as pointers to the
 * RemoteDelivery will be deserialized as pointing to the promise for the
 * result of the send. This may violate static types or other constraints,
 * and cause deserialization to fail.
 *
 * @author Mark S. Miller
 */
public class RemoteDelivery extends StemCell {

    static private final long serialVersionUID = -1702614205914869122L;

    /**
     * @serial receives the message
     */
    private final Object myRecipient;

    /**
     * @serial the message name (selector, method name)
     */
    private final String myVerb;

    /**
     * @serial the message arguments
     */
    private final Object[] myArgs;

    /**
     * Descibe an invocation to be run on the other side of a serialization
     * barrier of some sort.
     *
     * @param recipient receives the message
     * @param verb      the message name (selector, method name)
     * @param args      the message arguments
     */
    public RemoteDelivery(Object recipient,
                          String verb,
                          Object[] args) {
        myRecipient = recipient;
        myVerb = verb;
        myArgs = args;
    }

    /**
     * Invoked directly by the ObjectInputStream following desrialization of
     * the entire RemoteDelivery to get the object to use instead.
     */
    private Object readResolve() throws ObjectStreamException {
        Object result = E.sendAll(myRecipient, myVerb, myArgs);
        myOptResolver.resolve(result);
        return result;
    }

    /**
     *
     * @param out
     * @throws IOException
     */
    public void __printOn(TextWriter out) throws IOException {
        if (isResolved()) {
            Object target = resolution();
            if (this != target) {
                out.print(target);
                return;
                // If this == target, then this is a made StemCell, which we
                // handle below.
            }
        }
        out.print("RemoteDelivery(", myRecipient, " <- ", myVerb);
        ConstList.fromArray(myArgs).printOn("(", ", ", "))", out);
    }
}
