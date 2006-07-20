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

import org.erights.e.elib.tables.Twine;

/**
 * For feeding in one line of Twine at a time taken from a dynamically
 * provided Twine source.
 *
 * @author Mark S. Miller
 */
public class TwineFeeder implements LineFeeder {

    private final Twine mySource;

    private int myPos;

    /**
     *
     */
    public TwineFeeder(Twine sourceCode) {
        mySource = sourceCode;
        myPos = 0;
    }

    /**
     *
     */
    public String toString() {
        return "<TwineFeeder>";
    }

    /**
     *
     */
    public Twine optNextLine(boolean atTop,
                             boolean quoted,
                             int indent,
                             char closer,
                             int closeIndent) {
        int len = mySource.size();
        if (myPos >= len) {
            return null;
        }
        int i = mySource.indexOf("\n", myPos);
        if (-1 == i) {
            Twine result = (Twine)mySource.run(myPos, len);
            myPos = len;
            return result;
        }
        Twine result = (Twine)mySource.run(myPos, i + 1);
        myPos = i + 1;
        return result;
    }
}
