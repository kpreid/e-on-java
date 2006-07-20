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

/**
 * Define the allowable message types for the VatTP system. This class only
 * contains static final values. It can not be instantiated.
 * <p/>
 * <p>This class only defines static final values.
 *
 * @author Bill Frantz
 */
public class Msg {

    /**
     * The protocol versions this implementations supports.
     */
    static final String[] Version = {"E0", "E0"};


// Valid initial bytes in calls to VatTPConnection.sendMsg(...)

    // Value zero is not defined as an error check to make sure callers
    // actually assign something to the first byte of the message.

    /**
     * Supported versions of the E protocol.
     */
    static final byte PROTOCOL_VERSION = 1;

    /**
     * Initial connection setup message
     */
    static final byte STARTUP = 2;

    /**
     * STARTUP response showing which protocol version to use
     */
    static final byte PROTOCOL_ACCEPTED = 3;

    /**
     * Suspend or shutdown this connection. A shutdown message has only the
     * message type. A suspend messages also has the suspendID as an encoded
     * string parameter.
     */
    static final byte SUSPEND = 4;

    /**
     * Ping other end to ensure connection still intact
     */
    static final byte PING = 5;

    /**
     * Response to a PING message
     */
    static final byte PONG = 6;

    /**
     * E level message to an object at the remote end
     */
    static public final byte E_MSG = 7;

    /**
     * RMI Comm message to a stream at the other end
     */
    static public final byte TUNNEL_MSG = 8;

    // We want to fill in here with other message codes to keep the allocation
    // compact.


    /**
     * Highest message type allocated (used to allocate arrays)
     */
    static final byte HIGH_MSG_TYPE = 8;


    /**
     * Maximum length message permitted on a connection. This limit is enforced
     * by the VatTPConnection object.
     */
    static public final int MAX_OUTBOUND_MSG_LENGTH = 1024 * 1024;

    /**
     * Maximum length message we will receive. This length is set longer than
     * MAX_OUTBOUND_MSG_LENGTH to allow for MAC overhead etc. This limit is
     * enforced by the RecvThread.
     */
    static final int MAX_INBOUND_MSG_LENGTH = MAX_OUTBOUND_MSG_LENGTH + 256;


    /**
     * A fudge factor with which to multiply the timeouts for purposes of
     * debugging.
     */
    //static private final long TIME_FUDGE = 1;  //the normal value
    static private final long TIME_FUDGE = 60;  //debug value

    /**
     * The tick rate for the connection keep alive clock. Used in conjunction
     * with PING_SENDTIME and PING_TIMEOUT to ensure that the connection is
     * still in good working order. In milliseconds.
     */
    static final long TICK_RATE = 15 * 1000 * TIME_FUDGE;

    /**
     * The timeout for no messages received. When this timeout is exceeded, we
     * send a ping message. In milliseconds.
     */
    static final long PING_SENDTIME = 20 * 1000 * TIME_FUDGE;

    /**
     * The timeout for a ping. If we don't receive a message within this time
     * after we have sent a ping, we declare the connection down. In
     * milliseconds.
     */
    static final long PING_TIMEOUT = 20 * 1000 * TIME_FUDGE;

    private Msg() {
    }    // This class is only static final values.
}
