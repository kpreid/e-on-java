package org.quasiliteral.syntax;

/*
The contents of this file are subject to the Electric Communities E Open
Source Code License Version 1.0 (the "License"); you may not use this file
except in compliance with the License. You may obtain a copy of the License
at http://www.communities.com/EL/.

Software distributed under the License is distributed on an "AS IS" basis,
WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
the specific language governing rights and limitations under the License.

The Original Code is the Distributed E Language Implementation, released
July 20, 1998.

The Initial Developer of the Original Code is Electric Communities.
Copyright (C) 1998 Electric Communities. All Rights Reserved.

Contributor(s): ______________________________________.
*/

import org.erights.e.develop.assertion.T;
import org.erights.e.elib.tables.Twine;

/**
 * This class provides some conveniences for manipulating E URI-tokens.
 * <p/>
 * A URI-token is an E token with a type-code of either URI or URIStart.<pre>
 * <pre>
 *     &lt;uri&gt; ::= '&lt;' {@link
 * org.erights.e.elang.syntax.ELexer#identifier &lt;identifier&gt;
 * } ':' {@link #isURIC &lt;uric&gt;}* '&gt;'
 *     &lt;uriStart&gt; ::= '&lt;' &lt;identifier&gt; ':'
 * </pre>
 *
 * @author Mark S. Miller
 */
public class URIKit {

    static private final boolean[] URICs = new boolean[128];

    private URIKit() {
    }

    static {
        for (int i = 0; 128 > i; i++) {
            //XXX this intitialization may be unnecessary, as the
            //language probably guarantees initialization to false.
            URICs[i] = false;
        }
        for (char c = 'a'; 'z' >= c; c++) {
            URICs[c] = true;
        }
        for (char c = 'A'; 'Z' >= c; c++) {
            URICs[c] = true;
        }
        for (char c = '0'; '9' >= c; c++) {
            URICs[c] = true;
        }
        String allowed = ";/?:@&=+$,-_.!~*'()%\\|#";
        for (int i = 0; i < allowed.length(); i++) {
            URICs[allowed.charAt(i)] = true;
        }
    }

    /**
     * Normalizes an input URI string to the "actual" URI string.
     * <p/>
     * A backslash ('\\') normalizes to a slash ('/'), and a vertical bar ('|')
     * normalizes to a colon (':'), leaving only URIC characters (according to
     * <a href= "http://www.ics.uci.edu/pub/ietf/uri/rfc2396.txt" >BNF of
     * opaque URIs (see Appendix A)</a>) and the fragment identifier
     * (sharp-sign ('#')).
     * <p/>
     * XXX An open question is whether normalize/1 should also normalize
     * '%&lt;hex&gt;&lt;hex&gt;' to the encoded character. Currently this is
     * not done.
     */
    static public Twine normalize(Twine uriText) {
        return uriText.replaceAll("\\", "/").replaceAll("|", ":");
    }

    /**
     * All text up to but not including the first colon.
     */
    static public Twine getProtcol(Twine uriText) {
        int colon = uriText.indexOf(":");
        T.require(0 <= colon, "':' not found: ", uriText);
        return (Twine)uriText.run(0, colon);
    }

    /**
     * All text after the first colon
     */
    static public Twine getBody(Twine uriText) {
        int colon = uriText.indexOf(":");
        T.require(0 <= colon, "':' not found: ", uriText);
        return (Twine)uriText.run(colon + 1, uriText.size());
    }

    /**
     * According to <a href= "http://www.ics.uci.edu/pub/ietf/uri/rfc2396.txt"
     * >BNF of opaque URIs (see Appendix A)</a> these are characters that can
     * occur within a URI body:
     * <pre>    a-z, A-Z, 0-9, any of ;/?:@&=+$,-_.!~*'()%</pre>
     * In addition, by special dispensation, we allow '\\', which we normalize
     * to '/', and '|', which we normalize to ':', and '#' which is the
     * fragment indicator.
     * <p/>
     * {@link org.erights.e.meta.java.io.FileGetter FileGetter} also does these
     * normalizations dynamically.
     */
    static public boolean isURIC(char c) {
        return 128 > c && URICs[c];
    }

    /**
     * @return
     */
    static public boolean isURICs(String str) {
        for (int i = 0, len = str.length(); i < len; i++) {
            if (!isURIC(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
