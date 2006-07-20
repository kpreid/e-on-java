// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.elib.eio;

import org.erights.e.elib.base.Thunk;

import java.io.IOException;

/**
 * A connector through which an
 * <a href="http://www.erights.org/elib/concurrency/eio/index.html"
 * >EIO</a> uni-directional stream of elements flow.
 * <p/>
 * There are two kinds of connector: {@link InStream} and {@link
 * OutStream}. You use an OutStream to place elements into a stream,
 * and you use a downstream InStream to obtain elements from the
 * stream. Either may or may not be ready to do so depending on how
 * much it has {@link #available()}. For an InStream, the issue is how
 * many elements are now ready to be read. For an OutStream, the issue
 * is how much spare capacity (unallocated buffer space) it has to
 * accept elements that are to be written.
 *
 * @author Mark S. Miller
 */
public interface Stream {

    /**
     * The type of elements that may appear in this stream.
     * <p/>
     * If this class represents a {@link Class#isPrimitive() scalar type}, then
     * all elements must be instances of the elementType. Otherwise, the
     * elements may either be instances of the elementType or be null.
     * <p/>
     * A later version of this API may allow a guard here instead of a class.
     */
    Class getElementType();

    /**
     * The most that might ever be available at once through this connector.
     * <p/>
     * This is typically the same as the buffer size, but only if the buffer
     * will never be grown. If this isn't known, isn't meaningful, or isn't a
     * fixed value, then ALL must be returned.
     */
    int maxAvailable();

    /**
     * What are the largest number of elements that are guaranteed to be
     * transferable now (immediately read or written) through connector?
     * <p/>
     * Must be &gt;= 0, &lt;= {@link #maxAvailable()}, and &lt; ALL
     */
    int available();

    /**
     * Registers a reactor to be immediately called once there's enough
     * available, or once this connector {@link #terminates()}.
     * <p/>
     * At most one reactor may be registered. The reactor is registered exactly
     * until it is triggered. A registered reactor has first claim on the next
     * available elements to be read or capacity to be written, so while a
     * reactor is registered the connector reports that 0 is
     * {@link #available()}.
     * <p/>
     * Unlike most E callbacks, the reactor is invoked synchronously in the
     * same turn as the occurrence of its triggering condition, such that this
     * condition will still be true at the moment the reactor is called. If the
     * condition is already true at the time whenAvailable is called, then the
     * reactor will be immediately called back from whenAvailable rather than
     * being registered.
     * <p/>
     * The triggering condition? Either:<ul>
     * <li>the number available is &gt;= atLeast, or
     * <li>the connector has terminated (closed or failed).
     * </ul>
     *
     * @param atLeast      How many elements should be available at the time this
     *                     reactor is called. <tt>atLeast</tt> must be
     *                     <tt>&gt;= 1</tt>, <tt>&lt;= maxAvailable()</tt>,
     *                     and <tt>!= ALL</tt>.
     * @param availReactor To be called by this connected when its triggering
     *                     occurs.
     * @return A vow for the result of calling availReactor. If availReactor
     *         is called during the whenAvailable, then this is immediately the
     *         result of that call.
     */
    Object whenAvailable(int atLeast, Thunk availReactor);

    /**
     * Returns a vow for the terminator.
     * <p/>
     * The terminator represents the terminating condition that occured after
     * all the elements of the stream.
     * <p/>
     * The terminator is either <tt>true</tt>, meaning a {@link #close()}
     * happened and the stream terminated successfully, or it's a reference
     * broken by the terminal problem (an IOException), in which case the
     * stream failed reporting this problem as the reason.
     * <p/>
     * If the stream is already done, then <tt>terminates()</tt> returns the
     * terminator immediately. Otherwise it returns a vow that must not resolve
     * until the stream is done.
     * <p/>
     * The funny name for this method was chosen so that it would read smoothly
     * in the following context:<pre>
     *     when (c.terminates()) -> done(_) {
     *         # ... deal with successful close ...
     *     } catch problem {
     *         # ... deal with failure ...
     *     }
     * }</pre>
     * Note that the done parameter is "<tt>_</tt>", meaning "ignore", since
     * in the success case it will always be <tt>true</tt>. Unlike
     * whenAvailable, this when/catch will only be invoked eventually in its
     * own turn. But since termination is monotonic (once failed always
     * failed), this introduces no race conditions.
     */
    Object terminates()
      throws IOException;

    /**
     * Has this connector terminated?
     * <p/>
     * <tt>c.isTerminated()</tt> is equivalent to
     * <tt>Ref.isResolved(c.terminates())</tt>.
     */
    boolean isTerminated();

    /**
     * Terminate the stream successfully, unless it has already terminated.
     * <p/>
     * On the writing side, a close() implies a flush(). One closes a writer
     * to indicate that no more elements will ever be written to the stream.
     * One closes a reader to indicate that no one will pay any attention to
     * any elements not yet read from the stream.
     *
     * @return The stream's terminator. This is just what terminates() returns,
     *         but following a call to close/0, it must be resolved.
     */
    Object close()
      throws IOException;

    /**
     * Terminate the stream with failure, reporting problem as the reason.
     * <p/>
     * On the writing side, a fail(..) implies a flush(). A stream may fail
     * spontaneously, or one may call fail to indicate a problem that should
     * terminate the stream.
     *
     * @return The stream's terminator. This is just what terminates() returns,
     *         but following a call to fail/1, it must be resolved.
     */
    Object fail(IOException problem)
      throws IOException;
}
