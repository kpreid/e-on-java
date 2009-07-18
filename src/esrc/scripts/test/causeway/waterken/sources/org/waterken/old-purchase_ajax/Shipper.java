package org.waterken.purchase_ajax;

public interface Shipper {

    void canDeliver(String profile, Callback tellCanDeliver);
}
