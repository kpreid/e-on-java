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

import org.erights.e.develop.assertion.T;
import org.erights.e.elib.oldeio.EPrintable;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.serial.DeepPassByCopy;
import org.erights.e.elib.serial.Persistent;
import org.erights.e.elib.tables.AssocFunc;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.EIteratable;
import org.quasiliteral.astro.Astro;
import org.quasiliteral.astro.AstroBuilder;

import java.io.IOException;

/**
 * The document tree is a tree of <tt>Node</tt>s -- the supertype
 * of the individual types of nodes. <p>
 * <p/>
 * This is a substantial revision by ERights.org of the file as
 * released by the w3c, as is allowed by the license. See the package
 * comment. The original comment: <p>
 * <p/>
 * <blockquote>
 * The <tt>Node</tt> interface is the primary datatype for the entire
 * Document Object Model. It represents a single node in the document tree.
 * While all objects implementing the <tt>Node</tt> interface expose
 * methods for dealing with children, not all objects implementing the
 * <tt>Node</tt> interface may have children. For example,
 * <tt>Text</tt> nodes may not have children, and adding children to such
 * nodes results in a <tt>DOMException</tt> being raised.
 * <p> The attributes <tt>nodeName</tt> , <tt>nodeValue</tt> and
 * <tt>attributes</tt> are included as a mechanism to get at node
 * information without casting down to the specific derived interface. In
 * cases where there is no obvious mapping of these attributes for a specific
 * <tt>nodeType</tt> (e.g., <tt>nodeValue</tt> for an
 * <tt>Element</tt> or <tt>attributes</tt> for a <tt>Comment</tt>
 * ), this returns <tt>null</tt> . Note that the specialized interfaces
 * may contain additional and more convenient mechanisms to get and set the
 * relevant information.
 * </blockquote>
 *
 * @deprecated Use Term trees instead.
 */
public abstract class Node
  implements Persistent, DeepPassByCopy, EIteratable, EPrintable {

    static final long serialVersionUID = 1930678083045024505L;

    // NodeType
    static public final short ELEMENT_NODE = 1;

    //static public final short ATTRIBUTE_NODE            = 2;
    static public final short TEXT_NODE = 3;
    //static public final short CDATA_SECTION_NODE        = 4;
    //static public final short ENTITY_REFERENCE_NODE     = 5;
    //static public final short ENTITY_NODE               = 6;
    //static public final short PROCESSING_INSTRUCTION_NODE = 7;
    //static public final short COMMENT_NODE              = 8;
    //static public final short DOCUMENT_NODE             = 9;
    //static public final short DOCUMENT_TYPE_NODE        = 10;
    //static public final short DOCUMENT_FRAGMENT_NODE    = 11;
    //static public final short NOTATION_NODE             = 12;

    /**
     * All the subclasses are in this package
     */
    Node() {
    }

    /**
     * A code representing the type of the underlying object, as defined
     * above.
     */
    public abstract short getNodeType();

    /**
     * Modified to return an ConstList of Nodes rather than a
     * NodeList. The original documantation: <p>
     * <p/>
     * A <tt>NodeList</tt> that contains all children of this node. If
     * there are no children, this is a <tt>NodeList</tt> containing no
     * nodes. The content of the returned <tt>NodeList</tt> is "live" in
     * the sense that, for instance, changes to the children of the node
     * object that it was created from are immediately reflected in the nodes
     * returned by the <tt>NodeList</tt> accessors; it is not a static
     * snapshot of the content of the node. This is true for every
     * <tt>NodeList</tt> , including the ones returned by the
     * <tt>getElementsByTagName</tt> method.
     */
    public abstract ConstList getChildNodes();

    /**
     * A node iterates its children
     */
    public void iterate(AssocFunc body) {
        getChildNodes().iterate(body);
    }

    /**
     * The classic visitor pattern from the patterns literature, except that
     * we don't dispatch on the type of the node, but, for Elements, on a
     * verb generated from the tag name. <p>
     * <p/>
     * In order to call the visitor with generated names, we call the visitor
     * with E.call().
     */
    public abstract Object welcome(Object visitor);

    /**
     * Tests whether the DOM implementation implements a specific feature and
     * that feature is supported by this node.
     *
     * @param feature The name of the feature to test. This is the same name
     *                which can be passed to the method <tt>hasFeature</tt> on
     *                <tt>DOMImplementation</tt> .
     * @param version This is the version number of the feature to test. In
     *                Level 2, version 1, this is the string "2.0". If the version is not
     *                specified, supporting any version of the feature will cause the
     *                method to return <tt>true</tt> .
     * @return Returns <tt>true</tt> if the specified feature is supported
     *         on this node, <tt>false</tt> otherwise.
     * @since DOM Level 2
     */
    public boolean supports(String feature, String version) {
        return false;
    }

    /**
     * Used to convert from a Node tree to an Astro (eg, AST or Term) tree.
     */
    public abstract Astro build(AstroBuilder builder);

    /**
     * Asks this Node to return a minimal form of the optional node to the
     * left of itself and itself.
     * <p/>
     * If the node to the left is provided (ie, if 'optLeft' isn't null), then
     * this node may safely assume that this provided sibling has already been
     * minimized.
     * <p/>
     * The minimization is much like XML canonicalization, but depends on an
     * assumption specific to Minimal-XML: That for an Element that contains
     * sub-Elements, all pure-whitespace Texts that it also contains may be
     * gotten rid of.
     * <p/>
     * The number of nodes returned cannot exceed the number examined. In
     * other words, if 'optLeft' is null, only zero or one node may be
     * returned. If 'optLeft' isn't null, the number returned may only be
     * zero, one, or two.
     *
     * @return A list of nodes to replace 'optLeft' and itself.
     */
    abstract Node[] minimize(Node optLeft);

    /**
     *
     */
    public Node minimize() {
        Node[] result = minimize(null);
        if (result.length == 0) {
            //This can only happen if the original was an empty Text,
            //so return an empty Text, since we gotta return something
            return new Text("");
        } else {
            T.require(result.length == 1,
                      "Internal: minimization shouldn't expand: ", this);
            return result[0];
        }
    }

    /**
     * Prints as "sml`...`", where pretty printed XML appears between
     * the backquotes
     */
    public void __printOn(TextWriter out) throws IOException {
        out.print("sml`");
        prettyPrintOn(out.indent(), true);
        out.print("`");
    }

    /**
     * Prints pretty printed XML
     */
    public abstract void prettyPrintOn(TextWriter out, boolean quasiFlag)
      throws IOException;

    /**
     * @return
     */
    public String toString() {
        return E.toString(this);
    }
}
