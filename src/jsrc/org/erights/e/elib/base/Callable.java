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

import org.erights.e.elib.oldeio.EPrintable;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.sealing.Amplifiable;
import org.erights.e.elib.slot.Conformable;

import java.io.IOException;

/**
 * Objects that handle E messages themselves.
 *
 * @author Mark S. Miller
 */
public interface Callable extends Amplifiable, Conformable, EPrintable {

    /**
     * Returns null or a Script that can be execute()d, having the same effect
     * as a callAll() on this object.
     * <p/>
     * If the Script isn't "shorter", ie, if it doesn't represent a shortened
     * implementation path to the actual execution, then optShorten should
     * return null instead, in order to avoid an infinite regress with
     * CallableScript. In addition, it is always correct for optShorten() to
     * return null.
     */
    Script optShorten(String verb, int arity);

    /**
     * must ensure this call gets backtrace'd
     */
    Object callAll(String verb, Object[] args);

    /**
     * Returns a description of the type the object alleges to implement.
     * Should probably be moved to the __optSealedDispatch.
     */
    TypeDesc getAllegedType();

    /**
     * Does the object respond to verb/arity?
     */
    boolean respondsTo(String verb, int arity);

    /**
     * When {@link org.erights.e.elib.prim.MirandaMethods#__printOn
     * MirandaMethods.__printOn} gets a Callable, it calls back to here so that
     * the Callable can provide a Miranda per-Callable-implementation.
     */
    void mirandaPrintOn(TextWriter out) throws IOException;
}
