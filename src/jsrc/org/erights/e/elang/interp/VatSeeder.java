package org.erights.e.elang.interp;

/*
Copyright University of Southampton IT Innovation Centre, 2010,
under the terms of the MIT X license, available from
http://www.opensource.org/licenses/mit-license.html
*/

import org.erights.e.elang.evm.EExpr;
import org.erights.e.elang.scope.Scope;
import org.erights.e.elang.syntax.EParser;
import org.erights.e.elib.tables.ConstMap;
import org.erights.e.elib.tables.Twine;

/** The object passed to Vat.seed.
 * This is implemented in Java because the argument to Vat.seed can't be E, since E
 * scopes aren't thread-safe.
 */
public class VatSeeder {
    private final Twine myPrivSrc;
    private final ConstMap myProps;

    public VatSeeder(Twine privSrc, ConstMap props) {
        myPrivSrc = privSrc;
        myProps = props;
    }

    public class BogusInterp {
        public ConstMap getProps() {
            return myProps;
        }
    }

    public Object run() {
        final EExpr privExpr = (EExpr) EParser.run(myPrivSrc);

        Scope privScope = ScopeSetup.privileged("__main$",
                null,
                null,
                null,
                myProps,
                new BogusInterp(),
                null);
        return privExpr.eval(privScope);
    }
}
