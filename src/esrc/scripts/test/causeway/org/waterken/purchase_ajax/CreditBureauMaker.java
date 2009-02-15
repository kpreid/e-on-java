package org.waterken.purchase_ajax;

import java.io.Serializable;

import org.joe_e.Struct;
import org.ref_send.promise.eventual.Eventual;

public final class
CreditBureauMaker {
    private CreditBureauMaker() {}
    
    static public CreditBureau
    make(final Eventual _) {
        class CreditBureauX extends Struct implements CreditBureau,Serializable{
            static private final long serialVersionUID = 1L;
            
            public void
            doCreditCheck(String name, Callback tellIsCreditOK) {
                _.log.comment("credit ok");
                _._(tellIsCreditOK).run(true);
            }
        }
        return new CreditBureauX();
    }
}
