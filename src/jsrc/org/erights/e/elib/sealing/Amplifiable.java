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

/**
 * The universal rights amplification protocol.
 * <p/>
 * Implement this to be recognized as implementing the Miranda method
 * {@link org.erights.e.elib.prim.MirandaMethods#__optSealedDispatch
 * __optSealedDispatch/1}.
 *
 * @author Mark S. Miller
 * @see <a href=
 *      "http://www.eros-os.org/pipermail/e-lang/2000-September/003810.html">The
 *      email thread</a>
 */
public interface Amplifiable {

    /**
     * Dispatch on the brand.
     * <p/>
     * If the brand is not one you recognize, return
     * null. Otherwise, return a box sealed by the sealer of this brand
     * containing something you are willing to reveal to someone holding the
     * unsealer of this brand, and which you think they want, given that they
     * used this brand in this request. The brand identity, therefore, also
     * conveys the meaning of the request, very much like message names do.
     * <p/>
     * Use {@link org.erights.e.elib.ref.Ref#__optSealedDispatch
     * Ref.__optSealedDispatch(ref, brand)} rather than
     * <tt>ref.__optSealedDispatch(brand)</tt>.
     */
    SealedBox __optSealedDispatch(Brand brand);
}
