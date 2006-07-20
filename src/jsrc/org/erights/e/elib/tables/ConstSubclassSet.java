package org.erights.e.elib.tables;

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
 * Represents a set of classes, such that if X is a member of the set, all
 * subclasses of X are in the set.
 * <p/>
 * XXX Does not yet work according to any collection protocol convention, and
 * is not yet pass by copy. When made pass-by-copy, only a copy of the
 * initialMap should be passed, so it can fill in lazily on the other side as
 * well. XXX currently assumes that fully-qualified class names are one-to-one
 * with classes, an assumption broken by ClassLoaders.
 * <p/>
 * Instances of this class are typically used as static state. Although
 * semantically this isn't mutable state, and so is ok, the implementation must
 * be thread safe since an instance will be concurrently accessed from multiple
 * threads.
 *
 * @author Mark S. Miller
 */
public class ConstSubclassSet {

    /**
     * Should make this transient, but only after we arrange to restore it
     */
    private final Object myLock = new Object();

    private final FlexSet myInnerSet;

    /**
     *
     */
    private ConstSubclassSet(FlexSet initialSet) {
        myInnerSet = initialSet;
    }

    /**
     *
     */
    static public ConstSubclassSet make(String[] fqNames) {
        FlexSet set = FlexSet.fromType(String.class);
        for (int i = 0; i < fqNames.length; i++) {
            set.addElement(fqNames[i]);
        }
        return new ConstSubclassSet(set);
    }

    /**
     *
     */
    public boolean has(Class clazz) {
        if (clazz == null) {
            return false;
        }
        String fqName = clazz.getName();
        synchronized (myLock) {
            if (myInnerSet.contains(fqName)) {
                return true;
            }
            if (myInnerSet.contains("java.lang.Object")) {
                return true;
            }

            boolean result = false;
            if (has(clazz.getSuperclass())) {
                result = true;
            } else {
                Class[] faces = clazz.getInterfaces();
                for (int i = 0; i < faces.length; i++) {
                    if (has(faces[i])) {
                        result = true;
                        break;
                    }
                }
            }
            if (result) {
                myInnerSet.addElement(fqName);
            }
            return result;
        }
    }
}
