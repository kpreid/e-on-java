package org.erights.e.elib.debug;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.develop.exception.NestedException;
import org.erights.e.develop.exception.ThrowableSugar;
import org.erights.e.elib.base.Ejection;
import org.erights.e.elib.base.SourceSpan;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.tables.FlexList;

import java.text.NumberFormat;

/**
 * @author Mark S. Miller
 */
public class CallCounter {

    static private final NumberFormat NF = NumberFormat.getPercentInstance();

    static {
        NF.setMinimumFractionDigits(1);
        //NF.setMinimumIntegerDigits(2);
    }

    /**
     *
     */
    private final Object myProfKey;

    private final SourceSpan myOptSpan;

    /**
     *
     */
    private long myOkCount;

    /**
     *
     */
    private long myBadCount;

    /**
     *
     */
    CallCounter(Object profKey, SourceSpan optSpan) {
        myProfKey = profKey;
        myOptSpan = optSpan;
        myOkCount = 0;
        myBadCount = 0;
    }

    /**
     *
     */
    long getOkCount() {
        return myOkCount;
    }

    /**
     *
     */
    long getBadCount() {
        return myBadCount;
    }

    /**
     *
     */
    public void bumpOkCount() {
        myOkCount++;
    }

    /**
     * Besides bumping myBadCount, this also return problem annotated with
     * myProfKey for use from {@link ThrowableSugar#eStack}.
     * <p/>
     * If the problem's {@link ThrowableSugar#leaf leaf} is an
     * {@link Ejection}, then we instead bump myOkCount and return the
     * Ejection, since we really have a case of successful (even if abrupt)
     * completion.
     */
    public RuntimeException bumpBadCount(Throwable problem) {
        Throwable leaf = ThrowableSugar.leaf(problem);
        if (leaf instanceof Ejection) {
            myOkCount++;
            return (Ejection)leaf;
        } else {
            myBadCount++;
            String msg = "- " + myProfKey;
            if (null != myOptSpan) {
                msg += ": " + myOptSpan;
            }
            return new NestedException(problem, msg);
        }
    }

    /**
     * Annotates problem with further backtrace info.
     */
    public RuntimeException bumpBadCount(Throwable problem,
                                         Object optSelf,
                                         String verb,
                                         Object[] args) {
        problem = bumpBadCount(problem);
        return E.backtrace(problem, optSelf, verb, args);
    }

    /**
     *
     */
    void clearCallCounts() {
        myOkCount = 0;
        myBadCount = 0;
    }

    /**
     *
     */
    void report(double thresh, double total, FlexList result) {
        if (myOkCount >= thresh) {
            Object[] entry = {new Long(myOkCount),
                              NF.format(myOkCount / total),
                              "ok " + myProfKey};
            result.push(entry);
        }
        if (myBadCount >= thresh) {
            Object[] entry = {new Long(myBadCount),
                              NF.format(myBadCount / total),
                              "bad " + myProfKey};
            result.push(entry);
        }
    }
}
