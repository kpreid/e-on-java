// Copyright 2005 Mark S. Miller, under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.elib.tables;

import org.erights.e.develop.exception.ExceptionMgr;
import org.erights.e.elib.serial.DeepFrozenAuditor;
import org.erights.e.elib.serial.DeepPassByCopyAuditor;
import org.erights.e.elib.util.OneArgFunc;
import org.erights.e.meta.java.lang.DoubleSugar;

/**
 * Given a suppossedly deterministic {@link OneArgFunc} which only accepts
 * DeepFrozen args and only returns DeepPassByCopy results, return a memoizing
 * wrapper for that function which should be semantically identical (except
 * that its not {@link Equalizer#isSameEver(Object,Object) the same}).
 * <p/>
 * Until we use auditors to verify the above constraints, we make Memoizer be
 * unsafe; and rely on its clients to use it only in ways that match the above
 * spec.
 * <p/>
 * Because some individual Memoizers are used globally to a jvm, we make this
 * synchronized, in order to be conventionally thread safe.
 *
 * @author Mark S. Miller
 */
public class Memoizer implements OneArgFunc {

    static private final int POSITIVE_MASK = 0x7FFFFFFF;

    static private int myIdentHits = 0;
    static private int myHits = 0;
    static private int myMisses = 0;

    private final OneArgFunc myFunc;
//    private final Object[] myIdentKeys;
    //    private final Object[] myIdentValues;
    private final Object[] myKeys;
    private final Object[] myValues;

    private final Object myLock = new Object();

    /**
     * @param func
     * @param budget
     */
    public Memoizer(OneArgFunc func, int budget) {
        myFunc = (OneArgFunc)DeepFrozenAuditor.THE_ONE.coerce(func, null);
//        myIdentKeys = new Object[budget];
//        myIdentValues = new Object[budget];
        myKeys = new Object[budget];
        myValues = new Object[budget];
    }

    /**
     * @param arg
     * @return
     */
    public Object run(Object arg) {
        synchronized (myLock) {
            Object key = DeepFrozenAuditor.THE_ONE.coerce(arg, null);

            // After ensuring that arg coerces, do the identity lookup with
            // arg rather than key.
//            int identHash = System.identityHashCode(arg) & POSITIVE_MASK;
//            int iIndex = identHash % myIdentKeys.length;
//            if (myIdentKeys[iIndex] == arg) {
//                myIdentHits++;
//                return myIdentValues[iIndex];
//            }
            int hash = Equalizer.samenessHash(key) & POSITIVE_MASK;
            int index = hash % myKeys.length;
            try {
                if (Equalizer.isSameEver(myKeys[index], key)) {
                    myHits++;
                    return myValues[index];
                }
            } catch (NotSettledException e) {
                throw ExceptionMgr.asSafe(e);
            }
            myMisses++;
            DeepPassByCopyAuditor Data = DeepPassByCopyAuditor.THE_ONE;
            Object result = Data.coerce(myFunc.run(key), null);
//            myIdentKeys[iIndex] = arg;
//            myIdentValues[iIndex] = result;
            myKeys[index] = key;
            myValues[index] = result;
            return result;
        }
    }

    static public void printCacheStats() {
        double total = myIdentHits + myHits + myMisses;
        double percent = (((myIdentHits + myHits) * 100.0) / total);
        System.err
          .println("Memoizer idHits: " + myIdentHits + " hits: " + myHits +
            " misses: " + myMisses + " (" + DoubleSugar.round(percent) + "%)");
    }
}
