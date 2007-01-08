package net.captp.jcomm;

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

import org.erights.e.elib.serial.JOSSPassByConstruction;

/**
 * How non-PassByConstruction objects are passed, ie, how references to them
 * are passed.
 * <p/>
 * The sending side should ensure that only the captp comm system itself can
 * cause these to be sent, and that they are always sent in a valid state. The
 * receiving side should always revalidate during dereference (which happens
 * during unserialization).
 * <p/>
 * XXX is a "private void validate()" automatically called during
 * unserialization?  Where does it say this?
 *
 * @author Chip Morningstar
 * @author Mark S. Miller
 */
interface ObjectRefDesc extends JOSSPassByConstruction {

    long serialVersionUID = -1216565871698747757L;

    /**
     * Return an appropriate object (eg, a Proxy) matching this description.
     *
     * @param conn The connection over which we are communicating
     */
    Object dereference(CapTPConnection conn);
}
