package org.erights.e.elib.serial;

import org.erights.e.elib.util.IdentityFunc;
import org.erights.e.elib.util.OneArgFunc;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............


/**
 * Used to specialize the UnserializationStream for reviving from state
 * serialized using a corresponding {@link Serializer}, where this reviving
 * uses no magic authority.
 * <p/>
 * Therefore, we declare the class Unserializer to be safe.
 * <p/>
 * Unserializer is specializable from E by providing a reviveFunc so
 * that, on reincarnating for example, a restored object may be revives as a
 * different object to be checkpointed as its representative.
 *
 * @author Mark S. Miller
 */
public class Unserializer extends Reviver {

    /**
     * The canonical instance
     */
    static public final Unserializer THE_ONE =
      new Unserializer(IdentityFunc.THE_ONE);

    private final OneArgFunc myReviveFunc;

    /**
     *
     * @param reviveFunc
     */
    public Unserializer(OneArgFunc reviveFunc) {
        myReviveFunc = reviveFunc;
    }

    /**
     * Currently, just returns reviveFunc(ref).
     * <p/>
     * XXX Bug: Must make sure that classes are safe and that (therefore?)
     * instances are instances of classes that grant no authority by virtue
     * of creation.
     */
    public Object substitute(Object ref) {
        return myReviveFunc.run(ref);
    }
}
