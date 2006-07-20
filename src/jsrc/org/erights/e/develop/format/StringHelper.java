package org.erights.e.develop.format;

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

/**
 * Like the old StringSugar, except it's only a convenience for ELib
 * programmers, not for E language programmers. E language programmers never
 * see Java Strings directly, they only see Twine. Twine has some similar
 * methods built on these.
 *
 * @author Mark S. Miller
 */
public final class StringHelper {

    /**
     * prevent instantiation
     */
    private StringHelper() {
    }

    /**
     * reps repitions of self
     */
    static public String multiply(String self, int reps) {
        StringBuffer buf = new StringBuffer(self.length() * reps);
        for (int i = 0; i < reps; i++) {
            buf.append(self);
        }
        return buf.toString();
    }

    /**
     * The string-based replaceAll() supplements the character-based replace().
     *
     * @see String#replace
     */
    static public String replaceAll(String self,
                                    String oldStr,
                                    String newStr) {
        StringBuffer buf = new StringBuffer(self.length() * 2);
        int oldLen = oldStr.length();
        int p1 = 0;
        for (int p2 = self.indexOf(oldStr);
             p2 != -1;
             p2 = self.indexOf(oldStr, p1)) {
            buf.append(self.substring(p1, p2));
            buf.append(newStr);
            p1 = p2 + oldLen;
        }
        buf.append(self.substring(p1));
        return buf.toString();
    }

    /**
     * Each crlf is turned into an lf to deal with MSWindows, and then each
     * remaining cr is turned into an lf to deal with Mac.
     */
    static public String canonical(String self) {
        if (self.indexOf('\r') == -1) {
            return self;
        } else {
            return replaceAll(self, "\r\n", "\n").replace('\r', '\n');
        }
    }

    /**
     * Returns a string that, when interpreted as a literal, represents the
     * original string.
     */
    static public String quote(String self) {
        int len = self.length();
        StringBuffer buf = new StringBuffer(len * 2);
        buf.append('\"');
        for (int i = 0; i < len; i++) {
            char c = self.charAt(i);
            //XXX Mostly redundant with CharacterSugar.escaped(c).
            switch (c) {
            case '\b':
                {
                    buf.append("\\b");
                    break;
                }
            case '\t':
                {
                    buf.append("\\t");
                    break;
                }
            case '\n':
                {
                    //Output an actual newline, which is legal in a
                    //literal string in E.
                    buf.append("\n");
                    break;
                }
            case '\f':
                {
                    buf.append("\\f");
                    break;
                }
            case '\r':
                {
                    buf.append("\\r");
                    break;
                }
            case '\"':
                {
                    buf.append("\\\"");
                    break;
                }
//            case '\'':
//                {
//                    buf.append("\\\'");
//                    break;
//                }
            case '\\':
                {
                    buf.append("\\\\");
                    break;
                }
            default:
                {
                    if (c < 32 || c > 255) {
                        String num = "0000" + Integer.toHexString(c);
                        int numlen = num.length();
                        num = num.substring(numlen - 4, numlen);
                        buf.append("\\u").append(num);
                    } else {
                        buf.append(c);
                    }
                }
            }
        }
        buf.append('\"');
        return buf.toString();
    }

    /**
     * Return self prefixed by "a " or "an " according to a simple (and
     * therefore inadequate) heuristic, but good enough for cheezy uses.
     * <p/>
     * Note that this routine is not expected to internationalize well.
     */
    static public String aan(String self) {
        char c = Character.toLowerCase(self.charAt(0));
        if (self.length() >= 1 && "aeiouh".indexOf(c) != -1) {
            return "an " + self;
        } else {
            return "a " + self;
        }
    }
}
