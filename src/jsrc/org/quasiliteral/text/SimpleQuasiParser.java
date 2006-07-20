package org.quasiliteral.text;

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

import org.erights.e.develop.assertion.T;
import org.erights.e.elib.base.ValueThunk;
import org.erights.e.elib.tables.IdentityCacheTable;
import org.erights.e.elib.tables.Twine;
import org.quasiliteral.base.MatchMaker;
import org.quasiliteral.base.QuasiExprParser;
import org.quasiliteral.base.QuasiPatternParser;
import org.quasiliteral.base.ValueMaker;

/**
 * The default -- and simplest -- of the quasi parsers
 *
 * @author Mark S. Miller
 */
public class SimpleQuasiParser
  implements QuasiExprParser, QuasiPatternParser {

    static public final SimpleQuasiParser THE_ONE = new SimpleQuasiParser();

    /**
     * caches previous simple parses (as is used for quasi-parsing)
     */
    private final IdentityCacheTable myCache =
      new IdentityCacheTable(Substituter.class, 100);

    private SimpleQuasiParser() {
    }

    /**
     *
     */
    private Substituter make(Twine template) {
        Substituter result =
          (Substituter)myCache.fetch(template, ValueThunk.NULL_THUNK);
        if (null == result) {
            result = new Substituter(template);
            myCache.put(template, result);
        }
        return result;
    }

    /**
     *
     */
    public ValueMaker valueMaker(Twine template, int[] dlrHoles) {
        T.fail("XXX new quasi valueMaker API not yet implemented");
        return null; //make compiler happy
    }

    /**
     * Synonym for 'make', used by quasi-literal expansion
     */
    public ValueMaker valueMaker(Twine template) {
        return make(template);
    }

    /**
     *
     */
    public MatchMaker matchMaker(Twine template,
                                 int[] dlrHoles,
                                 int[] atHoles) {
        T.fail("XXX new quasi matchMaker API not yet implemented");
        return null; //make compiler happy
    }

    /**
     * Synonym for 'make', used by quasi-pattern expansion
     */
    public MatchMaker matchMaker(Twine template) {
        return make(template);
    }
}
