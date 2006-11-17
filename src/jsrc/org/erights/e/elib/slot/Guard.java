package org.erights.e.elib.slot;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.elib.oldeio.EPrintable;
import org.erights.e.elib.util.OneArgFunc;

/**
 * A Guard will either coerce an input to a value that matches some condition,
 * or it will fail.
 * <p/>
 * A Guard is used in the E language to guard the definition of a variable or
 * the return value of a method. Implementors of Guard should always override
 * __printOn/1 to print a guard expression reflecting the guard's value.
 * <p/>
 * XXX Guard will also require DeepFrozen
 *
 * @author Mark S. Miller
 */
public interface Guard extends EPrintable /*, Marker */ {

    /**
     * If specimen coerces to a value that matches the condition represented by
     * this guard, return that value; other fail (according to optEjector) with
     * a problem explaining why not.
     * <p/>
     * If optEjector is null, then throw the problem. Otherwise, call
     * optEjector with the problem. optEjector should perform a non-local exit,
     * and so should not return. If optEjector returns anyway, then throw the
     * problem after all.
     * <p/>
     * A note to Java implementors of this method: You almost certainly want to
     * start your method with
     * <pre>    specimen = Ref.resultion(specimen);</pre>
     */
    Object coerce(Object specimen, OneArgFunc optEjector);
}
