package net.captp.jcomm;

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

import org.erights.e.elib.prim.StaticMaker;
import org.erights.e.elib.serial.DeepPassByCopy;
import org.erights.e.elib.serial.Persistent;
import org.erights.e.meta.java.math.BigIntegerSugar;

import java.math.BigInteger;

/**
 * A pairing of a VatID and a SwissHash, uniquely identifying a Selfish
 * object without providing access to the object. <p>
 *
 * @author Chip Morningstar
 * @author Mark S. Miller
 */
public class ObjectID implements Persistent, DeepPassByCopy {

    static private final long serialVersionUID = 2221338666124259513L;

    /**
     *
     */
    static public final StaticMaker ObjectIDMaker =
      StaticMaker.make(ObjectID.class);

    /**
     * The vat this ID is relative to (i.e., the vat that created this ID).
     */
    private final String myVatID;

    /**
     * The object's identity, relative to that vat
     */
    private final BigInteger mySwissHash;

    /**
     * Construct an object identifier given the SwissHash directly
     */
    public ObjectID(String vatID, BigInteger swissHash) {
        mySwissHash = swissHash;
        myVatID = vatID;
    }

    /**
     *
     */
    public Object[] getSpreadUncall() {
        Object[] result = {ObjectIDMaker, "new", myVatID, mySwissHash};
        return result;
    }

    /**
     * Two ObjectIDs are equal if the vatIDs and SwissHashes are both equal
     */
    public boolean equals(Object obj) {
        if (obj instanceof ObjectID) {
            ObjectID test = (ObjectID)obj;
            return (myVatID.equals(test.myVatID) &&
              mySwissHash.equals(test.mySwissHash));
        } else {
            return false;
        }
    }

    /**
     *
     */
    public int hashCode() {
        return myVatID.hashCode() ^ mySwissHash.hashCode();
    }

    /**
     * Return the vat ID
     */
    public String getVatID() {
        return myVatID;
    }

    /**
     * Return the Swiss number
     */
    public BigInteger getSwissHash() {
        return mySwissHash;
    }

    /**
     *
     */
    public String toString() {
        return (myVatID.substring(0, 4) + "/##" +
          BigIntegerSugar.toYURL32(mySwissHash).substring(0, 4));
    }
}
