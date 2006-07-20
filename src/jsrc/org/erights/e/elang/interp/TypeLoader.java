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
import org.erights.e.elib.base.ClassDesc;
import org.erights.e.elib.ref.Ref;
import org.erights.e.elib.serial.BaseLoader;
import org.erights.e.elib.serial.DeepFrozenAuditor;
import org.erights.e.elib.slot.Auditor;
import org.erights.e.elib.slot.FinalSlot;
import org.erights.e.elib.slot.Guard;
import org.erights.e.elib.slot.Slot;
import org.erights.e.elib.util.ClassCache;

/**
 * The Loader bound to type__uriGetter in the safe scope.
 * <p/>
 * Given a fully-qualified-name, returns a {@link DeepFrozenAuditor DeepFrozen}
 * {@link Guard Guard} (and possibly an {@link Auditor Auditor}) corresponding
 * to that name. This is currently only meaningful for Java fully qualified
 * class names, so it currently throws an exception on anything else. An
 * E-to-JVM compiler will cause this to apply somehow to E code, so if this is
 * extended to deal with interpretive E code as well, it should be extended to
 * have the same semantics that it would apply to compiled E code.
 * <p/>
 * As explained in the superclass comment, this must be thread-safe.
 *
 * @author Mark S. Miller
 * @author E. Dean Tribble
 */
class TypeLoader extends BaseLoader {

    static public final TypeLoader THE_ONE = new TypeLoader();

    /**
     * We assume that ClassLoaders are thread-safe
     */
    private final ClassLoader myOptLoader;

    /**
     *
     */
    private TypeLoader(ClassLoader optLoader) {
        myOptLoader = optLoader;
    }

    /**
     * optLoader defaults to null.
     */
    private TypeLoader() {
        this(null);
    }

    /**
     * exception if not found
     */
    private Object getWrapped(String fqName) {
        if ("*".equals(fqName)) {
            return this;
        } else if (fqName.endsWith(".*")) {
            return new PackageLoader(this, "type:", fqName);
        }
        try {
            //XXX should use myOptLoader, if not null
            return ClassCache.forName(fqName);
        } catch (ClassNotFoundException cnfe) {
            T.fail(fqName + " not found");
            return null; //make compiler happy
        }
    }

    /**
     *
     */
    private Slot getLocalSlot(String fqName) {
        return new FinalSlot(getWrapped(fqName));
    }

    /**
     * exception if not found
     */
    public Object get(String fqName) {
        return getLocalSlot(fqName).getValue();
    }

    /**
     * XXX For now, this only need work for Classes and ClassDescs.
     */
    public Object[] optUncall(Object obj) {
        obj = Ref.resolution(obj);
        String fqName;
        if (obj instanceof ClassDesc) {
            fqName = ((ClassDesc)obj).getFQName();
        } else if (obj instanceof Class) {
            fqName = ClassDesc.sig((Class)obj);
        } else {
            return null;
        }
        return BaseLoader.ungetToUncall(this, fqName);
    }

    /**
     *
     */
    public String toString() {
        return "<type>";
    }
}
