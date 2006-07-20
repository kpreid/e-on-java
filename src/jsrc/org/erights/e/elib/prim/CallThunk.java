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

import org.erights.e.elib.base.Thunk;
import org.erights.e.elib.oldeio.EPrintable;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.tables.ConstList;

import java.io.IOException;

/**
 * When run(), it does the described E.callAll()
 *
 * @author Mark S. Miller
 */
class CallThunk implements Thunk, EPrintable {

    private final Object myReceiver;

    private final String myVerb;

    private final Object[] myArgs;

    public CallThunk(Object receiver, String verb, Object[] args) {
        myReceiver = receiver;
        myVerb = verb;
        myArgs = args;
    }

    public Object run() {
        return E.callAll(myReceiver, myVerb, myArgs);
    }


    /**
     *
     */
    public void __printOn(TextWriter out) throws IOException {
        out.print(myReceiver, ".", myVerb);
        ConstList.fromArray(myArgs).printOn("(", ", ", ")", out);
    }

    /**
     *
     */
    public String toString() {
        return E.toString(this);
    }
}
