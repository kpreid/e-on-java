package org.waterken.purchase_promise;

import org.ref_send.promise.Promise;

public interface Shipper {

    Promise<Boolean> canDeliver(String profile);
}
