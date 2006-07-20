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

import java.io.Serializable;

/**
 * Marker interface that enables object to be saved and restored to a
 * checkpoint for purposes of persistence.
 * <p/>
 * This interface helps to determine whether an object {@link
 * org.erights.e.elib.ref.Ref#isPersistent(Object)}.
 *
 * @author Mark S. Miller
 */
public interface Persistent extends Serializable, Marker {

    static final long serialVersionUID = -1917353432884312686L;

    /**
     * List of Java library classes to be considered persistent.
     * <p/>
     * This is because, since they are JavaSoft's, we obviously can't go back
     * and modify them to implement the Persistent interface, but we equally
     * obviously want people to be able to use them as if we had.
     */
    static final String[] HONORED_NAMES = {
        //also PBC, Transparent, & Selfless, and so PassByCopy
        "java.lang.Boolean",
        "java.lang.Character",
        "java.lang.String",

        //also PBC, Transparent, & Selfless, and so PassByCopy
        "java.lang.Number",

        //also PBC & Selfless, but not Transparent or PassByCopy
        "net.captp.jcomm.SturdyRef",

        //We would like Throwable to be transitively PassByCopy, but it
        //doesn't provide its own equals() and hashCode(), so it can't be
        //made even honorarily Selfless, it's not really transparent because
        //of the stack trace, and we currently have no way to enforce the
        //transitive adherence to any rules by Throwable subclasses.
        "java.lang.Throwable",
        "java.lang.StackTraceElement",

        //We'd like these to be (at least honorarily) Selfless, and therefore
        //PassByCopy, but they don't implement their own equals() and
        //hashCode(), and there doesn't seem to be any generic way to get
        //their contents and restore them from their contents. However,
        //KeySpec looks like a hopeful solution someday.
        //News: progress has been made towards using KeySpecs. See the
        //org.erights.e.meta.java.security.**Sugar classes.
        "java.security.Key",
        "java.security.KeyPair",
    };

    /**
     * HONORARY (effectively) contains all the classes named in HONORED_NAMES
     * and all their subclasses.
     */
    static public final ConstSubclassSet HONORARY
      = ConstSubclassSet.make(HONORED_NAMES);
}
