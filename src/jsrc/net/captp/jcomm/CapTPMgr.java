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
import net.vattp.data.NetConfig;
import net.vattp.data.NewConnectionReactor;
import net.vattp.data.VatLocationLookup;
import net.vattp.data.VatTPConnection;
import net.vattp.data.VatTPMgr;
import net.vattp.security.ESecureRandom;
import org.erights.e.develop.exception.ExceptionMgr;
import org.erights.e.develop.trace.Trace;
import org.erights.e.elib.ref.ReferenceMonitor;
import org.erights.e.elib.ref.Resolver;
import org.erights.e.elib.sealing.Brand;
import org.erights.e.elib.sealing.Sealer;
import org.erights.e.elib.sealing.Unsealer;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.FlexMap;

import java.io.IOException;
import java.net.UnknownHostException;
import java.security.KeyPair;

/**
 * Manage the interface between this vat and other vats. <p>
 * <p/>
 * The CapTPMgr acts as the focal point for managing the point-to-point
 * connections between vats. It is the application layer's only interface with
 * the low-level comm system.
 */
public class CapTPMgr implements NewConnectionReactor {

    /**
     * Registration table to use for map between ObjectIDs and objs
     */
    private final SwissTable mySwissTable;

    /**
     * map from DataConnections to ProxyConnections
     */
    private FlexMap myProxyConnections = null;

    /**
     * The connections manager to talk to
     */
    private VatTPMgr myConnMgr = null;

    /**
     * makes new unguessable numbers
     */
    private final ESecureRandom myEntropy;

    /**
     * for self recognition
     */
    final Sealer mySealer;

    final Unsealer myUnsealer;

    /** */
    private final LocatorUnum myLocatorUnum;

    /**
     *
     */
    private final ReferenceMonitor myRefmon;

    /**
     * Constructor
     *
     * @param identityKeys The identity of the vat we are managing remote
     *                     references into.
     * @param netConfig    Where to listen, register, and tell other to look
     *                     for me.
     * @param swissTable   Associates swissNumbers with references.
     * @param entropy      Provides new unguessable numbers.
     */
    CapTPMgr(KeyPair identityKeys,
             NetConfig netConfig,
             SwissTable swissTable,
             ESecureRandom entropy,
             LocatorUnum locatorUnum,
             ReferenceMonitor refmon)
      throws UnknownHostException, IOException {

        myConnMgr = new VatTPMgr(identityKeys, netConfig);

        myConnMgr.addNewConnectionReactor(this);

        mySwissTable = swissTable;
        myProxyConnections =
          FlexMap.fromTypes(VatTPConnection.class, CapTPConnection.class);
        myEntropy = entropy;

        Object[] pair = Brand.run("captp");
        mySealer = (Sealer)pair[0];
        myUnsealer = (Unsealer)pair[1];

        myLocatorUnum = locatorUnum;

        myRefmon = refmon;

        if (Trace.captp.debug && Trace.ON) {
            Trace.captp
              .debugm("Create CapTPMgr " + this + " connMgr=" + myConnMgr);
        }
    }

    /**
     * Do the work of setting up a new CapTPConnection, regardless of which end
     * initiated the transaction. If there is already an existing connection to
     * the indicated party, just use it.
     *
     * @param dataConn The VatTPConnection upon which we are communicating.
     * @return The resulting new (or existing) CapTPConnection.
     */
    private CapTPConnection arrangeProxyConnection(VatTPConnection dataConn)
      throws IOException {
        CapTPConnection proxyConn;
        try {
            /* First, look for an existing one... */
            proxyConn = (CapTPConnection)myProxyConnections.get(dataConn);
        } catch (IndexOutOfBoundsException ex) {
            /* Well, that didn't work. Make a new one, then. */
            proxyConn = new CapTPConnection(this, dataConn, myEntropy);
            myProxyConnections.put(dataConn, proxyConn);
        }
        if (Trace.captp.debug && Trace.ON) {
            Trace.captp
              .debugm("CapTPMgr " + this + " arrangeConnection " + dataConn +
                "->" + proxyConn);
        }
        return proxyConn;
    }

    /**
     * Be informed that one of our connections has died.
     *
     * @param connection         The connection that died
     * @param optBufferedLookups SturdyRef lookup requests that were issued in
     *                           the midst of orderly shutdown (these need to
     *                           be resubmitted).
     */
    void connectionDead(VatTPConnection connection,
                        Resolver optBufferedLookups) {
        myProxyConnections.removeKey(connection, true);
        if (null == optBufferedLookups) {
            return;
        }
        /* Get back into it, then */
        try {
            CapTPConnection proxyConn =
              getOrMakeProxyConnection(connection.getRemoteSearchPath(),
                                       connection.getRemoteVatID());
            proxyConn.submitLookups(optBufferedLookups);
        } catch (IOException ioe) {
            optBufferedLookups.smash(ioe);
        } catch (Throwable problem) {
            optBufferedLookups.smash(problem);
            throw ExceptionMgr.asSafe(problem);
        }
    }

    /**
     *
     */
    SwissTable getSwissTable() {
        return mySwissTable;
    }

    /**
     * Notice that a new (inbound) data connection has appeared.
     *
     * @param connection The new VatTPConnection object.
     * @see net.vattp.data.NewConnectionReactor
     */
    public void reactToNewConnection(VatTPConnection connection) {
        if (Trace.captp.debug && Trace.ON) {
            Trace.captp
              .debugm("CapTPMgr " + this + " reactToNewConnection " +
                connection + " rvat=%" + connection.getRemoteVatID() +
                " lvat=%" + connection.getLocalVatID());
        }
        try {
            arrangeProxyConnection(connection);
        } catch (IOException e) {
            /* I guess it wasn't meant to be... */
        }
    }

//    /**
//     * If I've got a live CapTPConnection for that vatID, return it, else
//     * null.
//     * <p>
//     * This does *not* initiate a connection attempt.
//     */
//    CapTPConnection optProxyConnection(String vatID) throws IOException {
//        VatTPConnection optDataConn = myConnMgr.optConnection(vatID);
//        if (null == optDataConn) {
//            return null;
//        } else {
//            return arrangeProxyConnection(optDataConn);
//        }
//    }

    /**
     * If vatID is me, return null, else get or make a live CapTPConnection for
     * vatID. <p>
     */
    CapTPConnection getOrMakeProxyConnection(ConstList searchPath,
                                             String vatID) throws IOException {
        VatTPConnection optDataConn =
          myConnMgr.getConnection(vatID, searchPath);
        if (null == optDataConn) {
            return null;
        } else {
            return arrangeProxyConnection(optDataConn);
        }
    }

    /**
     * Return the search path to our own vat.
     */
    public ConstList searchPath() {
        return myConnMgr.searchPath();
    }

    /**
     * Return the post-on-the-air configuration parameters
     */
    public NetConfig getNetConfig() {
        return myConnMgr.getNetConfig();
    }

    /**
     * Set the object which will respond to vat location queries on our listen
     * port.
     *
     * @param vls The VLS object
     */
    public void setVatLocationLookup(VatLocationLookup vls) {
        myConnMgr.setVatLocationLookup(vls);
    }

    /**
     *
     */
    public LocatorUnum getLocatorUnum() {
        return myLocatorUnum;
    }

    /**
     *
     */
    public ReferenceMonitor getReferenceMonitor() {
        return myRefmon;
    }
}
