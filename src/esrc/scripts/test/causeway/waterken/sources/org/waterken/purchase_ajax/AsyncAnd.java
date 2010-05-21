package org.waterken.purchase_ajax;

import static org.ref_send.promise.Eventual.ref;

import java.io.Serializable;

import org.ref_send.promise.Promise;
import org.ref_send.promise.Channel;
import org.ref_send.promise.Do;
import org.ref_send.promise.Eventual;
import org.ref_send.promise.Resolver;

/**
 * An asynchronous adaptation of the conjunctive and operator.
 * <p>
 * This class is an asynchronous adaptation of the conjunctive and operator,
 * familiar from seqential programming. It reports true to its callback
 * function only if every expected answer is true. It promptly reports false
 * if an expected answer is false, thus short-circuiting the logic.
 * </p>
 * <p>
 * Promises are not required but a single promise-resolver pair is used here
 * to improve event logging for post-mortem debugging.
 * </p>
 */

public class AsyncAnd implements Callback, Serializable {
    static private final long serialVersionUID = 1L;
    
    private final class DoAnswer extends Do<Boolean, Void> implements Serializable {
        static private final long serialVersionUID = 1L;
    
        private final Eventual _;
        private final Callback tellAreAllTrue;
        
        private DoAnswer(Eventual _, Callback tellAreAllTrue) {
            this._ = _;
            this.tellAreAllTrue = tellAreAllTrue;
        }
        
        @Override
        public Void fulfill(Boolean answer) throws Exception {
            _._(tellAreAllTrue).run(answer);
            return null;
        }
    }
    
    /**
     * Constructs an instance.
     * @param _ eventual operator
     * @param expected number of expected answers
     * @param tellAreAllTrue callback to report result to
     */
     
    private final Eventual _;
    private int expected;
    private Resolver<Boolean> resolver;
    
    public AsyncAnd(Eventual _, int expected, Callback tellAreAllTrue) {
        super();
        this._ = _;
        this.expected = expected;
        Channel<Boolean> channel = _.defer();
        this.resolver = channel.resolver;
        
        /*
         * Register a when-block on the resolution of the promise.
         */
        _.when(channel.promise, new DoAnswer(_, tellAreAllTrue));
    }

    public void run(boolean answer) {
        if (resolver != null) {
            if (answer) {
                expected -= 1;
                if (expected == 0) {
                    /*
                     * Resolve the promise with true.
                     */
                    resolver.apply(true);
                    resolver = null;
                } else {
                    /*
                     * Progress had been made in resolving the promise.
                     * If logging is on, a Progressed event record is written.
                     * If logging is off, this is a noop.
                     */
                    resolver.progress();
                }
            } else {
                /*
                 * Resolve the promise with false. Notice that this 
                 * short-circuits the logic: any remaining expected answers
                 * are ignored.
                 */
                resolver.apply(false);
                resolver = null;
            }
        }
    }
}
