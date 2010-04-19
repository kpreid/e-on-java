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

import java.io.IOException;

/**
 * All message asyncronously delivered to a SwitchableRef will be forwarded its
 * current target. A SwitchableRef starts out switchable, in which case it
 * stays EVENTUAL and does not reveal its current target. While it is
 * switchable, its Resolver can change its target, and can make it
 * unswitchable. Once it's unswitchable, its Resolver can do neither, and the
 * SwitchableRef becomes equivalent to its target -- ie, it can shorten.
 *
 * @author Mark S. Miller
 */
class SwitchableRef extends Ref {

    /**
     * The current destination for all messages.
     */
    private Ref myTarget;

    /**
     * Can myTarget (and myIsSwitchable) be changed?
     */
    private boolean myIsSwitchable;

    /**
     *
     */
    SwitchableRef(Ref target) {
        myTarget = target;
        myIsSwitchable = true;
    }

    /**
     * As long as I'm switchable, I'm EVENTUAL so my optProblem must be null,
     * even if my target is BROKEN.
     * <p/>
     * All implementations of <tt>optProblem/0</tt> must be thread safe, in
     * order for {@link Ref#state() Ref.state/0} to be thread safe.
     */
    public Throwable optProblem() {
        if (myIsSwitchable) {
            return null;
        } else {
            resolutionRef();
            return myTarget.optProblem();
        }
    }

    /**
     * If this has been shortened (target set and committed), then return that
     * target; else return <tt>this</tt>.
     * <p/>
     * All implementations of <tt>resolutionRef/0</tt> must be thread safe, in
     * order for {@link Ref#resolution() Ref.resolution/0} to be thread safe:
     * If resolutionRef/0 is called from another thread while this ref is in
     * the middle of being shortened, then resolutionRef/0 must return either
     * <tt>this</tt> or what this ref is being shortened to.
     * <p/>
     * XXX Although the implementation doesn't synchronize, it is inductively
     * thread safe given a simple memory model. Is it safe in Java's complex
     * memory model? Do we care -- are any Java implementations not faithful to
     * the simple memory model?
     */
    Ref resolutionRef() {
        // XXX Is this truly safe against cycles?
        // Since we don't synchronize, this further shortening might happen
        // redundantly, which is fine.
        myTarget = myTarget.resolutionRef();
        if (myIsSwitchable) {
            return this;
        } else {
            return myTarget;
        }
    }

    /**
     * If shortened (targetted and committed), returns the target's state();
     * else returns EVENTUAL.
     * <p/>
     * All implementations of <tt>state/0</tt> must be thread safe, in order
     * for {@link Ref#isNear(Object) Ref.isNear/1} to be thread safe.
     */
    public String state() {
        if (myIsSwitchable) {
            return EVENTUAL;
        } else {
            resolutionRef();
            return myTarget.state();
        }
    }

    /**
     *
     */
    public Object callAll(String verb, Object[] args) {
        if (myIsSwitchable) {
            T.fail("not synchronously callable (" + verb + ")");
            return null; //make compiler happy
        } else {
            resolutionRef();
            return myTarget.callAll(verb, args);
        }
    }

    /**
     * Override to pass the Message through to the resolved target.
     */
    public void sendMsg(Message msg) {
        resolutionRef();
        myTarget.sendMsg(msg);
    }

    /**
     *
     */
    public Ref sendAll(String verb, Object[] args) {
        resolutionRef();
        return myTarget.sendAll(verb, args);
    }

    /**
     *
     */
    public Throwable sendAllOnly(String verb, Object[] args) {
        resolutionRef();
        return myTarget.sendAllOnly(verb, args);
    }

    /**
     *
     */
    public boolean isResolved() {
        if (myIsSwitchable) {
            return false;
        } else {
            resolutionRef();
            return myTarget.isResolved();
        }
    }

    /**
     *
     */
    void setTarget(Ref newTarget) {
        if (myIsSwitchable) {
            //since, while I'm switchable, my resolutionRef() is myself, the
            //only problematic cycle case is that his resolutionRef() turns
            //out to be me.
            myTarget = newTarget.resolutionRef();
            if (this == myTarget) {
                Throwable th = new ViciousCycleException("Ref loop");
                myTarget = new UnconnectedRef(th);
            }
        } else {
            T.fail("No longer switchable");
        }
    }

    /**
     *
     */
    void commit() {
        if (!myIsSwitchable) {
            return;
        }
        Ref newTarget = myTarget.resolutionRef();
        myTarget = TheViciousRef;
        TheViciousRef.optProblem().fillInStackTrace();
        myIsSwitchable = false;
        newTarget = newTarget.resolutionRef();
        if (newTarget == TheViciousRef) {
            myTarget =
              new UnconnectedRef(new ViciousCycleException("Ref loop"));
        } else {
            myTarget = newTarget;
        }
    }

    /**
     *
     */
    public SealedBox __optSealedDispatch(Object brand) {
        if (myIsSwitchable) {
            return MirandaMethods.__optSealedDispatch(this, brand);
        } else {
            resolutionRef();
            return myTarget.__optSealedDispatch(brand);
        }
    }

    /**
     *
     */
    public Object __conformTo(Guard guard) {
        if (myIsSwitchable) {
            return MirandaMethods.__conformTo(this, guard);
        } else {
            resolutionRef();
            return myTarget.__conformTo(guard);
        }
    }

    /**
     *
     */
    public void __printOn(TextWriter out) throws IOException {
        if (myIsSwitchable) {
            out.print("<Promise>");
        } else {
            resolutionRef();
            myTarget.__printOn(out);
        }
    }
}
