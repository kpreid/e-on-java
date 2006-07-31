package org.erights.e.elib.base;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.elib.oldeio.EPrintable;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.prim.StaticMaker;
import org.erights.e.elib.serial.Persistent;
import org.erights.e.elib.slot.Guard;
import org.erights.e.elib.tables.ConstList;
import org.quasiliteral.syntax.BaseLexer;

import java.io.IOException;

/**
 * Makes a description of a message signature within a type description.
 */
public class MessageDesc implements Persistent, EPrintable {

    /**
     * @noinspection StaticNonFinalField
     */
    static private StaticMaker OptMessageDescMaker = null;

    /**
     * @noinspection NonThreadSafeLazyInitialization
     */
    static public StaticMaker GetMessageDescMaker() {
        if (null == OptMessageDescMaker) {
            OptMessageDescMaker = StaticMaker.make(MessageDesc.class);
        }
        return OptMessageDescMaker;
    }

    private final String myDocComment;

    private final String myVerb;

    private final ConstList myParams;

    private Object myOptResultGuard;

    /**
     * Without initial newLine, but if anything's printed, with a final one.
     */
    static public void synopsize(TextWriter out, String docComment)
      throws IOException {
        int len = docComment.length();
        if (len >= 1) {
            boolean endsWithNL = docComment.endsWith("\n");
            if (endsWithNL) {
                docComment = docComment.substring(0, len - 1);
            }
            out.print("/**");
            out.indent(" *").print(docComment);
            if (endsWithNL) {
                out.lnPrint(" */");
            } else {
                out.print("*/");
            }
            out.println();
        }
    }

    /**
     * If the verb is a {@link org.erights.e.elang.syntax.ELexer#isIdentifier
     * valid E identifier}, then print it, else print it as a quoted string.
     * <p/>
     * XXX In order to preserve the current implementation's layering, this
     * actually checks only that the verb is a {@link BaseLexer#isIdentifierOrKeyword
     * valid E identifier or keyword}, since the list of E keywords doesn't
     * appear till the E-lang layer.
     *
     * @noinspection UnnecessaryFullyQualifiedName
     */
    static public void printVerbOn(String verb, TextWriter out)
      throws IOException {
        if (BaseLexer.isIdentifierOrKeyword(verb)) {
            out.print(verb);
        } else {
            out.quote(verb);
        }
    }

    /**
     * @param optResultGuard In order to avoid a circular dependency, this
     *                       variable can hold a Class instead of a Guard, in
     *                       which case it will be converted to a Guard as
     *                       needed.
     */
    public MessageDesc(String docComment,
                       String verb,
                       ConstList params,
                       Object optResultGuard) {
        myDocComment = docComment;
        myParams = params;
        myOptResultGuard = optResultGuard;

        int i = verb.lastIndexOf('/');
        if (-1 != i) {
            verb = verb.substring(0, i);
        }
        myVerb = verb;
    }

    /**
     *
     */
    public String getDocComment() {
        return myDocComment;
    }

    /**
     *
     */
    public String getVerb() {
        return myVerb;
    }

    /**
     *
     */
    public ConstList getParams() {
        return myParams;
    }

    /**
     *
     */
    public Guard getOptResultGuard() {
        if (null == myOptResultGuard) {
            return null;
        } else if (myOptResultGuard instanceof Guard) {
            return (Guard)myOptResultGuard;
        } else if (myOptResultGuard instanceof Class) {
            myOptResultGuard = ClassDesc.byJavaRules((Class)myOptResultGuard);
            //noinspection CastConflictsWithInstanceof
            return (Guard)myOptResultGuard;
        } else {
            myOptResultGuard = E.as(myOptResultGuard, Guard.class);
            return (Guard)myOptResultGuard;
        }
    }

//    /**
//     *
//     */
//    public void printHelpOn(TextWriter out) throws IOException {
//        out.println();
//        printDeclOn(out);
//        out.indent().lnPrint(myDocComment);
//    }

    /**
     *
     */
    private void printDeclOn(TextWriter out) throws IOException {
        out.print("to ");
        printVerbOn(myVerb, out);
        out.print("(");
        int len = myParams.size();
        if (len >= 1) {
            out.print(myParams.get(0));
            for (int i = 1; i < len; i++) {
                out.print(", ", myParams.get(i));
            }
        }
        out.print(")");
        Guard optGuard = getOptResultGuard();
        if (null != optGuard) {
            out.print(" :", optGuard);
        }
    }

    /**
     *
     */
    public void __printOn(TextWriter out) throws IOException {
        out.println();
        synopsize(out, myDocComment);
        printDeclOn(out);
    }
}
