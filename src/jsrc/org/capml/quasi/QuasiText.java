package org.capml.quasi;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.capml.dom.Node;
import org.capml.dom.Text;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.StaticMaker;
import org.erights.e.elib.prim.Thrower;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.FlexList;
import org.erights.e.elib.util.OneArgFunc;

import java.io.IOException;

/**
 * Represents contiguous literal text with a quasi-literal XML tree. <p>
 * <p/>
 * As a ValueMaker, it simply evaluates to the corresponding Text object. As a
 * MatchMaker, it checks that the specimen is the corresponding Text.
 *
 * @author Mark S. Miller
 * @deprecated Use Term trees instead.
 */
public class QuasiText extends QuasiContent {

    static private final long serialVersionUID = -1744822228049984960L;

    static public final StaticMaker QuasiTextMaker =
      StaticMaker.make(QuasiText.class);

    private final String myData;

    /**
     *
     */
    public QuasiText(String data) {
        myData = data;
    }

    /**
     * Uses 'QuasiTextMaker(myIndex)'
     */
    public Object[] getSpreadUncall() {
        Object[] result = {QuasiTextMaker, "run", myData};
        return result;
    }

    /**
     * Since a QuasiText contains no holes, args is ignored and it simply
     * returns the corresponding Text.
     */
    public Object substitute(Object[] args) {
        return new Text(myData);
    }

    /**
     * Since I contain no holes, args and bindings are ignored, and I simply
     * see if specimen is content representing the same Text.
     * <p/>
     * XXX Open issue: who merges adjacent Text nodes?
     */
    public void matchBind(ConstList args,
                          Object specimen,
                          OneArgFunc optEjector,
                          FlexList bindings) {
        Node other = optTheOne(specimen);
        if (null == other || !(other instanceof Text)) {
            throw Thrower.toEject(optEjector, "Must be Text: " + other);
        }
        //and only if they have the same String data
        String otherData = ((Text)other).getData();
        if (!myData.equals(otherData)) {
            throw Thrower.toEject(optEjector,
                                  "Mismatch: " + myData + " vs " + otherData);
        }
    }

    /**
     *
     */
    public void prettyPrintOn(TextWriter out) throws IOException {
        //XXX Have to escape things again
        out.print(myData);
    }
}
