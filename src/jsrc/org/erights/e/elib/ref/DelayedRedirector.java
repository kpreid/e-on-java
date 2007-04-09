package org.erights.e.elib.ref;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.elib.prim.E;
import org.erights.e.elib.serial.PassByProxy;
import org.erights.e.elib.util.DeadManSwitch;
import org.erights.e.elib.util.OneArgFunc;

/**
 * Wraps a EProxyResolver for a OldRemotePromise in a way suitable for inclusion
 * as an argument in the first __whenMoreResolved message, in order to preserve
 * reference-full-order.
 *
 * @author Mark S. Miller
 */
public class DelayedRedirector
  implements DeadManSwitch, OneArgFunc, PassByProxy {

    /**
     *
     */
    private EProxyResolver myOptResolver;

    /**
     * While the DelayedRedirector isn't garbage, its {@link EProxy} isn't
     * either, since the DelayedRedirector may still send on it.
     */
    private Ref myOptProxy;

    /**
     *
     */
    public DelayedRedirector(EProxyResolver resolver) {
        myOptResolver = resolver;
        myOptProxy = resolver.getProxy();
    }

    /**
     * XXX the following documents only the unoptimized case:
     * <p/>
     * On the first response, send a second __whenMoreResolved on the original
     * RemotePromise, and then resolve the EProxyResolver to a Promise that
     * will be resolved by the answer to this second __whenMoreResolved.
     * <p/>
     * This ensures that all messages have drained out of the previous path
     * before enabling the new path. Once I've done my one-time-job, I become
     * inoperative.
     * <p/>
     * The argument of this first response is ignored (thanks Dean!). The
     * argument of the second response is used as the true resolution of my
     * RemotePromise.
     */
    public Object run(Object target) {
        if (null == myOptResolver) {
            //If my RemotePromise is already resolved, then ignore further
            //resolution attempts
            return null;
        }

        EProxyHandler handler = myOptResolver.optHandler();
        if (handler.isFresh() || handler.sameConnection(target) ||
          Ref.isDeepFrozen(target)) {
            //handler.isFresh():
            //If no messages have yet been sent over my RemotePromise, then
            //there's no message ordering issue, so resolve to target
            //immediately.

            //handler.sameConnection(target):
            //If the new target is a remote reference into the same vat that
            //my RemotePromise is into, then messages to be sent over target
            //will only arrive after messages previously sent on this
            //RemotePromise, so again there's no message ordering issue, and
            //we can resolve to target immediately.

            //Ref.isDeepFrozen(target):
            //If target is DeepFrozen, then the order in which messages are
            //delivered to it cannot matter, so resolve to target
            //immediately.

            myOptResolver.resolve(target);
            myOptResolver = null;
            myOptProxy = null;
            return null;
        }

        //If we fall through the above special cases, then we're in the
        //unoptimized case, where we do need to deal with the message ordering
        //issue. In this case, ignore the run/1 argument, send a last
        //__whenMoreResolved/1 message over the RemotePromise with a simple
        //Redirector as argument, and then resolve to a promise whose
        //resolver is held by that Redirector.
        //
        //This will locally buffer messages until all previous messages have
        //been drained out, and will then resolve to the argument of the
        //second run/1 message (thereby delivering all buffered messages as
        //well).

        Object[] pair = Ref.promise();
        Redirector rdr = new Redirector((Resolver)pair[1]);
        E.sendOnly(myOptProxy, "__whenMoreResolved", rdr);
        myOptResolver.resolve(pair[0]);

        myOptResolver = null;
        myOptProxy = null;
        return null;
    }

    /**
     * Smash the EProxyResolver with the arg.
     */
    public void __reactToLostClient(Object problem) {
        if (null != myOptResolver) {
            myOptResolver.smash(E.asRTE(problem));
            myOptResolver = null;
            myOptProxy = null;
        }
    }
}
