package org.erights.e.meta.java.lang;

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
import org.erights.e.elib.oldeio.TextWriter;

import java.io.IOException;

/**
 * A sweetener defining extra messages that may be e-sent to characters.
 *
 * @author Mark S. Miller
 */
public class CharacterSugar {

    /**
     * prevent instantiation
     */
    private CharacterSugar() {
    }

    /**
     * Unicode character code
     */
    static public int asInteger(char self) {
        return self;
    }

    /**
     *
     */
    static public String getCategory(char self) {
        int type = Character.getType(self);
        String optResult = CharacterMakerSugar.CHAR_CAT_PAIRS[type][0];
        T.requireSI(null != optResult,
                    "internal: Invalid character type: ", type);
        return optResult;
    }

    /**
     *
     */
    static public char add(char self, int delta) {
        return CharacterMakerSugar.asChar((long)self + delta);
    }

    /**
     * Kludge to force overload resolution to prevent a char from
     * successfully Java-coercing, on method.invoke(), to an int.
     */
    static public void add(char self, char other) {
        throw new IllegalArgumentException("Can't add characters");
    }

    /**
     *
     */
    static public char subtract(char self, int delta) {
        return CharacterMakerSugar.asChar((long)self - delta);
    }

    /**
     *
     */
    static public int subtract(char self, char other) {
        return self - other;
    }

    /**
     *
     */
    static public char next(char self) {
        if (self >= Character.MAX_VALUE) {
            return self;
        } else {
            return (char)(self + 1);
        }
    }

    /**
     *
     */
    static public char previous(char self) {
        if (self <= Character.MIN_VALUE) {
            return self;
        } else {
            return (char)(self - 1);
        }
    }

    /**
     *
     */
    static public char min(char self, char other) {
        return (char)StrictMath.min((int)self, (int)other);
    }

    /**
     *
     */
    static public char max(char self, char other) {
        return (char)StrictMath.max((int)self, (int)other);
    }

    /**
     * Just prints the character itself to the stream.
     * <p/>
     * If you want to print the quoted form, use c.quote() instead.
     * <p/>
     * Prior to 0.8.26i, the spec used to say:
     * Unlike Java's Writer.print(char), E's chars print by printing
     * their quoted form.
     * <p/>
     * If you want to contribute the character itself to a TextWriter,
     * print it by doing <tt>out.print(""+c)</tt>
     */
    static public void __printOn(char self, TextWriter out)
      throws IOException {
        out.print("" + self);
    }

    /**
     *
     */
    static public String quote(char self) {
        return "'" + escaped(self) + "'";
    }

    /**
     * Just the part of a character's quoted form that encodes the
     * character.
     * <p/>
     * In other words, everything except the enclosing quote signs.
     * This is used for composing a quoted character. It is mostly redundant
     * with {@link org.erights.e.elib.tables.Twine#quote} and
     * {@link org.erights.e.develop.format.StringHelper#quote}, but this one
     * outputs a backslash-en for a newline, rather than the newline itself.
     */
    static public String escaped(char self) {
        StringBuffer buf = new StringBuffer();
        //XXX Mostly edundant with the inner loop of Twine.quote() and
        //StringHelper.quote()
        switch (self) {
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
                buf.append("\\n");
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
        case '\'':
            {
                buf.append("\\\'");
                break;
            }
        case '\\':
            {
                buf.append("\\\\");
                break;
            }
        default:
            {
                if (self < 32 || self > 255) {
                    String num = "0000" + Integer.toHexString(self);
                    int len = num.length();
                    num = num.substring(len - 4, len);
                    buf.append("\\u" + num);
                } else {
                    buf.append(self);
                }
            }
        }
        return buf.toString();
    }
}
