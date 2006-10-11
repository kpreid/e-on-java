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
 * comment. <p>
 */

import org.erights.e.develop.format.StringHelper;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.prim.StaticMaker;
import org.erights.e.elib.tables.ConstList;
import org.quasiliteral.astro.Astro;
import org.quasiliteral.astro.AstroBuilder;

import java.io.IOException;

/**
 * This represents text that appears between a start-tag and end-tag,
 * after all escapes and whitespace have been processed. <p>
 * <p/>
 * This is a substantial revision by ERights.org of the file as
 * released by the w3c, as is allowed by the license. See the package
 * comment. For this dom, the only kind of character-data is text, so we
 * folded together the original superclass CharacterData into its
 * subclass Text. <p>
 * <p/>
 * Original comment from CharacterData: <p>
 * <p/>
 * <blockquote>
 * The <tt>CharacterData</tt> interface extends Node with a set of
 * attributes and methods for accessing character data in the DOM. For
 * clarity this set is defined here rather than on each object that uses
 * these attributes and methods. No DOM objects correspond directly to
 * <tt>CharacterData</tt> , though <tt>Text</tt> and others do
 * inherit the interface from it. All <tt>offsets</tt> in this interface
 * start from 0.
 * <p> As explained in the <tt>DOMString</tt> interface, text strings in
 * the DOM are represented in UTF-16, i.e. as a sequence of 16-bit units. In
 * the following, the term  16-bit units is used whenever necessary to
 * indicate that indexing on CharacterData is done in 16-bit units. <p>
 * </blockquote>
 * <p/>
 * Original comment of Text: <p>
 * <p/>
 * <blockquote>
 * The <tt>Text</tt> interface inherits from <tt>CharacterData</tt>
 * and represents the textual content (termed  character  data in XML) of an
 * <tt>Element</tt> or <tt>Attr</tt> . If there is no markup inside
 * an element's content, the text is contained in a single object
 * implementing the <tt>Text</tt> interface that is the only child of the
 * element. If there is markup, it is parsed into the  information items
 * (elements,  comments, etc.) and <tt>Text</tt>  nodes that form the
 * list of children of the element.
 * <p> When a document is first made available via the DOM, there is  only one
 * <tt>Text</tt> node for each block of text. Users may create  adjacent
 * <tt>Text</tt> nodes that represent the  contents of a given element
 * without any intervening markup, but should be aware that there is no way
 * to represent the separations between these nodes in XML or HTML, so they
 * will not (in general) persist between DOM editing sessions. The
 * <tt>normalize()</tt> method on <tt>Element</tt> merges any such
 * adjacent <tt>Text</tt> objects into a single node for each block of
 * text.
 * </blockquote>
 *
 * @deprecated Use Term trees instead.
 */
public class Text extends Node {

    static private final long serialVersionUID = -7993276082779589979L;

    /**
     *
     */
    static public final StaticMaker TextMaker
      = StaticMaker.make(Text.class);

    /**
     *
     */
    static private final Node[] NO_NODES = {};

    /**
     * @serial The characters themselves
     */
    private final String myData;

    /**
     *
     */
    public Text(String data) {
        myData = data;
    }

    /**
     * Uses 'TextMaker(myData)'
     */
    public Object[] getSpreadUncall() {
        Object[] result = {TextMaker, "run", myData};
        return result;
    }

    /**
     *
     */
    public short getNodeType() {
        return TEXT_NODE;
    }

    /**
     * An empty list, since Text has no children
     */
    public ConstList getChildNodes() {
        return ConstList.EmptyList;
    }

    /**
     * The character data of the node that implements this interface. The DOM
     * implementation may not put arbitrary limits on the amount of data that
     * may be stored in a  <tt>CharacterData</tt> node. However,
     * implementation limits may  mean that the entirety of a node's data may
     * not fit into a single <tt>DOMString</tt> . In such cases, the user
     * may call <tt>substringData</tt> to retrieve the data in
     * appropriately sized pieces.
     */
    public String getData() {
        return myData;
    }

    /**
     * Does 'visitor viewText(myData)'. <p>
     * <p/>
     * Called "view" rather than "visit" to keep it distinct from
     * Element-tag-based visitation.
     */
    public Object welcome(Object visitor) {
        return E.call(visitor, "viewText", myData);
    }

    /**
     *
     */
    public void prettyPrintOn(TextWriter out, boolean quasiFlag)
      throws IOException {
        //XXX Have to escape things again
        String label = myData;
        label = StringHelper.replaceAll(label, "<", "&lt;");
        label = StringHelper.replaceAll(label, ">", "&gt;");
        label = StringHelper.replaceAll(label, "&", "&amp;");
        //XXX more entities
        if (quasiFlag) {
            label = StringHelper.replaceAll(label, "$", "$$");
            label = StringHelper.replaceAll(label, "@", "@@");
            label = StringHelper.replaceAll(label, "`", "``");
        }
        out.print(label);
    }

    /**
     * Returns an Astro (eg, AstroToken or Term) consisting of this literal
     * text (as a leaf) and zero arguments.
     */
    public Astro build(AstroBuilder builder) {
        return builder.term(builder.leafString(myData, null),
                            builder.empty());
    }

    /**
     *
     */
    Node[] minimize(Node optLeft) {
        if (null == optLeft) {
            if (myData.length() == 0) {
                return NO_NODES;
            } else {
                // A single non-empty text node with no siblings is already
                //minimal
                Node[] result = {this};
                return result;
            }
        } else if (optLeft instanceof Element) {
            //Whitespace is not significant after an Element
            String data = myData.trim();
            if (data.length() == 0) {
                Node[] result = {optLeft};
                return result;
            } else {
                Node[] result = {optLeft, new Text(data)};
                return result;
            }
        } else {
            //this coalesces them, but is can't know whether it should trim
            //the resulting string.
            Text left = (Text)optLeft;
            Node[] result = {new Text(left.myData + myData)};
            return result;
        }
    }
}
