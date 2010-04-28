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
import org.erights.e.develop.exception.EBacktraceException;
import org.erights.e.develop.trace.Trace;
import org.erights.e.elang.evm.EExpr;
import org.erights.e.elang.scope.Scope;
import org.erights.e.elang.syntax.EParser;
import org.erights.e.elib.base.ClassDesc;
import org.erights.e.elib.base.ValueThunk;
import org.erights.e.elib.prim.SafeJ;
import org.erights.e.elib.prim.StaticMaker;
import org.erights.e.elib.ref.Ref;
import org.erights.e.elib.ref.Resolver;
import org.erights.e.elib.serial.BaseLoader;
import org.erights.e.elib.serial.JOSSPassByConstruction;
import org.erights.e.elib.slot.FinalSlot;
import org.erights.e.elib.slot.Slot;
import org.erights.e.elib.tables.FlexMap;
import org.erights.e.elib.tables.Twine;
import org.erights.e.elib.util.ClassCache;
import org.erights.e.elib.vat.StackContext;
import org.erights.e.meta.java.net.URLSugar;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.net.URL;

/**
 * The Loader bound to import__uriGetter.
 * <p/>
 * As explained in the superclass comment, this must be thread-safe.
 *
 * @author Mark S. Miller
 * @author E. Dean Tribble
 */
class ImportLoader extends BaseLoader implements JOSSPassByConstruction {
    static private final long serialVersionUID = 1L;

    /**
     * The values in the slots are either EStaticWrappers on Class objects (for
     * safe classes), PackageScopes (for packages), or whatever value a .emaker
     * file evaluated to, when interpreted in the safeScope.
     */
    private final transient FlexMap myAlreadyImported;

    private transient Thread myOptThread;
    private transient Scope mySafeScope;

    /**
     *
     */
    ImportLoader() {
        myAlreadyImported =
          FlexMap.fromTypes(String.class, FinalSlot.class);

        Object[] safePair = ScopeSetup.safeScopePair("root$", this);
        mySafeScope = (Scope) safePair[1];
    }

    /**
     * A step towards fixing http://sourceforge.net/tracker/index.php?func=detail&aid=1212444&group_id=75274&atid=551529
     */
    static String getOptJFQName(String fqName) {
        String flatName = ClassDesc.flatName(fqName);
        if (flatName.startsWith("make") && fqName.endsWith(flatName)) {
            int fqLen = fqName.length();
            int flatLen = flatName.length();
            int makeLen = "make".length();
            return fqName.substring(0, fqLen - flatLen) +
              flatName.substring(makeLen);
        }
        return null;
    }

    static String getFQName(String jfqName) {
        String jflatName = ClassDesc.flatName(jfqName);
        int jfqLen = jfqName.length();
        int jflatLen = jflatName.length();
        return jfqName.substring(0, jfqLen - jflatLen) + "make" + jflatName;
    }

    /**
     *
     */
    private Object readResolve() throws ObjectStreamException {
        return new ImportLoader();
    }

    /**
     * returns null if not found
     */
    private URL optResource(String rName) {
        return ClassLoader.getSystemResource(rName);
    }

    /**
     * returns null if not found
     */
    private Twine optESource(String fqName) {
        String rName = fqName.replace('.', '/') + ".emaker";
        URL resource = optResource(rName);
        if (resource == null) {
            return null;
        } else {
            try {
                return URLSugar.getTwine(resource);
            } catch (IOException ioe) {
                throw new EBacktraceException(ioe, "# getting E source");
            }
        }
    }

    /**
     * null if not found. Thrown exception if found but unsafe.
     */
    private StaticMaker getOptStaticMaker(String jfqName, String fqName) {
        Class clazz;
        try {
            //XXX should use myOptLoader, if not null
            clazz = ClassCache.forName(jfqName);
        } catch (ClassNotFoundException cnfe) {
            return null;
        }
        if (!SafeJ.approve(clazz, true)) {
            //if jfqName is not in the approved list
            throw new SecurityException(fqName + " not approved as safe");
        }
        return StaticMaker.make(clazz);
    }

    /**
     * Gets the value at fqName, and indicate whether it is itself DeepFrozen.
     * <p/>
     * If not found, throws an exception. Sets isConfinedPtr[0] according to
     * whether the result is itself DeepFrozen (transitively immutable, or safe
     * to treat as such).
     */
    private Object getValue(String fqName, boolean[] isConfinedPtr) {
        if (myOptThread == null) {
            myOptThread = Thread.currentThread();
        } else {
            if (myOptThread != Thread.currentThread()) {
                throw new RuntimeException("ImportLoader called from first thread " + myOptThread + ", then from " + Thread.currentThread());
            }
        }

        if ("*".equals(fqName)) {
            isConfinedPtr[0] = true;
            return this;
        } else if (fqName.endsWith(".*")) {
            isConfinedPtr[0] = true;
            return new PackageLoader(this, "import:", fqName);
        }
        //prefer a compiled one.
        Object result = getOptStaticMaker(fqName, fqName);
        if (null != result) {
            warnNoMake(fqName);
            isConfinedPtr[0] = true;
            return result;
        }

        String optJFQName = getOptJFQName(fqName);
        if (null != optJFQName) {
            result = getOptStaticMaker(optJFQName, fqName);
            if (null != result) {
                isConfinedPtr[0] = true;

                if (Trace.eruntime.warning && Trace.ON) {
                    if (null != optESource(fqName)) {
                        Trace.eruntime
                          .warningm("Ignoring interpreted " + fqName +
                            " in favor of compiled one.");
                    }
                }

                return result;
            }
        }

        //XXX todo: look for E prefix as package
        Twine eSource = optESource(fqName);
        if (null != eSource) {
            EExpr eExpr = (EExpr)EParser.run(eSource);
            //The fqnPrefix for the loaded defs has this fqName as its outer
            //"class".
            Scope newSafeScope = mySafeScope.withPrefix(fqName + "$");
            result = eExpr.eval(newSafeScope);

            // Will be a step towards a better module system
//            isConfinedPtr[0] = Ref.isDeepFrozen(result);
            // XXX Leave it this way for now until we figure out the caching
            // issue.
            isConfinedPtr[0] = false;
            return result;
        }

        T.fail(fqName + " not found");
        return null; //make compiler happy
    }

    /**
     *
     */
    static void warnNoMake(String fqName) {
        if (Trace.eruntime.warning && Trace.ON) {
            if (!fqName.startsWith("make")) {
                String goodFQName = getFQName(fqName);
                StackContext sc =
                  new StackContext("Importing " + fqName, true, true);
                Trace.eruntime
                  .warningm("Import " + goodFQName + " instead", sc);
            }
        }
    }

    /**
     *
     */
    public Object get(String uriBody) {
        return getLocalSlot(uriBody).get();
    }

    /**
     * Must handle cyclic imports
     */
    public Slot getLocalSlot(String fqName) {
//System.err.println("ImportLoader.getting: " + fqName);
        Slot result;
        result = (Slot)myAlreadyImported.fetch(fqName, ValueThunk.NULL_THUNK);
        if (null != result) {
            //XXX Once we detect that an emaker is DeepFrozen and cache it for
            // longer, we need to also somehow check if a later ESource is
            // available, and, if so, use it and (perhaps?) upgrade
            // old instances. Since we don't yet test emaker confinement, so
            // we can't yet cache them for longer than it takes to resolve a
            // cycle (one top-level import: request), this isn't yet an
            // issue.
            //XXX Since we found it, it must either be there to resolve a
            // cycle, or it is itself DeepFrozen.
            return result;
        }
        Object[] promise = Ref.promise();
        Ref ref = (Ref)promise[0];
        Resolver resolver = (Resolver)promise[1];

        result = new FinalSlot(ref);
        myAlreadyImported.put(fqName, result, true);
        Object value;
        //start with it set to false, so we'll also remove fqName if
        //getValue() throws
        boolean[] keep = {false};
        try {
            value = getValue(fqName, keep);
        } finally {
            if (!keep[0]) {
                //keep[0] says that the stored association is itself
                //DeepFrozen. If not, remove it.
                myAlreadyImported.removeKey(fqName, true);
            }
        }
        resolver.resolve(value);
        return result;
    }

    /**
     * XXX For now, this only need work for StaticMakers.
     */
    public Object[] optUncall(Object obj) {
        obj = Ref.resolution(obj);
        if (obj instanceof StaticMaker) {
            StaticMaker maker = (StaticMaker)obj;
            String jfqName = maker.asType().getFQName();
            Class clazz;
            try {
                //XXX should use myOptLoader, if not null
                clazz = ClassCache.forName(jfqName);
            } catch (ClassNotFoundException cnfe) {
                return null;
            }
            if (SafeJ.approve(clazz, true)) {
                return BaseLoader.ungetToUncall(this, getFQName(jfqName));
            }
        }
        return null;
    }

    /**
     *
     */
    public String toString() {
        return "<import>";
    }
}
