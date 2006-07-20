package org.erights.e.elib.ref;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............


import org.erights.e.elib.prim.E;
import org.erights.e.elib.serial.PassByProxy;
import org.erights.e.elib.util.DeadManSwitch;
import org.erights.e.elib.util.OneArgFunc;

/**
 * Used to implement {@link Ref#whenResolved(Object, OneArgFunc)}, which is
 * used to implement the <a
 * href="http://www.skyhunter.com/marcs/ewalnut.html#SEC20">when-catch</a>
 * construct.
 * <p/>
 * Wraps a 'done' function (the second argument of whenResolved, which is the
 * right side of the "->" of a when-catch construct), so that the done
 * function will eventually be invoked with the original reference exactly
 * once under the following conditions:
 * <ul>
 * <li>The original reference is resolved
 * <li>Either all prior messages sent on the original reference are already
 * delivered, or the original reference has become broken.
 * </ul>
 * This object is woken up by __reactToLostClient and the response to
 * __whenMoreResolved, but in both cases it ignores the argument and treats the
 * message just as a wakeup call.
 *
 * @author Mark S. Miller
 * @author Terry Stanley
 * @see WhenBrokenReactor
 */
class WhenResolvedReactor
  implements DeadManSwitch, OneArgFunc, PassByProxy {

    private boolean myIsDone;

    private OneArgFunc myOptWrapped;

    private Object myRef;

    private Resolver myOptResolver;

    /**
     * When ref is resolved, invoke wrapped, and resolve optResolver (if
     * non-null) to its outcome.
     * <p/>
     * Assumes a first __whenMoreResolved will be sent with this
     * WhenResolvedReactor as argument.
     */
    WhenResolvedReactor(OneArgFunc wrapped,
                        Object ref,
                        Resolver optResolver) {
        myIsDone = false;
        myOptWrapped = wrapped;
        myRef = ref;
        myOptResolver = optResolver;
    }

    /**
     * Causes us to wakeup and check if myRef is resolved.
     * <p/>
     * If myRef is resolved, then invoke myOptWrapped once (resolving
     * myOptResolver to the outcome), and remember not to invoke it again (by
     * forgetting it). Also forget myRef and myOptResolver, since we won't
     * need them again. Further invocations silently return null rather than
     * complaining.
     * <p/>
     * If myRef is not resolved, then send a new <pre>
     * <p/>
     *     myRef <- __whenMoreResolved(this)
     * </pre> message whose response should wake me up again.
     *
     * @return Always returns null, irrespective of what
     *         myOptWrapped.run(...) returns.
     */
    public Object run(Object ignored) {
        if (myIsDone) {
            return null;
        }
        if (Ref.isResolved(myRef)) {
            Object ref = myRef;
            OneArgFunc wrapped = myOptWrapped;
            Resolver optResolver = myOptResolver;

            myIsDone = true;
            myRef = null;
            myOptWrapped = null;
            myOptResolver = null;

            Object outcome;
            try {
                outcome = wrapped.run(ref);
            } catch (Throwable problem) {
                outcome = Ref.broken(problem);
            }
            if (null != optResolver) {
                optResolver.resolve(outcome);
            }
        } else {
            //Not yet resolved, keep polling.
            E.sendOnly(myRef, "__whenMoreResolved", this);
            //XXX should we care about a non-null return result from
            //sendOnly?
        }
        return null;
    }

    /**
     * Just like run/1, this is treated merely as a wakeup call to check
     * myRef.
     */
    public void __reactToLostClient(Throwable ignored) {
        run(ignored);
    }
}
