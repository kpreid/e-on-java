package org.erights.e.elib.util;

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

import java.util.concurrent.ConcurrentHashMap;

/**
 * A performance optimizing hack: by hanging onto classes that are looked up by
 * name, we can spare Java's overhead if they ever have to be looked up again.
 * <p/>
 * Also recognizes the names for the scalar types and "void".
 * <p/>
 * XXX The original motivation -- the surprising overhead of Java's {@link
 * Class#forName(String)} may have been long fixed, in which case this
 * implementation should do less.
 *
 * @author Mark S. Miller
 */
public class ClassCache {

    /**
     * Using java.util.concurrent instead of ELib's tables in order to avoid
     * circular dependencies, and in order to get the thread-safety necessary
     * for static used globally shared across a JVM.
     */
    static private final ConcurrentHashMap OurCache = new ConcurrentHashMap();

    static {
        OurCache.put("boolean", Boolean.TYPE);
        OurCache.put("char", Character.TYPE);

        OurCache.put("byte", Byte.TYPE);
        OurCache.put("short", Short.TYPE);
        OurCache.put("int", Integer.TYPE);
        OurCache.put("long", Long.TYPE);

        OurCache.put("float", Float.TYPE);
        OurCache.put("double", Double.TYPE);

        OurCache.put("void", Void.TYPE);
    }

    /**
     * prevent instantiation
     */
    private ClassCache() {
    }

    /**
     * Like {@link Class#forName(String)}, but also accepts the string-names
     * for the scalar types and "void".
     */
    static public Class forName(String name) throws ClassNotFoundException {
        Class result = (Class)OurCache.get(name);
        if (result == null) {
            result = Class.forName(name);
            OurCache.putIfAbsent(name, result);
        }
        return result;
    }
}
