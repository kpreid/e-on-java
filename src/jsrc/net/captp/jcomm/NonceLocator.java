package net.captp.jcomm;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import net.captp.tables.NearGiftTable;
import net.captp.tables.PromiseGiftTable;
import net.captp.tables.SwissTable;
import net.captp.tables.Vine;
import org.erights.e.develop.assertion.T;
import org.erights.e.develop.trace.Trace;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.ref.Ref;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.meta.java.math.BigIntegerSugar;

import java.io.IOException;
import java.math.BigInteger;

/**
 * Made magically available at incoming position 0.
 * <p/>
 * Used to resolve 3-vat live Granovetter introductions, and to log tracing
 * info sent from the other vat.
 *
 * @author Mark S. Miller
 */
public class NonceLocator {

    /**
     *
     */
    private final PromiseGiftTable myPGifts;

    /**
     *
     */
    private final NearGiftTable myNGifts;

    /**
     *
     */
    private final String myOwnID;

    /**
     *
     */
    private final CapTPMgr myCapTPMgr;

    /**
     *
     */
    private final SwissTable mySwissTable;

    /**
     *
     */
    NonceLocator(PromiseGiftTable pGifts,
                 NearGiftTable nGifts,
                 String ownID,
                 CapTPMgr capTPMgr,
                 SwissTable swissTable) {
        myPGifts = pGifts;
        myNGifts = nGifts;
        myOwnID = ownID;
        myCapTPMgr = capTPMgr;
        mySwissTable = swissTable;
    }

    /**
     *
     */
    public Vine provideFor(Object gift,
                           String recipID,
                           long nonce) {
        return myPGifts.provideFor(gift, recipID, nonce);
    }

    /**
     *
     */
    public Vine provideFor(Object gift,
                           String recipID,
                           long nonce,
                           BigInteger swissHash) {
        T.require(Ref.isNear(gift),
                  "Must be Near: ", gift);
        //If gift isn't Selfish, this will throw an exception,
        //which is as it should be.
        BigInteger giftSwiss = mySwissTable.getIdentity(gift);
        BigInteger giftHash = BigIntegerSugar.cryptoHash(giftSwiss);
        T.require(giftHash.equals(swissHash),
                  "wrong hash: ", swissHash);
        return myNGifts.provideFor(gift, recipID, nonce, swissHash);
    }

    /**
     * @param donorID    The vatID of the vat (Alice, the gift giver) that
     *                   provided the gift we're picking up.
     * @param nonce      Identifies (together with myOwnID) the gift in the
     *                   donor's table.
     * @param optFarVine Justs hold onto it until the request is done, to
     *                   prevent it from being gced.
     */
    public Object acceptFrom(ConstList donorPath,
                             String donorID,
                             long nonce,
                             Object optFarVine)
      throws IOException {
        CapTPConnection optDonorConn =
          myCapTPMgr.getOrMakeProxyConnection(donorPath, donorID);
        if (null == optDonorConn) {
            return Ref.broken(E.asRTE("The donor is gone"));
        }
        PromiseGiftTable donorTable = optDonorConn.getPromiseGiftTable();
        return donorTable.acceptFor(myOwnID, nonce);
    }

    /**
     * @param donorID    The vatID of the vat (Alice, the gift giver) that
     *                   provided the gift we're picking up.
     * @param nonce      Identifies (together with myOwnID) the gift in the
     *                   donor's table.
     * @param swissHash  The gift should only be returned if it has this
     *                   identity. Otherwise the recipient should get a
     *                   DisconnectedRef. This isn't yet fully implemented.
     * @param optFarVine Justs hold onto it until the request is done, to
     *                   prevent it from being gced.
     */
    public Object acceptFrom(ConstList donorPath,
                             String donorID,
                             long nonce,
                             BigInteger swissHash,
                             Object optFarVine)
      throws IOException {
        CapTPConnection optDonorConn =
          myCapTPMgr.getOrMakeProxyConnection(donorPath, donorID);
        if (null == optDonorConn) {
            return Ref.broken(E.asRTE("The donor is gone"));
        }
        NearGiftTable donorTable = optDonorConn.getNearGiftTable();
        Object result = donorTable.acceptFor(myOwnID, nonce, swissHash);
        if (!Ref.isNear(result)) {
            T.fail("internal: non-near gift for " +
                   swissHash);
        }
        //If result isn't Selfish, this will throw an exception,
        //which is as it should be.
        BigInteger id = mySwissTable.getIdentity(result);
        BigInteger idHash = BigIntegerSugar.cryptoHash(id);
        if (!swissHash.equals(idHash)) {
            T.fail("internal: hash mismatch: " +
                   swissHash);
        }
        return result;
    }

    /**
     * Do nothing, letting the argument become garbage. <p>
     * <p/>
     * The purpose of the message is to ensure that the argument isn't
     * garbage until the message is delivered.
     */
    public void ignore(Object optFarVine) {
    }

    /**
     *
     */
    public Object lookupSwiss(BigInteger swissNum, Object optFarVine) {
        return mySwissTable.lookupSwiss(swissNum);
    }

    /**
     * Enables our counterparty to log a message to our tracing system.
     * <p/>
     * These messages are tagged with the vatID of our counterparty. They
     * are logged at debug level, and currently to the "captp" subsystem.
     * These should probably instead have their own subsystem.
     */
    public void traceRemote(String message) {
        if (Trace.captp.debug && Trace.ON) {
            Trace.captp.debugm(myOwnID + ": " + message);
        }
    }
}
