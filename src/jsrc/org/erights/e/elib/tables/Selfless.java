package org.erights.e.elib.tables;

import org.erights.e.elib.ref.Ref;
import org.erights.e.elib.serial.Marker;

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
 * Selfless objects have no eq-identity -- only value-based identity. There are
 * two varieties of Selfless objects: 1) Transparent Selfless objects are those
 * implement the interface 'Selfless'. 2) Honorary Selfless object are
 * instances of the classes listed in the list of honorary classes below (and
 * therefore instances of any subclasses of those classes). In addition, null
 * and all arrays are honorary Selfless objects. <p>
 * <p/>
 * Transparent Selfless objects are tested and hashed for sameness based only
 * on their getSpreadUncall(). Two transparent Selfless object are the same
 * exactly when their getSpreadUncall()s are the same. Two honorary Selfless
 * objects are tested and hashed for sameness based on their Java .equals() and
 * .hashCode() methods, which is trusted to be commutative. By contrast,
 * selfish objects are tested and hashed for sameness using Java's EQ (Java's
 * "==") and System.identityHashCode(). See Equalizer.same() for the
 * authoritative sameness semantics. <p>
 * <p/>
 * Selfless object tend to be PassByConstruction, but this is not mandated. For
 * example, Equalizer instances are Selfless but not PassByConstruction.
 * <p/>
 * <p/>
 * Arrays and null are also effectively Selfless objects, even though they
 * can't be listed by honorary class below. Honorary selfless objects tend to
 * be transparent, but SturdyRef and Equalizer are not. <p>
 * <p/>
 * PassByProxy objects (actual or HONORARY) may not be Selfless (actual or
 * HONORARY). XXX Must find a way to enforce this.
 *
 * @author Mark S. Miller
 * @see Ref#isSameEver
 * @see net.captp.jcomm.SturdyRef
 */
public interface Selfless extends Marker {

    /**
     * Two Selfless objects are the same iff their getSpreadUncall()s are the
     * same.
     * <p/>
     * To make this work, we need an adequately strong convention as to what
     * should be placed in this array. The convention is an element array
     * describing the canonical expression for recreating the object. The
     * elements are recipient, verb, and then the args. The described
     * invocation
     * <pre>    recipient.verb(args...)</pre>
     * should create an object identical to this one. This should avoid
     * collisions, since an object that isn't the same as this one could not
     * correctly choose the same expression. Since the TCB depends on the
     * correctness of getSpreadUncall(), all the implementors are considered in
     * the TCB. <p>
     */
    Object[] getSpreadUncall();

    /**
     * List of Java library classes that are compared using equals() and
     * hashCode().
     * <p/>
     * This is because, since they are JavaSoft's, we obviously can't go back
     * and modify them to implement the Selfless interface, but we also
     * obviously want people to be able to use them as if we had.
     */
    String[] HONORED_NAMES = {
      //also Transparent & JOSSPassByConstruction, and so PassByCopy
      "java.lang.Boolean",
      "java.lang.Character",
      "java.lang.String",
      "java.lang.Number",

      "java.security.Key",
      "java.security.KeyPair",
      "COM.rsa.jsafe.SunJSSE_l",

      //also Transparent & JOSSPassByConstruction, and so PassByCopy
      //made HONORARY for speed, and so it can be used as a FarRef identity.
      "net.captp.jcomm.ObjectID",

      //also JOSSPassByConstruction, but not Transparent or PassByCopy
      "net.captp.jcomm.SturdyRef",

      //Not really Selfless, but does its own sameness comparison.
      "org.erights.e.elib.tables.TraversalKey",
      "org.erights.e.elib.tables.WeakKey",
      "org.erights.e.elib.vat.BootRefIdentity",

      //Not Near, and therefore not actually Selfless, but does its own
      //sameness comparison.
      //also JOSSPassByConstruction, but not Transparent or PassByCopy
      "org.erights.e.elib.ref.DisconnectedRef",

      //Not Near, and therefore not actually Selfless, but does its own
      //sameness comparison.
      "org.erights.e.elib.ref.FarRef",
      "org.erights.e.elib.ref.FarRef2",};

    /**
     * HONORARY (effectively) contains all the classes named in HONORED_NAMES
     * and all their subclasses.
     */
    ConstSubclassSet HONORARY = ConstSubclassSet.make(HONORED_NAMES);
}
