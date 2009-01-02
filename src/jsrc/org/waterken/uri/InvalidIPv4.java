// Copyright 2005 Waterken Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html
package org.waterken.uri;

/**
 * Signals an invalid IP address literal.
 *
 * @author Tyler
 */
public class InvalidIPv4 extends RuntimeException {

    static private final long serialVersionUID = -6866919856527173933L;

    protected InvalidIPv4() {
    }

    /**
     * The instance.
     */
    static private final InvalidIPv4 INSTANCE = new InvalidIPv4();

    /**
     * Constructs an <code>InvalidIPv4</code>.
     */
    static public InvalidIPv4 make() {
        return INSTANCE;
    }
}
