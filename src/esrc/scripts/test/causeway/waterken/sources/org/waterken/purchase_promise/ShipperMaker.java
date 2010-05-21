package org.waterken.purchase_promise;

import static org.ref_send.promise.Eventual.ref;

import java.io.Serializable;

import org.joe_e.Struct;
import org.ref_send.promise.Promise;
import org.ref_send.promise.Eventual;

public class
ShipperMaker {
    private ShipperMaker() {}
    
    static public Shipper
    make(final Eventual _) {
        class ShipperX extends Struct implements Shipper, Serializable {
            static private final long serialVersionUID = 1L;
            
            public Promise<Boolean>
            canDeliver(String profile) {
                return ref(true);
            }
        }
        return new ShipperX();
    }
}