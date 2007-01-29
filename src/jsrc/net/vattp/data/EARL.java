package net.vattp.data;

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

import org.erights.e.develop.format.ETimeFormat;
import org.erights.e.develop.trace.Trace;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.meta.java.math.BigIntegerSugar;
import org.erights.e.meta.java.math.EInt;

import java.math.BigInteger;
import java.net.MalformedURLException;
import java.text.ParseException;

/**
 * A parsed URI designating an E object.
 * <p/>
 * Created by parsing of a stringified reference to an E-object. The
 * stringified form is:
 * <pre>    captp://*vatID@searchpath/swissStr[#expirationDate]</pre>
 * where <ul> <li>The "searchpath" is a comma-separated list of network
 * addresses to search to locate the vat designated by the "vatID". At these
 * addresses may be found either vats or VLSes. Each network address is
 * expected to be of the form parsed by {@link NetAddr#NetAddr(String)}, except
 * that an absent port number defaults to 80. <li>The "vatID", which is the
 * identity of a vat -- a public key fingerprint encoded in YURL32. A vatID
 * designates any vat that authenticates using the corresponding private key.
 * It is generally assumed that there is only one such live vat at any moment,
 * but this assumption is unenforceable. <li>The "swissStr" is the Swiss number
 * of the object within that vat. It is an unguessable secret number, encoded
 * in YURL32, whose knowledge demonstrates authority to invoke the object it
 * designates. <li>"expirationDate" is the optional registration expiration
 * date, represented as the difference, measured in milliseconds, between the
 * expiration time and midnight, January 1, 1970 UTC (known as the epoch). If
 * the expiration date is omitted or is equal to Long.MAX_VALUE milliseconds
 * since the epoch, then the expiration is assumed to be infinite. The format
 * of this date field is according to {@link org.erights.e.develop.format.ETimeFormat#formatTime(long)}.
 * </ul>
 * <p/>
 * Note that YURL32 is supported by {@link BigIntegerSugar#toYURL32(BigInteger)}
 * and {@link EInt#fromYURL32(String)}.</p>
 *
 * @author Bill Frantz based on work by Eric Messick.
 * @author Mark S. Miller (changed the date format and some comments)
 */
public class EARL {

    private String myURI;

    private ConstList mySearchPath;

    private String myVatID;

    private String mySwissStr;

    private long myExpiration;

    /**
     * Construct an EARL given the components of the URI.
     *
     * @param searchPath The search path, a list of domain names and/or IP
     *                   addresses.
     * @param vatID      is the vatID for the object, a YURL32 encoded public
     *                   key fingerprint.
     * @param swissNum   is the swissNumber for the object.
     * @param expiration is the registration expiration date. It is the
     *                   difference, measured in milliseconds, between the
     *                   expiration time and midnight, January 1, 1970 UTC (the
     *                   epoch). A value equal to Long.MAX_VALUE will also be
     *                   treated as infinite expiration.
     */
    public EARL(ConstList searchPath,
                String vatID,
                BigInteger swissNum,
                long expiration) throws MalformedURLException {
        mySearchPath = searchPath;
        myVatID = vatID;
        mySwissStr = BigIntegerSugar.toYURL32(swissNum);
        myExpiration = expiration;
        String flattenedSearchPath = flattenSearchPath(mySearchPath);
        myURI =
          "captp://*" + vatID + "@" + flattenedSearchPath + "/" + mySwissStr;
        if (Long.MAX_VALUE != expiration) {
            myURI += "#" + ETimeFormat.formatTime(expiration);
        }
        //XXX should reproduce here all the validity checks we test when
        //parsing.
        if (0 == flattenedSearchPath.length()) {
            throw new MalformedURLException("Search path must not be empty");
        }
    }

    /**
     * Construct an EARL given the URI string.
     *
     * @param uri is an URI of the form:
     *            <pre>captp://*vatID@searchpath/swissStr[#expirationDate]</pre>
     *            See the class comment for an explanation of the fields of
     *            this format.
     */
    public EARL(String uri) throws MalformedURLException {
        myURI = uri;
        parseEARL(uri);
    }

    /**
     * Return the expiration date string for this EARL.
     * <p/>
     * Infinite expiration is indicated as Long.MAX_VALUE.
     */
    public long expiration() {
        return myExpiration;
    }

    /**
     *
     */
    static public String flattenSearchPath(ConstList path) {
        String ret = "";
        if (path == null || 0 == path.size()) {
            return ret; // could be reasonable to throw an exception here...
        }

        int i = 0;
        while (i < path.size() - 1) {
            ret += path.get(i++) + ",";
        }
        ret += path.get(i);
        return ret;
    }

    /**
     * Return the swissStr string for this EARL.
     */
    public String swissStr() {
        return mySwissStr;
    }

    /**
     * Return the swissStr for this EARL as a swissNumber.
     */
    public BigInteger swissNumber() {
        return EInt.big(EInt.fromYURL32(mySwissStr));
    }

    /**
     * Parse a URI into its constituent elements, saving them as our own.
     * <p/>
     * Modified by MarkM so that it strips off any number of leading "captp:"
     * prefixes, including zero, enabling it to accept URI bodies as well.
     *
     * @param uri The URI string to be parsed.
     */
    private void parseEARL(String uri) throws MalformedURLException {
        if (Trace.comm.debug && Trace.ON) {
            Trace.comm.debugm("parseEarl(" + uri + ")");
        }
        if (uri == null) {
            throw new MalformedURLException("null URI");
        }
        String rest = uri.trim();
        while (rest.toLowerCase().startsWith("captp:")) {
            rest = rest.substring("captp:".length());
        }
        if (!rest.startsWith("//*")) {
            throw new MalformedURLException(
              "URI " + uri + " does not start with \"captp://*\"");
        }
        rest = rest.substring("//*".length());
        int i = rest.indexOf('@');
        if (1 > i) {
            throw new MalformedURLException(
              "URI " + uri + " does not have a vatID");
        }
        myVatID = rest.substring(0, i);
        rest = rest.substring(i + 1);

        i = rest.indexOf('/');
        if (1 > i) {
            throw new MalformedURLException(
              "URI " + uri + " does not have a search path");
        }
        String path = rest.substring(0, i);
        rest = rest.substring(i + 1);
        mySearchPath = parseSearchPath(path);


        i = rest.indexOf('#');
        if (0 > i) {
            i = rest.length();
        }
        if (1 > i) {
            throw new MalformedURLException(
              "URI " + uri + " does not have an swissStr");
        }
        mySwissStr = rest.substring(0, i);
        if (rest.length() > i) {
            rest = rest.substring(i + 1);
            try {
                myExpiration = ETimeFormat.parseTime(rest);
            } catch (ParseException e) {
                throw new MalformedURLException(
                  "Parsing expiration date \"" + rest + "\" " + e);
            }
        } else {
            myExpiration = Long.MAX_VALUE;
        }

        if (Trace.comm.debug && Trace.ON) {
            for (i = 0; i < mySearchPath.size(); i++) {
                Trace.comm
                  .debugm("mySearchPath[" + i + "]=" + mySearchPath.get(i));
            }
            Trace.comm
              .debugm("myVatID=" + myVatID + " mySwissStr=" + mySwissStr);
        }
    }

    /**
     * Parse a (comma-separated) search path into its constituent elements
     *
     * @param path The search path to be parsed
     * @return An array of Strings containing the elements of the search path
     */
    static public ConstList parseSearchPath(String path) {
        int count;
        int start;
        int end;
        String[] parts;

        if (Trace.comm.debug && Trace.ON) {
            Trace.comm.debugm("parsing " + path);
        }
        if (path == null || 0 == path.length()) {
            return ConstList.EmptyList;
        }
        count = 1;
        start = -1;
        while (0 <= (start = path.indexOf(',', start + 1))) {
            count++;
        }
        parts = new String[count];
        start = 0;
        for (int i = 0; i < count - 1; i++) {
            end = path.indexOf(',', start);
            parts[i] = path.substring(start, end);
            if (Trace.comm.debug && Trace.ON) {
                Trace.comm.debugm("found " + parts[i]);
            }
            start = end + 1;
        }
        parts[count - 1] = path.substring(start);
        if (Trace.comm.debug && Trace.ON) {
            Trace.comm.debugm("found " + parts[count - 1]);
        }
        return ConstList.fromArray(parts);
    }

    /**
     * Return the search path for this EARL.
     *
     * @return an array of strings, one element for each search path member
     */
    public ConstList searchPath() {
        return mySearchPath;
    }

    /**
     * Return the vatID string for this EARL.
     */
    public String vatID() {
        return myVatID;
    }

    /**
     * Without angle brackets
     */
    public String getURI() {
        return myURI;
    }

    /**
     * Since this corresponds to an E URI expression, print with angle
     * brackets.
     */
    public String toString() {
        return "<" + myURI + ">";
    }
}
