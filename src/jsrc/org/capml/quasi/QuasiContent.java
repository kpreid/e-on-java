package org.capml.quasi;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.capml.dom.Node;
import org.capml.dom.Text;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.serial.PassByConstruction;
import org.erights.e.elib.serial.Persistent;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.FlexList;
import org.erights.e.elib.tables.Selfless;
import org.erights.e.elib.util.AlreadyDefinedException;
import org.erights.e.elib.util.OneArgFunc;
import org.quasiliteral.base.MatchMaker;

import java.io.IOException;

/**
 * Represents a quasi-literal list of dom Nodes. <p>
 * <p/>
 * We define "Content" to be either a concrete Node, or a ConstList of
 * concrete Nodes. The latter we also define as a "ContentList".
 * As a ValueMaker, a QuasiContent will evaluate to a ContentList. As a
 * MatchMaker, it will match against Content, and either succeed, extracting
 * values, or fail. The Minimal-XML quasi-parser produces a QuasiContent.
 *
 * @author Mark S. Miller
 * @deprecated Use Term trees instead.
 */
public abstract class QuasiContent
  implements Persistent, Selfless, PassByConstruction, MatchMaker {

    static private final long serialVersionUID = 1194569409508874930L;

    /**
     * Coerce 'content', which must be Content (as defined in the class
     * comment) or a String into a ContentList (as also defined there). <p>
     * <p/>
     * If 'content' is a Node, a singleton list containing that Node is
     * returned. If it's a String, a singleton list of a Text on that String
     * is returned. Otherwise, content must be a ConstList of Nodes, and is
     * returned.
     */
    static ConstList toContentList(Object content) {
        if (content instanceof Node) {
            Object[] result = {content};
            return ConstList.fromArray(result);
        } else if (content instanceof String) {
            Object[] result = {new Text((String)content)};
            return ConstList.fromArray(result);
        } else {
            return (ConstList)content;
        }
    }

    /**
     * If content represents a single Node, return it.
     * <p/>
     * If it's a String, return the corresponding Text. Otherwise return
     * null. If content isn't valid Content, it's ok to throw an exception
     * instead of returning null.
     */
    static Node optTheOne(Object content) {
        if (content instanceof Node) {
            return (Node)content;
        } else if (content instanceof String) {
            return new Text((String)content);
        } else {
            ConstList list = (ConstList)content;
            if (list.size() == 1) {
                return (Node)list.get(0);
            } else {
                return null;
            }
        }
    }

    /**
     * Coerce 'content', which must be Content (as defined in the class
     * comment) or a String into a Content (as also defined there).
     * <p/>
     * If 'content' is a Node, it's
     * returned. If it's a String, a Text on that String
     * is returned. Otherwise, it must be a ConstList of Nodes. If it's a
     * singleton list, it's one member is returned. Otherwise the list is
     * returned.
     */
    static Object toContent(Object content) {
        Node result = optTheOne(content);
        if (null == result) {
            return content;
        } else {
            return result;
        }
    }

    /**
     * A convenience for placing @-hole matches into bindings. XXX Note that
     * it will not detect multiple stores of null to the same index.
     */
    static void bind(FlexList bindings, int index, Object value) {
        bindings.ensureSize(index + 1);
        if (bindings.get(index) != null) {
            throw new AlreadyDefinedException("conflict defining @{"
                                              + index + "}");
        }
        bindings.put(index, value);
    }

    /**
     * As a ValueMaker, given a list of args, use each arg to fill in the
     * corresponding $-hole, and return the resulting concrete Content --
     * either a Node or a ConstList of Nodes. <p>
     * <p/>
     * The args and return value must be concrete (not quasi). If it's
     * filling in an Element tagName, the arg must be a String. Otherwise it
     * may be either a String or Content.
     */
    public abstract Object substitute(Object[] args);

    /**
     * As a MatchMaker, while using the args to fill in the $-holes, pattern
     * match against the specimen, which must be Content or String, and
     * return the list of values extracted by @-holes.
     * <p/>
     * The args, specimen, and return value must be concrete. The args must
     * follow the same rules as the args to substitute(). The specimen may be
     * either Content or String. The returned list of values extracted by
     *
     * @-holes are Strings, if they were extracted by a tagName @-hole, or a
     * Content, if they were extracted by an @-hole in content position.
     */
    public ConstList matchBind(ConstList args,
                               Object specimen,
                               OneArgFunc optEjector) {
        FlexList bindings = FlexList.make();
        matchBind(args, specimen, optEjector, bindings);
        return bindings.snapshot();
    }

    /**
     * As a MatchMaker, while using the args to fill in the $-holes, pattern
     * match against the specimen, which must be Content, and put the values
     * extracted by the @-holes into 'bindings'.
     * <p/>
     * The args, specimen, and the values put into bindings must be
     * concrete. The args must follow the same rules as the args to
     * substitute(). The specimen may be either Content or String. The
     * values extracted by @-holes are Strings, if they were extracted by a
     * tagName @-hole, or a Content, if they were extracted by an @-hole in
     * content position.
     */
    public abstract void matchBind(ConstList args,
                                   Object specimen,
                                   OneArgFunc optEjector,
                                   FlexList bindings);

    /**
     * Prints as "sml`...`", where pretty printed quasi-XML appears between
     * the backquotes
     */
    public void printOn(TextWriter out) throws IOException {
        out.print("sml`");
        prettyPrintOn(out.indent());
        out.print("`");
    }

    /**
     * Prints pretty printed quasi-XML
     */
    public abstract void prettyPrintOn(TextWriter out) throws IOException;
}
