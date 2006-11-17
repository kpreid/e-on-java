package org.erights.e.elang.interp;

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

import org.erights.e.develop.exception.ExceptionMgr;
import org.erights.e.elib.serial.BaseLoader;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * An instance of this exists for each of the standard URL protocol, and is
 * bound to corresponding protocol name. For example, "http:" is bound to a
 * URLGetter for the http protocol. By special dispensation, the URLGetter for
 * the "file:" protocol is bound to "fileUrl:", since "file:" is reserved for
 * the FileGetter.
 *
 * @author Mark S. Miller
 * @see org.erights.e.meta.java.io.FileGetter
 */
public class URLGetter extends BaseLoader {

    /**
     * "protocol" as in "file:" or "http:"
     */
    private final String myProtocol;

    /**
     *
     */
    URLGetter(String protocol) {
        myProtocol = protocol;
    }

    /**
     * @param uriBody
     */
    public Object get(String uriBody) {
        try {
            return new URL(myProtocol + ":" + uriBody);
        } catch (MalformedURLException e) {
            throw ExceptionMgr.asSafe(e);
        }
    }

    /**
     * @param obj
     * @return
     */
    public Object[] optUncall(Object obj) {
        if (!(obj instanceof URL)) {
            return null;
        }
        URL url = (URL)obj;
        String ef = url.toExternalForm();
        String prefix = myProtocol + ":";
        if (!ef.startsWith(prefix)) {
            return null;
        }
        String suffix = ef.substring(prefix.length());
        return BaseLoader.ungetToUncall(this, suffix);
    }

    /**
     *
     */
    public String protocol() {
        return myProtocol;
    }

    /**
     *
     */
    public String toString() {
        return "<" + myProtocol + ">";
    }
}
