// Copyright 2003 Hewlett Packard, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.meta.java.math;

import org.erights.e.elib.base.ClassDesc;

/**
 * @author Mark S. Miller
 */
public abstract class BaseEIntGuardSugar extends ClassDesc {

    /**
     * @param clazz must be BigInteger.class
     */
    protected BaseEIntGuardSugar(Class clazz) {
        super(clazz);
    }
}
