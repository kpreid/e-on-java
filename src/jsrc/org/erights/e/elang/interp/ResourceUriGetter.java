package org.erights.e.elang.interp;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.develop.assertion.T;
import org.erights.e.elib.serial.BaseLoader;

import java.net.URL;

/**
 * Bound to resource__uriGetter in the safe scope
 */
public class ResourceUriGetter extends BaseLoader {

    /**
     *
     */
    static public final ResourceUriGetter THE_ONE = new ResourceUriGetter();

    /**
     *
     */
    private ResourceUriGetter() {
    }

    /**
     * &lt;resource:...&gt; expands to resource__uriGetter.get(...)
     */
    public Object get(String uriBody) {
        URL result = ClassLoader.getSystemResource(uriBody);
        T.notNull(result, "Resource not found: ", uriBody);
        return result;
    }

    /**
     * XXX This one is unimplemented, and we should expect it to be
     * unimplemented for awhile, as it's hard.
     * <p/>
     * The problem is, the URL returned {@link ClassLoader#getSystemResource}
     * is not one that necessarily came from that source. For example,
     * &lt;resource:scripts/eBrowser.e-awt&gt; just returns a URL that could
     * have been returned by &lt;fileURL:.../scripts/eBrowser.e-awt&gt; and can
     * be uncalled by fileURL__uriGetter.
     */
    public Object[] optUncall(Object obj) {
        T.fail("XXX not yet implemented");
        return null; //make compiler happy
    }

    /**
     *
     */
    public String toString() {
        return "<resource>";
    }
}
