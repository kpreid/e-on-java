package org.erights.e.elib.ref;

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

import org.erights.e.elib.prim.E;
import org.erights.e.elib.util.OneArgFunc;

/**
 * Sends a message to target containing a cheap 1-argument function as
 * argument. When this function is called it calls a method on the reactor with
 * its argument. It's like the following E code:
 * <pre>
 * <p/>
 *     def OneArgFuncAdapter adapt(target, whenVerb, reactor, onVerb) {
 *         def adapter(arg) {
 *             E sendOnly(reactor, onVerb, [arg]);
 *         }
 *         E sendOnly(target, whenVerb, [adapter])
 *     }
 * </pre>
 * Note that the on method (the method named by the onVerb) will be invoked
 * after an additional send.
 *
 * @author Mark S. Miller
 */
public class OneArgFuncAdapter implements OneArgFunc {

    private final Object myReactor;

    private final String myOnVerb;

    /**
     *
     */
    public OneArgFuncAdapter(Object reactor, String onVerb) {
        myReactor = reactor;
        myOnVerb = onVerb;
    }

    /**
     *
     */
    static public Throwable adapt(Object target,
                                  String whenVerb,
                                  Object reactor,
                                  String onVerb) {
        return E.sendOnly(target,
                          whenVerb,
                          new OneArgFuncAdapter(reactor, onVerb));
    }

    /**
     *
     */
    public Object run(Object arg) {
        return E.sendOnly(myReactor, myOnVerb, arg);
    }
}
