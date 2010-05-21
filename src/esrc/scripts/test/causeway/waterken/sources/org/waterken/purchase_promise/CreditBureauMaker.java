package org.waterken.purchase_promise;

import static org.ref_send.promise.Eventual.ref;

import java.io.Serializable;

import org.joe_e.Struct;
import org.ref_send.promise.Promise;
import org.ref_send.promise.Eventual;

public final class CreditBureauMaker {
    private CreditBureauMaker() {}
    
    static public CreditBureau
    make(final Eventual _) {
        class CreditBureauX extends Struct implements CreditBureau,Serializable{
            static private final long serialVersionUID = 1L;

            public Promise<Boolean>
            checkCredit(String name) {
                return ref(true);
            }
        }
        return new CreditBureauX();
    }
}
