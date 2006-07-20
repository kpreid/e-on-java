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

import java.net.ServerSocket;
import java.util.Hashtable;


/**
 * Class to listen for incoming TCP connections to be proxied to vats which are
 * using HTTP for communications.
 *
 * @author Bill Frantz
 */

public class TCPConnectionListener extends Thread {

    /**
     * map of logged on clients. Key is vatID, data is HTTPClient
     */
    private final Hashtable myVats;

    private final ServerSocket myListen;


    TCPConnectionListener(ServerSocket listen, Hashtable vats) {
        myListen = listen;
        myVats = vats;
        start();
    }

    public void run() {
        //xxx
    }
}
