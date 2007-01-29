package org.erights.e.meta.org.eclipse.swt.dnd;

import org.eclipse.swt.dnd.DragSourceEvent;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

/**
 * @author Mark S. Miller
 */
public class DragSourceEventSugar {

    private DragSourceEventSugar() {
    }

    /**
     * Works around the fact that we've suppressed 'data' in DragSourceEvent's
     * superclass, TypedEvent
     */
    static public void setDragData(DragSourceEvent self, Object newData) {
        self.data = newData;
    }
}
