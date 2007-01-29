// Copyright 2004 Hewlett Packard, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package test.joee;

import org.erights.e.develop.assertion.T;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.ref.Ref;
import org.erights.e.elib.sealing.Brand;
import org.erights.e.elib.sealing.SealedBox;
import org.erights.e.elib.sealing.Sealer;
import org.erights.e.elib.sealing.Unsealer;
import org.erights.e.elib.sealing.UnsealingException;
import org.erights.e.elib.util.OneArgFunc;

import java.math.BigInteger;

public class Mint {

    private final Sealer mySealer;
    private final Unsealer myUnsealer;

    public class Purse {

        private BigInteger myBalance;
        private final Decr myDecr;

        private class Decr {

            public void decr(BigInteger amount) {
                T.require(
                  0 <= amount.signum() && 0 >= amount.compareTo(myBalance),
                  "oops");
                myBalance = myBalance.subtract(amount);
            }
        }

        Purse(BigInteger balance) {
            T.require(0 <= balance.signum(), "oops");
            myBalance = balance;
            myDecr = new Decr();
        }

        public BigInteger getBalance() {
            return myBalance;
        }

        public Purse makePurse() {
            return new Purse(BigInteger.ZERO);
        }

        public SealedBox getDecr() {
            return mySealer.seal(myDecr);
        }

        public void deposit(BigInteger amount, Purse src)
          throws UnsealingException {
            ((Decr)myUnsealer.unseal(src.getDecr())).decr(amount);
            myBalance = myBalance.add(amount);
        }
    }

    public Mint(String name) {
        Object[] pair = Brand.run(name);
        mySealer = (Sealer)pair[0];
        myUnsealer = (Unsealer)pair[1];
    }

    public Purse makePurse(BigInteger balance) {
        return new Purse(balance);
    }
}

class Alice {

    private final Ref myPurse;
    private final Bob myBob;

    Alice(Ref purse, Bob bob) {
        myPurse = purse;
        myBob = bob;
    }

    void payBob() {
        Ref payment = E.send(myPurse, "makePurse");
        E.sendOnly(payment, "deposit", BigInteger.valueOf(10), myPurse);
        E.sendOnly(myBob, "accept", payment);
    }
}

class Bob {

    private final Ref myPurse;

    Bob(Ref purse) {
        myPurse = purse;
    }

    public void accept(Mint.Purse payment) {
        Ref ackVow =
          E.send(myPurse, "deposit", BigInteger.valueOf(10), payment);
        Ref.whenResolvedOnly(ackVow, new OneArgFunc() {
            public Object run(Object ack) {
                T.require(!Ref.isBroken(ack), "oops");
                // ... react to being paid.
                return null;
            }
        });
    }
}

