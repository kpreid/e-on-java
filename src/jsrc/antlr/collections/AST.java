package antlr.collections;

/* ANTLR Translator Generator
 * Project led by Terence Parr at http://www.jGuru.com
 * Software rights: http://www.antlr.org/license.html
 *
 * $Id: //depot/code/org.antlr/release/antlr-2.7.5/antlr/collections/AST.java#1 $
 */

import antlr.Token;

/**
 * Minimal AST node interface used by ANTLR AST generation and tree-walker.
 */
public interface AST {

    /**
     * Add a (rightmost) child to this node
     */
    void addChild(AST c);

    boolean equals(AST t);

    boolean equalsList(AST t);

    boolean equalsListPartial(AST t);

    boolean equalsTree(AST t);

    boolean equalsTreePartial(AST t);

    ASTEnumeration findAll(AST tree);

    ASTEnumeration findAllPartial(AST subtree);

    /**
     * Get the first child of this node; null if no children
     */
    AST getFirstChild();

    /**
     * Get	the next sibling in line after this one
     */
    AST getNextSibling();

    /**
     * Get the token text for this node
     */
    String getText();

    /**
     * Get the token type for this node
     */
    int getType();

    /**
     * @since 2.7.3 Need for error handling
     */
    int getLine();

    /**
     * @since 2.7.3 Need for error handling
     */
    int getColumn();

    /**
     * Get number of children of this node; if leaf, returns 0
     */
    int getNumberOfChildren();

    void initialize(int t, String txt);

    void initialize(AST t);

    void initialize(Token t);

    /**
     * Set the first child of a node.
     */
    void setFirstChild(AST c);

    /**
     * Set the next sibling after this one.
     */
    void setNextSibling(AST n);

    /**
     * Set the token text for this node
     */
    void setText(String text);

    /**
     * Set the token type for this node
     */
    void setType(int ttype);

    String toString();

    String toStringList();

    String toStringTree();
}
