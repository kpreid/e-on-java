// Copyright 2002-2005 Waterken Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html
package org.waterken.url.io;

import java.io.IOException;

/**
 * Signals that too much data has been requested from a stream.
 *
 * @author Tyler
 */
public class TooMuchData extends IOException {

    static private final long serialVersionUID = 8449729970758218002L;

    /**
     * Constructs an <code>TooMuchData</code>.
     */
    public TooMuchData() {
    }
}
