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
import org.erights.e.elib.tables.FlexList;
import org.erights.e.elib.vat.SendingContext;

/**
 * The arrowhead facet of a local promise for resolving the outcome of the
 * promise.
 *
 * @author Mark S. Miller
 */
class LocalResolver implements Resolver {

    /**
     * Once it's done, it stops pointing at the Ref.
     */
    private Ref myOptRef;

    /**
     * Until the promise is done, this holds the buffer of all messages sent to
     * far.
     */
    private FlexList myOptBuf;

    /**
     *
     */
    private SendingContext myOptSendingContext;

    /**
     * Makes (what should be) the one resolver for resolving sRef. sRef should
     * start out switchable, and forwarding to a BufferingRef, for which 'buf'
     * is the buffer.
     */
    LocalResolver(Ref sRef, FlexList buf) {
        myOptRef = sRef;
        myOptBuf = buf;
        myOptSendingContext = null;
    }

    /**
     * @param target
     * @param strict
     * @return
     */
    public boolean resolve(Object target, boolean strict) {
        if (null == myOptRef) {
            T.require(!strict, "Already resolved");
            return false;
        } else {
            myOptRef.setTarget(Ref.toRef(target));
            myOptRef.commit();
            if (null != myOptBuf) {
                BufferingRef.deliverAll(myOptBuf, target, myOptSendingContext);
            }
            myOptRef = null;
            myOptBuf = null;
            return true;
        }
    }

    /**
     * @param target
     */
    public void resolve(Object target) {
        resolve(target, true);
    }

    public boolean resolveRace(Object target) {
        return resolve(target, false);
    }

    /**
     * @return
     */
    public boolean smash(Throwable problem) {
        return resolve(new UnconnectedRef(problem), false);
    }

    /**
     *
     */
    public boolean isDone() {
        return null == myOptRef;
    }

    /**
     *
     */
    public void gettingCloser() {
        myOptSendingContext =
          new SendingContext("SCcloser", myOptSendingContext);
    }

    /**
     *
     */
    public String toString() {
        if (isDone()) {
            return "<Closed Resolver>";
        } else {
            return "<Resolver>";
        }
    }
}
