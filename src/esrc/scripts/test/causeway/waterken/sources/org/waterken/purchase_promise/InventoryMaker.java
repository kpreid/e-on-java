package org.waterken.purchase_promise;

import static org.ref_send.promise.Eventual.ref;

import java.io.Serializable;

import org.joe_e.Struct;
import org.ref_send.promise.Promise;
import org.ref_send.promise.Eventual;

public final class InventoryMaker {
    private InventoryMaker() {}
    
    static public Inventory
    make(final Eventual _) {
        class InventoryX extends Struct implements Inventory, Serializable {
            static private final long serialVersionUID = 1L;
            
            public Promise<Boolean>
            partInStock(String partNo) {
                return ref(true);
            }

            public Promise<Boolean>
            placeOrder(String buyer, String partNo) {
                return ref(true);
            }
        }
        return new InventoryX();
    }
}
