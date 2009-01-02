// Copyright 2002-2004 Waterken Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html
package org.waterken.uri;

/**
 * Signals an invalid URI path.
 *
 * @author Tyler
 */
public class InvalidPath extends RuntimeException {

    static private final long serialVersionUID = 8459875934719645712L;

    protected InvalidPath() {
    }

    /**
     * The instance.
     */
    static private final InvalidPath INSTANCE = new InvalidPath();

    /**
     * Constructs an <code>InvalidPath</code>.
     */
    static public InvalidPath make() {
        return INSTANCE;
    }
}
