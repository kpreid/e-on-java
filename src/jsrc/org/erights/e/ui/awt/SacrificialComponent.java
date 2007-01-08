package org.erights.e.ui.awt;

import java.awt.AWTEvent;
import java.awt.Component;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

/**
 * Exists just to be the do-nothing source of AWTRunnerEvents.
 *
 * @author Mark S. Miller
 * @author Terry Stanley
 */
class SacrificialComponent extends Component {

    static private final long serialVersionUID = 3823041179631544340L;

    /**
     *
     */
    static final SacrificialComponent THE_ONE = new SacrificialComponent();

    /**
     *
     */
    private SacrificialComponent() {
    }

    /**
     *
     */
    protected AWTEvent coalesceEvents(AWTEvent existingEvent,
                                      AWTEvent newEvent) {
        return null;
    }
}
