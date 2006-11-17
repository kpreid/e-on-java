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

import org.erights.e.elib.tables.ConstSubclassSet;

/**
 * Marker interface that makes objects passable-by-proxy via captp.
 * <p/>
 * When such objects are passed between vats, the recipients ends up with a Far
 * reference to the PassByProxy object.
 * <p/>
 * PassByProxy objects (actual or HONORARY) may not be Selfless (actual or
 * HONORARY). XXX Must find a way to enforce this.
 *
 * @author Mark S. Miller
 */
public interface PassByProxy extends Marker {

    /**
     * List of Java library classes that can be passed-by-proxy. This is
     * because, since they are JavaSoft's, we obviously can't go back and
     * modify them to implement the PassByProxy interface, but we equally
     * obviously want people to be able to use them as if we had. <p>
     */
    static final String[] HONORED_NAMES =
      {"java.io.File", "java.net.URL", "java.rmi.Remote",};

    /**
     * HONORARY (effectively) contains all the classes named in HONORED_NAMES
     * and all their subclasses.
     */
    static public final ConstSubclassSet HONORARY =
      ConstSubclassSet.make(HONORED_NAMES);
}
