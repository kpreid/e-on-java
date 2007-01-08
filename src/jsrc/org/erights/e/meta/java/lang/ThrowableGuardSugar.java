package org.erights.e.meta.java.lang;

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

import org.erights.e.develop.assertion.T;
import org.erights.e.develop.exception.ExceptionMgr;
import org.erights.e.develop.exception.ThrowableSugar;
import org.erights.e.elib.base.ClassDesc;
import org.erights.e.elib.base.Ejection;
import org.erights.e.elib.tables.Twine;
import org.erights.e.elib.util.OneArgFunc;
import org.erights.e.elib.util.TwineException;

/**
 * Coerces Strings, Twine, and Throwables to kinds of Throwbles.
 * <p/>
 * Strings and Twine coerce to RuntimeException or TwineException, and thereby
 * also to Exception and Throwable. Throwables coerce to RuntimeException.
 *
 * @author Mark S. Miller
 */
public class ThrowableGuardSugar extends ClassDesc {

    static private final long serialVersionUID = 6717054284440653769L;

    /**
     * @param clazz must be a type of Throwable
     */
    public ThrowableGuardSugar(Class clazz) {
        super(clazz);
        T.require(Throwable.class.isAssignableFrom(clazz),
                  clazz,
                  " must be a type of Throwable");
    }

    /**
     * Coerces the specimen to, in fact, a RuntimeException.
     * <p/>
     * This method can (and typically will) return a RuntimeException,
     * independent of myClass, so, if myClass isn't RuntimeException,
     * Exception, or Throwable, the result will often not represent a
     * successful coercion. We let 'coerce' sort this out.
     */
    protected Object subCoerceR(Object shortSpecimen, OneArgFunc optEjector) {
        if (shortSpecimen instanceof String) {
            return new RuntimeException((String)shortSpecimen);
        }
        if (shortSpecimen instanceof Twine) {
            return TwineException.make((Twine)shortSpecimen);
        }
        if (shortSpecimen instanceof Throwable) {
            Throwable leaf = ThrowableSugar.leaf((Throwable)shortSpecimen);
            if (leaf instanceof Ejection) {
                T.fail("Internal: An Ejection should not be reifiable: " +
                  shortSpecimen + ", leaf:" + leaf);
            }
            return ExceptionMgr.asSafe((Throwable)shortSpecimen);
        }
        return super.subCoerceR(shortSpecimen, optEjector);
    }
}
