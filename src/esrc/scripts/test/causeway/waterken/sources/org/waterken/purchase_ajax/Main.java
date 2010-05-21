package org.waterken.purchase_ajax;
//Copyright 2008 Teleometry Design. under the terms of the MIT X license
//found at http://www.opensource.org/licenses/mit-license.html

import java.io.Serializable;

import org.ref_send.promise.Do;
import org.ref_send.promise.Eventual;
import org.ref_send.promise.Promise;
import org.ref_send.promise.Vat;

import static org.ref_send.promise.Eventual.ref;

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
 * remote objects with asynchronous message sends, implemented as Ajax-style
 * continuation passing. 
 * </p>
 * <p>
 * The answers from the asynchronous queries must be collected and examined to
 * verify that all requirements are satisfied before placing the order.
 * The solution is an asynchronous adaptation of the conjunctive and operator,
 * familiar from sequential programming, implemented by {@link AsyncAnd}.
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
                
                /*
                * To collect the three answers, teller is passed as an argument
                * to each of the remote queries, serving as a callback function.
                */
                Callback teller = new AsyncAnd(_, 3, checkAnswers(_, inventory));

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
