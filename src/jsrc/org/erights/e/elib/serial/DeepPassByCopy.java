package org.erights.e.elib.serial;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.elib.tables.ConstSubclassSet;
import org.erights.e.elib.tables.Selfless;

/**
 * Marker interface that declares objects to be transitively PassByCopy, ie, to
 * be transitively {@link Selfless}, Transparent, and {@link
 * PassByConstruction}.
 * <p/>
 * The {@link org.erights.e.elib.vat.BootRefHandler boot-comm-system} will only
 * pass PassByConstruction objects that are DeepPassByCopy. It does so by
 * sharing the actual object between vats, so any private mutable state, such
 * as a semantics-free cache (e.g. {@link org.quasiliteral.term.Term#myHeight
 * Term.myHeight}) had better be mutated in a thread-safe fashion.
 *
 * @author Mark S. Miller
 */
public interface DeepPassByCopy extends PassByConstruction, Selfless {

    long serialVersionUID = 6883670304147415885L;

    /**
     * List of Java library classes whose instances are transitively
     * PassByCopy.
     * <p/>
     * This is because, since they are JavaSoft's, we obviously can't go back
     * and modify them to implement the DeepPassByCopy interface, but we
     * equally obviously want people to be able to use them as if we had.
     */
    String[] HONORED_NAMES = {"java.lang.Boolean",
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
      "java.lang.StackTraceElement"};

    /**
     * HONORARY (effectively) contains all the classes named in HONORED_NAMES
     * and all their subclasses.
     */
    ConstSubclassSet HONORARY = ConstSubclassSet.make(HONORED_NAMES);
}
