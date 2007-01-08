// Copyright 2007 Hewlett Packard, under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package test;

import java.io.Serializable;
import java.lang.reflect.Field;

/**
 * Checks if a class is Serializable but fails to implement a
 * serialVersionUID.
 *
 * @author Mark S. Miller
 */
public final class IsBadSerial {

    private IsBadSerial() {
    }

    static public boolean run(Class clazz) {
        if (!Serializable.class.isAssignableFrom(clazz)) {
            return false;
        }
        if (clazz.isInterface()) {
            // Interfaces don't need one.
            return false;
        }
        //noinspection UnusedDeclaration
        Field field;
        try {
            //noinspection UnusedAssignment
            field = clazz.getDeclaredField("serialVersionUID");
        } catch (NoSuchFieldException e) {
            return true;
        }
        // any other validity tests we might want to do on field go here.
        return false;
    }

}
