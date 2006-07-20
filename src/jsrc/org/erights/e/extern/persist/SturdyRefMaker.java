package org.erights.e.extern.persist;

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

import net.captp.jcomm.IdentityMgr;
import net.captp.jcomm.Introducer;
import net.captp.jcomm.SturdyRef;
import org.erights.e.develop.assertion.T;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.ref.Ref;
import org.erights.e.elib.serial.RemoteDelivery;
import org.erights.e.elib.tables.FlexMap;
import org.erights.e.elib.util.OneArgFunc;
import org.erights.e.extern.timer.Timer;

import java.math.BigInteger;
import java.net.MalformedURLException;

/**
 * Enables one to make SturdyRefs for objects, even if one is in a distributed
 * confinement box.
 * <p/>
 * An instance of the object is accessible in the privileged scope under the
 * name "makeSturdyRef".
 * <p/>
 * An SturdyRefMaker isn't itself Persistent, but the relevant instance is
 * expected to be an unscope-key, so a persistent object holding a persistent
 * pointer to that SturdyRefMaker will likely revive holding instead an
 * appropriate substitute SturdyRefMaker.
 *
 * @author Mark S. Miller
 */
public class SturdyRefMaker {

    private final Introducer myIntroducer;

    private final Timer myTimer;

    private final IdentityMgr myIdentityMgr;

    private FlexMap myOptSwissRetainers;

    private Object myOptTimeMachine = null;

    /**
     *
     * @param introducer
     * @param timer
     * @param identityMgr
     */
    public SturdyRefMaker(Introducer introducer,
                          Timer timer,
                          IdentityMgr identityMgr) {
        myIntroducer = introducer;
        myTimer = timer;
        myIdentityMgr = identityMgr;
        myOptSwissRetainers = null;
    }

    /**
     * @deprecated Use {@link #tearOffRoots(FlexMap, Object) tearOffRoots/2}
     *             instead.
     */
    public FlexMap tearOffRoots(FlexMap optOldRoots) {
        return tearOffRoots(optOldRoots, null);
    }

    /**
     * Uses the "tear-off" technique for establishing private shared state
     * between two objects.
     * <p/>
     * The "tear-off" tag can only be torn off once, and once it's torn off
     * it's apparent that it is to all those who try to do so.
     * <p/>
     * In this case, the tearing off is supposed to happen by the TimeMachine
     * when it transitions out of the Ephemeral state. Therefore, if a tearOff
     * hasn't happened yet, the code here assumes that we're still in the
     * Ephemeral state and that nothing may be made persistent.
     * <p/>
     * Note: If you want to make something sturdy for purposes of distribution,
     * but not persistent, use {@link IdentityMgr} or {@link #temp} instead.
     * This requires the Introducer to be identified, but is independent of the
     * state of the TimeMachine.
     *
     * @return
     */
    public FlexMap tearOffRoots(FlexMap optOldRoots, Object optTimeMachine) {
        T.require(null == myOptSwissRetainers,
                  "Already torn off");
        if (null == optOldRoots) {
            myOptSwissRetainers = FlexMap.fromTypes(SwissRetainer.class,
                                                    Object.class);
        } else {
            myOptSwissRetainers = optOldRoots;
            SwissRetainer[] retainers =
              (SwissRetainer[])optOldRoots.getKeys(SwissRetainer.class);
            for (int i = 0, len = retainers.length; i < len; i++) {
                retainers[i].init(myOptSwissRetainers);
            }
        }
        myOptTimeMachine = optTimeMachine;
        return myOptSwissRetainers;
    }

    /**
     * Produce a persistent perpetual SturdyRef for an object. <p>
     * <p/>
     * Just run/2 with the optExpirationDate defaulting to forever.
     *
     * @param obj The object for which a SturdyRef is desired
     * @return A new SturdyRef for the indicated object
     */
    public SturdyRef run(Object obj) throws MalformedURLException {
        return run(obj, Long.MAX_VALUE);
    }

    /**
     * Produce a non-cancellable persistent SturdyRef for an object.
     * <p/>
     * Equivalent to<br>
     * <tt>make(obj,optExpirationDate)[0]</tt>.
     */
    public SturdyRef run(Object obj, long optExpirationDate)
      throws MalformedURLException {
        return (SturdyRef)make(obj, optExpirationDate)[0];
    }

    /**
     * Produce a vow for a persistent perpetual SturdyRef for an object.
     * <p/>
     * Just vow/2 with the optExpirationDate defaulting to forever.
     *
     * @param obj The object for which a SturdyRef is desired
     * @return A vow for a new SturdyRef for the indicated object
     */
    public Object vow(Object obj) throws MalformedURLException {
        return vow(obj, Long.MAX_VALUE);
    }

    /**
     * Produce a vow for a non-cancellable persistent SturdyRef for an object.
     * <p/>
     * Makes the SturdyRef immediately, but only resolves the vow after the
     * next time the associated timeMachine is saved; so only committed
     * SturdyRefs are available to be given out.
     * <p/>
     * XXX We should extend the behavior of this to allow it to be called
     * before the introducer is identified or onTheAir, it which case it
     * should wait for that before internally making the SturdyRef.
     *
     * @param obj The object for which a SturdyRef is desired
     * @return A vow for a new SturdyRef for the indicated object
     */
    public Object vow(Object obj, long optExpirationDate)
      throws MalformedURLException {
        final SturdyRef result = (SturdyRef)make(obj, optExpirationDate)[0];
        if (null == myOptTimeMachine) {
            return result;
        } else {
            Object nextSaveVow = E.call(myOptTimeMachine, "whenSaved");
            OneArgFunc doneFunc = new OneArgFunc() {
                public Object run(Object arg) {
                    if (Ref.isBroken(arg)) {
                        return arg;
                    } else {
                        return result;
                    }
                }
            };
            return Ref.whenResolved(nextSaveVow, doneFunc);
        }
    }

    /**
     * optExpirationDate defaults to forever.
     *
     * @return
     */
    public Object[] make(Object obj) throws MalformedURLException {
        return make(obj, Long.MAX_VALUE);
    }

    /**
     * Produce a SturdyRef for a persistence-capable object, thereby making it
     * persistent.
     * <p/>
     * The SturdyRef will designate the object across time and space, at
     * least until the expiration time, so long as the hosting vat (this vat)
     * is accessible.
     *
     * @param obj               The object for which a SturdyRef is desired
     * @param optExpirationDate The date after which the SturdyRef will not
     *                          longer be guaranteed to be valid. If
     *                          Long.MAX_VALUE, then it's always valid.
     * @return A pair of a new SturdyRef for the indicated object and a
     *         persistent {@link Runnable} for cancelling this persistent
     *         sturdiness.
     */
    public Object[] make(Object obj, long optExpirationDate)
      throws MalformedURLException {

        T.notNull(myOptSwissRetainers,
                  "TimeMachine must be made non-Ephemeral first.\n" +
                  "Or use makeSturdyRef.temp(...) instead if suitable.");

        BigInteger swissBase = myIdentityMgr.nextSwiss();
        SwissRetainer retainer = new SwissRetainer(myTimer,
                                                   optExpirationDate,

                                                   obj,
                                                   myIdentityMgr,
                                                   swissBase);
        myOptSwissRetainers.put(retainer, obj);
        retainer.init(myOptSwissRetainers);
        SturdyRef sr = myIdentityMgr.makeSturdyRef(obj,
                                                   swissBase,
                                                   optExpirationDate);
        Object[] result = {sr, retainer};
        return result;
    }

    /**
     * Produce a temporary SturdyRef for an object. <p>
     * <p/>
     * Just temp/2 with the optExpirationDate defaulting to forever.
     *
     * @param obj The object for which a temporary SturdyRef is desired
     * @return A new temporary SturdyRef for the indicated object
     */
    public SturdyRef temp(Object obj) throws MalformedURLException {
        return temp(obj, Long.MAX_VALUE);
    }

    /**
     * Produce a non-cancellable temporary SturdyRef for an object.
     * <p/>
     * A temporary SturdyRef is the oxymoron it seems to be. Like a persistent
     * SturdyRef, it can be used for offline introductions, or to reconnect
     * following a partition. However, it cannot survive the crash and revive
     * of this incarnation of its hosting vat. Typically, it is used only for
     * ephemeral vats -- vats which themselves are never made persistent.
     * <p/>
     * Because temporary SturdyRefs are not used as roots for persistence,
     * the <tt>temp</tt> methods accept argument objects which cannot be
     * serialized for persistence. Conceivably, this even has utility in the
     * context of a persistent vat.
     * <p/>
     * Equivalent to
     * <pre>    identityMgr.makeKnown(obj,optExpirationDate)[0]</pre>.
     */
    public SturdyRef temp(Object obj, long optExpirationDate)
      throws MalformedURLException {
        return (SturdyRef)myIdentityMgr.makeKnown(obj, optExpirationDate)[0];
    }

    /**
     * Arrange for reactor to eventually be notified as
     * <pre>    reactor <- verb(args...)</pre>
     * following future revivals.
     * <p/>
     * Once this notification is no longer needed on further revivals,
     * the notification action should use the returned persistent Runnable to
     * cancel them.
     *
     * @return
     */
    public Runnable onRevival(Object reactor, String verb, Object[] args)
      throws MalformedURLException {

        Object[] pair = make(new RemoteDelivery(reactor, verb, args));
        return (SwissRetainer)pair[1];
    }

    /**
     *
     */
    public String toString() {
        if (myIntroducer.isOnTheAir()) {
            return "<makeSturdyRef On The Air>";
        } else {
            return "<makeSturdyRef Off The Air>";
        }
    }
}
