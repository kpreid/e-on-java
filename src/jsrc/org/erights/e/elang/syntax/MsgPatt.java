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

import org.erights.e.elang.evm.EExpr;
import org.erights.e.elang.evm.Pattern;
import org.erights.e.elib.base.SourceSpan;


/**
 * For a Parser temporary
 *
 * @author Mark S. Miller
 */


class MsgPatt {

    private final SourceSpan myOptSpan;

    private final String myVerb;

    private final Pattern[] myPatterns;

    private final EExpr myOptResultGuard;

    MsgPatt(SourceSpan optSpan,
                   String verb,
                   Pattern[] patterns,
                   EExpr optResultGuard) {
        myOptResultGuard = optResultGuard;
        myOptSpan = optSpan;
        myPatterns = patterns;
        myVerb = verb;
    }

    /**
     * @return
     */
    public SourceSpan getOptSpan() {
        return myOptSpan;
    }

    /**
     * @return
     */
    public String getVerb() {
        return myVerb;
    }

    /**
     * @return
     */
    public Pattern[] getPatterns() {
        return myPatterns;
    }

    /**
     * @return
     */
    public EExpr getOptResultGuard() {
        return myOptResultGuard;
    }
}
