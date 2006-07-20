// Copyright 2004-2005 Waterken Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html
package org.waterken.url.http;

import java.io.IOException;
import java.net.Socket;

/**
 * Signals a protocol upgrade request.
 *
 * @author Tyler
 */
public class UpgradeProtocol extends IOException {

    /**
     * The connection socket.
     */
    private Socket socket;

    /**
     * The upgrade protocol identifiers.
     */
    private String[] protocol;

    /**
     * Constructs an <code>UpgradeProtocol</code>.
     *
     * @param socket   The connection socket.
     * @param protocol The upgrade protocol identifiers.
     */
    public UpgradeProtocol(final Socket socket, final String[] protocol) {
        this.socket = socket;
        this.protocol = protocol;
    }

    // UpgradeProtocol interface.

    /**
     * Gets the connection socket.
     */
    public Socket getSocket() {
        return socket;
    }

    /**
     * Gets the upgrade protocol identifiers.
     */
    public String[] getProtocol() {
        return protocol;
    }
}
