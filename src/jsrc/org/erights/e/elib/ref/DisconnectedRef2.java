// Copyright 2007 Hewlett Packard, under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.elib.ref;

import org.erights.e.elib.tables.Equalizer;
import org.erights.e.elib.tables.NotSettledException;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.prim.MirandaMethods;
import org.erights.e.elib.slot.Guard;
import org.erights.e.elib.sealing.SealedBox;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.develop.assertion.T;
import org.erights.e.develop.exception.ExceptionMgr;

import java.io.IOException;

/**
 * @author Mark S. Miller
*/ // XXX shouldn't this be renamed DisconnectedRef and the original
// Disconnected ref be OldDisconnectedRef?
final class DisconnectedRef2 extends Ref {

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

    public boolean equals(Object obj) {
        if (obj.getClass() != getClass()) {
            return false;
        }
        DisconnectedRef2 that = (DisconnectedRef2)obj;
        Equalizer e = Equalizer.make();
        try {
            boolean result = Equalizer.isSameEver(myHandler,
                                                  that.myHandler) &&
              Equalizer.isSameEver(myResolutionIdentity,
                                   that.myResolutionIdentity);
            if (result &&
              !Equalizer.isSameEver(myProblem, that.myProblem)) {
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
        if (1 == args.length && ("__whenBroken".equals(verb) ||
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
