package net.vattp.data;

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
import org.erights.e.develop.trace.Trace;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.ref.Ref;
import org.erights.e.elib.ref.Resolver;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.FlexList;
import org.erights.e.elib.vat.Vat;

import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyPair;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Manage the connections between this vat and other vats.
 * <p/>
 * <p> The VatTPMgr acts as the focal point for managing the TCP connections
 * from a particular vat. It uses the public/private key pair for the vat,
 * which defines the vat's identity.
 * <p/>
 * <p>The important method(s) are getConnection(...) which returns a
 * VatTPConnection to a remote vat, and addNewConnectionReactor which lets the
 * higher layers connect themselves to connections which have completed the
 * startup protocol.
 *
 * @author Bill Frantz
 * @author Mark S. Miller
 */
public class VatTPMgr {

    // Identity stuff
    private final KeyPair myIdentityKeys;

    /**
     * The vatID of the local vat. The hash of this end's public key.
     */
    private final String myLocalVatID;

    // VLS registration stuff
    private final NetConfig myNetConfig;

    /**
     * VLS registrations that we maintain
     */
    private VatLocationLookup myVLS;

    /**
     * Connection to the E run queue
     */
    private final Vat myVat;

    /**
     * The threads which are listening for incoming connections
     */
    private final ListenThread[] myListenThreads;

    /**
     * The object to notify about newly available connections
     */
    private NewConnectionReactor myReactor;

    // The following fields maintain knowledge of the existing
    // VatTPConnection objects. A VatTPConnection object start in either
    // the myPendingOutgoingConnections or myPendingIncomingConnections
    // table. When it finishes the startup protocol, it moves to the
    // myRunningDataConnections table. When it start to shutdown, it moves
    // to the myDieingConnections table. When it completes it's shutdown,
    // it is removed from all tables so it may be garbage collected.

    /**
     * The unidentified connections which haven't completed the start up
     * protocol. The key is the DataPath object. The value is the DataPath
     * object if this entry is for an incoming connections, or the Resolver if
     * the path was created for a connectToVatAt connection.
     */
    private Hashtable /*nullOK*/ myUnidentifiedConnections;

    /**
     * The connections which haven't completed the start up protocol. The key
     * is the vatID. The value is the VatTPConnection object.
     */
    private Hashtable /*nullOK*/ myIdentifiedConnections;

    /**
     * The running DataConnections. The key is vatID, the value is the
     * VatTPConnection object.
     */
    private final Hashtable myRunningDataConnections = new Hashtable(5);

    /**
     * The connections in the process of suspending. The key is vatID, the
     * value is the VatTPConnection object.
     */
    private Hashtable /*nullOK*/ mySuspendingConnections;

    /**
     * The connections which are suspended. The key is vatID, the value is the
     * VatTPConnection object.
     */
    private Hashtable /*nullOK*/ mySuspendedConnections;

    /**
     * Connections which have been told to shut down (until they have gone).
     * The key is the VatTPConnection object and the value is the vatID.
     */
    private Hashtable /*nullOK*/ myDieingConnections;

    static final int LIVES_CONTINUE = 1;

    static final int LIVES_DUP = 2;

    static final int LIVES_NOTIFY = 3;

    /**
     * Make a VatTPMgr listening on the specified ports.
     * <p/>
     * Each VatTPMgr made will have a different ID. The VatTPMgr is associated
     * with one vat.
     * <p/>
     * This constructor gets the Vat for that vat via the static method {@link
     * Vat#getCurrentVat()}. The rest of the VatTP system gets access to that
     * Vat by having the reference explicitly passed during construction.
     * Therefore, if it is necessary to have a VatTP system work with a
     * different Vat, the constructor which accepts the Vat as a specific
     * parameter should be used.
     *
     * @param identityKeys is the KeyPair which defines the identity of this
     *                     vat.
     * @param netConfig    holds the configuration parameters for configuring
     *                     how we interact with the network.
     */
    public VatTPMgr(KeyPair identityKeys, NetConfig netConfig)
      throws UnknownHostException, IOException {
        this(identityKeys, netConfig, Vat.getCurrentVat());
    }

    /**
     * Make a VatTPMgr.
     * <p/>
     * Each VatTPMgr made will have a different ID. The VatTPMgr is associated
     * with one vat.
     *
     * @param identityKeys is the KeyPair which defines the identity of this
     *                     vat.
     * @param netConfig    holds the configuration parameters for configuring
     *                     how we interact with the network.
     * @param vat          is the Vat to use to synchronize the connections.
     */
    private VatTPMgr(KeyPair identityKeys, NetConfig netConfig, Vat vat)
      throws UnknownHostException, IOException {
        myIdentityKeys = identityKeys;
        myVLS = null;
        myLocalVatID = VatIdentity.calculateVatID(identityKeys.getPublic());

        if (Trace.comm.debug && Trace.ON) {
            Trace.comm.debugm("netConfig is " + E.toString(netConfig));
        }
        myVat = vat;

        ConstList listenPath = netConfig.getListenAddrPath();
        if (0 == listenPath.size()) {
            listenPath = listenPath.with(null);
        }
        int len = listenPath.size();
        myListenThreads = new ListenThread[len];
        FlexList newListenPath = FlexList.fromType(String.class, len);
        FlexList newSearchPath = FlexList.fromType(String.class, len);
        for (int i = 0; i < len; i++) {
            String optLocalAddr = (String)listenPath.get(i);
            myListenThreads[i] = new ListenThread(optLocalAddr, this, myVat);
            NetAddr netAddr = myListenThreads[i].listenAddress();
            newListenPath.push(netAddr.toString());
            newSearchPath.append(expandPath(netAddr));
        }
        newSearchPath.append(netConfig.getSearchPath());
        myNetConfig = new NetConfig(netConfig.getVLSPath(),
                                    newSearchPath.snapshot(),
                                    newListenPath.snapshot());
        if (Trace.comm.event && Trace.ON) {
            Trace.comm.eventm("VatTPMgr constructor done " + this);
        }
    }

    /**
     * Return the subset of a searchPath for directly connecting to netAddr.
     * <p/>
     * This does various kinds of DNS lookup to convert netAddr to an
     * externally addressable form
     */
    static private ConstList expandPath(NetAddr netAddr)
      throws UnknownHostException {
        InetAddress[] ips = null;
        InetAddress optIP = netAddr.optInetAddress();
        if (optIP == null) {
            InetAddress localhost = null;
            try {
                localhost = InetAddress.getLocalHost();
            } catch (UnknownHostException uhe) {
                //the loopback host: 127.0.0.1
                localhost = InetAddress.getByName(null);
            }
            ips = InetAddress.getAllByName(localhost.getHostName());
        } else {
            InetAddress[] single = {optIP};
            ips = single;
        }
        String suffix = ":" + netAddr.getPort();
        String[] parts = new String[ips.length * 2];
        for (int i = 0; i < ips.length; i++) {
            parts[2 * i] = ips[i].getCanonicalHostName() + suffix;

            String hostPart;
            if (ips[i] instanceof Inet6Address) {
                hostPart = "[" + ips[i].getHostAddress() + "]";
            } else {
                hostPart = ips[i].getHostAddress();
            }
            parts[2 * i + 1] = hostPart + suffix;
        }
        return ConstList.fromArray(parts);
    }

    /**
     * Register that this connection is suspended
     *
     * @param conn  The data connection which is suspended.
     * @param vatID The vatID of the remote end.
     */
    void connectionSuspended(VatTPConnection conn, String vatID) {
        VatTPConnection sus = removeSuspending(vatID);
        if (null == sus) {
            Trace.comm
              .errorm(
                "Suspended connection not suspending " + conn + "\n  " + this,
                new Throwable("locator"));
        } else if (sus != conn) {
            Trace.comm
              .errorm("Suspending ID " + sus + " not one suspended " + conn);
        }
        if (null == mySuspendedConnections) {
            mySuspendedConnections = new Hashtable(1);
        }
        mySuspendedConnections.put(vatID, conn);
    }

    /**
     * Register that this connection is suspending
     *
     * @param conn  The data connection which is suspending.
     * @param vatID The vatID of the remote end.
     */
    void connectionSuspending(VatTPConnection conn, String vatID) {
        VatTPConnection run =
          (VatTPConnection)myRunningDataConnections.remove(vatID);
        if (null == run) {
            Trace.comm
              .errorm(
                "Suspending connection not running " + conn + "\n  " + this,
                new Throwable("locator"));
        } else if (run != conn) {
            Trace.comm
              .errorm("Running ID " + run + " not one suspending " + conn);
        }
        if (null == mySuspendingConnections) {
            mySuspendingConnections = new Hashtable(1);
        }
        mySuspendingConnections.put(vatID, conn);
    }

    /**
     * Notify the connectToVatAt caller that he has tried to connect to self.
     *
     * @param path is the DataPath object trying the connection.
     */
    void connectToSelf(DataPath path) {
        // Remove from unidentified connections list
        Resolver res = (Resolver)removeUnidentified(path);
        if (null == res) {
            Trace.comm.errorm("DataPath not known" + path);
        } else {
            res.resolve(null);
        }
    }

    /**
     * Make an authenticated connection to a vat given an IP:port address.
     *
     * @param ipPort is the IP address and port to connect to separated by a
     *               colon. For example "groucho.communities.com:1670".
     * @return A promise for the resulting VatTPConnection
     */
    public Ref connectToVatAt(String ipPort) {
        Object[] promise = Ref.promise();
        Ref result = (Ref)promise[0];
        Resolver resolver = (Resolver)promise[1];
        connectToVatAt(ipPort, resolver);
        return result;
    }

    /**
     * Make an authenticated connection to a vat given an IP:port address.
     *
     * @param ipPort The IP address and port to connect to, separated by a
     *               colon. For example "groucho.communities.com:1670".
     * @param res    The object that will be notified when the connection is
     *               completed or if the attempt to connect fails.
     */
    public void connectToVatAt(String ipPort, Resolver res) {
        myVat.requireCurrent();
        DataPath path = new DataPath(this,
                                     null,
                                     VatIdentity.WHOEVER,
                                     ipPort,
                                     new Hashtable(1),
                                     myIdentityKeys,
                                     myLocalVatID,
                                     myVat,
                                     null,
                                     null,
                                     EARL.flattenSearchPath(searchPath()));
        // Register the DataPath as an unidentified path
        if (null == myUnidentifiedConnections) {
            myUnidentifiedConnections = new Hashtable(1);
        }
        myUnidentifiedConnections.put(path, res);
    }

    /**
     * Mark the death of a connection. Remove it from all tables so it can be
     * garbage collected.
     *
     * @param conn The data connection which has shut down.
     */
    void deathNotification(VatTPConnection conn) {
        Object te = removeDieing(conn);   //To find which table we
        //removed if from.
        if (null != te) {
            return;     // Tables cleaned up
        }

        String vID = conn.getRemoteVatID();
        te = myRunningDataConnections.remove(vID);
        if (null != te) {
            if (te != conn) {
                Trace.comm
                  .errorm("Two DataConnections for one vatID\nA=" + conn +
                    "\nB=" + te);
            }
            return;
        }
        te = removeIdentified(vID);
        if (null != te) {
            if (te != conn) {
                Trace.comm
                  .errorm("Two DataConnections for one vatID\nA=" + conn +
                    "\nB=" + te);
            }
            return;
        }
        te = removeSuspending(vID);
        if (null != te) {
            if (te != conn) {
                Trace.comm
                  .errorm("Two DataConnections for one vatID\nA=" + conn +
                    "\nB=" + te);
            }
            return;
        }
        te = removeSuspended(vID);
        if (null != te) {
            if (te != conn) {
                Trace.comm
                  .errorm("Two DataConnections for one vatID\nA=" + conn +
                    "\nB=" + te);
            }
            return;
        }
        Trace.comm
          .errorm("Unregistered VatTPConnection=" + conn + "\n" + toString(),
                  new Throwable("locator"));
    }

    /**
     * Register that this connection is dying
     *
     * @param conn  The data connection which is shutting down.
     * @param vatID The vatID of the remote end.
     */

    void enterHospice(VatTPConnection conn, String vatID) {
        if (null == myDieingConnections) {
            myDieingConnections = new Hashtable(1);
        }
        myDieingConnections.put(conn, vatID);
        myRunningDataConnections.remove(vatID);
        removeSuspending(vatID);
        removeSuspended(vatID);
    }

    /**
     * Find an existing VatTPConnection object that can or will be able to be
     * used for data transfer. (That is, one that is not dying or dead.)
     *
     * @param vatID is the vatID of the remote vat.
     * @return an existing VatTPConnection object or null if there are none.
     */
    private /*nullOK*/ VatTPConnection findDataConnection(String vatID) {
        VatTPConnection ret;
        ret = (VatTPConnection)myRunningDataConnections.get(vatID);
        if (null != ret) {
            return ret;
        }

        if (null != mySuspendingConnections) {
            ret = (VatTPConnection)mySuspendingConnections.get(vatID);
            if (null != ret) {
                return ret;
            }
        }

        if (null != mySuspendedConnections) {
            ret = (VatTPConnection)mySuspendedConnections.get(vatID);
            if (null != ret) {
                return ret;
            }
        }

        if (null != myIdentifiedConnections) {
            ret = (VatTPConnection)myIdentifiedConnections.get(vatID);
        }
        return ret;
    }

    /**
     * Return a VatTPConnection to the remote vat given its vatID and search
     * path.
     *
     * @param vatID      is the vatID for the remote vat wanted. The process of
     *                   setting up the physical connection will verify that
     *                   the vatID is indeed the hash of the public key of the
     *                   remote vat, and that the other end of the connection
     *                   holds the associated private key.
     * @param searchList is a list of Strings each of which is the IP address
     *                   and port number of a place to look for the remote vat.
     *                   The IP address can be a DNS name or a dot repsentation
     *                   of the 32 bit IP number. Most commonly, the Strings
     *                   will be the location of the Vat Location Servers
     *                   (VLSs) with which the remote vat is believed to
     *                   register. It may be useful under special circumstances
     *                   to include the actual address where the remote vat may
     *                   be listening in the list.
     * @return a VatTPConnection object to the remote vat or null. This is
     *         either an existing VatTPConnection object or a newly created
     *         one. If the VatTPConnection object is newly created, then it has
     *         been asked to initiate the connection startup protocol. This
     *         method returns null if the request is to connect the vat to
     *         itself.
     */
    public VatTPConnection getConnection(String vatID, ConstList searchList) {
        myVat.requireCurrent();
        if (Trace.comm.event && Trace.ON) {
            Trace.comm.eventm("getConnection to=" + vatID + " on " + this);
        }
        VatTPConnection dc = findDataConnection(vatID);
        if (null != dc) {
            return dc;
        }
        if (vatID.equals(myLocalVatID)) {
            return null;
        }
        dc = new VatTPConnection(this,
                                 vatID,
                                 EARL.flattenSearchPath(searchList),
                                 myIdentityKeys,
                                 myLocalVatID,
                                 myVat,
                                 EARL.flattenSearchPath(searchPath()));

        if (null == myIdentifiedConnections) {
            myIdentifiedConnections = new Hashtable(1);
        }
        myIdentifiedConnections.put(vatID, dc);
        return dc;
    }

//    /**
//     * Like getConnection((vatID, searchList), but if there isn't a working
//     * live one, will return null rather than creating one.
//     */
//    public VatTPConnection optConnection(String vatID) {
//        myVat.requireCurrent();
//        if (Trace.comm.event && Trace.ON) {
//            Trace.comm.eventm("optConnection to=" + vatID
//                              + " on " + this);
//        }
//        return findDataConnection(vatID);
//    }

    /**
     * List the current DataPaths/DataConnections in a enumeration.
     *
     * @param iter is the Enumeration to list. The elements produced should be
     *             either DataConnections or DataPaths.
     * @param buf  is a StringBuffer to fill with the list.
     */
    private void listState(Enumeration iter, StringBuffer buf) {
        while (iter.hasMoreElements()) {
            Object element = iter.nextElement();
            buf.append(element.toString()).append("\n");
        }
    }

    /**
     * Move a path from the unidentified list to the identified but not
     * connected path list. This also handles forwarding to the promise
     * returned by connectToVatAt for that type of connection.
     *
     * @param path        is the DataPath object which has been identified.
     * @param remoteVatID is the identified remote vat's vatID.
     * @param isIncoming  says whether this is an incoming connection (true) or
     *                    an outgoing connection (false).
     * @return LIVES_CONTINUE - Continue setting up this connection <br>
     *         LIVES_DUP - This connection is a duplicate, discard it. <br>
     *         LIVES_NOTIFY - Notify the other end of a duplicate connection.
     *         The other end must decide which connection to keep.
     */
    private int moveIdentifiedPath(DataPath path,
                                   String remoteVatID,
                                   boolean isIncoming) throws IOException {
        int ret;

        // Remove from unidentified connections list
        Object uidPath = removeUnidentified(path);
        if (null == uidPath) {
            Trace.comm.errorm("DataPath not known" + path);
        }

        VatTPConnection dc = findDataConnection(remoteVatID);
        if (null == dc) {
            dc = new VatTPConnection(this,
                                     myIdentityKeys,
                                     myLocalVatID,
                                     myVat,
                                     remoteVatID,
                                     path,
                                     EARL.flattenSearchPath(searchPath()),
                                     isIncoming);
            Trace.comm
              .debugm("New VatTPConnection " + dc + " for incoming=" +
                isIncoming + " " + path);
            // Insert the VatTPConnection in pending connections
            if (null == myIdentifiedConnections) {
                myIdentifiedConnections = new Hashtable(1);
            }
            myIdentifiedConnections.put(remoteVatID, dc);
            ret = LIVES_CONTINUE;
        } else {
            ret = dc.connectPath(path, remoteVatID, isIncoming);
        }


        if (uidPath instanceof Resolver) {
            Resolver res = (Resolver)uidPath;
            res.resolve(dc);
        }
        return ret;
    }

    /**
     * Register a DataPath for an connection where the remote vat has been
     * identified
     *
     * @param path        the new DataPath
     * @param remoteVatID is the vat ID of the remote vat.
     * @param localVatID  is the vat ID of this vat.
     * @param isIncoming  is true if this is an inbound connection, false if it
     *                    is an outbound connection.
     * @return LIVES_CONTINUE - Continue setting up this connection <br>
     *         LIVES_DUP - This connection is a duplicate, discard it. <br>
     *         LIVES_NOTIFY - Notify the other end of a duplicate connection.
     *         The other end must decide which connection to keep.
     */
    int newConnectionIdentified(DataPath path,
                                String remoteVatID,
                                String localVatID,
                                boolean isIncoming) throws IOException {
        if (Trace.comm.debug && Trace.ON) {
            Trace.comm.debugm("(" + path + ") on " + this);
        }

        return moveIdentifiedPath(path, remoteVatID, isIncoming);
    }

    /**
     * Receive a new inbound socket from the ListenThread.
     *
     * @param socket is the new inbound socket object, just as received by the
     *               listen operation.
     */
    void newInboundSocket(Socket socket) {
        DataPath path = new DataPath(this,
                                     socket,
                                     myIdentityKeys,
                                     myLocalVatID,
                                     myVat,
                                     EARL.flattenSearchPath(searchPath()),
                                     myVLS);
        if (null == myUnidentifiedConnections) {
            myUnidentifiedConnections = new Hashtable(1);
        }
        myUnidentifiedConnections.put(path, path);
    }

    /**
     * Learn of a problem with the ListenThread.
     *
     * @param error the Throwable which represents the error.
     */
    void noticeProblem(Throwable error) {
        //I (wsf) don't think there is anything to do here
        Trace.comm.errorm("Fatal error reported", error);
        Trace.comm.notifyFatal();
    }

    /**
     * Register an object to notice a new connection which has successfully
     * completed the startup protocol. This object is expected to connect up
     * MsgHandlers for all the messages processed by higher layers in the
     * protocol. Note that a maximum of one NewConnectionReactor may be
     * registered. <p>
     * <p/>
     * This notification is for all DataConnections, whether the connection was
     * initiated from this end or from the other end.
     *
     * @param reactor is an object which implements NewConnectionReactor.
     * @throws IOException is thrown if there is already a reactor registered.
     */
    public void addNewConnectionReactor(NewConnectionReactor reactor)
      throws IOException {
        if (Trace.comm.event && Trace.ON) {
            Trace.comm.eventm("addNewConnectionReactor " + reactor);
        }
        if (null != myReactor) {
            throw new IOException("Must only addNewConnectionReactor() once");
        }
        myReactor = reactor;
    }

    /**
     * Remove an existing VatTPConnection object that can or will be able to be
     * used for data transfer (That is, one that is not dying or dead) from the
     * hash table which holds it and return it.
     *
     * @param vatID is the vatID of the remote vat.
     * @return an existing VatTPConnection object or null if there are none.
     */
    private /*nullOK*/ VatTPConnection removeDataConnection(String vatID) {
        VatTPConnection ret;
        ret = (VatTPConnection)myRunningDataConnections.remove(vatID);
        if (null != ret) {
            return ret;
        }

        ret = removeSuspending(vatID);
        if (null != ret) {
            return ret;
        }

        ret = removeSuspended(vatID);
        if (null != ret) {
            return ret;
        }

        ret = removeIdentified(vatID);
        return ret;
    }

    /**
     * Remove an entry from myDieingConnections
     *
     * @param regID is the VatID to remove.
     */
    private String /*nullOK*/ removeDieing(VatTPConnection dc) {
        if (null == myDieingConnections) {
            return null;
        }
        String ret = (String)myDieingConnections.remove(dc);
        if (0 == myDieingConnections.size()) {
            myDieingConnections = null;
        }
        return ret;
    }

    /**
     * Remove an entry from myIdentifiedConnections
     *
     * @param regID is the VatID to remove.
     */
    private VatTPConnection /*nullOK*/ removeIdentified(String regID) {
        if (null == myIdentifiedConnections) {
            return null;
        }
        VatTPConnection ret =
          (VatTPConnection)myIdentifiedConnections.remove(regID);
        if (0 == myIdentifiedConnections.size()) {
            myIdentifiedConnections = null;
        }
        return ret;
    }

    /**
     * Remove an entry from mySuspendedConnections
     *
     * @param regID is the VatID to remove.
     */
    private VatTPConnection /*nullOK*/ removeSuspended(String regID) {
        if (null == mySuspendedConnections) {
            return null;
        }
        VatTPConnection ret =
          (VatTPConnection)mySuspendedConnections.remove(regID);
        if (0 == mySuspendedConnections.size()) {
            mySuspendedConnections = null;
        }
        return ret;
    }

    /**
     * Remove an entry from mySuspendingConnections
     *
     * @param regID is the VatID to remove.
     */
    private VatTPConnection /*nullOK*/ removeSuspending(String regID) {
        if (null == mySuspendingConnections) {
            return null;
        }
        VatTPConnection ret =
          (VatTPConnection)mySuspendingConnections.remove(regID);
        if (0 == mySuspendingConnections.size()) {
            mySuspendingConnections = null;
        }
        return ret;
    }

    /**
     * Remove an entry from myUnidentifiedConnections
     *
     * @param regID is the VatID to remove.
     */
    private Object /*nullOK*/ removeUnidentified(DataPath path) {
        if (null == myUnidentifiedConnections) {
            return null;
        }
        Object ret = myUnidentifiedConnections.remove(path);
        if (0 == myUnidentifiedConnections.size()) {
            myUnidentifiedConnections = null;
        }
        return ret;
    }

    /**
     * Returns the VatTP configuration parameters as they exist after
     * allocating a listen port.
     */
    public NetConfig getNetConfig() {
        return myNetConfig;
    }

    /**
     * Return the search path to our own vat.
     */
    public ConstList searchPath() {
        return myNetConfig.getSearchPath();
    }

    /**
     * Set the object which will respond to vat location queries on our listen
     * port.
     *
     * @param vls The VLS object
     */
    public void setVatLocationLookup(VatLocationLookup vls) {
        if (myVLS != null) {
            T.fail("duplicate setVatLocationLookup");
        }
        myVLS = vls;
    }

    /**
     * Inform the world that a new conection is ready to pass data. This method
     * is called by VatTPConnection after the start up protocol has
     * successfully completed.
     *
     * @param connection  The VatTPConnection which is now in RUNNING state.
     * @param remoteVatID is the vatID of the remote vat.
     * @param isResuming  is true if this connection is resuming, false if it
     *                    is starting.
     */

    void startupSuccessful(VatTPConnection connection,
                           String remoteVatID,
                           boolean isResuming) {
        Object dc = null;
        if (isResuming) {
            dc = removeSuspended(remoteVatID);
        } else {
            dc = removeIdentified(remoteVatID);
        }
        if (null == dc) {
            Trace.comm
              .errorm(connection + " Not registered as pending\n" + this);
        }

        VatTPConnection oldconn =
          (VatTPConnection)myRunningDataConnections.put(remoteVatID,
                                                        connection);
        if (null != oldconn) {
            Trace.comm.errorm("Overlaying " + oldconn + " with " + connection);
            oldconn.shutDownConnection(new ConnectionShutDownException(
              "Replacing old connection with newly started one"));
        }
        if (null != myReactor && !isResuming) {
            if (Trace.comm.event && Trace.ON) {
                Trace.comm
                  .eventm("call NewConnectionReactor " + myReactor + " with " +
                    connection);
            }
            myReactor.reactToNewConnection(connection);
        }
    }

    /**
     * Convert the state of the world to a string and print it.
     *
     * @return is a String which descirbes this VatTPMgr and the state of all
     *         the connections it is managing.
     */
    public String toString() {
        StringBuffer buf = new StringBuffer(500);
        if (!myVat.isCurrent()) {
            buf.append("\n***Warning: Caller does not hold vat lock***");
        }
        buf.append(super.toString()).append("\nlistening at ");
        for (int i = 0, len = myListenThreads.length; i < len; i++) {
            if (1 <= i) {
                buf.append(";");
            }
            buf.append(myListenThreads[i].listenAddress().toString());
        }
        buf.append(" myVatID=").append(myLocalVatID);

        if (null != myUnidentifiedConnections) {
            buf.append("\n" + myUnidentifiedConnections.size() +
              " Unidentified Connections\n");
            listState(myUnidentifiedConnections.keys(), buf);
        }
        if (null != myIdentifiedConnections) {
            buf.append("\n" + myIdentifiedConnections.size() +
              " Identified Starting Connections\n");
            listState(myIdentifiedConnections.elements(), buf);
        }
        if (0 != myRunningDataConnections.size()) {
            buf.append("\n" + myRunningDataConnections.size() +
              " Running Connections\n");
            listState(myRunningDataConnections.elements(), buf);
        }
        if (null != mySuspendingConnections) {
            buf.append("\n" + mySuspendingConnections.size() +
              " Suspending Connections\n");
            listState(mySuspendingConnections.elements(), buf);
        }
        if (null != mySuspendedConnections) {
            buf.append("\n" + mySuspendedConnections.size() +
              " Suspended Connections\n");
            listState(mySuspendedConnections.elements(), buf);
        }
        if (null != myDieingConnections) {
            buf.append(
              "\n" + myDieingConnections.size() + " Dieing Connections\n");
            listState(myDieingConnections.keys(), buf);
        }
        return buf.toString();
    }

    /**
     * Note that a DataPath for an connection has died before the vat at the
     * other end was identified.
     *
     * @param path is the DataPath object whose connection has died.
     */

    void unidentifiedConnectionDied(DataPath path,
                                    String msg,
                                    Throwable problem) {
        String remoteAddr = path.getRemoteAddress();
        Object dp = removeUnidentified(path);
        if (null == dp) {
            Trace.comm
              .errorm("Dieing DataPath=" + path + " not registered",
                      new Throwable("locator"));
            dp = this;      // Something which isn't a resolver
        }
        if (null != problem) {
            if (problem instanceof IOException) {
                if (Trace.comm.event && Trace.ON) {
                    Trace.comm
                      .eventm(problem + " on unidentified connection to " +
                        remoteAddr);
                }
            } else {
                Trace.comm
                  .errorm("Exception on unidentified connection to " +
                    remoteAddr + "\n  " + problem);
            }
        } else if (null != msg) {
            if (Trace.comm.event && Trace.ON) {
                Trace.comm
                  .eventm("Unidentified connection to " + remoteAddr +
                    " died, " + msg);
            }
            problem = new Throwable(msg);
        } else {
            problem = new Throwable("Undetermined reason");
        }
        if (dp instanceof Resolver) {
            ((Resolver)dp).smash(problem);
        }
    }
}
