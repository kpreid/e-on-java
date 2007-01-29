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

import org.erights.e.develop.assertion.T;
import org.erights.e.develop.trace.Trace;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;

/**
 * Class to handle the suspension and shutdown messages.
 *
 * @author Bill Frantz
 */

public class HTTPServeMain {

    private HTTPServeMain() {
    }

    static public void main(String[] args) {
        int httpPort;
        int listenPort;

        /** map of logged on clients. Key is vatID, data is
         *  HTTPClient. Note that an entry in this map implies
         *  an entry in the sessions map and vicea versa. */
        Hashtable vats = new Hashtable(1);

        /** map of sessions to logged on clients. Key is sessionID,
         *  data is HTTPClient. Note that an entry in this map implies
         *  an entry in the vats map and vicea versa. */
        Hashtable sessions = new Hashtable(1);

        if (2 > args.length) {
            System.err.println("Requires httpPort listenPort");
            System.exit(1);
        }
        try {
            httpPort = Integer.parseInt(args[0]);
            listenPort = Integer.parseInt(args[1]);
            if (0 >= httpPort || 0xffff < httpPort) {
                T.fail("httpPort must be between 1 and 2**16-1");
            }
            if (0 >= listenPort || 0xffff < listenPort) {
                T.fail("listenPort must be between 1 and 2**16-1");
            }
        } catch (Exception e) {
            System.err
              .println("Exception " + e + "parsing httpPort and listenPort");
            System.exit(1);
            return;
        }

        ServerSocket http;
        try {
            http = new ServerSocket(httpPort);
        } catch (IOException e) {
            Trace.tunnel.errorm("Exception building http port", e);
            System.exit(1);
            return;
        }
        ServerSocket listen;
        try {
            listen = new ServerSocket(listenPort);
        } catch (IOException e) {
            Trace.tunnel.errorm("Exception building TCP listen port", e);
            System.exit(1);
            return;
        }

        TCPConnectionListener tcpListener =
          new TCPConnectionListener(listen, vats);
        String tcpListenAddress =
          listen.getInetAddress().getHostName() + ':' + listen.getLocalPort();

        try {
            while (true) {

                Socket httpSocket = http.accept();
                new HTTPConnectionHandler(httpSocket,
                                          vats,
                                          sessions,
                                          tcpListenAddress);
            }
        } catch (IOException e) {
            Trace.tunnel.errorm("Exception listening for HTTP connections", e);
            System.exit(1);
        }
    }
}
