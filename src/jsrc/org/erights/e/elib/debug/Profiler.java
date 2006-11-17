package org.erights.e.elib.debug;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.elib.base.SourceSpan;
import org.erights.e.elib.base.ValueThunk;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.FlexList;
import org.erights.e.elib.tables.FlexMap;

/**
 * Accumulates static profile info.
 * <p/>
 * XXX Security alert: mutable static state / covert channel.
 *
 * @author Mark S. Miller
 */
public class Profiler {

    /**
     *
     */
    static public final Profiler THE_ONE = new Profiler();

    /**
     * Maps from profKeys (Strings or Scripts) to CallCounters
     */
    private final FlexMap myCounters;

    /**
     *
     */
    private Profiler() {
        myCounters = FlexMap.fromTypes(Object.class, CallCounter.class);
    }

    /**
     * Note that multiple registrations of the same profKey will share the same
     * CallCounter -- this is on purpose.
     * <p/>
     * EMethodNode and EMatcher use this by providing their printstring as the
     * profKey, and pointing at the CallCounter. This means that multiple
     * EMethodNodes with the same printstring will increment the same counters,
     * and that the CallCounter object will not prevent these EMethodNodes from
     * being garbage collected. A dynamic number of EMethodNodes with a static
     * number of names will still only cause a static number of CallCounters to
     * be allocated.
     * <p/>
     * Should we start using multiple Java ClassLoaders, then we probably want
     * to change JavaMemberNode and OverloaderNode to use the same trick.
     */
    public synchronized CallCounter register(Object profKey,
                                             SourceSpan optSpan) {
        CallCounter optResult =
          (CallCounter)myCounters.fetch(profKey, ValueThunk.NULL_THUNK);
        if (null == optResult) {
            optResult = new CallCounter(profKey, optSpan);
            myCounters.put(profKey, optResult);
        }
        return optResult;
    }

    /**
     * XXX Security alert: covert channel?
     */
    public synchronized void clearCallCounts() {
        CallCounter[] counters =
          (CallCounter[])myCounters.getValues(CallCounter.class);
        for (int i = 0, len = counters.length; i < len; i++) {
            counters[i].clearCallCounts();
        }
    }

    /**
     * XXX Security alert: covert channel?
     * <p/>
     * XXX TODO: Add a query for interface coverage checking.
     */
    public synchronized ConstList getCallCounts(double percentile) {

        double total = 0.0;
        CallCounter[] counters =
          (CallCounter[])myCounters.getValues(CallCounter.class);
        for (int i = 0, len = counters.length; i < len; i++) {
            total += counters[i].getOkCount();
            total += counters[i].getBadCount();
        }
        double thresh = total * percentile / 100.0;
        FlexList result = FlexList.fromType(Object[].class);
        for (int i = 0, len = counters.length; i < len; i++) {
            counters[i].report(thresh, total, result);
        }
        return result.sort();
    }

    /**
     *
     */
    public void printTime(double percentile) {
        ConstList counts = getCallCounts(percentile);
        for (int i = 0, len = counts.size(); i < len; i++) {
            System.err.println(E.toString(counts.get(i)));
        }
    }
}
