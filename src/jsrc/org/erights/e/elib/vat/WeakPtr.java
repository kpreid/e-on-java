package org.erights.e.elib.vat;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.elib.prim.E;
import org.erights.e.elib.prim.Message;

import java.lang.ref.WeakReference;

/**
 * The E-equivalent of WeakReference.
 * <p/>
 * A WeakPtr without an reactor is just an unregistered Java WeakReference.
 * A WeakPtr with an reactor also has the additional behavior of doing
 * <pre>    reactor &lt;- run()</pre> when the referent gets GCed.
 * <p/>
 * Confined E code must not be given access to weak references of any kind,
 * as that would expose them to GC timing non-determinism. This
 * non-determinism can be logged, so it doesn't threaten replay, but it
 * would enable a confined Bob to read a covert channel written by Mallet,
 * and so enable Bob to receive instructions from Mallet.
 *
 * @author Mark S. Miller
 */
public class WeakPtr extends WeakReference {

    /**
     * Package-scope so it can be grabbed by the WeakPtrThread.
     */
    final Vat myVat;

    /**
     * Package-scope so it can be grabbed by the WeakPtrThread.
     */
    final Object myReactor;

    /**
     * The message to be sent to myReactor to inform it that referent is
     * toast.
     * <p/>
     * Stored on creation of this WeakPtr in order to capture the
     * {@link SendingContext}. Package-scope so it can be grabbed by the
     * WeakPtrThread.
     */
    final Message myMessage;

    /**
     * Makes a WeakPtr to referent that eventually notifies the reactor by
     * <pre>    reactor &lt;- verb(args..)</pre>
     * after the referent has been GCed.
     */
    public WeakPtr(Object referent,
                   Object reactor,
                   String verb,
                   Object[] args) {
        super(referent, WeakPtrThread.THE_ONE.getWeakPtrQueue());
        myVat = Vat.getCurrentVat();
        myReactor = reactor;
        myMessage = new Message(null, verb, args);
    }

    /**
     * Makes a WeakPtr to referent that eventually notifies the reactor by
     * <pre>    reactor &lt;- run()</pre>
     * after the referent has been GCed.
     */
    public WeakPtr(Object referent, Runnable reactor) {
        this(referent, reactor, "run", E.NO_ARGS);
    }
}
