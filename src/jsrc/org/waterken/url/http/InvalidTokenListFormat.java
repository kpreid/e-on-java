// Copyright 2002-2004 Waterken Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html
package org.waterken.url.http;

/**
 * Signals an invalid <code>token</code> list format.
 *
 * @author Tyler
 */
public class InvalidTokenListFormat extends RuntimeException {

    static final long serialVersionUID = 3859309500294447096L;

    /**
     * Constructs an <code>InvalidTokenListFormat</code>.
     *
     * @param detail The detail message.
     */
    public InvalidTokenListFormat(final String detail) {
        super(detail);
    }

    /**
     * Constructs an <code>InvalidTokenListFormat</code>.
     */
    public InvalidTokenListFormat() {
    }
}
