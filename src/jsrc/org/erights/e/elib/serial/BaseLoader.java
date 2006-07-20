// Copyright 2003 Hewlett Packard, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.elib.serial;

/**
 * @author Mark S. Miller
 */
public abstract class BaseLoader implements Loader {

    /**
     * @return
     */
    static public Object[] ungetToUncall(Object self, String ungetPath) {
        Object[] args = {ungetPath};
        Object[] result = {self, "get", args};
        return result;
    }

    /**
     * A convenience method for an implementing a wrapping Uncaller as an
     * authority-diminishing facet on a more powerful underlying wrapped
     * Uncaller.
     * <p/>
     * Iff wrapped returns according to the expected pattern
     * <pre>    [wrapped, "get", [`$prefix@shortName`]]</pre>
     * does this method then return instead
     * <pre>    [self, "get", [shortName]]</pre>
     * Otherwise, it returns null.
     * <p/>
     * See {@link Uncaller#optUncall} for the security constraint that
     * would typically cause one to use this method.
     * <p/>
     *
     * @param self    The authority-diminishing facet we're going to portray
     *                the answer in terms of.
     * @param wrapped The powerful Uncaller we're going to consult.
     * @param prefix  Must include the separator at the end, such as the
     *                terminal "." or "/".
     * @param obj     The object being portrayed.
     * @return Either null, or a portrayal-triple that reveals no more
     *         authority than that already held by someone who holds both
     *         <tt>wrapper</tt> and <tt>obj</tt>.
     */
    static public Object[] getOptWrappingUncall(Object self,
                                                Uncaller wrapped,
                                                String prefix,
                                                Object obj) {
        Object[] optFullDesc = wrapped.optUncall(obj);
        if (null == optFullDesc) {
            return null;
        }
        Object[] fullArgs = (Object[])optFullDesc[2];
        if (fullArgs.length != 1) {
            return null;
        }
        String fullName = (String)fullArgs[0];
        if (wrapped == optFullDesc[0] &&
          "get".equals(optFullDesc[1]) &&
          fullName.startsWith(prefix)) {

            String shortName = fullName.substring(prefix.length());
            return ungetToUncall(self, shortName);
        }
        return null;
    }

    /**
     * Even though it would seem more natural to implement optUncall out of
     * optUnget, it should actually go the other way for reasons explained
     * <a href=
     * "http://www.erights.org/data/serial/jhu-paper/subgraph-security.html"
     * >here</a>.
     */
    public String optUnget(Object child) {
        Object[] optPortrayal = optUncall(child);
        if (null != optPortrayal &&
          this == optPortrayal[0] &&
          "get".equals(optPortrayal[1])) {

            return (String)optPortrayal[2];
        } else {
            return null;
        }
    }
}
