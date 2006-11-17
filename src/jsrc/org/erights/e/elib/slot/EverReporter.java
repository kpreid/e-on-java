package org.erights.e.elib.slot;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............


/**
 * Reports new values to subscribed {@link EverReactor}s.
 * <p/>
 * The Reporter/Reactor pattern is much like the Observable/Observer pattern
 * (or the bean/Listener pattern), described by the following terminology:<ul>
 * <li><b>Reporter</b> for Observable or bean. <li><b>Reactor</b> for Observer
 * or Listener. <li><b>subscribe</b> for registering a Reactor to receive
 * reports from a Reporter. There are two kinds of subscriptions:<ul>
 * <li><b>Continuing subscription</b>: By subscribing with
 * <b>add<i>Foo</i>Reactor(..)</b>, the Reactor will receive every report the
 * reporter sends about <i>Foo</i> until it is unsubscribed with a
 * corresponding <b>remove<i>Foo</i>Reactor(..)</b>. These are directly
 * analogous to Java's <tt>add<i>Foo</i>Listener(..)</tt> /
 * <tt>remove<i>Foo</i>Listener(..)</tt> convention. <li><b>One-report
 * subscription</b>: By subscribing with a <b>when<i>Foo</i>(...)</b> message,
 * the Reactor will receive at most one report about <i>Foo</i>, which also
 * terminates the subscription. Below we explain the pattern for using such
 * one-report subscriptions to create a <i>virtual continuing subscription</i>
 * that is better in many ways than an actual continuing subscription for
 * distributed programming. </ul> <li><b>report</b> -- a Reporter reports by
 * sending reports to its subscribed Reactors. A report is either a
 * <b>reactTo<i>Foo</i>(...)</b> message or simply a <b>run(...)</b> message.
 * <li>Because reports flow from a Reporter to its subscribed Reactors, and
 * because these Reactors are sometimes themselves Reporters with further
 * subscribers (creating a multicast tree for reports), we speak of a Reporter
 * as being <b>upstream</b> of its subscribed Reactors, which are therefore
 * <b>downstream</b> of it. Subscriptions flow upstream; reports flow
 * downstream. </ul> To accommodate distributed programming, the
 * Reporter/Reactor subscription patterns differ from the conventional Observer
 * pattern as follows:<ul> <li>Both subscriptions and reports are sent
 * asynchronously, with {@link org.erights.e.elib.prim.E#sendAllOnly(Object,
 *String,Object[]) no reply expected}. Therefore, notification is only
 * eventual, and should be assumed stale on arrival. <li>Reporters only retain
 * a live reference to each subscribing Reactor, so a live subscription (the
 * kind described above) is automatically terminated by partition. A persistent
 * (ie, partition-surviving) subscription is an arrangement for restoring a
 * live subscription (actual or virtual) once the two sides can reconnect.
 * Analogy with the observable/observer pattern would suggest having the
 * Reporter remember a SturdyRef to the Reactor in order to restore the live
 * subscription. However, this puts the burden on the wrong party.
 * <p/>
 * Instead, in a persistent subscription, the Reactor should remember a
 * SturdyRef to the Reporter, and restore the live subscription when this
 * SturdyRef can be revived. XXX The current system can only support "when this
 * SturdyRef can be revived" by {@link net.captp.jcomm.SturdyRef#getRcvr(long,
 *long) direct polling}, but we expect to fix this transparently to client
 * code.
 * <p/>
 * On reconnect, the Reactor should assume that messages have been lost and
 * that other time trauma might have occurred (ie, one or both sides may have
 * regressed). Therefore, the Reactor should restore whatever distributed
 * consistency it needs (by querying the Reporter) before continuing
 * operations. </ul> The EverReporter/EverReactor pair is the main example of
 * the <i>virtual continuing subscription</i> pattern. A virtual continuing
 * subscription is built out of one-report subscriptions as follows:<ul>
 * <li>Since a one-report subscription only lasts till the first report is
 * sent, a Reactor that wishes to be continually updated must therefore
 * re-subscribe following the reception of each report. A Reactor <i>may</i>
 * therefore delay re-subscribing until it needs a less stale value. <li>A
 * Reactor which always immediately re-subscribes is a <i>forever-reactor</i>,
 * whereas one that delays until a client indicates a need for a less stale
 * value is a <i>whenever-reactor</i>. A whenever-reactor is normally a means
 * to an end, whereas a forever-reactor is normally an end in itself. A system
 * of forever-reactors is eventually consistent under quiescence and
 * non-partition. A system of whenever-reactors is eventually consistent under
 * quiescence, non-partition, and continued expression of local interest in
 * less stale values. <li>For a one-report subscription, there is no
 * remove-reactor method. Instead, a Reactor that should no longer react should
 * change its own state to become insensitive, and to avoid re-subscribing.
 * This means junk subscriptions may stick around in the Reporter until the
 * next report. Except for this minor leakage, this gives us most of the
 * advantages of weak subscriptions, but without having to invent a "network
 * weak pointer" primitive (which would be hard). <li>The above points mean
 * that the live Reporter/Reactor connection keeps bouncing around between
 * Reporter-pointing-at-Reactor vs Reactor-pointing-at-Reporter. During the
 * transition between the two, neither points at the other. Rather, the
 * connectivity is only in a reference to one carried in a message
 * (when<i>Foo</i> or reactTo<i>Foo</i>) being sent to the other. <li>This
 * bouncing acts like event notification when events to be reported happen less
 * often than network round trips, which is good. This bouncing acts like
 * polling when these events happen more often than network round trips, which
 * is good. <li>This bouncing relies on a delicate inter-vat consistency that
 * cannot realistically be preserved across partition. Fortunately, on
 * reconnect, simply re-subscribing is adequate to restore the distributed
 * consistency of this bouncing around. </ul> An EverReporter is a Reporter
 * that reports successively more recent versions of a single value to its
 * registered EverReactors. EverReactors are assumed to care about only the
 * most recent available version of the value, so the "ever" protocol is lossy
 * -- it skips unobserved intermediate values.
 * <p/>
 * When the values to be reported are large, one can imagine more sophisticated
 * variants of this protocol that send differences or update operations rather
 * than new copies. However, these variants should be defined as new types, not
 * as kinds of EverReporter/EverReactor.
 *
 * @author Mark S. Miller
 * @author Derived from the Lamport Cell work done in collaboration with Dean
 *         Tribble and inspired by Leslie Lamport.
 * @author Thanks to Terry Stanley and Marc Stiegler for the Reactor / Reporter
 *         terminology. Finding good terminology was harder than it looks.
 * @see makeLamportSlot$lamportSlot
 * @see makeLamportSlot$lamportReporter
 * @see makeLamportSlot$lamportReactor
 */
public interface EverReporter extends Slot {

    /**
     * This is how a downstream EverReactor subscribes (or re-subscribes) with
     * this EverReporter.
     * <p/>
     * This EverReporter is being told <blockquote> 'reactor' has a value
     * that's current as of 'lastGeneration'. When this EverReporter has more
     * recent news, report it to him. </blockquote> Once this EverReporter
     * reports to the EverReactor, this EverReporter forgets the EverReactor,
     * so the EverReactor will receive further reports only if it
     * re-subscribes.
     *
     * @param lastGeneration an {@link org.erights.e.meta.java.math.EInt
     *                       EInt}.
     */
    void whenUpdated(EverReactor reactor, Number lastGeneration);

    /**
     * For initial connectivity.
     * <p/>
     * Should be equivalent to <tt>whenUpdated(reactor, -1)</tt>
     */
    void whenUpdated(EverReactor reactor);

    /**
     * Synchronously return the locally stored value, which may be stale if
     * this Reporter is also a Reactor downstream from a more authoritative
     * Reporter.
     * <p/>
     * A getValue() query is an expression of local interest: If this Reporter
     * is also a <i>whenever-reactor</i> (see the class comment), then
     * getValue() should trigger any delayed updating so that future
     * getValue()s will come to be less stale.
     */
    Object getValue();

    /**
     * A typical EverReporter will normally double as a read-only Slot, in
     * which case it should implement setValue to simply throw an informative
     * exception.
     */
    void setValue(Object newValue);
}
