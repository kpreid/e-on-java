package org.erights.e.meta.org.eclipse.swt.widgets;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.erights.e.develop.assertion.T;

/**
 * @author Mark S. Miller
 */
public class CompositeSugar {

    /**
     * prevent instantiation
     */
    private CompositeSugar() {
    }

    /**
     * If 'self' is the parent of both 'above' and 'optBelow', then does an
     * 'above.{@link Control#moveAbove(Control) moveAbove}(optBelow)'
     */
    static public void moveAbove(Composite self,
                                 Control above,
                                 Control optBelow) {
        T.require(above.getParent() == self,
                  "'self' must be 'above's parent: ",
                  self,
                  ", ",
                  above);
        T.require(null == optBelow || optBelow.getParent() == self,
                  "'self' must be 'optBelow's parent: ",
                  self,
                  ", ",
                  optBelow);
        above.moveAbove(optBelow);
    }

    /**
     * If 'self' is the parent of both 'below' and 'optAbove', then does an
     * 'below.{@link Control#moveBelow(Control) moveBelow}(optAbove)'
     */
    static public void moveBelow(Composite self,
                                 Control below,
                                 Control optAbove) {
        T.require(below.getParent() == self,
                  "'self' must be 'below's parent: ",
                  self,
                  ", ",
                  below);
        T.require(null == optAbove || optAbove.getParent() == self,
                  "'self' must be 'optAbove's parent: ",
                  self,
                  ", ",
                  optAbove);
        below.moveBelow(optAbove);
    }
}
