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

    static public final String TheLineSeparator =
//      System.getProperty("line.separator");
        "\n";

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
     * The string-based replaceAll() supplements the character-based
     * replace().
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
             -1 != p2;
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
        if (-1 == self.indexOf('\r')) {
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
            if ('\n' == c) {
                buf.append(c);
            } else {
                escapedInto(c, buf);
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
        if (1 <= self.length() && -1 != "aeiouh".indexOf(c)) {
            return "an " + self;
        } else {
            return "a " + self;
        }
    }

    /**
     * Just the part of a character's quoted form that encodes the character.
     * <p/>
     * In other words, everything except the enclosing quote signs. This is
     * used for composing a quoted {@link org.erights.e.meta.java.lang.CharacterSugar
     * #escaped(char) character}, {@link org.erights.e.develop.format.StringHelper#quote(String)
     * string}, or {@link org.erights.e.elib.tables.Twine#quote()} Twine.
     * <p/>
     * In order to be efficiently reusable, this method takes a StringBuffer
     * argument. Since this doesn't make much sense from E, this method is
     * tamed away.
     *
     * @noinspection UnnecessaryFullyQualifiedName
     */
    static public boolean escapedInto(char self, StringBuffer buf) {
        switch (self) {
        case'\b': {
            buf.append("\\b");
            return true;
        }
        case'\t': {
            buf.append("\\t");
            return true;
        }
        case'\n': {
            buf.append("\\n");
            return true;
        }
        case'\f': {
            buf.append("\\f");
            return true;
        }
        case'\r': {
            buf.append("\\r");
            return true;
        }
        case'\"': {
            buf.append("\\\"");
            return true;
        }
        case'\'': {
            buf.append("\\\'");
            return true;
        }
        case'\\': {
            buf.append("\\\\");
            return true;
        }
        default: {
            if (32 > self || 126 < self) {
                String num = "0000" + Integer.toHexString(self);
                int len = num.length();
                num = num.substring(len - 4, len);
                buf.append("\\u").append(num);
                return true;
            } else {
                buf.append(self);
                return false;
            }
        }
        }
    }
}
