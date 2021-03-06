package org.waterken.simple_purchase_ajax;

import java.io.Serializable;

import org.joe_e.Struct;
import org.ref_send.promise.Eventual;

public final class
ShipperMaker {
    private ShipperMaker() {}
    
    static public Shipper
    make(final Eventual _) {
        class ShipperX extends Struct implements Shipper, Serializable {
            static private final long serialVersionUID = 1L;
            
            public void
            canDeliver(String profile, Callback tellCanDeliver) {
                _._(tellCanDeliver).run(true);
            }
        }
        return new ShipperX();
    }
}
