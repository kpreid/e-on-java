// Copyright 2004 Waterken Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html
package org.waterken.uri;

/**
 * <i>U</i>niform <i>R</i>esource <i>I</i>dentifier manipulation.
 *
 * @author Tyler
 */
public final class URI {

    private URI() {
    }

    /**
     * Extracts the <code>scheme</code> component.
     *
     * @param otherwise The default value.
     * @param uri       An absolute URI.
     * @return The <code>scheme</code> component.
     */
    public static String scheme(final String otherwise, final String uri) {
        final int last = scheme_end(uri);
        return -1 != last ? uri.substring(0, last).toLowerCase() : otherwise;
    }

    private static int scheme_end(final String uri) {
        int last;
        final int len = uri.length();
        if (0 == len || !isStartSymbol(uri.charAt(0))) {
            last = -1;
        } else {
            last = 1;
            while (len != last && isComponentSymbol(uri.charAt(last))) {
                ++last;
            }
            if (last == len || ':' != uri.charAt(last)) {
                last = -1;
            }
        }
        return last;
    }

    private static boolean isStartSymbol(final char c) {
        return ('a' <= c && 'z' >= c) || ('A' <= c && 'Z' >= c);
    }

    private static boolean isComponentSymbol(final char c) {
        return ('a' <= c && 'z' >= c) || ('A' <= c && 'Z' >= c) ||
          ('0' <= c && '9' >= c) || '+' == c || '.' == c || '-' == c;
    }

    /**
     * Extracts the <code>authority</code> component.
     *
     * @param uri An absolute URI.
     * @return The <code>authority</code> component.
     */
    public static String authority(final String uri) {
        final int first = authority_begin(uri, scheme_end(uri));
        final int last = authority_end(uri, first);
        return uri.substring(first, last);
    }

    private static int authority_begin(final String uri, int first) {
        ++first;    // Skip past the ':' separator.
        if (uri.startsWith("//", first)) {
            first += 2;
        }
        return first;
    }

    private static int authority_end(final String uri, final int first) {
        final int last = uri.indexOf('/', first);
        return -1 != last ? last : hierarchy_end(uri, first);
    }

    private static int hierarchy_end(final String uri, final int first) {
        final int query = uri.indexOf('?', first);
        final int fragment = uri.indexOf('#', first);
        return -1 == query ?
          (-1 == fragment ? uri.length() : fragment) :
          (-1 == fragment ? query : (query < fragment ? query : fragment));
    }

    /**
     * Extracts the rootless <code>path</code> component.
     *
     * @param uri An absolute URI.
     * @return The rootless <code>path</code> component.
     */
    public static String path(final String uri) {
        final int first = service_end(uri);
        final int last = hierarchy_end(uri, first);
        return last != first ? uri.substring(first + 1, last) : "";
    }

    private static int service_end(final String uri) {
        return authority_end(uri, authority_begin(uri, scheme_end(uri)));
    }

    /**
     * Extracts the <code>query</code> component.
     *
     * @param otherwise The default value.
     * @param uri       An absolute URI.
     * @return The <code>query</code> component.
     */
    public static String query(final String otherwise, final String uri) {
        String r;
        final int start = uri.indexOf('?');
        if (-1 == start) {
            r = otherwise;
        } else {
            final int end = uri.indexOf('#');
            r = -1 == end ?
              uri.substring(start + 1) :
              (start < end ? uri.substring(start + 1, end) : otherwise);
        }
        return r;
    }

    /**
     * Extracts the <code>fragment</code> component.
     *
     * @param otherwise The default value.
     * @param uri       An absolute URI.
     * @return The <code>fragment</code> component.
     */
    public static String fragment(final String otherwise, final String uri) {
        final int start = uri.indexOf('#');
        return -1 != start ? uri.substring(start + 1) : otherwise;
    }

    /**
     * Extracts the proxy request URI.
     *
     * @param uri An absolute URI.
     * @return The URI, stripped of any <code>fragment</code> component.
     */
    public static String proxy(final String uri) {
        final int start_fragment = uri.indexOf('#');
        return -1 == start_fragment ? uri : uri.substring(0, start_fragment);
    }

    /**
     * Extracts the remote service identifier.
     *
     * @param uri An absolute URI.
     * @return The <code>scheme</code> and <code>authority</code> components.
     */
    public static String service(final String uri) {
        return uri.substring(0, service_end(uri));
    }

    /**
     * Extracts the request URI.
     *
     * @param uri An absolute URI.
     * @return The <code>path</code> and <code>query</code> components.
     */
    public static String request(final String uri) {
        final int first = service_end(uri);
        final int last = uri.indexOf('#', first);
        return -1 == last ? uri.substring(first) : uri.substring(first, last);
    }

    /**
     * Resolves a relative URI string.
     *
     * @param base     An absolute URI.
     * @param relative A relative URI string.
     * @return The resolved URI.
     */
    public static String resolve(final String base, final String relative) {
        String r;
        if ("".equals(relative)) {
            r = proxy(base);
        } else if ('#' == relative.charAt(0)) {
            r = proxy(base) + relative;
        } else if ('?' == relative.charAt(0)) {
            r = base.substring(0, hierarchy_end(base, 0)) + relative;
        } else if (relative.startsWith("//")) {
            r = scheme("", base) + ":" + relative;
        } else if (relative.startsWith("/")) {
            r = service(base) + relative;
        } else if (-1 != scheme_end(relative)) {
            r = relative;
        } else {
            final String root = service(base);
            final int base_path_first = root.length();
            final int base_path_last = hierarchy_end(base, base_path_first);
            final String base_path = base_path_last != base_path_first ?
              base.substring(base_path_first + 1, base_path_last) :
              "";
            final String base_folder =
              base_path.substring(0, base_path.lastIndexOf('/') + 1);
            final int relative_path_last = hierarchy_end(relative, 0);
            final String relative_path =
              relative.substring(0, relative_path_last);
            final String path = Path.vouch(base_folder + relative_path);
            final String absolute_path = "".equals(path) ? "" : "/" + path;
            final String tail = relative.substring(relative_path_last);
            r = root + absolute_path + tail;
        }
        return r;
    }

    /**
     * Encodes an absolute URI relative to a base URI.
     *
     * @param base   The absolute base URI.
     * @param target The absolute target URI.
     * @return The relative URI string from base to target.
     */
    public static String relate(final String base, final String target) {
        String r;
        final int first = service_end(base);
        if (base.regionMatches(0, target, 0, first)) {
            // Determine the common parent folder.
            final int last = hierarchy_end(base, first);
            final String path = base.substring(first, last);
            int i = 0;
            int j = path.indexOf('/');
            while (-1 != j &&
              path.regionMatches(i, target, first + i, j + 1 - i)) {
                j = path.indexOf('/', i = j + 1);
            }
            if (-1 == j) {
                // Compare the last segment.
                j = last - first;
                if (path.regionMatches(i, target, first + i, j - i) && (
                  last == target.length() || '?' == target.charAt(last) ||
                    '#' == target.charAt(last))) {
                    // Compare the query.
                    int f = base.indexOf('#', last);
                    if (-1 == f) {
                        f = base.length();
                    }
                    if (base.regionMatches(last, target, last, f - last) &&
                      (f == target.length() || '#' == target.charAt(f))) {
                        r = target.substring(f);
                    } else {
                        r = target.substring(last);
                    }
                } else {
                    r = target.substring(first + i);
                }
            } else {
                // Wind up to the common base.
                final StringBuffer buffer = new StringBuffer();
                if (0 == j) {
                    j = path.indexOf('/', 1);
                }
                while (-1 != j) {
                    buffer.append("../");
                    j = path.indexOf('/', j + 1);
                }
                buffer.append(target.substring(first + i));
                r = buffer.toString();
            }
        } else {
            r = target;
        }
        return r;
    }
}
