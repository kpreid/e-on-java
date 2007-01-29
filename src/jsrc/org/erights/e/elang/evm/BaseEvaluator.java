// Copyright 2003 Hewlett Packard, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.elang.evm;

import org.erights.e.develop.assertion.T;
import org.erights.e.elib.tables.ConstMap;

/**
 * @author Mark S. Miller
 */
public class BaseEvaluator implements Evaluator {

    static public final Evaluator THE_ONE = new BaseEvaluator();

    /**
     *
     */
    private BaseEvaluator() {
    }

    /**
     *
     */
    public Object eval(EExpr eExpr,
                       ConstMap bindingsIn,
                       boolean forValue,
                       ConstMap bindingsOut) throws Throwable {
        T.fail("XXX not yet implemented");
        return null; // make compiler happy
    }

    /**
     *
     */
    public Object[] evalToSingleton(EExpr eExpr,
                                    ConstMap bindingsIn,
                                    boolean forValue,
                                    ConstMap bindingsOut) throws Throwable {
        T.fail("XXX not yet implemented");
        return null; // make compiler happy
    }

    /**
     *
     */
    public boolean matchBind(Pattern pattern,
                             ConstMap bindingsIn,
                             boolean forTest,
                             ConstMap bindingsOut,
                             Object specimen) throws Throwable {
        T.fail("XXX not yet implemented");
        return false; // make compiler happy
    }
}
