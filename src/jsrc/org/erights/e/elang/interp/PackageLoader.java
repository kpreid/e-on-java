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

import org.erights.e.develop.assertion.T;
import org.erights.e.elib.serial.BaseLoader;
import org.erights.e.elib.serial.Loader;

/**
 * The subtree of a Loader under a package-prefix.
 * <p>
 * As explained in the superclass comment, this must be thread-safe.
 *
 * @author Mark S. Miller
 */
class PackageLoader extends BaseLoader {

    /**
     * Inductively thread-safe.
     */
    private final Loader mySubstrate;

    private final String myProtocol;

    private final String myPrefix;

    /**
     *
     */
    PackageLoader(Loader substrate,
                  String protocol,
                  String fqName) {
        mySubstrate = substrate;
        T.require(protocol.endsWith(":"),
                  "internal: protocol must end with colon:", protocol);
        myProtocol = protocol;
        T.require(fqName.endsWith(".*"),
                  "internal: bad package name: ", fqName);
        //chop off just the "*" (leave the terminal ".")
        myPrefix = fqName.substring(0, fqName.length() - "*".length());
    }

    /**
     *
     */
    public Object get(String name) {
        return mySubstrate.get(myPrefix + name);
    }

    /**
     * See {@link org.erights.e.elib.serial.BaseLoader#getOptWrappingUncall}.
     */
    public Object[] optUncall(Object obj) {
        return BaseLoader.getOptWrappingUncall(this,
                                               mySubstrate,
                                               myPrefix,
                                               obj);
    }

    /**
     *
     */
    public String toString() {
        return "<" + myProtocol + myPrefix + "*>";
    }
}
