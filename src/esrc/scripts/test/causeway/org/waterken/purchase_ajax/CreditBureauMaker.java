package org.waterken.purchase_ajax;

import java.io.Serializable;

import org.joe_e.Struct;
import org.ref_send.promise.Eventual;

public final class
CreditBureauMaker {
    private CreditBureauMaker() {}
    
    static public CreditBureau
    make(final Eventual _) {
        class CreditBureauX extends Struct implements CreditBureau,Serializable{
            static private final long serialVersionUID = 1L;
            
            public boolean
            currentStatusOK(int acct) { 
                return true; 
            }
            
            public boolean
            paymentHistoryOK(int acct) { 
                return true; 
            }
            
            public void
            checkCreditScore(int acctNo, Callback tellCreditOK) { 
                if (paymentHistoryOK(acctNo) && currentStatusOK(acctNo)) {
                    _._(tellCreditOK).run(true);
                } else {
                    _._(tellCreditOK).run(false);
                }               
            }

            public int
            lookupAcctNo(String name) { 
                return 1; 
            }
            
            public void
            checkCredit(String name, Callback tellCreditOK) {
                int acctNo = lookupAcctNo(name);
                checkCreditScore(acctNo, tellCreditOK);
            }
        }
        return new CreditBureauX();
    }
}
