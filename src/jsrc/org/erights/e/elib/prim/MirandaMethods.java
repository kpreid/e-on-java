package org.erights.e.elib.prim;

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
import org.erights.e.develop.exception.ThrowableSugar;
import org.erights.e.develop.format.StringHelper;
import org.erights.e.elib.base.Callable;
import org.erights.e.elib.base.ClassDesc;
import org.erights.e.elib.base.Script;
import org.erights.e.elib.base.TypeDesc;
import org.erights.e.elib.oldeio.EPrintable;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.ref.Ref;
import org.erights.e.elib.sealing.Amplifiable;
import org.erights.e.elib.sealing.SealedBox;
import org.erights.e.elib.slot.Conformable;
import org.erights.e.elib.slot.Guard;
import org.erights.e.elib.slot.Slot;
import org.erights.e.elib.tables.ArrayHelper;
import org.erights.e.elib.tables.Equalizer;
import org.erights.e.elib.tables.Selfless;
import org.erights.e.elib.util.OneArgFunc;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * A sweetener defining default behavior for messages that may be e-called or
 * e-sent to any methodical object.
 * <p/>
 * These methods are not to be invoked directly, as they only apply when the
 * object does not override them. For all these, one can invoke them safely
 * using {@link E#callAll}. Individual methods may explain how they may be
 * invoked safely and more efficiently from Java.
 * <p/>
 * These methods apply directly to null or a "new Object()". Therefore, they
 * must work when self is null.
 *
 * @author Mark S. Miller
 */
public class MirandaMethods {

    /**
     *
     */
    static private final Class[] NO_CLASSES = {};

    /**
     * prevents instantiation
     */
    private MirandaMethods() {
    }

    /**
     * For sending messages contingent on an earier success.
     * <p/>
     * This method does the equivalent of
     * <pre>
     *     to __order(self, nestedVerb, nestedArgs) :any {
     *         [E.call(self, nestedVerb, nestedArg), self]
     *     }
     * </pre>
     * In other words, it calls the receiving object with the message
     * <tt>nestedVerb(nestedArgs...)</tt>, and, if successful, returns a pair
     * of the result of this call and the receiving object itself.
     * <p/>
     * What is this for?  Consider the client code fragment
     * <pre>
     *      databaseRcvr <- put(key1, value1)
     *      def value2Vow := databaseRcvr <- get(key2)
     * </pre>
     * E's <a href="http://www.erights.org/elib/concurrency/partial-order.html"
     * >partial ordering semantics</a> ensure that put will be delivered before
     * get is delivered. That is often good enough. But it is a weaker
     * guarantee than that provided by the following sequential code
     * <pre>
     *      database put(key1, value1)
     *      def value2Vow := database get(key2)
     * </pre>
     * In this code, not only will get only happen after put is delivered, get
     * will only happen after put <i>succeeds</i>. If put instead throws an
     * exception, the get will never happen. Often we want this effect. How can
     * we acheive this with eventual-sends to eventual references?
     * <p/>
     * When one wants to take an action contingent on the results of a previous
     * action, the conventional E answer is to use a when-catch-finally
     * expression
     * <pre>
     *     def ackVow := databaseRcvr <- put(key1, value1)
     *     def value2Vow := when (ackVow) -> done(_) :any {
     *         databaseRcvr <- get(key2)
     *     } catch problem {
     *         throw(problem)
     *     }
     * </pre>
     * This is fine, as is probably the solution to be used by default for this
     * situation. However, it does force a round-trip between the get and put,
     * and so loses the benefits of pipelining. Using the __order message, we
     * can get contingent execution + pipelining, at some cost in obscurity.
     * (Note: often, the cost of obscurity will dominate)
     * <pre>
     *     def pairVow := databaseRcvr <- __order("put", [key1, value1])
     *     # If put's return value were interesting, we'd 'pairVow <- get(0)'
     *     def newDBRcvr := pairVow <- get(1)
     *     def value2Vow := newDBRcvr <- get(key2)
     * </pre>
     * If put throws, then pairVow will resolve directly to broken, so newDB
     * will likewise resolve to broken, as will value2Vow
     */
    static public Object[] __order(Object self,
                                   String nestedVerb,
                                   Object[] nestedArgs) {
        Object[] result = {E.callAll(self, nestedVerb, nestedArgs), self};
        return result;
    }

    /**
     * This should return either <tt>null</tt> or a triple describing a call
     * that, if performed, will create an object resembling this one.
     * <p/>
     * Scalars (ints, float64s, chars, boolean, null) and bare Strings are
     * <i>atomic</i>. <tt>__optUncall</tt> on an atomic objects return null,
     * but atomic objects are still considered transparent. Objects which
     * return non-null are <i>non-atomic</i> and <i>transparent</i>. Non-atomic
     * objects that return null are <i>opaque</i>. Opaque objects may be
     * <i>selectively transparent</i> to certain clients by implementing {@link
     * #__optSealedDispatch} as described there.
     * <p/>
     * When a transpatent non-atomic object is {@link Selfless}, then the
     * result of <tt>__optUncall</tt> is guaranteed to be accurate: It
     * describes a call that, when performed, must result in this very same
     * object, according to {@link Ref#isSameEver E's "==" operation}. The
     * Selfless auditor ensures that all Selfless objects are accurately
     * self-describing in this way.
     * <p/>
     * The uncall of a Selfless object is canonical, so if x and y are both
     * Selfless, then
     * <pre>    x == y iff x.__optUncall() == y.__optUncall()</pre>
     * <p/>
     * Performing the call described by the uncall of a non-Selfless object
     * generally creates whatever that object wished to create as its
     * representative, but because it had to provide the ingredients, its
     * representative could only be something it could have created. Therefore,
     * the representative cannot convey any more authority than the original
     * object itself has.
     */
    static public Object[] __optUncall(Object self) {
        self = Ref.resolution(self);
        if (self != null && self instanceof Selfless) {
            Object[] spreadUncall = ((Selfless)self).getSpreadUncall();
            Object[] result = {spreadUncall[0],
              spreadUncall[1],
              ArrayHelper.slice(spreadUncall, 2, spreadUncall.length)};
            return result;
        }
        return null;
    }

    /**
     * Returns a description of the protocol this object alleges to implement.
     */
    static public TypeDesc __getAllegedType(Object self) {
        self = Ref.resolution(self);
        if (self == null) {
            return ClassDesc.make(Void.class);
        } else if (self instanceof Callable) {
            return ((Callable)self).getAllegedType();
        } else {
            // Reveal only the simplification of our class, so that we don't
            // reveal distinctions which should be invisible (according to
            // sameness).
            return ClassDesc.make(Equalizer.Simplification(self.getClass()));
        }
    }

    /**
     * Does this object respond to messages described by verb/arity?
     */
    static public boolean __respondsTo(Object self, String verb, int arity) {
        self = Ref.resolution(self);
        Class selfClass;
        if (self == null) {
            selfClass = Object.class;
        } else {
            selfClass = self.getClass();
        }
        Script script = ScriptMaker.THE_ONE.instanceScript(selfClass);
        return script.respondsTo(self, verb, arity);
    }

    /**
     * When someone was holding a partitionable eventual reference to this
     * object, and it sufferes a partition, then this object is informed that
     * one of its clients may no longer be able to talk to it, and why.
     * <p/>
     * The Miranda behavior is to do nothing, but objects may override this to
     * provide DeadManSwitch behavior. For example, a revoking facet of a
     * revokable service may decide that if its client may no longer be able to
     * talk to it, that it should auto-revoke. However inconvenient this
     * solution, it is failsafe.
     */
    static public void __reactToLostClient(Object self, Object problem) {
        T.noop();
    }

    /**
     * Used to implement when-catch and the "Ref whenResolved/2"; it should not
     * be called directly. <p>
     * <p/>
     * The Miranda behavior responds by doing 'reactor <- run(self)'. If the
     * reference never becomes resolved, the reactor is not invoked. <p>
     * <p/>
     * In the cooperative (non-malicious) case, the reactor will not be invoked
     * more than once.
     * <p/>
     * When sent on a reference, once the reference becomes resolved the
     * reactor will be invoked with the resolution. Should the reactor be
     * invoked with a non-broken reference, all earlier messages are guaranteed
     * to have been successfully delivered. <p>
     * <p/>
     * Should the reference become broken, or should breakage prevent the
     * reporting of fulfillment to the reactor, the reactor will be invoked
     * with a broken reference. The reactor may be invoked more than once. In
     * particular, if the reference becomes fulfilled and then broken, the
     * reactor may hear of either or both of these events.
     *
     * @see Ref#whenBroken
     */
    static public void __whenMoreResolved(Object self, Object reactor) {
        E.sendOnly(reactor, "run", self);
    }

    /**
     * Used to implement "Ref whenBroken/2"; it should not be called directly.
     * <p/>
     * The Miranda behavior ignores the message, as only breakable ref
     * implementations ever respond to this message.
     *
     * @see Ref#whenBroken(Object,OneArgFunc) Ref.whenBroken/2
     */
    static public void __whenBroken(Object self, Object reactor) {
        T.noop();
    }

    /**
     * Used in the expansion of '<tt>foo::bar</tt>' so that this syntax
     * represents foo's "bar" property.
     * <p/>
     * '<tt>foo::bar</tt>' expands to '<tt>foo.__getPropertySlot("bar").get()</tt>'.
     * When used an an lValue, '<tt>foo::bar := newValue</tt>' expands
     * essentially to '<tt>foo.__getPropertySlot("bar").put(newValue)</tt>'
     * except that the expansion also has the value of the assignment
     * expression be the value assigned.
     * <p/>
     * And finally '<tt>foo::&bar</tt>' expands to '<tt>foo.__getPropertySlot("bar")</tt>'
     * <p><hr> The Miranda behavior provided here synthesizes, for foo's bar
     * property, a Slot object <ul> <li>whose '<tt>get()</tt>' does a
     * '<tt>foo.getBar()</tt>' <li>whose '<tt>put(newValue)</tt>' does a
     * '<tt>foo.setBar(newValue)</tt>' <li>and whose '<tt>isFinal()</tt>'
     * returns false</tt>'. </ul>
     *
     * @see <a href= "http://www.eros-os.org/pipermail/e-lang/2004-April/009720.html"
     *      >Re: On kernel-E, operators, and properties (part 1)</a>.
     * @deprecated Even if E does again decide to support explicit properties,
     *             it'll do it with a global function that asks the object's
     *             {@link #__getAllegedType} for the methods for its
     *             properties.
     */
    static public Slot __getPropertySlot(Object self, String propName) {
        String getterVerb = JavaMemberNode.asGetterVerb(propName);
        String setterVerb = JavaMemberNode.asSetterVerb(propName);
        return new PropertySlot(self, getterVerb, setterVerb, propName);
    }

    /**
     * Generic object-level rights amplification protocol.
     * <p/>
     * Dispatch on the brand much as one would dispatch on a message name. If
     * we recognize the brand and we have the corresponding sealer, then we may
     * return something meaningful inside a SealedBox sealed with that Sealer.
     * If we have nothing to return, given the meaning we associate with that
     * brand as a request, then we return null.
     * <p/>
     * Something meaningful?  Sounds strange. See <a href=
     * "http://www.eros-os.org/pipermail/e-lang/2002-May/006435.html" >[e-lang]
     * Object coercion / adaptation</a> and the surrounding thread for more on
     * the rationale for the design of this method. Note that, at the time of
     * that discussion, this method was named getOptMeta instead.
     * <p/>
     * The default implementation: If self is Amplifiable, delegate to it.
     * Otherwise, return null.
     * <p/>
     * If this object isn't actually transparent, but if <tt>brand</tt>
     * represents a party this object would like to reveal itself to (such as a
     * serialization system implementing persistence for this object's
     * subsystem), then this object can choose to return a SealedBox, sealed by
     * the by the Sealer for that brand, containing the same triple that {@link
     * #__optUncall} would otherwise have returned. By so doing, the object
     * reveals its internals only to someone having the corresponding
     * Unsealer.
     * <p/>
     * If you know self to be {@link Amplifiable}, you can invoke this more
     * efficiently from Java by
     * <pre>    self.__optSealedDispatch(brand);</pre> Else by
     * <pre>    Ref.optSealedDispatch(self, brand);</pre>
     */
    static public SealedBox __optSealedDispatch(Object self, Object brand) {
        return null;
    }

    /**
     * When a guard doesn't succeed by itself at coercing an object, the guard
     * may enlists the object's aid in bring about a coercion it would find
     * acceptable.
     * <p/>
     * A guard enlists the object's aid at the price of being <a
     * href="http://www.sims.berkeley.edu/~ping/auditors/">Gozarian</a>. A
     * Gozarian guard wants to enlists the object's aid <i>because</i> it is
     * ignorant of the kind of object it's dealing with, so a generic protocol
     * is needed. That's why __conformTo/1 is provided as a MirandaMethod.
     * <p/>
     * The guard asks the object to conform to some guard that the object may
     * know about. Often this will be the guard that's attempting the coercion.
     * An object should attempt to return a representation of itself that the
     * argument guard would succeed in coercing. A requesting guard should then
     * try again on the result, but if this doesn't coerce, it should fail
     * there rather than making further __conformTo/1 requests. This implies
     * that it's the responsibility of the object's __conformTo/1
     * implementation to do any iteration needed for multi-step conversions.
     * <p/>
     * The default (Miranda) implementation of __conformTo/1 just returns
     * self.
     * <p/>
     * If you know self to be a {@link Conformable}, you can invoke this more
     * efficiently from Java by
     * <pre>    self.__conformTo(guard);</pre> Else by
     * <pre>    Ref.conformTo(self, guard);</pre>
     */
    static public Object __conformTo(Object self, Guard guard) {
        return self;
    }

    /**
     * For E, this is the method that is overridden, rather than toString(),
     * since composition via toString() cannot break cycles.
     * <p/>
     * If you know self to be a {@link EPrintable}, you can invoke this more
     * efficiently from Java by
     * <pre>    self.__printOn(out);</pre> Else, given that out is a
     * TextWriter, by
     * <pre>    out.print(self);</pre>
     */
    static public void __printOn(Object self, TextWriter out)
      throws IOException {
        self = Ref.resolution(self);
        if (null == self) {
            out.write("null");

        } else if (self instanceof Throwable) {
            Throwable leaf = ThrowableSugar.leaf((Throwable)self);
            if (leaf instanceof EPrintable) {
                ((EPrintable)leaf).__printOn(out);
            } else {
                //XXX we engage in this horrible kludge because ThrowableSugar
                //exists in a layer prior to TextWriter
                ThrowableSugar.printThrowableOn((Throwable)self, out);
            }
        } else if (self instanceof Callable) {
            ((Callable)self).mirandaPrintOn(out);
        } else {
            Class clazz = self.getClass();
            Method toStringMethod;
            try {
                toStringMethod = clazz.getMethod("toString", NO_CLASSES);
            } catch (NoSuchMethodException e) {
                throw ExceptionMgr.asSafe(e);
            } catch (SecurityException e) {
                throw ExceptionMgr.asSafe(e);
            }

            if (Object.class == toStringMethod.getDeclaringClass()) {
                // Neither Object#toString nor EPrintable#__printOn was
                // overridden, so use class name
                out.write(
                  "<" + StringHelper.aan(ClassDesc.simpleSig(clazz)) + ">");
            } else {
                //use overriding toString
                out.write(String.valueOf(self));
            }
        }
    }
}
