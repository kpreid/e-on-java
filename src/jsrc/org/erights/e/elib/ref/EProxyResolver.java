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
import org.erights.e.elib.oldeio.EPrintable;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.sealing.Brand;
import org.erights.e.elib.sealing.SealedBox;
import org.erights.e.elib.sealing.Unsealer;
import org.erights.e.elib.tables.Selfless;
import org.erights.e.elib.vat.WeakPtr;

import java.io.IOException;

/**
 * A maker on demand of a EProxy as well as the resolver of this EProxy.
 * <p/>
 * I use a WeakPtr to point at my Ref so it may be GCed. Note that
 * EProxyResolver exposes its EProxyHandler. If this isn't suitable for you,
 * wrap it.
 *
 * @author Mark S. Miller
 */
public class EProxyResolver implements Resolver, EPrintable {

    /**
     * While my Ref is handled, it delegates many of my decisions to this
     * handler.
     * <p/>
     * When I'm done, I set my handler to null.
     */
    private EProxyHandler myOptHandler;

    /**
     * The basis for the settled identity of the OldFarRef I make, or null if I
     * make a RemotePromise.
     */
    private final Object myOptIdentity;

    /**
     * Once it's done, it stops holding the weak ptr.
     */
    private WeakPtr myOptRefPtr;

    /**
     * @param handler     Delegates delegatable eventual-ref behavior to the
     *                    handler.
     * @param optIdentity If null, the handled reference will be unresolved (a
     *                    RemotePromise). If non-null, the optIdentity must be
     *                    an honorary {@link Selfless} object. This includes
     *                    {@link java.math.BigInteger} and {@link
     *                    net.captp.jcomm.ObjectID}. The identity object serves
     *                    as the basis for the sameness identity of the
     *                    resulting handled resolved reference (OldFarRef).
     */
    public EProxyResolver(EProxyHandler handler, Object optIdentity) {
        optIdentity = Ref.resolution(optIdentity);
        T.require(
          null == optIdentity || Selfless.HONORARY.has(optIdentity.getClass()),
          optIdentity,
          " must be an honorary Selfless object");
        myOptHandler = handler;
        myOptIdentity = optIdentity;
        myOptRefPtr = null;
    }

    /**
     *
     */
    public boolean isDone() {
        return null == myOptHandler;
    }

    /**
     *
     */
    public void gettingCloser() {
        // TODO: Record causality information
    }

    /**
     *
     */
    public Object getOptIdentity() {
        return myOptIdentity;
    }

    /**
     * Gets the Ref if it's still there. Otherwise return null.
     */
    private Ref getOptRef() {
        if (isDone()) {
            T.fail("done");
        }
        if (null == myOptRefPtr) {
            return null;
        } else {
            return (Ref)myOptRefPtr.get();
        }
    }

    /**
     * Gets my Ref, or quietly remakes it if the old one is gone. This enables
     * imports to be quietly revived when DGC-ships cross in the night.
     */
    public Ref getProxy() {
        Ref result = getOptRef();
        if (null != result) {
            return result;
        }
        if (null == myOptIdentity) {
            result = new RemotePromise(myOptHandler);
        } else {
            result = new OldFarRef(myOptIdentity, myOptHandler);
        }
        myOptRefPtr = new WeakPtr(result, this, "reactToGC", E.NO_ARGS);
        return result;
    }

    /**
     * Returns the unwrapped (underlying) handler
     */
    public EProxyHandler optHandler() {
        if (null == myOptHandler) {
            return null;
        } else {
            return myOptHandler.unwrap();
        }
    }

    /**
     * Automatically eventually invoked when one of my Refs have gone away.
     * <p/>
     * Normally just forwards the reactToGC() to myOptHandler. But, in order to
     * avoid a race condition, it only does so if there's no current Ref. There
     * might be a current Ref if getProxy() was called after the old Ref was
     * GCed, but before I was notified. Note that multiple Refs might be
     * created and GCed before I get notified, in which case I might notify
     * myOptHandler multiple times.
     */
    public void reactToGC() {
        if (null == myOptHandler) {
            //since notification is eventual, it could happen after I'm done.
            return;
        }
        Ref result = getOptRef();
        if (null != result) {
            //If a new Ref has already replaced the GCed one, then, don't
            //notify the handler.
            return;
        }
        //XXX should we allow throws to propogate?
        myOptHandler.reactToGC();
    }

    /**
     * Resolves the proxy to become the target.
     * <p/>
     * In CapTP, there are two reasons this might normally happen:<ol>
     * <li>Communications failure making us BROKEN, or <li>the target of a
     * EProxy responded to a __whenMoreResolved/1, of which this EProxyResolver
     * is the argument. XXX </ol>
     */
    public boolean resolve(Object target, boolean strict) {
        Ref optRef = getOptRef();
        if (null == optRef) {
            T.require(!strict, "Already resolved");
            return false;
        }
        optRef.setTarget(Ref.toRef(target));
        optRef.commit();
        myOptHandler = null;
        //myOptIdentity = null; leave this for debugging
        myOptRefPtr = null;
        return true;
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
     * If 'ref' is a remote reference over some comm system and 'unsealer' is
     * the magic unsealer for that comm system, then return ref's
     * EProxyHandler.
     */
    static public EProxyHandler getOptProxyHandler(Unsealer unsealer,
                                                   Object ref) {
        Brand brand = unsealer.getBrand();
        SealedBox optBox = Ref.optSealedDispatch(ref, brand);
        return (EProxyHandler)unsealer.optUnseal(optBox, EProxyHandler.class);
    }

    /**
     *
     */
    public void __printOn(TextWriter out) throws IOException {
        out.print("<");
        if (isDone()) {
            out.print("Closed");
        } else if (null == myOptRefPtr || null == myOptRefPtr.get()) {
            //XXX violating radio silence for debugging purposes.
            out.print("Pointless");
        } else {
            out.print("Open");
        }
        if (null != myOptIdentity) {
            out.print("@", myOptIdentity);
        }
        out.print(" EProxyResolver>");
    }

    /**
     *
     */
    public String toString() {
        return E.toString(this);
    }
}
