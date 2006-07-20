package org.erights.e.elib.deflect;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.elib.base.Callable;
import org.erights.e.elib.base.Ejection;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.prim.StaticMaker;
import org.erights.e.elib.ref.Ref;
import org.erights.e.elib.serial.PassByConstruction;
import org.erights.e.elib.serial.Persistent;
import org.erights.e.elib.tables.FlexList;
import org.erights.e.elib.tables.Selfless;
import org.erights.e.elib.vat.Vat;
import org.erights.e.develop.trace.Trace;
import org.erights.e.develop.exception.ThrowableSugar;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationTargetException;

/**
 * Deflectors enable E objects to implement Java interfaces, and thereby to
 * be called by Java callers written without any knowledge of E or ELib.
 * <p/>
 * You <i>deflect</i> an object to an interface by calling the static deflect
 * method below. We say the result is a <i>deflection</i> of the original
 * object. The result is a three layer wrapping: 1) On the outside is the
 * deflection. This wraps 2) a Deflector, which wraps 3) the deflected
 * object.
 * <p/>
 * The deflection is actually a dynamically generated subclass of
 * Proxy which implements both the interface being deflected to as well as
 * Callable. Deflector itself implements PassByConstruction, Selfless,
 * and Persistent just in case the deflected object does. The Deflection of
 * the object implements whichever of these the deflected object does. We
 * say the interface being deflected to is the J-Interface, and we say that
 * Callable, and whatever subset of PassByConstruction, Selfless, and
 * Persistent the deflection implements are the E-Interfaces.
 * <p/>
 * If the J-Interface has any message definitions in common with the
 * E-Interfaces, confusion will result. XXX This case should be
 * automatically detected, preventing such deflections.
 * <p/>
 * When a deflection is E-called (as with "E.call(...)"), the call is simply
 * passed through to the deflected object (but with the vat context restored,
 * in case the call is coming from a non-turn event in our hosting Runner,
 * such as an AWT-initiated callback). To the E programmer (or the ELib
 * programmer using only ELib mechanisms rather than corresponding Java
 * mechanisms), the deflection is identical to the deflected object.
 * <p/>
 * When a deflection is Java-called (as with "."), this is turned into a
 * {@link Method#invoke}. The vat context is also restored around this.
 * <p/>
 * Note that you <i>can</i> deflect an eventual reference, but you can't use
 * the deflection until the deflected becomes a near reference.
 * <p/>
 * XXX Deflector does not and cannot implement DeepPassByCopy, etc, since it
 * isn't. Will this cause trouble?
 *
 * @author Mark S. Miller
 */
public class Deflector
  implements InvocationHandler, PassByConstruction, Selfless, Persistent {

    static private final long serialVersionUID = 8164442851196730818L;

    /**
     *
     */
    static private final StaticMaker DeflectorMaker
      = StaticMaker.make(Deflector.class);

    /**
     * See the class comment about E-Interfaces
     */
    static private final Class[] EInterfaces = {
        Callable.class,
        PassByConstruction.class,
        Selfless.class,
        Persistent.class
    };

    /**
     * @serial The wrapped/target/deflected object.
     */
    private final Callable myDeflected;

    /**
     * XXX Bug: Since this is transient, it must be restored to the
     * containing Vat on revival; or perhaps it shouldn't be transient?
     */
    private transient final Vat myVat;

    /**
     * Makes a deflector which will wrap and deflect a Callable.
     */
    private Deflector(Callable target) {
        myDeflected = target;
        myVat = Vat.getCurrentVat();
    }

    /**
     * Deflects target to face by wrapping it in a Deflector, and wrapping
     * that in a deflection (a Proxy).
     * <p/>
     * Taming note: To be used only by
     * {@link org.erights.e.meta.java.lang.InterfaceGuardSugar#coerce
     * InterfaceGuardSugar.coerce/2}, as that's where the we check whether
     * <tt>face</tt> is a rubber-stamping (non-{@link
     * org.erights.e.elib.serial.Marker Marker}) interface.
     */
    static public Proxy deflect(Object target, Class face) {
        target = Ref.resolution(target);

        FlexList faceList = FlexList.fromType(Class.class,
                                              1 + EInterfaces.length);
        faceList.push(face);
        faceList.push(Callable.class);
        for (int i = 1, len = EInterfaces.length; i < len; i++) {
            Class intf = EInterfaces[i];
            if (intf.isInstance(target)) {
                faceList.push(intf);
            }
        }
        Class[] faces = (Class[])faceList.getArray(Class.class);
        Callable targ = Ref.toCallable(target);
        InvocationHandler handler = new Deflector(targ);
        return (Proxy)Proxy.newProxyInstance(Callable.class.getClassLoader(),
                                             faces,
                                             handler);
    }

    /**
     * If <tt>optProxy</tt> is a {@link Deflector deflection}, then return the
     * deflected object.
     * <p/>
     * Else return null.
     * <p/>
     * <tt>getOptDeflected/1</tt> must be thread safe, in order for
     * {@link Ref#resolution(Object) Ref.resolution/1} to be thread safe.
     */
    static public Callable getOptDeflected(Proxy optProxy) {
        if (null == optProxy) {
            return null;
        }
        if (!Proxy.isProxyClass(optProxy.getClass())) {
            return null;
        }
        InvocationHandler ih = Proxy.getInvocationHandler(optProxy);
        if (!(ih instanceof Deflector)) {
            return null;
        }
        return ((Deflector)ih).getDeflected();
    }

    /**
     * Uses 'DeflectorMaker(myDeflected)'
     */
    public Object[] getSpreadUncall() {
        Object[] result = {DeflectorMaker, "run", myDeflected};
        return result;
    }

    /**
     * The original deflected target object.
     * <p/>
     * <tt>getDeflected/0</tt> must be thread safe, in order for
     * {@link #getOptDeflected(Proxy) getOptDeflected/1} to be thread safe.
     */
    public Callable getDeflected() {
        return myDeflected;
    }

    /**
     * This is the magic method invoked by the Proxy mechanism.
     * <p/>
     * If the method is either an Object method or a method declared by one
     * of the EInterfaces, then just invoke this method directly on the
     * deflected target object.
     * <p/>
     * Otherwise, we turn it into a callAll() on the deflected target object.
     * <p/>
     * XXX Currently, we only use the method's simple name, but that's fine
     * for all objects defined in E. The only practical place this fails is
     * remote invocation of overloaded Java methods.
     *
     * @param optArgs may be null, so we replace with an empty list.
     */
    public Object invoke(Object proxy, Method method, Object[] optArgs)
      throws Throwable {

        try {
            Object[] args = E.NO_ARGS;
            if (null != optArgs) {
                args = optArgs;
            }
            Class clazz = method.getDeclaringClass();
            boolean isEInterface = (Object.class == clazz);
            if (!isEInterface) {
                for (int i = 0; i < EInterfaces.length; i++) {
                    if (EInterfaces[i] == clazz) {
                        isEInterface = true;
                        break;
                    }
                }
            }
            if (isEInterface) {
                return myVat.invoke(myDeflected, method, args);
            }

            String verb = method.getName();
            Class retType = method.getReturnType();
            Object result = myVat.callAll(myDeflected, verb, args);
            return E.as(result, retType);

        } catch (Throwable ex) {
            Throwable leaf = ThrowableSugar.leaf(ex);
            if (leaf instanceof Ejection) {
                throw (Ejection)leaf;
            }
            if (Trace.causality.event && Trace.ON) {
                Trace.causality.eventm("During Deflection: ", ex);
            }
            throw ex;
        }
    }
}
