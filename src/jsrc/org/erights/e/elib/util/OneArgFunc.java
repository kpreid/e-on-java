package org.erights.e.elib.util;

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
 * An interface for anonymous inner classes to implement so they can act as a
 * one-argument inline closure. <p>
 * <p/>
 * It is non-sufficient for them to simply
 * declare their own public run() method, as CRAPI only considers a method
 * public if it's introduced by a public class/interface. Anonymous inner
 * classes are not public by default. (Can they be declared to be?  If so,
 * this interface can be removed.)
 *
 * @author Mark S. Miller
 */
public interface OneArgFunc {

    /**
     * An object is invoked with one of several selectors, each associated
     * with a sequence of arguments. <p>
     * <p/>
     * Functions or procedures (from math and
     * early computer languages) are invoked with no selector and a single
     * sequence of arguments. For objects to act like functions, we need a
     * convention for a default selector name that means "no interesting
     * selector here". Smalltalk uses "value" and "value:". Joule uses
     * "::". Java has no general convention, but java.lang.Runnable is
     * essentially a no-argument, void-return procedure, using the selector
     * "run", so in our interest in being standard, we respect this
     * precedent.
     */
    Object run(Object arg);
}
