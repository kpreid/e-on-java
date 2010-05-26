package org.waterken.purchase_promise;

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
 */

public class AsyncAnd implements Serializable {
    static private final long serialVersionUID = 1L;
    
    private final class DoAnswer extends Do<Boolean, Void> implements Serializable {
        static private final long serialVersionUID = 1L;
        
        private final int[] myExpected;
        private final Resolver<Boolean> myResolver;

        private DoAnswer(int[] expected, Resolver<Boolean> resolver) {
            myExpected = expected;
            myResolver = resolver;
        }

        @Override
        public Void fulfill(Boolean answer) throws Exception {
            if (answer) {
                myExpected[0]--;
                if (myExpected[0] == 0) {
                    /*
                     * Resolve the promise with true.
                     */
                    myResolver.apply(true);
                } else {
                    /*
                     * Progress had been made in resolving the promise.
                     * If logging is on, a Progressed event record is written.
                     * If logging is off, this is a noop.
                     */
                    myResolver.progress();
                }
            } else {
                /*
                 * Resolve the promise with false. Notice that this 
                 * short-circuits the logic: any remaining expected answers
                 * are ignored.
                 */
                myResolver.apply(false);
            }
            return null;
        }

        @Override
        public Void reject(Exception reason) throws Exception {
            _.log.comment("Promise rejected.");
            myResolver.reject(reason);
            return null;
        }
    }
    
    private final Eventual _;
    
    public AsyncAnd(Eventual _) {
        this._ = _;
    }
    
    public Promise<Boolean> run(Promise<Boolean>... answers) {
        final Channel<Boolean> result = _.defer();
        final int[] expected = {answers.length};
        for (Promise<Boolean> answerP : answers) {
            /*
             * Register a when-block on each promise. The block executes
             * when the promise resolves (either fulfilled or rejected).
             */
            _.when(answerP, new DoAnswer(expected, result.resolver));
        }
        
        return result.promise;
    }
}
