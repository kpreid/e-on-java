package org.erights.e.elib.base;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.develop.assertion.T;
import org.erights.e.develop.exception.ExceptionMgr;
import org.erights.e.develop.format.StringHelper;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.prim.ScriptMaker;
import org.erights.e.elib.prim.Thrower;
import org.erights.e.elib.ref.Ref;
import org.erights.e.elib.slot.AnyGuard;
import org.erights.e.elib.slot.Guard;
import org.erights.e.elib.slot.NullOkGuard;
import org.erights.e.elib.slot.VoidGuard;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.ConstMap;
import org.erights.e.elib.tables.FlexList;
import org.erights.e.elib.tables.FlexMap;
import org.erights.e.elib.tables.FlexSet;
import org.erights.e.elib.util.ClassCache;
import org.erights.e.elib.util.OneArgFunc;
import org.erights.e.meta.java.lang.ArrayGuardSugar;
import org.erights.e.meta.java.lang.InterfaceGuardSugar;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Describes the E-type of instances of a Java class (and its subclasses). This
 * is the Java type as modified by the E sugar mechanism, and as seen through
 * E.
 *
 * @author Mark S. Miller
 */
public class ClassDesc extends TypeDesc {

    /**
     * Maps from the fully qualified class name of a non-primitive to the
     * fqname of the GuardSugar class providing its coerce method.
     * <p/>
     * When a Class is used as a type (coerced to a ClassDesc), it might have
     * per-Class coercion behavior. Such behavior would be in a GuardSugar
     * class that implements Guard. This table maps from the fqnames of Classes
     * to the fqnames of their corresponding GuardSugar class. The ClassDesc
     * that a Class is an instance of this GuardSugar.
     * <p/>
     * For a given Class, if a direct match isn't found, we check that class's
     * supertypes (both extends & implements). If a unique best match is found
     * we take that. Otherwise we throw an Exception. (XXX double check that we
     * actually do so.)
     * <p/>
     * Arrays make use of ArrayGuardSugar, interfaces make use of
     * InterfaceGuardSugar by default, and fully unmatched types make use of
     * ClassDesc itself.
     */
    static private final String[][] GuardSugarings = {{"java.lang.Class",
      "org.erights.e.meta.java.lang.ClassGuardSugar"},
      {"java.lang.String", "org.erights.e.meta.java.lang.StringGuardSugar"},

      {"java.lang.Throwable",
        "org.erights.e.meta.java.lang.ThrowableGuardSugar"},

      {"java.lang.Byte", "org.erights.e.meta.java.lang.ByteGuardSugar"},
      {"java.lang.Short", "org.erights.e.meta.java.lang.ShortGuardSugar"},
      {"java.lang.Integer", "org.erights.e.meta.java.lang.IntegerGuardSugar"},
      {"java.lang.Long", "org.erights.e.meta.java.lang.LongGuardSugar"},
      {"java.math.BigInteger",
        "org.erights.e.meta.java.math.BigIntegerGuardSugar"},
      {"org.erights.e.meta.java.math.EInt",
        "org.erights.e.meta.java.math.EIntGuardSugar"},

      {"java.lang.Float", "org.erights.e.meta.java.lang.FloatGuardSugar"},
      {"java.lang.Double", "org.erights.e.meta.java.lang.DoubleGuardSugar"},

      {"java.lang.Void", "org.erights.e.meta.java.lang.VoidGuardSugar"},

      {"org.erights.e.elib.base.TypeDesc",
        "org.erights.e.meta.org.erights.e.elib.base.TypeDescGuardSugar"},

      {"org.erights.e.elib.ref.Ref",
        "org.erights.e.meta.org.erights.e.elib.ref.RefGuardSugar"},

      {"org.erights.e.elib.tables.EList",
        "org.erights.e.meta.org.erights.e.elib.tables.EListGuardSugar"},

      {"org.quasiliteral.astro.Astro",
        "org.erights.e.meta.org.quasiliteral.astro.AstroGuardSugar"}};

    /**
     * The above list of pairs turned into a ConstMap.
     */
    static private final ConstMap TheGuardSugars =
      ConstMap.fromPairs(GuardSugarings);

    /**
     *
     */
    static private final FlexMap GuardCache =
      FlexMap.fromTypes(Class.class, Class.class);

    /**
     * @param clazz A non-primitive class
     * @return A kind of ClassDesc.
     */
    static private Class GetGuard(Class clazz) throws ClassNotFoundException {
        if (clazz.isPrimitive()) {
            T.fail("Must not be primitive: " + clazz);
        }
        Class result;
        //Need to synchronize since this is inter-vat mutable state
        synchronized (GuardCache) {
            result = (Class)GuardCache.fetch(clazz, ValueThunk.NULL_THUNK);
        }
        if (null != result) {
            return result;
        }
        String sugarFQName =
          (String)TheGuardSugars.fetch(clazz.getName(), ValueThunk.NULL_THUNK);
        if (null != sugarFQName) {
            result = ClassCache.forName(sugarFQName);
        } else if (clazz.isArray()) {
            result = ArrayGuardSugar.class;
        } else {
            Class[] ifaces = clazz.getInterfaces();
            Class[] supers;
            Class optSuper = clazz.getSuperclass();
            if (null == optSuper) {
                supers = ifaces;
            } else {
                supers = new Class[ifaces.length + 1];
                System.arraycopy(ifaces, 0, supers, 1, ifaces.length);
                supers[0] = optSuper;
            }
            FlexSet superSugarSet = FlexSet.fromType(Class.class);
            for (int i = 0; i < supers.length; i++) {
                Class superSugar = GetGuard(supers[i]);
                if (ClassDesc.class != superSugar && (
                  InterfaceGuardSugar.class != superSugar ||
                    clazz.isInterface())) {
                    //only interfaces inherit InterfaceGuardSugar
                    superSugarSet.addElement(superSugar);
                }
            }
            Class[] superSugars =
              (Class[])superSugarSet.getElements(Class.class);
            if (0 == superSugars.length) {
                if (clazz.isInterface()) {
                    result = InterfaceGuardSugar.class;
                } else {
                    result = ClassDesc.class;
                }
            } else if (1 == superSugars.length) {
                result = superSugars[0];
            } else {
                //avoid E.toString() in order to avoid infinite regress
                StringBuffer buf = new StringBuffer();
                for (int i = 0; i < superSugars.length; i++) {
                    buf.append(" ").append(superSugars[i]);
                }
                T.fail("coercion inheritance conflict" + buf);
            }
        }
        synchronized (GuardCache) {
            GuardCache.put(clazz, result);
        }
        return result;
    }

    /**
     * A Mapping from classes, primitive or not, to ClassDescs (instances of
     * ClassDesc or a GuardSugar subclass of ClassDesc).
     */
    static private final FlexMap DescCache =
      FlexMap.fromTypes(Class.class, ClassDesc.class);

    /**
     * Returns a guard that corresponds more closely than make/1, in two ways,
     * to Java's type conformance rules.
     * <p/>
     * 1) In Java, when a class is used to declare a variable, parameter, or
     * return type, unless the class is primitive, it accepts <tt>null</tt> in
     * addition to instances of its type. If the class is primitive (ie, a
     * scalar, like <tt>char</tt>, then it accepts only instances of its type.
     * <p/>
     * By contrast, when a Java class is used as an E guard, it accepts only
     * instances of its type. In order to form a guard that accepts either
     * <tt>null</tt> or any instance of class T, one uses the
     * <tt>nullOk</tt> function: <pre>
     *     def x :nullOk[T]
     * </pre>
     * The byJavaRules/1 method will turn a class into a guard in the same way
     * as make/1, except that if the Class parameter isn't primitive, it'll
     * then wrap the result in a call to nullOk/1.
     * <p/>
     * 2) The class Object.class is turned into the equivalent of <tt>:any</tt>
     * rather than using a ClassDesc on the class object. This reflects that
     * Java parameters of type Object will also accept non-near pointers.
     * Likewise, it converts Void.class and Void.TYPE into the equivalent of
     * <tt>:void</tt>.
     */
    static public Guard byJavaRules(Class clazz) {
        if (Object.class == clazz) {
            return AnyGuard.THE_ONE;
        }
        if (Void.class == clazz || Void.TYPE == clazz) {
            return VoidGuard.THE_ONE;
        }
        Guard result = make(clazz);
        if (clazz.isPrimitive()) {
            return result;
        } else {
            return NullOkGuard.THE_BASE.get(result);
        }
    }

    /**
     * Returns the E-object that a Java Class object promotes to.
     * <p/>
     * This provides several services<ul> <li>A descriptions of the messages
     * that may be sent to members of this type. <li>Guard (coerce/2) behavior,
     * enabling a Class to be used as a Guard. <li>Auditor (audit/1) behavior,
     * enabling... </ul>
     */
    static public ClassDesc make(Class clazz) {
        try {
            return privateMake(clazz);
        } catch (Exception ex) {
            throw ExceptionMgr.asSafe(ex);
        }
    }

    /**
     *
     */
    static private final Class[] ArgTypes = {Class.class};

    /**
     * Returns a description of the messages that may be sent to members of
     * this type.
     */
    static private ClassDesc privateMake(Class clazz) throws
      ClassNotFoundException, IllegalAccessException, InstantiationException,
      InvocationTargetException, NoSuchMethodException {
        ClassDesc result;
        //Need to synchronize since this is inter-vat mutable state
        synchronized (DescCache) {
            result = (ClassDesc)DescCache.fetch(clazz, ValueThunk.NULL_THUNK);
            if (null != result) {
                return result;
            }
            //noinspection CallToNativeMethodWhileLocked
            if (clazz.isPrimitive()) {
                //This should only happen once, and clazz must be one of the
                //nine primitive TYPEs below
                DescCache.put(Boolean.TYPE, privateMake(Boolean.class));
                DescCache.put(Character.TYPE, privateMake(Character.class));
                DescCache.put(Byte.TYPE, privateMake(Byte.class));
                DescCache.put(Short.TYPE, privateMake(Short.class));
                DescCache.put(Integer.TYPE, privateMake(Integer.class));
                DescCache.put(Long.TYPE, privateMake(Long.class));
                DescCache.put(Float.TYPE, privateMake(Float.class));
                DescCache.put(Double.TYPE, privateMake(Double.class));
                DescCache.put(Void.TYPE, privateMake(Void.class));
                return privateMake(clazz);
            }
        }
        Class guardSugar = GetGuard(clazz);
        Constructor guardConstructor = guardSugar.getConstructor(ArgTypes);
        Object[] args = {clazz};
        result = (ClassDesc)guardConstructor.newInstance(args);
        synchronized (DescCache) {
            DescCache.put(clazz, result);
        }
        return result;
    }

    /**
     *
     */
    static private ConstList mTypes(Class clazz) {
        Script script = ScriptMaker.THE_ONE.instanceScript(clazz);
        FlexList mTypeList = FlexList.fromType(MessageDesc.class);
        //null, since the instance doesn't matter for a Java type.
        script.protocol(null, mTypeList);
        return mTypeList.snapshot();
    }

    /**
     *
     */
    private final Class myClass;

    /**
     *
     */
    public ClassDesc(Class clazz) {
        super(" Missing docComment ", //XXX for now
              sig(clazz), null, //XXX for now
              ConstList.EmptyList, mTypes(clazz));
        myClass = clazz;
    }

    /**
     * Handles null, handles a trivial match ({@link Class#isInstance(Object)}),
     * and otherwise delegates to {@link #subCoerceR(Object,OneArgFunc)}.
     * <p/>
     * But does check the output, so it can give a meaningful error if it
     * doesn't match. This allows subCoerce implementations to avoid checking
     * for those errors that may as well be caught here.
     */
    protected Object tryCoerceR(Object shortSpecimen, OneArgFunc optEjector) {
        if (null == shortSpecimen) {
            throw Thrower.toEject(optEjector,
                                  new NullPointerException("must be " +
                                    StringHelper.aan(E.toString(this)) +
                                    " rather than null"));
        }
        if (!Ref.isNear(shortSpecimen)) {
            //Must check this before isInstance, since eventual references
            //are Java-instances of some Java-types, and we wish to reject
            //these anyway
            throw Thrower.toEject(optEjector,
                                  "Must be near: " + shortSpecimen);
        }
        if (myClass.isInstance(shortSpecimen)) {
            //try for a cheap success
            return shortSpecimen;
        }
        shortSpecimen = subCoerceR(shortSpecimen, optEjector);
        if (myClass.isInstance(shortSpecimen)) {
            return shortSpecimen;
        }
        throw Thrower.toEject(optEjector,
                              "but " + shortSpecimen + " isn't " +
                                StringHelper.aan(E.toString(this)));
    }

    /**
     * 'shortSpecimen' is assumed to already be shortened, not be null, and
     * already known not to be an instance of myClass.
     * <p/>
     * Used by {@link #tryCoerceR} and overridden by subclasses to provide the
     * type-specific part of the coercion behavior. The default implementation
     * here firsts sees if specimen promotes to a subtype of the target type,
     * and if so, returns that promotion. Failing that, the default
     * implementation ejects according to optEjector.
     * <p/>
     * Subclasses should override to provide other coercions, and delegate to
     * this one (super.subCoerce...) if they can't coerce.
     */
    protected Object subCoerceR(Object shortSpecimen, OneArgFunc optEjector) {
        Class optPromo = ScriptMaker.OptPromotion(shortSpecimen.getClass());
        if (null != optPromo && myClass != optPromo &&
          myClass.isAssignableFrom(optPromo)) {

            return make(optPromo).coerce(shortSpecimen, optEjector);
        }
        throw doesntCoerceR(shortSpecimen, optEjector);
    }

    /**
     *
     */
    protected RuntimeException doesntCoerceR(Object shortSpecimen,
                                             OneArgFunc optEjector) {
        String specTypeName = simpleSig(shortSpecimen.getClass());
        ClassCastException prob = new ClassCastException(specTypeName +
          " doesn't coerce to " + StringHelper.aan(E.toString(this)));
        return Thrower.toEject(optEjector, prob);
    }

    /**
     * Not meaningful from E, since a Class promotes to its ClassDesc anyway.
     */
    public final Class asClass() {
        return myClass;
    }

    /**
     * Cause "myClass[5]" to return a 5 element array of myClass.
     * <p/>
     * Rather weird kludge. Result is that, in E, "myClass[5]" doesn't get the
     * fifth (or sixth) of anything, but rather produces a 5 element array of
     * type myClass.
     *
     * @deprecated Since it makes an uninitialized array (or rather an array
     *             initialized to its type's zero element), and since arrays,
     *             to E, are immutable, this probably isn't what you wanted
     *             anyway.
     */
    public Object get(int index) {
        return ArrayHelper.newArray(myClass, index);
    }

    /**
     * Cause "myClass[]" to return the type "list of myClass".
     * <p/>
     * At the Java level, it's actually the type "Array of myClass".
     *
     * @deprecated Use ':List[type]' instead of ':type[]'
     */
    public Guard get() {
        //XXX this is a horrible way to do this, but I couldn't find any
        //simpler way.
        return make(get(0).getClass());
    }

    /************ name mangling and demangling, sort of ************/

    /**
     * Drops any package or containing class prefixes.
     */
    static public String flatName(String fqName) {
        int lastSep = fqName.lastIndexOf('.');
        lastSep = StrictMath.max(lastSep, fqName.lastIndexOf('$'));
        //if there are no dots or dollar signs then lastIndexOf() returns -1,
        //so the +1 below will still do the correct thing.
        return fqName.substring(lastSep + 1);
    }

    /**
     * Drops any package or containing class prefixes and any "__C" or "__T"
     * suffixes prior to the last "significant" name.
     * <p/>
     * A "significant" name is any name that doesn't begin with a digit (ruling
     * out anonymous objects and classes) and that isn't "self".
     */
    static public String simpleName(String fqName) {
        int lastSep = fqName.lastIndexOf('.');
        lastSep = StrictMath.max(lastSep, fqName.lastIndexOf('$'));
        //if there are no dots or dollar signs then lastIndexOf() returns -1,
        //so the +1 below will still do the correct thing.
        String result = fqName.substring(lastSep + 1);
        if (result.endsWith("__C") || result.endsWith("__T")) {
            int len = result.length() - "__C".length();
            result = result.substring(0, len);
        }
        if ("self".equals(result) || Character.isDigit(result.charAt(0))) {
            //result so far is not "significant".
            if (1 <= lastSep) {
                String qualifier = fqName.substring(0, lastSep);
                result = "..." + simpleName(qualifier) +
                  fqName.charAt(lastSep) + result;
            }
        }
        return result;
    }

    /**
     * If an array type, then the sig-with-suffix of the base type followed by
     * "[]".
     */
    static public String sig(Class type, String suffix) {
        if (type.isArray()) {
            return sig(type.getComponentType(), suffix) + "[]";
        } else {
            return type.getName() + suffix;
        }
    }

    /**
     * If an array type, then the sig of the base type followed by "[]".
     * <p/>
     * For example, an array or arrays of Strings would be
     * <code>"java.lang.String[][]"</code>. Primitive types are represented by
     * their simple name.
     */
    static public String sig(Class type) {
        return sig(type, "");
    }

    /**
     * Only the class name itself without the package qualifier.
     * <p/>
     * If an array type, then the flatSig of the base type followed by "[]".
     */
    static public String flatSig(Class type) {
        return flatName(sig(type));
    }

    /**
     * Only the class name itself without the package qualifier.
     * <p/>
     * If an array type, then the simpleSig of the base type followed by "[]".
     */
    static public String simpleSig(Class type) {
        return simpleName(sig(type));
    }
}
