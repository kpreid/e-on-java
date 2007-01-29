package org.erights.e.meta.java.net;

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
import org.erights.e.develop.exception.ExceptionMgr;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.serial.BaseLoader;
import org.erights.e.elib.tables.AssocFunc;
import org.erights.e.elib.tables.Twine;
import org.erights.e.meta.java.io.BufferedReaderSugar;
import org.erights.e.meta.java.io.FileGetter;
import org.erights.e.meta.java.io.InputStreamSugar;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;

/**
 * A sweetener defining extra messages that may be e-sent to a URL.
 * <p/>
 * Makes a URL act as if it implements {@link org.erights.e.elib.serial.Loader}.
 * <p/>
 * Note that a URL of the form <tt>&lt;protocol:path&gt;</tt> grants access
 * only to the resource named "path" starting with that protocol handler. By
 * contrast, a URL of the form <tt>&lt;protocol:path/&gt;</tt> grants access to
 * all resources whose names, starting at that protocol handler, begin with
 * "path/". For example, <tt>&lt;http://www.erights.org&gt;</tt> evaluates to a
 * URL which grants access to the erights.org home page, whereas
 * <tt>&lt;http://www.erights.org/&gt;</tt> grants access to the whole
 * website.
 * <p/>
 * Note that the {@link org.erights.e.elang.interp.URLGetter
 * fileURL__uriGetter} returns a {@link URL}, which therefore plays by the
 * above rules, but that the {@link FileGetter file__uriGetter} returns a
 * {@link File} which doesn't. A File always allows prefixing separated by "/"
 * but disallowing "..".
 */
public class URLSugar {

    /**
     * prevent instantiation
     */
    private URLSugar() {
    }

    /**
     * Open 'self' for reading text, decoding UTF-8 and turning platform
     * newlines into '\n's
     */
    static public BufferedReader textReader(URL self) throws IOException {
        return new BufferedReader(new InputStreamReader(self.openStream()));
    }

    /**
     * Enumerates lineNumber =&gt String (text line) associations.
     * <p/>
     * Each text line ends with a "\n". isLocated defaults to false.
     */
    static public void iterate(URL self, AssocFunc func) throws IOException {
        iterate(self, func, false);
    }

    /**
     * Enumerates lineNumber =&gt String/Twine (text line) associations.
     * <p/>
     * Each text line ends with a "\n".
     */
    static public void iterate(URL self, AssocFunc func, boolean isLocated)
      throws IOException {
        BufferedReader reader = textReader(self);
        try {
            String optURL = null;
            if (isLocated) {
                optURL = self.toString();
            }
            BufferedReaderSugar.iterate(reader, func, optURL);
        } finally {
            reader.close();
        }
    }

    /**
     * Gets the contents of the url as String, normalizing newlines into
     * '\n's.
     */
    static public String getText(URL self) throws IOException {
        return BufferedReaderSugar.getText(textReader(self));
    }

    /**
     * Gets the contents of the url as Twine (a text string that remembers
     * where it came from), normalizing newlines into '\n's.
     */
    static public Twine getTwine(URL self) throws IOException {
        return BufferedReaderSugar.getTwine(textReader(self), self.toString());
    }

    /**
     * A SHA hash of the binary content of the URL's contents.
     */
    static public BigInteger getCryptoHash(URL self)
      throws NoSuchAlgorithmException, IOException {
        return InputStreamSugar.getCryptoHash(self.openStream());
    }

    /**
     * Everything after 'protocol:'.
     *
     * @return
     */
    static public String getBody(URL self) {
        String str = self.toExternalForm();
        int colon = str.indexOf(':');
        T.require(0 <= colon, "':' not found: ", str);
        return str.substring(colon + 1);
    }

    /**
     * "implements" {@link org.erights.e.elib.serial.Loader#get}, but only if
     * the body of this URL ends in a "/".
     *
     * @return
     */
    static public URL get(URL self, String suffix) {
        String prefix = self.toExternalForm();
        T.require(prefix.endsWith("/"), "Traversal not allowed: ", prefix);
        try {
            return new URL(prefix + suffix);
        } catch (MalformedURLException e) {
            throw ExceptionMgr.asSafe(e);
        }
    }

    /**
     * "implements" {@link org.erights.e.elib.serial.Loader#optUncall}, but
     * only if the body of this URL ends in a "/".
     *
     * @return
     */
    static public Object[] optUncall(URL self, Object obj) {
        if (!(obj instanceof URL)) {
            return null;
        }
        URL url = (URL)obj;
        String selfStr = self.toExternalForm();
        T.require(selfStr.endsWith("/"), "Traversal not allowed: ", selfStr);
        String urlStr = url.toExternalForm();
        if (!urlStr.startsWith(selfStr)) {
            return null;
        }
        String suffix = urlStr.substring(selfStr.length());
        return BaseLoader.ungetToUncall(self, suffix);
    }

    /**
     * E URIs have angle brackets around them
     */
    static public void __printOn(URL self, TextWriter out) throws IOException {
        String ef = self.toExternalForm();
        int colon = ef.indexOf(':');
        if (0 <= colon) {
            String prot = ef.substring(0, colon);
            String rest = ef.substring(colon);
            if ("file".equals(prot)) {
                prot = "fileURL";
            }
            ef = prot + rest;
        }
        out.print("<", ef, ">");
    }
}
