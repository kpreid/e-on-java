package org.waterken.purchase_promise;

import java.io.Serializable;

import org.joe_e.Struct;
import org.ref_send.promise.Fulfilled;
import org.ref_send.promise.Promise;
import org.ref_send.promise.eventual.Eventual;

public final class CreditBureauMaker {
    private CreditBureauMaker() {}
    
    static public CreditBureau
    make(final Eventual _) {
        class CreditBureauX extends Struct implements CreditBureau,Serializable{
            static private final long serialVersionUID = 1L;

            public Promise<Boolean>
            doCreditCheck(String name) {
                _.log.comment("credit ok");
                return Fulfilled.ref(true);
            }
        }
        return new CreditBureauX();
    }
}
