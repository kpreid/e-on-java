// Copyright 2004 Hewlett Packard, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.elib.slot;

/**
 * Implement this to be recognized as implementing the Miranda method {@link
 * org.erights.e.elib.prim.MirandaMethods#__conformTo __conformTo/1}.
 *
 * @author Mark S. Miller
 */
public interface Conformable {

    /**
     * @return
     */
    Object __conformTo(Guard guard);
}
