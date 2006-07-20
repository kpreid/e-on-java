package org.erights.e.elang.syntax;

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
import org.quasiliteral.syntax.LineFeeder;

import java.io.IOException;

/**
 * Like LineFeeder, but collapses @@ to @ and $$ to $. For use as input to
 * the E lexer/parser used as a quasi-parser.
 *
 * @author Mark S. Miller
 */
public class QuasiFeeder implements LineFeeder {

    /**
     *
     */
    private final LineFeeder myWrapped;

    /**
     *
     */
    public QuasiFeeder(LineFeeder wrapped) {
        myWrapped = wrapped;
    }

    /**
     * Like LineFeeder.optNextLine, but collapses every doubled @ or $ to a
     * single one.
     */
    public Twine optNextLine(boolean atTop,
                             boolean quoted,
                             int indent,
                             char closer,
                             int closeIndent)
      throws IOException {
        //XXX thread blockage point:
        Twine optResult = myWrapped.optNextLine(atTop,
                                                quoted,
                                                indent,
                                                closer,
                                                closeIndent);

        if (null == optResult) {
            return null;
        }
        return optResult.replaceAll("@@", "@").replaceAll("$$", "$");
    }
}
