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

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * A simple class which represents an extended IP address -- an IP address
 * together with a port number.
 *
 * @author Bill Frantz (Based on work by Eric Messick and Chip Morningstar)
 */
public class NetAddr {

    /**
     * null means a port associated with all local IP addresses
     */
    private final InetAddress myOptIP;

    private final int myPortNumber;

    /**
     * Construct a NetAddr from a string in the form: hostname:portnumber or
     * hostname. If the :portnumber is omitted, port number 0 will be assumed.
     * The hostname may be either a DNS name or an IP number in dotted decimal
     * format. It may be both of these separated by a slash, in which case only
     * the part after the slash is significant. If the significant part of the
     * hostname is absent, then the port is associated with all local IP
     * addresses.
     * <p/>
     * Examples: <pre>
     *   "the-earth.communities.com/205.162.51.187:4568"
     *   "the-earth.communities.com:4568"
     *   "205.162.51.187:4568"
     *   "the-earth.communities.com"
     *   ":4568"
     *   ""
     *   null
     * </pre>
     *
     * @param optAddr is the network address.
     * @throws UnknownHostException is thrown if the host name can not be
     *                              resolved.
     */
    public NetAddr(String optAddr) throws UnknownHostException {
        if (optAddr == null) {
            optAddr = "";
        }
        int colon = optAddr.indexOf(':');
        if (0 > colon) {
            myPortNumber = 0;
        } else {
            myPortNumber = Integer.parseInt(optAddr.substring(colon + 1));
            optAddr = optAddr.substring(0, colon);
        }
        int slash = optAddr.indexOf('/');
        if (0 <= slash) {
            optAddr = optAddr.substring(slash + 1);
        }
        if (1 <= optAddr.length()) {
            myOptIP = InetAddress.getByName(optAddr);
        } else {
            myOptIP = null;
        }
    }

    /**
     * Construct a new NetAddr given an IP address and a port number.
     *
     * @param optIP      An IP address, or null meaning all local IP addresses
     * @param portNumber A port at that IP address
     */
    public NetAddr(InetAddress optIP, int portNumber) {
        myOptIP = optIP;
        myPortNumber = portNumber;
    }

    /**
     * Test if another object is an NetAddr denoting the same address as this.
     *
     * @param other The other object to test for equality.
     * @return true iff this and other denote the same net address.
     */
    public boolean equals(Object other) {
        if (other == null || !(other instanceof NetAddr)) {
            return false;
        }
        NetAddr otherAddr = (NetAddr)other;
        if (myPortNumber != otherAddr.myPortNumber) {
            return false;
        }
        InetAddress otherOptIP = otherAddr.myOptIP;
        if (myOptIP == otherOptIP) {
            return true;
        }
        if (null == myOptIP || null == otherOptIP) {
            return false;
        }
        return myOptIP.equals(otherOptIP);
    }

    /**
     * @return A hash code that accounts for both the IP address and port.
     */
    public int hashCode() {
        if (null == myOptIP) {
            return myPortNumber;
        } else {
            return myOptIP.hashCode() ^ myPortNumber;
        }
    }

    /**
     * Return my IP address, or null indicating all local IP addresses.
     *
     * @return my IP address
     */
    public InetAddress optInetAddress() {
        return myOptIP;
    }

    /**
     * Return my port number.
     *
     * @return my port number
     */
    public int getPort() {
        return myPortNumber;
    }

    /**
     * Produce a printable representation of this.
     *
     * @return A nicely formatted string representing this address.
     */
    public String toString() {
        if (myOptIP == null) {
            return ":" + myPortNumber;
        } else {
            return myOptIP.getHostAddress() + ":" + myPortNumber;
        }
    }
}
