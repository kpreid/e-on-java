package org.erights.e.meta.java.math;

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
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.util.OneArgFunc;

import java.io.IOException;
import java.math.BigInteger;

/**
 * @author Mark S. Miller
 */
public class BigIntegerGuardSugar extends BaseEIntGuardSugar {

    /**
     * @param clazz must be BigInteger.class
     */
    public BigIntegerGuardSugar(Class clazz) {
        super(clazz);
        T.require(BigInteger.class == clazz,
                  clazz,
                  " must be represent the BigInteger type");
    }

    /**
     *
     */
    protected Object subCoerceR(Object shortSpecimen, OneArgFunc optEjector) {
        if (shortSpecimen instanceof Number) {
            Number eInt = (Number)shortSpecimen;
            return EInt.big(eInt, optEjector);
        }
        throw doesntCoerceR(shortSpecimen, optEjector);
    }

    /**
     *
     */
    public void __printOn(TextWriter out) throws IOException {
        out.print("<type:java.math.BigInteger>");
    }
}
