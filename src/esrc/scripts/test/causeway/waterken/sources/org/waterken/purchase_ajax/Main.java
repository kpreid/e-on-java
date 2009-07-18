package org.waterken.purchase_ajax;
//Copyright 2008 Teleometry Design. under the terms of the MIT X license
//found at http://www.opensource.org/licenses/mit-license.html

import java.io.Serializable;

import org.ref_send.promise.Do;
import org.ref_send.promise.Eventual;
import org.ref_send.promise.Promise;
import org.ref_send.promise.Vat;
import org.waterken.bang.Drum;

import static org.ref_send.promise.Eventual.ref;

/**
 * An introduction to eventual operations in Java.
 * <p>
 * This class provides an introduction to eventual operations by using them to
 * update and query a counter held in an object of type {@link Drum}.
 * </p>
 */
public final class
Main {
    private Main() {}
    
    static public final String partNo = "123abc";
    static public final String name = "West Coast Buyers";
    static public final String profile = "West Coast Buyers Profile";

    /**
     * Constructs an instance.
     * @throws Exception 
     */
    static public Promise<Product>
    make(final Eventual _) throws Exception {
        class Buy extends Do<Product,Promise<Product>> implements Serializable {
            static private final long serialVersionUID = 1L;
            
            private final CreditBureau creditBureau;
            
            public Buy(CreditBureau creditBureau) {
                this.creditBureau = creditBureau;
            }

            public @Override Promise<Product>
            fulfill(final Product product) {
                Inventory inventory = product.inventory;
                Shipper shipper = product.shipper;
                
                Callback teller =
                    new AsyncAnd(_, 3, checkAnswers(_, inventory));
                
                _._(inventory).partInStock(partNo, teller);
                _._(creditBureau).checkCredit(name, teller);
                _._(shipper).canDeliver(profile, teller);
                return ref(product);
            }
        }
        final Vat<Promise<Product>> prodVat = _.spawn("product", Product.class);
        final Vat<CreditBureau> credVat = _.spawn("accounts", CreditBureauMaker.class);
        return _.when(prodVat.top, new Buy(credVat.top));
    }
    
    static private Callback
    checkAnswers(final Eventual _, final Inventory inventory) {
        class CheckAnswers implements Callback, Serializable {
            static private final long serialVersionUID = 1L;

            public void
            run(boolean allOK) {
                if (allOK) {
                    _._(inventory).placeOrder(name, partNo, tellOrderPlaced(_));
                }
            }
        }
        return new CheckAnswers();
    }
    
    static private Callback
    tellOrderPlaced(final Eventual _) {
        class TellOrderPlaced implements Callback, Serializable {
            static private final long serialVersionUID = 1L;
        
            public void
            run(boolean placed) {
                if (placed) {
                    _.log.comment("Order placed for " + name + ", " + partNo);
                } else {
                    _.log.comment("Order for " + name + ", " + partNo + 
                                  " not placed");
                }
            }
        }
        return new TellOrderPlaced();
    }
}
