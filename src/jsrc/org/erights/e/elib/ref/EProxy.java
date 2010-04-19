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
import org.erights.e.develop.trace.Trace;
import org.erights.e.elib.prim.MirandaMethods;
import org.erights.e.elib.sealing.SealedBox;
import org.erights.e.elib.slot.Guard;

/**
 * A EProxy is intended to be the arrowtail of a Ref whose arrowhead is in
 * another vat, and which will therefore pass messages sent on to that other
 * vat.
 * <p/>
 * However, a EProxy delegates most of its behavior to its EProxyHandler, which
 * may be untrusted code. Therefore, the EProxy itself has responsibility for
 * adhering to the Ref contract despite arbitrary behavior by its handler. This
 * is a step toward reimplementing CapTP in the E language.
 * <p/>
 * A EProxyHandler should create and point at its EProxy only using
 * EProxyResolver, so it can find out when the EProxy has been GCed, be able to
 * resolve it, and be able to transparently revive it.
 * <p/>
 * A Settled EProxy (one with sameness identity) is represented by the subclass
 * OldFarRef. An Unsettled EProxy (one without such identity, and therefore a
 * Promise) is represented by the subclass OldRemotePromise.
 *
 * @author Mark S. Miller
 */
abstract class EProxy extends Ref {

    /**
     * While I'm handled, I delegate many of my decisions to my handler.
     */
    EProxyHandler myOptHandler;

    /**
     * Once I'm not handled, I'm resolved to myOptTarget
     */
    Ref myOptTarget;

    /**
     *
     */
    EProxy(EProxyHandler handler) {
        myOptHandler = handler;
        myOptTarget = null;
    }

    /**
     * If this is a handled EProxy whose handler is an instance of
     * handlerClass, then return that handler; else null.
     * <p/>
     * All implementations of <tt>getOptProxyHandler/1</tt> must be thread
     * safe, in order for {-@link BootRefHandler#getOptBootRefHandler(Object)
     * BootRefHandler.getOptBootRefHandler/1} to be thread safe: myOptHandler
     * only makes one transition from non-null to null. This implementation
     * samples it once, and then proceeds with the possibly slightly stale
     * sample.
     */
    public EProxyHandler getOptProxyHandler(Class handlerClass) {
        EProxyHandler result = myOptHandler;
        if (null == result) {
            return null;
        } else if (handlerClass.isInstance(result)) {
            return result;
        } else {
            return null;
        }
    }

    /**
     * If handled, return null; else return the target's problem.
     * <p/>
     * All implementations of <tt>optProblem/0</tt> must be thread safe, in
     * order for {@link Ref#state() Ref.state/0} to be thread safe.
     */
    public Throwable optProblem() {
        if (null == myOptTarget) {
            return null;
        } else {
            resolutionRef();
            return myOptTarget.optProblem();
        }
    }

    /**
     * If this proxy is still being handled, return <tt>this</tt>; else return
     * whatever it's been shortened to.
     * <p/>
     * All implementations of <tt>resolutionRef/0</tt> must be thread safe, in
     * order for {@link Ref#resolution() Ref.resolution/0} to be thread safe:
     * If resolutionRef/0 is called from another thread while this proxy is in
     * the middle of being shortened, then resolutionRef/0 must return either
     * <tt>this</tt> or what this proxy is being shortened to.
     * <p/>
     * XXX Although the implementation doesn't synchronize, it is inductively
     * thread safe given a simple memory model. Is it safe in Java's complex
     * memory model? Do we care -- are any Java implementations not faithful to
     * the simple memory model?
     */
    Ref resolutionRef() {
        // XXX Is this truly safe against cycles?
        if (null == myOptTarget) {
            return this;
        } else {
            // Since we don't synchronize, this further shortening might happen
            // redundantly, which is fine.
            myOptTarget = myOptTarget.resolutionRef();
            return myOptTarget;
        }
    }

    /**
     * If handled, returns EVENTUAL; else returns the target's state().
     * <p/>
     * All implementations of <tt>state/0</tt> must be thread safe, in order
     * for {@link Ref#isNear(Object) Ref.isNear/1} to be thread safe.
     */
    public String state() {
        if (null == myOptTarget) {
            return EVENTUAL;
        } else {
            resolutionRef();
            return myOptTarget.state();
        }
    }

    /**
     *
     */
    public Object callAll(String verb, Object[] args) {
        if (null == myOptTarget) {
            T.fail("not synchronously callable (" + verb + ")");
            return null; //make compiler happy
        } else {
            resolutionRef();
            return myOptTarget.callAll(verb, args);
        }
    }

    /**
     *
     */
    public Ref sendAll(String verb, Object[] args) {
        if (null == myOptTarget) {
            try {
                return myOptHandler.handleSendAll(verb, args);
            } catch (Throwable problem) {
                if (Trace.causality.warning && Trace.ON) {
                    Trace.causality.warningm("from handler: ", problem);
                }
                return Ref.broken(problem);
            }
        } else {
            resolutionRef();
            return myOptTarget.sendAll(verb, args);
        }
    }

    /**
     *
     */
    public Throwable sendAllOnly(String verb, Object[] args) {
        if (null == myOptTarget) {
            try {
                myOptHandler.handleSendAllOnly(verb, args);
                return null;
            } catch (Throwable problem) {
                //A sendAllOnly's report back to its caller is normally
                //ignored, so a possibly ignored throw should be traced.
                if (Trace.causality.warning && Trace.ON) {
                    Trace.causality
                      .warningm("ignored from handler: ", problem);
                }
                return problem;
            }
        } else {
            resolutionRef();
            return myOptTarget.sendAllOnly(verb, args);
        }
    }

    /**
     * ignored
     */
    void commit() {
        //do nothing
    }

    /**
     * If handled, ask our handler; else delegate to our target.
     */
    public SealedBox __optSealedDispatch(Object brand) {
        if (null == myOptTarget) {
            //XXX should we allow throws to propogate?
            return myOptHandler.handleOptSealedDispatch(brand);
        } else {
            return myOptTarget.__optSealedDispatch(brand);
        }
    }

    /**
     *
     */
    public Object __conformTo(Guard guard) {
        if (null == myOptTarget) {
            return MirandaMethods.__conformTo(this, guard);
        } else {
            return myOptTarget.__conformTo(guard);
        }
    }
}
