// Copyright 2004 Hewlett Packard, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.meta.java.awt;

import org.erights.e.develop.assertion.T;

import java.awt.Color;
import java.awt.color.ColorSpace;

/**
 * @author Mark S. Miller
 */
public final class ColorSugar {

    /**
     * prevents instantiation
     */
    private ColorSugar() {
    }

    /**
     * Like the corresponding method of {@link Color} with an additional null
     * as the last float[] argument.
     * <p/>
     *
     * @see <a href=
     *      "https://bugs.sieve.net/bugs/?func=detailbug&bug_id=125618&group_id=16380"
     *      >Taming bug: java.awt.Color.getComponents and related</a>
     */
    static public float[] getRGBComponents(Color self) {
        return self.getRGBComponents(null);
    }

    /**
     * Like the corresponding method of {@link Color} with an additional null
     * as the last float[] argument.
     * <p/>
     *
     * @see <a href=
     *      "https://bugs.sieve.net/bugs/?func=detailbug&bug_id=125618&group_id=16380"
     *      >Taming bug: java.awt.Color.getComponents and related</a>
     */
    static public float[] getRGBColorComponents(Color self) {
        return self.getRGBColorComponents(null);
    }

    /**
     * Like the corresponding method of {@link Color} with an additional null
     * as the last float[] argument.
     * <p/>
     *
     * @see <a href=
     *      "https://bugs.sieve.net/bugs/?func=detailbug&bug_id=125618&group_id=16380"
     *      >Taming bug: java.awt.Color.getComponents and related</a>
     */
    static public float[] getComponents(Color self) {
        return self.getComponents(null);
    }

    /**
     * Like the corresponding method of {@link Color} with an additional null
     * as the last float[] argument.
     * <p/>
     *
     * @see <a href=
     *      "https://bugs.sieve.net/bugs/?func=detailbug&bug_id=125618&group_id=16380"
     *      >Taming bug: java.awt.Color.getComponents and related</a>
     */
    static public float[] getColorComponents(Color self) {
        return self.getColorComponents(null);
    }

    /**
     * Like the corresponding method of {@link Color} with an additional null
     * as the last float[] argument.
     * <p/>
     *
     * @see <a href=
     *      "https://bugs.sieve.net/bugs/?func=detailbug&bug_id=125618&group_id=16380"
     *      >Taming bug: java.awt.Color.getComponents and related</a>
     */
    static public float[] getComponents(Color self, ColorSpace cspace) {
        if (null == cspace) {
            T.fail("Use getComponents/0");
        }
        return self.getComponents(cspace, null);
    }

    /**
     * Like the corresponding method of {@link Color} with an additional null
     * as the last float[] argument.
     * <p/>
     *
     * @see <a href=
     *      "https://bugs.sieve.net/bugs/?func=detailbug&bug_id=125618&group_id=16380"
     *      >Taming bug: java.awt.Color.getComponents and related</a>
     */
    static public float[] getColorComponents(Color self, ColorSpace cspace) {
        if (null == cspace) {
            T.fail("Use getColorComponents/0");
        }
        return self.getColorComponents(cspace, null);
    }
}
