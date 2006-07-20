package org.erights.e.elib.serial;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.develop.assertion.T;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.ref.Ref;
import org.erights.e.elib.util.IdentityFunc;
import org.erights.e.elib.util.OneArgFunc;

/**
 * A SerializationStream as specialized by a Serializer should represent
 * no magic authority -- the serialized infomation it produces should only
 * include information obtainable by unprivileged code all written in E.
 * <p/>
 * Therefore, we declare the class Serializer to be safe.
 * <p/>
 * Serializer is specializable from E by providing replaceFunc(ref) so that,
 * on checkpointing for example, a live object may be replaced by a different
 * object to be checkpointed as its representative.
 * <p/>
 * The corresponding revived object is not simply this representative. It is
 * this representative as resolved by {@link Unserializer}.
 *
 * @author Mark S. Miller
 */
public final class Serializer extends Replacer {

    /**
     * The canonical instance
     */
    static public final Serializer THE_ONE =
      new Serializer(IdentityFunc.THE_ONE);

    private final OneArgFunc myReplaceFunc;

    /**
     *
     * @param replaceFunc
     */
    public Serializer(OneArgFunc replaceFunc) {
        myReplaceFunc = replaceFunc;
    }

    /**
     *
     */
    public Object substitute(Object ref) {
        ref = myReplaceFunc.run(ref);
        ref = Ref.resolution(ref);
        if (Ref.isSelfless(ref) && Ref.isPBC(ref)) {
            return ref;
        }
        if (Ref.isBroken(ref)) {
            return ref;
        }
        T.fail("Not simply serializable: " + E.toQuote(ref));
        return null; //make the compiler happy
    }
}
