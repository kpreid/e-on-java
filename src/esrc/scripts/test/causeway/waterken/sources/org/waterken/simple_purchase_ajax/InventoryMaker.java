package org.waterken.simple_purchase_ajax;

import java.io.Serializable;

import org.joe_e.Struct;
import org.ref_send.promise.Eventual;

public final class
InventoryMaker {
    private InventoryMaker() {}
    
    static public Inventory
    make(final Eventual _) {
        class InventoryX extends Struct implements Inventory, Serializable {
            static private final long serialVersionUID = 1L;

            public void
            partInStock(String partNo,  Callback tellPartInStock) {
                _._(tellPartInStock).run(true);
            }

            public void
            placeOrder(String buyer, String partNo,  Callback tellOrderPlaced) {
                _._(tellOrderPlaced).run(true);
            }
        }
        return new InventoryX();
    }
}
