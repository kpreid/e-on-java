// Copyright 2006 Hewlett Packard, under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.elang.tests;

/**
 * @author Mark S. Miller
 */
class NthToggle extends Toggle {

    int count_max = 0;
    int counter = 0;

    NthToggle(boolean start_state, int max_counter) {
        super(start_state);
        count_max = max_counter;
        counter = 0;
    }

    public Toggle activate() {
        counter += 1;
        if (counter >= count_max) {
            state = !state;
            counter = 0;
        }
        return (this);
    }
}
