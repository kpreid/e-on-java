package org.erights.e.meta.org.eclipse.swt.widgets;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.eclipse.swt.widgets.Shell;

/**
 * @author Mark S. Miller
 */
public class ShellMakerSugar {

    /**
     * prevent instantiation
     */
    private ShellMakerSugar() {
    }

    /**
     * Make dest be the active shell.
     */
    static public void activate(Shell dest) {
        dest.setActive();
    }
}
