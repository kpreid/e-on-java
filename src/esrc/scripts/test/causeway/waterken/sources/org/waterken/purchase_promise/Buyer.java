package org.waterken.purchase_promise;

import java.io.Serializable;

import org.ref_send.promise.Promise;
import org.ref_send.promise.eventual.Do;
import org.ref_send.promise.eventual.Eventual;

public class Buyer {
    private Buyer() {}
    
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
                final Promise<Boolean> allOkP = new AsyncAnd(_).run(
                    _._(product.inventory).isAvailable(partNo),
                    _._(product.creditBureau).doCreditCheck(name),
                    _._(product.shipper).canDeliver(profile)
                );
                _.when(allOkP, checkAnswers(_, product.inventory));
                return null;
            }
        }
        final Promise<Product> child = _.spawn("product", Product.class);
        _.when(child, new Buy());
    }
    
    static private Do<Boolean,Void>
    checkAnswers(final Eventual _, final Inventory inventory) {
        class CheckAnswers extends Do<Boolean,Void> implements Serializable {
            static private final long serialVersionUID = 1L;

            public Void
            fulfill(Boolean allOk) {
                if (allOk) {
                    Promise<Boolean> placedP = 
                        _._(inventory).placeOrder(name, partNo);
                    _.when(placedP, tellOrderPlaced(_));
                }
                return null;
            }
        }
        return new CheckAnswers();
    }
    
    static private Do<Boolean,Void>
    tellOrderPlaced(final Eventual _) {
        class TellOrderPlaced extends Do<Boolean,Void> implements Serializable {
            static private final long serialVersionUID = 1L;
        
            public Void
            fulfill(Boolean placed) throws Exception {
                if (placed) {
                    _.log.comment("Order placed for " + name + ", " + partNo);
                } else {
                    _.log.comment("Order for " + name + ", " + partNo + 
                                  " not placed");
                }
                return null;
            }
        }
        return new TellOrderPlaced();
    }
}
