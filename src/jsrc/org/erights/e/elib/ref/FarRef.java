package org.erights.e.elib.ref;

// Copyright 2007 Kevin Reid under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.develop.assertion.T;
import org.erights.e.develop.exception.ExceptionMgr;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.prim.MirandaMethods;
import org.erights.e.elib.sealing.SealedBox;
import org.erights.e.elib.slot.FinalSlot;
import org.erights.e.elib.slot.Guard;
import org.erights.e.elib.tables.Equalizer;
import org.erights.e.elib.tables.NotSettledException;
import org.erights.e.elib.tables.TraversalKey;

import java.io.IOException;

/**
 * A resolved Proxy; one which can become broken but not another reference.
 *
 * @author Kevin Reid
 */
public final class FarRef extends Proxy {

    private TraversalKey myResolutionIdentity;

    FarRef(Object handler, Object resolutionBox) throws NotSettledException {
        super(handler, resolutionBox);
        myResolutionIdentity = new TraversalKey(resolutionBox);
    }

    protected void commit() {
        Object handler = myHandler; // will be discarded by super.commit

        super.commit();

        {
            Object resolution = ((FinalSlot)myResolutionBox).get();

            if (!(Ref.isBroken(resolution))) {
                resolution = Ref.broken(E.asRTE(
                  "Attempt to resolve a Far ref handled by " +
                    E.toQuote(handler) + " to another identity (" + 
                    E.toQuote(resolution) + ")."));
            }

            // A FarRef can only resolve to a DisconnectedRef to preserve its
            // identity
            resolution = new DisconnectedRef2(handler,
                                              myResolutionIdentity,
                                              Ref.optProblem(resolution));

            myResolutionBox = new FinalSlot(resolution);
        }

        myResolutionIdentity = null;
    }

    public boolean equals(Object other) {
        // super will check that we can't have jettisoned yet
        return super.equals(other) && Equalizer.make()
          .isSameYet(myResolutionIdentity,
                     ((FarRef)other).myResolutionIdentity);
    }

    protected boolean isResolvedIfNotForwarding() {
        return true;
    }

    protected void __printOnIfNotForwarding(TextWriter out)
      throws IOException {
        out.write("<Far ref>");
    }

    // XXX shouldn't this be renamed DisconnectedRef and the original 
    // Disconnected ref be OldDisconnectedRef?
    private final class DisconnectedRef2 extends Ref {

        protected Object myHandler;
        protected Object myResolutionIdentity;
        protected Throwable myProblem;

        DisconnectedRef2(Object handler,
                         Object resolutionIdentity,
                         Throwable problem) {
            myHandler = handler;
            myResolutionIdentity = resolutionIdentity;
            myProblem = problem;
        }

        public boolean equals(Object other) {
            if (other.getClass() != this.getClass()) {
                return false;
            }
            DisconnectedRef2 that = (DisconnectedRef2)other;
            Equalizer e = Equalizer.make();
            try {
                boolean result = e.isSameEver(myHandler, that.myHandler) &&
                  e.isSameEver(myResolutionIdentity,
                               that.myResolutionIdentity);
                if (result && !e.isSameEver(myProblem, that.myProblem)) {
                    T.fail(
                      "can't happen: disconnected refs with same identity " +
                        "and different problem");
                }
                return result;
            } catch (NotSettledException ex) {
                throw ExceptionMgr.asSafe(ex);
            }
        }

        public String state() {
            return Ref.BROKEN;
        }

        public boolean isResolved() {
            return true;
        }

        public Throwable optProblem() {
            return myProblem;
        }

        public Ref resolutionRef() {
            return this;
        }

        private Throwable handleReactors(String verb, Object[] args) {
            if (args.length == 1 && ("__whenBroken".equals(verb) ||
              "__whenMoreResolved".equals(verb))) {
                return E.sendOnly(args[0], "run", this);
            } else {
                return null;
            }
        }

        public Object callAll(String verb, Object[] args) {
            handleReactors(verb, args);
            throw ExceptionMgr.asSafe(myProblem);
        }

        public Ref sendAll(String verb, Object[] args) {
            handleReactors(verb, args);
            return this;
        }

        public Throwable sendAllOnly(String verb, Object[] args) {
            return handleReactors(verb, args);
        }

        public Object __conformTo(Guard guard) {
            return MirandaMethods.__conformTo(this, guard);
        }

        public SealedBox __optSealedDispatch(Object brand) {
            return MirandaMethods.__optSealedDispatch(this, brand);
        }

        public void __printOn(TextWriter out) throws IOException {
            out.write("<ref broken by ");
            out.print(myProblem);
            out.write(">");
        }

        void setTarget(Ref newTarget) {
            T.fail("setTarget doesn't apply to DisconnectedRef2");
        }

        void commit() {
            T.fail("commit doesn't apply to DisconnectedRef2");
        }
    }

}
