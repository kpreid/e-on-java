// Copyright 2006 Hewlett Packard, under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.meta.org.quasiliteral.astro;

import org.erights.e.develop.assertion.T;
import org.erights.e.elib.base.ClassDesc;
import org.erights.e.elib.tables.AssocFunc;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.ConstMap;
import org.erights.e.elib.tables.Twine;
import org.erights.e.elib.util.OneArgFunc;
import org.quasiliteral.astro.Astro;
import org.quasiliteral.astro.AstroArg;
import org.quasiliteral.astro.AstroBuilder;
import org.quasiliteral.term.TermBuilder;

/**
 * Refines the 'coerce/2' behavior of Astro types, so that they may be useful
 * as Guards.
 *
 * @author Mark S. Miller
 */
public class AstroGuardSugar extends ClassDesc {

    static private final long serialVersionUID = -9057701628280810835L;

    private final AstroBuilder myBuilder;

    /**
     * @param clazz must be an Astro.class or a subclass.
     */
    public AstroGuardSugar(Class clazz) {
        this(clazz, TermBuilder.FOR_TERMS);
    }

    private AstroGuardSugar(Class clazz, AstroBuilder builder) {
        super(clazz);
        myBuilder = builder;
        T.require(Astro.class.isAssignableFrom(clazz),
                  clazz,
                  " must be a kind of Astro");
    }

    /**
     * So that one can say, for example, <tt>:Term[altBuilder]</tt>.
     *
     * @noinspection MethodOverloadsMethodOfSuperclass
     */
    public AstroGuardSugar get(AstroBuilder builder) {
        return new AstroGuardSugar(asClass(), builder);
    }

    private Object leafTag(String tagName) {
        return myBuilder.namedTerm(tagName, myBuilder.empty());
    }

    /**
     * Converts null, and otherwise delegates to super.
     */
    protected Object tryCoerceR(Object shortSpecimen, OneArgFunc optEjector) {
        if (null == shortSpecimen) {
            return leafTag("null");
        }
        return super.tryCoerceR(shortSpecimen,
                                optEjector); //make compiler happy
    }

    /**
     * Converts ints, chars, booleans, strings, {@link Twine} to a
     * corresponding Term.
     */
    protected Object subCoerceR(Object shortSpecimen,
                                final OneArgFunc optEjector) {
        if (shortSpecimen instanceof Boolean) {
            if (((Boolean)shortSpecimen).booleanValue()) {
                return leafTag("true");
            } else {
                return leafTag("false");
            }
        } else if (shortSpecimen instanceof Number) {
            return myBuilder.leafData(shortSpecimen, null);
        } else if (shortSpecimen instanceof Character) {
            return myBuilder.leafChar(((Character)shortSpecimen).charValue(),
                                      null);
        } else if (shortSpecimen instanceof String) {
            return myBuilder.leafString((String)shortSpecimen, null);
        } else if (shortSpecimen instanceof Twine) {
            Twine twine = (Twine)shortSpecimen;
            // XXX Should we be passing the getOptSpan() here, or null?
//            return myBuilder.leafTwine(twine, twine.getOptSpan());
            return myBuilder.leafTwine(twine, null);
        } else if (shortSpecimen instanceof ConstList) {
            // We already know that it isn't a Twine
            ConstList args = (ConstList)shortSpecimen;
            final AstroArg[] argSeqCell = {myBuilder.empty()};
            args.iterate(new AssocFunc() {
                public void run(Object key, Object value) {
                    Astro arg = (Astro)coerce(value, optEjector);
                    argSeqCell[0] = myBuilder.seq(argSeqCell[0], arg);
                }
            });
            return myBuilder.tuple(argSeqCell[0]);
        } else if (shortSpecimen instanceof ConstMap) {
            ConstMap map = (ConstMap)shortSpecimen;
            final AstroArg[] argSeqCell = {myBuilder.empty()};
            map.iterate(new AssocFunc() {
                public void run(Object key, Object value) {
                    Astro k = (Astro)coerce(key, optEjector);
                    Astro v = (Astro)coerce(value, optEjector);
                    Astro arg = myBuilder.attr(k, v);
                    argSeqCell[0] = myBuilder.seq(argSeqCell[0], arg);
                }
            });
            return myBuilder.bag(argSeqCell[0]);
        }
        return super.subCoerceR(shortSpecimen,
                                optEjector); //make compiler happy
    }
}
