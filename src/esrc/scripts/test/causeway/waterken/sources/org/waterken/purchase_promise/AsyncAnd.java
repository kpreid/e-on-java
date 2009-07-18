package org.waterken.purchase_promise;

import java.io.Serializable;

import org.ref_send.promise.Promise;
import org.ref_send.promise.eventual.Channel;
import org.ref_send.promise.eventual.Do;
import org.ref_send.promise.eventual.Eventual;
import org.ref_send.promise.eventual.Resolver;

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
                    _.log.comment("happened: all true");
                    myResolver.run(true);
                } else {
                    _.log.comment("leadsto: all true");
                }
            } else {
                _.log.comment("found a false");
                myResolver.run(false);
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
