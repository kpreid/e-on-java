package org.erights.e.elib.util;

import org.erights.e.elib.prim.MirandaMethods;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

/**
 * A DeadManSwitch is defined informally as anything that overrides {@link
 * MirandaMethods#__reactToLostClient __reactToLostClient/1} in order to
 * actually react to this notification.
 * <p/>
 * This interface is provided as a convenience for DeadManSwitches which are
 * written in Java, so that they don't themselves have to be a public class in
 * order for this method to be E-invocable.
 *
 * @author Mark S. Miller
 */
public interface DeadManSwitch {

    /**
     * Notification that some client of this object may no longer be able to
     * talk to it, presumably because of a Partition whose case is described by
     * 'problem'.
     *
     * @see MirandaMethods#__reactToLostClient
     */
    void __reactToLostClient(Object problem);
}
