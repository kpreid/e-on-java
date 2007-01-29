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
 * Nested version of the Java Error class
 */
public class NestedError extends Error implements NestedThrowable {

    static private final long serialVersionUID = -8224390403639236240L;

    private final Throwable myContainedThrowable;

    /**
     *
     */
    public NestedError(Throwable t, String msg) {
        super(msg);
        myContainedThrowable = t;
    }

    /**
     *
     */
    public Throwable getNestedThrowable() {
        return myContainedThrowable;
    }
}
