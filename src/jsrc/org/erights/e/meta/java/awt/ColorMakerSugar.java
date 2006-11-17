// Copyright 2004 Hewlett Packard, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.meta.java.awt;

import java.awt.Color;

/**
 * @author Mark S. Miller
 */
public final class ColorMakerSugar {

    /**
     * prevents instantiation
     */
    private ColorMakerSugar() {
    }

    /**
     * Like {@link Color#RGBtoHSB}(r,g,b,null).
     * <p/>
     *
     * @see <a href= "https://bugs.sieve.net/bugs/?func=detailbug&bug_id=125618&group_id=16380"
     *      >Taming bug: java.awt.Color.getComponents and related</a>
     */
    static public float[] RGBtoHSB(int r, int g, int b) {
        return Color.RGBtoHSB(r, g, b, null);
    }
}
