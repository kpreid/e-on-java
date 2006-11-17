package net.captp.tables;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.elib.base.ValueThunk;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.ref.Ref;
import org.erights.e.elib.ref.Resolver;
import org.erights.e.elib.tables.FlexMap;
import org.erights.e.elib.vat.WeakPtr;

import java.math.BigInteger;

/**
 * Rendezvous point for 3-vat live Granovetter introductions of Unresolved
 * remote references (RemotePromises).
 * <p/>
 * Where we normally speak of Alice giving Bob a reference to Carol, we here
 * speak of the Donor (VatA) giving to the Recipient (VatB) a reference to the
 * Gift (Carol) residing on the Host (VatC). There is one PromiseGiftTable in
 * the Host per Donor. The Donor associates the gift with a Nonce and the
 * Recipient's ID, and she tells the Nonce to the Recipient. The Recipient asks
 * the Host "What gift has Donor (identified by DonorID) deposited for me at
 * this Nonce?".
 * <p/>
 * A Nonce is a use once unique number. It only needs to be "impossible" to
 * collide under benevolent assumptions; it doesn't need to be unguessable, so
 * we just use a 64 bit 'long'.
 * <p/>
 * Two interesting complexities: 1) The Recipient's lookup request may arrive
 * at the Host before the Donors donation request does. 2) A partition could
 * prevent the operation from completing, in which case the Recipient cannot be
 * left hanging, and the association must be cleaned up.
 * <p/>
 * For 3-vat live Granovetter introductions of Resolved remote references
 * (FarRefs), see the NearGiftTable.
 *
 * @author Mark S. Miller.
 */
public class PromiseGiftTable {

    /**
     * Maps from [recipID, Nonce] to [referent, optResolver]
     */
    private FlexMap myStuff;

    /**
     * A RemotePromise for the Donor's NonceLocator.
     */
    private final Object myNonceLocatorPromise;

    /**
     * @param nonceLocatorPromise A RemotePromise for the Donor's NonceLocator.
     *                            The ignore(vine) message is sent to it, so
     *                            that it can ignore it.
     */
    public PromiseGiftTable(Object nonceLocatorPromise) {
        myStuff = FlexMap.fromTypes(Object[].class, Object[].class);
        myNonceLocatorPromise = nonceLocatorPromise;
    }

    /**
     * Smash all untaken promises for undonated gifts, and then disable this
     * table.
     */
    public void smash(Throwable problem) {
        Object[][] vals = (Object[][])myStuff.getValues(Object[].class);
        for (int i = 0; i < vals.length; i++) {
            Resolver optResolver = (Resolver)vals[i][1];
            if (null != optResolver) {
                optResolver.smash(problem);
            }
        }
        myStuff = null;
    }

    /**
     * Make the gift available to the recipient so long as the Vine is held
     * onto (isn't garbage).
     */
    public Vine provideFor(Object gift, String recipID, long nonce) {
        Object[] keyPair = {recipID, BigInteger.valueOf(nonce)};
        Object[] optValuePair =
          (Object[])myStuff.fetch(keyPair, ValueThunk.NULL_THUNK);
        Vine result = new Vine(null);
        if (null == optValuePair) {
            //donation happened first.
            Object[] valuePair = {gift, null};
            myStuff.put(keyPair, valuePair);
            //XXX do we need to hold onto the weakPtr in order for it to be
            //finalized?  If so, then we must.
            WeakPtr weakPtr = new WeakPtr(result, this, "drop", keyPair);
        } else {
            //resolve the promise for a gift already looked up. Done.
            Resolver resolver = (Resolver)optValuePair[1];
            resolver.resolve(gift);
            myStuff.removeKey(keyPair);
        }
        return result;
    }

    /**
     * Lookup the gift. <p>
     * <p/>
     * If absent, return a promise for what will be given, and sends an
     * ignore(vine) to the nonceLocatorPromise. The promise is good as long as
     * the vine is held. <p>
     * <p/>
     * Note that this message is named "acceptFor" whereas the corresponding
     * NonceLocator message is named "acceptFrom". In acceptFor, the recipient
     * is explicit and the donor implicit. In acceptFrom, the donor is explicit
     * and the recipient implicit.
     *
     * @param recipID The vatID of the vat (Bob, the gift recipient) that the
     *                gift is being picked up for.
     * @param nonce   Identifies (together with the recipID) the gift.
     */
    public Object acceptFor(String recipID, long nonce) {
        Object[] keyPair = {recipID, BigInteger.valueOf(nonce)};
        Object[] optValuePair =
          (Object[])myStuff.fetch(keyPair, ValueThunk.NULL_THUNK);
        if (null == optValuePair) {
            //lookup happened first, promise a gift to be donated.
            optValuePair = Ref.promise();
            myStuff.put(keyPair, optValuePair);
            Vine vine = new Vine(null);
            //XXX do we need to hold onto the weakPtr in order for it to be
            //finalized?  If so, then we must.
            WeakPtr weakPtr = new WeakPtr(vine, this, "drop", keyPair);
            E.sendOnly(myNonceLocatorPromise, "ignore", vine);
        } else {
            //Done.
            myStuff.removeKey(keyPair);
        }
        return optValuePair[0];
    }

    /**
     * Automagically called when the vine is dropped. <p> The Donor is able to
     * call this as well, which isn't appropriate, but isn't dangerous, so what
     * the hell.
     */
    public void drop(String recipID, long nonce) {
        Object[] keyPair = {recipID, BigInteger.valueOf(nonce)};
        Object[] optValuePair =
          (Object[])myStuff.fetch(keyPair, ValueThunk.NULL_THUNK);
        if (null == optValuePair) {
            return;
        }
        Resolver optResolver = (Resolver)optValuePair[1];
        if (null != optResolver) {
            optResolver.smash(E.asRTE("The vine was dropped: " + nonce));
        }
        myStuff.removeKey(keyPair);
    }
}
