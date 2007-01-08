// Copyright 2005 Waterken Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html
package net.vattp;

import java.io.IOException;

/**
 * Signals a crossed connection.
 *
 * @author Tyler
 */
public class CrossedConnection extends IOException {

    static private final long serialVersionUID = -7235768127586093517L;

    /**
     * Constructs a <code>CrossedConnection</code>.
     */
    public CrossedConnection() {
    }
}
