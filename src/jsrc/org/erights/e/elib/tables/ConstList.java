package org.erights.e.elib.tables;

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

import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.prim.MirandaMethods;
import org.erights.e.elib.prim.StaticMaker;
import org.erights.e.elib.serial.PassByConstruction;

import java.io.IOException;
import java.lang.reflect.Array;

/**
 * A ConstList is an immutable list that simplifies to an array or String,
 * which is Selfless and pass by construction.
 * <p/>
 * A ConstList can be compared for partial ordering with other ConstLists.
 * <p/>
 * As a special case, a ConstList all of whose elements are DeepPassByCopy
 * is itself considered to be DeepPassByCopy by
 * {@link org.erights.e.elib.ref.Ref#isDeepPassByCopy Ref.isDeepPassByCopy/1}.
 *
 * @author Mark S. Miller
 */
public abstract class ConstList
  extends EList implements PassByConstruction, Selfless {

    static private final long serialVersionUID = -4704670888158933364L;

    /**
     * Initialized lazily to avoid circular initialization.
     */
    static private StaticMaker ConstListMaker = null;

    /**
     *
     */
    static public StaticMaker GetMaker() {
        if (null == ConstListMaker) {
            ConstListMaker = StaticMaker.make(ConstList.class);
        }
        return ConstListMaker;
    }

    /**
     *
     */
    static private final Object[] EmptyArray = {};

    /**
     * The canonical empty ConstList
     */
    static public final ConstList EmptyList = new ConstListImpl(EmptyArray);


    /**
     * Only subclasses within the package
     */
    ConstList() {
    }

    /**
     *
     */
    static public ConstList fromArray(Object oldArray) {
        int len = Array.getLength(oldArray);
        return new ConstListImpl(ArrayHelper.resize(oldArray, len));
    }

    /**
     *
     */
    static public ConstList fromArray(Object oldArray, int start, int bound) {
        return new ConstListImpl(ArrayHelper.slice(oldArray, start, bound));
    }

    /**
     * This defines the matcher of the ConstListMaker -- the StaticMaker on the
     * class ConstList -- to respond to a "run" method of any arity by
     * returning the list of arguments as a list.
     * <p/>
     * If this were a method of an instance, we'd declare the instance to
     * implement {@link org.erights.e.elib.prim.JMatcher}.
     *
     * @return
     * @throws NoSuchMethodException if the verb isn't "run"
     */
    static public Object match(String verb, ConstList args)
      throws NoSuchMethodException {
        if ("run".equals(verb)) {
            return args;
        }
        if ("__respondsTo".equals(verb) && args.size() == 2) {
            //XXX should say yes if args[0] =~ `run`
            return Boolean.FALSE;
        }
        if ("__getAllegedType".equals(verb) && args.size() == 0) {
            //XXX kludge
            return E.call(null, "__getAllegedType");
        }
        throw new NoSuchMethodException(verb + "/" + args.size());
    }

    /**
     * Just returns itself
     */
    public ConstList snapshot() {
        return this;
    }

    /**
     * Just returns itself
     */
    public EList readOnly() {
        return this;
    }

    /**
     * Ordered by lexicographic ordering of the elements. In other words,
     * the ordering is the same as the ordering of the first unequal
     * comparison, in left-to-right order. If all compare equal but one list
     * is shorter, the shorter list is less than the longer list.
     */
    public double op__cmp(ConstList other) {
        return op__cmp(other, SimpleCompFunc.THE_ONE);
    }

    /**
     * Like one-arg op__cmp, but uses the provided compFunc to compare the
     * elements.
     */
    public double op__cmp(ConstList other, CompFunc compFunc) {
        int len1 = size();
        int len2 = other.size();
        int len = StrictMath.min(len1, len2);
        for (int i = 0; i < len; i++) {
            double result = compFunc.run(get(i), other.get(i));
            if (0.0 != result) {
                return result;
            }
        }
        return (double)(len1 - len2);
    }

    /**
     * Ensure the public overrides in non-public subclasses are actually
     * public.
     * <p/>
     * The default implementation here just delegates to
     * {@link MirandaMethods#__optUncall}.
     */
    public Object[] __optUncall() {
        return MirandaMethods.__optUncall(this);
    }

    /**
     * Prints using E list syntax
     */
    public void __printOn(TextWriter out) throws IOException {
        printOn("[", ", ", "]", out);
    }
}
