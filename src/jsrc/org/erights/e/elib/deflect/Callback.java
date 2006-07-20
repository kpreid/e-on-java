// Copyright 2004 Hewlett Packard, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.elib.deflect;

/**
 * An empty interface which exists for the purpose of deflecting E objects
 * handed out of the vat, so that they will remember their vat of origin when
 * invoked.
 *
 * @author Mark S. Miller
 * @see <a href=
 * "https://bugs.sieve.net/bugs/?func=detailbug&bug_id=125634&group_id=16380"
 * >JComboBox prints in the wrong thread</a>
 */
public interface Callback {

}
