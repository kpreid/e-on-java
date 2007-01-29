// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.elib.eio;

import org.erights.e.elib.tables.ConstList;

import java.io.IOException;

/**
 * For <a href="http://www.erights.org/elib/concurrency/eio/obtaining.html"
 * >obtaining</a> elements from a stream.
 * <p/>
 * A client of an InStream is a <i>consumer</i>.
 *
 * @author Mark S. Miller
 * @see <a href="http://www.erights.org/elib/concurrency/eio/obtaining.html"
 *      >obtaining</a> for more info.
 */
public interface InStream extends Stream {

    /**
     * <b>sched</b> value: The operation is performed immediately, whether or
     * not sufficient elements are ready.
     * <p/>
     * If a sufficient number of elements cannot be obtained immediately, then
     * an {@link UnavailableException} or other IOException is thrown. If an
     * UnavailableException is thrown, then nothing was consumed and no
     * side-effects should have happened. If another IOException is thrown,
     * then any number up to atMost may have been consumed.
     */
    String NOW = "NOW";

    /**
     * <b>sched</b> value: <b><i><font color="#ff0000">Warning: may block the
     * vat and cause deadlock</font></i></b>.
     * <p/>
     * If the operation can succeed immediately, it is performed immediately.
     * Else, if insufficient elements are currently ready, and if this InStream
     * supports waiting, then the calling vat/runner/thread is blocked until
     * this operation can immediately complete. The wrappers of java.io streams
     * support waiting. <p>m This should normally only be used when<ul> <li>The
     * programmer knows the source to be prompt even though the underlying
     * java.io stream doesn't. <li>This vat exists for the purpose of building
     * one <i>virtual device</i> from some underlying non-prompt stream, as
     * with a one-top-level-expr-at-a-time parser parsing an InStream of
     * characters. </ul>
     */
    String WAIT = "WAIT";

    /**
     * <b>sched</b> value: Registers a claim for the next
     * <tt>atLeast..atMost</tt> elements, and returns a promise for them.
     * <p/>
     * When these characters become ready, the claim will be satisfied and the
     * promise will be resolved.
     */
    String LATER = "LATER";

    /**
     * <b>proceed</b> value: Advances the stream past the obtained data.
     */
    String ADVANCE = "ADVANCE";

    /**
     * <b>proceed</b> value: Does not advance the stream.
     * <p/>
     * A QUERY operation should ideally be side-effect free.
     */
    String QUERY = "QUERY";

    /**
     * <b>report</b> value: Return the obtained data.
     */
    String ELEMENTS = "ELEMENTS";

    /**
     * <b>report</b> value: Ignore the obtained data and return termination
     * status.
     */
    String STATUS = "STATUS";


    /**
     * The generic obtain operation in which all the orthogonal dimensions are
     * explicit parameters.
     * <p/>
     * All other <i>reading</i> operations are specified by equivalence to a
     * parameterization of <tt>obtain/5</tt>. Normally, one would call one of
     * those other shorter operations instead.
     * <p/>
     * At an InStream, a sequence of arriving elements is matched to a sequence
     * of reading operations according to the order of the elements and the
     * order in which the reading operations were invoked on the InStream.
     *
     * @param atLeast Must be &gt;= 0 or {@link EIO#ALL}. Indicates the
     *                smallest number of elements this obtain operation may
     *                obtain and still report success. ALL means "All remaining
     *                elements in the stream until stream termination." Any
     *                other non-negative integer means "That many elements, or
     *                all remaining elements, whichever comes first."
     * @param atMost  Must be &gt;= atLeast or {@link EIO#ALL}. Indicates the
     *                largest number of elements this obtain operation may
     *                obtain.
     *                <p/>
     *                A <b><i>sufficient</i></b> number of elements is a number
     *                in the range of size that may be obtain according to
     *                <tt>atLeast..atMost</tt> by the above spec. A reading
     *                operation generally should obtain the largest sufficient
     *                number it can conveniently obtain.
     * @param sched   One of {@link #NOW}, {@link #WAIT}, or {@link #LATER}.
     * @param proceed Are the obtain elements consumed from the stream? If so,
     *                this relieves backpressure, thereby allowing further
     *                elements to continue flowing downstream.
     *                <p/>
     *                If not, then this is a <i>peeking</i> operation and
     *                should be side-effect free.
     * @param report  Are the obtain elements gathered into a list and
     *                returned? If not, then this is a <i>skipping</i>
     *                operation. If both proceed and report are false, then
     *                this is an operation for checking when new data
     *                <i>becomesReady</i>.
     * @return If sched is LATER, then return a vow for the result. Else return
     *         the result. If report is true, then the result will be a list of
     *         a sufficient number of elements; else the result will be null.
     *         If this operation fails, the vow should resolve to a reference
     *         broken by the problem that would have been thrown.
     * @throws UnavailableException If sched==NOW, sufficient elements were not
     *                              ready, and none were consumed.
     * @throws IOException          If any other I/O problems occur, or if the
     *                              stream has already failed. If this is
     *                              thrown because the stream has already
     *                              failed, this should be the stream's
     *                              terminal problem.
     */
    Object obtain(int atLeast,
                  int atMost,
                  String sched,
                  String proceed,
                  String report) throws UnavailableException, IOException;

    /**
     * Immediately reads, consumes, and returns the next N elements of the
     * stream, where N is in the range defined by <tt>atLeast..atMost</tt>.
     * <pre>    i.read(atLeast,atMost)</pre>
     * is defined by equivalence to
     * <pre>    i.obtain(atLeast,atMost,NOW,true,true)</pre>
     */
    ConstList readNow(int atLeast, int atMost)
      throws UnavailableException, IOException;

    /**
     * Blocks the vat if necessary until it can "immediately" read, consume,
     * and return the next N elements of the stream, where N is in the range
     * defined by <tt>atLeast..atMost</tt>.
     * <pre>    i.read(atLeast,atMost)</pre>
     * is defined by equivalence to
     * <pre>    i.obtain(atLeast,atMost,WAIT,true,true)</pre>
     */
    ConstList readWait(int atLeast, int atMost)
      throws UnavailableException, IOException;

    /**
     * Immediately reads, consumes, and returns the next N elements of the
     * stream, where N is in the range defined by <tt>atLeast..atMost</tt>.
     * <pre>    i.readLater(atLeast,atMost)</pre>
     * is defined by equivalence to
     * <pre>    i.obtain(atLeast,atMost,LATER,true,true)</pre>
     */
    ConstList readLater(int atLeast, int atMost)
      throws UnavailableException, IOException;

    /**
     * Immediately reads, consumes, and returns the next element of the
     * stream.
     * <pre>    i.readOptOne()</pre>
     * is defined by equivalence to<pre>
     *     def list := i.obtain(1,1,NOW,true,true)
     *     if (list.size() == 0) {
     *         null
     *     } else {
     *         list[0]
     *     }</pre>
     * In other words, if the stream is terminated, then return null. If null
     * is also a valid element of this stream, readOptOne's caller must be
     * aware that a null may be returned for either reason.
     */
    Object readOptOne() throws UnavailableException, IOException;

    /**
     * Immediately reads, consumes, and returns all remaining elements of the
     * stream.
     * <pre>    i.readAll()</pre>
     * is defined by equivalence to
     * <pre>    i.obtain(ALL,ALL,NOW,true,true)</pre>
     */
    ConstList readAll() throws UnavailableException, IOException;

    /**
     * Immediately reads, consumes, and returns all remaining elements of the
     * stream.
     * <pre>    i.readAllLater()</pre>
     * is defined by equivalence to
     * <pre>    i.obtain(ALL,ALL,LATER,true,true)</pre>
     */
    Object readAllLater() throws UnavailableException, IOException;

    /**
     * Immediately reads and returns, but does not consume, the next N elements
     * of the stream, where N is in the range defined by
     * <tt>atLeast..atMost</tt>.
     * <pre>    i.peek(atLeast,atMost)</pre>
     * is defined by equivalence to
     * <pre>    i.obtain(atLeast,atMost,NOW,false,true)</pre>
     */
    ConstList peek(int atLeast, int atMost)
      throws UnavailableException, IOException;

    /**
     * Removes and discards the next <tt>num</tt> elements from the stream.
     * <pre>    i.skip(num)</pre>
     * is defined by equivalence to
     * <pre>    i.obtain(num,num,LATER,true,false)</pre>
     */
    Object skip(int num) throws UnavailableException, IOException;

    /**
     * Returns a vow that's resolved once the next <tt>num</tt> elements from
     * the stream becomes ready.
     * <pre>    i.becomesReady(num)</pre>
     * is defined by equivalence to
     * <pre>    i.obtain(num,num,LATER,false,false)</pre>
     */
    Object becomesReady(int num) throws IOException;

    /**
     * Reliquish the ability to obtain any further elements from the stream,
     * thereby allowing the stream and its upstream producers to release
     * resources and avoid wasted effort.
     * <pre>    i.close()</pre>
     * is defined by equivalence to
     * <pre>    i.obtain(ALL,ALL,LATER,true,false)</pre>
     */
    Object close() throws IOException;

    /**
     * Reliquish the ability to obtain any further elements from the stream,
     * thereby allowing the stream and its upstream producers to release
     * resources and avoid wasted effort.
     * <pre>    i.close()</pre>
     * is defined by equivalence to
     * <pre>    i.obtain(ALL,ALL,LATER,true,false)</pre>
     */
    Object terminates() throws IOException;

    /**
     * How many elements remain in the stream?
     * <p/>
     * Until this is known, the answer is ALL. When the stream is terminated,
     * the answer is 0. When all remaining elements are known to be immediately
     * available (as when reading a file), then
     * <pre>    available() == remaining()</pre>.
     */
    int remaining();
}
