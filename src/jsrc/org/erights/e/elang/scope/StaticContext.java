// Copyright 2003 Hewlett Packard, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.elang.scope;

import org.erights.e.develop.assertion.T;
import org.erights.e.elang.evm.ObjectExpr;
import org.erights.e.elib.prim.StaticMaker;
import org.erights.e.elib.serial.DeepPassByCopy;
import org.erights.e.elib.tables.ConstMap;

/**
 * @author Mark S. Miller
 */
public class StaticContext implements DeepPassByCopy {

    static public final StaticMaker StaticContextMaker =
      StaticMaker.make(StaticContext.class);

    private final String myFQNPrefix;

    private final ConstMap mySynEnv;

    private final ObjectExpr myOptSource;

    /**
     * @param FQNPrefix
     * @param optSource
     * @param synEnv
     */
    public StaticContext(String FQNPrefix,
                         ConstMap synEnv,
                         ObjectExpr optSource) {
        myFQNPrefix = FQNPrefix;
        mySynEnv = synEnv;
        myOptSource = optSource;
    }

    /**
     * Uses 'StaticContextMaker(myFQNPrefix, mySynEnv, myOptSource)'
     */
    public Object[] getSpreadUncall() {
        Object[] result =
          {StaticContextMaker, "run", myFQNPrefix, mySynEnv, myOptSource};
        return result;
    }

    /**
     * @return
     */
    public ObjectExpr getOptSource() {
        return myOptSource;
    }

    /**
     * @return
     */
    public ObjectExpr getSource() {
        T.notNull(myOptSource, "No source");
        return myOptSource;
    }

    /**
     * @return
     */
    public String getFQNPrefix() {
        return myFQNPrefix;
    }

    /**
     *
     */
    public ConstMap getSynEnv() {
        return mySynEnv;
    }

    public String toString() {
        return "<static " + myFQNPrefix + ", " + mySynEnv.size() +
          (null == myOptSource ? "" : " s") + ">";
    }
}
