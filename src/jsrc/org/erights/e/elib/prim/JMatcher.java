// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.elib.prim;

import org.erights.e.elib.tables.ConstList;

/**
 * Non-public classes that would introduce a public matcher should instead
 * implement JMatcher so that their match method is actually public.
 *
 * @author Mark S. Miller
 */
public interface JMatcher {

    /**
     * When a JMatcher (or any object with a truly public 'match(String,
     * ConstList)' method) is invoked through ELib, if no matching method is
     * found, then the match/2 method is invoked with the verb (message name)
     * and args of the message that didn't match.
     * <p/>
     * When defining a matcher, it's a good idea to define it to respond to
     * {@link MirandaMethods#__respondsTo} and {@link MirandaMethods#__getAllegedType}
     * appropriately. Most appropriate would be to respond with a description
     * of the behavior of the matcher as a whole. But a degenerate behavior is
     * better than none.
     * <p/>
     * XXX To solve bug <a href= "https://bugs.sieve.net/bugs/?func=detailbug&bug_id=125617&group_id=16380"
     * >Uninformative pattern-match reasons for failure</a> and similar related
     * problems, we should extend this interface so that the match ejector is
     * explicitly provided, so that __respondsTo and __getAllegedType can
     * distinguish match-failure from a thrown exception.
     *
     * @return
     */
    Object match(String verb, ConstList args) throws NoSuchMethodException;
}
