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

import org.erights.e.elib.base.ValueThunk;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.StaticMaker;
import org.erights.e.elib.serial.JOSSPassByConstruction;
import org.erights.e.elib.util.ArityMismatchException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Properties;

/**
 * A ConstMap is a finite single-valued map from keys to values. Equivalently,
 * it can be considered a finite set of pairs, where each is a pair of a key
 * and a value, and no two pairs have the same key. ConstMaps are the same
 * based on whether their contents are the same. <p>
 * <p/>
 * ConstMaps must be immutable, and so can be passed-by-copy. Together with
 * value-based equality, ConstMaps can be passed-by-copy over the network
 * transparently.
 *
 * @author Mark S. Miller
 */
public abstract class ConstMap extends EMap
  implements JOSSPassByConstruction, Selfless {

    static private final long serialVersionUID = -6424210992869387563L;

    /**
     *
     */
    static private StaticMaker ConstMapMaker = null;

    /**
     * The static variable cache is initialized lazily to avoid circular static
     * initialization dependency with StaticMaker.
     */
    static public StaticMaker GetMaker() {
        if (null == ConstMapMaker) {
            ConstMapMaker = StaticMaker.make(ConstMap.class);
        }
        return ConstMapMaker;
    }

    /**
     * The canonical empty ConstMap
     */
    static public final ConstMap EmptyMap =
      new ConstMapImpl(new FlexMapImpl());


    /**
     * Only subclasses within the package
     */

    ConstMap() {
    }

    /**
     * Uses 'ConstMapMaker.fromColumns(key, values)'
     */
    public Object[] getSpreadUncall() {
        Object[] result = {GetMaker(), "fromColumns", getKeys(), getValues()};
        return result;
    }

    /**
     * Returns itself
     */
    public ConstMap snapshot() {
        return this;
    }

    /**
     * Returns itself
     */
    public EMap readOnly() {
        return this;
    }

    /**
     * The domain of a ConstMap is always a {@link ConstSet}, but is declared
     * as an ESet because Java's type system doesn't allow covariant return
     * types.
     */
    public ESet domain() {
        return ConstSet.make(this);
    }

    /**
     * This method enables E's magnitude comparison operators (&lt;, &lt;=,
     * &lt;=&gt;, &gt;=, &gt;) to express subset-ness of the domains of
     * ConstMaps. <p>
     * <p/>
     * If this ConstMap's domain is a strict subset of other's, return -1.0. If
     * this ConstMap has the same domain as other, return 0.0. If this
     * ConstMap's domain is a strict superset of other's, return 1.0. Otherwise
     * return NaN.
     * <p/>
     * The canonical implementation of ConstMap (ConstMapImpl) recursively
     * turns the question around of this ConstMap's size() is smaller that
     * other's. Therefore, the base case is one in which this ConstMap's size()
     * is &gt;= other's. Therefore, alternate implementations of ConstMaps must
     * also treat this as the base case. XXX security implications?
     */
    public double op__cmp(ConstMap other) {
        int sz = size();
        int otherSz = other.size();
        if (sz < otherSz) {
            return -other.op__cmp(this);
        }
        //at this point, this ConstMap is at least as big as other, so this
        //ConstMap's domain is either a superset (strict or not) or
        //incomparable.
        ConstMap union = or(other);
        ConstMap isect = and(other);
        int unionSz = union.size();
        int isectSz = isect.size();
        if (unionSz == isectSz) {
            //domains must be the same
            if (sz != unionSz || otherSz != unionSz) {
                throw new Error("internal: domain size confusion");
            }
            return 0.0;
        } else if (isectSz == otherSz) {
            //this ConstMap's domain must be a superset of other's
            if (sz != unionSz || sz <= otherSz) {
                throw new Error("internal: domain size confusion");
            }
            return 1.0;
        } else {
            //the ConstMaps must be incomparable
            if (unionSz <= sz || isectSz >= otherSz) {
                throw new Error("internal: domain size confusion");
            }
            //noinspection divzero
            return 0.0 / 0.0;
        }
    }

    /**
     * Prints using E language notation
     */
    public void __printOn(TextWriter out) throws IOException {
        if (0 == size()) {
            out.print("[].asMap()");
        } else {
            printOn("[", " => ", ", ", "]", out);
        }
    }

    /**
     * Used in the expansion of E's map syntax
     */
    static public ConstMap fromPairs(Object[][] pairs) {
        return FlexMap.fromPairs(pairs, true).snapshot();
    }

    /**
     * Used by my getSpreadUncall
     */
    static public ConstMap fromColumns(ConstList keys, ConstList vals)
      throws ArityMismatchException {
        return FlexMap.fromColumns(keys, vals).snapshot();
    }

    /**
     * Iterate the iteratable, and accumulate the key =&gt; value associations
     * into a map.
     *
     * @param strict If two keys collide, then, if strict, an exception is
     *               thrown. If not strict, then the later association for that
     *               key wins.
     */
    static public ConstMap fromIteratable(EIteratable iteratable,
                                          final boolean strict) {
        final FlexMap map = FlexMap.make();
        iteratable.iterate(new AssocFunc() {
            public void run(Object key, Object value) {
                map.put(key, value, strict);
            }
        });
        return map.snapshot();
    }

    /**
     *
     */
    static public ConstMap fromProperties(Properties props) {
        FlexMap result =
          FlexMap.fromTypes(String.class, String.class, props.size());
        Enumeration iter = props.propertyNames();
        while (iter.hasMoreElements()) {
            String key = (String)iter.nextElement();
            String val = props.getProperty(key);
            result.put(key, val, true);
        }
        return result.snapshot();
    }

    /**
     *
     */
    static public ConstMap fromPropertiesString(String propsString)
      throws UnsupportedEncodingException, IOException {
        //this is the encoding assumed by Properties.load(..)
        byte[] bytes = propsString.getBytes("ISO-8859-1");
        ByteArrayInputStream bain = new ByteArrayInputStream(bytes);
        Properties props = new Properties();
        props.load(bain);
        return fromProperties(props);
    }

    /**
     * Test the setting of a boolean property.
     * <p/>
     * A boolean property is true if its (String) value is "true", and false if
     * it's absent, or if its string value is "false" or "allow".
     * <p/>
     * XXX ConstMap is a silly place for this method, but where should it go?
     * XXX Should the comparison be case insensitive?
     *
     * @param props    A ConstMap representing the Properties
     * @param propName The name of the property to test
     * @return the value of the property as a boolean.
     * @throws IllegalArgumentException if propValue is neither "true", nor
     *                                  "false", nor "allow"
     */
    static public boolean testProp(ConstMap props, String propName) {
        String propValue =
          ((String)props.fetch(propName, new ValueThunk("false"))).intern();
        if ("true" == propValue) {
            return true;
        } else if ("false" == propValue) {
            return false;
        } else if ("allow" == propValue) {
            return false;
        } else {
            throw new IllegalArgumentException(
              propValue + " must be 'true', 'false', or 'allow'");
        }
    }
}
