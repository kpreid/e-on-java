package org.erights.e.meta.java.io;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.develop.assertion.T;
import org.erights.e.develop.exception.ExceptionMgr;
import org.erights.e.elib.oldeio.EPrintable;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.serial.BaseLoader;
import org.erights.e.elib.serial.PassByProxy;
import org.erights.e.elib.serial.Persistent;
import org.erights.e.elib.tables.AssocFunc;
import org.erights.e.elib.tables.EIteratable;
import org.erights.e.elib.tables.Twine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;


/**
 * @author Mark S. Miller
 */
public class ReadOnlyFile extends BaseLoader
  implements PassByProxy, Persistent, EIteratable, EPrintable {

    static private final long serialVersionUID = 7927004638816860460L;

    private final File myPrecious;

    private final boolean myIsTransitive;

    /**
     *
     */
    public ReadOnlyFile(File file, boolean isTransitive) {
        myPrecious = file;
        myIsTransitive = isTransitive;
    }

    /**
     *
     */
    public ReadOnlyFile shallowReadOnly() {
        return this;
    }

    /**
     *
     */
    public ReadOnlyFile deepReadOnly() {
        if (myIsTransitive) {
            return this;
        } else {
            return new ReadOnlyFile(myPrecious, true);
        }
    }

    /**
     */
    public String getName() {
        return myPrecious.getName();
    }

    /**
     */
    public String getParent() {
        return FileSugar.getParent(myPrecious);
    }

    /**
     */
    public String getPath() {
        return FileSugar.getPath(myPrecious);
    }

    /**
     */
    public boolean isAbsolute() {
        return myPrecious.isAbsolute();
    }

    /**
     */
    public String getAbsolutePath() {
        return FileSugar.getAbsolutePath(myPrecious);
    }

    /**
     */
    public ReadOnlyFile getAbsoluteFile() {
        return new ReadOnlyFile(myPrecious.getAbsoluteFile(), myIsTransitive);
    }

    /**
     */
    public String getCanonicalPath() throws IOException {
        return FileSugar.getCanonicalPath(myPrecious);
    }

    /**
     */
    public ReadOnlyFile getCanonicalFile() throws IOException {
        return new ReadOnlyFile(myPrecious.getCanonicalFile(), myIsTransitive);
    }

    /**
     */
    public String getPlatformPath() {
        return FileSugar.getPlatformPath(myPrecious);
    }

    /**
     */
    public boolean canRead() {
        return myPrecious.canRead();
    }

    /**
     */
    public boolean canWrite() {
        return false;
    }

    /**
     */
    public boolean exists() {
        return myPrecious.exists();
    }

    /**
     */
    public boolean isDirectory() {
        return myPrecious.isDirectory();
    }

    /**
     */
    public boolean isNormal() {
        return myPrecious.isFile();
    }

    /**
     */
    public boolean isHidden() {
        return myPrecious.isHidden();
    }

    /**
     */
    public long lastModified() {
        return myPrecious.lastModified();
    }

    /**
     */
    public long length() {
        return myPrecious.length();
    }

    /**
     */
    public String[] list() {
        return myPrecious.list();
    }

    /**
     */
    public String[] list(FilenameFilter filter) {
        return myPrecious.list(filter);
    }

    /**
     *
     */
    private Object[] protectFiles(File[] files) {
        if (myIsTransitive) {
            ReadOnlyFile[] result = new ReadOnlyFile[files.length];
            for (int i = 0; i < files.length; i++) {
                result[i] = new ReadOnlyFile(files[i], true);
            }
            return result;
        } else {
            return files;
        }
    }

    /**
     */
    public Object[] listFiles() {
        return protectFiles(myPrecious.listFiles());
    }

    /**
     */
    public Object[] listFiles(FilenameFilter filter) {
        return protectFiles(myPrecious.listFiles(filter));
    }

    /* ---- from FileSugar */

    /**
     * Gets the contents of the file as a String, normalizing newlines into
     * '\n's.
     */
    public String getText() throws IOException {
        return FileSugar.getText(myPrecious);
    }

    /**
     * Gets the contents of the file as Twine (a text string that remembers
     * where it came from), normalizing newlines into '\n's.
     */
    public Twine getTwine() throws IOException {
        return FileSugar.getTwine(myPrecious);
    }

    public byte[] getBytes() throws IOException {
        return FileSugar.getBytes(myPrecious);
    }

    /**
     * If the file is a directory, enumerate filename =&gt; File associations
     * for each child of the directory.
     * <p/>
     * Otherwise assume it's a text file and enumerates lineNumber =&gt; String
     * (text line) associations. Like Perl, each text line ends with a "\n".
     */
    public void iterate(AssocFunc func) {
        try {
            iterate(func, false);
        } catch (IOException ioe) {
            throw ExceptionMgr.asSafe(ioe);
        }
    }

    /**
     * If the file is a directory, enumerate filename =&gt; File associations
     * for each child of the directory.
     * <p/>
     * Otherwise assume it's a text file and enumerates lineNumber =&gt;
     * String/Twine (text line) associations. Like Perl, each text line ends
     * with a "\n".
     */
    public void iterate(AssocFunc func, boolean isLocated) throws IOException {
        if (myPrecious.isDirectory()) {
            String[] names = myPrecious.list();
            for (int i = 0; i < names.length; i++) {
                File file = new File(myPrecious, names[i]);
                Object value;
                if (myIsTransitive) {
                    value = new ReadOnlyFile(file, true);
                } else {
                    value = file;
                }
                func.run(names[i], value);
            }
        } else {
            FileSugar.iterate(myPrecious, func, isLocated);
        }
    }

    /**
     * Open 'self' for reading text, decoding UTF-8 and turning platform
     * newlines into '\n's
     */
    public BufferedReader textReader() throws FileNotFoundException {
        return FileSugar.textReader(myPrecious);
    }

    /**
     * @param uriBody
     */
    public Object get(String uriBody) {
        File result = FileSugar.get(myPrecious, uriBody);
        if (myIsTransitive) {
            return new ReadOnlyFile(result, true);
        } else {
            return result;
        }
    }

    /**
     * @return
     */
    public Object[] optUncall(Object obj) {
        T.fail("XXX not yet implemented");
        return null; // make compiler happy
    }

    /**
     * Normalize the E-printed form to use forward slashes as separators. E'ers
     * can still use getPath() to get the path as the File object sees it.
     */
    public void __printOn(TextWriter out) throws IOException {
        out.print(myPrecious);
        if (myIsTransitive) {
            out.print(".deepReadOnly()");
        } else {
            out.print(".shallowReadOnly()");
        }
    }
}
