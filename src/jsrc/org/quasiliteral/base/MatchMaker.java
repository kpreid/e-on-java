package org.quasiliteral.base;

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

import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.util.OneArgFunc;


/**
 * Represents a pattern template.
 * <p/>
 * A MatchMaker plus the args are a pattern. It will match itself plus args
 * against a provided specimen and either 1) report failure, or 2) report
 * success and provide binding resulting from the match.
 *
 * @author Mark S. Miller
 */
public interface MatchMaker {

    /**
     * Reports success by returning a list of bindings.
     * <p/>
     * Reports failure by invoking optEjector
     */
    ConstList matchBind(ConstList args,
                        Object specimen,
                        OneArgFunc optEjector);
}
