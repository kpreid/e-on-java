// Copyright 2006 Hewlett Packard, under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.elang.tests;

/**
 * @author Mark S. Miller
 */
class Toggle {

    boolean state = true;

    Toggle(boolean start_state) {
        state = start_state;
    }

    public boolean value() {
        return (state);
    }

    public Toggle activate() {
        state = !state;
        return (this);
    }
}
