package org.erights.e.elib.serial;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.ref.Ref;
import org.erights.e.elib.ref.StemCell;
import org.erights.e.elib.tables.ConstList;

import java.io.IOException;
import java.io.ObjectStreamException;

/**
 * A RemoteCall unserializes into the result of <pre>
 * <p/>
 *     recipient.verb(args...)
 * </pre>
 * allowing arbitrary execution *during* deserialization, but only within
 * capability constraints. All pointers serialized as pointers to the
 * RemoteCall will be deserialized as pointing to the result of the call, or,
 * if the unserializtion of the RemoteCall is in progress, to a promise for
 * this result. This may violate static types or other constraints, and
 * cause deserialization to fail.
 * <p/>
 * If the call throws a problem, then the resolution is a reference broken by
 * that problem.
 *
 * @author Mark S. Miller
 */
public class RemoteCall extends StemCell {

    static private final long serialVersionUID = 3703520454697636174L;

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
    public RemoteCall(Object recipient,
                      String verb,
                      Object[] args) {
        myRecipient = recipient;
        myVerb = verb;
        myArgs = args;
    }

    /**
     * Invoked directly by the ObjectInputStream following desrialization of
     * the entire RemoteCall to get the object to use instead.
     */
    private Object readResolve() throws ObjectStreamException {
        Object result;
        try {
            result = E.callAll(myRecipient, myVerb, myArgs);
        } catch (Throwable problem) {
            result = Ref.broken(problem);
        }
        myOptResolver.resolve(result);
        return result;
    }

    /**
     * If <tt>candidate</tt> is a RemoteCall, return the uncall triple it
     * represents.
     * <p/>
     * Otherwise, return null.
     */
    static public Object[] optUncall(Object candidate) {
        if (!(candidate instanceof RemoteCall)) {
            return null;
        }
        RemoteCall self = (RemoteCall)candidate;
        Object[] result = {self.myRecipient,
                           self.myVerb,
                           self.myArgs};
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
        out.print("RemoteCall(", myRecipient, " <- ", myVerb);
        ConstList.fromArray(myArgs).printOn("(", ", ", "))", out);
    }
}
