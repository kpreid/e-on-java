// Copyright 2006 Hewlett Packard, under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.meta.org.quasiliteral.astro;

import org.erights.e.develop.assertion.T;
import org.erights.e.elib.base.ClassDesc;
import org.erights.e.elib.tables.Twine;
import org.erights.e.elib.util.OneArgFunc;
import org.quasiliteral.astro.Astro;
import org.quasiliteral.astro.AstroBuilder;
import org.quasiliteral.astro.AstroTag;
import org.quasiliteral.term.TermBuilder;

/**
 * Refines the 'coerce/2' behavior of Astro types, so that they may be useful
 * as Guards.
 *
 * @author Mark S. Miller
 */
public class AstroGuardSugar extends ClassDesc {

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
        AstroTag tag = myBuilder.getSchema().obtainTagForName(tagName);
        return myBuilder.leafTag(tag, null);
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
    protected Object subCoerceR(Object shortSpecimen, OneArgFunc optEjector) {
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
        }
        return super.subCoerceR(shortSpecimen,
                                optEjector); //make compiler happy
    }
}
