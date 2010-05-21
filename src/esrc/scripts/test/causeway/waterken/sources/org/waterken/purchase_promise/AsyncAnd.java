package org.waterken.purchase_promise;

import static org.ref_send.promise.Eventual.ref;

import java.io.Serializable;

import org.ref_send.promise.Promise;
import org.ref_send.promise.Channel;
import org.ref_send.promise.Do;
import org.ref_send.promise.Eventual;
import org.ref_send.promise.Resolver;

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
                    myResolver.apply(true);
                } else {
                    myResolver.progress();
                }
            } else {
                myResolver.apply(false);
            }
            return null;
        }

        @Override
        public Void reject(Exception reason) throws Exception {
            _.log.comment("oops");
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
            _.when(answerP, new DoAnswer(expected, result.resolver));
        }
        
        return result.promise;
    }
}
