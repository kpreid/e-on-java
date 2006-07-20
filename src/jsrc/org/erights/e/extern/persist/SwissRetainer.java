// Copyright 2003 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.extern.persist;

import net.captp.jcomm.IdentityMgr;
import org.erights.e.elib.prim.StaticMaker;
import org.erights.e.elib.sealing.Amplifiable;
import org.erights.e.elib.sealing.Brand;
import org.erights.e.elib.sealing.SealedBox;
import org.erights.e.elib.serial.PersistentKeyHolder;
import org.erights.e.elib.tables.FlexMap;
import org.erights.e.extern.timer.Retainer;
import org.erights.e.extern.timer.Timer;
import org.erights.e.meta.java.math.EInt;

import java.math.BigInteger;
import java.net.MalformedURLException;

/**
 * A Retainer that also registers the payload in the SwissTable using the
 * swissBase.
 *
 * @author Mark S. Miller
 */
public class SwissRetainer extends Retainer implements Amplifiable {

    static private final StaticMaker SwissRetainerMaker =
      StaticMaker.make(SwissRetainer.class);

    private Object myPayload;

    private IdentityMgr myIdentityMgr;

    private BigInteger mySwissBase;

    public SwissRetainer(Timer optTimer,
                         long optExpirationDate,

                         Object payload,
                         IdentityMgr identityMgr,
                         BigInteger swissBase)
      throws MalformedURLException {
        super(optTimer, optExpirationDate);
        myPayload = payload;
        myIdentityMgr = identityMgr;
        mySwissBase = swissBase;
    }

    /**
     * Registers the payload in the SwissTable using the swissBase.
     * <p/>
     * After construction or unserialization, the SwissRetainer isn't in a
     * valid state until init() is called. We don't automate this with
     * readResolve() in order to avoid a circular unserialization bug.
     */
    protected void init(FlexMap retainers) {
        super.init(retainers);
        myIdentityMgr.registerNewSwiss(myPayload, mySwissBase);
    }

    /**
     *
     */
    public void run() {
        super.run();
        myPayload = null;
        myIdentityMgr = null;
        mySwissBase = null;
    }

    /**
     * @return
     */
    public SealedBox __optSealedDispatch(Brand brand) {
        if (PersistentKeyHolder.THE_BRAND == brand) {
            Object[] args = {
                myOptTimer,
                EInt.valueOf(myOptExpirationDate),
                myPayload,
                myIdentityMgr,
                mySwissBase
            };
            Object[] uncall = {SwissRetainerMaker, "run", args};
            return PersistentKeyHolder.THE_SEALER.seal(uncall);
        } else {
            return null;
        }
    }
}
