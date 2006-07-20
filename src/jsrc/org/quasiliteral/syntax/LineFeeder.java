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

import java.io.IOException;

/**
 * Where the lexer gets its input from, one line of Twine at a time. <p>
 *
 * @author Mark S. Miller
 */
public interface LineFeeder {

    /**
     * Returns either a Twine containing the next line of input (and
     * presumably memory of where it came from), or null meaning end of
     * input (EOF). The parameters are for indentation-smart prompting or
     * pretty printing.
     *
     * @param atTop       Is this the beginning of a top-level unit (typically, a
     *                    top-level expression)?
     * @param quoted      Will this next line be taken as literal text?  If so,
     *                    then it should not be trimmed or indented. 'quoted' is
     *                    true between double quotes, or between quasi-quotes when
     *                    not inside a $ or @ hole.
     * @param indent      The suggested indentation level for the next line,
     *                    unless the next line begins with closer.
     * @param closer      The character that would close the most recent unclosed
     *                    openner.
     * @param closeIndent The suggested indentation level for the next line
     *                    if it does begin (after trimming) with closer.
     */
    Twine optNextLine(boolean atTop,
                      boolean quoted,
                      int indent,
                      char closer,
                      int closeIndent)
      throws IOException;
}
