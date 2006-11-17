package org.erights.e.elib.base;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.oldeio.UnQuote;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.prim.ScriptMaker;
import org.erights.e.elib.serial.Persistent;
import org.erights.e.elib.slot.BaseAuditor;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.ConstMap;
import org.erights.e.elib.tables.FlexMap;
import org.quasiliteral.syntax.BaseLexer;

import java.io.IOException;
import java.io.StringWriter;

/**
 * A type description object, as would be created by an "implements"
 * expression.
 * <p/>
 * A TypeDesc describes the protocol that objects of that type respond to, and
 * is used as a Guard to coerce a provided value into an instance of this
 * type.
 *
 * @author Mark S. Miller
 */
public class TypeDesc extends BaseAuditor implements Persistent {

    private final String myDocComment;

    private final String myFQName;

    private final ConstList mySupers;

    private final ConstList myAuditors;

    private ConstMap myMTypes;
    private transient boolean myIsSorted = false;

    public TypeDesc(String docComment,
                    String optFQName,
                    ConstList supers,
                    ConstList auditors,
                    ConstList mTypes) {
        myDocComment = docComment;
        if (null == optFQName) {
            myFQName = "_";
        } else {
            myFQName = optFQName;
        }
        if (null == supers) {
            mySupers = ConstList.EmptyList;
        } else {
            mySupers = supers;
        }
        myAuditors = auditors;

        FlexMap map = FlexMap.fromTypes(String.class, MessageDesc.class);
        int len = mTypes.size();
        for (int i = 0; i < len; i++) {
            MessageDesc mType = (MessageDesc)mTypes.get(i);
            map.put(mType.getVerb() + "/" + mType.getParams().size(), mType);
        }
        myMTypes = map.snapshot();
    }

    public String getDocComment() {
        return myDocComment;
    }

    /**
     * Gets the fully qualified name
     */
    public String getFQName() {
        return myFQName;
    }

    /**
     *
     */
    public ConstList getSupers() {
        return mySupers;
    }

    /**
     *
     */
    public ConstList getAuditors() {
        return myAuditors;
    }

    /**
     *
     */
    public ConstMap getMessageTypes() {
        if (!myIsSorted) {
            myMTypes = myMTypes.sortKeys();
            myIsSorted = true;
        }
        return myMTypes;
    }

    /**
     * Gets the simple name.
     * <p/>
     * XXX should it be the flat name?
     */
    public void __printOn(TextWriter out) throws IOException {
        out.print(ClassDesc.simpleName(myFQName));
    }

    /**
     *
     */
    public UnQuote help() throws IOException {
        return help(false, false);
    }

    /**
     *
     */
    public UnQuote help(boolean mirandaFlag, boolean fullFlag)
      throws IOException {
        StringWriter strWriter = new StringWriter();
        printHelpOn(mirandaFlag, fullFlag, new TextWriter(strWriter));
        return new UnQuote(strWriter.getBuffer().toString());
    }

    /**
     * Prints in the most expansive form accepted by "interface".
     *
     * @param fullFlag controls whether message names containing an '(' get
     *                 printed
     */
    public void printHelpOn(boolean mirandaFlag,
                            boolean fullFlag,
                            TextWriter out) throws IOException {
        MessageDesc.synopsize(out, myDocComment);
        out.print("interface ", E.toQuote(myFQName));
        int numSupers = mySupers.size();
        if (numSupers >= 1) {
            out.print(" extends ", mySupers.get(0));
            for (int i = 1; i < numSupers; i++) {
                out.print(", ", mySupers.get(i));
            }
        }
        int numAuditors = myAuditors.size();
        if (numAuditors >= 1) {
            out.print(" implements ", myAuditors.get(0));
            for (int i = 1; i < numAuditors; i++) {
                out.print(", ", myAuditors.get(i));
            }
        }
        out.print(" {");
        TextWriter nest = out.indent();
        MessageDesc[] methTypes =
          (MessageDesc[])getMessageTypes().getValues(MessageDesc.class);

        Script mirandas = ScriptMaker.THE_ONE.instanceScript(Object.class);

        for (int i = 0; i < methTypes.length; i++) {
            MessageDesc methType = methTypes[i];
            String verb = methType.getVerb();
            int arity = methType.getParams().size();
            if (mirandaFlag || !mirandas.respondsTo(null, verb, arity)) {
                // XXX Even if !mirandaFlag, an override of a miranda
                // method should arguably still show up.

                if (fullFlag || BaseLexer.isIdentifierOrKeyword(verb)) {
                    nest.lnPrint(methType);
                }
            }
        }
        out.lnPrint("}\n");
    }
}
