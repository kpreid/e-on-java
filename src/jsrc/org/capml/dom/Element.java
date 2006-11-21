package org.capml.dom;

/*
 * Copyright (c) 2000 World Wide Web Consortium,
 * (Massachusetts Institute of Technology, Institut National de
 * Recherche en Informatique et en Automatique, Keio University). All
 * Rights Reserved. This program is distributed under the W3C's Software
 * Intellectual Property License. This program is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE. See W3C License http://www.w3.org/Consortium/Legal/ for more
 * details.
 *
 * This is a substantial revision by ERights.org of the file as
 * released by the w3c, as is allowed by the license. See the package
 * comment.
 */

import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.prim.StaticMaker;
import org.erights.e.elib.tables.ConstList;
import org.quasiliteral.astro.Astro;
import org.quasiliteral.astro.AstroArg;
import org.quasiliteral.astro.AstroBuilder;

import java.io.IOException;

/**
 * Represents everything in a Minimal-XML document from a start tag to an end
 * tag, inclusive. <p>
 * <p/>
 * This is a substantial revision by ERights.org of the file as released by the
 * w3c, as is allowed by the license. See the package comment. The original
 * class comment: <p>
 * <p/>
 * <blockquote> The <tt>Element</tt> interface represents an element in an HTML
 * or XML document. Elements may have attributes associated with them; since
 * the <tt>Element</tt> interface inherits from <tt>Node</tt> , the generic
 * <tt>Node</tt> interface attribute <tt>attributes</tt> may be used to
 * retrieve the set of all attributes for an element. There are methods on the
 * <tt>Element</tt> interface to retrieve either an <tt>Attr</tt> object by
 * name or an attribute value by name. In XML, where an attribute value may
 * contain entity references, an <tt>Attr</tt> object should be retrieved to
 * examine the possibly fairly complex sub-tree representing the attribute
 * value. On the other hand, in HTML, where all attributes have simple string
 * values, methods to directly access an attribute value can safely be used as
 * a convenience. In DOM Level 2, the method <tt>normalize</tt> is inherited
 * from the <tt>Node</tt> interface where it was moved. </blockquote>
 *
 * @deprecated Use Term trees instead.
 */
public class Element extends Node {

    static private final long serialVersionUID = -6137794451491033993L;

    static public final StaticMaker ElementMaker =
      StaticMaker.make(Element.class);

    /**
     * @serial the name after the &lt;
     */
    private final String myTagName;

    /**
     * @serial A list of Nodes. myChildren represent the stuff between the
     * start tag and the end tag.
     */
    private final ConstList myChildren;

    /**
     *
     */
    public Element(String tagName, ConstList children) {
        myTagName = tagName;
        myChildren = children;
    }

    /**
     * Uses 'ElementMaker(myTagName, myChildren)'
     */
    public Object[] getSpreadUncall() {
        Object[] result = {ElementMaker, "run", myTagName, myChildren};
        return result;
    }

    /**
     *
     */
    public short getNodeType() {
        return ELEMENT_NODE;
    }

    /**
     * The name of the element. For example, in:
     * <pre>
     * &lt;elementExample id="demo"&gt;
     *         ...
     * &lt;/elementExample&gt; ,</pre>
     * <tt>tagName</tt> has the value <tt>"elementExample"</tt> . Note that
     * this is case-preserving in XML, as are all of the operations of the DOM.
     * The HTML DOM returns the <tt>tagName</tt> of an HTML element in the
     * canonical uppercase form, regardless of the case in the  source HTML
     * document.
     */
    public String getTagName() {
        return myTagName;
    }

    /**
     *
     */
    public ConstList getChildNodes() {
        return myChildren;
    }

    /**
     * Does 'visitor visit&lt;TagName&gt;(children...)', where the first letter
     * of tagName is upperCased. <p>
     * <p/>
     * For example, if the tag name is "foo", then the visitor will be called
     * as 'visitor visitFoo(children...)'
     */
    public Object welcome(Object visitor) {
        return E.call(visitor, verb(), myChildren);
    }

    /**
     * Turns the tagName "foo" into the verb "visitFoo".
     */
    private String verb() {
        return "visit" + Character.toUpperCase(myTagName.charAt(0)) +
          myTagName.substring(1);
    }

    /**
     *
     */
    public void prettyPrintOn(TextWriter out, boolean quasiFlag)
      throws IOException {
        out.print("<", myTagName, ">");
        TextWriter nest = out.indent();
        for (int i = 0; i < myChildren.size(); i++) {
            nest.println();
            ((Node)myChildren.get(i)).prettyPrintOn(nest, quasiFlag);
        }
        out.println();
        out.print("</", myTagName, ">");
    }

    /**
     * Converts to a Astro (eg, Term or AST) whose functor has this tag name
     * and whose args are my converted children in order.
     */
    public Astro build(AstroBuilder builder) {
        AstroArg args = builder.empty();
        int len = myChildren.size();
        for (int i = 0; i < len; i++) {
            Node child = (Node)myChildren.get(i);
            args = builder.seq(args, child.build(builder));
        }
        return builder.namedTerm(myTagName, args);
    }

    /**
     *
     */
    Node[] minimize(Node optLeft) {
        Node[] badChildren = (Node[])myChildren.getArray(Node.class);
        //The number of goodChildren cannot exceed the original number of
        //bad ones.
        Node[] goodChildren = new Node[badChildren.length];
        int firstBad = 0;
        int lastGood = -1;
        while (firstBad < badChildren.length) {
            Node optLastGoodChild = null;
            if (lastGood >= 0) {
                //pop good
                optLastGoodChild = goodChildren[lastGood--];
            }
            //pop bad
            Node firstBadChild = badChildren[firstBad++];
            Node[] mins = firstBadChild.minimize(optLastGoodChild);
            if (null != optLastGoodChild && mins.length == 1) {
                //A special case: reexamine with newly exposed left
                //push bad
                badChildren[--firstBad] = mins[0];
            } else {
                for (int i = 0; i < mins.length; i++) {
                    //push good
                    goodChildren[++lastGood] = mins[i];
                }
            }
        }
        ConstList minChildren = ConstList.fromArray(goodChildren);
        minChildren = minChildren.run(0, lastGood + 1);
        Element minSelf = new Element(myTagName, minChildren);

        if (null == optLeft) {
            Node[] result = {minSelf};
            return result;
        } else if (optLeft instanceof Element) {
            //Elements don't interact on minimization
            Node[] result = {optLeft, minSelf};
            return result;
        } else {
            //Only non-whitespace is significant to the left of an Element
            String data = ((Text)optLeft).getData().trim();
            if (data.length() == 0) {
                Node[] result = {minSelf};
                return result;
            } else {
                Node[] result = {new Text(data), minSelf};
                return result;
            }
        }
    }
}
