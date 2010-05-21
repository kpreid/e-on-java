package org.waterken.purchase_promise;

import static org.ref_send.promise.Eventual.ref;

import java.io.Serializable;

import org.joe_e.Struct;
import org.ref_send.Record;
import org.ref_send.deserializer;
import org.ref_send.name;
import org.ref_send.promise.Promise;
import org.ref_send.promise.Eventual;

public final class
Product extends Struct implements Record, Serializable {
    static private final long serialVersionUID = 1L;
    
    public final Inventory inventory;
    public final Shipper shipper;

    public @deserializer
    Product(@name("inventory") final Inventory inventory,
            @name("shipper") final Shipper shipper) {
        this.inventory = inventory;
        this.shipper = shipper;
    }     
    
    static public Promise<Product>
    make(final Eventual _) {
        return ref(new Product(InventoryMaker.make(_),
                               ShipperMaker.make(_)));
    }
}
