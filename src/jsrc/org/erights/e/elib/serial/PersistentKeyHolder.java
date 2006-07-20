// Copyright 2003 Hewlett Packard, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.elib.serial;

import org.erights.e.elib.sealing.Brand;
import org.erights.e.elib.sealing.Sealer;
import org.erights.e.elib.sealing.Unsealer;

/**
 * @author Mark S. Miller
 */
public class PersistentKeyHolder {

    static private final Object[] THE_PAIR = Brand.run("Persistent");

    static public final Sealer THE_SEALER = (Sealer)THE_PAIR[0];

    static public final Unsealer THE_UNSEALER = (Unsealer)THE_PAIR[1];

    static public final Brand THE_BRAND = THE_SEALER.getBrand();
}
