package org.waterken.purchase_promise;

import java.io.Serializable;

import static org.ref_send.promise.Eventual.ref;

import org.ref_send.promise.Promise;
import org.ref_send.promise.Do;
import org.ref_send.promise.Eventual;
import org.ref_send.promise.Vat;

/**
 * A simple distributed procedure for handling new purchase orders.
 * <p>
 * Before an order is placed, certain conditions must be met: the item is in
 * stock and available, the customer's account is in good standing and the 
 * delivery options are up to date.
 * </p>
 * <p>
 * An object residing in the "buyer" vat has remote references to objects
 * residing in the "product" and "accounts" vats. The buyer queries the
 * remote objects with promise-based asynchronous message sends.
* </p>
 * <p>
 * The answers from the asynchronous queries must be collected and examined to
 * verify that all requirements are satisfied before placing the order.
 * The solution is an asynchronous adaptation of the conjunctive and operator,
 * familiar from sequential programming, implemented by {@link AsyncAnd}.
 * </p>
 */

public class Buyer {
    private Buyer() {}
    
    static public final String partNo = "123abc";
    static public final String name = "West Coast Buyers";
    static public final String profile = "West Coast Buyers Profile";

    /**
     * Constructs an instance.
     */
    static public Promise<Product>
    make(final Eventual _) {
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

                Promise<Boolean> partP = _._(inventory).partInStock(partNo);
                Promise<Boolean> creditP = _._(creditBureau).checkCredit(name);
                Promise<Boolean> deliverP = _._(shipper).canDeliver(profile);

                final Promise<Boolean> allOkP = 
                    new AsyncAnd(_).run(partP, creditP, deliverP);
                        
                /*
                 * Register a when-block on promise returned by AsyncAnd. 
                 * The block executes when the promise resolves (either 
                 * fulfilled or rejected).
                 */

                _.when(allOkP, checkAnswers(_, inventory));
                return ref(product);
            }
        }
        final Vat<Promise<Product>> prodVat = _.spawn("product", Product.class);
        final Vat<CreditBureau> credVat = _.spawn("accounts", CreditBureauMaker.class);
        return _.when(prodVat.top, new Buy(credVat.top));
    }
    
    static private Do<Boolean,Void>
    checkAnswers(final Eventual _, final Inventory inventory) {
        class CheckAnswers extends Do<Boolean,Void> implements Serializable {
            static private final long serialVersionUID = 1L;

            public Void
            fulfill(Boolean allOK) {
                if (allOK) {
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
