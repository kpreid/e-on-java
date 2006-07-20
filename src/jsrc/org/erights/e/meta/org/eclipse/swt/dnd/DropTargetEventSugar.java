package org.erights.e.meta.org.eclipse.swt.dnd;

import org.eclipse.swt.dnd.DropTargetEvent;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

/**
 * @author Mark S. Miller
 */
public class DropTargetEventSugar {

    /**
     * Works around the fact that we've suppressed 'data' in DropTargetEvent's
     * superclass, TypedEvent.
     */
    static public Object getDropData(DropTargetEvent self) {
        return self.data;
    }
}
