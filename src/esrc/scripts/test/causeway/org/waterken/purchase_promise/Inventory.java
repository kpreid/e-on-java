package org.waterken.purchase_promise;

import org.ref_send.promise.Promise;

public interface Inventory {

    Promise<Boolean> isAvailable(String partNo);
    
    Promise<Boolean> placeOrder(String buyer, String partNo);
}
