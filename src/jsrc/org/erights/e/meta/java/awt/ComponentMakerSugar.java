package org.erights.e.meta.java.awt;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import javax.swing.SwingUtilities;
import java.awt.Component;

/**
 * @author Mark S. Miller
 */
public class ComponentMakerSugar {

    /**
     * prevent instantiation
     */
    private ComponentMakerSugar() {
    }

    /**
     * Do any of these have or contain the focus?
     */
    static private boolean anyContainsFocus(Component[] sources) {
        for (int i = 0, len = sources.length; i < len; i++) {
            Component optC = SwingUtilities.findFocusOwner(sources[i]);
            if (null != optC) {
                return true;
            }
        }
        return false;
    }

    /**
     * If any of 'sources' currently has or contains the focus, then requests
     * that 'dest' gets the focus.
     *
     * @param 'sources' Provided to show that the requestor has rights to the
     *                  focus, in order to authorize the transfer to 'dest'.
     * @return Whether the operation was authorized by 'sources', not whether
     *         the 'requestFocus()' succeeded.
     */
    static public boolean transferFocus(Component[] sources, Component dest) {
        if (!anyContainsFocus(sources)) {
            return false;
        }
        dest.requestFocus();
        return true;
    }
}
