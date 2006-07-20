package org.erights.e.ui.awt;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.elib.vat.PendingEvent;

import java.awt.AWTEvent;
import java.awt.ActiveEvent;
import java.awt.event.PaintEvent;


/**
 * For scheduling {@link org.erights.e.elib.vat.PendingEvent}s (vat turns) on the AWT Event Queue at
 * the low priority (PaintEvent.PAINT), which is "supposed" to be for
 * repaints.
 * <p/>
 * Unless we put everything at the low priority, repaints may be starved.
 *
 * @author Mark S. Miller
 * @author Terry Stanley
 */
class AWTRunnerEvent extends AWTEvent implements ActiveEvent {

    /**
     *
     */
    private final PendingEvent myTodo;

    /**
     *
     */
    AWTRunnerEvent(PendingEvent todo, AWTRunner runner) {
        super(SacrificialComponent.THE_ONE, PaintEvent.PAINT);
        myTodo = todo;
    }

    /**
     *
     */
    public void dispatch() {
        myTodo.run();
    }
}
