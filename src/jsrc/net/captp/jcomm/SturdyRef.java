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

import net.vattp.data.EARL;
import org.erights.e.develop.assertion.T;
import org.erights.e.elib.oldeio.EPrintable;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.ref.Ref;
import org.erights.e.elib.serial.PassByConstruction;
import org.erights.e.elib.serial.Persistent;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.util.OneArgFunc;

import java.io.IOException;
import java.math.BigInteger;
import java.net.MalformedURLException;

/**
 * An object reference that can be checkpointed and/or externalized and which
 * can survive network partitions.
 */
public final class SturdyRef
  implements Persistent, PassByConstruction, EPrintable {

    static private final long serialVersionUID = 1034104891471210303L;

    /**
     * The LocatorUnum that links us to the outside world.
     */
    private final LocatorUnum myLocatorUnum;

    /**
     * Hints about how our vat might be located.
     */
    private final ConstList mySearchPath;

    /**
     * the Vat in which mySwissNum is bound to something
     */
    private final String myHostID;

    /**
     * Bound to an object in the vat identified by myHostID
     */
    private final BigInteger mySwissNum;

    /**
     * How long is the object obligated to stay around?
     */
    private final long myExpiration;

    /**
     * Package scope constructor for a new SturdyRef.
     *
     * @param locatorUnum The LocatorUnum which maintains the new ref's
     *                    registration, and which gives the SturdyRef the
     *                    authority to obtain the live object. This component
     *                    serialized specially, so that only those that had a
     *                    LocatorUnum when they were serialized can obtain a
     *                    new one when unserialized.
     * @param searchPath  Hints about how to find the new ref's home vat
     * @param swissNum    Swiss number of the object that the new ref
     *                    designates
     */
    SturdyRef(LocatorUnum locatorUnum,
              ConstList searchPath,
              String hostID,
              BigInteger swissNum,
              long expiration) throws MalformedURLException {
        myLocatorUnum = locatorUnum;
        mySearchPath = searchPath;
        myHostID = hostID;
        mySwissNum = swissNum;
        myExpiration = expiration;
        if (searchPath.size() == 0) {
            throw new MalformedURLException("Search path must not be empty");
        }
    }

    /**
     * Test if this SturdyRef and another designate the same object.
     * <p/>
     * In the E language,
     * <pre>    x &lt;=&gt; y</pre>
     * is syntactic sugar for approximately
     * <pre>    x.op__cmp(y) == 0.0</pre>
     * Note that we could have provided a full order for the other cases, but
     * since SturdyRefs are supposed to hold their cryptographic bits opaqely,
     * for all non-same cases we always return NaN (incomparable).
     *
     * @param other SturdyRef against which we are to be tested for equality
     * @return 0.0 iff this and other designate the same object. Otherwise
     *         NaN.
     */
    public double op__cmp(SturdyRef other) {
        if (myHostID.equals(other.myHostID) &&
          mySwissNum.equals(other.mySwissNum)) {
            return 0.0;
        } else {
            return Double.NaN;
        }
    }

    /**
     * Test if this SturdyRef and another are fully equivalent SturdyRefs.
     * <p/>
     * If you wish to test instead whether they designate the same object by
     * virtue of the comparing VatID and SwissNumbers, but not comparing
     * incidentals like searchPath or expiration, then use {@link #op__cmp
     * "&lt;=&gt;"}.
     *
     * @param obj SturdyRef against which we are to be tested for equality
     * @return true iff this and obj are fully equivalent SturdyRefs.
     */
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof SturdyRef)) {
            return false;
        }
        SturdyRef other = (SturdyRef)obj;
        return (myLocatorUnum == other.myLocatorUnum &&
          mySearchPath.op__cmp(other.mySearchPath) == 0.0 &&
          myHostID.equals(other.myHostID) &&
          mySwissNum.equals(other.mySwissNum) &&
          myExpiration == other.myExpiration);
    }

    /**
     * Return a hashcode for this SturdyRef. <p>
     */
    public int hashCode() {
        return mySwissNum.hashCode();
    }

    /**
     * Package scope method to produce a URI string for this SturdyRef.
     *
     * @param otherLU A cheap Java-based way to do an amplification test in
     *                order to fix bug <a href= "https://bugs.sieve.net/bugs/?func=detailbug&bug_id=125628&group_id=16380"
     *                >SturdyRefs amplify even when they should not</a>
     * @return A URI string that refers to the same object as this SturdyRef
     *         designates.
     */
    String exportRef(LocatorUnum otherLU) throws MalformedURLException {
        T.require(myLocatorUnum == otherLU,
                  "SturdyRef must be from the same CapTP instance");
        EARL earl = new EARL(mySearchPath, myHostID, mySwissNum, myExpiration);
        return earl.getURI();
    }

    /**
     * Return a (live) reference to the object which this SturdyRef
     * designates.
     * <p/>
     * The result may be either a direct or eventual reference. If we cannot
     * <i>currently</i> connect to the object's hosting vat, then the result
     * will resolve to broken.
     * <p/>
     * getRcvr only works when we're {@link Introducer#onTheAir onTheAir}.
     */
    public Object getRcvr() throws IOException {
        return myLocatorUnum.getRcvr(mySearchPath, myHostID, mySwissNum, null);
    }

    /**
     * Like {@link #getRcvr()} but doesn't resolve the result until we connect
     * to the object's hosting vat.
     * <p/>
     * Note that the result may still resolve to broken, if the object's
     * hosting vat, for example, no longer has this object registered for
     * lookup by this SturdyRef (no longer has an entry for this SturdyRef's
     * SwissNumber).
     * <p/>
     * getRcvr only works when we're {@link Introducer#onTheAir onTheAir}.
     *
     * @param pollMillis A polling interval hint. Until we have VLS rendezvous
     *                   support, the best we can do is poll the attempt to
     *                   connect to the hosting vat. <tt>pollMillis</tt> says
     *                   how often we should do so, and thereby tradeoff
     *                   CPU/comm against promptness. Once we have VLS
     *                   rendezvous support, then we will post a long-lived
     *                   query with the relevant VLSs, and refresh this query
     *                   at a frequency determined by those VLSs, not by our
     *                   client. This refreshing is also a polling loop, but
     *                   can be long without impeding the responsiveness of
     *                   getting connected.
     * @param timeout    The time in absMillis (since the epoch) at which we
     *                   should give up trying to connect. Once the time has
     *                   passed, we should resolve the result to broken rather
     *                   than continuing to poll or refresh. A value of -1
     *                   means to keep trying forever.
     */
    public Object getRcvr(long pollMillis, long timeout) throws IOException {
        return Ref.whenResolved(getRcvr(), new OneArgFunc() {
            public Object run(Object ref) {
                if (Ref.isBroken(ref)) {
                    //XXX Bug: Note that if the proper target of the SturdyRef
                    //is a broken reference, this will mistake it for a failure
                    //to connect, and will substitute a broken ref with the
                    //wrong problem.
                    T.fail("XXX long-lived getRcvr not yet implemented");
                    return null; //make the compiler happy
                } else {
                    return ref;
                }
            }
        });
    }

    /**
     * @param out
     * @throws IOException
     */
    public void __printOn(TextWriter out) throws IOException {
        Introducer introducer = myLocatorUnum.myIntroducer;
        if (myHostID.equals(introducer.getVatID())) {
            Object referent =
              introducer.getSwissTable().lookupSwiss(mySwissNum);
            if (Ref.isNear(referent)) {
                out.print("<SturdyRef to ");
                out.quote(referent);
                out.print(">");
                return;
            }
        }
        out.print("<SturdyRef>");
    }
}
