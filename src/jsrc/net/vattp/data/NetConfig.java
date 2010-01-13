package net.vattp.data;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.develop.assertion.T;
import org.erights.e.elib.base.ValueThunk;
import org.erights.e.elib.oldeio.EPrintable;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.StaticMaker;
import org.erights.e.elib.serial.DeepPassByCopy;
import org.erights.e.elib.serial.Persistent;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.ConstMap;

import java.io.IOException;

/**
 * Configuration parameters for initializing or re-initializing a VatTPMgr
 *
 * @author Mark S. Miller
 */
public class NetConfig implements Persistent, DeepPassByCopy, EPrintable {

    static private final long serialVersionUID = 7141557786514460621L;

    /**
     *
     */
    static public final StaticMaker NetConfigMaker =
      StaticMaker.make(NetConfig.class);

    /**
     * @serial Where I register so others can find me. <p>
     */
    private final ConstList myVLSPath;

    /**
     * @serial Where others should look for me. <p>
     */
    private final ConstList mySearchPath;

    /**
     * @serial If we're on the air, this must be the TCP/IP addresses of our
     * sockets. <p>
     */
    private final ConstList myListenAddrPath;

    /**
     * Make a NetConfig initialized from configuration propoerties (as might be
     * provided by eprops.txt or "-D" on the command line). 
     * <ul> 
     * <li>e.VLSPath is an optional semicolon-separated list of TCP/IP addresses
     *     of VLSs this vat should register with. Defaults to "" -- the null 
     *     list. Doesn't actually mean anything until the VLS code is revived. 
     * <li>e.SearchPath is an optional semicolon-separated list of TCP/IP 
     *     addresses for others to look for me. Defaults to the VLSPath. Once a
     *     vat knows its own ListenAddressPath, these should be added to the 
     *     front of the list.
     * <li>e.ListenAddressPath is the optional TCP/IP addresses at which this
     *     vat should create the sockets it listens to. For each element of the
     *     path, if an IP address isn't given, the socket defaults to listening
     *     on all IP addresses of this host. If an IP address is given, it must
     *     be one of the IP addresses of this host. If the TCP port isn't given,
     *     it defaults to 0. A TCP port of 0 instructs the OS to pick any free 
     *     TCP port. 
     * </ul>
     */
    static public NetConfig make(ConstMap optProps) {
        String flattenedVLSPath =
          (String)optProps.fetch("e.VLSPath", new ValueThunk(""));
        ConstList vlsPath = EARL.parseSearchPath(flattenedVLSPath);

        String flattenedSearchPath = (String)optProps.fetch("e.SearchPath",
                                                            new ValueThunk(
                                                              flattenedVLSPath));
        ConstList searchPath = EARL.parseSearchPath(flattenedSearchPath);

        String flattenedListenAddrPath =
          (String)optProps.fetch("e.ListenAddressPath", new ValueThunk(""));
        String optListenAddr =
          (String)optProps.fetch("e.ListenAddress", ValueThunk.NULL_THUNK);
        if (null != optListenAddr) {
            T.fail("'e.ListenAddress' is no longer supported. Use " +
              "'e.ListenAddressPath' instead");
        }
        ConstList listenAddrPath =
          EARL.parseSearchPath(flattenedListenAddrPath);

        return new NetConfig(vlsPath, searchPath, listenAddrPath);
    }

    /**
     *
     */
    public NetConfig() {
        this(ConstList.EmptyList, ConstList.EmptyList, ConstList.EmptyList);
    }

    /**
     *
     */
    public NetConfig(ConstList vlsPath,
                     ConstList searchPath,
                     ConstList listenAddrPath) {
        myVLSPath = vlsPath;
        mySearchPath = searchPath;
        myListenAddrPath = listenAddrPath;
    }

    /**
     * Uses 'NetConfigMaker(vlsPath, searchPath, listenAddrPath)'.
     */
    public Object[] getSpreadUncall() {
        Object[] result =
          {NetConfigMaker, "run", myVLSPath, mySearchPath, myListenAddrPath};
        return result;
    }

    /**
     * Where I register so others can find me.
     */
    public ConstList getVLSPath() {
        return myVLSPath;
    }

    /**
     * Where others should look for me. This path is used in network
     * designators I export (remote live refs, sturdy refs, URIs) that
     * designate objects I host. <p>
     * <p/>
     * Usually the same as myVLSPath + myself, but may have further difference
     * because of NAT (Network Address Translation -- a common bad firewall
     * technique). Configuration parameter read as a JavaBeans property.
     */
    public ConstList getSearchPath() {
        return mySearchPath;
    }

    /**
     * If we're on the air, this must be the TCP/IP addresses of our socket.
     * <p/>
     * (XXX in what format?)
     * <p/>
     * If we're not yet on the air and this isn't empty, it says what socket
     * addresses are preferred -- nothing more. Configuration parameter read as
     * a JavaBeans property.
     */
    public ConstList getListenAddrPath() {
        return myListenAddrPath;
    }

    /**
     *
     */
    public void __printOn(TextWriter out) throws IOException {
        out.print("<NetConfig:");
        TextWriter nest = out.indent();
        nest.lnPrint("       vlsPath: ");
        nest.print(myVLSPath);
        nest.lnPrint("    searchPath: ");
        nest.print(mySearchPath);
        nest.lnPrint("ListenAddrPath: ");
        nest.print(myListenAddrPath, ">");
    }
}
