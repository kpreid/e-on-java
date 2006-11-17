package net.vattp.vls;

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

import net.vattp.data.VatLocationLookup;
import org.erights.e.elib.base.ValueThunk;
import org.erights.e.elib.tables.FlexMap;
import org.erights.e.extern.timer.Timer;

/**
 * A vat location server, which maintains a collection of the locations of E
 * vats which it can lookup on demand. <p>
 * <p/>
 * These registrations have a finite lifetime which we also manage here. In
 * order to better decouple issues during this stage of development, this
 * version is no longer persistent. <p>
 * <p/>
 * XXX The VLS must be registered under swiss number 0 so it can respond, as an
 * E object, to registrations (and unregistrations?). <p>
 * <p/>
 * XXX The VLS must be registered by setVatLocationLookup to listen on port 0
 * for location queries.
 *
 * @author Eris Messick
 * @author Bill Frantz
 * @author Chip Morningstar
 * @author Mark S. Miller
 */
class VatLocationServer implements VatLocationLookup {

    /**
     * Collection of current registrations
     */
    private final FlexMap myRegistrations;

    /**
     * Can be transient or persistent, depending on policy
     */
    private final Timer myTimer;

    /* Reregistration must occur before the registration times out. It's
       currently set to 60 minutes. */
    // 60000 ms. == 1 min
    static private final int MINUTE = 60000;

    // 60 ticks == 1 hour
    static private final int REG_DURATION = 60 * MINUTE;
    // commit every 5 minutes
    // static private final int COMMIT_INTERVAL = 5 * MINUTE;

    /**
     * Construct a vat location server. <p>
     * <p/>
     * Since, in this version, we are only starting from scratch, it makes a
     * new empty registration collection.
     */
    public VatLocationServer(Timer timer) {
        myRegistrations = FlexMap.fromTypes(String.class, String.class);
        myTimer = timer;
    }

    static private final String[] NO_LOCATIONS = {};

    /**
     * XXX Will currently only return either a zero or a one element array.
     */
    public String[] getLocations(String vatID) {
        String optLocation =
          (String)myRegistrations.fetch(vatID, ValueThunk.NULL_THUNK);
        if (null == optLocation) {
            return NO_LOCATIONS;
        } else {
            String[] result = {optLocation};
            return result;
        }
    }

    /**
     * Add a new vat to the registry.
     *
     * @param vatID      Vat ID of vat being added
     * @param searchPath Were in the world to find it
     */
    public void put(String vatID, String searchPath) {
        myRegistrations.put(vatID, searchPath);
        Object[] args = {vatID};
        myTimer.whenPast(myTimer.now() + REG_DURATION,
                         myRegistrations,
                         "remove",
                         args);
    }
}
