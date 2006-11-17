package org.erights.e.elib.prim;

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

import org.erights.e.elib.oldeio.EPrintable;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.ref.Resolver;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.vat.SendingContext;

import java.io.IOException;

/**
 * A message consists of the verb, the args, and the optional Resolver for
 * reporting the outcome of delivering the message.
 * <p/>
 * In other words, it contains all the non-debugging parts of a PendingDelivery
 * except for the object to which the message should be delivered.
 *
 * @author Mark S. Miller
 */
public class Message implements EPrintable {

    private final Resolver myOptResolver;

    private final String myVerb;

    private final Object[] myArgs;

    private final SendingContext mySendingContext;

    /**
     * Remembers the current vat
     */
    public Message(Resolver optResolver, String verb, Object[] args) {
        super();
        myOptResolver = optResolver;
        myVerb = verb;
        myArgs = args;
        mySendingContext = new SendingContext("SCmsg");
    }

    /**
     *
     */
    public Resolver getOptResolver() {
        return myOptResolver;
    }

    /**
     * An intern()ed string.
     */
    public String getVerb() {
        return myVerb;
    }

    /**
     * Security Alert: caller is trusted not to modify this array!
     */
    public Object[] getArgs() {
        return myArgs;
    }

    /**
     * @return
     */
    public SendingContext getSendingContext() {
        return mySendingContext;
    }

    /**
     *
     */
    public void __printOn(TextWriter out) throws IOException {
        out.println();
        out.print("_ <- ", myVerb);
        ConstList.fromArray(myArgs).printOn("(", ", ", ")", out);
        mySendingContext.printContextOn(out.indent("--- "));
        out.println();
    }

    /**
     * @return
     */
    public String toString() {
        return "_ <- " + myVerb + ConstList.fromArray(myArgs);
    }
}
