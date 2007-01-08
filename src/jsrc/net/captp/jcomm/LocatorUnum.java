package net.captp.jcomm;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.develop.trace.Trace;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.serial.JOSSPassByConstruction;
import org.erights.e.elib.tables.ConstList;

import java.io.IOException;
import java.math.BigInteger;

/**
 * Each instance of this class represents a presence of the pervasive
 * LocatorUnum service.
 * <p/>
 * For Una in general: To hold a reference to any presence of the Unum is
 * conceptually to hold a reference to the Unum as a whole. Therefore, a
 * reference to the Unum may as well always be a reference to a local presence
 * of the Unum. Since it can be, and since this would provide better service,
 * we specify that all references to an Unum will be local.
 * <p/>
 * Therefore, an encoded reference to a presence of Unum that's local to the
 * sending side will be decoded as a reference to a presence of the same Unum
 * local to the receiving side. Therefore, a fulfilled reference to an Unum is
 * always <a href="http://www.erights.org/elib/concurrency/refmech.html">Near</a>,
 * and therefore a reference to an Unum is always eventually Near or Broken.
 * <p/>
 * The LocatorUnum is the only Unum built into E itself, and the only Unum
 * currently supported. It represents the pervasive vatID/swissNumber lookup
 * service built jointly out of all vats and VLSes. It is used only by
 * SturdyRefs to establish their authority to perform a lookup, and to enable
 * SturdyRefs to maintain this authority as they are copied between vats.
 * <p/>
 * Although this class isn't declared Persistent, the relevant instance is
 * expected to be an unscope-key.
 * <p/>
 * XXX In CapTP, we should use the unscope technique as well rather than the
 * LocatorUnumDesc.
 *
 * @author Mark S. Miller
 */
public class LocatorUnum implements JOSSPassByConstruction {

    static private final long serialVersionUID = 6565076271340985687L;

    final Introducer myIntroducer;

    /**
     * @param introducer
     */
    public LocatorUnum(Introducer introducer) {
        myIntroducer = introducer;
    }

    /**
     *
     */
    private Object writeReplace() {
        return LocatorUnumDesc.THE_ONE;
    }

    /**
     * The basic operation underlying 'SturdyRef.getRcvr()'.
     * <p/>
     * This is where a reference to a remote object actually gets the various
     * underlying comm systems connected so that we can send messages.
     * <p/>
     * getRcvr only works when we're {@link Introducer#onTheAir onTheAir}.
     *
     * @param searchPath A list of places to try to find the vat
     * @param vatID      The vat from which the object reference should be
     *                   obtained
     * @param swissNum   The SwissNumber of the desired object
     * @param optFarVine Optional object for holding onto a Remote reference
     *                   via whoever we got it from long enough to fetch our
     *                   own Remote reference via this lookup.
     */
    public Object getRcvr(ConstList searchPath,
                          String vatID,
                          BigInteger swissNum,
                          Object optFarVine)
      throws IOException, IndexOutOfBoundsException {
        if (Trace.captp.debug && Trace.ON) {
            Trace.captp
              .debugm("" + this + ".getRcvr(" + searchPath + ", " + vatID +
                ", " + swissNum + ", " + optFarVine + ")");
        }
        if (vatID.equals(myIntroducer.getVatID())) {

            //if we are following into the local vat, do a local lookup.
            //If not found, it'll throw an IndexOutOfBoundsException
            return myIntroducer.getSwissTable().lookupSwiss(swissNum);
        }
        CapTPConnection optProxyConn = myIntroducer.getCapTPMgr()
          .getOrMakeProxyConnection(searchPath, vatID);
        Object remoteNonceLocator = optProxyConn.getRemoteNonceLocator();
        return E.send(remoteNonceLocator, "lookupSwiss", swissNum, optFarVine);
    }
}
