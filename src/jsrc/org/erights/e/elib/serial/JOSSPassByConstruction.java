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
 * Marker interface that makes objects passable-by-construction via CapTP.
 *
 * @author Mark S. Miller
 * @author Terry Stanley
 */
public interface JOSSPassByConstruction extends Serializable, Marker {

    long serialVersionUID = -1917353432884312686L;

    /**
     * List of Java library classes that can be passed by JOSS construction.
     * <p/>
     * This is because, since they are JavaSoft's, we obviously can't go back
     * and modify them to implement the JOSSPassByConstruction interface, but we
     * equally obviously want people to be able to use them as if we had.
     */
    String[] HONORED_NAMES = {
      //also Transparent & Selfless, and so PassByCopy
      "java.lang.Boolean",
      "java.lang.Character",
      "java.lang.String",
      "java.lang.Number",

      //We would like Throwable to be transitively PassByCopy, but it
      //doesn't provide its own equals() and hashCode(), so it can't be
      //made even honorarily Selfless, it's not really transparent because
      //of the stack trace, and we currently have no way to enforce the
      //transitive adherence to any rules by Throwable subclasses.
      "java.lang.Throwable",

      //since JDK1.4. See
      // bugs.sieve.net/bugs/?func=detailbug&bug_id=125434&group_id=16380
      "java.lang.StackTraceElement",

      "java.security.Key",
      "java.security.KeyPair",
      "COM.rsa.jsafe.SunJSSE_l"};

    /**
     * HONORARY (effectively) contains all the classes named in HONORED_NAMES
     * and all their subclasses.
     */
    ConstSubclassSet HONORARY = ConstSubclassSet.make(HONORED_NAMES);
    
}
