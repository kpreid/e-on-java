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

import org.erights.e.develop.assertion.T;
import org.erights.e.develop.exception.ExceptionMgr;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.ref.DelayedRedirector;
import org.erights.e.elib.ref.EProxyHandler;
import org.erights.e.elib.ref.EProxyResolver;
import org.erights.e.elib.ref.Ref;
import org.erights.e.elib.ref.ReferenceMonitor;
import org.erights.e.elib.sealing.SealedBox;
import org.erights.e.elib.sealing.Sealer;
import org.erights.e.elib.sealing.Unsealer;
import org.erights.e.elib.tables.FlexList;

/**
 * Handles the tail-ends of remote references. <p>
 * <p/>
 * There is a 3x2 taxonomy of RemoteHandlers. The first distinction is
 * between a) handlers in the Imports table (or "imported handlers"), for
 * which the position is positive, b) handlers in the questions table (or
 * "question handlers"), for which the position is negative, and c) the
 * handler of the other side's NonceLocator, for which the position is zero.
 * <p/>
 * <p/>
 * The second distinction is between handlers for resolved remote references,
 * represented by the concrete subclass FarHandler, and handlers for
 * remote references that aren't yet resolved, represented by the concrete
 * subclass RemotePromiseHandler. As a special case, the handler at zero for
 * the other side's NonceLocator is a RemotePromiseHandler, since shutdown
 * attempts may cause it to be retargeted at a newly incarnated NonceLocator
 * from the other side, which may have a new identity.
 *
 * @author Mark S. Miller
 */
abstract class RemoteHandler implements EProxyHandler {

    /**
     * Connection to the vat I point into
     */
    final CapTPConnection myConn;

    /**
     * My outgoing position. <p>
     * <p/>
     * If < 0, in the Questions table.
     * If == 0, the special RemotePromiseHandler for the other side's
     * NonceLocator.
     * If > 0, in the Imports table.
     */
    private final int myPos;

    /**
     * Only relevant if myPos > 0. A count for all wire references that we
     * haven't yet accounted for to the other side.
     */
    private int myWireCount;

    /**
     *
     */
    private final FlexList myBreakageReactors;

    /**
     * The Resolver of our Proxy, which also revives the Proxy on demand.
     */
    final EProxyResolver myResolver;

    /**
     * The wireCount is initialized to 1
     *
     * @param conn        The CapTPConnection to communicate via
     * @param pos         The Imports or Questions map pos of the object
     * @param optIdentity null, or the sameness identity of the far
     *                    reference.
     */
    RemoteHandler(CapTPConnection conn,
                  int pos,
                  Object optIdentity) {
        myConn = conn;
        myPos = pos;
        myWireCount = 1;
        myBreakageReactors = FlexList.make();
        ReferenceMonitor refmon = myConn.getReferenceMonitor();
        myResolver = new EProxyResolver(refmon.wrap(this), optIdentity);
    }

    /**
     * 
     */
    public EProxyHandler unwrap() {
        return this;
    }

    /**
     * How should my ref respond to an __optSealedDispatch request?
     */
    public SealedBox handleOptSealedDispatch(Object brand) {
        Sealer sealer = myConn.getSealer();
        if (sealer.getBrand() == brand) {
            return sealer.seal(this);
        } else {
            return null;
        }
    }

    /**
     * Handles this special case separately, so subclasses can override
     * separately.
     * <p/>
     * The default implementation up here tries to deliver-only the
     * __whenMoreResolved, but if that throws an exception back at us, ie, if
     * the __whenMoreResolved message seems to be unable to get through, we
     * report a broken reference (broken by that problen) to the reactor as
     * the resolution, and return that problem.
     */
    Throwable handleWhenMoreResolved(Object reactor) {
        reactor = Ref.resolution(reactor);
        if (Ref.isEventual(reactor)) {
            Unsealer unsealer = myConn.getUnsealer();
            RemoteHandler optHandler =
              (RemoteHandler)EProxyResolver.getOptProxyHandler(unsealer,
                                                               reactor);
            if (null != optHandler && optHandler.myConn != myConn) {
                // When the reactor is a remote reference to yet a third
                // vat, we shorten rather than forward.

                E.sendOnly(reactor, "run", myResolver.getProxy());
                return null; // even if the above E.sendOnly complains
            }
        }

        Object[] args = {reactor};
        try {
            myConn.sendDeliverOnlyOp(myPos, "__whenMoreResolved", args);
            return null;
        } catch (Throwable problem) {
            Ref broke = Ref.broken(problem);
            E.sendOnly(reactor, "run", broke);
            return problem;
        }
    }

    /**
     * Handles this special case separately, so subclasses can override
     * separately.
     * <p/>
     * The default implementation up here stores the reactor in
     * myBreakageReactors and always returns null.
     */
    Throwable handleWhenBroken(Object reactor) {
        myBreakageReactors.push(reactor);
        return null;
    }

    /**
     *
     */
    public void handleSendAllOnly(String verb, Object[] args) {
        if (1 == args.length) {
            if ("__whenMoreResolved".equals(verb)) {
                Throwable optProblem = handleWhenMoreResolved(args[0]);
                if (null == optProblem) {
                    return;
                } else {
                    throw ExceptionMgr.asSafe(optProblem);
                }

            } else if ("__whenBroken".equals(verb)) {
                Throwable optProblem = handleWhenBroken(args[0]);
                if (null == optProblem) {
                    return;
                } else {
                    throw ExceptionMgr.asSafe(optProblem);
                }
            }
        }
        handleRegularSendAllOnly(verb, args);
    }

    /**
     * Handles this non-special case separately, so subclasses can override
     * separately.
     */
    void handleRegularSendAllOnly(String verb, Object[] args) {
        myConn.sendDeliverOnlyOp(myPos, verb, args);
    }

    /**
     *
     */
    static private Ref promiseForNull() {
        Object[] pair = Ref.promise();
        E.sendOnly(pair[1], "resolve", null);
        return (Ref)pair[0];
    }

    /**
     *
     */
    public Ref handleSendAll(String verb, Object[] args) {
        if (1 == args.length) {
            if ("__whenMoreResolved".equals(verb)) {
                Throwable optProblem = handleWhenMoreResolved(args[0]);
                if (null == optProblem) {
                    return promiseForNull();
                } else {
                    return Ref.broken(optProblem);
                }

            } else if ("__whenBroken".equals(verb)) {
                Throwable optProblem = handleWhenBroken(args[0]);
                if (null == optProblem) {
                    return promiseForNull();
                } else {
                    return Ref.broken(optProblem);
                }
            }
        }
        return handleRegularSendAll(verb, args);
    }

    /**
     * Handles this non-special case separately, so subclasses can override
     * separately.
     */
    Ref handleRegularSendAll(String verb, Object[] args) {
        EProxyResolver pr = myConn.makeQuestion();
        RemotePromiseHandler rvh = (RemotePromiseHandler)pr.optHandler();
        myConn.sendDeliverOp(rvh.getPos(),
                             new DelayedRedirector(pr),
                             myPos,
                             verb,
                             args);
        return pr.getProxy();
    }

    /**
     *
     */
    public void handleResolution(Object newTarget) {
        int len = myBreakageReactors.size();
        for (int i = 0; i < len; i++) {
            Object reactor = myBreakageReactors.get(i);
            E.sendOnly(newTarget, "__whenBroken", reactor);
        }
        //XXX should we allow throws to propogate?
        reactToGC();
    }

    /**
     *
     */
    public void reactToGC() {
        if (myConn.getOptProblem() != null) {
            //If this is a dead connection, then gc doesn't need to be
            //reported remotely.
            return;
        }
        if (0 < myPos) {
            if (1 <= myWireCount) {
                myConn.sendGCExportOp(myPos, myWireCount);
                myWireCount = 0;
            }
        } else if (0 > myPos) {
            myConn.dropQuestion(myPos);
        }
    }

    /**
     *
     */
    public void mustBeDisposable() {
        T.requireSI(0 == myWireCount,
                    "wireCount must be 0 rather than ", myWireCount);
    }

    /**
     *
     */
    int getPos() {
        return myPos;
    }

    /**
     *
     */
    void countWireRef() {
        myWireCount++;
        //XXX if over threshold, send back the extras
    }

    /**
     *
     */
    public boolean sameConnection(Object other) {
        Unsealer unsealer = myConn.getUnsealer();
        RemoteHandler optHandler =
          (RemoteHandler)EProxyResolver.getOptProxyHandler(unsealer, other);
        return null != optHandler && myConn == optHandler.myConn;
    }
}
