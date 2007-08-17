package org.erights.e.elang.interp;

/*
The contents of this file are subject to the Electric Communities E Open
Source Code License Version 1.0 (the "License"); you may not use this file
except in compliance with the License. You may obtain a copy of the License
at http://www.communities.com/EL/.

Software distributed under the License is distributed on an "AS IS" basis,
WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
the specific language governing rights and limitations under the License.

The Original Code is the Distributed E Language Implementation, released
July 20, 1998.

The Initial Developer of the Original Code is Electric Communities.
Copyright (C) 1998 Electric Communities. All Rights Reserved.

Contributor(s): ______________________________________.
*/

import org.erights.e.develop.format.StringHelper;
import org.erights.e.elib.base.ClassDesc;
import org.erights.e.elib.base.TypeDesc;
import org.erights.e.elib.oldeio.EPrintable;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.oldeio.UnQuote;
import org.erights.e.elib.tables.Equalizer;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.ref.Ref;

import java.io.IOException;
import java.io.StringWriter;


/**
 * Implements the E help command
 *
 * @author Mark S. Miller
 */
public class Help implements EPrintable {

    /**
     *
     */
    public Help() {
    }

    /**
     *
     */
    public UnQuote run(Object subject) throws IOException {
        return run(subject, false, false);
    }

    /**
     *
     */
    public UnQuote run(Object subject, boolean mirandaFlag)
      throws IOException {
        return run(subject, mirandaFlag, false);
    }

    /**
     *
     */
    public UnQuote run(Object subject, boolean mirandaFlag, boolean fullFlag)
      throws IOException {
        StringWriter strWriter = new StringWriter();
        printHelpOn(subject, mirandaFlag, fullFlag, new TextWriter(strWriter));
        StringBuffer buf = strWriter.getBuffer();
        return new UnQuote(StringHelper.canonical(buf.toString()));
    }

    /**
     *
     */
    public void printHelpOn(Object subject,
                            boolean mirandaFlag,
                            boolean fullFlag,
                            TextWriter out) throws IOException {
        subject = Ref.resolution(subject);
        if (null == subject) {
            out.println("a null");
        } else {
            out.println(StringHelper.aan(ClassDesc.sig(
              Equalizer.Simplification(subject.getClass()))));
        }
        if (Ref.isNear(subject)) {
            TypeDesc type = (TypeDesc)E.call(subject, "__getAllegedType");
            type.printHelpOn(mirandaFlag, fullFlag, out);

//            ConstMap mTypeMap = type.getMessageTypes().sortKeys();
//            MessageDesc[] mTypes
//              = (MessageDesc[])mTypeMap.getValues(MessageDesc.class);
//            TextWriter nest = out.indent();
//            for (int i = 0; i < mTypes.length; i++) {
//                MessageDesc mType = mTypes[i];
//                if (fullFlag || mType.getVerb().indexOf('(') == -1) {
//                    mType.printHelpOn(nest);
//                }
//            }
        }
    }

    /**
     * Note that the toString() behavior is not overridden, since this help
     * text would not be helpful to a Java-level programmer.
     */
    public void __printOn(TextWriter out) throws IOException {
        out.print("? help\n" + "    Shows this message.\n" +
          "? help(<expression>)\n" +
          "    Shows what messages the expression's value responds to.\n" +
//          "? meta.getState().bindings()\n" +
//          "    Shows variable bindings in the current scope.\n" +
          "? rune([\"--help\"])\n" +
          "    For help on running external commands\n" + "$ rune --help\n" +
          "    How you get help on \"rune\" from an external shell.\n" +
          "Documentation on the E Language can be found at\n" +
          "    http://www.erights.org/elang/help.html");
    }
}
