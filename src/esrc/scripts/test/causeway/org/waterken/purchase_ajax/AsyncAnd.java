package org.waterken.purchase_ajax;

import java.io.Serializable;

import org.ref_send.promise.eventual.Eventual;

public class AsyncAnd implements Callback, Serializable {
    static private final long serialVersionUID = 1L;
    
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
        if (answer) {
            expected -= 1;
            if (expected == 0) {
                if (tellAreAllTrue != null) {
                    _.log.comment("happened: all true");
                    _._(tellAreAllTrue).run(true);
                    tellAreAllTrue = null;
                }
            } else {
                _.log.comment("leadsto: all true");
            }
        } else {
            if (tellAreAllTrue != null) {
                _.log.comment("found a false");
                _._(tellAreAllTrue).run(false);
                tellAreAllTrue = null;
            }
        }
    }
}
