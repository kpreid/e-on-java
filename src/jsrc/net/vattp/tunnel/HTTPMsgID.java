package net.vattp.tunnel;

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
 * Definitions of the HTTP message ID codes.
 *
 * @author Bill Frantz
 */

public class HTTPMsgID {

    static public final byte HTTP_Logon = 0x01;

    static public final byte HTTP_Session = 0x02;

    static public final byte HTTP_Shutdown = 0x03;

    static public final byte HTTP_Error = 0x04;

    static public final byte HTTP_LoggedOn = 0x05;

    static public final byte HTTP_SetServerNonce = 0x06;

    static public final byte HTTP_NewConnection = 0x10;

    static public final byte HTTP_Data = 0x11;

    static public final byte HTTP_OKToSend = 0x12;

    static public final byte HTTP_Close = 0x13;

    static public final byte HTTP_InvalidID = 0x14;

    static public final byte HTTP_ConnectionFailed = 0x15;

    static public final byte HTTP_ConnectionComplete = 0x16;

    private HTTPMsgID() {
    }  // Don't build one
}
