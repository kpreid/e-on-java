// Copyright 2004-2005 Waterken Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html
package org.waterken.url.amp;

import java.io.IOException;

/**
 * Signals an HTTP level error.
 *
 * @author Tyler
 */
public final class HTTPException extends IOException {

    static private final long serialVersionUID = 7521688257434685450L;

    /**
     * The HTTP status code.
     */
    private final int status;

    /**
     * Constructs an <code>HTTPException</code>.
     *
     * @param status The HTTP status code.
     * @param detail The HTTP response message.
     */
    public HTTPException(final int status, final String detail) {
        super(detail);
        this.status = status;
    }

    // org.waterken.url.amp.HTTPException interface.

    /**
     * Gets the HTTP status code.
     */
    public int getStatus() {
        return status;
    }
}
