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
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * Specialized by parameterization rather than subclassing.
 * <p/>
 * This stream should be used at least for persistence and CapTP. It is
 * specialized by composition rather than subclassing -- by providing a {@link
 * Replacer} rather than overriding the .replaceObject(..) method, as a step
 * towards enabling E programs (which can't subclass Java classes) to
 * specialize serialization behavior.
 * <p/>
 * Note that reset() is considered legitimate usage of a SerializationStream.
 *
 * @author Mark S. Miller
 */
public final class SerializationStream extends ObjectOutputStream {

    private final Replacer myReplacer;

    /**
     * Makes SerializationStream specialized by 'replacer'.
     * <p/>
     * Makes an ObjectOutputStream on out with the following differences: <ul>
     * <li>When you do serializer.writeObject(foo), rather than foo being
     * written, replacer(foo) is written, and likewise for every object
     * reachable from the replacement objects. </ul> replacer(..) is used as
     * the overriding of .replaceObject(..). See the Java Serialization spec
     * for the detailed implications of this.
     */
    SerializationStream(OutputStream out, Replacer replacer)
      throws IOException {
        super(out);
        enableReplaceObject(true);
        myReplacer = replacer;
    }

    /**
     * returns the {@link Ref#resolution(Object)} of replacer(ref)
     */
    protected Object replaceObject(Object ref) {
        return Ref.resolution(myReplacer.substitute(ref));
    }
}
