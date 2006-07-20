// Copyright 2004 Mark S. Miller, under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package test;

import org.erights.e.elib.util.OneArgFunc;

/**
 * @author Mark S. Miller
 */
public class AdderCreator implements OneArgFunc {

    public Object run(Object x) {
        final int xval = ((Number)x).intValue();
        return new OneArgFunc() {
            public Object run(Object y) {
                int yval = ((Number)y).intValue();
                return new Integer(xval + yval);
            }
        };
    }
}
