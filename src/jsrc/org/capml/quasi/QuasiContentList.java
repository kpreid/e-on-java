package org.capml.quasi;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.capml.dom.Node;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.StaticMaker;
import org.erights.e.elib.prim.Thrower;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.FlexList;
import org.erights.e.elib.util.OneArgFunc;

import java.io.IOException;

/**
 * Represents a list of QuasiContents, none of which are themselves actually
 * a QuasiContentList, but which may nevertheless represent a list of Nodes.
 * <p/>
 * <p/>
 * When matching a QuasiContentList against a ContentList, we could get fancy
 * and do all sorts of backtracking. However, for Elements for now we choose
 * to define a simple rule: All but the last QuasiContent in the
 * QuasiContentList has to match pairwise with their corresponding Node. The
 * last QuasiContent is then matched against the list of remaining Nodes. <p>
 * <p/>
 * Unfortunately, Text cannot be handled so simply. Nevertheless, since our
 * only need right now is the compiler, we XXX ignore this issue for now.
 *
 * @author Mark S. Miller
 * @deprecated Use Term trees instead.
 */
public class QuasiContentList extends QuasiContent {

    static final long serialVersionUID = -3122022149910166996L;

    static public final StaticMaker QuasiContentListMaker
      = StaticMaker.make(QuasiContentList.class);

    /**
     *
     */
    private final ConstList myQuasis;

    /**
     *
     */
    public QuasiContentList(ConstList quasis) {
        myQuasis = quasis;
    }

    /**
     * Uses 'QuasiContentListMaker(myQuasis)'
     */
    public Object[] getSpreadUncall() {
        Object[] result = {QuasiContentListMaker, "run", myQuasis};
        return result;
    }

    /**
     * Appends together the substitutions of my quasis. <p>
     * <p/>
     * Should merge adjacent Texts, but XXX doesn't do so yet.
     */
    public Object substitute(Object[] args) {
        FlexList result = FlexList.fromType(Node.class);
        int len = myQuasis.size();
        for (int i = 0; i < len; i++) {
            QuasiContent quasi = (QuasiContent)myQuasis.get(i);
            result.append(toContentList(quasi.substitute(args)));
        }
        return toContent(result.snapshot());
    }

    /**
     *
     */
    public void matchBind(ConstList args,
                          Object specimen,
                          OneArgFunc optEjector,
                          FlexList bindings) {
        int len = myQuasis.size();
        ConstList others = toContentList(specimen);
        if (len > others.size()) {
            //With our current simple rule (see class comment) there aren't
            //enough nodes to match against.
            throw Thrower.toEject(optEjector,
                                  "Not enough nodes: " +
                                  len + " vs " + others.size());
        }
        for (int i = 0; i < len - 1; i++) {
            QuasiContent quasi = (QuasiContent)myQuasis.get(i);
            Node other = (Node)others.get(i);

            //with our simple rule, we fail on the first nested failure.
            //We will eventually realize this is silly
            quasi.matchBind(args, other, optEjector, bindings);
        }
        QuasiContent quasi = (QuasiContent)myQuasis.get(len - 1);
        ConstList rest = others.run(len - 1, others.size());
        quasi.matchBind(args, rest, optEjector, bindings);
    }

    /**
     * I just pretty print my quasis in order
     */
    public void prettyPrintOn(TextWriter out) throws IOException {
        int len = myQuasis.size();
        for (int i = 0; i < len; i++) {
            QuasiContent quasi = (QuasiContent)myQuasis.get(i);
            quasi.prettyPrintOn(out);
        }
    }
}
