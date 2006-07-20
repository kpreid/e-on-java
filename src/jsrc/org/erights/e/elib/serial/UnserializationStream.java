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

import org.erights.e.elib.ref.Ref;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

/**
 * Made usable from E by parameterization rather than subclassing.
 * <p/>
 * This stream should be used at least for persistence and CapTP. It is
 * specialized by composition rather than subclassing -- by providing a
 * {@link Reviver} rather than overriding the .resolveObject(..)
 * method, as a step towards enabling E programs (which can't subclass Java
 * classes) to specialize serialization behavior.
 *
 * @author Mark S. Miller
 */
public final class UnserializationStream extends ObjectInputStream {

    private final Reviver myReviver;

    /**
     * Makes an UnserializationStream specialized by 'reviver'.
     * <p/>
     * Makes an ObjectInputStream on inp with the following differences: <ul>
     * <li>When you do unserializer.readObject(), rather than the encoded
     * object, let's say foo, being returned, reviver(foo) is returned,
     * and likewise for every object reachable from foo.
     * </ul>
     * reviver(..) is used as the overriding of .resolveObject(..). See the
     * Java Serialization spec for the detailed implications of this.
     */
    UnserializationStream(InputStream inp, Reviver reviver)
      throws IOException {
        super(inp);
        enableResolveObject(true);
        myReviver = reviver;
    }

    /**
     * returns the {@link Ref#resolution(Object)} of reviver(ref)
     */
    protected Object resolveObject(Object ref) {
        return Ref.resolution(myReviver.substitute(ref));
    }
}
