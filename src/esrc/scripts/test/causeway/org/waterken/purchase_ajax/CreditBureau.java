package org.waterken.purchase_ajax;

public interface CreditBureau {

    void doCreditCheck(String name, Callback tellIsCreditOK);
}
