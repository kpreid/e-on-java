package org.waterken.purchase_ajax;

import java.io.Serializable;

import org.joe_e.Struct;
import org.ref_send.promise.eventual.Eventual;

public final class
InventoryMaker {
    private InventoryMaker() {}
    
    static public Inventory
    make(final Eventual _) {
        class InventoryX extends Struct implements Inventory, Serializable {
            static private final long serialVersionUID = 1L;

            public void
            isAvailable(String partNo,  Callback tellIsAvailable) {
                _.log.comment("is available");
                _._(tellIsAvailable).run(true);
            }

            public void
            placeOrder(String buyer, String partNo,  Callback tellOrderPlaced) {
                _.log.comment("placing order");
                _._(tellOrderPlaced).run(true);
            }
        }
        return new InventoryX();
    }
}
