package org.erights.e.meta.java.io;

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

import org.erights.e.develop.assertion.T;
import org.erights.e.develop.format.StringHelper;
import org.erights.e.elib.serial.BaseLoader;

import java.io.File;
import java.io.IOException;

/**
 * In E, "file:" is bound to the FileGetter, which uses the file system.
 * <p/>
 * To use the URLGetter for "file:" urls, use "fileUrl:" instead.
 *
 * @author Mark S. Miller
 * @see org.erights.e.elang.interp.URLGetter
 */
public class FileGetter extends BaseLoader {

    static public final FileGetter THE_ONE = new FileGetter();

    /**
     *
     */
    private FileGetter() {
    }

    /**
     * The repeated part
     */
    static private String slash(String path) {
        path = path.replace(File.separatorChar, '/');
        if (!path.endsWith("/")) {
            //temporarily make sure there's a terminal "/"
            path += "/";
        }
        return path;
    }

    /**
     * Turn it into an E-normalized file name. This uses "/" for the separator
     * (Unix style), is absolute, and ends in a "/" if the path names a
     * directory. An initial "~/" is expanded using the System property
     * "user.home", assuming this is the user's home directory.
     */
    static public String normalize(String optPath) {
        if (null == optPath) {
            return null;
        }
        String path = optPath;
        path = path.replace('|', ':');
        path = path.replace('\\', '/');
        path = slash(path);

        if (path.startsWith("~/")) {
            //substitute the user's home directory
            //XXX can't yet do "~joe/...."
            String home = System.getProperty("user.home");
            if (null != home) {
                path = home + path.substring(1);
            }
        }
        path = StringHelper.replaceAll(path, "/./", "/");
        path = slash(new File(path).getAbsolutePath());

        if ((//it may be a windows 3.1 short filename.
          '\\' == File.separatorChar && -1 != path.indexOf('~')) ||
          (//parent pointers are evil
            -1 != path.indexOf("/../"))) {
            try {
                path = slash(new File(path).getCanonicalPath());
            } catch (IOException ioe) {
                //ignored
            }
        }
        int len = path.length();

        if (!new File(path).isDirectory() && '/' == path.charAt(len - 1) &&
          path.indexOf('/') < len - 1) {
            //if it's not a directory, but the path ends in the separator,
            //and that separator isn't the first separator, then remove that
            //last separator. This rule leaves "c:/" alone.

            path = path.substring(0, len - 1);
        }
        if (2 <= path.length() && ':' == path.charAt(1)) {
            char driveLetter = path.charAt(0);
            if ("win32".equals(System.getProperty("e.osdir", "?")) ||
              System.getProperty("os.name", "?").startsWith("Windows")) {

                // On MSWindows, canonicalize the case of the drive letter,
                // which otherwise seems random.
                driveLetter = Character.toLowerCase(driveLetter);
                path = "" + driveLetter + path.substring(1);
            }
        }
        return path;
    }

    /**
     * @param uriBody
     */
    public Object get(String uriBody) {
        return new File(normalize(uriBody));
    }

    /**
     *
     */
    public Object[] optUncall(Object obj) {
        if (obj instanceof File) {
            String path = FileSugar.getAbsolutePath((File)obj);
            return BaseLoader.ungetToUncall(this, path);
        } else if (obj instanceof ReadOnlyFile) {
            ReadOnlyFile roFile = (ReadOnlyFile) obj;
            String path = roFile.getAbsolutePath();
            return new Object[] {
                new File(path),
                roFile.isDeepReadOnly() ? "deepReadOnly" : "shallowReadOnly",
                new Object[] {},
            };
        } else {
            return null;
        }
    }

    /**
     * @return
     */
    public String toString() {
        return "<file>";
    }
}
