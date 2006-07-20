package org.erights.e.elib.slot;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............


/**
 * Reacts to reports from an {@link EverReporter}.
 *
 * @author Mark S. Miller
 * @see EverReporter
 */
public interface EverReactor {

    /**
     * A report from an EverReporter that <tt>newValue</tt> is current as of
     * generation number <tt>newReporterGen</tt> of <tt>optNewReporter</tt>'s
     * local numbering.
     * <p/>
     * <tt>optNewReporter</tt> is typically, but not necessarily, the same
     * reporter we subscribed to. When we re-subscribe, we should
     * subscribe with optNewReporter, as that is the reporter we should
     * use <i>according to</i> our original reporter. If we're not trying to
     * subscribe persistently, this means we don't need to remember who we
     * subscribed to once we send off a subscription request.
     * <p/>
     * If this Reactor is also a Reporter (with further downstream Reactors),
     * then, on receiving this report, it should in turn report to its
     * subscribers. A non-empty set of subscribers is an expression of local
     * interest, as is a {@link EverReporter#getValue()} request.
     * <p/>
     * If this is a forever-reactor, then it will react to the report by
     * immediately re-subscribing to optNewReporter. If this is a
     * whenever-reactor, then it will delay the re-subscribe request until
     * there's an expression of local interest.
     *
     * @param newReporterGen an {@link org.erights.e.meta.java.math.EInt EInt}.
     * @param optNewReporter If null, then this EverReactor has been cut loose
     *                       -- this last newValue is now authoritative since
     *                       there's no one upstream to ask.
     */
    void reactToUpdate(Object newValue,
                       Number newReporterGen,
                       EverReporter optNewReporter);
}
