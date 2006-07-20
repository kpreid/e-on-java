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

import org.erights.e.elib.tables.AssocFunc;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.meta.java.math.EInt;

import java.io.IOException;


/**
 * A sweetener defining extra messages that may be e-sent to a StringBuffer.
 *
 * @author Mark S. Miller
 */
public class StringBufferSugar {

    /**
     * prevents instantiation
     */
    private StringBufferSugar() {
    }

    /**
     * "bar"[2] == 'r'
     */
    static public char get(StringBuffer self, int index) {
        return self.charAt(index);
    }

    /**
     * Considered as a map, an array is a ConstMap from indices to
     * values, so iterate() will call func with each successive pair of
     * index and value, in ascending index order.
     */
    static public void iterate(StringBuffer self, AssocFunc func) {
        int len = self.length();
        for (int k = 0; k < len; k++) {
            func.run(EInt.valueOf(k),
                     CharacterMakerSugar.valueOf(self.charAt(k)));
        }
    }

    /**
     *
     */
    static public int size(StringBuffer self) {
        return self.length();
    }

    /**
     * Just like the Java-level toString, but avoids confusion with
     * Object.toString().
     */
    static public String snapshot(StringBuffer self) {
        return self.toString();
    }

    /**
     *
     * @param self
     * @param out
     */
    static public void __printOn(StringBuffer self, TextWriter out)
      throws IOException {
        out.print("<stringBuffer ");
        out.quote(self.toString());
        out.print(">");
    }
}
