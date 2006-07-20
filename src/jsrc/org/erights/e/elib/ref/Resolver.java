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

/**
 * The facet of a promise for resolving the outcome of that promise.
 * <p/>
 * Once a Resolver is used, it isDone(), ie, it is used up.
 * <p/>
 * (Like the Joule <tt>Distributor</tt> or Original-E <tt>EResult</tt>.)
 *
 * @author Mark S. Miller
 */
public interface Resolver {

    /**
     * The <tt>strict</tt> flag of {@link #resolve(Object, boolean) resolve/2}
     * defaults to <b><tt>true</tt></b>.
     * <p/>
     * Since a successful return indicates a successful resolution, this is a
     * void return, rather than always returning true.
     */
    void resolve(Object target);

    /**
     * Queues all accumulated and future messages for delivery to target, and,
     * for example, a local promise becomes resolved into being E-equivalent to
     * the target.
     * <p/>
     * If this resolver is not yet {@link #isDone}, resolve/2 the promise (or
     * whatever) becomes resolved, this Resolver becomes done, and resolve/2
     * returns true. If this resolver already {@link #isDone}, then, if strict,
     * resolve/2 throws an exception. Otherwise it returns false. In any case,
     * a Resolver will be isDone() following a resolve.
     *
     * @return Whether this resolve caused this Resolver to become done. If
     *         strict, then we will always either return true or throw an
     *         exception.
     */
    boolean resolve(Object target, boolean strict);

    /**
     * @deprecated Use <tt>{@link #resolve(Object, boolean) resolve}(...,
     *             false)</tt> instead.
     */
    boolean resolveRace(Object target);

    /**
     * Breaks the promise, reporting 'problem' as the reason.
     * <p/>
     * "r.smash(p)" is equivalent to "r.resolve(Ref.broken(p), false)".
     *
     * @return Whether this smash(..) caused this Resolver to become done.
     * @see #resolve(Object, boolean)
     */
    boolean smash(Throwable problem);

    /**
     * Has the corresponding promise (or whatever) been resolved to become some
     * target yet?
     * <p/>
     * Once a Resolver is done, it is done forever.
     */
    boolean isDone();

    /**
     * Used to let a resolver know that progress has just been made towards its
     * resolution.
     * <p/>
     * In the conventional sense, this method has no semantics; it is
     * equivalent to a no-op. Rather, the purpose of this call is to inform
     * diagnostic and debugging tools that what just happened, as part of this
     * turn, has contributed towards resolving this resolver. Causeway, for
     * example, uses this to record the current event as one of the causal
     * parents of all the events caused by delivery of all the messages that
     * were or will be buffered in this promise prior to its resolution.
     */
    void gettingCloser();
}
