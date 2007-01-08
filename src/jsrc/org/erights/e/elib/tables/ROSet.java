package org.erights.e.elib.tables;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.serial.PassByProxy;

import java.io.IOException;

/**
 * @author Mark S. Miller
 */
class ROSet extends ESet implements PassByProxy {

    static private final long serialVersionUID = 7670561978532433369L;

    /**
     *
     */
    private ROSet(EMap map) {
        super(map);
    }

    /**
     *
     */
    static public ROSet make(EMap map) {
        return new ROSet(map);
    }

    /**
     *
     */
    public ESet readOnly() {
        return this;
    }

//    /**
//     * Divulges itself only to a holder of
//     * {@link org.erights.e.elib.serial.PersistentKeyHolder#THE_UNSEALER}.
//     * <p>
//     * XXX Should provide an optional creation-time parameter of a Sealer to
//     * use in addition (or instead?) as a secret divulging channel.
//     *
//     * @param brand
//     * @return
//     */
//    public SealedBox __optSealedDispatch(Brand brand) {
//        if (PersistentKeyHolder.THE_BRAND == brand) {
//            Object[] uncall = {
//                FlexSet.make(myMap), "readOnly()", E.NO_ARGS
//            };
//            return PersistentKeyHolder.THE_SEALER.seal(uncall);
//        } else {
//            return null;
//        }
//    }

    /**
     * Prints as an E list sent the messages '.asSet().readOnly()'
     */
    public void __printOn(TextWriter out) throws IOException {
        printOn("[", ", ", "].asSet().readOnly()", out);
    }
}
