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

import net.captp.tables.SwissTable;
import net.captp.tables.Vine;
import net.vattp.data.NetConfig;
import org.erights.e.elib.util.OneArgFunc;
import org.erights.e.extern.timer.Timeout;
import org.erights.e.extern.timer.Timer;

import java.math.BigInteger;
import java.net.MalformedURLException;

/**
 * An instance of the object is accessible in the privileged scope under the
 * name "identityMgr".
 * <p/>
 * It represents both more and less authority than the {@link
 * org.erights.e.extern.persist.SturdyRefMaker makeSturdyRef} function. It has
 * more authority in that it allows a form of conversion between capabilities
 * and bits -- specifically SwissBases -- and can therefore not be given to
 * objects you wish to confine in a <a href="http://www.erights.org/elib/capability/dist-confine.html"
 * >distributed confinement box</a>.
 * <p/>
 * It allows less authority, in that by itself it cannot cause an object to
 * survive a checkpoint/revive cycle. However, by making the SwissBases
 * available, it allows its clients to manually revive such objects.
 * <p/>
 * An IdentityMgr isn't itself Persistent, but the relevant instance is
 * expected to be an unscope-key, so a persistent object holding a persistent
 * pointer to that IdentityMgr will likely revive holding instead an
 * appropriate substitute IdentityMgr.
 *
 * @author Mark S. Miller
 */
public class IdentityMgr {

    private final Introducer myIntroducer;

    private final Timer myTimer;

    /**
     * @param introducer
     * @param timer
     */
    IdentityMgr(Introducer introducer, Timer timer) {
        myIntroducer = introducer;
        myTimer = timer;
    }

    /**
     * Returns an unguessable random number suitable for use as a swissBase.
     *
     * @return
     */
    public BigInteger nextSwiss() {
        return myIntroducer.getSwissTable().nextSwiss();
    }

    /**
     * Just delegates to {@link SwissTable#registerNewSwiss}
     *
     * @return
     */
    public BigInteger registerNewSwiss(Object obj, BigInteger swissBase) {
        SwissTable swissTable = myIntroducer.getSwissTable();
        BigInteger swissNum = swissTable.registerNewSwiss(obj, swissBase);
        return swissNum;
    }

    /**
     * Given an obj-swissNum association, make a SturdyRef that designates this
     * object across space (between vats, even after partitions) and possibly
     * across time (checkpoint/revives).
     * <p/>
     * This method doesn't itself arrange for the object to be retained. That's
     * up to its callers. They must arrange for the object to last until the
     * expiration time, so that the weak association in the SwissTable will
     * last till then as well.
     * <p/>
     * If the object is to survive this incarnation, its caller must arrange to
     * revive or reconstruct the object itself, and then to call {@link
     * #makeKnownAs} to re-establish the association between the object and its
     * swissNumber. The CapTP package does not provide such functionality
     * itself. But {@link org.erights.e.extern.persist.SturdyRefMaker} builds
     * this functionality on top of the API listed here.
     *
     * @param obj               The object for which a SturdyRef is desired
     * @param swissBase         The hash of which will be the swissNum
     *                          associated with this object.
     * @param optExpirationDate The date after which the SturdyRef will not
     *                          longer be guaranteed to be valid. If
     *                          Long.MAX_VALUE, then it's always valid.
     * @return A new SturdyRef for the indicated object
     */
    public SturdyRef makeSturdyRef(Object obj,
                                   BigInteger swissBase,
                                   long optExpirationDate)
      throws MalformedURLException {

        BigInteger swissNum = registerNewSwiss(obj, swissBase);

        NetConfig netConfig = myIntroducer.getNetConfig();
        LocatorUnum locatorUnum = myIntroducer.getLocatorUnum();
        return new SturdyRef(locatorUnum,
                             netConfig.getSearchPath(),
                             myIntroducer.getVatID(),
                             swissNum,
                             optExpirationDate);
    }

    /**
     * optExpirationDate defaults to forever
     */
    public Object[] makeKnown(Object obj) throws MalformedURLException {
        return makeKnown(obj, Long.MAX_VALUE);
    }

    /**
     * Produce a SturdyRef, and a swissBase so in a later incarnation of this
     * vat a newly created object can be made to be the reincarnation of this
     * one (from the perspective of those holding the SturdyRef).
     * <p/>
     * An object is that which object references to it designate. From one vat
     * incarnation to another, the only intervat references which survive are
     * SturdyRefs. When doing identity-persistence (as opposed to object
     * persistence), the application creates the first SturdyRef to a
     * persistent object using makeKnown(..), and makes sure to store the
     * swissBase somewhere for use during this vat's next incarnation. (In
     * identity-persistence, where this precious information is stored is up to
     * the app.)
     * <p/>
     * On the next incarnation of the vat, the app creates a fresh object to
     * serve as the reincarnation of the original object, and calls
     * makeKnownAs(..) so the old SturdyRef will now designate the new object.
     *
     * @param obj               The object for which a SturdyRef is desired
     * @param optExpirationDate The date after which the SturdyRef will not
     *                          longer be guaranteed to be valid. If
     *                          Long.MAX_VALUE, then it's always valid.
     * @return A triple of <ul> <li>A new SturdyRef for 'obj', <li>A {@link
     *         org.erights.e.extern.timer.Timeout} for cancelling this
     *         sturdiness of the object. Though, once a SturdyRef has been
     *         given out promising a given expirationDate, it's considered rude
     *         to cancel it without coordinating with those other parties.
     *         <p/>
     *         <i>Note that if an object is sturdified multiple times,
     *         cancelling a registration only cancels that one registration,
     *         leaving the others intact.</i> <li>A swissBase for reincarnating
     *         the identity assigned to 'obj'. </ul>
     */
    public Object[] makeKnown(Object obj, long optExpirationDate)
      throws MalformedURLException {

        BigInteger swissBase = nextSwiss();
        Timeout timeout = myTimer.whenAlarm(optExpirationDate, new Vine(obj));

        SturdyRef sr = makeSturdyRef(obj, swissBase, optExpirationDate);
        Object[] result = {sr, timeout, swissBase};
        return result;
    }

    /**
     * optExpirationDate defaults to forever
     */
    public Object[] makeKnownAs(Object obj, BigInteger swissBase)
      throws MalformedURLException {
        return makeKnownAs(obj, swissBase, Long.MAX_VALUE);
    }

    /**
     * Cause 'obj' to be the object designated by 'swissBase.cryptoHash()' in
     * this vat.
     * <p/>
     * Used by an identity-persistent app (as opposed to an object-persistent
     * app) to cause old SturdyRefs that were given out by a previous
     * incarnation of this vat to continue to function. It's polite for an
     * identity-peristent app to use an expiration date that's at least as big
     * as the ones used in previous incarnations. To do otherwise is to not
     * honor the previously implied obligation.
     *
     * @return A pair of a new SturdyRef for obj and a Timeout for cancelling
     *         this sturdiness of the object, as explained at {@link
     *         #makeKnown(Object,long)}.
     */
    public Object[] makeKnownAs(Object obj,
                                BigInteger swissBase,
                                long optExpirationDate)
      throws MalformedURLException {

        Timeout timeout = myTimer.whenAlarm(optExpirationDate, new Vine(obj));

        SturdyRef sr = makeSturdyRef(obj, swissBase, optExpirationDate);
        Object[] result = {sr, timeout};
        return result;
    }

    /**
     * @see SwissTable#addFaultHandler
     */
    public void addFaultHandler(OneArgFunc swissDB) {
        SwissTable st = myIntroducer.getSwissTable();
        st.addFaultHandler(swissDB);
    }

    /**
     * @see SwissTable#removeFaultHandler
     */
    public void removeFaultHandler(OneArgFunc swissDB) {
        SwissTable st = myIntroducer.getSwissTable();
        st.removeFaultHandler(swissDB);
    }

    /**
     *
     */
    public String toString() {
        if (myIntroducer.isOnTheAir()) {
            return "<IdentityMgr On The Air>";
        } else {
            return "<IdentityMgr Off The Air>";
        }
    }
}
