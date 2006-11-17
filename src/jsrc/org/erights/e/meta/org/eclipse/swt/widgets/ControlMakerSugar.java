package org.erights.e.meta.org.eclipse.swt.widgets;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.erights.e.develop.assertion.T;

/**
 * @author Mark S. Miller
 */
public class ControlMakerSugar {

    /**
     * prevent instantiation
     */
    private ControlMakerSugar() {
    }

    /**
     * Do any of these have or contain the focus?
     */
    static private boolean anyContainsFocus(Control[] sources) {
        for (int i = 0, len = sources.length; i < len; i++) {
            if (sources[i].isFocusControl()) {
                return true;
            }
            if (sources[i] instanceof Composite) {
                Composite comp = (Composite)sources[i];
                if (anyContainsFocus(comp.getChildren())) {
                    return true;
                }
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
     * @return Whether the operation was authorized by 'sources', and whether
     *         the 'setFocus()' succeeded.
     */
    static public boolean transferFocus(Control[] sources, Control dest) {
        if (!anyContainsFocus(sources)) {
            return false;
        }
        return dest.setFocus();
    }

    /**
     * Are any of the sources the active shell?
     * <p/>
     * None of the sources may be null.
     */
    static private boolean anyIsActive(Shell[] sources) {
        Shell optActive = Display.getCurrent().getActiveShell();
        if (null == optActive) {
            return false;
        }
        for (int i = 0, len = sources.length; i < len; i++) {
            Shell source = sources[i];
            T.requireSI(null != source, "No source may be null: ", i);
            if (optActive == source) {
                return true;
            }
        }
        return false;
    }

    /**
     * If any of the sources were the active shell, then make dest the active
     * shell.
     * <p/>
     * Neither dest nor any of the sources may be null.
     */
    static public boolean transferActive(Shell[] sources, Shell dest) {
        T.notNull(dest, "dest must not be null");
        if (!anyIsActive(sources)) {
            return false;
        }
        dest.setActive();
        return true;
    }
}
