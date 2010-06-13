package org.waterken.simple_purchase_ajax;

import java.io.Serializable;

import org.ref_send.promise.Eventual;


/**
 * An asynchronous adaptation of the conjunctive and operator.
 * <p>
 * This class is an asynchronous adaptation of the conjunctive and operator,
 * familiar from seqential programming. It reports true to its callback
 * function only if every expected answer is true. It promptly reports false
 * if an expected answer is false, thus short-circuiting the logic.
 * </p>
 */

public class AsyncAnd implements Callback, Serializable {
    static private final long serialVersionUID = 1L;
    
    /**
     * Constructs an instance.
     * @param _ eventual operator
     * @param expected number of expected answers
     * @param tellAreAllTrue callback to report result to
     */
     
    private final Eventual _;
    private int expected;
    private Callback tellAreAllTrue;
    
    public AsyncAnd(Eventual _, int expected, Callback tellAreAllTrue) {
        super();
        this._ = _;
        this.expected = expected;
        this.tellAreAllTrue = tellAreAllTrue;
    }

    public void run(boolean answer) {
        if (tellAreAllTrue != null) {
            if (answer) {
                expected -= 1;
                if (expected == 0) {
                    _._(tellAreAllTrue).run(true);
                    tellAreAllTrue = null;
                }             
            } else {
                _._(tellAreAllTrue).run(false);
                tellAreAllTrue = null;
            }
        }
    }
}
