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

import org.erights.e.elib.base.SourceSpan;
import org.erights.e.elib.tables.AssocFunc;
import org.erights.e.elib.tables.FlexList;
import org.erights.e.elib.tables.Twine;
import org.erights.e.meta.java.math.EInt;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * A sweetener defining extra messages that may be e-sent to a
 * Reader.
 *
 * @author Mark S. Miller
 */
public class BufferedReaderSugar {

    /**
     * prevent instantiation
     */
    private BufferedReaderSugar() {
    }

    /**
     * Enumerates lineNumber =&gt; String (text line) associations.
     * <p/>
     * Each text line ends with a "\n". optURL defaults to null.
     */
    static public void iterate(BufferedReader self, AssocFunc func)
      throws Throwable {
        iterate(self, func, null);
    }

    /**
     * Enumerates lineNumber =&gt; String/Twine (text line) associations.
     * <p/>
     * Each text line ends with a "\n". If optURL is null, then each text line
     * will be a String. Otherwise, each will be a Twine with location info.
     */
    static public void iterate(BufferedReader self,
                               AssocFunc func,
                               String optURL)
      throws IOException {
        for (int lineNum = 1; ; lineNum++) {
            String str = self.readLine();
            if (str == null) {
                //Reached EOF
                return;
            }
            str += '\n';
            Number lineNumObj = EInt.valueOf(lineNum);
            if (null == optURL) {
                func.run(lineNumObj, str);
            } else {
                SourceSpan span = new SourceSpan(optURL, true,
                                                 lineNum, 0,
                                                 lineNum, str.length() - 1);
                Twine line = Twine.fromString(str, span);
                func.run(lineNumObj, line);
            }
        }
    }

    /**
     * Gets the rest of the input as a String (equivalently, bare Twine),
     * normalizing newlines into '\n's.
     */
    static public String getText(BufferedReader self)
      throws IOException {
        StringBuffer buf = new StringBuffer();
        try {
            for (int lineNum = 1; ; lineNum++) {
                String str = self.readLine();
                if (str == null) {
                    //Reached EOF
                    return buf.toString();
                }
                buf.append(str);
                buf.append('\n');
            }
        } finally {
            self.close();
        }
    }

    /**
     * Gets the contents of the url as Twine (a text string that remembers
     * where it came from), normalizing newlines into '\n's.
     */
    static public Twine getTwine(BufferedReader self, String url)
      throws IOException {
        FlexList parts = FlexList.fromType(Twine.class);
        try {
            for (int lineNum = 1; ; lineNum++) {
                String str = self.readLine();
                if (str == null) {
                    //Reached EOF
                    return Twine.fromParts(parts.snapshot());
                }
                str += '\n';
                SourceSpan span = new SourceSpan(url, true,
                                                 lineNum, 0,
                                                 lineNum, str.length() - 1);
                Twine line = Twine.fromString(str, span);
                parts.push(line);
            }
        } finally {
            self.close();
        }
    }
}
