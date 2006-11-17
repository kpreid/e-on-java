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

/**
 * Thrown if the input end sooner than expected, to suggest how much the next
 * line of input should be indented.
 *
 * @author Mark S. Miller
 */
public class NeedMoreException extends RuntimeException {

    private final boolean myQuoted;

    private final int myIndent;

    private final char myCloser;

    private final int myCloseIndent;

    /**
     * After msg, the remaining parameters are the same as for {@link
     * LineFeeder}, with the same
     *
     * @param msg         is the normal Exception message
     * @param quoted      Will this next line be taken as literal text?  If so,
     *                    then it should not be trimmed or indented. 'quoted'
     *                    is true between double quotes, or between
     *                    quasi-quotes when not inside a $ or @ hole.
     * @param indent      The suggested indentation level for the next line,
     *                    unless the next line begins with closer.
     * @param closer      The character that would close the most recent
     *                    unclosed openner.
     * @param closeIndent The suggested indentation level for the next line if
     *                    it does begin (after trimming) with closer.
     */
    public NeedMoreException(String msg,
                             boolean quoted,
                             int indent,
                             char closer,
                             int closeIndent) {
        super(msg);
        myQuoted = quoted;
        myIndent = indent;
        myCloser = closer;
        myCloseIndent = closeIndent;
    }

    /**
     *
     */
    public boolean isQuoted() {
        return myQuoted;
    }

    /**
     *
     */
    public int indent() {
        return myIndent;
    }

    /**
     *
     */
    public char getCloser() {
        return myCloser;
    }

    /**
     *
     */
    public int getCloseIndent() {
        return myCloseIndent;
    }
}
