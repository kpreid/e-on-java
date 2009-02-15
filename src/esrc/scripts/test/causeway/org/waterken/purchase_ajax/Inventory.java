package org.waterken.purchase_ajax;

public interface Inventory {

    void isAvailable(String partNo, Callback tellIsAvailable);
    
    void placeOrder(String buyer, 
                    String partNo, 
                    Callback tellOrderPlaced);
}
