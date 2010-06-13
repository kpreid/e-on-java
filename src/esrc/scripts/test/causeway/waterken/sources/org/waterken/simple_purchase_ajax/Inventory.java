package org.waterken.simple_purchase_ajax;

public interface Inventory {

    void partInStock(String partNo, Callback tellPartInStock);
    
    void placeOrder(String buyer, 
                    String partNo, 
                    Callback tellOrderPlaced);
}
