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
import org.erights.e.elib.prim.MirandaMethods;
import org.erights.e.elib.sealing.SealedBox;
import org.erights.e.elib.serial.PassByConstruction;
import org.erights.e.elib.serial.Persistent;
import org.erights.e.elib.slot.Guard;

import java.io.IOException;

/**
 * What a FarRef becomes when it breaks -- a BROKEN Ref with identity.
 * <p/>
 * Maintains the same()ness identity and sameness hash of the FarRef. A
 * DisconnectedRef is listed as an HONORARY Selfless object for implementation
 * reasons only: (HONORARY because it encapsulates its identity, so it isn't
 * transparent).
 * <p/>
 * A DisconnectedRef's contents must be transitively transparently Selfless and
 * passable by construction. DisconnectedRef is listed as implementing
 * PassByConstruction for implementation reasons only. Not being NEAR, by
 * definition it is not a PassByContruction object.
 *
 * @author Mark S. Miller
 */
class DisconnectedRef extends Ref implements Persistent, PassByConstruction {

    static private final long serialVersionUID = 1307531130876647340L;

    /**
     *
     */
    private final Throwable myProblem;

    /**
     *
     */
    final Object myIdentity;

    /**
     * Makes a Ref that will never deliver messages to any object because of
     * problem, but which is considered the same() as a particular object.
     * <p/>
     * Iff two [Broken]FarRefs have the same() identity, then they are
     * considered the same().
     * <p/>
     * identity must itself be an honorary Selfless object.
     */
    DisconnectedRef(Throwable problem, Object identity) {
        myProblem = problem;
        myIdentity = identity;
        T.notNull(myProblem, "Missing problem");
    }

    /**
     * As an HONORARY Selfless object, my .equals() and .hashCode() determine
     * sameness.
     * <p/>
     * NOTE: Uses myIdentity's .equals(), which is safe, as myIdentity must be
     * an honorary Selfless object.
     */
    public boolean equals(Object other) {
        if (other instanceof FarRef) {
            return myIdentity.equals(((FarRef)other).myIdentity);
        } else if (other instanceof DisconnectedRef) {
            return myIdentity.equals(((DisconnectedRef)other).myIdentity);
        } else {
            return false;
        }
    }

    /**
     * As an HONORARY Selfless object, my .equals() and .hashCode() determine
     * sameness.
     * <p/>
     * NOTE: Uses myIdentity's .hashCode(), which is safe, as myIdentity must
     * be an honorary Selfless object.
     */
    public int hashCode() {
        return myIdentity.hashCode();
    }

    /**
     * Returns this broken ref's problem.
     * <p/>
     * All implementations of <tt>optProblem/0</tt> must be thread safe, in
     * order for {@link Ref#state() Ref.state/0} to be thread safe.
     *
     * @return our problem
     */
    public Throwable optProblem() {
        return myProblem;
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
     * Returns BROKEN.
     * <p/>
     * All implementations of <tt>state/0</tt> must be thread safe, in order
     * for {@link Ref#isNear(Object) Ref.isNear/1} to be thread safe.
     *
     * @return BROKEN
     */
    public String state() {
        return BROKEN;
    }

    /**
     * Should be called only if the state is already BROKEN.
     * <p/>
     * Takes care of __whenMoreResolved(reactor) and __whenBroken(reactor). If
     * you know verb/args aren't one of these, you don't need to call
     * doBreakage().
     *
     * @return Why wasn't a __whenMoreResolved/1 or __whenBroken/1 queued? It
     *         isn't queued if this vat or comm connection is shut down, in
     *         which case the returned problem explains why. If null is
     *         returned, then the message wasn't __whenMoreResolved/1 or
     *         __whenBroken/1, in which case there was nothing to queue, or it
     *         was one of these and it was queued, though it may still not
     *         arrive.
     */
    private Throwable doBreakage(String verb, Object[] args) {
        if (1 == args.length &&
          ("__whenMoreResolved".equals(verb) || "__whenBroken".equals(verb))) {

            return E.sendOnly(args[0], "run", this);
        } else {
            return null;
        }
    }

    /**
     * This default implementation switches on state() and either synchronously
     * forward the message, if we're NEAR, or complains.
     */
    public Object callAll(String verb, Object[] args) {
        doBreakage(verb, args);
        throw ExceptionMgr.asSafe(myProblem);
    }

    /**
     *
     */
    public Ref sendAll(String verb, Object[] args) {
        doBreakage(verb, args);
        return new UnconnectedRef(myProblem);
    }

    /**
     *
     */
    public Throwable sendAllOnly(String verb, Object[] args) {
        return doBreakage(verb, args);
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
        out.print("<ref disconnected by ", myProblem, ">");
    }
}
