package org.waterken.purchase_ajax;

import static org.ref_send.promise.Fulfilled.ref;

import java.io.Serializable;

import org.joe_e.Struct;
import org.ref_send.Record;
import org.ref_send.deserializer;
import org.ref_send.name;
import org.ref_send.promise.Promise;
import org.ref_send.promise.eventual.Eventual;

public final class
Product extends Struct implements Record, Serializable {
    static private final long serialVersionUID = 1L;
    
    public final Inventory inventory;
    public final CreditBureau creditBureau;
    public final Shipper shipper;

    public @deserializer
    Product(@name("inventory") final Inventory inventory,
            @name("creditBureau") final CreditBureau creditBureau,
            @name("shipper") final Shipper shipper) {
        this.inventory = inventory;
        this.creditBureau = creditBureau;
        this.shipper = shipper;
    }     
    
    static public Promise<Product>
    make(final Eventual _) {
        return ref(new Product(InventoryMaker.make(_),
                               CreditBureauMaker.make(_),
                               ShipperMaker.make(_)));
    }
}
