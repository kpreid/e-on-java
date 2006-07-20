package org.erights.e.elang.interp;

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

import org.erights.e.develop.assertion.T;
import org.erights.e.elib.base.ValueThunk;
import org.erights.e.elib.prim.StaticMaker;
import org.erights.e.elib.ref.Ref;
import org.erights.e.elib.serial.BaseLoader;
import org.erights.e.elib.slot.FinalSlot;
import org.erights.e.elib.slot.Slot;
import org.erights.e.elib.tables.FlexMap;
import org.erights.e.elib.util.ClassCache;

/**
 * The Loader bound to unsafe__uriGetter.
 * <p>
 * As explained in the superclass comment, this must be thread-safe.
 *
 * @author Mark S. Miller
 * @author E. Dean Tribble
 */
class UnsafeLoader extends BaseLoader {

    static public final UnsafeLoader THE_ONE = new UnsafeLoader();

    /**
     * We assume that ClassLoaders are thread-safe
     * @noinspection UNUSED_SYMBOL,FieldCanBeLocal
     */
    private final ClassLoader myOptLoader;

    /**
     * The values in the slots are either ESTaticWrappers around Class
     * objects (for classes), PackageScopes (for packages).
     * <p>
     * Must synchronize access to this
     */
    private final FlexMap myLocals;

    /**
     *
     */
    private UnsafeLoader(ClassLoader optLoader) {
        myOptLoader = optLoader;
        myLocals = FlexMap.fromTypes(String.class, FinalSlot.class);
    }

    /**
     * optLoader defaults to null.
     */
    private UnsafeLoader() {
        this(null);
    }

    /** exception if not found */
    private Object getWrapped(String fqName) {
        if ("*".equals(fqName)) {
            return this;
        } else if (fqName.endsWith(".*")) {
            return new PackageLoader(this, "unsafe:", fqName);
        }
        Class clazz;
        try {
            //XXX should use myOptLoader, if not null
            clazz = ClassCache.forName(fqName);
            ImportLoader.warnNoMake(fqName);
        } catch (ClassNotFoundException cnfe) {
            // XXX Should refactor this.
            String optJFQName = ImportLoader.getOptJFQName(fqName);
            if (null == optJFQName) {
                T.fail(fqName + " not found");
                return null; //make compiler happy
            } else {
                try {
                    //XXX should use myOptLoader, if not null
                    clazz = ClassCache.forName(optJFQName);
                } catch (ClassNotFoundException cnfe2) {
                    T.fail(fqName + " not found");
                    return null; //make compiler happy
                }
            }
        }
        return StaticMaker.make(clazz);
    }

    /**
     *
     */
    private Slot getLocalSlot(String fqName) {
        Slot result;
        synchronized (myLocals) {
            result = (Slot)myLocals.fetch(fqName, ValueThunk.NULL_THUNK);
        }
        if (null != result) {
            return result;
        }
        result = new FinalSlot(getWrapped(fqName));
        synchronized (myLocals) {
// See https://bugs.sieve.net/bugs/?func=detailbug&bug_id=125642&group_id=16380
            myLocals.put(fqName, result, false);
        }
        return result;
    }

    /**
     * exception if not found
     *
     * @noinspection ParameterNameDiffersFromOverriddenParameter
     */
    public Object get(String fqName) {
        return getLocalSlot(fqName).getValue();
    }

    /**
     * This only need work for StaticMakers.
     */
    public Object[] optUncall(Object obj) {
        obj = Ref.resolution(obj);
        if (obj instanceof StaticMaker) {
            StaticMaker maker = (StaticMaker)obj;
            String jfqName = maker.asType().getFQName();
            try {
                //XXX should use myOptLoader, if not null
                ClassCache.forName(jfqName);
            } catch (ClassNotFoundException cnfe) {
                return null;
            }
            return BaseLoader.ungetToUncall(this,
                                            ImportLoader.getFQName(jfqName));
        }
        return null;
    }


    /**
     *
     */
    public String toString() {
        return "<unsafe>";
    }
}
