package antlr;

/* ANTLR Translator Generator
 * Project led by Terence Parr at http://www.jGuru.com
 * Software rights: http://www.antlr.org/license.html
 *
 * $Id: //depot/code/org.antlr/release/antlr-2.7.5/antlr/FileLineFormatter.java#1 $
 */

public abstract class FileLineFormatter {

    static private FileLineFormatter formatter =
      new DefaultFileLineFormatter();

    static public FileLineFormatter getFormatter() {
        return formatter;
    }

    static public void setFormatter(FileLineFormatter f) {
        formatter = f;
    }

    /**
     * @param fileName the file that should appear in the prefix. (or null)
     * @param line     the line (or -1)
     * @param column   the column (or -1)
     */
    public abstract String getFormatString(String fileName,
                                           int line,
                                           int column);
}
