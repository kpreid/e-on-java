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
import org.erights.e.develop.exception.ExceptionMgr;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.prim.Thrower;
import org.erights.e.elib.serial.BaseLoader;
import org.erights.e.elib.tables.AssocFunc;
import org.erights.e.elib.tables.Twine;
import org.erights.e.elib.util.OneArgFunc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;

/**
 * A sweetener defining extra messages that may be e-sent to a File.
 * <p/>
 * Since File isn't final, and none of its public instance methods are final,
 * it would seem we should subclass it instead of sugarring it. However, our
 * handling of URL and File are closely parallel and should stay so.
 * Unfortunately, URL is declared final, and so must be dealt with by
 * sugarring. Therefore, we continue to do so for File as well.
 * <p/>
 * A File is made to act like it implements {@link org.erights.e.elib.serial.Loader},
 * which we would do if we were defining a subclass. It "implements" Loader to
 * provide a loader specific to the directory tree rooted in itself.
 *
 * @author Mark S. Miller
 */
public class FileSugar {

    /**
     * platform newlines
     */
    static private final String NEWLINE = System.getProperty("line.separator");

    /**
     * prevent instantiation
     */
    private FileSugar() {
    }

    /**
     * Returns a one-level readOnly view of this file or directory.
     * <p/>
     * If this is a directory, then the result gives read-only access to the
     * directory itself, but undiminished access to the contents of the
     * directory. If this is a non-directory, then the result gives read-only
     * access to it.
     */
    static public ReadOnlyFile shallowReadOnly(File self) {
        return new ReadOnlyFile(self, false);
    }

    /**
     * Returns a transitively read-only (or "Sensory") view of this file or
     * directory.
     * <p/>
     * If this is a directory, then all contents fetched through this view will
     * also result in a transitively read-only view of those contents.
     */
    static public ReadOnlyFile deepReadOnly(File self) {
        return new ReadOnlyFile(self, true);
    }

    /**
     * Append to the file so the additional contents represents the string
     * 'text', turning '\n's into platform newlines, and converting to UTF-8
     */
    static public void appendText(File self, String text) throws IOException {
        writeText(self, text, true);
    }

    /**
     *
     */
    static public String getPath(File self) {
        return FileGetter.normalize(self.getPath());
    }

    /**
     *
     */
    static public String getAbsolutePath(File self) {
        return FileGetter.normalize(self.getAbsolutePath());
    }

    /**
     *
     */
    static public String getCanonicalPath(File self) throws IOException {
        return FileGetter.normalize(self.getCanonicalPath());
    }

    /**
     *
     */
    static public String getPlatformPath(File self) {
        return self.getPath();
    }

    /**
     *
     */
    static public String getParent(File self) {
        return FileGetter.normalize(self.getParent());
    }

    /**
     * Is this a "normal" file?  (ie, a non-directory)  This is the same test
     * as self.isFile(), but less confusingly named.
     */
    static public boolean isNormal(File self) {
        return self.isFile();
    }

    /**
     * XXX should this be public?  Should we use Java's File.toUrl() instead?
     */
    static private String asUrl(File self) {
        return "file:" + getPath(self);
    }

    /**
     * Open 'self' for reading text, decoding UTF-8 and turning platform
     * newlines into '\n's
     */
    static public BufferedReader textReader(File self)
      throws FileNotFoundException {
        return new BufferedReader(new FileReader(self));
    }

    /**
     * Enumerates lineNumber =&gt; String (text line) associations.
     * <p/>
     * Each text line ends with a "\n". <tt>isLocated</tt> defaults to false.
     */
    static public void iterate(File self, AssocFunc func) throws IOException {
        iterate(self, func, false);
    }

    /**
     * If the file is a directory, enumerate filename =&gt; File associations
     * for each child of the directory.
     * <p/>
     * Otherwise assume it's a text file and enumerates lineNumber =&gt;
     * String/Twine (text line) associations. Like Perl, each text line ends
     * with a "\n".
     *
     * @param isLocated If true, and if self is a non-directory, then enumerate
     *                  text lines as Twine with location info rather than bare
     *                  Strings.
     */
    static public void iterate(File self, AssocFunc func, boolean isLocated)
      throws IOException {
        if (self.isDirectory()) {
            String[] names = self.list();
            for (int i = 0; i < names.length; i++) {
                func.run(names[i], new File(self, names[i]));
            }
        } else {
            BufferedReader reader = textReader(self);
            try {
                String optURL = null;
                if (isLocated) {
                    optURL = asUrl(self);
                }
                BufferedReaderSugar.iterate(reader, func, optURL);
            } finally {
                reader.close();
            }
        }
    }

    /**
     * Gets the contents of the file as a String, normalizing newlines into
     * '\n's.
     * <p/>
     * XXX should be made atomic.
     */
    static public String getText(File self) throws IOException {
        return BufferedReaderSugar.getText(textReader(self));
    }

    /**
     * Gets the contents of the file as Twine (a text string that remembers
     * where it came from), normalizing newlines into '\n's.
     * <p/>
     * XXX should be made atomic.
     */
    static public Twine getTwine(File self) throws IOException {
        return BufferedReaderSugar.getTwine(textReader(self), asUrl(self));
    }

    /**
     * A SHA hash of the binary content of the file.
     * <p/>
     * XXX should be made atomic.
     */
    static public BigInteger getCryptoHash(File self)
      throws NoSuchAlgorithmException, FileNotFoundException, IOException {
        return InputStreamSugar.getCryptoHash(new FileInputStream(self));
    }

    /**
     * Returns the binary contents of the file as an array of bytes.
     * <p/>
     * XXX should be made atomic.
     */
    static public byte[] getBytes(File self) throws IOException {
        InputStream inp = new FileInputStream(self);
        byte[] result;
        try {
            result = InputStreamSugar.readAvailable(inp);
        } finally {
            inp.close();
        }
        if (result == null || result.length != self.length()) {
            throw new IOException("reading " + self.getPath());
        }
        return result;
    }

    /**
     * Sets the binary contents of the file to 'contents'.
     * <p/>
     * Not atomic or crash safe. If you want atomicity, consider using
     * makeAtomicFile.
     */
    static public void setBytes(File self, byte[] contents)
      throws IOException {
        OutputStream out = new FileOutputStream(self);
        try {
            out.write(contents);
        } finally {
            out.close();
        }
    }

    /**
     * Like {@link #renameTo}, but copies rather than renames.
     * <p/>
     * Not atomic or crash safe. If you want atomicity, consider using
     * makeAtomicFile.
     */
    static public void copyTo(File self,
                              final File dest,
                              final OneArgFunc optEjector) {
        try {
            if (self.isDirectory()) {
                if (dest.exists()) {
                    if (dest.isDirectory()) {
                        // we're cool
                    } else {
                        throw Thrower.toEject(optEjector,
                                              "Can't copy directory " +
                                                E.toQuote(self) +
                                                " to non-directory " +
                                                E.toQuote(dest));
                    }
                } else {
                    mkdirs(dest, optEjector);
                }
                iterate(self, new AssocFunc() {
                    public void run(Object key, Object value) {
                        String name = (String)key;
                        File sub = (File)value;
                        copyTo(sub, get(dest, name), optEjector);
                    }
                });
            } else {
                // base case
                // XXX should copy some max blocksize at a time
                setBytes(dest, getBytes(self));
            }
        } catch (IOException e) {
            throw ExceptionMgr.asSafe(e);
        }
    }

    /**
     * Write the file so that its contents represents the string 'text',
     * turning '\n's into platform newlines, and converting to UTF-8.
     * <p/>
     * Not atomic or crash safe. If you want atomicity, consider using
     * makeAtomicFile.
     */
    static public void setText(File self, String text) throws IOException {
        writeText(self, text, false);
    }

    /**
     * 'append' argument defaults to false
     */
    static public TextWriter textWriter(File self) throws IOException {
        return textWriter(self, false);
    }

    /**
     * Open 'self' for writing of appending text, encoded as UTF-8, with '\n's
     * written as platform newlines.
     */
    static public TextWriter textWriter(File self, boolean append)
      throws IOException {
        return new TextWriter(new FileWriter(self.getPath(), append),
                              NEWLINE,
                              false,
                              true,
                              null);
    }

    /**
     * Write the file (or append to the file) so that its contents represents
     * the string 'text', turning '\n's into platform newlines, and converting
     * to UTF-8
     * <p/>
     * Not atomic or crash safe. If you want atomicity, consider using
     * makeAtomicFile.
     */
    static private void writeText(File self, String text, boolean append)
      throws IOException {
        TextWriter writer = textWriter(self, append);
        try {
            writer.write(text);
        } finally {
            writer.close();
        }
    }

    /**
     * "Implements" {@link org.erights.e.elib.serial.Loader#get}
     */
    static public File get(File self, String name) {
        if (self.exists() && !self.isDirectory()) {
            T.fail(E.toString(self) + " is a non-directory");
        }
        name = name.replace(File.separatorChar, '/');
        name = name.replace('\\', '/');

        if (("/" + name + "/").indexOf("/../") != -1) {
            throw new SecurityException("\"..\" not allowed: " + name);
        }

        String path = getPath(self);
        if (!path.endsWith("/")) {
            path += '/';
        }
        path += name;
        return (File)FileGetter.THE_ONE.get(path);
    }

    /**
     * "implements" {@link org.erights.e.elib.serial.Loader#optUncall}.
     *
     * @return
     */
    static public Object[] optUncall(File self, Object obj) {
        if (obj instanceof File) {
            String path = getAbsolutePath(self);
            if (!path.endsWith("/")) {
                path += "/";
            }
            return BaseLoader.getOptWrappingUncall(self,
                                                   FileGetter.THE_ONE,
                                                   path,
                                                   obj);
        } else if (obj instanceof ReadOnlyFile) {
            T.fail("XXX not yet implemented");
            return null; // make compiler happy
        } else {
            return null;
        }
    }

    /**
     * Normalize the E-printed form to use forward slashes as separators.
     * <p/>
     * Also, E URI expressions have angle brackets around them, so file objects
     * print that way too. E'ers can still use getPath() to get the path as the
     * File object sees it.
     */
    static public void __printOn(File self, TextWriter out)
      throws IOException {
        out.print("<file:", getPath(self), ">");
    }

    /**
     * Like {@link File#createNewFile()}, but under conditions where that
     * method would return false, this one instead does a {@link Thrower#eject
     * non-local exit} according to optEjector.
     */
    static public void createNewFile(File self, OneArgFunc optEjector) {
        try {
            if (!self.createNewFile()) {
                throw Thrower.toEject(optEjector,
                                      "Can't createNewFile " +
                                        E.toQuote(getPath(self)));
            }
        } catch (IOException e) {
            throw ExceptionMgr.asSafe(e);
        }
    }

    /**
     * Like {@link File#delete()}, but under conditions where that method would
     * return false, this one instead does a {@link Thrower#eject non-local
     * exit} according to optEjector.
     */
    static public void delete(File self, OneArgFunc optEjector) {
        if (!self.delete()) {
            throw Thrower.toEject(optEjector,
                                  "Can't delete " + E.toQuote(getPath(self)));
        }
    }

    /**
     * Like {@link File#mkdir()}, but, unless self is already a directory,
     * under conditions where that method would return false, this one instead
     * does a {@link Thrower#eject non-local exit} according to optEjector.
     * <p/>
     * If self is already a directory, then this just returns false. If self is
     * not already a directory, and if this succeeds, then it returns true.
     */
    static public boolean mkdir(File self, OneArgFunc optEjector) {
        if (self.isDirectory()) {
            return false;
        } else if (self.mkdir()) {
            return true;
        } else {
            throw Thrower.toEject(optEjector,
                                  "Can't mkdir " + E.toQuote(getPath(self)));
        }
    }

    /**
     * Like {@link File#mkdirs()}, but, unless self is already a directory,
     * under conditions where that method would return false, this one instead
     * does a {@link Thrower#eject non-local exit} according to optEjector.
     * <p/>
     * If self is already a directory, then this just returns false. If self is
     * not already a directory, and if this succeeds, then it returns true.
     */
    static public boolean mkdirs(File self, OneArgFunc optEjector) {
        if (self.isDirectory()) {
            return false;
        } else if (self.mkdirs()) {
            return true;
        } else {
            throw Thrower.toEject(optEjector,
                                  "Can't mkdirs " + E.toQuote(getPath(self)));
        }
    }

    /**
     * Like {@link File#renameTo}, but under conditions where that method would
     * return false, this one instead does a {@link Thrower#eject non-local
     * exit} according to optEjector.
     */
    static public void renameTo(File self, File dest, OneArgFunc optEjector) {
        if (!self.renameTo(dest)) {
            throw Thrower.toEject(optEjector,
                                  "Can't renameTo " +
                                    E.toQuote(getPath(self)));
        }
    }

    /**
     * Like {@link File#setLastModified}, but under conditions where that
     * method would return false, this one instead does a {@link Thrower#eject
     * non-local exit} according to optEjector.
     */
    static public void setLastModified(File self,
                                       long absMillis,
                                       OneArgFunc optEjector) {
        if (!self.setLastModified(absMillis)) {
            throw Thrower.toEject(optEjector,
                                  "Can't setLastModified " +
                                    E.toQuote(getPath(self)));
        }
    }

    /**
     * Like {@link File#setReadOnly()}, but under conditions where that method
     * would return false, this one instead does a {@link Thrower#eject
     * non-local exit} according to optEjector.
     */
    static public void setReadOnly(File self, OneArgFunc optEjector) {
        if (!self.setReadOnly()) {
            throw Thrower.toEject(optEjector,
                                  "Can't setReadOnly " +
                                    E.toQuote(getPath(self)));
        }
    }
}
