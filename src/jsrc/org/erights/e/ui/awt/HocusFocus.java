package org.erights.e.ui.awt;

import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import java.awt.Component;
import java.awt.Container;
import java.awt.Window;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

/**
 * Access to a StaticMaker on this class provides the authority to invoke the
 * suppressed 'requestFocus()' and 'toFront()' methods.
 *
 * @author Mark S. Miller
 */
public final class HocusFocus {

    /**
     * prevent instantiation
     */
    private HocusFocus() {
    }

    /**
     *
     */
    static public void requestFocus(Component comp) {
        comp.requestFocus();
    }

    /**
     *
     */
    static public void toFront(Window win) {
        win.toFront();
    }

    /**
     * This is needed because getTopLevelAncestor() is also suppressed.
     */
    static public void toFront(JComponent jComp) {
        Container top = jComp.getTopLevelAncestor();
        if (top instanceof Window) {
            ((Window)top).toFront();
        }
    }

    /**
     *
     */
    static public void toFrontInternalFrame(JInternalFrame frame) {
        frame.toFront();
    }
}
