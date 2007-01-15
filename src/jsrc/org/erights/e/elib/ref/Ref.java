package org.erights.e.elib.ref;

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
import org.erights.e.develop.exception.ExceptionMgr;
import org.erights.e.elib.base.Callable;
import org.erights.e.elib.base.Ejector;
import org.erights.e.elib.base.Script;
import org.erights.e.elib.base.TypeDesc;
import org.erights.e.elib.deflect.Deflector;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.prim.Message;
import org.erights.e.elib.prim.StaticMaker;
import org.erights.e.elib.sealing.Amplifiable;
import org.erights.e.elib.sealing.SealedBox;
import org.erights.e.elib.serial.DeepPassByCopy;
import org.erights.e.elib.serial.JOSSPassByConstruction;
import org.erights.e.elib.serial.PassByConstructionAuditor;
import org.erights.e.elib.serial.PassByProxy;
import org.erights.e.elib.serial.Persistent;
import org.erights.e.elib.slot.Conformable;
import org.erights.e.elib.slot.Guard;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.ConstMap;
import org.erights.e.elib.tables.Equalizer;
import org.erights.e.elib.tables.FlexList;
import org.erights.e.elib.tables.IdentityMap;
import org.erights.e.elib.tables.NotSettledException;
import org.erights.e.elib.tables.Selfless;
import org.erights.e.elib.util.OneArgFunc;
import org.erights.e.elib.vat.SendingContext;
import org.erights.e.meta.java.math.EInt;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Proxy;

/**
 * Objects that handle E eventual-send message delivery requests themselves.
 * <p/>
 * Represents a resolvable reference hopefully eventually designating some
 * object. <p>
 * <p/>
 * Because Refs are seen by E only through the Ref protocol, any public methods
 * that subclasses want to make visible should be static methods that take a
 * 'self' instance as an argument, rather than instance methods. <p>
 * <p/>
 * A Ref is often a facet of a promise. A promise is a composite consisting of
 * a Ref as arrow tail, and often a resolver of some sort as arrow head.
 * Usually, the resolver will be of type Resolver, but not necessarily.
 *
 * @author Mark S. Miller
 * @see <a href="http://www.erights.org/elib/concurrency/refmech.html"
 *      >Reference Mechanics</a>
 */
public abstract class Ref implements Callable {

    /**
     * Initialized lazily to avoid a circular initialization problem
     *
     * @noinspection StaticNonFinalField
     */
    static private StaticMaker OptRefMaker = null;

    static public StaticMaker GetRefMaker() {
        if (null == OptRefMaker) {
            //noinspection NonThreadSafeLazyInitialization
            OptRefMaker = StaticMaker.make(Ref.class);
        }
        return OptRefMaker;
    }

    static public final String NEAR = "NEAR";

    static public final String EVENTUAL = "EVENTUAL";

    static public final String BROKEN = "BROKEN";

    /**
     * The canonical reference BROKEN by TheViciousMarker. Used to detect
     * vicious forwarding cycles.
     */
    static final UnconnectedRef TheViciousRef =
      new UnconnectedRef(ViciousCycleException.TheViciousMarker);

    /**
     * Returns the two facets of a local promise -- a SwitchableRef and a
     * LocalResolver.
     * <p/>
     * The SwitchableRef starts on a BufferingRef, where the LocalResolver
     * holds the buffer.
     */
    static public Object[] promise() {
        FlexList buffer = FlexList.fromType(Message.class);
        BufferingRef bRef = new BufferingRef(buffer);
        SwitchableRef sRef = new SwitchableRef(bRef);
        Resolver resolver = new LocalResolver(sRef, buffer);
        Object[] result = {sRef, resolver};
        return result;
    }

    /**
     * Like Ref.promise(), except the resolving facet is a Switcher rather than
     * a Resolver.
     * <p/>
     * With a Switcher, the promise can be redirected repeatedly before being
     * committed.
     *
     * @deprecated Currently unused, so we may decide to retire this.
     */
    static public Object[] makeSwitchablePromise(Object target) {
        SwitchableRef sRef = new SwitchableRef(toRef(target));
        //noinspection deprecation
        Switcher switcher = new Switcher(sRef);
        Object[] result = {sRef, switcher};
        return result;
    }

    /**
     * Returns a pair of a BufferingRef and the FlexList acting as its
     * resolver.
     * <p/>
     * To make and hold both facets of a buffering promise without the extra
     * allocation, in E do: <pre>
     *     def Message := &lt;type:org.erights.e.elib.prim.Message&gt;
     *     def resolver = FlexList.fromType(Message)
     *     def ref = BufferingRef(resolver)</pre>
     * or, in Java, do: <pre>
     *     import org.erights.e.elib.prim.Message;
     *     FlexList resolver = FlexList.fromType(Message.class);
     *     BufferingRef ref = new BufferingRef(resolver);</pre>
     *
     * @deprecated Currently unused, so we may decide to retire this.
     */
    static public Object[] makeBufferingPromise() {
        FlexList resolver = FlexList.fromType(Message.class);
        BufferingRef ref = new BufferingRef(resolver);
        Object[] result = {ref, resolver};
        return result;
    }

    /**
     * Return a Ref broken allegedly because of problem.
     */
    static public Ref broken(Throwable problem) {
        return new UnconnectedRef(problem);
    }

    /**
     * If optProblem is null, then return null; else Ref.broken(optProblem)
     */
    static public Object optBroken(Throwable optProblem) {
        if (null == optProblem) {
            return null;
        } else {
            return broken(optProblem);
        }
    }

    /**
     * Return a Disconnected reference -- a broken reference with the same
     * identity as some far reference.
     * <p/>
     * 'prevRef' must currently be a far or disconnected reference. While it
     * seems sensible to allow one to make a Disconnected reference to a Near
     * object, there's no way to provide this in Ref without destroying our
     * layering -- the independence of local ELib from the CapTP layer.
     */
    static public Ref disconnected(Throwable problem, Object prevRef) {
        prevRef = resolution(prevRef);
        Object id;
        if (prevRef instanceof FarRef) {
            id = ((FarRef)prevRef).myIdentity;
        } else if (prevRef instanceof DisconnectedRef) {
            id = ((DisconnectedRef)prevRef).myIdentity;
        } else {
            T.fail("must be far or disconnected " + prevRef);
            return null; //make compiler happy
        }
        return new DisconnectedRef(problem, id);
    }

    /**
     * Does this reference designate an object in this vat?
     * <p/>
     * <tt>isNear/1</tt> must be thread safe, in order for {@link
     * #isDeepPassByCopy(Object,IdentityMap) Ref.isDeepPassByCopy/2} to be
     * thread safe.
     *
     * @see #state(Object)
     */
    static public boolean isNear(Object ref) {
        if (ref instanceof Ref) {
            return NEAR == ((Ref)ref).state();
        } else {
            return true;
        }
    }

    /**
     * Does this reference support eventual sends but not immediate calls?
     *
     * @see #state(Object)
     */
    static public boolean isEventual(Object ref) {
        if (ref instanceof Ref) {
            return EVENTUAL == ((Ref)ref).state();
        } else {
            return false;
        }
    }

    /**
     * Is this reference known never to be able to deliver messages?
     * <p/>
     * <tt>isBroken/1</tt> must be thread safe, in order for {-@link
     * org.erights.e.elib.vat.BootRefHandler#packageArg(Object,
     * org.erights.e.elib.vat.Vat, org.erights.e.elib.vat.Vat,
     * org.erights.e.elib.vat.Vat) BootRefHandler.packageArg/4} to be thread
     * safe.
     *
     * @see #state(Object)
     */
    static public boolean isBroken(Object ref) {
        if (ref instanceof Ref) {
            return BROKEN == ((Ref)ref).state();
        } else {
            return false;
        }
    }

    /**
     * If isBroken(ref), this returns the alleged reason why it's broken;
     * otherwise null.
     */
    static public Throwable optProblem(Object ref) {
        if (ref instanceof Ref) {
            return ((Ref)ref).optProblem();
        } else {
            return null;
        }
    }

    /**
     * One of {@link #EVENTUAL}, {@link #NEAR}, or {@link #BROKEN}.
     * <p/>
     * An EVENTUAL reference is unreliably invokable by E.send() -- ie, the
     * message may or may not get delivered to the designated recipient).
     * However, it will deliver messages reliably and in order until it fails.
     * Should it fail, it will eventually become broken.
     * <p/>
     * A NEAR reference is reliably invokable by E.send*() and E.call*() (and
     * possibly directly by Java's "."). The object is synchronously callable
     * and therefore within the same concurrency and atomic failure unit -- the
     * same vat.
     * <p/>
     * A BROKEN reference is one that fails to designate an object. It has an
     * associated Throwable that explains what problem resulted in this
     * condition.
     * <p/>
     * A Ref may be in any of these three states. A non-Ref is equivalent to a
     * NEAR Ref. An EVENTUAL Ref may become NEAR or BROKEN. A NEAR Ref must
     * forever remain NEAR, and a BROKEN Ref must forever remain BROKEN.
     * <p/>
     * <tt>state/1</tt> must be thread safe, in order for {-@link
     * org.erights.e.elib.vat.BootRefHandler#packageArg(Object,
     * org.erights.e.elib.vat.Vat, org.erights.e.elib.vat.Vat,
     * org.erights.e.elib.vat.Vat) BootRefHandler.packageArg/4} to be thread
     * safe.
     *
     * @see <a href= "http://www.erights.org/elib/concurrency/refmech.html"
     *      >Reference Mechanics</a>
     */
    static public String state(Object ref) {
        if (ref instanceof Ref) {
            return ((Ref)ref).state();
        } else {
            return NEAR;
        }
    }

    /**
     * User-means of Ref shortening.
     * <p/>
     * If isEventual(ref) or isBroken(ref), this returns ref (or a Ref
     * equivalent to ref but possibly more efficient).
     * <p/>
     * If isNear(ref), this returns a non-Ref. The resolution of a NEAR
     * reference may be called by both java dot and E.call*(), even if the
     * original was only callable by E.call*(). Non-Refs return themselves.
     * <p/>
     * First unwraps deflections.
     * <p/>
     * <tt>resolution/1</tt> must be thread safe, in order for {-@link
     * org.erights.e.elib.vat.BootRefHandler#packageArg(Object,
     * org.erights.e.elib.vat.Vat, org.erights.e.elib.vat.Vat,
     * org.erights.e.elib.vat.Vat) BootRefHandler.packageArg/4} to be thread
     * safe.
     */
    static public Object resolution(Object ref) {
        while (true) {
            if (null == ref) {
                return null;
            }
            if (!(ref instanceof Callable)) {
                // We should typically exit here the first time.
                return ref;
            }
            if (!(ref instanceof Proxy)) {
                break;
            }
            Callable optDeflected = Deflector.getOptDeflected((Proxy)ref);
            if (null == optDeflected) {
                break;
            }
            ref = optDeflected;
        }
        if (ref instanceof Ref) {
            return ((Ref)ref).resolution();
        } else {
            return ref;
        }
    }

    /**
     * If ref is fulfilled, return it (in resolved form), otherwise throw.
     * <p/>
     * If it's broken, throw the problem. If its not resolved, throw a
     * complaint to that effect.
     * <p/>
     * XXX To be used in a more compact expansion of when-catch.
     */
    static public Object fulfillment(Object ref) {
        ref = resolution(ref);
        Throwable optProb = optProblem(ref);
        if (isResolved(ref)) {
            if (null == optProb) {
                return ref;
            } else {
                throw ExceptionMgr.asSafe(optProb);
            }
        } else {
            T.fail("Not resolved: " + ref);
            return null; // make compiler happy
        }
    }

    /**
     * Return an object that's E-equivanetlt to target, but has Java-type
     * Callable.
     */
    static public Callable toCallable(Object target) {
        if (null != target && target instanceof Callable) {
            return (Callable)target;
        } else {
            return new NearRef(target);
        }
    }

    /**
     * Return an object that's E-equivanetlt to target, but has Java-type Ref.
     */
    static public Ref toRef(Object target) {
        if (null != target && target instanceof Ref) {
            return (Ref)target;
        } else {
            return new NearRef(target);
        }
    }

    /**
     * A reference isResolved when it is known what object it designates. <p>
     * <p/>
     * NEAR and BROKEN references are necessarily resolved. A reference that
     * isn't resolved is a promise, which is necessarily EVENTUAL. A resolved
     * EVENTUAL reference is a Far reference.
     *
     * @see <a href= "http://www.erights.org/elib/concurrency/refmech.html"
     *      >Reference Mechanics</a>
     */
    static public boolean isResolved(Object ref) {
        if (ref instanceof Ref) {
            return ((Ref)ref).isResolved();
        } else {
            //non-Refs are always resolved, though they may not be settled.
            return true;
        }
    }

    /**
     * Is 'ref' both eventual and resolved?
     */
    static public boolean isFar(Object ref) {
        return isEventual(ref) && isResolved(ref);
    }

    /**
     * A reference is settled when it has a fully determined identity.
     * <p/>
     * If x and y are settled references, then the E language's 'x == y' (which
     * expands to 'Ref same(x, y)' must yield either true or false. Whereas if
     * either or both are unsettled, then 'x == y' may instead throw a
     * NotSettledException since it cannot yet be determined whether further
     * settling will cause the same question to yield true or false.
     * <p/>
     * Only settled references may be used as keys in EMaps (hashtables), since
     * only from a fully determined identity is there enough information to
     * (internal to the E implementation) calculate a hash.
     * <p/>
     * Settled references are a subset of Resolved references -- unresolved
     * references (Promises) are necessarily unsettled. Objects with
     * object-creation identity are "Selfish" (they have a "self"). Those
     * without are "Selfless". For objects defined in the E language, only
     * PassByCopy are Selfless, all others are Selfish (and implicitly
     * PassByProxy). Near and Far references to Selfish objects are settled.
     * Broken references are settled. When a Far reference breaks, the
     * corresponding Broken reference retains the settled identity of its Far
     * reference, since identity must be stable. Other Broken references are
     * the same iff their problems are the same.
     * <p/>
     * Far references cannot point at Selfless objects (although RemotePromises
     * can, as a transient state). Therefore, the only case left is a Near
     * reference to a Selfless object. A Near reference to a Selfless object is
     * settled iff the Selfless object is settled. Just as two Selfless objects
     * are the same iff they are of the same types are their parts are
     * recursively the same, a Selfless object is settled when all its parts
     * are recursively settled. For example,
     * <pre>    [x, y]</pre>
     * is settled iff x and y are both settled.
     * <p/>
     * In the recursive defintions of both sameness and settledness, cycles are
     * fine. For example,
     * <pre>    def a := ["left", a, "right"]</pre>
     * defines an infinite (cyclic) settled tree.
     */
    static public boolean isSettled(Object ref) {
        return Equalizer.isSettled(ref);
    }

    /**
     * A Selfless object only has value-based sameness.
     * <p/>
     * Selfless objects are immutable, and compare for sameness based only on
     * their type and state. As a result, an E implementation can transparently
     * copy or merge copies of Selfless objects at will, with no effect visible
     * from the E language. (XXX talk about rational tree comparison.)
     * <p/>
     * isSelfless of a non-NEAR reference is false. This is a bit weird for
     * FarRef, and DisconnectedRef, as they are listed (for implementation
     * reasons) as Selfless objects (either actual or HONORARY).
     *
     * @see Selfless
     */
    static public boolean isSelfless(Object ref) {
        ref = resolution(ref);
        if (!isNear(ref)) {
            return false;
        }
        if (null == ref) {
            return true;
        }
        Class clazz = ref.getClass();
        if (clazz.isArray()) {
            return true;
        }
        if (Selfless.class.isAssignableFrom(clazz) ||
          Selfless.HONORARY.has(clazz)) {
            if (PassByProxy.class.isAssignableFrom(clazz) ||
              PassByProxy.HONORARY.has(clazz)) {

                T.fail("Can't be Selfless & PassByProxy: " + clazz);
            }
            return true;
        }
        return false;
    }

    /**
     * A NEAR reference that isn't Selfless is Selfish, and designates a
     * Selfish object. <p>
     * <p/>
     * Selfish objects have creation-identity, ie, normal EQness. For Selfish
     * objects, Java's "x==y" and E's "x==y" (ie, ELib's "E.same(x,y)") agree.
     * <p/>
     * <p/>
     * isSelfish of a non-NEAR reference is false. This is a bit wierd for
     * FarRef and DisconnectedRef, as they both have the creation identity of
     * the object they (originally) designate(d). However, for these, E's
     * "x==y" does not agree with Java's "x==y".
     */
    static public boolean isSelfish(Object ref) {
        return isNear(ref) && !isSelfless(ref);
    }

    /**
     * Would ref be successfully coerced by PassByProxyGuard?
     * <p/>
     * A PassByProxy object is passed between vats by creating a FarRef for it
     * on the remote end that forwards back to the original. PassByProxy
     * objects must be Selfish, and only exist in their hosting vat.
     * <p/>
     * It's safe to provide this test, since PassByProxyGuard's coerce() either
     * returns ref or fails. Ie, it doesn't actually coerce.
     * <p/>
     * isPassByProxy of a non-NEAR reference is false.
     * <p/>
     * <tt>isPassByProxy/1</tt> must be thread safe, in order for {-@link
     * org.erights.e.elib.vat.BootRefHandler#packageArg(Object,
     * org.erights.e.elib.vat.Vat, org.erights.e.elib.vat.Vat,
     * org.erights.e.elib.vat.Vat) BootRefHandler.packageArg/4} to be thread
     * safe.
     */
    static public boolean isPassByProxy(Object ref) {
        ref = resolution(ref);
        if (!isNear(ref)) {
            return false;
        }
        if (null == ref) {
            return false;
        }
        Class clazz = ref.getClass();
        if (PassByProxy.class.isAssignableFrom(clazz) ||
          PassByProxy.HONORARY.has(clazz)) {
            if (Selfless.class.isAssignableFrom(clazz) ||
              Selfless.HONORARY.has(clazz)) {

                T.fail("Can't be PassByProxy & Selfless: " + clazz);
            }
            return true;
        }
        // XXX Should return false at this point, but for convenience, we
        // treat all non-Selfless non-PBC objects as PassByProxy for now.
//        return false;
        return !Selfless.class.isAssignableFrom(clazz) &&
          !Selfless.HONORARY.has(clazz) && !isPBC(ref);
    }

    /**
     * Would ref be successfully coerced by PassByConstructionAuditor?
     * <p/>
     * PassByConstruction objects are passed between vats by constructing a new
     * object in the destination vat to serve as its representative. We often
     * speak of the original and each of its representatives individually as
     * presences, and the conceptual object that they all jointly represent as
     * an Unum. The most common and most trivial case of PassByConstruction is
     * PassByCopy, in which case each presence is a Selfess, transparent,
     * immutable copy of each other.
     * <p/>
     * It's mostly safe to provide this test, since PassByConstructionAuditor's
     * coerce() either returns ref or fails. Ie, it mostly doesn't coerce. The
     * exception is that coerce() will coerce an Array to a ConstList, which
     * should be transparent to the E language programmer.
     * <p/>
     * isPBC on a non-NEAR reference is false, which is a bit wierd for
     * UnconnectedRef and DisconnectedRef, as (for implementation reasons) they
     * are listed as PassByConstruction.
     */
    static public boolean isPBC(Object ref) {
        // XXX consider extracting this procedure for guard testing
        Ejector ej = new Ejector("isPBC failure");
        try {
            Object coerced = PassByConstructionAuditor.THE_ONE.coerce(ref, ej);
            return Equalizer.isSameYet(ref, coerced);
        } catch (Throwable t) {
            ej.result(t);
            return false;
        } finally {
            ej.disable();
        }

    }

    /**
     * Returns whether the reference is near and suitable for
     * pass-by-construction using Java serialization.
     */
    public static boolean isJOSSPBC(Object ref) {
        ref = resolution(ref);
        if (!isNear(ref)) {
            return false;
        }
        return isJOSSPBCRef(ref);
    }

    /**
     * allows non-near, for use by CapTPReplacer
     */
    public static boolean isJOSSPBCRef(Object ref) {
        if (null == ref) {
            //null is trivially PassByCopy
            return true;
        }
        Class clazz = ref.getClass();
        if (JOSSPassByConstruction.class.isAssignableFrom(clazz)) {
            return true;
        }
        if (clazz.isArray()) {
            //Pretend that arrays are ConstLists.
            return true;
        }
        return JOSSPassByConstruction.HONORARY.has(clazz);
    }

    /**
     * If it's both PBC and Selfless
     */
    static public boolean isPassByCopy(Object ref) {
        return isPBC(ref) && isSelfless(ref);
    }

    /**
     * Would ref be successfully coerced by DeepPassByCopyAuditor?
     * <p/>
     * It's mostly safe to provide this test, since DeepPassByCopyAuditor's
     * coerce() either returns ref or fails. Ie, it mostly doesn't coerce. The
     * exception is that coerce() will coerce an array to a ConstList, which
     * should be transparent to the E language programmer.
     * <p/>
     * isDeepPassByCopy on a non-NEAR reference is false, which is a bit wierd
     * for UnconnectedRef and DisconnectedRef.
     * <p/>
     * Of all PassByCopy data types where an instance might be considered
     * DeepPassByCopy based on a dynamic check of its contents, we currently
     * only perform such a dynamic check for arrays and ConstLists. XXX should
     * we instead check ConstMaps and ConstSets as well?  Or perhaps all
     * PassByCopy objects, since they are all transparent, so there should be a
     * reliable generic way to enumerate all their contents?
     * <p/>
     * <tt>isDeepPassByCopy/1</tt> must be thread safe, in order for {-@link
     * org.erights.e.elib.vat.BootRefHandler#packageArg(Object,
     * org.erights.e.elib.vat.Vat, org.erights.e.elib.vat.Vat,
     * org.erights.e.elib.vat.Vat) BootRefHandler.packageArg/4} to be thread
     * safe.
     */
    static public boolean isDeepPassByCopy(Object ref) {
        return isDeepPassByCopy(ref, null);
    }

    /**
     * Same as {@link #isDeepPassByCopy(Object)}.
     */
    static public boolean isData(Object ref) {
        return isDeepPassByCopy(ref);
    }

    /**
     * XXX Currently just delegates to isDeepPassByCopy, which will make errors
     * of omission (the safe kind).
     */
    static public boolean isDeepFrozen(Object ref) {
        return isDeepPassByCopy(ref, null);
    }

    /**
     * Are instances of clazz necessarily DeepPassByCopy?
     * <p/>
     * <tt>isDeepPassByCopyClass/1</tt> must be thread safe, in order for
     * {@link #isDeepPassByCopy(Object,IdentityMap) Ref.isDeepPassByCopy/2} to
     * be thread safe.
     */
    static private boolean isDeepPassByCopyClass(Class clazz) {
        if (DeepPassByCopy.class.isAssignableFrom(clazz)) {
            return true;
        }
        return DeepPassByCopy.HONORARY.has(clazz);
    }

    /**
     * We move the work to the two argument form so it can break cycles.
     * <p/>
     * <tt>isDeepPassByCopy/2</tt> must be thread safe, in order for {@link
     * #isDeepPassByCopy(Object) Ref.isDeepPassByCopy/1} to be thread safe:
     * Note that optSoFar will always start as null, and therefore will only
     * ever hold a thread-specific map, and so need not itself be thread safe.
     */
    static private boolean isDeepPassByCopy(Object ref, IdentityMap optSofar) {
        ref = resolution(ref);
        if (!isNear(ref)) {
            return false;
        }
        if (null == ref) {
            //null is trivially DeepPassByCopy
            return true;
        }
        Class clazz = ref.getClass();
        if (isDeepPassByCopyClass(clazz)) {
            return true;
        }
        if (clazz.isArray()) {
            //We assume that any arrays we're given are being treated as
            //immutable: both sematantically immutable and implementationally
            //thread safe. XXX we must re-tame the Java API to ensure that no
            //E code may cause an array to be mutated.
            if (isDeepPassByCopyClass(clazz.getComponentType())) {
                return true;
            }
            //If the componentType doesn't guarantee DeepPassByCopy, we check
            //the instances. For the ELib programmer, there's no need to be
            //this forgiving, but the E language programmer normally isn't
            //aware that it's even possible to constrain the types of lists.
            if (null == optSofar) {
                optSofar = new IdentityMap();
            } else if (optSofar.maps(ref)) {
                return true;
            }
            optSofar.put(ref, null, true);
            try {
                for (int i = 0, len = Array.getLength(ref); i < len; i++) {
                    if (!isDeepPassByCopy(Array.get(ref, i), optSofar)) {
                        return false;
                    }
                }
                return true;
            } finally {
                optSofar.removeKey(ref, true);
            }
        }
        if (ref instanceof Selfless && ref instanceof JOSSPassByConstruction) {
            //It's PassByCopy and transparent. Since it's transparent, we can
            //just look at the state to see if they're all DeepPassByCopy.
            if (ref instanceof ConstList &&
              isDeepPassByCopyClass(((ConstList)ref).valueType())) {
                //A useful semi-static special case
                return true;
            }
            if (ref instanceof ConstMap &&
              isDeepPassByCopyClass(((ConstMap)ref).keyType()) &&
              isDeepPassByCopyClass(((ConstMap)ref).valueType())) {
                //A useful semi-static special case
                return true;
            }

            if (null == optSofar) {
                optSofar = new IdentityMap();
            } else if (optSofar.maps(ref)) {
                return true;
            }
            optSofar.put(ref, null, true);
            try {
                Object[] elements = ((Selfless)ref).getSpreadUncall();
                int len = elements.length;
                if (1 <= len) {
                    //We count on the standard convention to only put a maker
                    //in position 0 when it represents the object's behavior,
                    //not its state.
                    Object maker = elements[0];
                    if (!(maker instanceof StaticMaker) &&
                      !isDeepPassByCopy(maker, optSofar)) {

                        return false;
                    }
                }
                for (int i = 1; i < len; i++) {
                    if (!isDeepPassByCopy(elements[i], optSofar)) {
                        return false;
                    }
                }
                return true;
            } finally {
                optSofar.removeKey(ref, true);
            }
        }
        return false;
    }

    /**
     * Can this reference be saved and restored to a checkpoint for purposes of
     * persistence.
     * <p/>
     * The cases are <ul> <li>Broken references are persistent.
     * <li><tt>null</tt> is persistent. <li>Instances of {@link Persistent}
     * (including {@link StemCell}s) are persistent. <li>Arrays are persistent.
     * <li>Instances of any {@link Persistent#HONORARY} classes are persistent.
     * <li>A Far reference (a Resolved Eventual reference) is not persistent,
     * but is saved as a DisconnectedRef (a kind of BrokenRef) that maintains
     * the far reference's sameness identity. <li>All other non-persistent
     * references, including all non-StemCell Promises (Unresolved references)
     * are saved as UnconnectedRefs (Broken references without a sameness
     * identity). <ul>
     */
    static public boolean isPersistent(Object ref) {
        ref = resolution(ref);
        if (isEventual(ref)) {
            return ref instanceof StemCell;
        }
        if (null == ref) {
            //null is trivially Persistent
            return true;
        }
        Class clazz = ref.getClass();
        if (Persistent.class.isAssignableFrom(clazz)) {
            return true;
        }
        if (clazz.isArray()) {
            //Because we try to pretend that arrays are ConstLists, we treat
            //them as Persistent.
            return true;
        }
        return Persistent.HONORARY.has(clazz);
    }

    /**
     * If two object references are the same(), they are indistinguishable up
     * to brokeness XXX no longer true, changed to designational equivalence,
     * need to write up. <p>
     * <p/>
     * The E language's "x == y" expands to "Ref.isSameEver(x, y)". <p>
     * <p/>
     * If (x == y && Ref optProblem(x) == Ref optProblem(y)) then substituting
     * one for the other must be semantically transparent XXX no longer true,
     * changed to designational equivalence, need to write up. See <a
     * href="http://www.erights.org/elang/same-ref.html"> "When Are Two Things
     * the <i>Same</i>?"</a>. <p>
     * <p/>
     * Sameness is stable, but it is only total among settled references. If 'x
     * == y' yield true or false then the same comparison must forever
     * afterwards yield the same answer. However, if either or both are
     * unsettled, it may throw NotSettledException instead.
     * <p/>
     * Throws a NotSettledException if left and right are not yet settled
     * enough to determine whether they will designate the same settled
     * identity.
     */
    static public boolean isSameEver(Object left, Object right) {
        try {
            return Equalizer.isSameEver(left, right);
        } catch (NotSettledException e) {
            throw ExceptionMgr.asSafe(e);
        }
    }

    /**
     * Used for the when-catch construct.
     * <p/>
     * If ref never becomes resolved, the reactor is not invoked. Should ref
     * become resolved, the reactor will be invoked exactly once. For example,
     * if ref becomes fulfilled and then broken, the reactor will hear of
     * exactly one of these events.
     * <p/>
     * Once ref becomes resolved the reactor will be invoked with the
     * resolution. Should the reactor be invoked with a non-broken value (and
     * therefore a fulfilled value), all earlier messages sent on ref before
     * the whenResolved are guaranteed to have been successfully delivered.
     *
     * @return A promise that will resolve to the outcome of calling the
     *         reactor, as explained <a href= "http://www.eros-os.org/pipermail/e-lang/2001-August/005638.html"
     *         >here</a>.
     * @see #whenBroken(Object,OneArgFunc)
     */
    static public Object whenResolved(Object ref, OneArgFunc reactor) {
        Object[] pair = promise();
        WhenResolvedReactor wrapper =
          new WhenResolvedReactor(reactor, ref, (Resolver)pair[1]);
        Throwable optProblem = E.sendOnly(ref, "__whenMoreResolved", wrapper);
        if (null == optProblem) {
            return pair[0];
        } else {
            return broken(optProblem);
        }
    }

    /**
     * Like {@link #whenResolved(Object,OneArgFunc)} but without a conventional
     * return result.
     *
     * @return Why wasn't the __whenMoreResolved/1 queued?  It isn't queued if
     *         this vat or comm connection is shut down, in which case the
     *         returned problem explains why. If null is returned, then the
     *         event was queued, though it may still not arrive.
     */
    static public Throwable whenResolvedOnly(Object ref, OneArgFunc reactor) {
        return E.sendOnly(ref,
                          "__whenMoreResolved",
                          new WhenResolvedReactor(reactor, ref, null));
    }

    /**
     * Should a breakable reference <i>ever</i> become broken, even if it
     * became fulfilled in the meantime, then the reactor is invoked once with
     * a broken reference.
     * <p/>
     * A vat-crossing reference that gets garbage collected before a partition
     * is not broken by that partition, and therefore doesn't need to inform
     * the reactor. Once a reference is logically garbage (unreachable), it may
     * or may not have been collected yet, and so may or may not inform the
     * reactor of partitions that happen after it is garbage.
     *
     * @return A promise that will resolve to the outcome of calling the
     *         reactor, as explained <a href= "http://www.eros-os.org/pipermail/e-lang/2001-August/005638.html"
     *         >here</a>. Note that, if ref becomes near, it'll never break, so
     *         the returned promise will never be resolved!
     * @see #whenResolved(Object,OneArgFunc)
     */
    static public Object whenBroken(Object ref, OneArgFunc reactor) {
        Object[] pair = promise();
        WhenBrokenReactor wrapper =
          new WhenBrokenReactor(reactor, ref, (Resolver)pair[1]);
        Throwable optProblem = E.sendOnly(ref, "__whenBroken", wrapper);
        if (null == optProblem) {
            return pair[0];
        } else {
            return broken(optProblem);
        }
    }

    /**
     * Like {@link #whenBroken(Object,OneArgFunc)} but without a conventional
     * return result.
     *
     * @return Why wasn't the __whenBroken/1 queued?  It isn't queued if this
     *         vat or comm connection is shut down, in which case the returned
     *         problem explains why. If null is returned, then the event was
     *         queued, though it may still not arrive.
     */
    static public Throwable whenBrokenOnly(Object ref, OneArgFunc reactor) {
        return E.sendOnly(ref,
                          "__whenBroken",
                          new WhenBrokenReactor(reactor, ref, null));
    }

    /**
     * Use Ref.optProblem(obj) rather than obj.optProblem().
     * <p/>
     * When we're BROKEN, our subclass must return a non-null problem. When
     * we're not BROKEN, our subclass must return null.
     * <p/>
     * All implementations of <tt>optProblem/0</tt> must be thread safe, in
     * order for {@link Ref#state() Ref.state/0} to be thread safe.
     *
     * @see #optProblem(Object)
     */
    public abstract Throwable optProblem();

    /**
     * Used to implement resolution(), and for internal use in the promise
     * package. Our subclass must either provide 'this', or a Ref which is
     * equivalent but less indirect. If the resolutionRef() is 'this', then our
     * state() must be BROKEN or EVENTUAL.
     * <p/>
     * All implementations of <tt>resolutionRef/0</tt> must be thread safe, in
     * order for {@link Ref#resolution() Ref.resolution/0} to be thread safe.
     */
    abstract Ref resolutionRef();

    /**
     * Use {@link Ref#resolution(Object) Ref.resolution/1} rather than
     * obj.resolution().
     * <p/>
     * Our subclass must either provide 'this', or an object which is
     * equivalent but less indirect. If the resolution() is 'this', then our
     * state() must be BROKEN or EVENTUAL. However, subclasses will typically
     * only override resolutionRef(). The only difference between the two is on
     * a NearRef.
     * <p/>
     * All implementations of <tt>resolution/0</tt> must be thread safe, in
     * order for {@link Ref#resolution(Object) Ref.resolution/1} to be thread
     * safe.
     */
    public Object resolution() {
        Ref result = resolutionRef();
        if (this == result) {
            //base case
            return this;
        } else {
            return result.resolution();
        }
    }

    /**
     * Use Ref.state(obj) rather than obj.state(). Our subclass *may* override
     * this, but only to return NEAR, EVENTUAL, or BROKEN consistent with the
     * above descriptions. The default behavior provided here is defined in
     * terms of optProblem() and resolutionRef().
     * <p/>
     * All implementations of <tt>state/0</tt> must be thread safe, in order
     * for {@link Ref#isNear(Object) Ref.isNear/1} to be thread safe: The
     * default implementation here is inductively thread safe even though it
     * doesn't synchronize, in that it's made only from thread safe parts used
     * together in a monotonic fashion.
     *
     * @see #state(Object)
     */
    public String state() {
        if (null != optProblem()) {
            return BROKEN;
        }
        Ref target = resolutionRef();
        if (target == this) {
            return EVENTUAL;
        } else {
            return target.state();
        }
    }

    /**
     * Return null
     */
    public Script optShorten(String verb, int arity) {
        return null;
    }

    /**
     * Use E.callAll(obj, verb, args) rather than obj.callAll(verb, args).
     */
    public abstract Object callAll(String verb, Object[] args);

    /**
     * Eventually sends a packaged message to this object.
     * <p/>
     * To the client, this has the same semantics as {@link #sendAll} or {@link
     * #sendAllOnly}, and the default implementation here in Ref just delegates
     * to these. However, those subclasses that can reuse the Message should do
     * so as a nice optimization, and to preserve the {@link SendingContext}
     * info captured in <tt>msg</tt> for causality tracing and debugging.
     * <p/>
     * XXX SECURITY ALERT, No longer true:<br> This is package scope, since
     * only trusted code is assumed to not resuse a Resolver so as to break
     * distributed transparency. (This safeguard isn't crucial, and isn't even
     * a big deal, but is nice.)
     */
    public void sendMsg(Message msg) {
        Resolver optResolver = msg.getOptResolver();
        if (null == optResolver) {
            sendAllOnly(msg.getVerb(), msg.getArgs());
        } else {
            optResolver.resolve(sendAll(msg.getVerb(), msg.getArgs()));
        }
    }

    /**
     * Use E.sendAll(obj, verb, args) rather than obj.sendAll(verb, args).
     */
    public abstract Ref sendAll(String verb, Object[] args);

    /**
     * Use E.sendAllOnly(obj, verb, args) rather than obj.sendAllOnly(verb,
     * args).
     *
     * @return Why wasn't this event queued?  It isn't queued if this vat or
     *         comm connection is shut down, in which case the returned problem
     *         explains why. If null is returned, then the event was queued,
     *         though it may still not arrive.
     */
    public abstract Throwable sendAllOnly(String verb, Object[] args);

    /**
     *
     */
    public abstract boolean isResolved();

    /**
     * If this is a handled EProxy whose handler is an instance of
     * handlerClass, then return that handler; else null.
     * <p/>
     * This is for internal use only. As with other instance methods of Ref it
     * is effectively suppressed for taming by virtue of Ref being a kind of
     * Callable.
     * <p/>
     * All implementations of <tt>getOptProxyHandler/1</tt> must be thread
     * safe, in order for {-@link BootRefHandler#getOptBootRefHandler(Object)
     * BootRefHandler.getOptBootRefHandler/1} to be thread safe: The default
     * implementation here merely returns null.
     */
    public EProxyHandler getOptProxyHandler(Class handlerClass) {
        return null;
    }

    /**
     *
     */
    public TypeDesc getAllegedType() {
        return (TypeDesc)callAll("__getAllegedType", E.NO_ARGS);
    }

    /**
     *
     */
    public boolean respondsTo(String verb, int arity) {
        Object[] args = {verb, EInt.valueOf(arity)};
        return ((Boolean)callAll("__respondsTo", args)).booleanValue();
    }

    /**
     * This default implemetation currently does nothing
     */
    public void ignore() {
    }

    /**
     * Used by a resolvers to change the target. If newTarget is equivalent to
     * this Ref, then this Ref becomes broken by a ViciousCycleException.
     */
    abstract void setTarget(Ref newTarget);

    /**
     * Used by a resolvers to turn off switchability, and thereby make this Ref
     * equivalent to its current target.
     * <p/>
     * If the current target is already equivalent to this Ref, then this Ref
     * becomes broken by a {@link ViciousCycleException}.
     */
    abstract void commit();

    /**
     * If the ref has anything to give to one who has the unsealer for the
     * provided brand, ask it to return a SealedBox containing that payload.
     * <p/>
     * That SealedBox should, of course, be sealed by the sealer for that
     * brand, but that's up to the ref. If ref has nothing to give to one who
     * has the unsealer for this brand, return null.
     */
    static public SealedBox optSealedDispatch(Object ref, Object brand) {
        ref = resolution(ref);
        if (ref != null && ref instanceof Amplifiable) {
            return ((Amplifiable)ref).__optSealedDispatch(brand);
        } else {
            // typically goes to Miranda.
            Object box = E.call(ref, "__optSealedDispatch", brand);
            return (SealedBox)E.as(box, SealedBox.class);
        }
    }

    /**
     *
     */
    static public Object conformTo(Object ref, Guard guard) {
        ref = resolution(ref);
        if (null != ref && ref instanceof Conformable) {
            return ((Conformable)ref).__conformTo(guard);
        } else {
            // typically goes to Miranda.
            return E.call(ref, "__conformTo", guard);
        }
    }

    /**
     *
     */
    public String toString() {
        return E.toString(this);
    }

    /**
     *
     */
    public void mirandaPrintOn(TextWriter out) throws IOException {
        // This should never get called
        out.print("<a Ref>");
    }
}
