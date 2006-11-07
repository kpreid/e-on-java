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
import net.vattp.data.EARL;
import net.vattp.data.NetConfig;
import net.vattp.data.StartUpProtocol;
import net.vattp.data.VatIdentity;
import net.vattp.security.ESecureRandom;
import org.erights.e.develop.exception.ExceptionMgr;
import org.erights.e.elib.ref.NullMonitor;
import org.erights.e.elib.ref.ReferenceMonitor;
import org.erights.e.elib.serial.BaseLoader;
import org.erights.e.elib.serial.PassByProxy;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.ConstMap;
import org.erights.e.extern.timer.Timer;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.security.KeyPair;

/**
 * This object represents an instance of the CapTP system, and is in the
 * privileged scope under the names <tt>captp__uriGetter</tt> and
 * <tt>introducer</tt>.
 * <p/>
 * An Introducer may already have an identity assigned, in which case it's
 * identified; or not, in which case it's unidentified. Introducers start
 * off unidentified, but once identified, always identified.
 * <p/>
 * An Introducer may be on-the-air or off-the-air. If an unidentified
 * Introducer goes on-the-air, it will first (privately) generate an identity
 * to identify itself. Since only an identified Introducer may be
 * on-the-air, we have three states altogether. For now, an on-the-air
 * Introducer doesn't yet have a way to go back off-the-air, but we expect to
 * add such an operation.
 * <p/>
 * An Introducer isn't itself Persistent, but the relevant instance is
 * expected to be an unscope-key, so a persistent object holding a persistent
 * pointer to that Introducer will likely revive holding instead an
 * appropriate substitute Introducer.
 *
 * @author Mark S. Miller
 */
public class Introducer extends BaseLoader implements PassByProxy {

    /**
     * Changes by going on-the-air
     */
    private NetConfig myNetConfig;

    private final ESecureRandom myEntropy;

    private final LocatorUnum myLocatorUnum;

    /**
     * if identified
     */
    private KeyPair myOptVatIdentity = null;

    /**
     * if identified
     */
    private SwissTable myOptSwissTable = null;

    /**
     * if onTheAir
     */
    private CapTPMgr myOptCapTPMgr = null;

    private ReferenceMonitor myRefmon;

    /**
     *
     */
    private Introducer(ConstMap optProps, ESecureRandom entropy) {
        myNetConfig = NetConfig.make(optProps);
        myEntropy = entropy;
        myLocatorUnum = new LocatorUnum(this);
        myRefmon = NullMonitor.THE_ONE;
    }

    /**
     * Returns a pair of <ul>
     * <li>a new Introducer,
     * <li>a corresponding IdentityMgr.
     * </ul>
     */
    static public Object[] makePair(ConstMap optProps,
                                    ESecureRandom entropy,
                                    Timer timer) {
        Introducer introducer = new Introducer(optProps, entropy);
        IdentityMgr identityMgr = new IdentityMgr(introducer, timer);
        Object[] result = {introducer, identityMgr};
        return result;
    }

    /**
     * Configuration parameter read as a JavaBeans property.
     */
    public NetConfig getNetConfig() {
        return myNetConfig;
    }

    /**
     * Configuration parameter set as a JavaBeans property, so long as
     * we are off the air.
     */
    public void setNetConfig(NetConfig newNetConfig) {
        if (isOnTheAir()) {
            throw new SecurityException
              ("Must be off the air to change NetConfig parameters");
        }
        myNetConfig = newNetConfig;
    }

    /**
     *
     * @param refmon
     */
    public void setReferenceMonitor(ReferenceMonitor refmon) {
        if (isOnTheAir()) {
            throw new SecurityException
              ("Must be off the air to set a reference monitor");
        }
        myRefmon = refmon;
    }

    /**
     * Is this Introducer's identity (and therefore, this vat's identity)
     * already determined?
     */
    public boolean hasIdentity() {
        return null != myOptVatIdentity;
    }

    /**
     * If no identity has yet been determined, generate a new one, and return
     * its public/private key pair.
     * <p/>
     * This pair conveys the authority to claim to be this vat, so guard it
     * well.
     *
     * @throws SecurityException if an identity has already been
     *                           determined.
     */
    public KeyPair newVatIdentity() {
        if (hasIdentity()) {
            throw new SecurityException("Already identified");
        }
        myOptVatIdentity = VatIdentity.generateKeyPair(myEntropy);
        myOptSwissTable = new SwissTable(myEntropy);
        return myOptVatIdentity;
    }

    /**
     * If no identity has yet been determined, become identified as the
     * identity represented by this key pair.
     * <p/>
     * To implement identity-persistence, the birth incarnation of a vat
     * should do a newVatIdentity and remember the resulting key pair.
     * Reincarnations of the "same" vat should then do a setVatIdentity with
     * the saved identity, in order to be the reincarnation of the previous
     * vat.
     *
     * @throws SecurityException if an identity has already been
     *                           determined.
     */
    public void setVatIdentity(KeyPair identity) {
        if (hasIdentity()) {
            throw new SecurityException("Already identified");
        }
        myOptVatIdentity = identity;
        //XXX since we're not using it to generate a key pair, is there
        //anything else we need to do so myEntropy will be ready?
        myOptSwissTable = new SwissTable(myEntropy);
    }

    /**
     * For use by the SturdyRefMaker and IdentityMgr
     */
    SwissTable getSwissTable() {
        if (!hasIdentity()) {
            throw new SecurityException("introducer not yet identified");
        }
        return myOptSwissTable;
    }

    /**
     * The fingerprint of the public key of this Introducers identity (and
     * therefore, this vat's identity).
     * <p/>
     * An identity must have already been determined.
     */
    public String getVatID() {
        if (!hasIdentity()) {
            throw new SecurityException("introducer not yet identified");
        }
        return VatIdentity.calculateVatID(myOptVatIdentity.getPublic());
    }

    /**
     * Is this vat able to send and receive inter-vat messages?
     */
    public boolean isOnTheAir() {
        return null != myOptCapTPMgr;
    }

    /**
     * Become able to communicate. <p>
     * <p/>
     * Change NetConfig according to the listen addresses we actually
     * acquired. Return the list of negotiable protocols. <p>
     * <p/>
     * If not yet identified, this will privately generate a new vat
     * identity, but not reveal it through any public protocol. Therefore,
     * applications that wish to do their own identity-persistence must first
     * call either newVatIdentity() or setVatIdentity(..).
     */
    public ConstList onTheAir()
      throws UnknownHostException, IOException {
        if (isOnTheAir()) {
            return negotiable();
        }

        if (!hasIdentity()) {
            newVatIdentity();
        }

        /* Turn on the comm system... */
        myOptCapTPMgr = new CapTPMgr(myOptVatIdentity,
                                     myNetConfig,
                                     myOptSwissTable,
                                     myEntropy,
                                     myLocatorUnum,
                                     myRefmon);
        myNetConfig = myOptCapTPMgr.getNetConfig();
        return negotiable();
    }

    /**
     * @return
     */
    CapTPMgr getCapTPMgr() {
        if (!isOnTheAir()) {
            throw new SecurityException("Must first be onTheAir");
        }
        return myOptCapTPMgr;
    }

    /**
     *
     */
    public LocatorUnum getLocatorUnum() {
        return myLocatorUnum;
    }

    /**
     * Produce a SturdyRef given a URI.
     *
     * @param uri An E format ("&lt;captp://...&gt;") URI string referring to
     *            some object.
     * @return A SturdyRef matching the given URI.
     */
    public SturdyRef sturdyFromURI(String uri) throws MalformedURLException {
        EARL earl = new EARL(uri);
        return new SturdyRef(getLocatorUnum(),
                             earl.searchPath(),
                             earl.vatID(),
                             earl.swissNumber(),
                             earl.expiration());
    }

    /**
     * Produce a URI given a SturdyRef.
     *
     * @param ref The SturdyRef whose URI is desired
     * @return An E format ("<captp://...>") URI string matching the SturdyRef
     */
    public String sturdyToURI(SturdyRef ref) throws MalformedURLException {
        return ref.exportRef(myLocatorUnum);
    }

    /**
     * The list of protocols I can negotiate to speak.
     */
    public ConstList negotiable() {
        return ConstList.fromArray(StartUpProtocol.authProtocolTable());
    }

    /**
     * Same as sturdyFromURI, but named get/1 so the introducer can be used
     * as a URIGetter.
     * @param uriBody
     */
    public Object get(String uriBody) {
        try {
            return sturdyFromURI(uriBody);
        } catch (MalformedURLException e) {
            throw ExceptionMgr.asSafe(e);
        }
    }

    /**
     * @return
     */
    public Object[] optUncall(Object obj) {
        if (!(obj instanceof SturdyRef)) {
            return null;
        }
        String uri;
        try {
            uri = sturdyToURI((SturdyRef)obj);
        } catch (MalformedURLException e) {
            return null;
        }
        if (!uri.startsWith("captp:")) {
            return null;
        }
        String suffix = uri.substring("captp:".length());
        return BaseLoader.ungetToUncall(this, suffix);
    }

    /**
     *
     */
    public String toString() {
        if (isOnTheAir()) {
            return "<On The Air " + negotiable() + ">";
        } else {
            return "<Off The Air>";
        }
    }
}
