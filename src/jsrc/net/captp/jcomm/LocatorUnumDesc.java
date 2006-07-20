package net.captp.jcomm;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

/**
 * @author Mark S. Miller
 */
class LocatorUnumDesc implements ObjectRefDesc {

    static final long serialVersionUID = -8748706103551187843L;

    /**
     *
     */
    static final LocatorUnumDesc THE_ONE = new LocatorUnumDesc();

    /**
     *
     */
    private LocatorUnumDesc() {
    }

    /**
     * Dereferences to the local presence of the LocatorUnum service
     */
    public Object dereference(CapTPConnection conn) {
        return conn.getLocatorUnum();
    }
}
