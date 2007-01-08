package org.erights.e.elib.vat;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.develop.assertion.T;
import org.erights.e.develop.exception.ExceptionMgr;
import org.erights.e.elib.ref.DelayedRedirector;
import org.erights.e.elib.ref.EProxyHandler;
import org.erights.e.elib.ref.EProxyResolver;
import org.erights.e.elib.ref.Ref;
import org.erights.e.elib.sealing.Brand;
import org.erights.e.elib.sealing.SealedBox;
import org.erights.e.elib.sealing.Sealer;
import org.erights.e.elib.sealing.Unsealer;
import org.erights.e.elib.serial.DeepPassByCopy;
import org.erights.e.elib.serial.JOSSPassByConstruction;
import org.erights.e.elib.serial.PassByProxy;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.ConstMap;
import org.erights.e.elib.util.ArityMismatchException;


/**
 * There is one boot-comm-system per JVM, and all boot-refs (all {@link Ref}s
 * handled by a BootRefHandler) are part of that one comm system.
 * <p/>
 * The boot-comm-system differs from normal inter-vat comm systems (like CapTP)
 * in the following ways: <ul> <li>The boot-comm-system can only be used to
 * communicate with vats within the same jvm (or host OS address space).
 * <li>Communications happen by pointer manipulation, not serialization, so the
 * only {@link JOSSPassByConstruction JOSSPassByConstruction} arguments that may
 * be passed or returned as results are those that are {@link DeepPassByCopy
 * DeepPassByCopy}. </ul> When PassByProxy objects are passed between vats by
 * the boot-comm-system, this is done by wrapping or unwrapping them in a
 * BootRefHandler.
 *
 * @author Mark S. Miller
 */
class BootRefHandler implements EProxyHandler {

    /**
     * So that the boot-comm-system can recognize itself.
     */
    static private final Sealer OurSealer;

    /**
     * So that the boot-comm-system can recognize itself.
     */
    static final Unsealer OurUnsealer;

    static {
        Object[] pair = Brand.run("PrimitiveCommSystem");
        OurSealer = (Sealer)pair[0];
        OurUnsealer = (Unsealer)pair[1];
    }

    /**
     * An invocation of a boot-ref will cause an invocation of myTarget in
     * myTargetsVat.
     */
    final Vat myTargetsVat;

    /**
     *
     */
    final Object myTarget;

    /**
     * Flag to remember whether any E-level messages have been sent over me.
     */
    private boolean myFreshFlag = true;

    /**
     * The Resolver of our Proxy, which also revives the Proxy on demand.
     */
    private final EProxyResolver myResolver;

    /**
     * In order to make an EVENTUAL reference to target which will queue
     * messages in targetsVat for delivery to target.
     * <p/>
     * It must be safe to invoke target in targetsVat
     */
    BootRefHandler(Vat targetsVat, Object target) {
        myTargetsVat = targetsVat;
        myTarget = target;
        Object optIdentity = null;
        if (Ref.isPassByProxy(target)) {
            optIdentity = new BootRefIdentity(target);
        }
        myResolver = new EProxyResolver(this, optIdentity);
    }

    /**
     *
     */
    public EProxyHandler unwrap() {
        return this;
    }

    /**
     *
     */
    public SealedBox handleOptSealedDispatch(Object brand) {
        if (OurSealer.getBrand() == brand) {
            return OurSealer.seal(this);
        } else {
            return null;
        }
    }

    /**
     * If 'ref' is a boot-ref, get its BootRefHandler.
     * <p/>
     * This is like
     * <pre>    {@link
     * EProxyResolver#getOptProxyHandler}(OurUnsealer, ref)</pre>
     * except that it's thread-safe.
     * <p/>
     * <tt>getOptBootRefHandler/1</tt> must be thread safe, in order for {@link
     * org.erights.e.elib.vat.BootRefHandler#packageArg(Object,Vat,Vat,Vat)
     * BootRefHandler.packageArg/4} to be thread safe: Callers of this should
     * keep in mind that ref may be shortened after the handler is gotten but
     * before these callers use it. If they access only final fields of the
     * handler in a thread safe way, then everything should be fine.
     */
    static private BootRefHandler getOptBootRefHandler(Object ref) {
        if (ref instanceof Ref) {
            return (BootRefHandler)((Ref)ref).
              getOptProxyHandler(BootRefHandler.class);
        } else {
            return null;
        }
    }

    /**
     * Given that <tt>arg</tt> is an object in the src vat (ie, an object that
     * would be safe to invoke in the src vat), then return a ref to it
     * packaged for use in the dest vat.
     * <p/>
     * By cases:<ul> <li>If src and dest are the same, then returns
     * <tt>arg</tt>. <li>If <tt>arg</tt> is {@link DeepPassByCopy
     * DeepPassByCopy} or a broken reference, then it can be invoked from any
     * vat, so we pass it as is. <li>If it's {@link PassByProxy PassByProxy},
     * then we gotta wrap it in a boot-ref for use in the dest vat, and return
     * that. <li>If it's already a boot-ref, then we gotta determine which vat
     * is it's target's vat. If it's target's vat is<ul> <li>the dest, then
     * return its target. <li>some other vat, then we re-wrap it in a fresh
     * boot-ref <li>(it would be an error for it to be the src vat) </ul>
     * <li>If it's a non-boot-ref eventual reference, then we wrap it in a
     * boot-ref and return that, but we also send a __whenMoreResolved message
     * to the eventual reference in the src vat, where the argument is a
     * boot-ref on a {@link DelayedRedirector} on the returned boot-ref. <li>In
     * all other cases, an exception is thrown. </ul>
     *
     * @param arg        The reference to be packaged.
     * @param src        The vat that 'arg' is valid within.
     * @param dest       The vat the return result needs to be valid within.
     * @param currentVat The vat within which we're currently executing, which
     *                   may be src, dest, or a third introducing vat (Alice).
     *                   This is used to resolve race conditions.
     */
    private Object packageArg(Object arg, Vat src, Vat dest, Vat currentVat) {
        arg = Ref.resolution(arg);
        String state = Ref.state(arg);
        arg = Ref.resolution(arg);

        if (src == dest) {
            return arg;
        }
        if (Ref.BROKEN == state) {
            return arg;
        } else if (Ref.NEAR == state) {
            if (Ref.isDeepPassByCopy(arg) /* implicit "and is thread-safe" */) {
                return arg;
            }
            if (Ref.isPassByProxy(arg)) {
                BootRefHandler handler = new BootRefHandler(src, arg);
                return handler.myResolver.getProxy();
            }
            if (arg instanceof ConstList) {
                //XXX Must handle cycles
                //XXX Should handle other PassByCopy types
                //XXX Perhaps we should abstract out arrays as a separate case?
                ConstList argList = (ConstList)arg;
                Object[] result = new Object[argList.size()];
                for (int i = 0, len = result.length; i < len; i++) {
                    result[i] =
                      packageArg(argList.get(i), src, dest, currentVat);
                }
                return ConstList.fromArray(result);
            }
            if (arg instanceof ConstMap) {
                //XXX Must handle cycles
                //XXX Should handle other PassByCopy types
                //XXX Perhaps we should abstract out arrays as a separate case?
                ConstMap argMap = (ConstMap)arg;
                ConstList keys = ConstList.fromArray(argMap.getKeys());
                keys = (ConstList)packageArg(keys, src, dest, currentVat);
                ConstList vals = ConstList.fromArray(argMap.getValues());
                vals = (ConstList)packageArg(vals, src, dest, currentVat);
                try {
                    return ConstMap.fromColumns(keys, vals);
                } catch (ArityMismatchException e) {
                    throw ExceptionMgr.asSafe(e);
                }
            }

            //XXX For now, if it isn't passable over boot comm, treat it as
            // if it was PassByProxy.
//            T.fail("Not passable over boot-comm: " +
//                   ClassDesc.simpleSig(arg.getClass()));
//            return null; //make compiler happy
            BootRefHandler handler = new BootRefHandler(src, arg);
            return handler.myResolver.getProxy();

        } else {
            T.require(Ref.EVENTUAL == state, "unrecognized state: ", state);
            BootRefHandler optHandler = getOptBootRefHandler(arg);
            if (null != optHandler) {
                // arg is a boot-ref (a ref handled by a BootRefHandler).
                if (dest == optHandler.myTargetsVat) {
                    return optHandler.myTarget;
                }
                //XXX I think the following rejection could be innocently
                //caused by a race condition, and should therefore be handled
                //rather than rejected.
                T.require(src != optHandler.myTargetsVat,
                          "Unshortened boot-ref: ",
                          arg);
                return packageArg(optHandler.myTarget,
                                  optHandler.myTargetsVat,
                                  dest,
                                  currentVat);
            }

            //arg is EVENTUAL (or was at the time of the test), and is not
            //handled by a BootRefHandler, so we treat it as a promise in the
            //src vat.

            BootRefHandler handler = new BootRefHandler(src, arg);
            DelayedRedirector rdr = new DelayedRedirector(handler.myResolver);
            //handler and rdr are in the dest vat
            Object[] args = {packageArg(rdr, dest, src, currentVat)};
            src.qSendAllOnly(arg, false, "__whenMoreResolved", args);
            //Is it ok to ignore the E.sendOnly return result here?
            return handler.myResolver.getProxy();
        }
    }

    /**
     * Given that args is packaged for use in the current vat, return a
     * corresponding list of args packaged for use in myTargetsVat.
     */
    private Object[] packageArgs(Object[] args) {
        Vat currentVat = Vat.getCurrentVat();
        Object[] result = new Object[args.length];
        for (int i = 0, len = args.length; i < len; i++) {
            result[i] =
              packageArg(args[i], currentVat, myTargetsVat, currentVat);
        }
        return result;
    }

    /**
     * Queues the send in myTargetsVat.
     *
     * @param args Assumed to be packaged for use in the current vat.
     */
    public void handleSendAllOnly(String verb, Object[] args) {
        myFreshFlag = false;
        myTargetsVat.qSendAllOnly(myTarget, false, verb, packageArgs(args));
    }

    /**
     * Queues the send in myTargetsVat.
     *
     * @param args Assumed to be packaged for use in the current vat.
     * @return the promise for the result packaged for use in the current vat.
     */
    public Ref handleSendAll(String verb, Object[] args) {
        myFreshFlag = false;
        Ref promise =
          myTargetsVat.qSendAll(myTarget, false, verb, packageArgs(args));
        Vat currentVat = Vat.getCurrentVat();
        return Ref.toRef(packageArg(promise,
                                    myTargetsVat,
                                    currentVat,
                                    currentVat));
    }

    /**
     *
     */
    public void handleResolution(Object newTarget) {
        //Do nothing
    }

    /**
     *
     */
    public void reactToGC() {
        //Do nothing
    }

    /**
     *
     */
    public boolean isFresh() {
        return myFreshFlag || null != myResolver.getOptIdentity();
    }

    /**
     *
     */
    public boolean sameConnection(Object other) {
        BootRefHandler optHandler = getOptBootRefHandler(other);
        return null != optHandler && myTargetsVat == optHandler.myTargetsVat;
    }

    /**
     * do nothing
     */
    public void mustBeDisposable() {
        // do nothing
    }
}
