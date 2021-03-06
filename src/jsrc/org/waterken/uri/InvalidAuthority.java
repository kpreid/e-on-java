// Copyright 2004 Waterken Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html
package org.waterken.uri;

/**
 * Signals an invalid URI authority.
 *
 * @author Tyler
 */
public class InvalidAuthority extends RuntimeException {

    static private final long serialVersionUID = -3377352962235832164L;

    protected InvalidAuthority() {
    }

    /**
     * The instance.
     */
    static private final InvalidAuthority INSTANCE = new InvalidAuthority();

    /**
     * Constructs an <code>InvalidAuthority</code>.
     */
    static public InvalidAuthority make() {
        return INSTANCE;
    }
}
