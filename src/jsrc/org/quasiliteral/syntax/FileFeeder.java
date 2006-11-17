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

import org.erights.e.develop.format.StringHelper;
import org.erights.e.elib.base.SourceSpan;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.tables.Twine;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * A FileFeeder reads its input from a Reader which it assumes starts at the
 * beginning of line 1 in the text unit (file?) described by the Url.
 *
 * @author Mark S. Miller
 */
public class FileFeeder implements LineFeeder {

    private final String myUrl;

    private int myLineNum;

    private BufferedReader myOptReader;

    private final TextWriter myOptOuts;

    /**
     *
     */
    public FileFeeder(String url,
                      BufferedReader optReader,
                      TextWriter optOuts) {
        myUrl = url;
        myLineNum = 0; //haven't read anything yet
        myOptReader = optReader;
        myOptOuts = optOuts;
    }

    /**
     *
     */
    public String toString() {
        return "<Feeding from " + myUrl + ">";
    }

    /**
     *
     */
    public Twine optNextLine(boolean atTop,
                             boolean quoted,
                             int indent,
                             char closer,
                             int closeIndent) throws IOException {
        if (myOptReader == null) {
            return null;
        }
        if (myOptOuts != null) {
            myOptOuts.print(prompt(atTop,
                                   quoted,
                                   indent,
                                   closer,
                                   closeIndent));
            myOptOuts.flush();
        }
        //XXX thread blockage point:
        String optResultStr = myOptReader.readLine();
        myLineNum++;

        if (null == optResultStr) {
            myOptReader.close();
            myOptReader = null;

        } else {
            optResultStr += "\n";
        }
        if (null == optResultStr) {
            return null;
        }
        SourceSpan span = new SourceSpan(myUrl,
                                         true,
                                         myLineNum,
                                         0,
                                         myLineNum,
                                         optResultStr.length() - 1);
        return Twine.fromString(optResultStr, span);
    }

    /**
     *
     */
    private String prompt(boolean atTop,
                          boolean quoted,
                          int indent,
                          char closer,
                          int closeIndent) {
        if (atTop) {
            return "? ";
        } else if (quoted) {
            return "> ";
        } else {
            //Kludge: without something like GNU's readline or the primitives
            //on which it builds, we can't output spaces that the user can
            //backspace over, so we just output the minimal number of spaces
            //he might need.
            int reps = StrictMath.max(0, StrictMath.min(indent, closeIndent));
            return "> " + StringHelper.multiply(" ", reps);
        }
    }
}
