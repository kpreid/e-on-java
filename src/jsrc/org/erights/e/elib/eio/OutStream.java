// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.elib.eio;

import org.erights.e.elib.tables.ConstList;

import java.io.IOException;

/**
 * Places a sequence of elements into a stream.
 * <p/>
 * A client of an OutStream is a <i>producer</i>.
 *
 * @author Mark S. Miller
 */
public interface OutStream extends Stream {

    /**
     * Accepts <tt>elements</tt> as the immediate next ones in the stream.
     * <p/>
     * If this stream is already closed, an UnavailableException must be
     * thrown.
     * <p/>
     * If this stream has already failed, the terminal IOException must be
     * thrown.
     * <p/>
     * If the stream fails during the write, then some initial subsequence of
     * the elements may be written in order, and the terminal IOException
     * must be thrown.
     * <p/>
     * If the stream doesn't fail<ul>
     * <li>If <tt>elements.size() <= w.available()</tt>, then all the
     * elements must be written in order.
     * <li>If the operation returns normally, then all the elements were
     * written in order.
     * <li>If an UnavailableException is thrown, then none of the elements
     * were written and the stream was not effected.
     * </ul>
     *
     * @param elements The elements to place into the stream.
     */
    void write(ConstList elements) throws UnavailableException, IOException;

    /**
     * A flush() operation obligates all elements that have already entered the
     * stream to eventually make progress except as limited by failure and
     * backpressure.
     * <p>Exerts
     * <a href="http://www.erights.org/elib/concurrency/eio/goals.html#flush"
     * >flush pressure</a> on all prior elements of the stream. When streams
     * are composed (for example, with pipes and filters), this obligation
     * travels with the elements until they emerge from the plumbing.
     * <p/>
     * A stream which exerts flush pressure on all elements that enter the
     * stream is free to ignore flush(), or to treat it as a semantics-free
     * suggestion to "hurry". This is indeed how the current implementation
     * works.
     */
    void flush();
}
