package org.erights.e.elib.slot;

// Copyright 2003 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.develop.assertion.T;
import org.erights.e.develop.exception.ExceptionMgr;
import org.erights.e.elib.base.ClassDesc;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.ConstMap;
import org.erights.e.elib.util.ArityMismatchException;
import org.erights.e.elib.util.OneArgFunc;

import java.io.IOException;

/**
 * In E, "Map[K,V]" evaluates to a guard which means, coerce to a ConstMap
 * in which the keys have been coerced by K and the values by V.
 * <p>
 * XXX TODO NON-UPWARDS-COMPATIBLE-CHANGE: The Map guard will mean, coerce
 * to a ConstMap (of keys and values...) such that two such ConstMaps of the
 * same keys and values in the same order are the same.

 * @author Mark S. Miller
 */
public class MapGuard implements Guard {

    static public final MapGuard THE_BASE = new MapGuard(null, null);

    private final ListGuard myOptKeyColumnGuard;
    private final ListGuard myOptValColumnGuard;

    /**
     *
     */
    private MapGuard(Guard optKeyGuard, Guard optValGuard) {
        myOptKeyColumnGuard =
          null == optKeyGuard ? null : ListGuard.THE_BASE.get(optKeyGuard);
        myOptValColumnGuard =
          null == optValGuard ? null : ListGuard.THE_BASE.get(optValGuard);
    }

    /**
     *
     */
    public MapGuard get(Guard keyGuard, Guard valGuard) {
        T.require(null == myOptKeyColumnGuard && null == myOptValColumnGuard,
                  "Already parameterized: ",
                  this);
        T.notNull(keyGuard, "Missing key guard parameter");
        T.notNull(valGuard, "Missing value guard parameter");
        return new MapGuard(keyGuard, valGuard);
    }

    /**
     * Matches a Map[k, v] guard made by this.
     */
    public Object match__get_2(Object specimen, OneArgFunc optEjector) {
        T.require(null == myOptKeyColumnGuard && null == myOptValColumnGuard,
                  "Already parameterized: ",
                  this);
        ClassDesc kind = ClassDesc.make(MapGuard.class);
        MapGuard ofKind = (MapGuard)kind.coerce(specimen, optEjector);
        Object[] result = {ofKind.getKeyGuard(), ofKind.getValGuard()};
        return ConstList.fromArray(result);
    }

    /**
     *
     */
    public Object coerce(Object specimen, OneArgFunc optEjector) {
        ClassDesc ConstMapGuard = ClassDesc.make(ConstMap.class);
        ConstMap map = (ConstMap)ConstMapGuard.coerce(specimen, optEjector);
        if (null == myOptKeyColumnGuard && null == myOptValColumnGuard) {
            return map;
        }
        //noinspection ConstantConditions
        ConstList keys =
          (ConstList)myOptKeyColumnGuard.coerce(map.getKeys(), optEjector);
        ConstList vals =
          (ConstList)myOptValColumnGuard.coerce(map.getValues(), optEjector);
        try {
            return ConstMap.fromColumns(keys, vals);
        } catch (ArityMismatchException e) {
            throw ExceptionMgr.asSafe(e);
        }
    }

    /**
     *
     */
    public Guard getKeyGuard() {
        if (null == myOptKeyColumnGuard) {
            return AnyGuard.THE_ONE;
        } else {
            return myOptKeyColumnGuard.getElemGuard();
        }
    }

    /**
     *
     */
    public Guard getValGuard() {
        if (null == myOptValColumnGuard) {
            return AnyGuard.THE_ONE;
        } else {
            return myOptValColumnGuard.getElemGuard();
        }
    }

    /**
     * Prints "Map" or "Map[<i>key-guard</i>, <i>value-guard</i>]"
     */
    public void __printOn(TextWriter out) throws IOException {
        out.print("Map");
        if (null != myOptKeyColumnGuard || null != myOptValColumnGuard) {
            out.print("[", getKeyGuard(), ", ", getValGuard(), "]");
        }
    }
}
