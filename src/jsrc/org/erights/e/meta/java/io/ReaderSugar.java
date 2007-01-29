package org.erights.e.meta.java.io;

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

import org.erights.e.elib.tables.AssocFunc;
import org.erights.e.elib.tables.Twine;
import org.erights.e.meta.java.lang.CharacterMakerSugar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

/**
 * A sweetener defining extra messages that may be e-sent to a Reader.
 *
 * @author Mark S. Miller
 */
public class ReaderSugar {

    /**
     * prevent instantiation
     */
    private ReaderSugar() {
    }

    /**
     * Enumerates lineNumber =&gt; String (text line) associations.
     * <p/>
     * Each text line ends with a "\n". optURL defaults to null.
     */
    static public void iterate(Reader self, AssocFunc func)
      throws IOException {
        iterate(self, func, null);
    }

    /**
     * Enumerates lineNumber =&gt; String/Twine (text line) associations.
     * <p/>
     * Each text line ends with a "\n".
     */
    static public void iterate(Reader self, AssocFunc func, String optURL)
      throws IOException {
        BufferedReaderSugar.iterate(new BufferedReader(self), func, optURL);
    }

    /**
     * Gets the rest of the input as a String, normalizing newlines into
     * '\n's.
     */
    static public String getText(Reader self) throws IOException {
        return BufferedReaderSugar.getText(new BufferedReader(self));
    }

    /**
     * Gets the contents of the url as Twine (a text string that remembers
     * where it came from), normalizing newlines into '\n's.
     */
    static public Twine getTwine(Reader self, String url) throws IOException {
        return BufferedReaderSugar.getTwine(new BufferedReader(self), url);
    }

    /**
     * Returns the next character, or null at end of file.
     */
    static public Character readChar(Reader self) throws IOException {
        int result = self.read();
        if (-1 == result) {
            return null;
        } else {
            return CharacterMakerSugar.valueOf((char)result);
        }
    }

    /**
     * Reads no more than 'size' characters from the file, and return them as a
     * String. If at end-of-file, return null.
     */
    static public String readString(Reader self, int size) throws IOException {
        char[] cbuf = new char[size];
        int numRead = self.read(cbuf);
        if (-1 == numRead) {
            //end of file
            return null;
        }
        return new String(cbuf, 0, numRead);
    }
}
