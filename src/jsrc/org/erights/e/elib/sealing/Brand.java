package org.erights.e.elib.sealing;

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

import org.erights.e.elib.serial.PassByProxy;
import org.erights.e.elib.serial.Persistent;

/**
 * Used to identify Sealer/Unsealer pair uniquely.
 *
 * @author Mark S. Miller
 */
public final class Brand implements PassByProxy, Persistent {

    /**
     * @serial The name is only for debugging purposes, but must be stable over
     * the life of the Brand. Only the Brand's unique object identity is
     * significant.
     */
    private final String myNickName;

    /**
     *
     */
    private Brand(String nickName) {
        myNickName = nickName;
    }

    /**
     * @deprecated Use {@link #run(String)} instead.
     */
    static public Object[] pair(String nickName) {
        return run(nickName);
    }

    /**
     * Returns a Sealer/Unsealer pair identified with a new unique Brand of the
     * specified (non-unique) name.
     */
    static public Object[] run(String nickName) {
        Brand brand = new Brand(nickName);
        Sealer sealer = new Sealer(brand);
        Unsealer unsealer = new Unsealer(brand);
        Object[] result = {sealer, unsealer};
        return result;
    }

    /**
     *
     */
    public String toString() {
        return myNickName;
    }

    /**
     * A non-unique name, used by the creator of the brand to label it, usually
     * for debugging purposes.
     */
    public String getNickName() {
        return myNickName;
    }
}
