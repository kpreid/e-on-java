package org.erights.e.elib.slot;

// Copyright 2003 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.elib.base.ClassDesc;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.prim.StaticMaker;
import org.erights.e.elib.prim.Thrower;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.util.OneArgFunc;

import java.io.IOException;

/**
 * @author Mark S. Miller
 */
public class TupleGuard implements Guard {

    static public final StaticMaker TupleGuardMaker =
      StaticMaker.make(TupleGuard.class);

    private final Guard[] myElemGuards;

    /**
     * This defines the matcher of the TupleGuardMaker -- the StaticMaker on
     * the class TupleGuard -- to respond to a "get" method of any arity by
     * returning a TupleGuard on those element guard arguments.
     * <p/>
     * If this were a method of an instance, we'd declare the instance to
     * implement {@link org.erights.e.elib.prim.JMatcher}.
     *
     * @return
     * @throws NoSuchMethodException if the verb isn't "get"
     */
    static public Object match(String verb, ConstList args)
      throws NoSuchMethodException {
        if ("get".equals(verb)) {
            return new TupleGuard((Guard[])E.as(args, Guard[].class));
        }
        if ("__respondsTo".equals(verb) && args.size() == 2) {
            //XXX should say yes if args[0] =~ `get`
            return Boolean.FALSE;
        }
        if ("__getAllegedType".equals(verb) && args.size() == 0) {
            //XXX kludge
            return E.call(null, "__getAllegedType");
        }
        throw new NoSuchMethodException(verb + "/" + args.size());
    }

    /**
     *
     */
    private TupleGuard(Guard[] elemGuards) {
        myElemGuards = elemGuards;
    }

    /**
     *
     */
    public Object coerce(Object specimen, OneArgFunc optEjector) {
        ClassDesc ConstListGuard = ClassDesc.make(ConstList.class);
        ConstList list =
          (ConstList)ConstListGuard.coerce(specimen, optEjector);
        int len = myElemGuards.length;
        if (len != list.size()) {
            RuntimeException problem = new RuntimeException(
              "Need " + len + " element list: " + specimen);
            Thrower.THE_ONE.eject(optEjector, problem);
        }
        Object[] result = new Object[len];
        for (int i = 0; i < len; i++) {
            result[i] = myElemGuards[i].coerce(list.get(i), optEjector);
        }
        return ConstList.fromArray(result);
    }

    /**
     * Prints "Tuple[<i>elem-guard</i>,...]"
     */
    public void __printOn(TextWriter out) throws IOException {
        if (0 == myElemGuards.length) {
            out.print("Tuple[]");
        } else {
            out.print("Tuple[", myElemGuards[0]);
            for (int i = 1, len = myElemGuards.length; i < len; i++) {
                out.print(", ", myElemGuards[i]);
            }
            out.print("]");
        }
    }
}
