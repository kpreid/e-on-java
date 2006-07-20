package org.erights.e.elib.serial;


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
 * XXX need to fix java-documentation.
 * <p/>
 * The following documentation is stale:
 * Provides access to the java fully-qualified class namespace.
 * <p/>
 * Must be implemented in a thread-safe fashion, as use of
 * {@link org.erights.e.elib.vat.Vat#seed Vat.seed/*} will normally cause
 * these to be accessed from multiple vats.
 * <p/>
 * If <tt>loader[name]</tt> yields <tt>obj</tt>, then
 * <pre>    loader.optUncall(obj)</pre>
 * should yield
 * <pre>    [loader, "get", [name]]</pre>
 * and vice versa.
 *
 * @author E. Dean Tribble
 * @author Mark S. Miller
 */
public interface Loader extends Uncaller {

    /**
     * How modules (and other things) get imported.
     * <p/>
     * If this loader is called 'foo__uriGetter', then '<foo:name>' will expand
     * to 'foo__uriGetter.get("name")'.
     */
    Object get(String name);

    /**
     * If child can be gotten from this Loader with a get, return the needed
     * argument string.
     *
     * @return
     */
    String optUnget(Object child);
}
