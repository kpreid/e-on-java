package org.erights.e.elib.prim.tests;

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

import org.erights.e.develop.exception.PrintStreamWriter;
import org.erights.e.develop.trace.TraceController;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.ref.Ref;
import org.erights.e.elib.ref.Resolver;
import org.erights.e.elib.util.OneArgFunc;
import org.erights.e.meta.java.lang.CharacterMakerSugar;

import java.io.IOException;
import java.util.Properties;

/**
 * Demonstrates (and trivially tests) ELib's event-loop concurrency style and
 * its Ref-based optimism. Running "java org.erights.e.elib.prim.tests.ERunDemo"
 * should result in <p>
 * <pre>
 * &lt;Promise&gt;, &lt;Unresolved Resolver&gt;, &lt;Promise&gt;
 * near: 4
 * hello world, &lt;Resolved Resolver&gt;, 4
 * </pre>
 * Which does happen under the Cafe debugger. Strangely, when run from bash we
 * get instead:
 * <pre>
 * &lt;Promise&gt;, &lt;Unresolved Resolver&gt;, &lt;Promise&gt;
 * hello world, &lt;Resolved Resolver&gt;, &lt;Promise&gt;
 * </pre>
 * So we've got a concurrency bug that doesn't occur under the debugger. Oh
 * joy!  Note: Early indications are that this bug's symptoms have disappeared
 * with the move to Java 2. This isn't necessarily good news!
 *
 * @author Mark S. Miller
 */
public class ERunDemo {

    private ERunDemo() {
    }

    static public void main(String[] args) throws IOException {

        TraceController.start(new Properties(System.getProperties()));

        //XXX this should use platform newlines rather the "\n"
        TextWriter stdout = new TextWriter(PrintStreamWriter.stdout(), true);

        Object[] promise = Ref.promise();
        Ref s = (Ref)promise[0];
        Resolver sR = (Resolver)promise[1];

        Ref i = E.send(s, "indexOf(int)", CharacterMakerSugar.valueOf('o'));

        Ref.whenResolvedOnly(i, new OneArgFunc() {

            public Object run(Object resolution) {
                PrintStreamWriter.stdout().println("near: " + resolution);
                return null; //to keep the compiler happy
            }
        });

        stdout.print(s, ", ", sR, ", ", i, "\n");

        sR.resolve("hello world");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            stdout.println("interrupted");
        }

        stdout.print(s, ", ", sR, ", ", i, "\n");
        System.exit(0);
    }
}
