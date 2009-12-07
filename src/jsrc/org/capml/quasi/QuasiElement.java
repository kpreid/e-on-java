package org.capml.quasi;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.capml.dom.Element;
import org.capml.dom.Node;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.StaticMaker;
import org.erights.e.elib.prim.Thrower;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.FlexList;
import org.erights.e.elib.util.OneArgFunc;
import org.quasiliteral.base.IncompleteQuasiException;

import java.io.IOException;

/**
 * Represents an Element in a quasi-literal XML tree. <p>
 * <p/>
 * A Minimal-XML Element has only a tagName and children. The children are a
 * list of Nodes, which we define as a ContentList, which we define as a kind
 * of Content (see the QuasiContent class comment). Therefore, since the same
 * QuasiElement must match against Elements with different numbers of children,
 * we use a QuasiContent to represent the children of a QuasiElement. <p>
 * <p/>
 * The tagName is either all hole or all literal. If it's literal, it must be a
 * well formed tagName identifier String (not checked). If it's a hole, the
 * corresponding arg or specimen must be such an identifier.
 *
 * @author Mark S. Miller
 * @deprecated Use Term trees instead.
 */
public class QuasiElement extends QuasiContent {

    static private final long serialVersionUID = -4487551194798463363L;

    static public final StaticMaker QuasiElementMaker =
      StaticMaker.make(QuasiElement.class);

    /**
     *
     */
    private final Object myTagName;

    /**
     *
     */
    private final QuasiContent myChildren;

    /**
     * @param tagName    If it's a String then this is the literal tag name. If
     *                   it's a non-negative Integer, then it's a $-hole with
     *                   that index. If it's a negative Integer, then it's a
     *                   @-hole whose 1's complement (~) is the index.
     * @param children represent the quasi-literal XML between the start tag
     *                 and the end tag.
     */
    public QuasiElement(Object tagName, QuasiContent children) {
        myTagName = tagName;
        myChildren = children;
    }

    /**
     * Uses 'QuasiElementMaker(myTagName, myChildren)'
     */
    public Object[] getSpreadUncall() {
        Object[] result = {QuasiElementMaker, "run", myTagName, myChildren};
        return result;
    }

    /**
     * Fill in all my $-holes from args, and return the resulting Element.
     */
    public Object substitute(Object[] args) {
        String tagName;
        if (myTagName instanceof String) {
            tagName = (String)myTagName;
        } else {
            int index = ((Integer)myTagName).intValue();
            if (0 <= index) {
                tagName = (String)args[index];
            } else {
                throw new IncompleteQuasiException(
                  "can't have @-holes in a ValueMaker");
            }
        }
        ConstList children = toContentList(myChildren.substitute(args));
        return new Element(tagName, children);
    }

    /**
     *
     */
    public void matchBind(ConstList args,
                          Object specimen,
                          OneArgFunc optEjector,
                          FlexList bindings) {
        Node other = optTheOne(specimen);
        if (null == other || !(other instanceof Element)) {
            throw Thrower.toEject(optEjector, "Must be an Element: " + other);
        }
        Element otherEl = (Element)other;
        String otherTagName = otherEl.getTagName();
        ConstList otherChildren = otherEl.getChildNodes();

        //check the tags
        if (myTagName instanceof String) {
            if (!myTagName.equals(otherTagName)) {
                throw Thrower.toEject(optEjector,
                                      "Tag mismatch: " + myTagName + "vs " +
                                        otherTagName);
            }
        } else {
            int index = ((Integer)myTagName).intValue();
            if (0 <= index) {
                String tagName = (String)args.get(index);
                if (!tagName.equals(otherTagName)) {
                    throw Thrower.toEject(optEjector,
                                          "${" + index + "} (" + tagName +
                                            ") doesn't match (" +
                                            otherTagName + ")");
                }
            } else {
                index = ~index;
                bind(bindings, index, otherTagName);
            }
        }

        //tags are fine, so we're happy if our children are happy.
        myChildren.matchBind(args, otherChildren, optEjector, bindings);
    }

    /**
     * If the tag is a hole, then the end tag prints as "&lt;/&gt;"
     */
    public void prettyPrintOn(TextWriter out) throws IOException {
        String startTag;
        String endTag = "";
        if (myTagName instanceof String) {
            startTag = (String)myTagName;
            endTag = startTag;
        } else {
            int index = ((Integer)myTagName).intValue();
            if (0 <= index) {
                startTag = "${" + index + "}";
            } else {
                index = ~index;
                startTag = "@{" + index + "}";
            }
        }

        out.print("<", startTag, ">");
        myChildren.prettyPrintOn(out.indent());
        out.println();
        out.print("</", endTag, ">");
    }
}
