// Copyright 2004 Hewlett Packard, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package test.joee;

import org.erights.e.elib.ref.Ref;
import org.erights.e.elib.ref.Resolver;
import org.erights.e.elib.util.OneArgFunc;

/**
 * @author Mark S. Miller
 */
public class AsyncAnd {

    private AsyncAnd() {
    }

    static Ref run(Ref[] boolVows) {
        Object[] pair = Ref.promise();
        final Resolver resolver = (Resolver)pair[1];
        final int[] countDownCell = {boolVows.length};
        for (int i = 0, len = boolVows.length; i < len; i++) {
            Ref boolVow = boolVows[i];
            Ref.whenResolvedOnly(boolVow, new OneArgFunc() {
                public Object run(Object bool) {
                    try {
                        bool = Ref.fulfillment(bool);
                        if (((Boolean)bool).booleanValue()) {
                            if (0 >= --countDownCell[0]) {
                                resolver.resolve(Boolean.TRUE);
                            } else {
                                resolver.gettingCloser();
                            }
                        } else {
                            resolver.resolve(Boolean.FALSE);
                        }
                    } catch (Throwable problem) {
                        resolver.smash(problem);
                    }
                    return null;
                }
            });
        }
        return (Ref)pair[0];
    }
}
