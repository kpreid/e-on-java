package org.erights.e.elib.base;

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

import org.erights.e.elib.tables.FlexList;

/**
 * The "code" executed to process a request sent to an object.
 *
 * @author Mark S. Miller
 */
public interface Script {

    /**
     * Returns a Script at least as good as this script for purposes of being
     * execute()d with a message matching the following description.
     * <p/>
     * We intend to grow this mechanism into a call-site caching mechanism, but
     * currently it's just to shorten the Java call stack needed for a given E
     * call stack.
     * <p/>
     * If the call won't actually resolve, then shorten may throw the relevant
     * exception, or it may return a Script whose execute will throw that
     * exception. The choice shouldn't matter.
     * <p/>
     * This method represents a start on the mechanism needed for call-site
     * cacheing, but the API will need to change to get their.
     *
     * @param optSelf If present, then a script could shorten using
     *                instance-specific data.
     * @param aVerb   Must be interned
     * @param arity   Typically, only its arity is used. But OverloaderNode
     *                uses the types of the arguments to select among Java
     *                overloads.
     * @return The Script to use in lieu of this one. Note that it's always
     *         correct a Script to just return itself.
     */
    Script shorten(Object optSelf, String aVerb, int arity);

    /**
     * Used to test for a call-site-cache hit.
     * <p/>
     * The script being tested should be the result of a previous shorten/3
     * with the same verb and arity, so we don't need to recheck these. This
     * message asks: If I did a lookup and shorten with the same verb and
     * arity, but a different optSelf, would I get back this script? It's
     * always safe to say <tt>false</tt>.
     * <p/>
     * The uppercase "R" suffix indicate that this method's callers must ensure
     * that the "short" arguments are already in the form that would be
     * returned by {@link org.erights.e.elib.ref.Ref#resolution
     * Ref.resolution/1}.
     *
     * @param optShortSelf Must already be in the form that would be returned
     *                     by {@link org.erights.e.elib.ref.Ref#resolution
     *                     Ref.resolution/1}.
     * @return Whether we have a call-site-cache hit.
     */
    boolean canHandleR(Object optShortSelf);

    /**
     * verb must be an interned string
     */
    Object execute(Object optSelf, String verb, Object[] args);

    /**
     * Adds to 'mTypes' the mappings provided by this script.
     *
     * @param optSelf If non-null, and if this script has a match clause, then
     *                delegate an __allegedType query to optSelf via the match
     *                clause so it can add further elements to mTypes.
     *                <p/>
     *                Since 'null' is normally a valid value for a 'self', this
     *                use of 'null' depends on our knowledge that 'null''s
     *                script does not have its own match clause.
     * @param mTypes  message descriptions.
     */
    void protocol(Object optSelf, FlexList mTypes);

    /**
     * Does an object whose behavior is this script respond to verb/arity
     * messages?
     *
     * @param optSelf If non-null, and if this script has a match clause, and
     *                if this script doesn't have a directly matching method,
     *                then delegate the respondsTo question to optSelf via the
     *                match clause to see if it responds.
     *                <p/>
     *                Since 'null' is normally a valid value for a 'self', this
     *                use of 'null' depends on our knowledge that 'null''s
     *                script does not have its own match clause.
     * @return
     */
    boolean respondsTo(Object optSelf, String verb, int arity);
}
