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
import org.erights.e.develop.exception.ExceptionMgr;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.prim.Message;
import org.erights.e.elib.sealing.SealedBox;
import org.erights.e.elib.slot.Guard;
import org.erights.e.elib.vat.Vat;

import java.io.IOException;

/**
 * A NearRef represents the Ref facet of a promise which has been fulfilled
 * with a local non-Ref object.
 * <p/>
 * In E, a NearRef is forever identical to the object it wraps (the target). In
 * the ELib implementation, NearRefs exist purely for implementation
 * convenience to wrap a non-Ref with a Ref interface. Note that all non-Refs
 * are considered NEAR.
 *
 * @author Mark S. Miller
 */
class NearRef extends Ref {

    private final Object myTarget;

    /**
     * Wrap target in Ref protocol. target must be a non-Ref.
     */
    NearRef(Object target) {
        myTarget = target;
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
     * Returns <tt>this</tt>, since target isn't a Ref
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
     * Returns the near object.
     * <p/>
     * All implementations of <tt>resolution/0</tt> must be thread safe, in
     * order for {@link Ref#resolution(Object) Ref.resolution/1} to be thread
     * safe.
     *
     * @return the actual target
     */
    public Object resolution() {
        return myTarget;
    }

    /**
     * Returns NEAR.
     * <p/>
     * All implementations of <tt>state/0</tt> must be thread safe, in order
     * for {@link Ref#isNear(Object) Ref.isNear/1} to be thread safe.
     *
     * @return BROKEN
     */
    public String state() {
        return NEAR;
    }

    /**
     * Synchronously forward the message and response.
     */
    public Object callAll(String verb, Object[] args) {
        return E.callAll(myTarget, verb, args);
    }

    public void sendMsg(Message msg) {
        Throwable optProblem = Vat.getCurrentVat().qSendMsg(myTarget, msg);
        if (optProblem != null) {
            throw ExceptionMgr.asSafe(optProblem);
        }
    }

    /**
     * Asynchronously forward the message and response.
     */
    public Ref sendAll(String verb, Object[] args) {
        return E.sendAll(myTarget, verb, args);
    }

    /**
     * Asynchronously forward the message.
     */
    public Throwable sendAllOnly(String verb, Object[] args) {
        return E.sendAllOnly(myTarget, verb, args);
    }

    /**
     *
     */
    public boolean isResolved() {
        return true;
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
     *
     */
    public SealedBox __optSealedDispatch(Object brand) {
        return Ref.optSealedDispatch(myTarget, brand);
    }

    /**
     *
     */
    public Object __conformTo(Guard guard) {
        return Ref.conformTo(myTarget, guard);
    }

    /**
     *
     */
    public void __printOn(TextWriter out) throws IOException {
        out.print(myTarget);
    }
}
