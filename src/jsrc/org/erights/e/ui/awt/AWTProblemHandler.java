package org.erights.e.ui.awt;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.develop.trace.Trace;

/**
 * As mandated by JDK1.3's {@link
 * java.awt.EventDispatchThread#handleException(Throwable)}
 *
 * @author Mark S. Miller
 */
class AWTProblemHandler {

    /**
     * As mandated by JDK1.3's {@link
     * java.awt.EventDispatchThread#handleException(Throwable)}
     */
    public AWTProblemHandler() {
    }

    /**
     * As mandated by JDK1.3's {@link
     * java.awt.EventDispatchThread#handleException(Throwable)}
     */
    public void handle(Throwable problem) {
        if (Trace.causality.warning && Trace.ON) {
            Trace.causality.warningm("While dispatching awt event",
                                     problem);
        }
    }
}
