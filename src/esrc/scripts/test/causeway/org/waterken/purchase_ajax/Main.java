package org.waterken.purchase_ajax;
//Copyright 2008 Teleometry Design. under the terms of the MIT X license
//found at http://www.opensource.org/licenses/mit-license.html

import java.io.Serializable;

import org.ref_send.promise.Promise;
import org.ref_send.promise.eventual.Do;
import org.ref_send.promise.eventual.Eventual;
import org.waterken.bang.Drum;

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
     */
    static public void
    make(final Eventual _) {
        class Buy extends Do<Product,Void> implements Serializable {
            static private final long serialVersionUID = 1L;
            
            public @Override Void
            fulfill(final Product product) {
                Callback teller =
                    new AsyncAnd(_, 3, checkAnswers(_, product.inventory));
                
                _._(product.inventory).isAvailable(partNo, teller);
                _._(product.creditBureau).doCreditCheck(name, teller);
                _._(product.shipper).canDeliver(profile, teller);
                return null;
            }
        }
        final Promise<Product> child = _.spawn("product", Product.class);
        _.when(child, new Buy());
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
