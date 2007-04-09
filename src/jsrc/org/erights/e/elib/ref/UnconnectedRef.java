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
import org.erights.e.elib.serial.JOSSPassByConstruction;
import org.erights.e.elib.serial.Persistent;
import org.erights.e.elib.slot.Guard;

import java.io.IOException;

/**
 * If an unresolved reference becomes Broken without first being Resolved, it
 * becomes an <a href="http://www.erights.org/elib/equality/same-ref.html"
 * >UnconnectedRef</a> (an Unsettled Broken reference).
 * <p/>
 * A UnconnectedRef will never designate an object, but provides a Throwable
 * explaining why not (the problem). An UnconnectedRef is not
 * sameness-comparable to anything else, including itself. A UnconnectedRef is
 * forever Broken by the same problem and without any sameness identity.
 * <p/>
 * A UnconnectedRef's contents must be transitively transparently Selfless and
 * passable by construction. UnconnectedRef is listed as implementing
 * JOSSPassByConstruction for implementation reasons only. Not being NEAR, by
 * definition it is not a PassByContruction object.
 *
 * @author Mark S. Miller
 */
class UnconnectedRef extends Ref
  implements Persistent, JOSSPassByConstruction {

    static private final long serialVersionUID = -2749826475753817626L;

    /**
     * @serial The Throwable that caused or explains why I'm broken
     */
    private final Throwable myProblem;

    /**
     * Makes a Ref that will never designate an object because of problem.
     */
    UnconnectedRef(Throwable problem) {
        myProblem = problem;
        T.notNull(myProblem, "Missing problem");
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
     * doBreakage()
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
        //clever Danfuzz
        return this;
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
        out.print("<ref broken by ", myProblem, ">");
    }
}
