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
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.Message;
import org.erights.e.elib.prim.MirandaMethods;
import org.erights.e.elib.sealing.SealedBox;
import org.erights.e.elib.slot.Guard;
import org.erights.e.elib.tables.FlexList;
import org.erights.e.elib.vat.SendingContext;

import java.io.IOException;
import java.lang.ref.WeakReference;

/**
 * A BufferingRef, although a proper Ref, is intended for use (by composition)
 * only within other Ref implementations.
 * <p/>
 * A BufferingRef is forever EVENTUAL, and simply stores all Messages
 * asynchronously sent to it in a buffer, a FlexList of Messages, acting in the
 * role of its resolver. In other word, a buffering promise consists two
 * facets: a BufferingRef and the FlexList which stores the incoming messages.
 * The static method deliverAll() is provided for use on the buffer,
 * effectively acting as a resolver method.
 * <p/>
 * A BufferingRef points at its buffer weakly, since if no one else still has
 * ahold of the buffer, there's no reason to keep it. In this case, incoming
 * messages are silently ignored with no externally visible effect. Therefore,
 * even though the non-determinism of GC timing is visible within the
 * implementation of a buffering promise, it presents its clients with a fully
 * deterministic contract.
 *
 * @author Mark S. Miller
 */
class BufferingRef extends Ref {

    private final WeakReference myBuf;

    /**
     *
     */
    BufferingRef(FlexList resolver) {
        myBuf = new WeakReference(resolver);
    }

    /**
     * Returns null.
     * <p/>
     * All implementations of <tt>optProblem/0</tt> must be thread safe, in
     * order for {@link Ref#state() Ref.state/0} to be thread safe.
     *
     * @return null
     */
    public Throwable optProblem() {
        return null;
    }

    /**
     * Returns <tt>this</tt>.
     * <p/>
     * All implementations of <tt>resolutionRef/0</tt> must be thread safe, in
     * order for {@link Ref#resolution() Ref.resolution/0} to be thread safe.
     *
     * @return <tt>this</tt>.
     */
    Ref resolutionRef() {
        return this;
    }

    /**
     * Returns EVENTUAL.
     * <p/>
     * All implementations of <tt>state/0</tt> must be thread safe, in order
     * for {@link Ref#isNear(Object) Ref.isNear/1} to be thread safe.
     *
     * @return EVENTUAL
     */
    public String state() {
        return EVENTUAL;
    }

    /**
     * Complains.
     */
    public Object callAll(String verb, Object[] args) {
        T.fail("not synchronously callable (" + verb + ")");
        return null; //make compiler happy
    }

    /**
     *
     */
    public void sendMsg(Message msg) {
        FlexList optMsgs = (FlexList)myBuf.get();
        if (null != optMsgs) {
            optMsgs.push(msg);
        }
    }

    /**
     * Stores the message
     *
     * @return a promise for the result
     */
    public Ref sendAll(String verb, Object[] args) {
        FlexList optMsgs = (FlexList)myBuf.get();
        if (null == optMsgs) {
            return this; //since I'm now a perfectly good black hole
            //Perhaps we should return a specialized BlackHoleRef. But
            //there's no way to make this useful without exposing the
            //non-determinism.
        } else {
            Object[] promise = Ref.promise();
            Ref result = (Ref)promise[0];
            optMsgs.push(new Message((Resolver)promise[1], verb, args));
            return result;
        }
    }

    /**
     * Stores the message.
     */
    public Throwable sendAllOnly(String verb, Object[] args) {
        FlexList optMsgs = (FlexList)myBuf.get();
        if (null == optMsgs) {
            return null;
        } else {
            optMsgs.push(new Message(null, verb, args));
            return null;
        }
    }

    /**
     *
     */
    public boolean isResolved() {
        return false;
    }

    /**
     *
     */
    void setTarget(Ref newTarget) {
        T.fail("Not switchable");
    }

    /**
     *
     */
    void commit() {
        //already committed, do nothing.
    }

    /**
     * Asyncronously send all the messages accumulated so far to the target,
     * forget them (so future deliverAll()s won't send them), and return the
     * count of how many messages this is.
     */
    static int deliverAll(FlexList buf,
                          Object target,
                          SendingContext optSendingContext) {
        Message[] msgs = (Message[])buf.getArray(Message.class);
        buf.setSize(0);
        Ref targRef = Ref.toRef(target);
        for (int i = 0; i < msgs.length; i++) {
            //Using Ref.sendMsg/1 rather than E.sendMsg/2 is necessary to
            //avoid an infinite regress in resolve/1 (as demonstrated by
            //Epimenides), by preserving the original Resolver. Besides,
            //it's also a nice optimization.
            SendingContext context = msgs[i].getSendingContext();
            context.appendContext(optSendingContext);
            targRef.sendMsg(msgs[i]);
        }
        return msgs.length;
    }

    /**
     *
     */
    public SealedBox __optSealedDispatch(Object brand) {
        return MirandaMethods.__optSealedDispatch(this, brand);
    }

    /**
     *
     */
    public Object __conformTo(Guard guard) {
        return MirandaMethods.__conformTo(this, guard);
    }

    /**
     *
     */
    public void __printOn(TextWriter out) throws IOException {
        out.print("<Promise>");
    }
}
