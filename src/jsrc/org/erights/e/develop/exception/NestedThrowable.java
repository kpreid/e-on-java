package org.erights.e.develop.exception;

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
 * Wraps a Throwable in order to turn it into some other kind of Throwable, in
 * order to add backtrace info, or both.
 * <p/>
 * For a NestedThrowable, the convention for the message is that it should be
 * empty, or each line should begin with <ul> <li>"# " to indicate a message
 * intended only for human parsing. <li>". " to indicate a printing of a
 * problematic call's value <li>"@ " to indicate where the problem occurred
 * <li>"- " to indicate what was called. </ul> If the message is empty, it will
 * be ignored by {@link ThrowableSugar#eStack}.
 *
 * @author modification by Mark S. Miller
 */
public interface NestedThrowable {

    /**
     *
     */
    Throwable getNestedThrowable();
}
